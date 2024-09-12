package de.svws_nrw.module.reporting.html;

import java.util.Arrays;
import java.util.List;

import de.svws_nrw.core.types.benutzer.BenutzerKompetenz;
import de.svws_nrw.core.types.reporting.ReportingReportvorlage;

/**
 * Eine ENUM der integrierten Report-Vorlagen des SVWS-Servers.
 * Sie definiert die im CoreType {@link ReportingReportvorlage} angelegten Report-Vorlagen
 * hinsichtlich der verwendeten Template-Dateien und Benutzerrechte genauer.
 * --------
 * Hinweis:
 * --------
 * Die Bezeichnungen der ENUM-Werte dienen auch dazu, die Daten-Contexts korrekt zu füllen.
 * Die Benennung der Vorlagen erfolgt nach dem Schema Hauptdaten_v_Detaildaten. Bei der Report-Generierung erfolgt in
 * Teilen ein entsprechendes Füllen der Datenkontexte anhand der Benennung.
 * ----------
 * Anmerkung:
 * ----------
 * Es gibt zwei Einträge für die erzeugung des Dateinamens. Einen "statischen" Dateinamen und eine Dateinamensvorlage.
 * Die Vorlage stellt den BODY eines html-Thymeleaf-Templates dar, welches eine einzige Zeile mit einem Absatz-Tag
 * ergeben muss. Diese Zeile wird dann für die Erzeugung des Dateinamens aus den Daten genutzt.
 */
public enum HtmlTemplateDefinition {

	/** Report-Vorlage: GOSt - Klausurplanung - Klausurtermine - Kurse */
	GOST_KLAUSURPLANUNG_v_KLAUSURTERMINE_MIT_KURSEN(
			ReportingReportvorlage.GOST_KLAUSURPLANUNG_v_KLAUSURTERMINE_MIT_KURSEN,
			"de/svws_nrw/module/reporting/gost/klausurplanung/GostKlausurplanungKlausurtermineMitKursen.html",
			"de/svws_nrw/module/reporting/gost/klausurplanung/GostKlausurplanungKlausurtermineMitKursen.css",
			"GOSt-Klausurplanung-Klausurtermine-Kurse",
			"""
			        <p th:text="${'GOSt-Klausurplanung-Klausurtermine-Kurse_' + #strings.replace(#strings.replace(Schule.auswahlSchuljahresabschnitt().textSchuljahresabschnittKurz(), '.', ''), '/', '-')}"></p>
			""",
			Arrays.asList(BenutzerKompetenz.OBERSTUFE_KLAUSURPLANUNG_ANSEHEN_ALLGEMEIN, BenutzerKompetenz.OBERSTUFE_KLAUSURPLANUNG_AENDERN,
					BenutzerKompetenz.OBERSTUFE_KLAUSURPLANUNG_ANSEHEN_FUNKTION)),

	/** Report-Vorlage: GOSt - Klausurplanung - Schueler - Klausuren */
	GOST_KLAUSURPLANUNG_v_SCHUELER_MIT_KLAUSUREN(
			ReportingReportvorlage.GOST_KLAUSURPLANUNG_v_SCHUELER_MIT_KLAUSUREN,
			"de/svws_nrw/module/reporting/gost/klausurplanung/GostKlausurplanungSchuelerMitKlausuren.html",
			"de/svws_nrw/module/reporting/gost/klausurplanung/GostKlausurplanungSchuelerMitKlausuren.css",
			"GOSt-Klausurplanung-Schueler-Klausuren",
			"""
			        <p th:if="${GostKlausurplan.schuelerGefiltert().isEmpty()}" th:text="${'GOSt-Klausurplanung-Schueler-Klausuren_' + #strings.replace(#strings.replace(Schule.auswahlSchuljahresabschnitt().textSchuljahresabschnittKurz(), '.', ''), '/', '-')}"></p>
			        <th:block th:if="${!GostKlausurplan.schuelerGefiltert().isEmpty()}" th:each="schueler,iterState : ${GostKlausurplan.schuelerGefiltert()}">
			            <p th:if="${iterState.first && (GostKlausurplan.schuelerGefiltert().size() == 1)}" th:text="${'GOSt-Klausurplanung-Schueler-Klausuren_' + #strings.replace(#strings.replace(Schule.auswahlSchuljahresabschnitt().textSchuljahresabschnittKurz(), '.', ''), '/', '-') + '_' + #strings.replace(schueler.nachname(), ' ', '_') + '__' + #strings.replace(schueler.vorname(), ' ', '_') + '_(' + schueler.id() + ')_' + #dates.format(#dates.createNow(), 'yyyyMMdd-HHmm')}"></p>
			            <p th:if="${iterState.first && (GostKlausurplan.schuelerGefiltert().size() > 1)}" th:text="${'GOSt-Klausurplanung-Schueler-Klausuren_' + #strings.replace(#strings.replace(Schule.auswahlSchuljahresabschnitt().textSchuljahresabschnittKurz(), '.', ''), '/', '-')}"></p>
			        </th:block>
			""",
			Arrays.asList(BenutzerKompetenz.OBERSTUFE_KLAUSURPLANUNG_ANSEHEN_ALLGEMEIN, BenutzerKompetenz.OBERSTUFE_KLAUSURPLANUNG_AENDERN,
					BenutzerKompetenz.OBERSTUFE_KLAUSURPLANUNG_ANSEHEN_FUNKTION)),

	/** Report-Vorlage: GOSt - Kursplanung - Kurs - Kurschüler */
	GOST_KURSPLANUNG_v_KURS_MIT_KURSSCHUELERN(
			ReportingReportvorlage.GOST_KURSPLANUNG_v_KURS_MIT_KURSSCHUELERN,
			"de/svws_nrw/module/reporting/gost/kursplanung/GostKursplanungKursMitKursschuelern.html",
			"de/svws_nrw/module/reporting/gost/kursplanung/GostKursplanungKursMitKursschuelern.css",
			"GOSt-Blockungsergebnis-Kurs-Schueler",
			"""
			        <p th:text="${'GOSt-Blockungsergebnis-Kurs-Schueler_Abi' + GostBlockungsergebnis.abiturjahr() + '_' + #strings.replace(GostBlockungsergebnis.gostHalbjahr().kuerzel, '.', '') + '_(Erg-ID-' + GostBlockungsergebnis.id() + ')'}"></p>
			""",
			Arrays.asList(BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_ALLGEMEIN, BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_FUNKTIONSBEZOGEN,
					BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_ALLGEMEIN, BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_FUNKTIONSBEZOGEN)),

	/** Report-Vorlage: GOSt - Kursplanung - Kurse - Statistikwerten */
	GOST_KURSPLANUNG_v_KURSE_MIT_STATISTIKWERTEN(
			ReportingReportvorlage.GOST_KURSPLANUNG_v_KURSE_MIT_STATISTIKWERTEN,
			"de/svws_nrw/module/reporting/gost/kursplanung/GostKursplanungKurseMitStatistikwerten.html",
			"de/svws_nrw/module/reporting/gost/kursplanung/GostKursplanungKurseMitStatistikwerten.css",
			"GOSt-Blockungsergebnis-Kurse-Statistikwerte",
			"""
			        <p th:text="${'GOSt-Blockungsergebnis-Kurse-Statistikwerte_Abi' + GostBlockungsergebnis.abiturjahr() + '_' + #strings.replace(GostBlockungsergebnis.gostHalbjahr().kuerzel, '.', '') + '_(Erg-ID-' + GostBlockungsergebnis.id() + ')'}"></p>
			""",
			Arrays.asList(BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_ALLGEMEIN, BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_FUNKTIONSBEZOGEN,
					BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_ALLGEMEIN, BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_FUNKTIONSBEZOGEN)),

	/** Report-Vorlage: GOSt - Kursplanung - Schüler - Kurse */
	GOST_KURSPLANUNG_v_SCHUELER_MIT_KURSEN(
			ReportingReportvorlage.GOST_KURSPLANUNG_v_SCHUELER_MIT_KURSEN,
			"de/svws_nrw/module/reporting/gost/kursplanung/GostKursplanungSchuelerMitKursen.html",
			"de/svws_nrw/module/reporting/gost/kursplanung/GostKursplanungSchuelerMitKursen.css",
			"GOSt-Blockungsergebnis-Schueler-Kurse",
			"""
			        <p th:text="${'GOSt-Blockungsergebnis-Schueler-Kurse_Abi' + GostBlockungsergebnis.abiturjahr() + '_' + #strings.replace(GostBlockungsergebnis.gostHalbjahr().kuerzel, '.', '') + '_(Erg-ID-' + GostBlockungsergebnis.id() + ')'}"></p>
			""",
			Arrays.asList(BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_ALLGEMEIN, BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_FUNKTIONSBEZOGEN,
					BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_ALLGEMEIN, BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_FUNKTIONSBEZOGEN)),

	/** Report-Vorlage: GOSt - Kursplanung - Schüler - Schienen-Kurse */
	GOST_KURSPLANUNG_v_SCHUELER_MIT_SCHIENEN_KURSEN(
			ReportingReportvorlage.GOST_KURSPLANUNG_v_SCHUELER_MIT_SCHIENEN_KURSEN,
			"de/svws_nrw/module/reporting/gost/kursplanung/GostKursplanungSchuelerMitSchienenKursen.html",
			"de/svws_nrw/module/reporting/gost/kursplanung/GostKursplanungSchuelerMitSchienenKursen.css",
			"GOSt-Blockungsergebnis-Schueler-Schienen-Kurse",
			"""
			        <p th:text="${'GOSt-Blockungsergebnis-Schueler-Schienen-Kurse_Abi' + GostBlockungsergebnis.abiturjahr() + '_' + #strings.replace(GostBlockungsergebnis.gostHalbjahr().kuerzel, '.', '') + '_(Erg-ID-' + GostBlockungsergebnis.id() + ')'}"></p>
			""",
			Arrays.asList(BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_ALLGEMEIN, BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_FUNKTIONSBEZOGEN,
					BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_ALLGEMEIN, BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_FUNKTIONSBEZOGEN)),

	/** Report-Vorlage: Schüler - GOSt - Abitur - APO - Anlage 12 (Abiturzeugnis) */
	SCHUELER_v_GOST_ABITUR_APO_ANLAGE_12(
			ReportingReportvorlage.SCHUELER_v_GOST_ABITUR_APO_ANLAGE_12,
			"de/svws_nrw/module/reporting/schueler/gost/abitur/apo/SchuelerGostAbiturApoAnlage12.html",
			"de/svws_nrw/module/reporting/schueler/gost/abitur/apo/SchuelerGostAbiturApoAnlage12.css",
			"APO-GOSt-Anlage12",
			"""
			        <p th:if="${Schueler.isEmpty()}">APO-GOSt-Anlage12</p>
			        <th:block th:if="${!Schueler.isEmpty()}" th:each="schueler,iterState : ${Schueler}">
			            <p th:if="${iterState.first && (Schueler.size() == 1)}" th:text="${'Abitur' + schueler.gostAbitur().abiturjahr() + '_APO-GOSt-Anlage12_' + '_' + #strings.replace(schueler.nachname(), ' ', '_') + '__' + #strings.replace(schueler.vorname(), ' ', '_') + '_(' + schueler.id() + ')'}"></p>
			            <p th:if="${iterState.first && (Schueler.size() > 1)}" th:text="${'Abitur' + schueler.gostAbitur().abiturjahr() + '_APO-GOSt-Anlage12'}"></p>
			        </th:block>
			""",
			Arrays.asList(BenutzerKompetenz.ABITUR_ANSEHEN_ALLGEMEIN, BenutzerKompetenz.ABITUR_ANSEHEN_FUNKTIONSBEZOGEN)),

	/** Report-Vorlage: Schüler - GOSt - Laufbahnplanung - Ergebnisübersicht */
	SCHUELER_v_GOST_LAUFBAHNPLANUNG_ERGEBNISUEBERSICHT(
			ReportingReportvorlage.SCHUELER_v_GOST_LAUFBAHNPLANUNG_ERGEBNISUEBERSICHT,
			"de/svws_nrw/module/reporting/schueler/gost/laufbahnplanung/SchuelerGostLaufbahnplanungErgebnisuebersicht.html",
			"de/svws_nrw/module/reporting/schueler/gost/laufbahnplanung/SchuelerGostLaufbahnplanungErgebnisuebersicht.css",
			"GOSt-Laufbahnplanung-Pruefungsergebnisse",
			"""
			        <p th:if="${Schueler.isEmpty()}">GOSt-Laufbahnplanung-Pruefungsergebnisse"</p>
			        <th:block th:if="${!Schueler.isEmpty()}" th:each="schueler,iterState : ${Schueler}">
			            <p th:if="${iterState.first}" th:text="${'GOSt-Laufbahnplanung-Pruefungsergebnisse_Abi' + schueler.gostLaufbahnplanung().abiturjahr() + '_' + #strings.replace(schueler.gostLaufbahnplanung().auswahlGOStHalbjahr(), '.', '')}"></p>
			        </th:block>
			""",
			Arrays.asList(BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_ALLGEMEIN, BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_FUNKTIONSBEZOGEN,
					BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_ALLGEMEIN, BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_FUNKTIONSBEZOGEN)),

	/** Report-Vorlage: Schüler - GOSt - Laufbahnplanung - Wahlbogen */
	SCHUELER_v_GOST_LAUFBAHNPLANUNG_WAHLBOGEN(
			ReportingReportvorlage.SCHUELER_v_GOST_LAUFBAHNPLANUNG_WAHLBOGEN,
			"de/svws_nrw/module/reporting/schueler/gost/laufbahnplanung/SchuelerGostLaufbahnplanungWahlbogen.html",
			"de/svws_nrw/module/reporting/schueler/gost/laufbahnplanung/SchuelerGostLaufbahnplanungWahlbogen.css",
			"GOSt-Laufbahnplanung-Wahlboegen",
			"""
			        <p th:if="${Schueler.isEmpty()}">GOSt-Laufbahnplanung-Wahlboegen"</p>
			        <th:block th:if="${!Schueler.isEmpty()}" th:each="schueler,iterState : ${Schueler}">
			            <p th:if="${iterState.first && (Schueler.size() == 1)}" th:text="${'GOSt-Laufbahnwahl_Abi' + schueler.gostLaufbahnplanung().abiturjahr() + '_' + #strings.replace(schueler.gostLaufbahnplanung().folgeAuswahlGOStHalbjahr(), '.', '') + '_' + #strings.replace(schueler.nachname(), ' ', '_') + '__' + #strings.replace(schueler.vorname(), ' ', '_') + '_(' + schueler.id() + ')_' + #dates.format(#dates.createNow(), 'yyyyMMdd-HHmm')}"></p>
			            <p th:if="${iterState.first && (Schueler.size() > 1)}" th:text="${'GOSt-Laufbahnwahl_Abi' + schueler.gostLaufbahnplanung().abiturjahr() + '_' + #strings.replace(schueler.gostLaufbahnplanung().folgeAuswahlGOStHalbjahr(), '.', '')}"></p>
			        </th:block>
			""",
			Arrays.asList(BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_ALLGEMEIN, BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_FUNKTIONSBEZOGEN,
					BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_ALLGEMEIN, BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_FUNKTIONSBEZOGEN)),

	/** Report-Vorlage: Schüler - Stammdaten - Liste */
	SCHUELER_v_STAMMDATEN_LISTE(
			ReportingReportvorlage.SCHUELER_v_STAMMDATEN_LISTE,
			"de/svws_nrw/module/reporting/schueler/stammdaten/SchuelerStammdatenListe.html",
			"de/svws_nrw/module/reporting/schueler/stammdaten/SchuelerStammdatenListe.css",
			"Schueler-Liste",
			"""
			        <p th:if="${Schueler.isEmpty()}">Schueler-Liste"</p>
			        <th:block th:if="${!Schueler.isEmpty()}" th:each="schueler,iterState : ${Schueler}">
			            <p th:if="${iterState.first}" th:text="${'Schueler-Liste_' + #dates.format(#dates.createNow(), 'yyyyMMdd-HHmm')}"></p>
			        </th:block>
			""",
			Arrays.asList(BenutzerKompetenz.SCHUELER_INDIVIDUALDATEN_ANSEHEN));



	/** Der CoreType {@link ReportingReportvorlage} dieser Template-Definition. */
	private final ReportingReportvorlage reportingReportvorlage;

	/** Der Pfad zur html-Template-Datei dieser Template-Definition. */
	private final String pfadHtmlTemplate;

	/** Der Pfad zur CSS-Datei der html-Template-Datei dieser Template-Definition. */
	private final String pfadCss;

	/** Der statische Dateiname ohne Dateiendung, der bei der Ausgabe als ZIP-Datei verwendet wird. */
	private final String dateiname;

	/** Die Vorlage für dynamische Generierung des Dateinamens ohne Dateiendung. Sie ist in der Form eines thymeleaf-html-Templates anzulegen. */
	private final String dateinamensvorlage;

	/** Die List mit Benutzerkompetenzen gemäß {@link BenutzerKompetenz}, die zur Nutzung des Templates erforderlich sind. */
	private final List<BenutzerKompetenz> benutzerKompetenzen;



	/**
	 * Erstellt eine neue Template-Definition
	 * @param reportingReportvorlage Der CoreType {@link ReportingReportvorlage} des Templates
	 * @param pfadHtmlTemplate Der Pfad zur html-Template-Datei
	 * @param pfadCss Pfad zur CSS-Datei der html-Template-Datei
	 * @param dateiname Der statische Dateiname ohne Dateiendung, der bei der Ausgabe als ZIP-Datei verwendet wird.
	 * @param dateinamensvorlage Die Vorlage für dynamische Generierung des Dateinamens ohne Dateiendung. Sie ist in der Form eines thymeleaf-html-Templates anzulegen.
	 * @param benutzerKompetenzen Die List mit Benutzerkompetenzen gemäß {@link BenutzerKompetenz}, die zur Nutzung des Templates erforderlich sind.
	 */
	HtmlTemplateDefinition(final ReportingReportvorlage reportingReportvorlage, final String pfadHtmlTemplate, final String pfadCss, final String dateiname,
			final String dateinamensvorlage, final List<BenutzerKompetenz> benutzerKompetenzen) {
		this.reportingReportvorlage = reportingReportvorlage;
		this.pfadHtmlTemplate = pfadHtmlTemplate;
		this.pfadCss = pfadCss;
		this.dateiname = dateiname;
		this.dateinamensvorlage = dateinamensvorlage;
		this.benutzerKompetenzen = benutzerKompetenzen;
	}



	/**
	 * Gibt den CoreType {@link ReportingReportvorlage} dieser Template-Definition zurück
	 * @return Der CoreType dieser Template-Definition
	 */
	public ReportingReportvorlage getReportingReportvorlage() {
		return this.reportingReportvorlage;
	}

	/**
	 * Gibt den Dateipfad der html-Template-Datei dieser Template-Definition zurück
	 * @return Der Dateipfad zur html-Template-Datei
	 */
	public String getPfadHtmlTemplate() {
		return this.pfadHtmlTemplate;
	}

	/**
	 * Gibt den Dateipfad der CSS_Datei zur html-Template-Datei dieser Template-Definition zurück
	 * @return Der Dateipfad zur CSS-Datei
	 */
	public String getPfadCss() {
		return this.pfadCss;
	}

	/**
	 * Gibt den statischen Dateinamen ohne Dateiendung zurück, der bei der Ausgabe als ZIP-Datei verwendet wird.
	 * @return Der statische Dateiname
	 */
	public String getDateiname() {
		return this.dateiname;
	}

	/**
	 * Gibt die Vorlage für die dynamische Generierung des Dateinamens ohne Dateiendung zurück.
	 * @return Die Vorlage für den Dateinamen
	 */
	public String getDateinamensvorlage() {
		return """
		<html lang="de" xmlns:th="http://www.thymeleaf.org">
		    <head>
		        <meta charset="utf-8" />
		        <meta name="viewport" content="width=device-width" />
		        <title>Dateinamensdefinition</title>
		    </head>
		    <body>
		        %s
		    </body>
		</html>
		""".formatted(this.dateinamensvorlage);
	}

	/**
	 * Gibt die Benutzer-Kompetenzen für diese Template-Definition zurück
	 * @return Die Liste der Benutzerkompetenzen
	 */
	public List<BenutzerKompetenz> getBenutzerKompetenzen() {
		return this.benutzerKompetenzen;
	}



	/**
	 * Diese Methode ermittelt die Report-Vorlage anhand des übergebenen Namens.
	 * @param reportingReportvorlage  Der CoreType der Report-Vorlage
	 * @return Die TemplateDefinition
	 */
	public static HtmlTemplateDefinition getByType(final ReportingReportvorlage reportingReportvorlage) {
		for (final HtmlTemplateDefinition td : HtmlTemplateDefinition.values())
			if (td.reportingReportvorlage == reportingReportvorlage)
				return td;
		return null;
	}

}
