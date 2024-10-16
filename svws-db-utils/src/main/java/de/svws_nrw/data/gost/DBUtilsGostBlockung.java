package de.svws_nrw.data.gost;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import de.svws_nrw.base.kurs42.Kurs42Import;
import de.svws_nrw.core.adt.Pair;
import de.svws_nrw.core.data.gost.GostBlockungKurs;
import de.svws_nrw.core.data.gost.GostBlockungKursLehrer;
import de.svws_nrw.core.data.gost.GostBlockungRegel;
import de.svws_nrw.core.data.gost.GostBlockungSchiene;
import de.svws_nrw.core.logger.Logger;
import de.svws_nrw.core.types.gost.GostKursart;
import de.svws_nrw.core.types.kursblockung.GostKursblockungRegelParameterTyp;
import de.svws_nrw.core.types.kursblockung.GostKursblockungRegelTyp;
import de.svws_nrw.core.types.schule.Schulform;
import de.svws_nrw.db.DBEntityManager;
import de.svws_nrw.db.dto.current.gost.DTOGostJahrgangsdaten;
import de.svws_nrw.db.dto.current.gost.kursblockung.DTOGostBlockung;
import de.svws_nrw.db.dto.current.gost.kursblockung.DTOGostBlockungKurs;
import de.svws_nrw.db.dto.current.gost.kursblockung.DTOGostBlockungKurslehrer;
import de.svws_nrw.db.dto.current.gost.kursblockung.DTOGostBlockungRegel;
import de.svws_nrw.db.dto.current.gost.kursblockung.DTOGostBlockungRegelParameter;
import de.svws_nrw.db.dto.current.gost.kursblockung.DTOGostBlockungSchiene;
import de.svws_nrw.db.dto.current.gost.kursblockung.DTOGostBlockungZwischenergebnis;
import de.svws_nrw.db.dto.current.gost.kursblockung.DTOGostBlockungZwischenergebnisKursSchiene;
import de.svws_nrw.db.dto.current.gost.kursblockung.DTOGostBlockungZwischenergebnisKursSchueler;
import de.svws_nrw.db.dto.current.schild.faecher.DTOFach;
import de.svws_nrw.db.dto.current.schild.lehrer.DTOLehrer;
import de.svws_nrw.db.dto.current.schild.schueler.DTOSchueler;
import de.svws_nrw.db.dto.current.schild.schule.DTOEigeneSchule;
import de.svws_nrw.db.dto.current.schema.DTOSchemaAutoInkremente;
import de.svws_nrw.db.schema.Schema;
import de.svws_nrw.db.schema.SchemaTabelle;
import de.svws_nrw.db.utils.ApiOperationException;
import jakarta.ws.rs.core.Response.Status;

/**
 * Diese Klasse stellt Hilfs-Methoden rund um Blockungen der gymnasialen Oberstufe zur Verfügung.
 */
public final class DBUtilsGostBlockung {

	private DBUtilsGostBlockung() {
		throw new IllegalStateException("Instantiation of " + DBUtilsGostBlockung.class.getName() + " not allowed");
	}

	/** Funktion zum Bestimmen der nächsten freien ID bei einer Tabelle mit Autoinkrement */
	private static BiFunction<DBEntityManager, SchemaTabelle, Long> getNextID = (final DBEntityManager conn, final SchemaTabelle tab) -> {
		final DTOSchemaAutoInkremente dbID = conn.queryByKey(DTOSchemaAutoInkremente.class, tab.name());
		return dbID == null ? 1 : dbID.MaxID + 1;
	};


	/**
	 * Importiert eine Kurs-42-Blockung in das Datenbank-Schema, welches durch die übergebene
	 * Verbindung festgelegt ist.
	 *
	 * @param conn     die Datenbank-Verbindung.
	 * @param logger   der Logger für Rückmeldungen zum Import-Prozess
	 * @param path     der Pfad, wo sich die Kurs-42-Import-Dateien befinden
	 *
	 * @return true im Erfolgsfall und false im Fehlerfall
	 *
	 * @throws IOException   Falls der Zugriff auf die Kurs42-Dateien fehlschlägt
	 *
	 * @throws ApiOperationException   im Fehlerfall
	 */
	public static boolean importKurs42(final DBEntityManager conn, final Logger logger, final Path path) throws IOException, ApiOperationException {
		// Lese zunächst die Informationen zur Schule aus der SVWS-Datenbank und überprüfe die Schulform
		logger.logLn("-> Lese Informationen zu der Schule ein...");
		logger.modifyIndent(2);
		final DTOEigeneSchule schule = conn.querySingle(DTOEigeneSchule.class);
		if (schule == null) {
			logger.logLn("[Fehler] - Konnte die Informationen zur Schule nicht aus der Datenbank lesen");
			throw new ApiOperationException(Status.NOT_FOUND, "Konnte die Informationen zur Schule nicht aus der Datenbank lesen.");
		}
		final Schulform schulform = schule.Schulform;
		if ((schulform == null) || (schulform.daten == null) || (!schulform.daten.hatGymOb)) {
			logger.logLn("[Fehler] - Die Schulform hat keine gymnasiale Oberstufe oder konnte nicht bestimmt werden");
			throw new ApiOperationException(Status.CONFLICT, "Die Schulform hat keine gymnasiale Oberstufe oder konnte nicht bestimmt werden.");
		}
		logger.logLn("[OK]");
		logger.modifyIndent(-2);
		// Bestimme nun die IDs der Schüler, welche in der Schule vorhanden sind
		logger.logLn("-> Bestimme die Schüler der Schule aus der Datenbank...");
		logger.modifyIndent(2);
		final List<DTOSchueler> listeSchueler = conn.queryAll(DTOSchueler.class);
		if ((listeSchueler == null) || (listeSchueler.isEmpty())) {
			logger.logLn("[Fehler] - Konnte die Liste der Schüler nicht einlesen");
			throw new ApiOperationException(Status.NOT_FOUND, "Konnte die Liste der Schüler nicht einlesen.");
		}
		final Set<Long> setSchueler = listeSchueler.stream().map(s -> s.ID).collect(Collectors.toSet());
		logger.logLn("[OK]");
		logger.modifyIndent(-2);
		// Bestimme nun die Lehrkräfte, die an der Schule tätig sind.
		logger.logLn("-> Bestimme die Lehrkräfte der Schule aus der Datenbank...");
		logger.modifyIndent(2);
		final List<DTOLehrer> listeLehrer = conn.queryAll(DTOLehrer.class);
		if ((listeLehrer == null) || (listeLehrer.isEmpty())) {
			logger.logLn("[Fehler] - Konnte die Liste der Lehrer nicht einlesen");
			throw new ApiOperationException(Status.NOT_FOUND, "Konnte die Liste der Lehrer nicht einlesen.");
		}
		final Map<String, Long> mapLehrer = listeLehrer.stream().collect(Collectors.toMap(l -> l.Kuerzel, l -> l.ID));
		logger.logLn("[OK]");
		logger.modifyIndent(-2);
		// Bestimme die Fächer, die an der Schule vorhanden sind.
		logger.logLn("-> Bestimme die Fächer der Schule aus der Datenbank...");
		logger.modifyIndent(2);
		final List<DTOFach> listeFaecher = conn.queryNamed("DTOFach.istoberstufenfach", true, DTOFach.class);
		if ((listeFaecher == null) || (listeFaecher.isEmpty())) {
			logger.logLn("[Fehler] - Konnte die Liste der Fächer der Oberstufe nicht einlesen");
			throw new ApiOperationException(Status.NOT_FOUND, "Konnte die Liste der Fächer der Oberstufe nicht einlesen.");
		}
		final Map<Long, String> mapFaecher = listeFaecher.stream().collect(Collectors.toMap(f -> f.ID, f -> f.Kuerzel));
		logger.logLn("[OK]");
		logger.modifyIndent(-2);
		// Lese nun die Daten von Kurs 42 aus dem angegeben Pfad ein
		logger.logLn("-> Lese die Kurs 42 - Export - Textdateien ein...");
		logger.modifyIndent(2);
		Kurs42Import k42 = null;
		try {
			k42 = new Kurs42Import(path, schule.SchulNr, mapLehrer, setSchueler, logger);
		} catch (final IOException e) {
			logger.logLn("[Fehler] - Fehler beim Einlesen des Kurs42-Text-Exports: ");
			final StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			logger.logLn(sw.toString());
			throw new ApiOperationException(Status.BAD_REQUEST, "Fehler beim Einlesen des Kurs42-Text-Exports.");
		}
		// Prüfe, ob der Abiturjahrgang zuvor angelegt wurde
		logger.logLn("-> Prüfe, ob der Abiturjahrgang %d angelegt ist...".formatted(k42.abiturjahrgang));
		logger.modifyIndent(2);
		final DTOGostJahrgangsdaten jahrgangsdaten = conn.queryByKey(DTOGostJahrgangsdaten.class, k42.abiturjahrgang);
		if (jahrgangsdaten == null) {
			logger.logLn("[Fehler] - Der Abiturjahrgang %d ist nicht angelegt.".formatted(k42.abiturjahrgang));
			throw new ApiOperationException(Status.NOT_FOUND, "Der Abiturjahrgang %d ist nicht angelegt.".formatted(k42.abiturjahrgang));
		}
		logger.logLn("[OK]");
		logger.modifyIndent(-2);
		try {
			logger.logLn("-> Schreibe die Blockungsdaten...");
			logger.modifyIndent(2);
			conn.transactionBegin();
			// Bestimme zunächst die IDs für die neue Blockung, die Schienen, die Kurse, die Regeln und das Ergebnis
			final Long blockungID = getNextID.apply(conn, Schema.tab_Gost_Blockung);
			final Long schienenID = getNextID.apply(conn, Schema.tab_Gost_Blockung_Schienen);
			final Long kursID = getNextID.apply(conn, Schema.tab_Gost_Blockung_Kurse);
			final Long regelID = getNextID.apply(conn, Schema.tab_Gost_Blockung_Regeln);
			final Long ergebnisID = getNextID.apply(conn, Schema.tab_Gost_Blockung_Zwischenergebnisse);
			// Lege die Blockung und ein zugehöriges Ergebnis an
			final DTOGostBlockung blockung = new DTOGostBlockung(blockungID, k42.name + " (import)", k42.abiturjahrgang, k42.halbjahr, false);
			final DTOGostBlockungZwischenergebnis erg = new DTOGostBlockungZwischenergebnis(ergebnisID, blockungID, false);
			conn.transactionPersist(blockung);
			conn.transactionPersist(erg);
			// Lege die Schienen an
			for (final GostBlockungSchiene schiene : k42.schienen) {
				conn.transactionPersist(new DTOGostBlockungSchiene(schienenID + schiene.id,
						blockungID, schiene.nummer, schiene.bezeichnung, schiene.wochenstunden));
			}
			// Lege die Kurse an
			for (final GostBlockungKurs kurs : k42.kurse) {
				// Prüfe, ob die Fach ID in Ordnung ist
				if (mapFaecher.get(kurs.fach_id) == null) {
					conn.transactionRollback();
					logger.logLn("[Fehler] - Fach-ID " + kurs.fach_id + " ist in der Datenbank nicht bekannt");
					throw new ApiOperationException(Status.NOT_FOUND, "Fach-ID " + kurs.fach_id + " ist in der Datenbank nicht bekannt");
				}
				conn.transactionPersist(new DTOGostBlockungKurs(kursID + kurs.id, blockungID, kurs.fach_id,
						GostKursart.fromID(kurs.kursart), kurs.nummer, kurs.istKoopKurs, kurs.anzahlSchienen, kurs.wochenstunden));
				for (final GostBlockungKursLehrer lehrer : kurs.lehrer)
					conn.transactionPersist(new DTOGostBlockungKurslehrer(kursID + kurs.id,
							lehrer.id, lehrer.reihenfolge, lehrer.wochenstunden));
			}
			// Lege die Regeln an
			for (final GostBlockungRegel regel : k42.regeln) {
				final GostKursblockungRegelTyp typ = GostKursblockungRegelTyp.fromTyp(regel.typ);
				conn.transactionPersist(new DTOGostBlockungRegel(regelID + regel.id, blockungID, typ));
				for (int i = 0; i < regel.parameter.size(); i++) {
					Long param = regel.parameter.get(i);
					final GostKursblockungRegelParameterTyp paramTyp = typ.getParamType(i);
					param = switch (paramTyp) {
						case KURSART -> param;
						case SCHIENEN_NR -> param;
						case KURS_ID -> kursID + param;
						case SCHUELER_ID -> param;
						default -> param;
					};
					conn.transactionPersist(new DTOGostBlockungRegelParameter(regelID + regel.id, i, param));
				}
			}
			// Schreibe die Kurs-Schienen-Zuordnungen
			for (final Pair<Long, Long> zuordnung : k42.zuordnung_kurs_schiene.getNonNullValuesAsList())
				conn.transactionPersist(new DTOGostBlockungZwischenergebnisKursSchiene(ergebnisID,
						kursID + zuordnung.a, schienenID + zuordnung.b));
			// Schreibe die Kurs-Schüler-Zuordnungen
			for (final Pair<Long, Long> zuordnung : k42.zuordnung_kurs_schueler.getNonNullValuesAsList())
				conn.transactionPersist(new DTOGostBlockungZwischenergebnisKursSchueler(ergebnisID,
						kursID + zuordnung.a, zuordnung.b));
			if (!conn.transactionCommit()) {
				logger.logLn("[Fehler] Unerwarteter Fehler beim Schreiben in die Datenbank.");
				logger.modifyIndent(-2);
				throw new ApiOperationException(Status.INTERNAL_SERVER_ERROR);
			}
			logger.logLn("[OK]");
			logger.modifyIndent(-2);
			return true;
		} catch (@SuppressWarnings("unused") final Exception exception) {
			return false;
		} finally {
			conn.transactionRollback();
		}
	}

}
