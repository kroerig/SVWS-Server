package de.nrw.schule.svws.core.kursblockung.test;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Vector;
import java.util.function.Consumer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import de.nrw.schule.svws.core.data.gost.GostBlockungKurs;
import de.nrw.schule.svws.core.data.gost.GostBlockungRegel;
import de.nrw.schule.svws.core.data.gost.GostBlockungSchiene;
import de.nrw.schule.svws.core.data.gost.GostBlockungsdaten;
import de.nrw.schule.svws.core.data.gost.GostFach;
import de.nrw.schule.svws.core.data.gost.GostFachwahl;
import de.nrw.schule.svws.core.data.schueler.Schueler;
import de.nrw.schule.svws.core.kursblockung.KursblockungAlgorithmus;
import de.nrw.schule.svws.core.kursblockung.KursblockungMatrix;
import de.nrw.schule.svws.core.kursblockung.KursblockungStatic;
import de.nrw.schule.svws.core.logger.LogData;
import de.nrw.schule.svws.core.logger.LogLevel;
import de.nrw.schule.svws.core.logger.Logger;
import de.nrw.schule.svws.core.types.gost.GostKursart;
import de.nrw.schule.svws.core.types.kursblockung.GostKursblockungRegelTyp;
import de.nrw.schule.svws.core.utils.gost.GostBlockungsdatenManager;
import de.nrw.schule.svws.core.utils.gost.GostBlockungsergebnisManager;
import de.nrw.schule.svws.core.utils.gost.GostFaecherManager;
import jakarta.validation.constraints.NotNull;

/** Diese Klasse testet <b>alle</b> relevanten Methoden, die für die Kursblockung benötigt werden. */
@DisplayName("Testet alle relevanten Kursblockungsalgorithmen.")
@TestMethodOrder(MethodOrderer.MethodName.class)
public class KursblockungTests {

	private static final String PFAD_DATEN_001 = "de/nrw/schule/svws/core/kursblockung/blockung001/";
	private static final String PFAD_DATEN_002 = "de/nrw/schule/svws/core/kursblockung/blockung002/";

	/**
	 * Testet den Algorithmus "Maximum Cardinality Bipartite Matching". Hierfür werden Matrizen unterschiedlicher
	 * Dimension erzeugt und mit 0en und 1en zufällig gefüllt. Anschließend wird ein maximales Matching berechnet.
	 * Gleichzeitig wird die negierte Matrix an einen "Minimum Weight Bipartite Matching" übergeben. Die Ergebnisse
	 * beider Algorithmen muss identisch sein (nicht die Zuordnung selbst, sondern die Größe des Matchings).
	 */
	@Test
	@DisplayName("Maximum Cardinality Bipartite Matching testen.")
	void test000_max_bipartite_matching() {
		Random rnd = new Random(1);
	
		// Führe viele zufällige Tests durch...
		for (int i = 0; i < 100000; i++) {
			int rows = rnd.nextInt(10) + 2;
			int cols = rnd.nextInt(10) + 2;
			double percent = rnd.nextDouble();
	
			// Erzeuge identische Matrizen mit +1 (m1) und -1 (m2) Werten
			KursblockungMatrix m1 = new KursblockungMatrix(rnd, rows, cols); // Maximales bipartites Matching
			KursblockungMatrix m2 = new KursblockungMatrix(rnd, rows, cols); // Minimales gewichtetes bipartites
																				// Matching
			for (int r = 0; r < rows; r++) {
				for (int c = 0; c < cols; c++) {
					if (rnd.nextDouble() < percent) {
						m1.getMatrix()[r][c] = 1;
						m2.getMatrix()[r][c] = -1;
					}
				}
			}
	
			// Berechne
			int[] r2c1 = m1.gibMaximalesBipartitesMatching(true);
			int[] r2c2 = m2.gibMinimalesBipartitesMatchingGewichtet(true);
	
			// Berechne die Summe der Zuordnung
			int summe1 = 0;
			int summe2 = 0;
			for (int r = 0; r < rows; r++) {
				summe1 += r2c1[r] < 0 ? 0 : m1.getMatrix()[r][r2c1[r]];
				summe2 += r2c2[r] < 0 ? 0 : -m2.getMatrix()[r][r2c2[r]];
			}
	
			// Die Summe beider Verfahren muss gleich sein
			if (summe1 != summe2) {
				fail(m1.convertToString("Summe1(" + summe1 + ") != Summe2(" + summe2 + ")", 5, false));
			}
		}
	
	}

	/**
	 * Testet den Algorithmus "Minimum Weight Bipartite Matching". Hierfür werden Matrizen unterschiedlicher Dimension
	 * erzeugt und mit Zahlen von -9 bis +9 zufällig gefüllt. Ebenso wird durch einen Brute-Force-Algorithmus auch ein
	 * "Minimum Weight Bipartite Matching" berechnet. Die Ergebnisse beider Algorithmen muss identisch sein (nicht die
	 * Zuordnung selbst, sondern die Größe des Matchings).
	 */
	@Test
	@DisplayName("Minimum Weight Bipartite Matching testen.")
	void test000_min_weight_bipartite_matching() {
		Random rnd = new Random(1);
	
		// Zufallsmatrizen testen
		// Vorsicht mit der Dimension! Zu groß --> exponentielle Laufzeit!
		for (int i = 0; i < 100000; i++) {
			// Füllen
			int rows = rnd.nextInt(5) + 2; // Dimension
			int cols = rnd.nextInt(5) + 2; // Dimension
			KursblockungMatrix m = new KursblockungMatrix(rnd, rows, cols);
			m.fuelleMitZufallszahlenVonBis(-9, 9);
	
			// Berechnen
			int[] r2c = m.gibMinimalesBipartitesMatchingGewichtet(true);
	
			// Summe berechnen (Minimum Matching Algorithmus)
			long wert1 = 0;
			for (int r = 0; r < r2c.length; r++) {
				int c = r2c[r];
				if (c >= 0)
					wert1 += m.getMatrix()[r][c];
			}
	
			// Summe berechnen (alle Kombinationen durchgehen)
			long wert2 = rows <= cols ? recursive_min_sum_r(m.getMatrix(), 0, new boolean[cols])
					: recursive_min_sum_c(m.getMatrix(), 0, new boolean[rows]);
	
			// Die Summen müssen gleich sein
			if (wert1 != wert2) {
				System.out.println(m.convertToString("Summe1(" + wert1 + ") != Summe2(" + wert2 + ")", 5, false));
				System.out.println(m.convertToString("Summe1(" + wert1 + ") != Summe2(" + wert2 + ")", 5, true));
				fail("Summe1(" + wert1 + ") != Summe2(" + wert2 + ")");
			}
	
		}
	
	}

	/** Testet das Einlesen und Konvertieren der Daten 001. Diese befinden sich hier {@link #PFAD_DATEN_001}. */
	@Test
	@DisplayName("Daten 001 einlesen.")
	void test001_data() {
		// Erzeugen eines Loggers mit Consumer.
		Logger log = new Logger();

		// Hinzufügen des Consumers, der im kritischen Fall 'fail' aufruft.
		log.addConsumer(new Consumer<LogData>() {
			@Override
			public void accept(LogData t) {
				if (t.getLevel().compareTo(LogLevel.APP) != 0)
					fail(t.getText());
			}
		});

		// Einlesen der Kurs42-Daten aus den Textdateien
		Kurs42Converter k42Converter = new Kurs42Converter(log, PFAD_DATEN_001, 999, false);

		GostBlockungsdatenManager manager = k42Converter.gibKursblockungInput();

		// Überprüfen der Ergebnisse
		if (manager.getSchienenAnzahl() != 14)
			fail("Blockung001 hat nicht 14 Schienen, sondern " + manager.getSchienenAnzahl() + ".");

		if (manager.getSchuelerAnzahlMitFachwahlen() != 137)
			fail("Blockung001 hat nicht 137 SuS mit Fachwahlen, sondern " + manager.getSchuelerAnzahlMitFachwahlen()
					+ ".");

		if (manager.getFaecherAnzahl() != 33)
			fail("Blockung001 hat nicht 33 Fächer, sondern " + manager.getFaecherAnzahl() + ".");

		if (manager.getKursartenAnzahl() != 3)
			fail("Blockung001 hat nicht 3 Kursarten, sondern " + manager.getKursartenAnzahl() + ".");

		if (manager.getKursAnzahl() != 69)
			fail("Blockung001 hat nicht 69 Kurse, sondern " + manager.getKursAnzahl() + ".");

		if (manager.getFachwahlAnzahl() != 1146)
			fail("Blockung001 hat nicht 1146 Fachwahlen, " + manager.getFachwahlAnzahl() + ".");
	}

	/**
	 * Liest diese {@link #PFAD_DATEN_001} Daten ein. Fixiert anschließend alle Kurse und lässt dann den
	 * Kursblockungsalgorithmus lediglich die SuS verteilen.
	 */
	@Test
	@DisplayName("Daten 001 blocken (nur SuS).")
	void test001_fixed_complete() {

		// Der Kursblockungsalgorithmus ist ein Service.
		KursblockungAlgorithmus kbAlgorithmus = new KursblockungAlgorithmus();

		// Logger vom Service übernehmen
		Logger log = kbAlgorithmus.getLogger();

		// Consumer triggert 'fail', wenn etwas kritisches geloggt wurde.
		log.addConsumer(new Consumer<LogData>() {
			@Override
			public void accept(LogData t) {
				if (t.getLevel().compareTo(LogLevel.APP) != 0)
					fail(t.getText());
			}
		});

		// Einlesen der Kurs42-Daten aus den Textdateien.
		long maxTimeMillis = 1000 * 1;
		Kurs42Converter k42Converter = new Kurs42Converter(log, PFAD_DATEN_001, maxTimeMillis, true);

		// Umwandlung von 'Kurs42Daten' zu 'KursblockungInput'.
		@NotNull GostBlockungsdatenManager kbInput = k42Converter.gibKursblockungInput();

		// Berechnung der Blockung und Rückgabe aller Blockungsergebnisse.
		Vector<GostBlockungsergebnisManager> kbOutputs = kbAlgorithmus.handle(kbInput);

		// Blockungsergebnisse vorhanden?
		assert kbOutputs != null : "kbOutputs == null";

		// Jedes einzelne Blockungsergebnis prüfen.
		for (GostBlockungsergebnisManager kbOutput : kbOutputs)
			check(kbInput, kbOutput);
	}

	/**
	 * Liest diese {@link #PFAD_DATEN_001} Daten ein. Definiert einige Regeln und lässt dann den
	 * Kursblockungsalgorithmus Kurse und SuS verteilen.
	 */
	@Test
	@DisplayName("Daten 001 blocken (Kurse und SuS).")
	void test001_fixed_some() {

		// Der Kursblockungsalgorithmus ist ein Service.
		KursblockungAlgorithmus kbAlgorithmus = new KursblockungAlgorithmus();

		// Logger vom Service übernehmen
		Logger log = kbAlgorithmus.getLogger();

		// Consumer triggert 'fail', wenn etwas kritisches geloggt wurde.
		log.addConsumer(new Consumer<LogData>() {
			@Override
			public void accept(LogData t) {
				if (t.getLevel().compareTo(LogLevel.APP) != 0)
					fail(t.getText());
			}
		});

		// Einlesen der Kurs42-Daten aus den Textdateien.
		long maxTimeMillis = 1000 * 1;
		Kurs42Converter k42Converter = new Kurs42Converter(log, PFAD_DATEN_001, maxTimeMillis, false);

		// Kurs42Daten --> GostBlockungsdatenManager
		@NotNull GostBlockungsdatenManager kbInput = k42Converter.gibKursblockungInput();

		// Fixierungen
		regelSperreSchieneFuerKursart(kbInput, "LK", 3, kbInput.getSchienenAnzahl());
		regelSperreSchieneFuerKursart(kbInput, "GK", 1, 2);
		regelSperreSchieneFuerKursart(kbInput, "PJK", 1, 2);
		regelFixiereKurseInSchieneSonstNichts(kbInput, new long[] { 35, 36, 37 }, 12);
		regelFixiereKurseInSchieneSonstNichts(kbInput, new long[] { 68 }, 13);
		regelFixiereKurseInSchieneSonstNichts(kbInput, new long[] { 18, 33 }, 14);

		// Berechnung der Blockung und Rückgabe aller Blockungsergebnisse.
		Vector<@NotNull GostBlockungsergebnisManager> kbOutputs = kbAlgorithmus.handle(kbInput);

		// Blockungsergebnisse vorhanden?
		assert kbOutputs != null : "kbOutputs == null";

		// Jedes einzelne Blockungsergebnis prüfen.
		for (GostBlockungsergebnisManager kbOutput : kbOutputs)
			check(kbInput, kbOutput);
	}

	/**
	 * Liest diese {@link #PFAD_DATEN_001} Daten ein. Definiert einige Regeln und explizit Regel 5, indem für Schüler
	 * Nr. 18 alle Kurse gesperrt werden. Lässt dann den Kursblockungsalgorithmus Kurse und SuS verteilen.
	 */
	@Test
	@DisplayName("Daten 001 blocken mit Regel 5 (Verbiete Schüler X in Kurs Y).")
	void test001_regel_5() {

		// Der Kursblockungsalgorithmus ist ein Service.
		KursblockungAlgorithmus kbAlgorithmus = new KursblockungAlgorithmus();

		// Logger vom Service übernehmen
		Logger log = kbAlgorithmus.getLogger();

		// Consumer triggert 'fail', wenn etwas kritisches geloggt wurde.
		log.addConsumer(new Consumer<LogData>() {
			@Override
			public void accept(LogData t) {
				if (t.getLevel().compareTo(LogLevel.APP) != 0)
					fail(t.getText());
			}
		});

		// Einlesen der Kurs42-Daten aus den Textdateien.
		long maxTimeMillis = 1000 * 1;
		Kurs42Converter k42Converter = new Kurs42Converter(log, PFAD_DATEN_001, maxTimeMillis, false);

		// Umwandlung von 'Kurs42Daten' zu 'KursblockungInput'.

		@NotNull GostBlockungsdatenManager kbInput = k42Converter.gibKursblockungInput();

		// Fixierungen
		regelSperreSchieneFuerKursart(kbInput, "LK", 3, kbInput.getSchienenAnzahl());
		regelSperreSchieneFuerKursart(kbInput, "GK", 1, 2);
		regelSperreSchieneFuerKursart(kbInput, "PJK", 1, 2);
		regelFixiereKurseInSchieneSonstNichts(kbInput, new long[] { 35, 36, 37 }, 12);
		regelFixiereKurseInSchieneSonstNichts(kbInput, new long[] { 68 }, 13);
		regelFixiereKurseInSchieneSonstNichts(kbInput, new long[] { 18, 33 }, 14);

		// Regel 5 --> Sperre einen Schüler in allen Kursen.
		for (@NotNull GostBlockungKurs gKurs : kbInput.daten().kurse)
			regelVerbieteSchuelerInKurs(kbInput, 18, gKurs.id);

		// Berechnung der Blockung und Rückgabe aller Blockungsergebnisse.
		Vector<@NotNull GostBlockungsergebnisManager> kbOutputs = kbAlgorithmus.handle(kbInput);

		// Blockungsergebnisse vorhanden?
		assert kbOutputs != null : "kbOutputs == null";

		// Jedes einzelne Blockungsergebnis prüfen.
		for (GostBlockungsergebnisManager kbOutput : kbOutputs) {
			check(kbInput, kbOutput);

			if (kbOutput.getOfKursOfSchieneIstZugeordnet(35, 12) == false)
				fail("kbOutput.getIstKursInSchiene(35, 12)");
			if (kbOutput.getOfKursOfSchieneIstZugeordnet(36, 12) == false)
				fail("kbOutput.getIstKursInSchiene(36, 12)");
			if (kbOutput.getOfKursOfSchieneIstZugeordnet(37, 12) == false)
				fail("kbOutput.getIstKursInSchiene(37, 12)");
			if (kbOutput.getOfKursOfSchieneIstZugeordnet(68, 13) == false)
				fail("kbOutput.getIstKursInSchiene(68, 13)");
			if (kbOutput.getOfKursOfSchieneIstZugeordnet(18, 14) == false)
				fail("kbOutput.getIstKursInSchiene(18, 14)");
			if (kbOutput.getOfKursOfSchieneIstZugeordnet(33, 14) == false)
				fail("kbOutput.getIstKursInSchiene(33, 14)");

			// TODO BAR Sperrungen überprüfen.
		}
	}

	/** Testet das Einlesen und Konvertieren der Daten 002. Diese befinden sich hier {@link #PFAD_DATEN_002}. */
	@Test
	@DisplayName("Daten 002 einlesen.")
	void test002_data() {

		// Erzeugen eines Loggers mit Consumer.
		Logger log = new Logger();

		// Hinzufügen des Consumers, der im kritischen Fall 'fail' aufruft.
		log.addConsumer(new Consumer<LogData>() {
			@Override
			public void accept(LogData t) {
				if (t.getLevel().compareTo(LogLevel.APP) != 0)
					fail(t.getText());
			}
		});

		// Einlesen der Kurs42-Daten aus den Textdateien
		long maxTimeMillis = 1000 * 1;
		Kurs42Converter k42Converter = new Kurs42Converter(log, PFAD_DATEN_002, maxTimeMillis, false);
		@NotNull GostBlockungsdatenManager manager = k42Converter.gibKursblockungInput();

		// Überprüfen der Ergebnisse
		if (manager.getSchienenAnzahl() != 12)
			fail("Blockung002 hat nicht 12 Schienen, sondern " + manager.getSchienenAnzahl() + ".");

		if (manager.getSchuelerAnzahlMitFachwahlen() != 150)
			fail("Blockung002 hat nicht 150 SuS mit Fachwahlen, sondern " + manager.getSchuelerAnzahlMitFachwahlen()
					+ ".");

		if (manager.getFaecherAnzahl() != 23)
			fail("Blockung002 hat nicht 23 Fächer, sondern " + manager.getFaecherAnzahl() + ".");

		if (manager.getKursartenAnzahl() != 2)
			fail("Blockung002 hat nicht 2 Kursarten, sondern " + manager.getKursartenAnzahl() + ".");

		if (manager.getKursAnzahl() != 86)
			fail("Blockung002 hat nicht 86 Kurse, sondern " + manager.getKursAnzahl());

		if (manager.getFachwahlAnzahl() != 1798)
			fail("Blockung002 hat nicht 1798 Fachwahlen, sondern " + manager.getFachwahlAnzahl() + ".");

	}

	/**
	 * Liest diese {@link #PFAD_DATEN_002} Daten ein. Fixiert anschließend alle Kurse und lässt dann den
	 * Kursblockungsalgorithmus lediglich die SuS verteilen.
	 */
	@Test
	@DisplayName("Daten 002 blocken (nur SuS).")
	void test002_fixed_complete() {

		// Der Kursblockungsalgorithmus ist ein Service.
		KursblockungAlgorithmus kbAlgorithmus = new KursblockungAlgorithmus();

		// Logger vom Service übernehmen
		Logger log = kbAlgorithmus.getLogger();

		// Consumer triggert 'fail', wenn etwas kritisches geloggt wurde.
		log.addConsumer(new Consumer<LogData>() {

			@Override
			public void accept(LogData t) {
				if (t.getLevel().compareTo(LogLevel.APP) != 0)
					fail(t.getText());
			}
		});

		// Einlesen der Kurs42-Daten aus den Textdateien.
		long maxTimeMillis = 1000 * 1;
		boolean kurseFixieren = true;
		Kurs42Converter k42Converter = new Kurs42Converter(log, PFAD_DATEN_002, maxTimeMillis, kurseFixieren);

		// Umwandlung von 'Kurs42Daten' zu 'KursblockungInput'.

		@NotNull GostBlockungsdatenManager kbInput = k42Converter.gibKursblockungInput();

		// Berechnung der Blockung und Rückgabe aller Blockungsergebnisse.
		Vector<@NotNull GostBlockungsergebnisManager> kbOutputs = kbAlgorithmus.handle(kbInput);

		// Blockungsergebnisse vorhanden?
		assert kbOutputs != null : "kbOutputs == null";

		// Jedes einzelne Blockungsergebnis prüfen.
		for (GostBlockungsergebnisManager kbOutput : kbOutputs)
			check(kbInput, kbOutput);
	}

	/**
	 * Liest diese {@link #PFAD_DATEN_002} Daten ein. Definiert einige Regeln und lässt dann den
	 * Kursblockungsalgorithmus Kurse und SuS verteilen.
	 */
	@Test
	@DisplayName("Daten 002 blocken (Kurse und SuS).")
	void test002_fixed_some() {

		// Der Kursblockungsalgorithmus ist ein Service.
		KursblockungAlgorithmus kbAlgorithmus = new KursblockungAlgorithmus();

		// Logger vom Service übernehmen
		Logger log = kbAlgorithmus.getLogger();

		// Consumer triggert 'fail', wenn etwas kritisches geloggt wurde.
		log.addConsumer(new Consumer<LogData>() {

			@Override
			public void accept(LogData t) {
				if (t.getLevel().compareTo(LogLevel.APP) != 0)
					fail(t.getText());
			}
		});

		// Einlesen der Kurs42-Daten aus den Textdateien.
		long maxTimeMillis = 1000 * 1;
		boolean kurseFixieren = false;
		Kurs42Converter k42Converter = new Kurs42Converter(log, PFAD_DATEN_002, maxTimeMillis, kurseFixieren);

		// Umwandlung von 'Kurs42Daten' zu 'KursblockungInput'.
		@NotNull GostBlockungsdatenManager kbInput = k42Converter.gibKursblockungInput();

		// Regeln
		regelFixiereKursInSchiene(kbInput, 52, 1);
		regelFixiereKursInSchiene(kbInput, 44, 1);
		regelFixiereKursInSchiene(kbInput, 17, 1);

		// Berechnung der Blockung und Rückgabe aller Blockungsergebnisse.
		Vector<@NotNull GostBlockungsergebnisManager> kbOutputs = kbAlgorithmus.handle(kbInput);

		// Blockungsergebnisse vorhanden?
		assert kbOutputs != null : "kbOutputs == null";

		// Jedes einzelne Blockungsergebnis prüfen.
		for (GostBlockungsergebnisManager kbOutput : kbOutputs)
			check(kbInput, kbOutput);
	}

	/**
	 * Liest diese {@link #PFAD_DATEN_002} Daten ein. Definiert einige Regeln und explizit Regel 4, die 19 SuS in Kurs 0
	 * fixiert. Lässt dann den Kursblockungsalgorithmus Kurse und SuS verteilen.
	 */
	@Test
	@DisplayName("Daten 002 blocken mit Regel 4 (Schüler-Kurs-Fixierung).")
	void test002_regel_4() {

		// Der Kursblockungsalgorithmus ist ein Service.
		KursblockungAlgorithmus kbAlgorithmus = new KursblockungAlgorithmus();

		// Logger vom Service übernehmen
		Logger log = kbAlgorithmus.getLogger();

		// Consumer triggert 'fail', wenn etwas kritisches geloggt wurde.
		log.addConsumer(new Consumer<LogData>() {

			@Override
			public void accept(LogData t) {
				if (t.getLevel().compareTo(LogLevel.APP) != 0)
					fail(t.getText());
			}
		});

		// Einlesen der Kurs42-Daten aus den Textdateien.
		long maxTimeMillis = 1000 * 1;
		boolean kurseFixieren = false;
		Kurs42Converter k42Converter = new Kurs42Converter(log, PFAD_DATEN_002, maxTimeMillis, kurseFixieren);

		// Umwandlung von 'Kurs42Daten' zu 'KursblockungInput'.

		@NotNull GostBlockungsdatenManager kbInput = k42Converter.gibKursblockungInput();

		// Weitere Regeln manuell hinzufügen.
		regelFixiereKursInSchiene(kbInput, 52, 1);
		regelFixiereKursInSchiene(kbInput, 44, 1);
		regelFixiereKursInSchiene(kbInput, 17, 1);

		long[] schuelerFixierungen = new long[] { 4, 10, 18, 21, 22, 27, 31, 55, 56, 58, 59, 61, 66, 78, 101, 118, 122,
				125, 128 };
		for (long schuelerID : schuelerFixierungen)
			regelFixiereSchuelerInKurs(kbInput, schuelerID, 0);

		// Berechnung der Blockung und Rückgabe aller Blockungsergebnisse.
		Vector<@NotNull GostBlockungsergebnisManager> kbOutputs = kbAlgorithmus.handle(kbInput);

		// Blockungsergebnisse vorhanden?
		assert kbOutputs != null : "kbOutputs == null";

		// Jedes einzelne Blockungsergebnis prüfen.
		for (GostBlockungsergebnisManager kbOutput : kbOutputs)
			check(kbInput, kbOutput);

	}

	private static void check(@NotNull GostBlockungsdatenManager kbInput, GostBlockungsergebnisManager kbOutput) {
		assert kbInput != null : "kbInput == null";
		assert kbOutput != null : "kbOutput == null";
		assert kbOutput.getBlockungsdatenID() == kbInput.getID() : "kbOutput.getDatenID() != kbInput.getID()";
		assert kbOutput.getOfBewertungAnzahlNichtZugeordneterKurse() <= 0 : "kbOutput.getAnzahlNichtZugeordneterKurse() > 0";

		int kollisionen = kbOutput.getOfBewertungAnzahlKollisionen();
		assert kollisionen == 0 : "kbOutput.getAnzahlKollisionen() != 0 --> " + kollisionen;
		
		int nichtwahlen = kbOutput.getOfBewertungAnzahlNichtzugeordneterFachwahlen();
		assert nichtwahlen >= 0 : "kbOutput.getAnzahlNichtzugeordneterFachwahlen() < 0 --> " + nichtwahlen;
		
//		int regelverletzungen = kbOutput.getErgebnis().bewertung.regelVerletzungen.size();
//		if (regelverletzungen > 0)
//			System.out.println("Regelverletzungen = " + regelverletzungen);
	}

	private long recursive_min_sum_r(long[][] matrix, int r, boolean[] usedC) {
		if (r == matrix.length)
			return 0;

		long min = Long.MAX_VALUE;
		for (int c = 0; c < matrix[r].length; c++) {
			if (!usedC[c]) {
				usedC[c] = true;
				long value = matrix[r][c] + recursive_min_sum_r(matrix, r + 1, usedC);
				min = Math.min(value, min);
				usedC[c] = false;
			}
		}

		return min;
	}

	private long recursive_min_sum_c(long[][] matrix, int c, boolean[] usedR) {
		if (c == matrix[0].length)
			return 0;

		long min = Long.MAX_VALUE;
		for (int r = 0; r < matrix.length; r++) {
			if (!usedR[r]) {
				usedR[r] = true;
				long value = matrix[r][c] + recursive_min_sum_c(matrix, c + 1, usedR);
				min = Math.min(value, min);
				usedR[r] = false;
			}
		}

		return min;
	}

	private static void regelSperreSchieneFuerKursart(@NotNull GostBlockungsdatenManager pInput, @NotNull String pKursart, int pVon, int pBis) {
		GostKursart gKursart = GostKursart.fromKuerzel(pKursart);
		if (gKursart == null)
			throw new AssertionError("GostKursart '" + pKursart + "' nicht gefunden.");

		GostBlockungRegel gRegel = new GostBlockungRegel();
		gRegel.id = pInput.getRegelAnzahl() + 1;
		gRegel.typ = GostKursblockungRegelTyp.KURSART_SPERRE_SCHIENEN_VON_BIS.typ;
		gRegel.parameter.add(Long.valueOf(gKursart.id));
		gRegel.parameter.add(Long.valueOf(pVon));
		gRegel.parameter.add(Long.valueOf(pBis));
		pInput.addRegel(gRegel);
	}

	private static void regelFixiereKurseInSchieneSonstNichts(@NotNull GostBlockungsdatenManager pInput, long[] pKursID,
			int pSchiene) {

		for (@NotNull GostBlockungKurs gKurs : pInput.daten().kurse) {

			boolean gefunden = false;
			for (int j = 0; j < pKursID.length; j++)
				if (gKurs.id == pKursID[j])
					gefunden = true;

			GostBlockungRegel gRegel = new GostBlockungRegel();
			gRegel.id = pInput.getRegelAnzahl() + 1;
			gRegel.typ = gefunden ? GostKursblockungRegelTyp.KURS_FIXIERE_IN_SCHIENE.typ
					: GostKursblockungRegelTyp.KURS_SPERRE_IN_SCHIENE.typ;
			gRegel.parameter.add(gKurs.id);
			gRegel.parameter.add(Long.valueOf(pSchiene));
			pInput.addRegel(gRegel);
		}

	}

	private static void regelVerbieteSchuelerInKurs(@NotNull GostBlockungsdatenManager pInput, long pSchuelerID,
			long pKursID) {
		GostBlockungRegel gRegel = new GostBlockungRegel();
		gRegel.id = pInput.getRegelAnzahl() + 1;
		gRegel.typ = GostKursblockungRegelTyp.SCHUELER_VERBIETEN_IN_KURS.typ;
		gRegel.parameter.add(pSchuelerID);
		gRegel.parameter.add(pKursID);
		pInput.addRegel(gRegel);
	}

	private static void regelFixiereKursInSchiene(@NotNull GostBlockungsdatenManager pInput, long pKursID, int pSchiene) {
		@NotNull GostBlockungKurs kurs = pInput.getKurs(pKursID); // wirft ggf. Exception
		GostBlockungRegel gRegel = new GostBlockungRegel();
		gRegel.id = pInput.getRegelAnzahl() + 1;
		gRegel.typ = GostKursblockungRegelTyp.KURS_FIXIERE_IN_SCHIENE.typ;
		gRegel.parameter.add(kurs.id);
		gRegel.parameter.add(Long.valueOf(pSchiene));
		pInput.addRegel(gRegel);
	}

	private static void regelFixiereSchuelerInKurs(@NotNull GostBlockungsdatenManager pInput, long pSchuelerID, long pKursID) {
		@NotNull GostBlockungKurs kurs = pInput.getKurs(pKursID); // wirft ggf. Exception
		GostBlockungRegel gRegel = new GostBlockungRegel();
		gRegel.id = pInput.getRegelAnzahl() + 1;
		gRegel.typ = GostKursblockungRegelTyp.SCHUELER_FIXIEREN_IN_KURS.typ;
		gRegel.parameter.add(pSchuelerID);
		gRegel.parameter.add(kurs.id);
		pInput.addRegel(gRegel);
	}

	/** Testet die Kursblockungsalgorithmen mit randomisierten Daten. */
	@Test
	@DisplayName("Daten - Random")
	void testeKursblockungAlgorithmusZufaellig() {
		Random lRandom = new Random(1);
		for (int i = 0; i < 10; i++)
			testeKursblockungAlgorithmusZufaelligEinMal(i, lRandom);
	}

	private static void testeKursblockungAlgorithmusZufaelligEinMal(long pID, Random pRandom) {

		// Der Kursblockungsalgorithmus ist ein Service.
		KursblockungAlgorithmus kbAlgorithmus = new KursblockungAlgorithmus();

		// Logger vom Service übernehmen
		Logger log = kbAlgorithmus.getLogger();

		// Consumer triggert 'fail', wenn etwas kritisches geloggt wurde.
		log.addConsumer(new Consumer<LogData>() {
			@Override
			public void accept(LogData t) {
				if (t.getLevel().compareTo(LogLevel.APP) != 0)
					fail(t.getText());
			}
		});

		// Umwandlung von 'Kurs42Daten' zu 'KursblockungInput'.
		GostBlockungsdatenManager kbInput = erzeugeZufallsdaten(pID, pRandom);

		// Berechnung der Blockung und Rückgabe aller Blockungsergebnisse.
		Vector<GostBlockungsergebnisManager> kbOutputs = kbAlgorithmus.handle(kbInput);

		// Blockungsergebnisse vorhanden?
		if (kbOutputs == null)
			throw new AssertionError("kbOutputs == null");

		// Jedes einzelne Blockungsergebnis prüfen.
		for (GostBlockungsergebnisManager kbOutput : kbOutputs)
			check(kbInput, kbOutput);
	}

	private static GostBlockungsdatenManager erzeugeZufallsdaten(long pID, Random pRandom) {

		int nKurse = pRandom.nextInt(100);
		int nFaecher = 1 + (nKurse / 2);
		int nKursarten = 1 + pRandom.nextInt(2);
		int nSchueler = nKurse * 2;
		int nSchienen = 1 + pRandom.nextInt(15);

		// Erzeuge alle Schienen.
		HashMap<Long, GostBlockungSchiene> mapSchienen = new HashMap<>();
		for (int i = 1; i <= nSchienen; i++) {
			GostBlockungSchiene gSchiene = new GostBlockungSchiene();
			gSchiene.id = i; // Pseudo-ID
			gSchiene.nummer = i;
			gSchiene.bezeichnung = "Schiene " + i;
			mapSchienen.put(gSchiene.id, gSchiene);
		}

		// Erzeuge alle Fächer.
		HashMap<String, GostFach> mapFaecher = new HashMap<>();
		for (int fachID = 0; fachID < nFaecher; fachID++) {
			GostFach gFach = new GostFach();
			gFach.id = fachID;
			gFach.kuerzel = "Test-Fach Nr. " + fachID;
			mapFaecher.put(gFach.kuerzel, gFach);
		}

		// Erzeuge alle Kursarten.
		HashMap<Integer, GostKursart> mapKursarten = new HashMap<>();
		for (int kursartID = 1; kursartID <= nKursarten; kursartID++) {
			GostKursart gKursart = GostKursart.fromID(kursartID);
			mapKursarten.put(gKursart.id, gKursart);
		}

		// Erzeuge alle Kurse und ordne sie einer zufälligen Schiene zu.
		HashMap<Long, GostBlockungKurs> mapKurse = new HashMap<>();
		HashMap<Long, GostBlockungSchiene> mapKursIDSchiene = new HashMap<>();
		for (int kursID = 0; kursID < nKurse; kursID++) {
			GostBlockungKurs gKurs = new GostBlockungKurs();
			gKurs.id = kursID;
			gKurs.nummer = 1;
			gKurs.fach_id = pRandom.nextLong(nFaecher);
			gKurs.kursart = pRandom.nextInt(nKursarten) + 1;
			gKurs.anzahlSchienen = 1;
			mapKurse.put(gKurs.id, gKurs);
			long schienenID = pRandom.nextLong(nSchienen) + 1;
			GostBlockungSchiene gSchiene = mapSchienen.get(schienenID);
			mapKursIDSchiene.put(gKurs.id, gSchiene);
		}

		// Erzeuge alle SchülerInnen und zufällige Fachwahlen.
		HashMap<Long, Schueler> mapSchueler = new HashMap<>();
		HashMap<Integer, GostFachwahl> mapFachwahlen = new HashMap<>();
		for (int schuelerID = 1; schuelerID <= nSchueler; schuelerID++) {
			Schueler gSchueler = new Schueler();
			gSchueler.id = schuelerID;
			gSchueler.nachname = "Nachname" + schuelerID;
			gSchueler.vorname = "Vorname" + schuelerID;
			gSchueler.geschlecht = 3;
			mapSchueler.put(gSchueler.id, gSchueler);

			HashSet<Long> setUsedSchiene = new HashSet<>();
			HashSet<Long> setUsedFach = new HashSet<>();
			for (int rnd : KursblockungStatic.gibPermutation(pRandom, nKurse)) {
				// Hole zufälligen Kurs.
				long kursID = rnd;
				GostBlockungKurs gKurs = mapKurse.get(kursID);

				// Fach bereits gewählt?
				if (setUsedFach.contains(gKurs.fach_id))
					continue;

				// Schiene bereits gewählt?
				GostBlockungSchiene gSchiene = mapKursIDSchiene.get(gKurs.id);
				if (setUsedSchiene.contains(gSchiene.id))
					continue;

				// Fachwahl hinzufügen.
				setUsedFach.add(gKurs.fach_id);
				setUsedSchiene.add(gSchiene.id);
				GostFachwahl gFachwahl = new GostFachwahl();
				gFachwahl.fachID = gKurs.fach_id;
				gFachwahl.kursartID = gKurs.kursart;
				gFachwahl.schuelerID = gSchueler.id;
				mapFachwahlen.put(mapFachwahlen.size(), gFachwahl);
			}

		}

		// Fixiere alle Kurse in ihren Schienen. Der Algorithmus muss nur die SuS verteilen.
		HashMap<Long, GostBlockungRegel> mapRegeln = new HashMap<>();
		for (GostBlockungKurs gKurs : mapKurse.values()) {
			GostBlockungRegel gRegel = new GostBlockungRegel();
			gRegel.id = mapRegeln.size() + 1;
			gRegel.typ = GostKursblockungRegelTyp.KURS_FIXIERE_IN_SCHIENE.typ;
			GostBlockungSchiene gSchiene = mapKursIDSchiene.get(gKurs.id);
			gRegel.parameter.add(gKurs.id);
			gRegel.parameter.add(gSchiene.id);
			mapRegeln.put(gRegel.id, gRegel);
		}

		// Erzeuge GostBlockungsdaten
		GostBlockungsdaten gDaten = new GostBlockungsdaten();
		gDaten.id = 1L; // Pseudo-ID
		gDaten.kurse.addAll(mapKurse.values());
		gDaten.regeln.addAll(mapRegeln.values());
		gDaten.schueler.addAll(mapSchueler.values());
		gDaten.fachwahlen.addAll(mapFachwahlen.values());
		gDaten.schienen.addAll(mapSchienen.values());

		// Erzeuge GostFaecherManager
		GostFaecherManager fManager = new GostFaecherManager();
		fManager.addAll(mapFaecher.values());

		// Erzeuge GostBlockungsdatenManager
		GostBlockungsdatenManager input = new GostBlockungsdatenManager(gDaten, fManager);
		input.setMaxTimeMillis(10);
		return input;
	}

	/** Testet Wahlen, deren Kurse nicht eingerichtet sind. */
	@Test
	@DisplayName("Daten Spezial.")
	void testeWahlOhneKurs() {
		// Der Kursblockungsalgorithmus ist ein Service.
		KursblockungAlgorithmus kbAlgorithmus = new KursblockungAlgorithmus();

		// Logger vom Service übernehmen
		Logger log = kbAlgorithmus.getLogger();

		// Consumer triggert 'fail', wenn etwas kritisches geloggt wurde.
		log.addConsumer(new Consumer<LogData>() {
			@Override
			public void accept(LogData t) {
				if (t.getLevel().compareTo(LogLevel.ERROR) == 0)
					fail(t.getText());
			}
		});

		// Erzeuge alle Schienen.
		int nSchienen = 11;
		HashMap<Long, GostBlockungSchiene> mapSchienen = new HashMap<>();
		for (int i = 1; i <= nSchienen; i++) {
			GostBlockungSchiene gSchiene = new GostBlockungSchiene();
			gSchiene.id = i; // Pseudo-ID
			gSchiene.nummer = i;
			gSchiene.bezeichnung = "Schiene " + i;
			mapSchienen.put(gSchiene.id, gSchiene);
		}

		GostFach kbFach1 = new GostFach();
		kbFach1.id = 1;
		GostFach kbFach2 = new GostFach();
		kbFach2.id = 2;

		GostBlockungKurs kbKurs = new GostBlockungKurs();
		kbKurs.id = 1;
		kbKurs.nummer = 1;
		kbKurs.fach_id = 1;
		kbKurs.kursart = GostKursart.GK.id;
		kbKurs.anzahlSchienen = 1;

		Schueler kbSchueler = new Schueler();
		kbSchueler.id = 1;

		// Fachwahl ohne Kurs!
		GostFachwahl kbFachwahl = new GostFachwahl();
		kbFachwahl.schuelerID = 1;
		kbFachwahl.fachID = 2;
		kbFachwahl.kursartID = 2;

		// Erzeuge GostBlockungsdaten
		GostBlockungsdaten gDaten = new GostBlockungsdaten();
		gDaten.id = 1L; // Pseudo-ID
		gDaten.kurse.add(kbKurs);
		gDaten.schueler.add(kbSchueler);
		gDaten.fachwahlen.add(kbFachwahl);
		gDaten.schienen.addAll(mapSchienen.values());
		// gDaten.regeln.addAll(mapRegeln.values());

		// Erzeuge GostFaecherManager
		GostFaecherManager fManager = new GostFaecherManager();
		fManager.add(kbFach1);
		fManager.add(kbFach2);

		// Erzeuge GostBlockungsdatenManager
		GostBlockungsdatenManager kbInput = new GostBlockungsdatenManager(gDaten, fManager);
		kbInput.setMaxTimeMillis(100);

		// Berechnung der Blockung und Rückgabe aller Blockungsergebnisse.
		Vector<@NotNull GostBlockungsergebnisManager> kbOutputs = kbAlgorithmus.handle(kbInput);

		// Blockungsergebnisse vorhanden?
		assert kbOutputs != null : "kbOutputs == null";

		// Jedes einzelne Blockungsergebnis prüfen.
		for (GostBlockungsergebnisManager kbOutput : kbOutputs)
			check(kbInput, kbOutput);
	}

}
