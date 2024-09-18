package de.svws_nrw.module.reporting.html.contexts;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.svws_nrw.module.reporting.types.schueler.ReportingSchueler;
import jakarta.ws.rs.core.Response;
import org.thymeleaf.context.Context;

import de.svws_nrw.asd.adt.Pair;
import de.svws_nrw.core.data.gost.klausurplanung.GostKlausurenCollectionAllData;
import de.svws_nrw.core.utils.gost.klausurplanung.GostKlausurplanManager;
import de.svws_nrw.data.gost.klausurplan.DataGostKlausuren;
import de.svws_nrw.db.utils.ApiOperationException;
import de.svws_nrw.module.reporting.proxytypes.gost.klausurplanung.ProxyReportingGostKlausurplanungKlausurplan;
import de.svws_nrw.module.reporting.repositories.ReportingRepository;
import de.svws_nrw.module.reporting.types.gost.klausurplanung.ReportingGostKlausurplanungKlausurplan;


/**
 * Ein ThymeLeaf-Html-Daten-Context zum Bereich "GostKlausurplanung", um ThymeLeaf-html-Templates mit Daten zu füllen und daraus PDF-Dateien zu erstellen.
 */
public final class HtmlContextGostKlausurplanungKlausurplan extends HtmlContext {

	/** Repository für die Reporting. */
	@JsonIgnore
	private final ReportingRepository reportingRepository;

	/** Klausurplan dieses Contexts. */
	@JsonIgnore
	private ReportingGostKlausurplanungKlausurplan gostKlausurplan;

	/**
	 * Initialisiert einen neuen HtmlContext mit den übergebenen Daten.
	 *
	 * @param reportingRepository	Das Repository mit Daten zum Reporting.
	 * @param idsFilterSchueler 	Eine Liste, die die schülerbezogene Ausgabe auf die Schüler mit den enthaltenen IDs beschränkt.
	 *
	 * @throws ApiOperationException   im Fehlerfall
	 */
	public HtmlContextGostKlausurplanungKlausurplan(final ReportingRepository reportingRepository, final List<Long> idsFilterSchueler)
			throws ApiOperationException {
		this.reportingRepository = reportingRepository;
		erzeugeContext(idsFilterSchueler);
	}

	/**
	 * Initialisiert einen neuen HtmlContext mit den übergebenen Daten.
	 *
	 * @param reportingRepository	Das Repository mit Daten zum Reporting.
	 * @param gostKlausurplan		Ein GOSt-Klausurplan, auf dem dieser Kontext aufbauen sollen.
	 * @param idsFilterSchueler 	Eine Liste, die die schülerbezogene Ausgabe auf die Schüler mit den enthaltenen IDs beschränkt.
	 */
	public HtmlContextGostKlausurplanungKlausurplan(final ReportingRepository reportingRepository, final ReportingGostKlausurplanungKlausurplan gostKlausurplan,
			final List<Long> idsFilterSchueler) {
		this.reportingRepository = reportingRepository;
		erzeugeContextFromKlausurplan(gostKlausurplan, idsFilterSchueler);
	}


	/**
	 * Erzeugt den Context zum Füllen eines html-Templates.
	 *
	 * @param idsFilterSchueler 		Eine Liste, die die schülerbezogene Ausgabe auf die Schüler mit den enthaltenen IDs beschränkt.
	 *
	 * @throws ApiOperationException   	im Fehlerfall
	 */
	private void erzeugeContext(final List<Long> idsFilterSchueler) throws ApiOperationException {

		// In den idsHauptdaten der Reporting-Parameter werden im Wechsel das Abiturjahr und des GostHlabjahr (0 = EF.1 bis 5 = Q2.2) übergeben.
		// Hier werden die Daten NICHT validiert. Die Daten aus den Parametern müssen vorab validiert worden sein (ReportingValidierung).
		final List<Long> parameterDaten = reportingRepository.reportingParameter().idsHauptdaten.stream().filter(Objects::nonNull).toList();
		final List<Pair<Integer, Integer>> selection = new ArrayList<>();

		if (!parameterDaten.isEmpty()) {
			// Stelle die übergebenen Stufen und Halbjahre zusammen.
			for (int i = 0; i < parameterDaten.size(); i = i + 2) {
				selection.add(new Pair<>(Math.toIntExact(parameterDaten.get(i)), Math.toIntExact(parameterDaten.get(i + 1))));
			}
		} else {
			// Es wurden keine Stufen übergeben. Erzeuge die Ausgabe für alle Stufen gemäß Schuljahresabschnitt im Client.
			// EF:
			selection.add(new Pair<>(reportingRepository.auswahlSchuljahresabschnitt().schuljahr() + 3,
					reportingRepository.auswahlSchuljahresabschnitt().abschnitt() - 1));
			// Q1:
			selection.add(new Pair<>(reportingRepository.auswahlSchuljahresabschnitt().schuljahr() + 2,
					reportingRepository.auswahlSchuljahresabschnitt().abschnitt() + 1));
			// Q2:
			selection.add(new Pair<>(reportingRepository.auswahlSchuljahresabschnitt().schuljahr() + 1,
					reportingRepository.auswahlSchuljahresabschnitt().abschnitt() + 3));
		}

		try {
			final GostKlausurenCollectionAllData allData = DataGostKlausuren.getAllData(reportingRepository.conn(), selection);
			final GostKlausurplanManager gostKlausurManager =
					new GostKlausurplanManager(reportingRepository.auswahlSchuljahresabschnitt().schuljahr(), allData);

			this.gostKlausurplan = new ProxyReportingGostKlausurplanungKlausurplan(reportingRepository, gostKlausurManager, idsFilterSchueler);

			// Daten-Context für Thymeleaf erzeugen.
			final Context context = new Context();
			context.setVariable("GostKlausurplan", this.gostKlausurplan);

			super.setContext(context);
		} catch (final ApiOperationException e) {
			throw new ApiOperationException(Response.Status.NOT_FOUND, e,
					"FEHLER: Zu mindestens einer Stufe konnten keine Klausurplanungsdaten ermittelt werden. Es konnte kein html-Klausuren-Kontext erstellt werden.");
		}
	}


	/**
	 * Erzeugt den Context zum Füllen eines html-Templates.
	 *
	 * @param gostKlausurplan		Ein GOSt-Klausurplan, auf dem dieser Kontext aufbauen sollen.
	 * @param idsFilterSchueler 	Eine Liste, die die schülerbezogene Ausgabe auf die Schüler mit den enthaltenen IDs beschränkt.
	 */
	private void erzeugeContextFromKlausurplan(final ReportingGostKlausurplanungKlausurplan gostKlausurplan, final List<Long> idsFilterSchueler) {
		this.gostKlausurplan = new ProxyReportingGostKlausurplanungKlausurplan(reportingRepository, gostKlausurplan.klausurtermine(), gostKlausurplan.kurse(),
				gostKlausurplan.kursklausuren(), gostKlausurplan.schueler(), gostKlausurplan.schuelerklausuren(), idsFilterSchueler);

		// Daten-Context für Thymeleaf erzeugen.
		final Context context = new Context();
		context.setVariable("GostKlausurplan", this.gostKlausurplan);

		super.setContext(context);
	}


	/**
	 * Teile diesen Context mit allen Schülern in eine Liste von Contexts auf, die jeweils auf einen Schüler filtern.
	 *
	 * @return	Liste der Einzel-Contexts.
	 */
	public List<HtmlContextGostKlausurplanungKlausurplan> getEinzelSchuelerContexts() {
		final List<HtmlContextGostKlausurplanungKlausurplan> resultContexts = new ArrayList<>();

		final List<Long> idsSchuelerImContext =
				this.gostKlausurplan.idsFilterSchueler().isEmpty() ? this.gostKlausurplan.schueler().stream().map(ReportingSchueler::id).toList()
						: this.gostKlausurplan.idsFilterSchueler();

		for (final Long idSchueler : idsSchuelerImContext) {
			final List<Long> einSchueler = new ArrayList<>();
			einSchueler.add(idSchueler);
			resultContexts.add(new HtmlContextGostKlausurplanungKlausurplan(this.reportingRepository, this.gostKlausurplan, einSchueler));
		}

		return resultContexts;
	}
}
