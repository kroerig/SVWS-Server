package de.svws_nrw.module.reporting.validierung;

import de.svws_nrw.core.data.gost.Abiturdaten;
import de.svws_nrw.core.data.gost.GostLaufbahnplanungBeratungsdaten;
import de.svws_nrw.core.data.schueler.SchuelerStammdaten;
import de.svws_nrw.core.logger.LogLevel;
import de.svws_nrw.data.gost.DBUtilsGost;
import de.svws_nrw.data.gost.DBUtilsGostAbitur;
import de.svws_nrw.data.gost.DataGostBlockungsdaten;
import de.svws_nrw.data.gost.DataGostBlockungsergebnisse;
import de.svws_nrw.data.gost.DataGostSchuelerLaufbahnplanungBeratungsdaten;
import de.svws_nrw.data.schueler.DataSchuelerLernabschnittsdaten;
import de.svws_nrw.data.schueler.DataSchuelerStammdaten;
import de.svws_nrw.db.DBEntityManager;
import de.svws_nrw.db.utils.ApiOperationException;
import de.svws_nrw.module.reporting.repositories.ReportingRepository;
import jakarta.ws.rs.core.Response.Status;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Statische Klasse mit Hilfsmethoden zur Validierung von Daten für das Reporting.
 */
public final class ReportingValidierung {

	private ReportingValidierung() {
		throw new IllegalStateException("Statische Klasse mit Hilfsmethoden zur Validierung von Daten für das Reporting. Initialisierung nicht möglich.");
	}

	/**
	 * Validiert von der API übergebene Daten für Schüler. Bei fehlenden oder unstimmigen Daten wird eine ApiOperationException geworfen.
	 * Über den Parameter cacheDaten kann gesteuert werden, ob bereits abgerufene Daten aus der DB im Repository zwischengespeichert werden soll.
	 *
	 * @param reportingRepository	Das Repository mit Daten zum Reporting.
	 * @param idsSchueler   		Liste der IDs der Schüler, die berücksichtigt werden sollen.
	 * @param mitGostDaten 			Legt fest, ob der Daten zur gymnasialen Oberstufe mit in den Kontext geladen werden sollen.
	 * @param mitAbiturDaten 		Legt fest, ob die Daten zum Abitur in der gymnasialen Oberstufe mit in den Kontext geladen werden sollen.
	 * @param cacheDaten 			Legt fest, ob die zur Validierung geladenen Daten im Repository gespeichert werden sollen.
	 *
	 * @throws ApiOperationException  im Fehlerfall
	 */
	public static void validiereDatenFuerSchueler(final ReportingRepository reportingRepository, final List<Long> idsSchueler, final boolean mitGostDaten,
			final boolean mitAbiturDaten, final boolean cacheDaten) throws ApiOperationException {

		// Grunddaten prüfen.
		final DBEntityManager conn = reportingRepository.conn();

		reportingRepository.logger().logLn(LogLevel.DEBUG, 4, "Beginn der Validierung der Schülerdaten.");

		if ((idsSchueler == null) || idsSchueler.isEmpty()) {
			reportingRepository.logger().logLn(LogLevel.ERROR, 4, "FEHLER: Es wurden keine Schüler-IDs übergeben.");
			throw new ApiOperationException(Status.NOT_FOUND, "FEHLER: Es wurden keine Schüler-IDs übergeben.");
		}

		// Prüfe die Schüler-IDs. Erzeuge Maps, damit auch später leicht auf die Schülerdaten zugegriffen werden kann.
		final Map<Long, SchuelerStammdaten> mapSchueler =
				DataSchuelerStammdaten.getListStammdaten(conn, idsSchueler).stream().collect(Collectors.toMap(s -> s.id, s -> s));
		for (final Long sID : idsSchueler)
			if (mapSchueler.get(sID) == null) {
				reportingRepository.logger().logLn(LogLevel.ERROR, 4, "FEHLER: Es wurden ungültige Schüler-IDs übergeben.");
				throw new ApiOperationException(Status.NOT_FOUND, "FEHLER: Es wurden ungültige Schüler-IDs übergeben.");
			}

		// Nur, wenn Daten zur gymnasialen Oberstufe mit angefordert werden.
		if (mitGostDaten) {
			// Schule hat eine gym. Oberstufe? pruefeSchuleMitGOSt wirft eine NOT_FOUND-Exception, wenn die Schule keine GOSt hat.
			try {
				DBUtilsGost.pruefeSchuleMitGOSt(conn);
			} catch (final ApiOperationException aoe) {
				reportingRepository.logger().logLn(LogLevel.ERROR, 4, "FEHLER: Keine Schule oder Schule ohne GOSt gefunden.");
				throw new ApiOperationException(Status.NOT_FOUND, aoe, "FEHLER: Keine Schule oder Schule ohne GOSt gefunden.");
			}

			final Map<Long, GostLaufbahnplanungBeratungsdaten> mapGostBeratungsdaten =
					new HashMap<>(new DataGostSchuelerLaufbahnplanungBeratungsdaten(conn).getMapFromIDs(idsSchueler));

			for (final Long sID : idsSchueler)
				if (mapGostBeratungsdaten.get(sID) == null) {
					reportingRepository.logger().logLn(LogLevel.ERROR, 4, "FEHLER: Es wurden Schüler-IDs übergeben, die nicht zur GOSt gehören.");
					throw new ApiOperationException(Status.NOT_FOUND, "FEHLER: Es wurden Schüler-IDs übergeben, die nicht zur GOSt gehören.");
				}
			reportingRepository.mapGostBeratungsdaten().putAll(mapGostBeratungsdaten);
		}

		// Nur, wenn Daten zum Abitur in der gymnasialen Oberstufe mit angefordert werden.
		if (mitAbiturDaten) {
			// Schule hat eine gym. Oberstufe? pruefeSchuleMitGOSt wirft eine NOT_FOUND-Exception, wenn die Schule keine GOSt hat.
			try {
				DBUtilsGost.pruefeSchuleMitGOSt(conn);
			} catch (final ApiOperationException aoe) {
				reportingRepository.logger().logLn(LogLevel.ERROR, 4, "FEHLER: Keine Schule oder Schule ohne GOSt gefunden.");
				throw new ApiOperationException(Status.NOT_FOUND, aoe, "FEHLER: Keine Schule oder Schule ohne GOSt gefunden.");
			}

			final Map<Long, Abiturdaten> mapGostSchuelerAbiturdaten = new HashMap<>();

			for (final Long sID : idsSchueler) {
				try {
					mapGostSchuelerAbiturdaten.put(sID, DBUtilsGostAbitur.getAbiturdaten(conn, sID));
				} catch (final ApiOperationException aoe) {
					reportingRepository.logger().logLn(LogLevel.ERROR, 4,
							"FEHLER: Es wurden Schüler-IDs übergeben, für die keine Abiturdaten in der GOSt existieren.");
					throw new ApiOperationException(Status.NOT_FOUND, aoe,
							"FEHLER: Es wurden Schüler-IDs übergeben, für die keine Abiturdaten in der GOSt existieren.");
				}
			}

			reportingRepository.mapGostSchuelerAbiturdaten().putAll(mapGostSchuelerAbiturdaten);
		}

		reportingRepository.logger().logLn(LogLevel.DEBUG, 4, "Ende der Validierung der Schülerdaten.");

		// Daten sind valide, speichere diese nun gemäß Parameter im Repository.
		if (cacheDaten) {
			reportingRepository.logger().logLn(LogLevel.DEBUG, 4, "Beginn der Speicherung der Daten aus der Validierung der Schülerdaten im Repository.");
			reportingRepository.mapSchuelerStammdaten().putAll(mapSchueler);
			reportingRepository.mapAktuelleLernabschnittsdaten()
					.putAll(new DataSchuelerLernabschnittsdaten(conn)
							.getListFromSchuelerIDsUndSchuljahresabschnittID(idsSchueler, reportingRepository.aktuellerSchuljahresabschnitt().id, false)
							.stream().collect(Collectors.toMap(l -> l.schuelerID, l -> l)));
			reportingRepository.logger().logLn(LogLevel.DEBUG, 4, "Ende der Speicherung der Daten aus der Validierung der Schülerdaten im Repository.");
		}
	}


	/**
	 * Validiert von der API übergebene Daten für GOSt-Blockungsergebnis. Bei fehlenden oder unstimmigen Daten wird eine ApiOperationException geworfen.
	 *
	 * @param reportingRepository	Das Repository mit Daten zum Reporting.
	 *
	 * @throws ApiOperationException  im Fehlerfall
	 */
	public static void validiereDatenFuerGostKursplanungBlockungsergebnis(final ReportingRepository reportingRepository)
			throws ApiOperationException {

		// Für die GOSt-Kursplanung muss die Schule eine Schule mit GOSt sein.
		try {
			DBUtilsGost.pruefeSchuleMitGOSt(reportingRepository.conn());
		} catch (final ApiOperationException e) {
			throw new ApiOperationException(Status.NOT_FOUND, e, "FEHLER: Keine Schule oder Schule ohne GOSt gefunden.");
		}

		final Long idBlockungsergebnis = reportingRepository.reportingParameter().idsHauptdaten.getFirst();

		// Prüfe nun, ob es zur angegebenen Blockungsergebnis-ID ein Ergebnis gibt.
		try {
			DataGostBlockungsdaten.getBlockungsdatenManagerFromDB(reportingRepository.conn(),
					DataGostBlockungsergebnisse.getErgebnisFromID(reportingRepository.conn(), idBlockungsergebnis).blockungID);
		} catch (final ApiOperationException e) {
			throw new ApiOperationException(Status.NOT_FOUND, e, "FEHLER: Mit der angegebenen Blockungsergebnis-ID konnte keine Daten ermittelt werden..");
		}
	}
}
