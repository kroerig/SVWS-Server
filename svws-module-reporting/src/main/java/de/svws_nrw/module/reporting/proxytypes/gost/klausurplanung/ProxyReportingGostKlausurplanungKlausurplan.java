package de.svws_nrw.module.reporting.proxytypes.gost.klausurplanung;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.svws_nrw.core.data.gost.klausurplanung.GostKlausurraum;
import de.svws_nrw.core.data.gost.klausurplanung.GostKlausurraumstunde;
import de.svws_nrw.core.data.gost.klausurplanung.GostKlausurtermin;
import de.svws_nrw.core.data.gost.klausurplanung.GostSchuelerklausur;
import de.svws_nrw.core.data.gost.klausurplanung.GostSchuelerklausurTermin;
import de.svws_nrw.core.data.schueler.SchuelerStammdaten;
import de.svws_nrw.core.utils.gost.klausurplanung.GostKlausurplanManager;
import de.svws_nrw.data.schueler.DataSchuelerStammdaten;
import de.svws_nrw.module.reporting.proxytypes.kurs.ProxyReportingKurs;
import de.svws_nrw.module.reporting.proxytypes.schueler.ProxyReportingSchueler;
import de.svws_nrw.module.reporting.repositories.ReportingRepository;
import de.svws_nrw.module.reporting.types.gost.klausurplanung.ReportingGostKlausurplanungKlausurplan;
import de.svws_nrw.module.reporting.types.gost.klausurplanung.ReportingGostKlausurplanungKlausurraum;
import de.svws_nrw.module.reporting.types.gost.klausurplanung.ReportingGostKlausurplanungKlausurtermin;
import de.svws_nrw.module.reporting.types.gost.klausurplanung.ReportingGostKlausurplanungKursklausur;
import de.svws_nrw.module.reporting.types.gost.klausurplanung.ReportingGostKlausurplanungSchuelerklausur;
import de.svws_nrw.module.reporting.types.schueler.ReportingSchueler;


/**
 *  <p>Proxy-Klasse im Rahmen des Reportings für Daten vom Typ GostKlausurplanungKlausurplan und erweitert die Klasse
 *  {@link ReportingGostKlausurplanungKlausurplan}.</p>
 *
 *  <p>In diesem Kontext besitzt die Proxy-Klasse ausschließlich die gleichen Methoden wie die zugehörige Reporting-Super-Klasse.
 *  Während die Super-Klasse aber als reiner Datentyp konzipiert ist, d. h. ohne Anbindung an die Datenbank,
 *  greift die Proxy-Klassen an verschiedenen Stellen auf die Datenbank zu.</p>
 *
 *  <ul>
 *      <li>Die Proxy-Klasse stellt in der Regel einen zusätzlichen Constructor zur Verfügung, um Reporting-Objekte
 *  		aus Stammdatenobjekten (aus dem Package core.data) erstellen zu können. Darin werden Felder, die Reporting-Objekte
 *  		zurückgegeben und nicht im Stammdatenobjekt enthalten sind, mit null initialisiert.</li>
 * 		<li>Die Proxy-Klasse überschreibt einzelne Getter der Super-Klasse (beispielsweise bei Felder, die mit null initialisiert wurden)
 *  		und lädt dort dann aus der Datenbank die Daten bei Bedarf nach (lazy-loading), um den Umfang der Datenstrukturen gering zu
 *  		halten.</li>
 *  	<li>Die Proxy-Klasse können zudem auf das Repository {@link ReportingRepository} zugreifen. Dieses
 *  		enthält neben den Stammdaten der Schule einige Maps, in der zur jeweiligen ID bereits ausgelesene Stammdaten anderer Objekte
 *  		wie Kataloge, Jahrgänge, Klassen, Lehrer, Schüler usw. gespeichert werden. So sollen Datenbankzugriffe minimiert werden. Werden in der
 *  		Proxy-Klasse Daten nachgeladen, so werden sie dabei auch in der entsprechenden Map des Repository ergänzt.</li>
 *  </ul>
 */
public class ProxyReportingGostKlausurplanungKlausurplan extends ReportingGostKlausurplanungKlausurplan {

	/** Repository für die Reporting. */
	@JsonIgnore
	private final ReportingRepository reportingRepository;

	/** Klausurmanager des GOSt-Klausurplans. */
	@JsonIgnore
	private final GostKlausurplanManager gostKlausurplanManager;



	/**
	 * Erstellt ein neues Reporting-Objekt anhand des Abiturjahres und des Gost-Halbjahres.
	 *
	 * @param reportingRepository		Repository für die Reporting.
	 * @param gostKlausurplanManager 	Der Manager der Klausuren zu diesem Klausurplan
	 */
	public ProxyReportingGostKlausurplanungKlausurplan(final ReportingRepository reportingRepository, final GostKlausurplanManager gostKlausurplanManager) {
		super(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

		this.reportingRepository = reportingRepository;
		this.gostKlausurplanManager = gostKlausurplanManager;

		// 1. Schülerstammdaten der Schüler aus den Schülerklausuren ermitteln und in Listen und Maps einfügen.
		initSchueler();

		// 2. Kurs-Objekte anhand der Kursklausuren erzeugen.
		super.kurse.addAll(this.gostKlausurplanManager.getKursManager().kurse().stream()
				.map(k -> new ProxyReportingKurs(this.reportingRepository, k))
				.toList());

		// 3. Klausurtermine erstellen
		// HINWEIS: Termine werden ohne Klausuren erzeugt. Wenn Klausuren erzeugt werden, werden diese dem Termin zugewiesen.
		super.klausurtermine.addAll(this.gostKlausurplanManager.terminGetMengeAsList().stream()
				.map(t -> (ReportingGostKlausurplanungKlausurtermin) new ProxyReportingGostKlausurplanungKlausurtermin(t))
				.toList());

		// 4. Kursklausuren erstellen.
		// HINWEIS: Kursklausuren und Klausurtermine erhalten ihre Schülerklausuren erst bei der Erzeugung der Schülerklausuren.
		// HINWEIS: Die Klausurräume werden in einem folgenden Schritt zentral zugewiesen.
		super.kursklausuren.addAll(this.gostKlausurplanManager.kursklausurGetMengeAsList().stream()
				.map(k -> (ReportingGostKlausurplanungKursklausur) new ProxyReportingGostKlausurplanungKursklausur(
						k,
						this.gostKlausurplanManager.vorgabeByKursklausur(k),
						(this.gostKlausurplanManager.terminOrNullByKursklausur(k) == null) ? null
								: klausurtermin(this.gostKlausurplanManager.terminOrNullByKursklausur(k).id),
						kurs(this.gostKlausurplanManager.kursdatenByKursklausur(k).id)))
				.toList());

		// 5. Klausurräume mit Aufsichten (sofern schon zugeteilt) erstellen.
		initKlausurraeume();

		// 6. Schülerklausuren erstellen.
		initSchuelerklausuren();

		// 7. Sortiere alle Schülerklausuren, sowohl Gesamtliste als auch bei den Kursklausuren.
		final Collator colGerman = Collator.getInstance(Locale.GERMAN);
		super.schuelerklausuren.sort(Comparator
				.comparing((ReportingGostKlausurplanungSchuelerklausur sk) -> sk.schueler().nachname(), colGerman)
				.thenComparing(sk -> sk.schueler().vorname(), colGerman)
				.thenComparing(sk -> sk.schueler().vornamen(), colGerman)
				.thenComparing(sk -> sk.schueler().id()));
		super.kursklausuren.forEach(kk -> kk.schuelerklausuren().sort(Comparator
				.comparing((ReportingGostKlausurplanungSchuelerklausur sk) -> sk.schueler().nachname(), colGerman)
				.thenComparing(sk -> sk.schueler().vorname(), colGerman)
				.thenComparing(sk -> sk.schueler().vornamen(), colGerman)
				.thenComparing(sk -> sk.schueler().id())));
	}

	/**
	 * Initialisiert die Schüler für die später zu erstellenden Schülerklausuren.
	 */
	private void initSchueler() {
		final List<SchuelerStammdaten> schuelerStammdaten = new ArrayList<>();
		final List<Long> fehlendeSchueler = new ArrayList<>();

		for (final Long idSchueler : this.gostKlausurplanManager.schuelerklausurGetMengeAsList().stream().map(s -> s.idSchueler).distinct().toList()) {
			if (this.reportingRepository.mapSchuelerStammdaten().containsKey(idSchueler))
				schuelerStammdaten.add(this.reportingRepository.mapSchuelerStammdaten().get(idSchueler));
			else
				fehlendeSchueler.add(idSchueler);
		}
		if (!fehlendeSchueler.isEmpty()) {
			final List<SchuelerStammdaten> fehlendeSchuelerStammdaten = DataSchuelerStammdaten.getListStammdaten(this.reportingRepository.conn(),
					fehlendeSchueler);
			schuelerStammdaten.addAll(fehlendeSchuelerStammdaten);
			fehlendeSchuelerStammdaten.forEach(s -> this.reportingRepository.mapSchuelerStammdaten().putIfAbsent(s.id, s));
		}

		final Collator colGerman = Collator.getInstance(Locale.GERMAN);
		super.schueler.addAll(schuelerStammdaten.stream().map(s -> (ReportingSchueler) new ProxyReportingSchueler(this.reportingRepository, s))
				.sorted(Comparator.comparing(ReportingSchueler::nachname, colGerman)
						.thenComparing(ReportingSchueler::vorname, colGerman)
						.thenComparing(ReportingSchueler::vornamen, colGerman)
						.thenComparing(ReportingSchueler::id))
				.toList());
	}

	/**
	 * Initialisiert die Raumdaten und Unterrichtsstunden der Klausurräume. Das Ergebnis wird in den übergebenen Listen gespeichert.
	 */
	private void initKlausurraeume() {
		// Durchlaufe alle Klausurtermine und weise ihnen die ReportingKlausurräume zu, die aus den Daten erzeugt werden.
		for (final ReportingGostKlausurplanungKlausurtermin termin : super.klausurtermine) {
			// Einem Termin können mehrere Räume zugewiesen worden sein. Ermittle sie gemäß TerminID.
			final GostKlausurtermin gostKlausurtermin = this.gostKlausurplanManager.terminGetByIdOrNull(termin.id);
			if (gostKlausurtermin != null) {
				// Durchlaufe alle Räume, ermittle dabei die Klausurstunden und erzeuge damit die Klausurräume.
				for (final GostKlausurraum terminraum : this.gostKlausurplanManager.raumGetMengeByTermin(gostKlausurtermin)) {
					termin.klausurraeume().add(
							new ProxyReportingGostKlausurplanungKlausurraum(this.reportingRepository, termin, terminraum,
									this.gostKlausurplanManager.klausurraumstundeGetMengeByRaum(terminraum)));
				}
			}
		}
	}

	/**
	 * Initialsiert die Schülerklausuren mit allen Informationen (auch individuelle Raumdaten, Zeit oder Klausurdaten).
	 */
	private void initSchuelerklausuren() {

		// Listen und Maps mit Daten aus den vorherigen Schritten, um nicht erneut auf die DB zugreifen zu müssen.
		final Map<Long, ReportingGostKlausurplanungKlausurtermin> mapKlausurtermine =
				super.klausurtermine.stream().collect(Collectors.toMap(ReportingGostKlausurplanungKlausurtermin::id, t -> t));
		final Map<Long, ReportingGostKlausurplanungKursklausur> mapKursklausuren =
				super.kursklausuren.stream().collect(Collectors.toMap(ReportingGostKlausurplanungKursklausur::id, k -> k));

		// Durchlaufe nun alle Schülerklausuren und erzeuge dafür deren Termine mit Klausurräumen usw.
		for (final GostSchuelerklausur sk : gostKlausurplanManager.schuelerklausurGetMengeAsList()) {

			// Zu einer Schülerklausur kann es mehrere Schülerklausurtermine geben, die sich in ihrer FolgeNr unterscheiden (z. B. bei Nachschrieb).
			for (final GostSchuelerklausurTermin skTermin : gostKlausurplanManager.schuelerklausurterminGetMengeBySchuelerklausur(sk)) {
				// 1. Den Klausurtermin für den Schülerklausurtermin ermitteln.
				ReportingGostKlausurplanungKlausurtermin klausurtermin = null;

				// Der Termin mit FolgeNr 0 und TerminID null ist der Termin der Kursklausur.
				if ((skTermin.folgeNr == 0) && (skTermin.idTermin == null)) {
					klausurtermin = mapKursklausuren.get(gostKlausurplanManager.kursklausurBySchuelerklausur(sk).id).klausurtermin();
				} else {
					klausurtermin = (skTermin.idTermin != null) ? mapKlausurtermine.get(skTermin.idTermin) : null;
				}

				// 2. Den Klausurraum mit den Stunden zum Schülerklausurtermin ermitteln.
				ReportingGostKlausurplanungKlausurraum klausurraum = null;

				final GostKlausurraum gostKlausurraum = gostKlausurplanManager.klausurraumGetBySchuelerklausurtermin(skTermin);
				if (gostKlausurraum != null) {
					final List<GostKlausurraumstunde> gostKlausurraumstundenSchueler = gostKlausurplanManager.klausurraumstundeGetMengeByRaum(gostKlausurraum);
					if (!gostKlausurraumstundenSchueler.isEmpty()) {
						klausurraum = new ProxyReportingGostKlausurplanungKlausurraum(
								this.reportingRepository, klausurtermin, gostKlausurraum, gostKlausurraumstundenSchueler);
					}
				}

				// 3. Schülerklausur erzeugen und der Gesamtliste der Schülerklausuren hinzufügen.
				super.schuelerklausuren.add(new ProxyReportingGostKlausurplanungSchuelerklausur(sk, skTermin, klausurraum, klausurtermin,
						mapKursklausuren.get(gostKlausurplanManager.kursklausurBySchuelerklausur(sk).id), schueler(sk.idSchueler)));
			}
		}
	}


	/**
	 * Gibt das Repository mit den Daten der Schule und den zwischengespeicherten Daten zurück.
	 * @return Repository für die Reporting
	 */
	@JsonIgnore
	public ReportingRepository reportingRepository() {
		return reportingRepository;
	}

}
