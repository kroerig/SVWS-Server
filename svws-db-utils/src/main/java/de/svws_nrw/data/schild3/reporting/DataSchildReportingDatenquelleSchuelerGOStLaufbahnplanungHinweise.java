package de.svws_nrw.data.schild3.reporting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.svws_nrw.core.abschluss.gost.AbiturdatenManager;
import de.svws_nrw.core.abschluss.gost.GostBelegpruefungErgebnis;
import de.svws_nrw.core.abschluss.gost.GostBelegpruefungErgebnisFehler;
import de.svws_nrw.core.abschluss.gost.GostBelegpruefungsArt;
import de.svws_nrw.core.abschluss.gost.GostBelegungsfehlerArt;
import de.svws_nrw.core.data.gost.Abiturdaten;
import de.svws_nrw.core.data.gost.GostJahrgangsdaten;
import de.svws_nrw.core.data.druck.DruckGostLaufbahnplanungSchuelerHinweise;
import de.svws_nrw.core.types.schild3.SchildReportingAttributTyp;
import de.svws_nrw.core.utils.gost.GostFaecherManager;
import de.svws_nrw.data.faecher.DBUtilsFaecherGost;
import de.svws_nrw.data.gost.DBUtilsGostLaufbahn;
import de.svws_nrw.data.gost.DataGostJahrgangFachkombinationen;
import de.svws_nrw.data.gost.DataGostJahrgangsdaten;
import de.svws_nrw.db.DBEntityManager;
import de.svws_nrw.db.dto.current.gost.DTOGostSchueler;
import de.svws_nrw.db.dto.current.schild.schueler.DTOSchueler;
import de.svws_nrw.db.utils.OperationError;
import jakarta.validation.constraints.NotNull;

/**
 * Die Definition einer Schild-Reporting-Datenquelle für die Hinweise in der Laufbahnplanung in der gymnasialen Oberstufe
 */
public final class DataSchildReportingDatenquelleSchuelerGOStLaufbahnplanungHinweise extends DataSchildReportingDatenquelle<DruckGostLaufbahnplanungSchuelerHinweise, Long> {

    /**
     * Erstelle die Datenquelle SchuelerGOStLaufbahnplanungHinweise
     */
	public DataSchildReportingDatenquelleSchuelerGOStLaufbahnplanungHinweise() {
        super(DruckGostLaufbahnplanungSchuelerHinweise.class);
        this.setMaster("schuelerID", "Schueler", "id", SchildReportingAttributTyp.INT, Long.class);
        // Beispiel für die Einschränkung auf Schulformen: this.restrictTo(Schulform.GY, Schulform.GE)
    }

	@Override
	public List<DruckGostLaufbahnplanungSchuelerHinweise> getDaten(final DBEntityManager conn, final List<Long> params) {

		// Prüfe, ob die Schüler in der DB vorhanden sind
        final Map<Long, DTOSchueler> schueler = conn
                .queryNamed("DTOSchueler.id.multiple", params, DTOSchueler.class)
                .stream().collect(Collectors.toMap(s -> s.ID, s -> s));
		for (final Long schuelerID : params) {
			if (schueler.get(schuelerID) == null)
				throw OperationError.NOT_FOUND.exception("Parameter der Abfrage ungültig: Ein Schüler mit der ID " + schuelerID.toString() + " existiert nicht.");
		}

		// Aggregiere die benötigten Daten aus der Datenbank, wenn alle Schüler-IDs existieren
		final ArrayList<DruckGostLaufbahnplanungSchuelerHinweise> result = new ArrayList<>();
		for (final Long schuelerID : params) {
			// GOSt-Daten des Schülers
			final DTOGostSchueler gostSchueler = conn.queryByKey(DTOGostSchueler.class, schuelerID);
			final Abiturdaten abidaten = DBUtilsGostLaufbahn.get(conn, schuelerID);

			if ((gostSchueler != null) && (abidaten.abiturjahr > 0)) {
				// Nur wenn zum Schüler GOSt-Daten und Abiturdaten gefunden werden, dann werden die gefundenen Fehler in die Ergebnisliste eingetragen. Andernfalls wird ein leerer Vektor zurückgegeben.
				// Alternativ wäre der vollständige Abbruch im Fehlerfall: throw OperationError.INTERNAL_SERVER_ERROR.exception("Parameter der Abfrage ungültig: Die GOSt-Daten oder Abiturdaten des Schülers mit der ID " + schuelerID.toString() + " konnten nicht ermittelt werden.")

				// Abiturdatenmanager für weitere Angaben erzeugen.
				// Da unter Umständen durch Migration und Importe alter Daten aus Schild und LuPO die GOSt-Fächer nicht mit den Fachwahlen übereinstimmen könnten,
				// kann beim Erzeugen der Manager ein Fehler auftreten. Dieser wird hier abgefangen, das Füllen der Datenquelle beendet und eine Exception geworfen.
				try {
			    	final @NotNull GostJahrgangsdaten jahrgangsdaten = DataGostJahrgangsdaten.getJahrgangsdaten(conn, abidaten.abiturjahr);
					final @NotNull GostFaecherManager faecherManager = DBUtilsFaecherGost.getFaecherListeGost(conn, abidaten.abiturjahr);
					faecherManager.addFachkombinationenAll(DataGostJahrgangFachkombinationen.getFachkombinationen(conn, abidaten.abiturjahr));
					final @NotNull AbiturdatenManager abiManager = new AbiturdatenManager(abidaten, jahrgangsdaten, faecherManager, GostBelegpruefungsArt.GESAMT);

					final GostBelegpruefungErgebnis ergebnis = abiManager.getBelegpruefungErgebnis();
					if (!ergebnis.fehlercodes.isEmpty()) {
						for (final GostBelegpruefungErgebnisFehler f : ergebnis.fehlercodes) {
							final GostBelegungsfehlerArt art = GostBelegungsfehlerArt.fromKuerzel(f.art);
							if (art == GostBelegungsfehlerArt.HINWEIS) {
								final DruckGostLaufbahnplanungSchuelerHinweise laufbahnplanungHinweis = new DruckGostLaufbahnplanungSchuelerHinweise();
								laufbahnplanungHinweis.schuelerID = schuelerID;
								laufbahnplanungHinweis.belegungshinweis = f.beschreibung;
								result.add(laufbahnplanungHinweis);
							}
						}
					}
				} catch (final Exception ex) {
					throw OperationError.INTERNAL_SERVER_ERROR.exception("Die Daten zur Laufbahn und zum Abitur des Schülers mit der ID " + schuelerID + " und die Einstellungen zu den Fächern der Oberstufe des Abiturjahrgangs " + abidaten.abiturjahr + " sind vermutlich inkonsistent. Folgender Fehler ist aufgetreten: " + ex.getMessage());
				}
			}
		}
		// Geben die Ergebnis-Liste mit den Core-DTOs zurück
        return result;
    }
}
