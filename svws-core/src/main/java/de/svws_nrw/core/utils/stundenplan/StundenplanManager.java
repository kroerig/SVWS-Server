package de.svws_nrw.core.utils.stundenplan;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import de.svws_nrw.core.adt.map.HashMap2D;
import de.svws_nrw.core.adt.set.AVLSet;
import de.svws_nrw.core.data.stundenplan.Stundenplan;
import de.svws_nrw.core.data.stundenplan.StundenplanAufsichtsbereich;
import de.svws_nrw.core.data.stundenplan.StundenplanFach;
import de.svws_nrw.core.data.stundenplan.StundenplanJahrgang;
import de.svws_nrw.core.data.stundenplan.StundenplanKalenderwochenzuordnung;
import de.svws_nrw.core.data.stundenplan.StundenplanKlasse;
import de.svws_nrw.core.data.stundenplan.StundenplanKlassenunterricht;
import de.svws_nrw.core.data.stundenplan.StundenplanKomplett;
import de.svws_nrw.core.data.stundenplan.StundenplanKurs;
import de.svws_nrw.core.data.stundenplan.StundenplanLehrer;
import de.svws_nrw.core.data.stundenplan.StundenplanPausenaufsicht;
import de.svws_nrw.core.data.stundenplan.StundenplanPausenzeit;
import de.svws_nrw.core.data.stundenplan.StundenplanRaum;
import de.svws_nrw.core.data.stundenplan.StundenplanSchiene;
import de.svws_nrw.core.data.stundenplan.StundenplanSchueler;
import de.svws_nrw.core.data.stundenplan.StundenplanUnterricht;
import de.svws_nrw.core.data.stundenplan.StundenplanUnterrichtsverteilung;
import de.svws_nrw.core.data.stundenplan.StundenplanZeitraster;
import de.svws_nrw.core.exceptions.DeveloperNotificationException;
import de.svws_nrw.core.types.Wochentag;
import de.svws_nrw.core.utils.BlockungsUtils;
import de.svws_nrw.core.utils.CollectionUtils;
import de.svws_nrw.core.utils.DateUtils;
import de.svws_nrw.core.utils.Map2DUtils;
import de.svws_nrw.core.utils.MapUtils;
import de.svws_nrw.core.utils.StringUtils;
import jakarta.validation.constraints.NotNull;

/**
 * Ein Manager für die Daten eines Stundenplanes. Die Daten werden aus vier DTO-Objekten aggregiert.
 *
 * @author Benjamin A. Bartsch
 */
public class StundenplanManager {

	// Comparators
	private static final @NotNull Comparator<@NotNull StundenplanAufsichtsbereich> _compAufsichtsbereich = (final @NotNull StundenplanAufsichtsbereich a, final @NotNull StundenplanAufsichtsbereich b) -> {
		final int result = a.kuerzel.compareTo(b.kuerzel);
		if (result != 0) return result;
		return Long.compare(a.id, b.id);
	};
	private static final @NotNull Comparator<@NotNull StundenplanFach> _compFach = (final @NotNull StundenplanFach a, final @NotNull StundenplanFach b) -> {
		final int result = a.kuerzel.compareTo(b.kuerzel);
		if (result != 0) return result;
		return Long.compare(a.id, b.id);
	};
	private static final @NotNull Comparator<@NotNull StundenplanJahrgang> _compJahrgang = (final @NotNull StundenplanJahrgang a, final @NotNull StundenplanJahrgang b) -> {
		final int result = a.kuerzel.compareTo(b.kuerzel);
		if (result != 0) return result;
		return Long.compare(a.id, b.id);
	};
	private static final @NotNull Comparator<@NotNull StundenplanKalenderwochenzuordnung> _compKWZ = (final @NotNull StundenplanKalenderwochenzuordnung a, final @NotNull StundenplanKalenderwochenzuordnung b) -> {
		if (a.jahr < b.jahr) return -1;
		if (a.jahr > b.jahr) return +1;
		if (a.kw < b.kw) return -1;
		if (a.kw > b.kw) return +1;
		if (a.wochentyp < b.wochentyp) return -1;
		if (a.wochentyp > b.wochentyp) return +1;
		return Long.compare(a.id, b.id);
	};
	private static final @NotNull Comparator<@NotNull StundenplanKlasse> _compKlasse = (final @NotNull StundenplanKlasse a, final @NotNull StundenplanKlasse b) -> {
		final int result = a.kuerzel.compareTo(b.kuerzel);
		if (result != 0) return result;
		return Long.compare(a.id, b.id);
	};
	private static final @NotNull Comparator<@NotNull StundenplanKlassenunterricht> _compKlassenunterricht = (final @NotNull StundenplanKlassenunterricht a, final @NotNull StundenplanKlassenunterricht b) -> {
		if (a.idKlasse < b.idKlasse) return -1;
		if (a.idKlasse > b.idKlasse) return +1;
		if (a.idFach < b.idFach) return -1;
		if (a.idFach > b.idFach) return +1;
		if (a.wochenstunden < b.wochenstunden) return -1;
		if (a.wochenstunden > b.wochenstunden) return +1;
		return a.bezeichnung.compareTo(b.bezeichnung);
	};
	private static final @NotNull Comparator<@NotNull StundenplanKurs> _compKurs = (final @NotNull StundenplanKurs a, final @NotNull StundenplanKurs b) -> Long.compare(a.id, b.id);
	private static final @NotNull Comparator<@NotNull StundenplanLehrer> _compLehrer = (final @NotNull StundenplanLehrer a, final @NotNull StundenplanLehrer b) -> {
		final int result = a.kuerzel.compareTo(b.kuerzel);
		if (result != 0) return result;
		return Long.compare(a.id, b.id);
	};
	private static final @NotNull Comparator<@NotNull StundenplanPausenaufsicht> _compPausenaufsicht = (final @NotNull StundenplanPausenaufsicht a, final @NotNull StundenplanPausenaufsicht b) -> Long.compare(a.id, b.id);
	private static final @NotNull Comparator<@NotNull StundenplanPausenzeit> _compPausenzeit = (final @NotNull StundenplanPausenzeit a, final @NotNull StundenplanPausenzeit b) -> {
		if (a.wochentag < b.wochentag) return -1;
		if (a.wochentag > b.wochentag) return +1;
		final int beginnA = a.beginn == null ? -1 : a.beginn;
		final int beginnB = b.beginn == null ? -1 : b.beginn;
		if (beginnA < beginnB) return -1;
		if (beginnA > beginnB) return +1;
		return Long.compare(a.id, b.id);
	};
	private static final @NotNull Comparator<@NotNull StundenplanRaum> _compRaum = (final @NotNull StundenplanRaum a, final @NotNull StundenplanRaum b) -> {
		final int result = a.kuerzel.compareTo(b.kuerzel);
		if (result != 0) return result;
		return Long.compare(a.id, b.id);
	};
	private static final @NotNull Comparator<@NotNull StundenplanSchiene> _compSchiene = (final @NotNull StundenplanSchiene a, final @NotNull StundenplanSchiene b) -> {
		if (a.idJahrgang < b.idJahrgang) return -1;
		if (a.idJahrgang > b.idJahrgang) return +1;
		if (a.nummer < b.nummer) return -1;
		if (a.nummer > b.nummer) return +1;
		return Long.compare(a.id, b.id);
	};
	private static final @NotNull Comparator<@NotNull StundenplanSchueler> _compSchueler = (final @NotNull StundenplanSchueler a, final @NotNull StundenplanSchueler b) -> {
		if (a.idKlasse < b.idKlasse) return -1;
		if (a.idKlasse > b.idKlasse) return +1;
		final int cmpNachname = a.nachname.compareTo(b.nachname);
		if (cmpNachname != 0) return cmpNachname;
		final int cmpVorname = a.vorname.compareTo(b.vorname);
		if (cmpVorname != 0) return cmpVorname;
		return Long.compare(a.id, b.id);
	};
	private static final @NotNull Comparator<@NotNull StundenplanUnterricht> _compUnterricht = (final @NotNull StundenplanUnterricht a, final @NotNull StundenplanUnterricht b) -> Long.compare(a.id, b.id);
	private static final @NotNull Comparator<@NotNull StundenplanZeitraster> _compZeitraster = (final @NotNull StundenplanZeitraster a, final @NotNull StundenplanZeitraster b) -> {
		if (a.wochentag < b.wochentag) return -1;
		if (a.wochentag > b.wochentag) return +1;
		if (a.unterrichtstunde < b.unterrichtstunde) return -1;
		if (a.unterrichtstunde > b.unterrichtstunde) return +1;
		return Long.compare(a.id, b.id);
	};

	// StundenplanAufsichtsbereich
	private final @NotNull HashMap<@NotNull Long, @NotNull StundenplanAufsichtsbereich> _aufsichtsbereich_by_id = new HashMap<>();
	private final @NotNull List<@NotNull StundenplanAufsichtsbereich> _aufsichtsbereichmenge = new ArrayList<>();

	// StundenplanFach
	private final @NotNull HashMap<@NotNull Long, @NotNull StundenplanFach> _fach_by_id = new HashMap<>();
	private final @NotNull List<@NotNull StundenplanFach> _fachmenge = new ArrayList<>();

	// StundenplanJahrgang
	private final @NotNull HashMap<@NotNull Long, @NotNull StundenplanJahrgang> _jahrgang_by_id = new HashMap<>();
	private final @NotNull List<@NotNull StundenplanJahrgang> _jahrgangmenge = new ArrayList<>();

	// StundenplanKalenderwochenzuordnung
	private final @NotNull HashMap<@NotNull Long, @NotNull StundenplanKalenderwochenzuordnung> _kwz_by_id = new HashMap<>();
	private final @NotNull List<@NotNull StundenplanKalenderwochenzuordnung> _kwzmenge = new ArrayList<>();
	private final @NotNull HashMap2D<@NotNull Integer, @NotNull Integer, @NotNull StundenplanKalenderwochenzuordnung> _kwz_by_jahr_and_kw = new HashMap2D<>();

	// StundenplanKlasse
	private final @NotNull HashMap<@NotNull Long, @NotNull StundenplanKlasse> _klasse_by_id = new HashMap<>();
	private final @NotNull List<@NotNull StundenplanKlasse> _klassenmenge = new ArrayList<>();

	// StundenplanKlassenunterricht
	private final @NotNull HashMap2D<@NotNull Long, @NotNull Long, @NotNull StundenplanKlassenunterricht> _klassenunterricht_by_idKlasse_and_idFach = new HashMap2D<>();
	private final @NotNull List<@NotNull StundenplanKlassenunterricht> _klassenunterrichtmenge = new ArrayList<>();
	private final @NotNull HashMap<@NotNull Long, @NotNull List<@NotNull StundenplanKlassenunterricht>> _klassenunterrichtmenge_by_idKlasse = new HashMap<>();
	private final @NotNull HashMap<@NotNull Long, @NotNull List<@NotNull StundenplanKlassenunterricht>> _klassenunterrichtmenge_by_idSchueler = new HashMap<>();
	private final @NotNull HashMap<@NotNull Long, @NotNull List<@NotNull StundenplanKlassenunterricht>> _klassenunterrichtmenge_by_idLehrer = new HashMap<>();

	// StundenplanKurs
	private final @NotNull HashMap<@NotNull Long, @NotNull StundenplanKurs> _kurs_by_id = new HashMap<>();
	private final @NotNull List<@NotNull StundenplanKurs> _kursmenge = new ArrayList<>();
	private final @NotNull HashMap<@NotNull Long, @NotNull List<@NotNull StundenplanKurs>> _kursmenge_by_idSchueler = new HashMap<>();
	private final @NotNull HashMap<@NotNull Long, @NotNull List<@NotNull StundenplanKurs>> _kursmenge_by_idLehrer = new HashMap<>();
	private final @NotNull HashMap<@NotNull Long, @NotNull List<@NotNull StundenplanKurs>> _kursmenge_by_idKlasse = new HashMap<>();

	// StundenplanLehrer
	private final @NotNull HashMap<@NotNull Long, @NotNull StundenplanLehrer> _lehrer_by_id = new HashMap<>();
	private final @NotNull List<@NotNull StundenplanLehrer> _lehrermenge = new ArrayList<>();
	private final @NotNull HashMap<@NotNull Long, @NotNull List<@NotNull StundenplanLehrer>> _lehrermenge_by_idUnterricht = new HashMap<>();

	// StundenplanPausenaufsicht
	private final @NotNull HashMap<@NotNull Long, @NotNull StundenplanPausenaufsicht> _pausenaufsicht_by_id = new HashMap<>();
	private final @NotNull List<@NotNull StundenplanPausenaufsicht> _pausenaufsichtmenge = new ArrayList<>();
	private final @NotNull HashMap<@NotNull Integer, @NotNull List<@NotNull StundenplanPausenaufsicht>> _pausenaufsichtmenge_by_wochentag = new HashMap<>();
	private final @NotNull HashMap<@NotNull Long, @NotNull List<@NotNull StundenplanPausenaufsicht>> _pausenaufsichtmenge_by_idPausenzeit = new HashMap<>();

	// StundenplanPausenzeit
	private final @NotNull HashMap<@NotNull Long, @NotNull StundenplanPausenzeit> _pausenzeit_by_id = new HashMap<>();
	private final @NotNull List<@NotNull StundenplanPausenzeit> _pausenzeitmenge = new ArrayList<>();
	private final @NotNull HashMap<@NotNull Integer, @NotNull List<@NotNull StundenplanPausenzeit>> _pausenzeitmenge_by_wochentag = new HashMap<>();
	private final @NotNull List<@NotNull StundenplanPausenzeit> _pausenzeitmengeOhneLeere = new ArrayList<>();
	private Integer _pausenzeitMinutenMin = null;
	private Integer _pausenzeitMinutenMax = null;
	private Integer _pausenzeitMinutenMinOhneLeere = null;
	private Integer _pausenzeitMinutenMaxOhneLeere = null;

	// StundenplanRaum
	private final @NotNull HashMap<@NotNull Long, @NotNull StundenplanRaum> _raum_by_id = new HashMap<>();
	private final @NotNull List<@NotNull StundenplanRaum> _raummenge = new ArrayList<>();

	// StundenplanSchiene
	private final @NotNull HashMap<@NotNull Long, @NotNull StundenplanSchiene> _schiene_by_id = new HashMap<>();
	private final @NotNull List<@NotNull StundenplanSchiene> _schienenmenge = new ArrayList<>();

	// StundenplanSchueler
	private final @NotNull HashMap<@NotNull Long, @NotNull StundenplanSchueler> _schueler_by_id = new HashMap<>();
	private final @NotNull List<@NotNull StundenplanSchueler> _schuelermenge = new ArrayList<>();
	private final @NotNull HashMap<@NotNull Long, @NotNull List<@NotNull StundenplanSchueler>> schuelermenge_by_idKlasse = new HashMap<>();
	private final @NotNull HashMap<@NotNull Long, @NotNull List<@NotNull StundenplanSchueler>> _schuelermenge_by_idKurs = new HashMap<>();

	// StundenplanUnterricht
	private final @NotNull HashMap<@NotNull Long, @NotNull StundenplanUnterricht> _unterricht_by_id = new HashMap<>();
	private final @NotNull List<@NotNull StundenplanUnterricht> _unterrichtmenge = new ArrayList<>();
	private final @NotNull HashMap<@NotNull Long, @NotNull List<@NotNull StundenplanUnterricht>> _unterrichtmenge_by_idKlasse = new HashMap<>();
	private final @NotNull HashMap2D<@NotNull Long, @NotNull Long, @NotNull List<@NotNull StundenplanUnterricht>> _unterrichtmenge_by_idKlasse_and_idZeitraster = new HashMap2D<>();
	private final @NotNull HashMap<@NotNull Long, @NotNull List<@NotNull StundenplanUnterricht>> _unterrichtmenge_by_idKurs = new HashMap<>();
	private final @NotNull HashMap<@NotNull Long, @NotNull List<@NotNull StundenplanUnterricht>> _unterrichtmenge_by_idZeitraster = new HashMap<>();
	private final @NotNull HashMap2D<@NotNull Long, @NotNull Integer, @NotNull List<@NotNull StundenplanUnterricht>> _unterrichtmenge_by_idZeitraster_and_wochentyp = new HashMap2D<>();
	private final @NotNull HashMap2D<@NotNull Long, @NotNull Long, @NotNull List<@NotNull StundenplanUnterricht>> _unterrichtmenge_by_idKlasse_and_idFach = new HashMap2D<>();
	private boolean _unterrichtHatMultiWochen = false;

	// StundenplanZeitraster
	private final @NotNull HashMap<@NotNull Long, @NotNull StundenplanZeitraster> _zeitraster_by_id = new HashMap<>();
	private final @NotNull HashMap2D<@NotNull Integer, @NotNull Integer, @NotNull StundenplanZeitraster> _zeitraster_by_wochentag_and_stunde = new HashMap2D<>();
	private final @NotNull List<@NotNull StundenplanZeitraster> _zeitrastermenge = new ArrayList<>();
	private final @NotNull List<@NotNull StundenplanZeitraster> _zeitrastermengeOhneLeere = new ArrayList<>();
	private final @NotNull HashMap<@NotNull Integer, @NotNull List<@NotNull StundenplanZeitraster>> _zeitrastermenge_by_wochentag = new HashMap<>();
	private final @NotNull HashMap<@NotNull Integer, @NotNull List<@NotNull StundenplanZeitraster>> _zeitrastermenge_by_stunde = new HashMap<>();
	private Integer _zeitrasterMinutenMin = null;
	private Integer _zeitrasterMinutenMax = null;
	private Integer _zeitrasterMinutenMinOhneLeere = null;
	private Integer _zeitrasterMinutenMaxOhneLeere = null;
	private final @NotNull HashMap<@NotNull Integer, Integer> _zeitrasterMinutenMinByStunde = new HashMap<>();
	private final @NotNull HashMap<@NotNull Integer, Integer> _zeitrasterMinutenMaxByStunde = new HashMap<>();

	private int _zeitrasterWochentagMin = Wochentag.MONTAG.id;
	private int _zeitrasterWochentagMax = Wochentag.MONTAG.id;
	private @NotNull Wochentag @NotNull [] _zeitrasterWochentageAlsEnumRange = new Wochentag[] {Wochentag.MONTAG};

	private int _zeitrasterStundeMin = 1;
	private int _zeitrasterStundeMax = 1;
	private @NotNull int @NotNull [] _zeitrasterStundenRange = new int[] {1};

	private int _zeitrasterStundeMinOhneLeere = 1;
	private int _zeitrasterStundeMaxOhneLeere = 1;
	private @NotNull int @NotNull [] _zeitrasterStundenRangeOhneLeere = new int[] {1};

	// Stundenplan
	private final long _stundenplanID;
	private final int _stundenplanWochenTypModell;
	private final long _stundenplanSchuljahresAbschnittID;
	private final @NotNull String _stundenplanGueltigAb;
	private final @NotNull String _stundenplanGueltigBis;
	private final @NotNull String _stundenplanBezeichnung;

	/**
	 * Der {@link StundenplanManager} benötigt vier data-Objekte und baut damit eine Datenstruktur für schnelle Zugriffe auf.
	 *
	 * @param daten                 liefert die Grund-Daten des Stundenplanes.
	 * @param unterrichte           liefert die Informationen zu allen {@link StundenplanUnterricht} im Stundenplan. Die Liste darf leer sein.
	 * @param pausenaufsichten      liefert die Informationen zu allen {@link StundenplanPausenaufsicht} im Stundenplan. Die Liste darf leer sein.
	 * @param unterrichtsverteilung liefert die Informationen zu der Unterrichtsverteilung eines Stundenplans. Darf NULL sein.
	 */
	public StundenplanManager(final @NotNull Stundenplan daten, final @NotNull List<@NotNull StundenplanUnterricht> unterrichte, final @NotNull List<@NotNull StundenplanPausenaufsicht> pausenaufsichten, final StundenplanUnterrichtsverteilung unterrichtsverteilung) {
		_stundenplanID = daten.id;
		_stundenplanWochenTypModell = daten.wochenTypModell;
		_stundenplanSchuljahresAbschnittID = daten.idSchuljahresabschnitt;
		_stundenplanGueltigAb = daten.gueltigAb;
		_stundenplanGueltigBis = daten.gueltigBis;
		_stundenplanBezeichnung = daten.bezeichnungStundenplan;

		// Spezialfall: "unterrichtsverteilung" ist NULL
		StundenplanUnterrichtsverteilung uv = unterrichtsverteilung;
		if (uv == null) {
			uv = new StundenplanUnterrichtsverteilung();
			uv.id = _stundenplanID;
		}

		// Spezielle Prüfungen.
		DeveloperNotificationException.ifTrue("Stundenplan.id != StundenplanUnterrichtsverteilung.id", daten.id != uv.id);

		// Initialisierungen der Maps und Prüfung der Integrität.
		initAll(daten.kalenderwochenZuordnung,
				uv.faecher,
				daten.jahrgaenge,
				daten.zeitraster,
				daten.raeume,
				daten.pausenzeiten,
				daten.aufsichtsbereiche,
				uv.lehrer,
				uv.schueler,
				daten.schienen,
				uv.klassen,
				uv.klassenunterricht,
				pausenaufsichten,
				uv.kurse,
				unterrichte
				);

	}

	/**
	 * Dieser Manager baut mit Hilfe des {@link StundenplanKomplett}-Objektes eine Datenstruktur für schnelle Zugriffe auf.
	 *
	 * @param stundenplanKomplett  Beinhaltet alle relevanten Daten für einen Stundenplan.
	 */
	public StundenplanManager(final @NotNull StundenplanKomplett stundenplanKomplett) {
		_stundenplanID = stundenplanKomplett.daten.id;
		_stundenplanWochenTypModell = stundenplanKomplett.daten.wochenTypModell;
		_stundenplanSchuljahresAbschnittID = stundenplanKomplett.daten.idSchuljahresabschnitt;
		_stundenplanGueltigAb = stundenplanKomplett.daten.gueltigAb;
		_stundenplanGueltigBis = stundenplanKomplett.daten.gueltigBis;
		_stundenplanBezeichnung = stundenplanKomplett.daten.bezeichnungStundenplan;

		// Spezielle Prüfungen.
		DeveloperNotificationException.ifTrue("Stundenplan.id != StundenplanUnterrichtsverteilung.id", stundenplanKomplett.daten.id != stundenplanKomplett.unterrichtsverteilung.id);

		initAll(stundenplanKomplett.daten.kalenderwochenZuordnung,
				stundenplanKomplett.unterrichtsverteilung.faecher,
				stundenplanKomplett.daten.jahrgaenge,
				stundenplanKomplett.daten.zeitraster,
				stundenplanKomplett.daten.raeume,
				stundenplanKomplett.daten.pausenzeiten,
				stundenplanKomplett.daten.aufsichtsbereiche,
				stundenplanKomplett.unterrichtsverteilung.lehrer,
				stundenplanKomplett.unterrichtsverteilung.schueler,
				stundenplanKomplett.daten.schienen,
				stundenplanKomplett.unterrichtsverteilung.klassen,
				stundenplanKomplett.unterrichtsverteilung.klassenunterricht,
				stundenplanKomplett.pausenaufsichten,
				stundenplanKomplett.unterrichtsverteilung.kurse,
				stundenplanKomplett.unterrichte);
	}

	private void initAll(final @NotNull List<@NotNull StundenplanKalenderwochenzuordnung> listKWZ,
			             final @NotNull List<@NotNull StundenplanFach> listFach,
			             final @NotNull List<@NotNull StundenplanJahrgang> listJahrgang,
			             final @NotNull List<@NotNull StundenplanZeitraster> listZeitraster,
			             final @NotNull List<@NotNull StundenplanRaum> listRaum,
			             final @NotNull List<@NotNull StundenplanPausenzeit> listPausenzeit,
			             final @NotNull List<@NotNull StundenplanAufsichtsbereich> listAufsichtsbereich,
			             final @NotNull List<@NotNull StundenplanLehrer> listLehrer,
			             final @NotNull List<@NotNull StundenplanSchueler> listSchueler,
			             final @NotNull List<@NotNull StundenplanSchiene> listSchiene,
			             final @NotNull List<@NotNull StundenplanKlasse> listKlasse,
			             final @NotNull List<@NotNull StundenplanKlassenunterricht> listKlassenunterricht,
			             final @NotNull List<@NotNull StundenplanPausenaufsicht> listPausenaufsicht,
			             final @NotNull List<@NotNull StundenplanKurs> listKurs,
			             final @NotNull List<@NotNull StundenplanUnterricht> listUnterricht) {

		DeveloperNotificationException.ifTrue("stundenplanWochenTypModell < 0", _stundenplanWochenTypModell < 0);
		DeveloperNotificationException.ifTrue("stundenplanWochenTypModell == 1", _stundenplanWochenTypModell == 1);

		kalenderwochenzuordnungAddAll(listKWZ);          // ✔, referenziert ---
		fachAddAll(listFach);                            // ✔, referenziert ---
		jahrgangAddAll(listJahrgang);                    // ✔, referenziert ---
		zeitrasterAddAll(listZeitraster);                // ✔, referenziert ---
		raumAddAll(listRaum);                            // ✔, referenziert ---
		pausenzeitAddAll(listPausenzeit);                // ✔, referenziert ---
		aufsichtsbereichAddAll(listAufsichtsbereich);    // ✔, referenziert ---
		lehrerAddAll(listLehrer);                        // ✔, referenziert [Fach]
		schuelerAddAll(listSchueler);                    // ✔, referenziert Klasse
		klasseAddAll(listKlasse);                        // ✔, referenziert [Jahrgang], [Schueler]
		schieneAddAll(listSchiene);                      // ✔, referenziert Jahrgang
		klassenunterrichtAddAll(listKlassenunterricht);  // ✔, referenziert Klasse, [Jahrgang], [Schienen]
		pausenaufsichtAddAll(listPausenaufsicht);        // ✔, referenziert Lehrer, Pausenzeit, [Aufsichtsbereich]
		kursAddAll(listKurs);                            // ✔, referenziert [Schienen], [Jahrgang], [Schüler]
		unterrichtAddAll(listUnterricht);                // ✔, referenziert Zeitraster, Kurs, Fach, [Lehrer], [Klasse], [Raum], [Schiene]
	}

	private void update_aufsichtsbereichmenge() {
		_aufsichtsbereichmenge.clear();
		_aufsichtsbereichmenge.addAll(_aufsichtsbereich_by_id.values());
		_aufsichtsbereichmenge.sort(_compAufsichtsbereich);
	}

	private void update_fachmenge() {
		_fachmenge.clear();
		_fachmenge.addAll(_fach_by_id.values());
		_fachmenge.sort(_compFach);
	}

	private void update_jahrgangmenge() {
		_jahrgangmenge.clear();
		_jahrgangmenge.addAll(_jahrgang_by_id.values());
		_jahrgangmenge.sort(_compJahrgang);
	}

	private void update_kwzmenge_update_kwz_by_jahr_and_kw() {
		// _list_kwz
		_kwzmenge.clear();
		_kwzmenge.addAll(_kwz_by_id.values());

		// _map2d_jahr_kw_zu_kwz (Original - Objekte)
		_kwz_by_jahr_and_kw.clear();
		for (final @NotNull StundenplanKalenderwochenzuordnung kwz : _kwzmenge)
			DeveloperNotificationException.ifMap2DPutOverwrites(_kwz_by_jahr_and_kw, kwz.jahr, kwz.kw, kwz);

		// _map2d_jahr_kw_zu_kwz (Pseudo - Objekte)
		final @NotNull int[] infoVon = DateUtils.extractFromDateISO8601(_stundenplanGueltigAb);
		final @NotNull int[] infoBis = DateUtils.extractFromDateISO8601(_stundenplanGueltigBis);
		final int jahrVon = infoVon[6]; // 6 = kalenderwochenjahr
		final int jahrBis = infoBis[6]; // 6 = kalenderwochenjahr
		final int kwVon = infoVon[5]; // 5 = kalenderwoche
		final int kwBis = infoBis[5]; // 5 = kalenderwoche
		DeveloperNotificationException.ifTrue("jahrVon > jahrBis", jahrVon > jahrBis);
		DeveloperNotificationException.ifTrue("(jahrVon == jahrBis) && (kwVon > kwBis)", (jahrVon == jahrBis) && (kwVon > kwBis));

		for (int jahr = jahrVon; jahr <= jahrBis; jahr++) {
			final int von = (jahr == jahrVon) ? kwVon : 1;
			final int bis = (jahr == jahrBis) ? kwBis : DateUtils.gibKalenderwochenOfJahr(jahr);
			for (int kw = von; kw <= bis; kw++)
				if (!_kwz_by_jahr_and_kw.contains(jahr, kw)) { // Überschreibe Original - Objekte der DB nicht!
					final @NotNull StundenplanKalenderwochenzuordnung kwz = new StundenplanKalenderwochenzuordnung();
					kwz.id = -1;
					kwz.jahr = jahr;
					kwz.kw = kw;
					kwz.wochentyp = kalenderwochenzuordnungGetWochentypOrDefault(jahr, kw);
					// Hinzufügen
					DeveloperNotificationException.ifMap2DPutOverwrites(_kwz_by_jahr_and_kw, kwz.jahr, kwz.kw, kwz);
					_kwzmenge.add(kwz);
				}
		}

		_kwzmenge.sort(_compKWZ);
	}

	private void update_klassenmenge() {
		_klassenmenge.clear();
		_klassenmenge.addAll(_klasse_by_id.values());
		_klassenmenge.sort(_compKlasse);
	}

	private void update_klassenunterrichtmenge() {
		_klassenunterrichtmenge.clear();
		_klassenunterrichtmenge.addAll(_klassenunterricht_by_idKlasse_and_idFach.getNonNullValuesAsList());
		_klassenunterrichtmenge.sort(_compKlassenunterricht);
	}

	private void update_klassenunterrichtmenge_by_idKlasse() {
		_klassenunterrichtmenge_by_idKlasse.clear();
		for (final @NotNull StundenplanKlassenunterricht klassenunterricht : _klassenunterricht_by_idKlasse_and_idFach.getNonNullValuesAsList())
			MapUtils.getOrCreateArrayList(_klassenunterrichtmenge_by_idKlasse, klassenunterricht.idKlasse).add(klassenunterricht);

		for (final @NotNull StundenplanKlasse klasse : _klasse_by_id.values())
			MapUtils.getOrCreateArrayList(_klassenunterrichtmenge_by_idKlasse, klasse.id).sort(_compKlassenunterricht);
	}

	private void update_klassenunterrichtmenge_by_idSchueler() {
		_klassenunterrichtmenge_by_idSchueler.clear();
		for (final @NotNull StundenplanKlassenunterricht klassenunterricht : _klassenunterricht_by_idKlasse_and_idFach.getNonNullValuesAsList())
			for (final @NotNull Long idSchueler : klassenunterricht.schueler)
				MapUtils.getOrCreateArrayList(_klassenunterrichtmenge_by_idSchueler, idSchueler).add(klassenunterricht);

		for (final @NotNull StundenplanSchueler schueler: _schueler_by_id.values())
			MapUtils.getOrCreateArrayList(_klassenunterrichtmenge_by_idSchueler, schueler.id).sort(_compKlassenunterricht);
	}

	private void update_klassenunterrichtmenge_by_idLehrer() {
		_klassenunterrichtmenge_by_idLehrer.clear();
		for (final @NotNull StundenplanKlassenunterricht klassenunterricht : _klassenunterricht_by_idKlasse_and_idFach.getNonNullValuesAsList())
			for (final @NotNull Long idLehrer : klassenunterricht.lehrer)
				MapUtils.getOrCreateArrayList(_klassenunterrichtmenge_by_idLehrer, idLehrer).add(klassenunterricht);

		for (final @NotNull StundenplanLehrer lehrer: _lehrer_by_id.values())
			MapUtils.getOrCreateArrayList(_klassenunterrichtmenge_by_idLehrer, lehrer.id).sort(_compKlassenunterricht);
	}

	private void update_kursmenge() {
		_kursmenge.clear();
		_kursmenge.addAll(_kurs_by_id.values());
		_kursmenge.sort(_compKurs);
	}

	private void update_kursmenge_by_idSchueler() {
		_kursmenge_by_idSchueler.clear();
		for (final @NotNull StundenplanKurs kurs : _kurs_by_id.values())
			for (final @NotNull Long idSchueler : kurs.schueler)
				MapUtils.getOrCreateArrayList(_kursmenge_by_idSchueler, idSchueler).add(kurs);

		for (final @NotNull StundenplanSchueler schueler : _schueler_by_id.values())
			MapUtils.getOrCreateArrayList(_kursmenge_by_idSchueler, schueler.id).sort(_compKurs);
	}

	private void update_kursmenge_by_idLehrer() {
		_kursmenge_by_idLehrer.clear();
		for (final @NotNull StundenplanKurs kurs : _kurs_by_id.values())
			for (final @NotNull Long idLehrer : kurs.lehrer)
				MapUtils.getOrCreateArrayList(_kursmenge_by_idLehrer, idLehrer).add(kurs);

		for (final @NotNull StundenplanLehrer lehrer : _lehrer_by_id.values())
			MapUtils.getOrCreateArrayList(_kursmenge_by_idLehrer, lehrer.id).sort(_compKurs);
	}

	private void update_kursmenge_by_idKlasse() {
		_kursmenge_by_idKlasse.clear();
		for (final @NotNull StundenplanKurs kurs : _kurs_by_id.values())
			for (final @NotNull Long idSchueler : kurs.schueler) {
				final @NotNull StundenplanSchueler schueler = DeveloperNotificationException.ifMapGetIsNull(_schueler_by_id, idSchueler);
				// Die Kurse der Klasse, aggregiert über die Klasse des Schülers.
				if ((schueler.idKlasse > 0) && (!MapUtils.getOrCreateArrayList(_kursmenge_by_idKlasse, schueler.idKlasse).contains(kurs)))
					MapUtils.getOrCreateArrayList(_kursmenge_by_idKlasse, schueler.idKlasse).add(kurs);
			}

		for (final @NotNull StundenplanKlasse klasse : _klasse_by_id.values())
			MapUtils.getOrCreateArrayList(_kursmenge_by_idKlasse, klasse.id).sort(_compKurs);
	}

	private void update_lehrermenge() {
		_lehrermenge.clear();
		_lehrermenge.addAll(_lehrer_by_id.values());
		_lehrermenge.sort(_compLehrer);
	}

	private void update_lehrermenge_by_idUnterricht() {
		_lehrermenge_by_idUnterricht.clear();
		for (final @NotNull StundenplanUnterricht u : _unterricht_by_id.values()) {
			for (final @NotNull Long idLehrer : u.lehrer) {
				final @NotNull StundenplanLehrer lehrer = DeveloperNotificationException.ifMapGetIsNull(_lehrer_by_id, idLehrer);
				MapUtils.getOrCreateArrayList(_lehrermenge_by_idUnterricht, u.id).add(lehrer);
			}
			MapUtils.getOrCreateArrayList(_lehrermenge_by_idUnterricht, u.id).sort(_compLehrer);
		}
	}

	private void update_pausenaufsichtmenge() {
		_pausenaufsichtmenge.clear();
		_pausenaufsichtmenge.addAll(_pausenaufsicht_by_id.values());
		_pausenaufsichtmenge.sort(_compPausenaufsicht);
	}

	private void update_pausenaufsichtmenge_by_wochentag() {
		_pausenaufsichtmenge_by_wochentag.clear();
		for (final @NotNull StundenplanPausenaufsicht a : _pausenaufsicht_by_id.values()) {
			final @NotNull StundenplanPausenzeit p = DeveloperNotificationException.ifMapGetIsNull(_pausenzeit_by_id, a.idPausenzeit);
			MapUtils.getOrCreateArrayList(_pausenaufsichtmenge_by_wochentag, p.wochentag).add(a);
		}

		for (final @NotNull Wochentag wochentag : Wochentag.values())
			MapUtils.getOrCreateArrayList(_pausenaufsichtmenge_by_wochentag, wochentag.id).sort(_compPausenaufsicht);
	}

	private void update_pausenaufsichtmenge_by_idPausenzeit() {
		_pausenaufsichtmenge_by_idPausenzeit.clear();
		for (final @NotNull StundenplanPausenaufsicht a : _pausenaufsicht_by_id.values())
			MapUtils.getOrCreateArrayList(_pausenaufsichtmenge_by_idPausenzeit, a.idPausenzeit).add(a);

		for (final @NotNull StundenplanPausenzeit z : _pausenzeit_by_id.values())
			MapUtils.getOrCreateArrayList(_pausenaufsichtmenge_by_idPausenzeit, z.id).sort(_compPausenaufsicht);
	}

	private void update_pausenzeitmenge() {
		_pausenzeitmenge.clear();
		_pausenzeitmenge.addAll(_pausenzeit_by_id.values());
		_pausenzeitmenge.sort(_compPausenzeit);

		_pausenzeitMinutenMin = null;
		_pausenzeitMinutenMax = null;
		for (final @NotNull StundenplanPausenzeit p : _pausenzeitmenge) {
			_pausenzeitMinutenMin = BlockungsUtils.minII(_pausenzeitMinutenMin, p.beginn);
			_pausenzeitMinutenMax = BlockungsUtils.maxII(_pausenzeitMinutenMax, p.ende);
		}
	}

	private void update_pausenzeitmengeOhnePausenaufsicht() {
		_pausenzeitmengeOhneLeere.clear();
		for (final @NotNull StundenplanPausenzeit z : _pausenzeit_by_id.values())
			if (!DeveloperNotificationException.ifMapGetIsNull(_pausenaufsichtmenge_by_idPausenzeit, z.id).isEmpty())
				_pausenzeitmengeOhneLeere.add(z);
		_pausenzeitmengeOhneLeere.sort(_compPausenzeit);

		_pausenzeitMinutenMinOhneLeere = null;
		_pausenzeitMinutenMaxOhneLeere = null;
		for (final @NotNull StundenplanPausenzeit p : _pausenzeitmengeOhneLeere) {
			_pausenzeitMinutenMinOhneLeere = BlockungsUtils.minII(_pausenzeitMinutenMinOhneLeere, p.beginn);
			_pausenzeitMinutenMaxOhneLeere = BlockungsUtils.maxII(_pausenzeitMinutenMaxOhneLeere, p.ende);
		}
	}

	private void update_pausenzeitmenge_by_wochentag() {
		_pausenzeitmenge_by_wochentag.clear();
		for (final @NotNull StundenplanPausenzeit z : _pausenzeit_by_id.values())
			MapUtils.getOrCreateArrayList(_pausenzeitmenge_by_wochentag, z.wochentag).add(z);

		for (final @NotNull Wochentag wochentag : Wochentag.values()) // Sortierung hier wichtig!
			MapUtils.getOrCreateArrayList(_pausenzeitmenge_by_wochentag, wochentag.id).sort(_compPausenzeit);
	}

	private void update_raummenge() {
		_raummenge.clear();
		_raummenge.addAll(_raum_by_id.values());
		_raummenge.sort(_compRaum);
	}

	private void update_schienenmenge() {
		_schienenmenge.clear();
		_schienenmenge.addAll(_schiene_by_id.values());
		_schienenmenge.sort(_compSchiene);
	}

	private void update_schuelermenge() {
		_schuelermenge.clear();
		_schuelermenge.addAll(_schueler_by_id.values());
		_schuelermenge.sort(_compSchueler);
	}

	private void update_schuelermenge_by_idKlasse() {
		schuelermenge_by_idKlasse.clear();
		for (final @NotNull StundenplanKlasse klasse : _klasse_by_id.values()) {
			for (final @NotNull Long idSchueler : klasse.schueler) {
				final @NotNull StundenplanSchueler schueler = DeveloperNotificationException.ifMapGetIsNull(_schueler_by_id, idSchueler);
				MapUtils.getOrCreateArrayList(schuelermenge_by_idKlasse, klasse.id).add(schueler);
			}
			MapUtils.getOrCreateArrayList(schuelermenge_by_idKlasse, klasse.id).sort(_compSchueler);
		}
	}

	private void update_schuelermenge_by_idKurs() {
		_schuelermenge_by_idKurs.clear();
		for (final @NotNull StundenplanKurs kurs : _kurs_by_id.values()) {
			for (final @NotNull Long idSchueler : kurs.schueler) {
				final @NotNull StundenplanSchueler schueler = DeveloperNotificationException.ifMapGetIsNull(_schueler_by_id, idSchueler);
				MapUtils.getOrCreateArrayList(_schuelermenge_by_idKurs, kurs.id).add(schueler);
			}
			MapUtils.getOrCreateArrayList(_schuelermenge_by_idKurs, kurs.id).sort(_compSchueler);
		}
	}

	private void update_unterrichtmenge() {
		_unterrichtmenge.clear();
		_unterrichtmenge.addAll(_unterricht_by_id.values());
		_unterrichtmenge.sort(_compUnterricht);

		_unterrichtHatMultiWochen = false;
		for (final @NotNull StundenplanUnterricht u : _unterrichtmenge)
			if (u.wochentyp > 0) {
				_unterrichtHatMultiWochen = true;
				break;
			}
	}

	private void update_unterrichtmenge_by_idKlasse() {
		_unterrichtmenge_by_idKlasse.clear();
		for (final @NotNull StundenplanUnterricht unterricht : _unterricht_by_id.values())
			for (final @NotNull Long idKlasse : unterricht.klassen)
				MapUtils.getOrCreateArrayList(_unterrichtmenge_by_idKlasse, idKlasse).add(unterricht);

		for (final @NotNull StundenplanKlasse klasse : _klasse_by_id.values())
			MapUtils.getOrCreateArrayList(_unterrichtmenge_by_idKlasse, klasse.id).sort(_compUnterricht);
	}

	private void update_unterrichtmenge_by_idKlasse_and_idZeitraster() {
		_unterrichtmenge_by_idKlasse_and_idZeitraster.clear();
		for (final @NotNull StundenplanUnterricht u : _unterricht_by_id.values())
			for (final @NotNull Long idKlasse : u.klassen)
				Map2DUtils.getOrCreateArrayList(_unterrichtmenge_by_idKlasse_and_idZeitraster, idKlasse, u.idZeitraster).add(u);

		for (final @NotNull Long idKlasse : _unterrichtmenge_by_idKlasse_and_idZeitraster.getKeySet())
			for (final @NotNull Long idZeitraster : _unterrichtmenge_by_idKlasse_and_idZeitraster.getKeySetOf(idKlasse))
				Map2DUtils.getOrCreateArrayList(_unterrichtmenge_by_idKlasse_and_idZeitraster, idKlasse, idZeitraster).sort(_compUnterricht);
	}

	private void update_unterrichtmenge_by_idKurs() {
		_unterrichtmenge_by_idKurs.clear();
		for (final @NotNull StundenplanUnterricht u : _unterricht_by_id.values())
			if (u.idKurs != null)
				MapUtils.getOrCreateArrayList(_unterrichtmenge_by_idKurs, u.idKurs).add(u);

		for (final @NotNull StundenplanKurs kurs : _kurs_by_id.values())
			MapUtils.getOrCreateArrayList(_unterrichtmenge_by_idKurs, kurs.id).sort(_compUnterricht);
	}

	private void update_unterrichtmenge_by_idZeitraster() {
		_unterrichtmenge_by_idZeitraster.clear();
		for (final @NotNull StundenplanUnterricht u : _unterricht_by_id.values())
			MapUtils.getOrCreateArrayList(_unterrichtmenge_by_idZeitraster, u.idZeitraster).add(u);

		for (final @NotNull StundenplanZeitraster z : _zeitraster_by_id.values())
			MapUtils.getOrCreateArrayList(_unterrichtmenge_by_idZeitraster, z.id).sort(_compUnterricht);
	}

	private void update_unterrichtmenge_by_idKlasse_and_idFach() {
		_unterrichtmenge_by_idKlasse_and_idFach.clear();
		for (final @NotNull StundenplanUnterricht u : _unterricht_by_id.values())
			for (final @NotNull Long idKlasse : u.klassen)
				Map2DUtils.getOrCreateArrayList(_unterrichtmenge_by_idKlasse_and_idFach, idKlasse, u.idFach).add(u);

		for (final @NotNull Long idKlasse : _unterrichtmenge_by_idKlasse_and_idFach.getKeySet())
			for (final @NotNull Long idFach : _unterrichtmenge_by_idKlasse_and_idFach.getKeySetOf(idKlasse))
				Map2DUtils.getOrCreateArrayList(_unterrichtmenge_by_idKlasse_and_idFach, idKlasse, idFach).sort(_compUnterricht);
	}

	private void update_unterrichtmenge_by_idZeitraster_and_wochentyp() {
		_unterrichtmenge_by_idZeitraster_and_wochentyp.clear();
		for (final @NotNull StundenplanUnterricht u : _unterricht_by_id.values())
			Map2DUtils.getOrCreateArrayList(_unterrichtmenge_by_idZeitraster_and_wochentyp, u.idZeitraster, u.wochentyp).add(u);

		for (final @NotNull Long idKlasse : _unterrichtmenge_by_idZeitraster_and_wochentyp.getKeySet())
			for (final @NotNull Integer wochentyp : _unterrichtmenge_by_idZeitraster_and_wochentyp.getKeySetOf(idKlasse))
				Map2DUtils.getOrCreateArrayList(_unterrichtmenge_by_idZeitraster_and_wochentyp, idKlasse, wochentyp).sort(_compUnterricht);
	}

	private void update_zeitraster_by_wochentag_and_stunde() {
		_zeitraster_by_wochentag_and_stunde.clear();
		for (final @NotNull StundenplanZeitraster zeitraster : _zeitraster_by_id.values())
			DeveloperNotificationException.ifMap2DPutOverwrites(_zeitraster_by_wochentag_and_stunde, zeitraster.wochentag, zeitraster.unterrichtstunde, zeitraster);
	}

	private void update_zeitrastermenge() {
		_zeitrastermenge.clear();
		_zeitrastermenge.addAll(_zeitraster_by_id.values());
		_zeitrastermenge.sort(_compZeitraster);

		_zeitrasterMinutenMinByStunde.clear();
		_zeitrasterMinutenMaxByStunde.clear();

		_zeitrasterMinutenMin = null;
		_zeitrasterMinutenMax = null;
		_zeitrasterWochentagMin = Wochentag.SONNTAG.id + 1;
		_zeitrasterWochentagMax = Wochentag.MONTAG.id - 1;
		_zeitrasterStundeMin = 999;
		_zeitrasterStundeMax = -999;
		for (final @NotNull StundenplanZeitraster z : _zeitrastermenge) {
			_zeitrasterMinutenMin = BlockungsUtils.minII(_zeitrasterMinutenMin, z.stundenbeginn);
			_zeitrasterMinutenMax = BlockungsUtils.maxII(_zeitrasterMinutenMax, z.stundenende);
			_zeitrasterWochentagMin = BlockungsUtils.minVI(_zeitrasterWochentagMin, z.wochentag);
			_zeitrasterWochentagMax = BlockungsUtils.maxVI(_zeitrasterWochentagMax, z.wochentag);
			_zeitrasterStundeMin = BlockungsUtils.minVI(_zeitrasterStundeMin, z.unterrichtstunde);
			_zeitrasterStundeMax = BlockungsUtils.maxVI(_zeitrasterStundeMax, z.unterrichtstunde);
			_zeitrasterMinutenMinByStunde.put(z.unterrichtstunde, BlockungsUtils.minII(_zeitrasterMinutenMinByStunde.get(z.unterrichtstunde), z.stundenbeginn));
			_zeitrasterMinutenMaxByStunde.put(z.unterrichtstunde, BlockungsUtils.maxII(_zeitrasterMinutenMaxByStunde.get(z.unterrichtstunde), z.stundenende));
		}
		_zeitrasterWochentagMin = (_zeitrasterWochentagMin == Wochentag.SONNTAG.id + 1) ? Wochentag.MONTAG.id : _zeitrasterWochentagMin;
		_zeitrasterWochentagMax = (_zeitrasterWochentagMax == Wochentag.MONTAG.id - 1) ? Wochentag.MONTAG.id : _zeitrasterWochentagMax;
		_zeitrasterStundeMin = (_zeitrasterStundeMin ==  999) ? 1 : _zeitrasterStundeMin;
		_zeitrasterStundeMax = (_zeitrasterStundeMax == -999) ? 1 : _zeitrasterStundeMax;


		// _zeitrasterWochentageAlsEnumRange
		_zeitrasterWochentageAlsEnumRange = new Wochentag[_zeitrasterWochentagMax - _zeitrasterWochentagMin + 1];
		for (int i = 0; i < _zeitrasterWochentageAlsEnumRange.length; i++)
			_zeitrasterWochentageAlsEnumRange[i] = Wochentag.fromIDorException(_zeitrasterWochentagMin + i);

		// _zeitrasterStundenRange
		_zeitrasterStundenRange = new int[_zeitrasterStundeMax - _zeitrasterStundeMin + 1];
		for (int i = 0; i < _zeitrasterStundenRange.length; i++)
			_zeitrasterStundenRange[i] = _zeitrasterStundeMin + i;
	}

	private void update_zeitrastermengeOhneLeereUnterrichtmenge() {
		_zeitrastermengeOhneLeere.clear();
		for (final @NotNull StundenplanZeitraster z : _zeitraster_by_id.values())
			if (!MapUtils.getOrCreateArrayList(_unterrichtmenge_by_idZeitraster, z.id).isEmpty())
				_zeitrastermengeOhneLeere.add(z);
		_zeitrastermengeOhneLeere.sort(_compZeitraster);

		_zeitrasterMinutenMinOhneLeere = null;
		_zeitrasterMinutenMaxOhneLeere = null;
		_zeitrasterStundeMinOhneLeere = 999;
		_zeitrasterStundeMaxOhneLeere = -999;
		for (final @NotNull StundenplanZeitraster z : _zeitrastermengeOhneLeere) {
			_zeitrasterMinutenMinOhneLeere = BlockungsUtils.minII(_zeitrasterMinutenMinOhneLeere, z.stundenbeginn);
			_zeitrasterMinutenMaxOhneLeere = BlockungsUtils.maxII(_zeitrasterMinutenMaxOhneLeere, z.stundenende);
			_zeitrasterStundeMinOhneLeere = BlockungsUtils.minVI(_zeitrasterStundeMinOhneLeere, z.unterrichtstunde);
			_zeitrasterStundeMaxOhneLeere = BlockungsUtils.maxVI(_zeitrasterStundeMaxOhneLeere, z.unterrichtstunde);
		}
		_zeitrasterStundeMinOhneLeere = (_zeitrasterStundeMinOhneLeere ==  999) ? 1 : _zeitrasterStundeMinOhneLeere;
		_zeitrasterStundeMaxOhneLeere = (_zeitrasterStundeMaxOhneLeere == -999) ? 1 : _zeitrasterStundeMaxOhneLeere;

		// _uZeitrasterStundenRangeOhneLeere
		_zeitrasterStundenRangeOhneLeere = new int[_zeitrasterStundeMaxOhneLeere - _zeitrasterStundeMinOhneLeere + 1];
		for (int i = 0; i < _zeitrasterStundenRangeOhneLeere.length; i++)
			_zeitrasterStundenRangeOhneLeere[i] = _zeitrasterStundeMinOhneLeere + i;
	}

	private void update_zeitrastermenge_by_wochentag() {
		_zeitrastermenge_by_wochentag.clear();
		for (final @NotNull StundenplanZeitraster zeitraster : _zeitraster_by_id.values())
			MapUtils.getOrCreateArrayList(_zeitrastermenge_by_wochentag, zeitraster.wochentag).add(zeitraster);

		for (final @NotNull Integer wochentag : _zeitrastermenge_by_wochentag.keySet())
			MapUtils.getOrCreateArrayList(_zeitrastermenge_by_wochentag, wochentag).sort(_compZeitraster);
	}

	private void update_zeitrastermenge_by_stunde() {
		_zeitrastermenge_by_stunde.clear();
		for (final @NotNull StundenplanZeitraster zeitraster : _zeitraster_by_id.values())
			MapUtils.getOrCreateArrayList(_zeitrastermenge_by_stunde, zeitraster.unterrichtstunde).add(zeitraster);

		for (final @NotNull Integer stunde : _zeitrastermenge_by_stunde.keySet())
			MapUtils.getOrCreateArrayList(_zeitrastermenge_by_stunde, stunde).sort(_compZeitraster);
	}

	// #####################################################################
	// #################### StundenplanAufsichtsbereich ####################
	// #####################################################################


	private void aufsichtsbereichRevalidate() {
		update_aufsichtsbereichmenge();
	}

	private void aufsichtsbereichAddOhneUpdate(final @NotNull StundenplanAufsichtsbereich aufsichtsbereich) {
		aufsichtsbereichCheck(aufsichtsbereich);
		DeveloperNotificationException.ifMapPutOverwrites(_aufsichtsbereich_by_id, aufsichtsbereich.id, aufsichtsbereich);
	}

	/**
	 * Fügt ein {@link StundenplanAufsichtsbereich}-Objekt hinzu.
	 * <br>Laufzeit: O(|StundenplanAufsichtsbereich|), da aufsichtsbereichUpdate() aufgerufen wird.
	 *
	 * @param aufsichtsbereich  Das {@link StundenplanAufsichtsbereich}-Objekt, welches hinzugefügt werden soll.
	 */
	public void aufsichtsbereichAdd(final @NotNull StundenplanAufsichtsbereich aufsichtsbereich) {
		aufsichtsbereichAddOhneUpdate(aufsichtsbereich);
		aufsichtsbereichRevalidate();
	}

	/**
	 * Fügt alle {@link StundenplanAufsichtsbereich}-Objekte hinzu.
	 * <br>Laufzeit: O(|StundenplanAufsichtsbereich|), da aufsichtsbereichUpdate() aufgerufen wird.
	 *
	 * @param listAufsichtsbereich  Die Menge der {@link StundenplanAufsichtsbereich}-Objekte, welche hinzugefügt werden soll.
	 */
	public void aufsichtsbereichAddAll(final @NotNull List<@NotNull StundenplanAufsichtsbereich> listAufsichtsbereich) {
		for (final @NotNull StundenplanAufsichtsbereich aufsichtsbereich : listAufsichtsbereich)
			aufsichtsbereichAddOhneUpdate(aufsichtsbereich);
		aufsichtsbereichRevalidate();
	}

	private static void aufsichtsbereichCheck(final @NotNull StundenplanAufsichtsbereich aufsichtsbereich) {
		DeveloperNotificationException.ifInvalidID("aufsicht.id", aufsichtsbereich.id);
		DeveloperNotificationException.ifStringIsBlank("aufsicht.kuerzel", aufsichtsbereich.kuerzel);
		// aufsicht.beschreibung darf "blank" sein
	}

	/**
	 * Liefert das zur ID zugehörige {@link StundenplanAufsichtsbereich}-Objekt.
	 *
	 * @param idAufsichtsbereich  Die Datenbank-ID des angefragten-Objektes.
	 *
	 * @return das zur ID zugehörige {@link StundenplanAufsichtsbereich}-Objekt.
	 */
	public @NotNull StundenplanAufsichtsbereich aufsichtsbereichGetByIdOrException(final long idAufsichtsbereich) {
		return DeveloperNotificationException.ifMapGetIsNull(_aufsichtsbereich_by_id, idAufsichtsbereich);
	}

	/**
	 * Liefert eine Liste aller {@link StundenplanAufsichtsbereich}-Objekte.
	 * <br> Laufzeit: O(1)
	 *
	 * @return eine Liste aller {@link StundenplanAufsichtsbereich}-Objekte.
	 */
	public @NotNull List<@NotNull StundenplanAufsichtsbereich> aufsichtsbereichGetMengeAsList() {
		return _aufsichtsbereichmenge;
	}

	/**
	 * Aktualisiert das vorhandene {@link StundenplanAufsichtsbereich}-Objekt durch das neue Objekt.
	 * <br>Die folgenden Attribute werden nicht aktualisiert:
	 * <br>{@link StundenplanAufsichtsbereich#id}
	 * <br>
	 * <br>Die folgenden Attribute werden kopiert:
	 * <br>{@link StundenplanAufsichtsbereich#beschreibung}
	 * <br>{@link StundenplanAufsichtsbereich#kuerzel}
	 *
	 * @param aufsichtsbereich  Das neue {@link StundenplanAufsichtsbereich}-Objekt, dessen Attribute kopiert werden.
	 */
	public void aufsichtsbereichPatchAttributes(final @NotNull StundenplanAufsichtsbereich aufsichtsbereich) {
		aufsichtsbereichCheck(aufsichtsbereich);

		// Altes Objekt durch neues Objekt ersetzen
		DeveloperNotificationException.ifMapRemoveFailes(_aufsichtsbereich_by_id, aufsichtsbereich.id);
		DeveloperNotificationException.ifMapPutOverwrites(_aufsichtsbereich_by_id, aufsichtsbereich.id, aufsichtsbereich);

		aufsichtsbereichRevalidate();
	}

	private void aufsichtsbereichRemoveOhneUpdateById(final long idAufsichtsbereich) {
		DeveloperNotificationException.ifMapRemoveFailes(_aufsichtsbereich_by_id, idAufsichtsbereich);
	}

	/**
	 * Entfernt ein {@link StundenplanAufsichtsbereich}-Objekt anhand seiner ID.
	 * <br>Laufzeit: O(|StundenplanAufsichtsbereich|), da aufsichtsbereichUpdate() aufgerufen wird.
	 *
	 * @param idAufsichtsbereich  Die Datenbank-ID des {@link StundenplanAufsichtsbereich}-Objekts, welches entfernt werden soll.
	 */
	public void aufsichtsbereichRemoveById(final long idAufsichtsbereich) {
		aufsichtsbereichRemoveOhneUpdateById(idAufsichtsbereich);
		aufsichtsbereichRevalidate();
	}

	/**
	 * Entfernt alle {@link StundenplanAufsichtsbereich}-Objekte.
	 *
	 * @param listAufsichtsbereich  Die Liste der zu entfernenden {@link StundenplanAufsichtsbereich}-Objekte.
	 */
	public void aufsichtsbereichRemoveAll(final @NotNull List<@NotNull StundenplanAufsichtsbereich> listAufsichtsbereich) {
		for (final @NotNull StundenplanAufsichtsbereich aufsichtsbereich : listAufsichtsbereich)
			aufsichtsbereichRemoveOhneUpdateById(aufsichtsbereich.id);
		aufsichtsbereichRevalidate();
	}

	// #####################################################################
	// #################### StundenplanFach ################################
	// #####################################################################

	private void fachRevalidate() {
		update_fachmenge();
		update_unterrichtmenge_by_idKlasse_and_idFach();
	}

	private void fachAddOhneUpdate(final @NotNull StundenplanFach fach) {
		fachCheck(fach);
		DeveloperNotificationException.ifMapPutOverwrites(_fach_by_id, fach.id, fach);
	}

	/**
	 * Fügt ein {@link StundenplanFach}-Objekt hinzu.
	 * <br>Laufzeit: O(|StundenplanFach|), da fachUpdate() aufgerufen wird.
	 *
	 * @param fach  Das {@link StundenplanFach}-Objekt, welches hinzugefügt werden soll.
	 */
	public void fachAdd(final @NotNull StundenplanFach fach) {
		fachAddOhneUpdate(fach);
		fachRevalidate();
	}

	/**
	 * Fügt alle {@link StundenplanFach}-Objekte hinzu.
	 * <br>Laufzeit: O(|StundenplanFach|), da fachUpdate() aufgerufen wird.
	 *
	 * @param listFach  Die Menge der {@link StundenplanFach}-Objekte, welche hinzugefügt werden soll.
	 */
	public void fachAddAll(final @NotNull List<@NotNull StundenplanFach> listFach) {
		for (final @NotNull StundenplanFach fach : listFach)
			fachAddOhneUpdate(fach);
		fachRevalidate();
	}

	private static void fachCheck(final @NotNull StundenplanFach fach) {
		DeveloperNotificationException.ifInvalidID("fach.id", fach.id);
		DeveloperNotificationException.ifStringIsBlank("fach.bezeichnung", fach.bezeichnung);
		DeveloperNotificationException.ifStringIsBlank("fach.kuerzel", fach.kuerzel);
	}

	/**
	 * Liefert das Fach mit der übergebenen ID.
	 *
	 * @param idFach  Die Datenbank-ID des Faches.
	 *
	 * @return  das Fach mit der übergebenen ID.
	 */
	public @NotNull StundenplanFach fachGetByIdOrException(final long idFach) {
		return DeveloperNotificationException.ifMapGetIsNull(_fach_by_id, idFach);
	}

	/**
	 * Liefert eine Liste aller {@link StundenplanFach}-Objekte.
	 * <br>Laufzeit: O(1)
	 *
	 * @return eine Liste aller {@link StundenplanFach}-Objekte.
	 */
	public @NotNull List<@NotNull StundenplanFach> fachGetMengeAsList() {
		return _fachmenge;
	}

	// #####################################################################
	// #################### StundenplanJahrgang ############################
	// #####################################################################

	private void jahrgangRevalidate() {
		update_jahrgangmenge();
	}

	private void jahrgangAddOhneUpdate(final @NotNull StundenplanJahrgang jahrgang) {
		jahrgangCheck(jahrgang);
		DeveloperNotificationException.ifMapPutOverwrites(_jahrgang_by_id, jahrgang.id, jahrgang);
	}

	/**
	 * Fügt ein {@link StundenplanJahrgang}-Objekt hinzu.
	 * <br>Laufzeit: O(|StundenplanJahrgang|), da jahrgangUpdate() aufgerufen wird.
	 *
	 * @param jahrgang  Das {@link StundenplanJahrgang}-Objekt, welches hinzugefügt werden soll.
	 */
	public void jahrgangAdd(final @NotNull StundenplanJahrgang jahrgang) {
		jahrgangAddOhneUpdate(jahrgang);
		jahrgangRevalidate();
	}

	/**
	 * Fügt alle {@link StundenplanJahrgang}-Objekte hinzu.
	 * <br>Laufzeit: O(|StundenplanJahrgang|), da jahrgangUpdate() aufgerufen wird.
	 *
	 * @param listJahrgang  Die Menge der {@link StundenplanJahrgang}-Objekte, welche hinzugefügt werden soll.
	 */
	public void jahrgangAddAll(final @NotNull List<@NotNull StundenplanJahrgang> listJahrgang) {
		for (final @NotNull StundenplanJahrgang jahrgang : listJahrgang)
			jahrgangAddOhneUpdate(jahrgang);
		jahrgangRevalidate();
	}

	private static void jahrgangCheck(final @NotNull StundenplanJahrgang jahrgang) {
		DeveloperNotificationException.ifInvalidID("jahrgang.id", jahrgang.id);
		DeveloperNotificationException.ifStringIsBlank("jahrgang.bezeichnung", jahrgang.bezeichnung);
		DeveloperNotificationException.ifStringIsBlank("jahrgang.kuerzel", jahrgang.kuerzel);
	}

	/**
	 * Liefert das {@link StundenplanJahrgang}-Objekt mit der übergebenen ID.
	 *
	 * @param idJahrgang  Die Datenbank-ID des {@link StundenplanJahrgang}-Objekts.
	 *
	 * @return das {@link StundenplanJahrgang}-Objekt mit der übergebenen ID.
	 */
	public @NotNull StundenplanJahrgang jahrgangGetByIdOrException(final long idJahrgang) {
		return DeveloperNotificationException.ifMapGetIsNull(_jahrgang_by_id, idJahrgang);
	}

	/**
	 * Liefert eine Liste aller {@link StundenplanJahrgang}-Objekte.
	 * <br>Laufzeit: O(1)
	 *
	 * @return eine Liste aller {@link StundenplanJahrgang}-Objekte.
	 */
	public @NotNull List<@NotNull StundenplanJahrgang> jahrgangGetMengeAsList() {
		return _jahrgangmenge;
	}

	/**
	 * Aktualisiert das vorhandene {@link StundenplanJahrgang}-Objekt durch das neue Objekt.
	 * <br>Die folgenden Attribute werden nicht aktualisiert:
	 * <br>{@link StundenplanJahrgang#id}
	 * <br>
	 * <br>Die folgenden Attribute werden kopiert:
	 * <br>{@link StundenplanJahrgang#bezeichnung}
	 * <br>{@link StundenplanJahrgang#kuerzel}
	 *
	 * @param jahrgang  Das neue {@link StundenplanJahrgang}-Objekt, dessen Attribute kopiert werden.
	 */
	public void jahrgangPatchAttributes(final @NotNull StundenplanJahrgang jahrgang) {
		jahrgangCheck(jahrgang);

		// Altes Objekt durch neues Objekt ersetzen
		DeveloperNotificationException.ifMapRemoveFailes(_jahrgang_by_id, jahrgang.id);
		DeveloperNotificationException.ifMapPutOverwrites(_jahrgang_by_id, jahrgang.id, jahrgang);

		jahrgangRevalidate();
	}

	private void jahrgangRemoveOhneUpdateById(final long idJahrgang) {
		DeveloperNotificationException.ifMapRemoveFailes(_jahrgang_by_id, idJahrgang);
	}

	/**
	 * Entfernt ein {@link StundenplanJahrgang}-Objekt anhand seiner ID.
	 * <br>Laufzeit: O(|StundenplanJahrgang|), da jahrgangUpdate() aufgerufen wird.
	 *
	 * @param idJahrgang  Die Datenbank-ID des {@link StundenplanJahrgang}-Objekts, welches entfernt werden soll.
	 */
	public void jahrgangRemoveById(final long idJahrgang) {
		jahrgangRemoveOhneUpdateById(idJahrgang);
		jahrgangRevalidate();
	}

	/**
	 * Entfernt alle {@link StundenplanJahrgang}-Objekte.
	 *
	 * @param listJahrgang  Die Liste der zu entfernenden {@link StundenplanJahrgang}-Objekte.
	 */
	public void jahrgangRemoveAll(final @NotNull List<@NotNull StundenplanJahrgang> listJahrgang) {
		for (final @NotNull StundenplanJahrgang jahrgang : listJahrgang)
			jahrgangRemoveOhneUpdateById(jahrgang.id);
		jahrgangRevalidate();
	}

	// #####################################################################
	// #################### StundenplanKalenderwochenzuordnung #############
	// #####################################################################

	private void kalenderwochenzuordnungRevalidate() {
		update_kwzmenge_update_kwz_by_jahr_and_kw();
	}

	private void kalenderwochenzuordnungAddOhneUpdate(final @NotNull StundenplanKalenderwochenzuordnung kwz) {
		kalenderwochenzuordnungCheck(kwz);
		DeveloperNotificationException.ifMapPutOverwrites(_kwz_by_id, kwz.id, kwz);
	}

	/**
	 * Fügt ein {@link StundenplanKalenderwochenzuordnung}-Objekt hinzu.
	 *
	 * @param kwz  Das {@link StundenplanKalenderwochenzuordnung}-Objekt, welches hinzugefügt werden soll.
	 */
	public void kalenderwochenzuordnungAdd(final @NotNull StundenplanKalenderwochenzuordnung kwz) {
		kalenderwochenzuordnungAddOhneUpdate(kwz);
		kalenderwochenzuordnungRevalidate();
	}

	/**
	 * Fügt alle {@link StundenplanKalenderwochenzuordnung}-Objekte hinzu.
	 *
	 * @param listKWZ  Die Menge der {@link StundenplanKalenderwochenzuordnung}-Objekte, welche hinzugefügt werden soll.
	 */
	public void kalenderwochenzuordnungAddAll(final @NotNull List<@NotNull StundenplanKalenderwochenzuordnung> listKWZ) {
		for (final @NotNull StundenplanKalenderwochenzuordnung kwz : listKWZ)
			kalenderwochenzuordnungAddOhneUpdate(kwz);
		kalenderwochenzuordnungRevalidate();
	}

	private void kalenderwochenzuordnungCheck(final @NotNull StundenplanKalenderwochenzuordnung kwz) {
		DeveloperNotificationException.ifInvalidID("kwz.id", kwz.id);
		DeveloperNotificationException.ifTrue("(kwz.jahr < DateUtils.MIN_GUELTIGES_JAHR) || (kwz.jahr > DateUtils.MAX_GUELTIGES_JAHR)", (kwz.jahr < DateUtils.MIN_GUELTIGES_JAHR) || (kwz.jahr > DateUtils.MAX_GUELTIGES_JAHR));
		DeveloperNotificationException.ifTrue("(kwz.kw < 1) || (kwz.kw > DateUtils.gibKalenderwochenOfJahr(kwz.jahr))", (kwz.kw < 1) || (kwz.kw > DateUtils.gibKalenderwochenOfJahr(kwz.jahr)));
		DeveloperNotificationException.ifTrue("kwz.wochentyp > stundenplanWochenTypModell", kwz.wochentyp > _stundenplanWochenTypModell);
		DeveloperNotificationException.ifTrue("kwz.wochentyp <=0", kwz.wochentyp <= 0);
	}

	/**
	 * Liefert das zur ID zugehörige {@link StundenplanKalenderwochenzuordnung}-Objekt.
	 * <br>Laufzeit: O(1)
	 *
	 * @param idKWZ Die ID des angefragten-Objektes.
	 *
	 * @return das zur ID zugehörige {@link StundenplanKalenderwochenzuordnung}-Objekt.
	 */
	public @NotNull StundenplanKalenderwochenzuordnung kalenderwochenzuordnungGetByIdOrException(final long idKWZ) {
		return DeveloperNotificationException.ifMapGetIsNull(_kwz_by_id, idKWZ);
	}

	/**
	 * Liefert das dem Jahr und der Kalenderwoche zugeordnete {@link StundenplanKalenderwochenzuordnung}-Objekt der Auswahl-Menge.
	 * <br>Hinweis: Einige Objekte dieser Menge können die ID = -1 haben, falls sie erzeugt wurden und nicht aus der DB stammen.
	 * <br>Laufzeit: O(1)
	 *
	 * @param jahr           Das Jahr der Kalenderwoche.
	 * @param kalenderwoche  Die gewünschten Kalenderwoche.
	 *
	 * @return das dem Jahr und der Kalenderwoche zugeordnete {@link StundenplanKalenderwochenzuordnung}-Objekt der Auswahl-Menge.
	 */
	public @NotNull StundenplanKalenderwochenzuordnung kalenderwochenzuordnungGetByJahrAndKWOrException(final int jahr, final int kalenderwoche) {
		return DeveloperNotificationException.ifMap2DGetIsNull(_kwz_by_jahr_and_kw, jahr, kalenderwoche);
	}

	/**
	 * Liefert eine Liste aller {@link StundenplanKalenderwochenzuordnung}-Objekte.
	 * <br>Hinweis: Einige Objekte dieser Menge können die ID = -1 haben, falls sie erzeugt wurden und nicht aus der DB stammen.
	 * <br>Laufzeit: O(1)
	 *
	 * @return eine Liste aller {@link StundenplanKalenderwochenzuordnung}-Objekte.
	 */
	public @NotNull List<@NotNull StundenplanKalenderwochenzuordnung> kalenderwochenzuordnungGetMengeAsList() {
		return _kwzmenge;
	}

	/**
	 * Liefert eine String-Darstellung der Kalenderwoche des {@link StundenplanKalenderwochenzuordnung}-Objekts.
	 * <br>Beispiel: Jahr 2023, KW  5 --> "30.01.2023 - 05.02.2023 (KW 2023.5)"
	 * <br>Beispiel: Jahr 2025, KW  1 --> "30.12.2024 - 05.01.2025 (KW 2025.1)"
	 * <br>Beispiel: Jahr 2026, KW 53 --> "28.12.2026 - 03.01.2027 (KW 2026.53)"
	 * <br>Laufzeit: O(1)

	 * @param kwz  Das {@link StundenplanKalenderwochenzuordnung}-Objekt.
	 *
	 * @return eine String-Darstellung der Kalenderwoche des {@link StundenplanKalenderwochenzuordnung}-Objekts.
	 */
	public @NotNull String kalenderwochenzuordnungGetWocheAsString(final @NotNull StundenplanKalenderwochenzuordnung kwz) {
		final @NotNull String sMo = DateUtils.gibDatumDesMontagsOfJahrAndKalenderwoche(kwz.jahr, kwz.kw);
		final @NotNull String sSo = DateUtils.gibDatumDesSonntagsOfJahrAndKalenderwoche(kwz.jahr, kwz.kw);
		final @NotNull String sMoGer = DateUtils.gibDatumGermanFormat(sMo);
		final @NotNull String sSoGer = DateUtils.gibDatumGermanFormat(sSo);
		final @NotNull String sJahrKW = "(KW " + kwz.jahr + "." + kwz.kw + ")";
		return sMoGer + " - " + sSoGer + " " + sJahrKW;
	}

	/**
	 * Liefert den zugeordneten Wochentyp, oder den Default-Wochentyp, welcher sich aus der Kalenderwoche berechnet.
	 * <br>Laufzeit: O(1)
	 *
	 * @param jahr           Das Jahr der Kalenderwoche. Es muss zwischen {@link DateUtils#MIN_GUELTIGES_JAHR} und {@link DateUtils#MAX_GUELTIGES_JAHR} liegen.
	 * @param kalenderwoche  Die gewünschten Kalenderwoche. Es muss zwischen 1 und {@link DateUtils#gibKalenderwochenOfJahr(int)} liegen.
	 *
	 * @return den zugeordneten Wochentyp, oder den Default-Wochentyp, welcher sich aus der Kalenderwoche berechnet.
	 */
	public int kalenderwochenzuordnungGetWochentypOrDefault(final int jahr, final int kalenderwoche) {
		// Überprüfen
		DeveloperNotificationException.ifSmaller("jahr", jahr, DateUtils.MIN_GUELTIGES_JAHR);
		DeveloperNotificationException.ifGreater("jahr", jahr, DateUtils.MAX_GUELTIGES_JAHR);
		DeveloperNotificationException.ifSmaller("kalenderwoche", kalenderwoche, 1);
		DeveloperNotificationException.ifGreater("kalenderwoche", kalenderwoche, DateUtils.gibKalenderwochenOfJahr(jahr));

		// Fall: Das globale Modell hat keine Multiwochen.
		if (_stundenplanWochenTypModell == 0)
			return 0;

		// Fall: Eine Zuordnung ist definiert.
		final StundenplanKalenderwochenzuordnung z = _kwz_by_jahr_and_kw.getOrNull(jahr, kalenderwoche);
		if (z != null)
			return z.wochentyp;

		// Default: Der Wert berechnet sich modulo der Kalenderwoche.
		final int wochentyp = kalenderwoche % _stundenplanWochenTypModell;
		return wochentyp == 0 ? _stundenplanWochenTypModell : wochentyp; // 0 wird zu stundenplanWochenTypModell rotiert!
	}

	/**
	 * Liefert TRUE, falls intern ein Mapping von "Jahr, Kalenderwoche" den Wochentyp verwendet wird.
	 * <br>Hinweis: Das Mapping muss existieren UND {@link #_stundenplanWochenTypModell} muss mindestens 2 sein.
	 * <br>Laufzeit: O(1)
	 *
	 * @param jahr           Das Jahr der Kalenderwoche. Es muss zwischen {@link DateUtils#MIN_GUELTIGES_JAHR} und {@link DateUtils#MAX_GUELTIGES_JAHR} liegen.
	 * @param kalenderwoche  Die gewünschten Kalenderwoche. Es muss zwischen 1 und {@link DateUtils#gibKalenderwochenOfJahr(int)} liegen.
	 *
	 * @return TRUE, falls intern ein Mapping von "Jahr, Kalenderwoche" den Wochentyp verwendet wird.
	 */
	public boolean kalenderwochenzuordnungGetWochentypUsesMapping(final int jahr, final int kalenderwoche) {
		// Überprüfen
		DeveloperNotificationException.ifSmaller("jahr", jahr, DateUtils.MIN_GUELTIGES_JAHR);
		DeveloperNotificationException.ifGreater("jahr", jahr, DateUtils.MAX_GUELTIGES_JAHR);
		DeveloperNotificationException.ifSmaller("kalenderwoche", kalenderwoche, 1);
		DeveloperNotificationException.ifGreater("kalenderwoche", kalenderwoche, DateUtils.gibKalenderwochenOfJahr(jahr));

		// Berechnen
		final StundenplanKalenderwochenzuordnung z = _kwz_by_jahr_and_kw.getOrNull(jahr, kalenderwoche);
		return (_stundenplanWochenTypModell >= 2) && (z != null);
	}

	/**
	 * Aktualisiert das vorhandene {@link StundenplanKalenderwochenzuordnung}-Objekt durch das neue Objekt.
	 * <br>Die folgenden Attribute werden nicht aktualisiert:
	 * <br>{@link StundenplanKalenderwochenzuordnung#id}
	 * <br>
	 * <br>Die folgenden Attribute werden kopiert:
	 * <br>{@link StundenplanKalenderwochenzuordnung#jahr}
	 * <br>{@link StundenplanKalenderwochenzuordnung#kw}
	 * <br>{@link StundenplanKalenderwochenzuordnung#wochentyp}
	 *
	 * @param kwz  Das neue {@link StundenplanKalenderwochenzuordnung}-Objekt, dessen Attribute kopiert werden.
	 */
	public void kalenderwochenzuordnungPatchAttributes(final @NotNull StundenplanKalenderwochenzuordnung kwz) {
		kalenderwochenzuordnungCheck(kwz);

		// Altes Objekt durch neues Objekt ersetzen
		DeveloperNotificationException.ifMapRemoveFailes(_kwz_by_id, kwz.id);
		DeveloperNotificationException.ifMapPutOverwrites(_kwz_by_id, kwz.id, kwz);

		kalenderwochenzuordnungRevalidate();
	}

	private void kalenderwochenzuordnungRemoveOhneUpdateById(final long idKWZ) {
		DeveloperNotificationException.ifMapRemoveFailes(_kwz_by_id, idKWZ);
	}

	/**
	 * Entfernt ein {@link StundenplanKalenderwochenzuordnung}-Objekt anhand seiner Datenbank-ID.
	 *
	 * @param idKWZ  Die Datenbank-ID des {@link StundenplanKalenderwochenzuordnung}-Objekts, welches entfernt werden soll.
	 */
	public void kalenderwochenzuordnungRemoveById(final long idKWZ) {
		kalenderwochenzuordnungRemoveOhneUpdateById(idKWZ);
		kalenderwochenzuordnungRevalidate();
	}

	/**
	 * Entfernt alle {@link StundenplanKalenderwochenzuordnung}-Objekte.
	 *
	 * @param listKWZ  Die Liste der zu entfernenden {@link StundenplanKalenderwochenzuordnung}-Objekte.
	 */
	public void kalenderwochenzuordnungRemoveAll(final @NotNull List<@NotNull StundenplanKalenderwochenzuordnung> listKWZ) {
		for (final @NotNull StundenplanKalenderwochenzuordnung kwz : listKWZ)
			kalenderwochenzuordnungRemoveOhneUpdateById(kwz.id);
		kalenderwochenzuordnungRevalidate();
	}

	// #####################################################################
	// #################### StundenplanKlasse ##############################
	// #####################################################################

	private void klasseRevalidate() {
		update_klassenmenge();
		update_schuelermenge_by_idKlasse();
		update_kursmenge_by_idKlasse();
		update_unterrichtmenge_by_idKlasse();
		update_klassenunterrichtmenge_by_idKlasse();
		update_unterrichtmenge_by_idKlasse_and_idFach();
		update_unterrichtmenge_by_idKlasse_and_idZeitraster();
	}

	private void klasseAddOhneUpdate(final @NotNull StundenplanKlasse klasse) {
		klasseCheck(klasse);
		DeveloperNotificationException.ifMapPutOverwrites(_klasse_by_id, klasse.id, klasse);
	}

	/**
	 * Fügt ein {@link StundenplanKlasse}-Objekt hinzu.
	 *
	 * @param klasse  Das {@link StundenplanKlasse}-Objekt, welches hinzugefügt werden soll.
	 */
	public void klasseAdd(final @NotNull StundenplanKlasse klasse) {
		klasseAddOhneUpdate(klasse);
		klasseRevalidate();
	}

	/**
	 * Fügt alle {@link StundenplanKlasse}-Objekte hinzu.
	 *
	 * @param listKlasse  Die Menge der {@link StundenplanKlasse}-Objekte, welche hinzugefügt werden soll.
	 */
	public void klasseAddAll(final @NotNull List<@NotNull StundenplanKlasse> listKlasse) {
		for (final @NotNull StundenplanKlasse klasse : listKlasse)
			klasseAddOhneUpdate(klasse);
		klasseRevalidate();
	}

	private void klasseCheck(final @NotNull StundenplanKlasse klasse) {
		DeveloperNotificationException.ifInvalidID("klasse.id", klasse.id);
		DeveloperNotificationException.ifStringIsBlank("klasse.kuerzel", klasse.kuerzel);
		// klasse.bezeichnung darf "blank" sein
		for (final @NotNull Long idJahrgang : klasse.jahrgaenge)
			DeveloperNotificationException.ifMapNotContains("_jahrgang_by_id", _jahrgang_by_id, idJahrgang);
		for (final @NotNull Long idSchueler : klasse.schueler)
			DeveloperNotificationException.ifMapNotContains("_schueler_by_id", _schueler_by_id, idSchueler);
	}

	/**
	 * Liefert das {@link StundenplanKlasse}-Objekt mit der übergebenen ID.
	 * <br>Laufzeit: O(1)
	 *
	 * @param idKlasse  Die Datenbank-ID des {@link StundenplanKlasse}-Objekts.
	 *
	 * @return das {@link StundenplanKlasse}-Objekt mit der übergebenen ID.
	 */
	public @NotNull StundenplanKlasse klasseGetByIdOrException(final long idKlasse) {
		return DeveloperNotificationException.ifMapGetIsNull(_klasse_by_id, idKlasse);
	}

	/**
	 * Liefert eine Liste aller {@link StundenplanKlasse}-Objekte.
	 * <br>Laufzeit: O(1)
	 *
	 * @return eine Liste aller {@link StundenplanKlasse}-Objekte.
	 */
	public @NotNull List<@NotNull StundenplanKlasse> klasseGetMengeAsList() {
		return _klassenmenge;
	}

	/**
	 * Aktualisiert das vorhandene {@link StundenplanKlasse}-Objekt durch das neue Objekt.
	 * <br>Die folgenden Attribute werden nicht aktualisiert:
	 * <br>{@link StundenplanKlasse#id}
	 * <br>
	 * <br>Die folgenden Attribute werden kopiert:
	 * <br>{@link StundenplanKlasse#bezeichnung}
	 * <br>{@link StundenplanKlasse#kuerzel}
	 * <br>{@link StundenplanKlasse#jahrgaenge}
	 * <br>{@link StundenplanKlasse#schueler}
	 *
	 * @param klasse  Das neue {@link StundenplanKlasse}-Objekt, dessen Attribute kopiert werden.
	 */
	public void klassePatchAttributes(final @NotNull StundenplanKlasse klasse) {
		klasseCheck(klasse);

		// Altes Objekt durch neues Objekt ersetzen
		DeveloperNotificationException.ifMapRemoveFailes(_klasse_by_id, klasse.id);
		DeveloperNotificationException.ifMapPutOverwrites(_klasse_by_id, klasse.id, klasse);

		klasseRevalidate();
	}

	private void klasseRemoveOhneUpdateById(final long idKlasse) {
		// Kaskade: StundenplanUnterricht (der Klasse)
		for (final @NotNull StundenplanKlassenunterricht u : DeveloperNotificationException.ifMapGetIsNull(_klassenunterrichtmenge_by_idKlasse, idKlasse))
			klassenunterrichtRemoveOhneUpdateById(u.idKlasse, u.idFach);

		// Entfernen
		DeveloperNotificationException.ifMapRemoveFailes(_klasse_by_id, idKlasse);
	}

	/**
	 * Entfernt ein {@link StundenplanKlasse}-Objekt anhand seiner ID.
	 *
	 * @param idKlasse  Die Datenbank-ID des {@link StundenplanKlasse}-Objekts, welches entfernt werden soll.
	 */
	public void klasseRemoveById(final long idKlasse) {
		klasseRemoveOhneUpdateById(idKlasse);
		klasseRevalidate();
		klassenunterrichtRevalidate();
		unterrichtRevalidate();
	}

	/**
	 * Entfernt alle {@link StundenplanKlasse}-Objekte.
	 *
	 * @param listKlasse  Die Liste der zu entfernenden {@link StundenplanKlasse}-Objekte.
	 */
	public void klasseRemoveAll(final @NotNull List<@NotNull StundenplanKlasse> listKlasse) {
		for (final @NotNull StundenplanKlasse klasse : listKlasse)
			klasseRemoveOhneUpdateById(klasse.id);
		klasseRevalidate();
		klassenunterrichtRevalidate();
		unterrichtRevalidate();
	}

	// #####################################################################
	// #################### StundenplanKlassenunterricht ###################
	// #####################################################################

	private void klassenunterrichtRevalidate() {
		update_klassenunterrichtmenge();
		update_klassenunterrichtmenge_by_idKlasse();
		update_klassenunterrichtmenge_by_idLehrer();
		update_klassenunterrichtmenge_by_idSchueler();
	}

	private void klassenunterrichtAddOhneUpdate(final @NotNull StundenplanKlassenunterricht klassenunterricht) {
		klassenunterrichtCheck(klassenunterricht);
		DeveloperNotificationException.ifMap2DPutOverwrites(_klassenunterricht_by_idKlasse_and_idFach, klassenunterricht.idKlasse, klassenunterricht.idFach, klassenunterricht);
	}

	/**
	 * Fügt ein {@link StundenplanKlassenunterricht}-Objekt hinzu.
	 *
	 * @param klassenunterricht  Das {@link StundenplanKlassenunterricht}-Objekt, welches hinzugefügt werden soll.
	 */
	public void klassenunterrichtAdd(final @NotNull StundenplanKlassenunterricht klassenunterricht) {
		klassenunterrichtAddOhneUpdate(klassenunterricht);
		klassenunterrichtRevalidate();
	}

	/**
	 * Fügt alle {@link StundenplanKlassenunterricht}-Objekte hinzu.
	 *
	 * @param listKlassenunterricht  Die Menge der {@link StundenplanKlassenunterricht}-Objekte, welche hinzugefügt werden soll.
	 */
	private void klassenunterrichtAddAll(@NotNull final List<@NotNull StundenplanKlassenunterricht> listKlassenunterricht) {
		for (final @NotNull StundenplanKlassenunterricht klassenunterricht : listKlassenunterricht)
			klassenunterrichtAddOhneUpdate(klassenunterricht);
		klassenunterrichtRevalidate();
	}

	private void klassenunterrichtCheck(final @NotNull StundenplanKlassenunterricht klassenunterricht) {
		DeveloperNotificationException.ifMapNotContains("_klasse_by_id", _klasse_by_id, klassenunterricht.idKlasse);
		DeveloperNotificationException.ifMapNotContains("_fach_by_id", _fach_by_id, klassenunterricht.idFach);
		for (final @NotNull Long idSchiene : klassenunterricht.schienen)
			DeveloperNotificationException.ifMapNotContains("_schiene_by_id", _schiene_by_id, idSchiene);
	}

	/**
	 * Liefert eine Liste aller {@link StundenplanKlassenunterricht}-Objekte.
	 * <br>Laufzeit: O(1)
	 *
	 * @return eine Liste aller {@link StundenplanKlassenunterricht}-Objekte.
	 */
	public @NotNull List<@NotNull StundenplanKlassenunterricht> klassenunterrichtGetMengeAsList() {
		return _klassenunterrichtmenge;
	}

	/**
	 * Liefert eine Liste aller {@link StundenplanKlassenunterricht}-Objekte der Klasse.
	 * <br>Laufzeit: O(1)
	 *
	 * @param idKlasse  Die Datenbank-ID der Klasse.
	 *
	 * @return eine Liste aller {@link StundenplanKlassenunterricht}-Objekte der Klasse.
	 */
	public @NotNull List<@NotNull StundenplanKlassenunterricht> klassenunterrichtGetMengeByKlasseIdAsList(final long idKlasse) {
		return MapUtils.getOrCreateArrayList(_klassenunterrichtmenge_by_idKlasse, idKlasse);
	}

	/**
	 * Liefert eine Liste aller {@link StundenplanKlassenunterricht}-Objekte des Lehrers.
	 * <br> Laufzeit: O(1)
	 *
	 * @param idLehrer  Die Datenbank-ID des Lehrers.
	 *
	 * @return eine Liste aller {@link StundenplanKlassenunterricht}-Objekte des Lehrers.
	 */
	public @NotNull List<@NotNull StundenplanKlassenunterricht> klassenunterrichtGetMengeByLehrerIdAsList(final long idLehrer) {
		return MapUtils.getOrCreateArrayList(_klassenunterrichtmenge_by_idLehrer, idLehrer);
	}

	/**
	 * Liefert eine Liste aller {@link StundenplanKlassenunterricht}-Objekte des Schülers.
	 * <br> Laufzeit: O(1)
	 *
	 * @param idSchueler  Die Datenbank-ID des Schülers.
	 *
	 * @return eine Liste aller {@link StundenplanKlassenunterricht}-Objekte des Schülers.
	 */
	public @NotNull List<@NotNull StundenplanKlassenunterricht> klassenunterrichtGetMengeBySchuelerIdAsList(final long idSchueler) {
		return MapUtils.getOrCreateArrayList(_klassenunterrichtmenge_by_idSchueler, idSchueler);
	}

	/**
	 * Liefert die IST-Wochenstunden des {@link StundenplanKlassenunterricht}.
	 * <br>Hinweis: Durch AB-Wochen, ist der Rückgabewert eine Kommazahl, da nur Stundenanteile gesetzt sein können.
	 * <br>Laufzeit: O(|Unterrichte des Klassenunterricht|)
	 *
	 * @param idKlasse  Die Datenbank-ID der Klasse.
	 * @param idFach    Die Datenbank-ID des Faches.
	 *
	 * @return die IST-Wochenstunden des {@link StundenplanKlassenunterricht}.
	 */
	public double klassenunterrichtGetWochenstundenIst(final long idKlasse, final long idFach) {
		double summe = 0;
		final double faktor = (_stundenplanWochenTypModell == 0) ? 1 : _stundenplanWochenTypModell;

		final @NotNull List<@NotNull StundenplanUnterricht> listU = DeveloperNotificationException.ifMap2DGetIsNull(_unterrichtmenge_by_idKlasse_and_idFach, idKlasse, idFach);
		for (final @NotNull  StundenplanUnterricht u : listU)
			summe += (u.wochentyp == 0) ? faktor : 1;

		return ((int) (summe * 100.0 / faktor)) / 100.0; // Auf 2 Nachkommastellen runden.
	}

	/**
	 * Liefert die SOLL-Wochenstunden des {@link StundenplanKlassenunterricht}.
	 * <br>Laufzeit: O(1)
	 *
	 * @param idKlasse  Die Datenbank-ID der Klasse.
	 * @param idFach    Die Datenbank-ID des Faches.
	 *
	 * @return die SOLL-Wochenstunden des {@link StundenplanKlassenunterricht}.
	 */
	public int klassenunterrichtGetWochenstundenSoll(final long idKlasse, final long idFach) {
		return DeveloperNotificationException.ifMap2DGetIsNull(_klassenunterricht_by_idKlasse_and_idFach, idKlasse, idFach).wochenstunden;
	}

	private void klassenunterrichtRemoveOhneUpdateById(final long idKlasse, final long idFach) {
		// Kaskade: StundenplanUnterricht (des Klassenunterrichts)
		for (final @NotNull StundenplanUnterricht u : DeveloperNotificationException.ifMap2DGetIsNull(_unterrichtmenge_by_idKlasse_and_idFach, idKlasse, idFach))
			unterrichtRemoveByIdOhneUpdate(u.id);

		// Entfernen
		DeveloperNotificationException.ifMap2DRemoveFailes(_klassenunterricht_by_idKlasse_and_idFach, idKlasse, idFach);
	}

	/**
	 * Entfernt ein {@link StundenplanKlassenunterricht}-Objekt anhand seiner ID.
	 *
	 * @param idKlasse  Die Datenbank-ID der Klasse.
	 * @param idFach    Die Datenbank-ID des Faches.
	 */
	public void klassenunterrichtRemoveById(final long idKlasse, final long idFach) {
		klassenunterrichtRemoveOhneUpdateById(idKlasse, idFach);
		klassenunterrichtRevalidate();
		unterrichtRevalidate();
	}

	/**
	 * Entfernt alle {@link StundenplanKlassenunterricht}-Objekte.
	 *
	 * @param listKlassenunterricht  Die Liste der zu entfernenden {@link StundenplanKlassenunterricht}-Objekte.
	 */
	public void klassenunterrichtRemoveAll(final @NotNull List<@NotNull StundenplanKlassenunterricht> listKlassenunterricht) {
		for (final @NotNull StundenplanKlassenunterricht klassenunterricht : listKlassenunterricht)
			klassenunterrichtRemoveOhneUpdateById(klassenunterricht.idKlasse, klassenunterricht.idFach);
		klassenunterrichtRevalidate();
		unterrichtRevalidate();
	}

	// #####################################################################
	// #################### StundenplanKurs ################################
	// #####################################################################

	private void kursRevalidate() {
		update_kursmenge();
		update_kursmenge_by_idKlasse();
		update_kursmenge_by_idLehrer();
		update_kursmenge_by_idSchueler();
		update_schuelermenge_by_idKurs();
		update_unterrichtmenge_by_idKurs();
	}

	private void kursAddOhneUpdate(final @NotNull StundenplanKurs kurs) {
		kursCheck(kurs);
		DeveloperNotificationException.ifMapPutOverwrites(_kurs_by_id, kurs.id, kurs);
	}

	/**
	 * Fügt ein {@link StundenplanKurs}-Objekt hinzu.
	 *
	 * @param kurs  Das {@link StundenplanKurs}-Objekt, welches hinzugefügt werden soll.
	 */
	public void kursAdd(final @NotNull StundenplanKurs kurs) {
		kursAddOhneUpdate(kurs);
		kursRevalidate();
	}

	/**
	 * Fügt alle {@link StundenplanKurs}-Objekte hinzu.
	 *
	 * @param listKurs  Die Menge der {@link StundenplanKurs}-Objekte, welche hinzugefügt werden soll.
	 */
	public void kursAddAll(final @NotNull List<@NotNull StundenplanKurs> listKurs) {
		for (final @NotNull  StundenplanKurs kurs : listKurs)
			kursAddOhneUpdate(kurs);
		kursRevalidate();
	}

	private void kursCheck(final @NotNull StundenplanKurs kurs) {
		DeveloperNotificationException.ifInvalidID("kurs.id", kurs.id);
		DeveloperNotificationException.ifStringIsBlank("kurs.bezeichnung", kurs.bezeichnung);
		DeveloperNotificationException.ifSmaller("kurs.wochenstunden", kurs.wochenstunden, 0);
		for (final @NotNull Long idSchieneDesKurses : kurs.schienen)
			DeveloperNotificationException.ifMapNotContains("_schiene_by_id", _schiene_by_id, idSchieneDesKurses);
		for (final @NotNull Long idJahrgangDesKurses : kurs.jahrgaenge)
			DeveloperNotificationException.ifMapNotContains("_jahrgang_by_id", _jahrgang_by_id, idJahrgangDesKurses);
		for (final @NotNull Long idSchuelerDesKurses : kurs.schueler)
			DeveloperNotificationException.ifMapNotContains("_schueler_by_id", _schueler_by_id, idSchuelerDesKurses);
		for (final @NotNull Long idLehrerDesKurses : kurs.lehrer)
			DeveloperNotificationException.ifMapNotContains("_lehrer_by_id", _lehrer_by_id, idLehrerDesKurses);
	}

	/**
	 * Liefert das {@link StundenplanKurs}-Objekt mit der übergebenen ID.
	 *
	 * @param idKurs  Die Datenbank-ID des {@link StundenplanKurs}-Objekts.
	 *
	 * @return das {@link StundenplanKurs}-Objekt mit der übergebenen ID.
	 */
	public @NotNull StundenplanKurs kursGetByIdOrException(final long idKurs) {
		return DeveloperNotificationException.ifMapGetIsNull(_kurs_by_id, idKurs);
	}

	/**
	 * Liefert TRUE, falls der übergebene Kurs am (Wochentyp / Wochentag / Unterrichtsstunde) stattfindet.
	 * <br>Laufzeit: O(|Unterrichtmenge des Kurses|)
	 *
	 * @param idKurs            Die Datenbank-ID des Kurses.
	 * @param wochentyp         Der Typ der Woche (beispielsweise bei AB-Wochen).
	 * @param wochentag         Der gewünschte {@link Wochentag}.
	 * @param unterrichtstunde  Die gewünschte Unterrichtsstunde.
	 *
	 * @return TRUE, falls der übergebene Kurs am (wochentyp / wochentag / Unterrichtsstunde) stattfindet.
	 */
	public boolean kursGetHatUnterrichtAm(final long idKurs, final int wochentyp, final @NotNull Wochentag wochentag, final int unterrichtstunde) {
		for (final @NotNull StundenplanUnterricht u : unterrichtGetMengeByKursIdAndWochentyp(idKurs, wochentyp)) {
			final @NotNull StundenplanZeitraster z =  zeitrasterGetByIdOrException(u.idZeitraster);
			if ((z.wochentag == wochentag.id) && (z.unterrichtstunde == unterrichtstunde))
				return true;
		}
		return false;
	}

	/**
	 * Liefert eine Liste aller {@link StundenplanKurs}-Objekte.
	 * <br>Laufzeit: O(1)
	 *
	 * @return eine Liste aller {@link StundenplanKurs}-Objekte.
	 */
	public @NotNull List<@NotNull StundenplanKurs> kursGetMengeAsList() {
		return _kursmenge;
	}

	/**
	 * Liefert eine Liste aller {@link StundenplanKurs}-Objekte der Klasse.
	 * <br> Laufzeit: O(1)
	 *
	 * @param idKlasse  Die Datenbank-ID der Klasse.
	 *
	 * @return eine Liste aller {@link StundenplanKurs}-Objekte der Klasse.
	 */
	public @NotNull List<@NotNull StundenplanKurs> kursGetMengeByKlasseIdAsList(final long idKlasse) {
		return MapUtils.getOrCreateArrayList(_kursmenge_by_idKlasse, idKlasse);
	}

	/**
	 * Liefert eine Liste aller {@link StundenplanKurs}-Objekte des Lehrers.
	 * <br> Laufzeit: O(1)
	 *
	 * @param idLehrer  Die Datenbank-ID des Lehrers.
	 *
	 * @return eine Liste aller {@link StundenplanKurs}-Objekte des Lehrers.
	 */
	public @NotNull List<@NotNull StundenplanKurs> kursGetMengeByLehrerIdAsList(final long idLehrer) {
		return MapUtils.getOrCreateArrayList(_kursmenge_by_idLehrer, idLehrer);
	}

	/**
	 * Liefert eine Liste aller {@link StundenplanKurs}-Objekte des Schülers.
	 * <br> Laufzeit: O(1)
	 *
	 * @param idSchueler  Die Datenbank-ID des Schülers.
	 *
	 * @return eine Liste aller {@link StundenplanKurs}-Objekte des Schülers.
	 */
	public @NotNull List<@NotNull StundenplanKurs> kursGetMengeBySchuelerIdAsList(final long idSchueler) {
		return MapUtils.getOrCreateArrayList(_kursmenge_by_idSchueler, idSchueler);
	}

	/**
	 * Liefert gefilterte Kurs-IDs, deren Unterricht zu (Wochentyp / Wochentag / Unterrichtsstunde) passt.
	 *
	 * @param idsKurs          Die Liste aller Kurs-IDs.
	 * @param wochentyp        Der Typ der Woche (beispielsweise bei AB-Wochen).
	 * @param wochentag        Der gewünschte {@link Wochentag}.
	 * @param unterrichtstunde Die gewünschte Unterrichtsstunde.
	 *
	 * @return gefilterte Kurs-IDs, deren Unterricht zu (Wochentyp / Wochentag / Unterrichtsstunde) passt.
	 */
	public @NotNull List<@NotNull Long> kursGetMengeGefiltertByWochentypAndWochentagAndStunde(final @NotNull List<@NotNull Long> idsKurs, final int wochentyp, final @NotNull Wochentag wochentag, final int unterrichtstunde) {
		return CollectionUtils.toFilteredArrayList(idsKurs, (final @NotNull Long idKurs) -> kursGetHatUnterrichtAm(idKurs, wochentyp, wochentag, unterrichtstunde));
	}

	/**
	 * Liefert gefilterte Kurs-IDs, deren Unterricht zu (Datum / Unterrichtsstunde) passt.
	 *
	 * @param idsKurs          Die Liste aller Kurs-IDs.
	 * @param datumISO8601     Das Datum. Daraus ergibt sich (Wochentyp / Wochentag).
	 * @param unterrichtstunde Die gewünschte Unterrichtsstunde.
	 *
	 * @return gefilterte Kurs-IDs, deren Unterricht zu (Datum / Unterrichtsstunde) passt.
	 */
	public @NotNull List<@NotNull Long> kursGetMengeGefiltertByDatumAndStunde(final @NotNull List<@NotNull Long> idsKurs, final @NotNull String datumISO8601, final int unterrichtstunde) {
		final int[] e = DateUtils.extractFromDateISO8601(datumISO8601);
		final int wochentyp = kalenderwochenzuordnungGetWochentypOrDefault(e[6], e[5]); // 6 = kalenderwochenjahr, 5 = kalenderwoche
		final @NotNull Wochentag wochentag = Wochentag.fromIDorException(e[3]); // 3 = tagInWoche
		return kursGetMengeGefiltertByWochentypAndWochentagAndStunde(idsKurs, wochentyp, wochentag, unterrichtstunde);
	}

	/**
	 * Liefert die IST-Wochenstunden des {@link StundenplanKurs}.
	 * <br>Hinweis: Durch AB-Wochen, ist der Rückgabewert eine Kommazahl, da nur Stundenanteile gesetzt sein können.
	 * <br>Laufzeit: O(|Unterrichte des Kurses|)
	 *
	 * @param idKurs  Die Datenbank-ID des Kurses.
	 *
	 * @return die IST-Wochenstunden des {@link StundenplanKlassenunterricht}.
	 */
	public double kursGetWochenstundenIst(final long idKurs) {
		double summe = 0;
		final double faktor = (_stundenplanWochenTypModell == 0) ? 1 : _stundenplanWochenTypModell;

		final @NotNull List<@NotNull StundenplanUnterricht> listU = DeveloperNotificationException.ifMapGetIsNull(_unterrichtmenge_by_idKurs, idKurs);
		for (final @NotNull  StundenplanUnterricht u : listU)
			summe += (u.wochentyp == 0) ? faktor : 1;

		return ((int) (summe * 100.0 / faktor)) / 100.0; // Auf 2 Nachkommastellen runden.
	}

	/**
	 * Liefert die Wochenstunden des Kurses.
	 * <br>Laufzeit: O(1)
	 *
	 * @param idKurs  Die Datenbank-ID des Kurses.
	 *
	 * @return die Wochenstunden des Kurses.
	 */
	public int kursGetWochenstundenSoll(final long idKurs) {
		return DeveloperNotificationException.ifMapGetIsNull(_kurs_by_id, idKurs).wochenstunden;
	}

	/**
	 * Aktualisiert das vorhandene {@link StundenplanKurs}-Objekt durch das neue Objekt.
	 * <br>Die folgenden Attribute werden nicht aktualisiert:
	 * <br>{@link StundenplanKurs#id}
	 * <br>
	 * <br>Die folgenden Attribute werden kopiert:
	 * <br>{@link StundenplanKurs#bezeichnung}
	 * <br>{@link StundenplanKurs#wochenstunden}
	 * <br>{@link StundenplanKurs#jahrgaenge}
	 * <br>{@link StundenplanKurs#schienen}
	 * <br>{@link StundenplanKurs#schueler}
	 *
	 * @param kurs  Das neue {@link StundenplanKurs}-Objekt, dessen Attribute kopiert werden.
	 */
	public void kursPatchAttributtes(final @NotNull StundenplanKurs kurs) {
		kursCheck(kurs);

		// Altes Objekt durch neues Objekt ersetzen
		DeveloperNotificationException.ifMapRemoveFailes(_kurs_by_id, kurs.id);
		DeveloperNotificationException.ifMapPutOverwrites(_kurs_by_id, kurs.id, kurs);

		kursRevalidate();
	}

	private void kursRemoveOhneUpdateById(final long idKurs) {
		// Kaskade: StundenplanUnterricht (des Kurses)
		for (final @NotNull StundenplanUnterricht u : DeveloperNotificationException.ifMapGetIsNull(_unterrichtmenge_by_idKurs, idKurs))
			unterrichtRemoveByIdOhneUpdate(u.id);

		// Entfernen
		DeveloperNotificationException.ifMapRemoveFailes(_kurs_by_id, idKurs);
	}

	/**
	 * Entfernt ein {@link StundenplanKurs}-Objekt anhand seiner ID.
	 *
	 * @param idKurs  Die Datenbank-ID des {@link StundenplanKurs}-Objekts, welches entfernt werden soll.
	 */
	public void kursRemoveById(final long idKurs) {
		kursRemoveOhneUpdateById(idKurs);
		kursRevalidate();
		unterrichtRevalidate();
	}

	/**
	 * Entfernt alle {@link StundenplanKurs}-Objekte.
	 *
	 * @param listKurs  Die Liste der zu entfernenden {@link StundenplanKurs}-Objekte.
	 */
	public void kursRemoveAll(final @NotNull List<@NotNull StundenplanKurs> listKurs) {
		for (final @NotNull StundenplanKurs kurs : listKurs)
			kursRemoveOhneUpdateById(kurs.id);
		kursRevalidate();
		unterrichtRevalidate();
	}

	// #####################################################################
	// #################### StundenplanLehrer ##############################
	// #####################################################################

	private void lehrerRevalidate() {
		update_lehrermenge();
		update_lehrermenge_by_idUnterricht();
		update_klassenunterrichtmenge_by_idLehrer();
		update_kursmenge_by_idLehrer();
	}

	private void lehrerAddOhneUpdate(final @NotNull StundenplanLehrer lehrer) {
		lehrerCheck(lehrer);
		DeveloperNotificationException.ifMapPutOverwrites(_lehrer_by_id, lehrer.id, lehrer);
	}

	/**
	 * Fügt ein {@link StundenplanLehrer}-Objekt hinzu.
	 *
	 * @param lehrer  Das {@link StundenplanLehrer}-Objekt, welches hinzugefügt werden soll.
	 */
	public void lehrerAdd(final @NotNull StundenplanLehrer lehrer) {
		lehrerAddOhneUpdate(lehrer);
		lehrerRevalidate();
	}

	/**
	 * Fügt alle {@link StundenplanLehrer}-Objekte hinzu.
	 *
	 * @param listLehrer  Die Menge der {@link StundenplanLehrer}-Objekte, welche hinzugefügt werden soll.
	 */
	public void lehrerAddAll(final @NotNull List<@NotNull StundenplanLehrer> listLehrer) {
		for (final @NotNull StundenplanLehrer lehrer : listLehrer)
			lehrerAddOhneUpdate(lehrer);
		lehrerRevalidate();
	}

	private void lehrerCheck(final @NotNull StundenplanLehrer lehrer) {
		DeveloperNotificationException.ifInvalidID("lehrer.id", lehrer.id);
		DeveloperNotificationException.ifStringIsBlank("lehrer.kuerzel", lehrer.kuerzel);
		DeveloperNotificationException.ifStringIsBlank("lehrer.nachname", lehrer.nachname);
		DeveloperNotificationException.ifStringIsBlank("lehrer.vorname", lehrer.vorname);
		for (final @NotNull Long idFachDesLehrers : lehrer.faecher)
			DeveloperNotificationException.ifMapNotContains("_fach_by_id", _fach_by_id, idFachDesLehrers);
	}

	/**
	 * Liefert das {@link StundenplanLehrer}-Objekt mit der übergebenen ID.
	 * <br>Laufzeit: O(1)
	 *
	 * @param idLehrer  Die Datenbank-ID des {@link StundenplanLehrer}-Objekts.
	 *
	 * @return das {@link StundenplanLehrer}-Objekt mit der übergebenen ID.
	 */
	public @NotNull StundenplanLehrer lehrerGetByIdOrException(final long idLehrer) {
		return DeveloperNotificationException.ifMapGetIsNull(_lehrer_by_id, idLehrer);
	}


	/**
	 * Liefert eine Liste aller {@link StundenplanLehrer}-Objekte.
	 * <br>Laufzeit: O(1)
	 *
	 * @return eine Liste aller {@link StundenplanLehrer}-Objekte.
	 */
	public @NotNull List<@NotNull StundenplanLehrer> lehrerGetMengeAsList() {
		return _lehrermenge;
	}

	/**
	 * Aktualisiert das vorhandene {@link StundenplanLehrer}-Objekt durch das neue Objekt.
	 * <br>Die folgenden Attribute werden nicht aktualisiert:
	 * <br>{@link StundenplanLehrer#id}
	 * <br>
	 * <br>Die folgenden Attribute werden kopiert:
	 * <br>{@link StundenplanLehrer#kuerzel}
	 * <br>{@link StundenplanLehrer#nachname}
	 * <br>{@link StundenplanLehrer#vorname}
	 * <br>{@link StundenplanLehrer#faecher}
	 *
	 * @param lehrer  Das neue {@link StundenplanLehrer}-Objekt, dessen Attribute kopiert werden.
	 */
	public void lehrerPatchAttributes(final @NotNull StundenplanLehrer lehrer) {
		lehrerCheck(lehrer);

		// Altes Objekt durch neues Objekt ersetzen
		DeveloperNotificationException.ifMapRemoveFailes(_lehrer_by_id, lehrer.id);
		DeveloperNotificationException.ifMapPutOverwrites(_lehrer_by_id, lehrer.id, lehrer);

		lehrerRevalidate();
	}

	private void lehrerRemoveOhneUpdateById(final long idLehrer) {
		DeveloperNotificationException.ifMapRemoveFailes(_lehrer_by_id, idLehrer);
	}

	/**
	 * Entfernt ein {@link StundenplanLehrer}-Objekt anhand seiner ID.
	 *
	 * @param idLehrer  Die Datenbank-ID des {@link StundenplanLehrer}-Objekts, welches entfernt werden soll.
	 */
	public void lehrerRemoveById(final long idLehrer) {
		lehrerRemoveOhneUpdateById(idLehrer);
		lehrerRevalidate();
		unterrichtRevalidate();
	}

	/**
	 * Entfernt alle {@link StundenplanLehrer}-Objekte.
	 *
	 * @param listLehrer  Die Liste der zu entfernenden {@link StundenplanLehrer}-Objekte.
	 */
	public void lehrerRemoveAll(final @NotNull List<@NotNull StundenplanLehrer> listLehrer) {
		for (final @NotNull StundenplanLehrer lehrer : listLehrer)
			lehrerRemoveOhneUpdateById(lehrer.id);
		lehrerRevalidate();
		unterrichtRevalidate();
	}

	// #####################################################################
	// #################### StundenplanPausenaufsicht ######################
	// #####################################################################

	private void pausenaufsichtRevalidate() {
		update_pausenaufsichtmenge();
		update_pausenaufsichtmenge_by_idPausenzeit();
		update_pausenaufsichtmenge_by_wochentag();
		update_pausenzeitmengeOhnePausenaufsicht();
	}

	private void pausenaufsichtAddOhneUpdate(final @NotNull StundenplanPausenaufsicht pausenaufsicht) {
		pausenaufsichtCheck(pausenaufsicht);
		DeveloperNotificationException.ifMapPutOverwrites(_pausenaufsicht_by_id, pausenaufsicht.id, pausenaufsicht);
	}

	/**
	 * Fügt ein {@link StundenplanPausenaufsicht}-Objekt hinzu.
	 *
	 * @param pausenaufsicht  Das {@link StundenplanPausenaufsicht}-Objekt, welches hinzugefügt werden soll.
	 */
	public void pausenaufsichtAdd(final @NotNull StundenplanPausenaufsicht pausenaufsicht) {
		pausenaufsichtAddOhneUpdate(pausenaufsicht);
		pausenaufsichtRevalidate();
	}

	/**
	 * Fügt alle {@link StundenplanPausenaufsicht}-Objekte hinzu.
	 *
	 * @param listPausenaufsicht  Die Menge der {@link StundenplanPausenaufsicht}-Objekte, welche hinzugefügt werden soll.
	 */
	private void pausenaufsichtAddAll(final @NotNull List<@NotNull StundenplanPausenaufsicht> listPausenaufsicht) {
		for (final @NotNull StundenplanPausenaufsicht pausenaufsicht : listPausenaufsicht)
			pausenaufsichtAddOhneUpdate(pausenaufsicht);
		pausenaufsichtRevalidate();
	}

	private void pausenaufsichtCheck(final @NotNull StundenplanPausenaufsicht pausenaufsicht) {
		DeveloperNotificationException.ifInvalidID("pausenaufsicht.id", pausenaufsicht.id);
		DeveloperNotificationException.ifMapNotContains("_map_idLehrer_zu_lehrer", _lehrer_by_id, pausenaufsicht.idLehrer);
		DeveloperNotificationException.ifMapNotContains("_map_idPausenzeit_zu_pausenzeit", _pausenzeit_by_id, pausenaufsicht.idPausenzeit);
		DeveloperNotificationException.ifTrue("(pa.wochentyp > 0) && (pa.wochentyp > stundenplanWochenTypModell)", (pausenaufsicht.wochentyp > 0) && (pausenaufsicht.wochentyp > _stundenplanWochenTypModell));
		for (final @NotNull Long idAufsichtsbereich : pausenaufsicht.bereiche)
			DeveloperNotificationException.ifMapNotContains("_aufsichtsbereich_by_id", _aufsichtsbereich_by_id, idAufsichtsbereich);
	}

	/**
	 * Liefert das zur ID zugehörige {@link StundenplanPausenaufsicht}-Objekt.
	 * <br>Laufzeit: O(1)
	 *
	 * @param idPausenaufsicht Die ID des angefragten-Objektes.
	 *
	 * @return das zur ID zugehörige {@link StundenplanPausenaufsicht}-Objekt.
	 */
	public @NotNull StundenplanPausenaufsicht pausenaufsichtGetByIdOrException(final long idPausenaufsicht) {
		return DeveloperNotificationException.ifMapGetIsNull(_pausenaufsicht_by_id, idPausenaufsicht);
	}

	/**
	 * Liefert eine sortierte Liste aller {@link StundenplanPausenaufsicht}-Objekte.
	 * <br> Laufzeit: O(1)
	 *
	 * @return eine sortierte Liste aller {@link StundenplanPausenaufsicht}-Objekte.
	 */
	public @NotNull List<@NotNull StundenplanPausenaufsicht> pausenaufsichtGetMengeAsList() {
		return _pausenaufsichtmenge;
	}

	/**
	 * Liefert eine Liste aller {@link StundenplanPausenaufsicht}-Objekte eines bestimmten Wochentages.
	 * <br> Laufzeit: O(1)
	 *
	 * @param wochentag  Die ID des ENUMS {@link Wochentag}.
	 *
	 * @return eine Liste aller {@link StundenplanPausenaufsicht}-Objekte eines bestimmten Wochentages.
	 */
	public @NotNull List<@NotNull StundenplanPausenaufsicht> pausenaufsichtGetMengeByWochentagOrEmptyList(final int wochentag) {
		return MapUtils.getOrCreateArrayList(_pausenaufsichtmenge_by_wochentag, wochentag);
	}

	/**
	 * Aktualisiert das vorhandene {@link StundenplanPausenaufsicht}-Objekt durch das neue Objekt.
	 * <br>Die folgenden Attribute werden nicht aktualisiert:
	 * <br>{@link StundenplanPausenaufsicht#id}
	 * <br>
	 * <br>Die folgenden Attribute werden kopiert:
	 * <br>{@link StundenplanPausenaufsicht#idLehrer}
	 * <br>{@link StundenplanPausenaufsicht#idPausenzeit}
	 * <br>{@link StundenplanPausenaufsicht#wochentyp}
	 * <br>{@link StundenplanPausenaufsicht#bereiche}
	 *
	 * @param pausenaufsicht  Das neue {@link StundenplanPausenaufsicht}-Objekt, dessen Attribute kopiert werden.
	 */
	public void pausenaufsichtPatchAttributes(final @NotNull StundenplanPausenaufsicht pausenaufsicht) {
		pausenaufsichtCheck(pausenaufsicht);

		// Altes Objekt durch neues Objekt ersetzen
		DeveloperNotificationException.ifMapRemoveFailes(_pausenaufsicht_by_id, pausenaufsicht.id);
		DeveloperNotificationException.ifMapPutOverwrites(_pausenaufsicht_by_id, pausenaufsicht.id, pausenaufsicht);

		pausenaufsichtRevalidate();
	}

	private void pausenaufsichtRemoveOhneUpdateById(final long idPausenaufsicht) {
		DeveloperNotificationException.ifMapRemoveFailes(_pausenaufsicht_by_id, idPausenaufsicht);
	}

	/**
	 * Entfernt aus dem Stundenplan eine existierendes {@link StundenplanPausenaufsicht}-Objekt.
	 * <br>Laufzeit: O(|StundenplanPausenaufsicht|), da pausenaufsichtUpdate() aufgerufen wird.
	 *
	 * @param idPausenaufsicht  Die ID des {@link StundenplanPausenaufsicht}-Objekts.
	 */
	public void pausenaufsichtRemoveById(final long idPausenaufsicht) {
		pausenaufsichtRemoveOhneUpdateById(idPausenaufsicht);
		pausenaufsichtRevalidate();
	}

	// #####################################################################
	// #################### StundenplanPausenzeit ##########################
	// #####################################################################

	private void pausenzeitRevalidate() {
		update_pausenzeitmenge();
		update_pausenzeitmenge_by_wochentag();
		update_pausenaufsichtmenge_by_idPausenzeit();
		update_pausenzeitmengeOhnePausenaufsicht();
	}

	private void pausenzeitAddOhneUpdate(final @NotNull StundenplanPausenzeit pausenzeit) {
		pausenzeitCheck(pausenzeit);
		DeveloperNotificationException.ifMapPutOverwrites(_pausenzeit_by_id, pausenzeit.id, pausenzeit);
	}

	/**
	 * Fügt ein {@link StundenplanPausenzeit}-Objekt hinzu.
	 *
	 * @param pausenzeit  Das {@link StundenplanPausenzeit}-Objekt, welches hinzugefügt werden soll.
	 */
	public void pausenzeitAdd(final @NotNull StundenplanPausenzeit pausenzeit) {
		pausenzeitAddOhneUpdate(pausenzeit);
		pausenzeitRevalidate();
	}

	/**
	 * Fügt alle {@link StundenplanPausenzeit}-Objekte hinzu.
	 *
	 * @param listPausenzeit  Die Menge der {@link StundenplanPausenzeit}-Objekte, welche hinzugefügt werden soll.
	 */
	public void pausenzeitAddAll(final @NotNull List<@NotNull StundenplanPausenzeit> listPausenzeit) {
		for (final @NotNull StundenplanPausenzeit pausenzeit : listPausenzeit)
			pausenzeitAddOhneUpdate(pausenzeit);
		pausenzeitRevalidate();
	}

	private static void pausenzeitCheck(final @NotNull StundenplanPausenzeit pausenzeit) {
		DeveloperNotificationException.ifInvalidID("pause.id", pausenzeit.id);
		Wochentag.fromIDorException(pausenzeit.wochentag);
		if ((pausenzeit.beginn != null) && (pausenzeit.ende != null))
			DeveloperNotificationException.ifTrue("pausenzeit.beginn >= pausenzeit.ende", pausenzeit.beginn >= pausenzeit.ende);
	}

	/**
	 * Liefert das zur ID zugehörige {@link StundenplanPausenzeit}-Objekt.
	 * <br>Laufzeit: O(1)
	 *
	 * @param idPausenzeit Die ID des angefragten-Objektes.
	 *
	 * @return das zur ID zugehörige {@link StundenplanPausenzeit}-Objekt.
	 */
	public @NotNull StundenplanPausenzeit pausenzeitGetByIdOrException(final long idPausenzeit) {
		return DeveloperNotificationException.ifMapGetIsNull(_pausenzeit_by_id, idPausenzeit);
	}

	/**
	 * Liefert die Beginn-Uhrzeit der {@link StundenplanPausenzeit} oder den leeren String, falls diese NULL ist.
	 * <br>Beispiel: "09:30" oder ""
	 * <br>Laufzeit: O(1)
	 *
	 * @param idPausenzeit  Die Datenbank-ID des {@link StundenplanPausenzeit}.
	 *
	 * @return die Beginn-Uhrzeit der {@link StundenplanPausenzeit} oder den leeren String, falls diese NULL ist.
	 */
	public @NotNull String pausenzeitGetByIdStringOfUhrzeitBeginn(final long idPausenzeit) {
		final @NotNull StundenplanPausenzeit pausenzeit =  DeveloperNotificationException.ifMapGetIsNull(_pausenzeit_by_id, idPausenzeit);
		return (pausenzeit.beginn == null) ? "" : DateUtils.getStringOfUhrzeitFromMinuten(pausenzeit.beginn);
	}

	/**
	 * Liefert die End-Uhrzeit der {@link StundenplanPausenzeit} oder den leeren String, falls diese NULL ist.
	 * <br>Beispiel: "10:15" oder ""
	 * <br>Laufzeit: O(1)
	 *
	 * @param idPausenzeit  Die Datenbank-ID des {@link StundenplanPausenzeit}.
	 *
	 * @return die End-Uhrzeit der {@link StundenplanPausenzeit} oder den leeren String, falls diese NULL ist.
	 */
	public @NotNull String pausenzeitGetByIdStringOfUhrzeitEnde(final long idPausenzeit) {
		final @NotNull StundenplanPausenzeit pausenzeit =  DeveloperNotificationException.ifMapGetIsNull(_pausenzeit_by_id, idPausenzeit);
		return (pausenzeit.ende == null) ? "" : DateUtils.getStringOfUhrzeitFromMinuten(pausenzeit.ende);
	}

	/**
	 * Liefert eine Liste aller {@link StundenplanPausenzeit}-Objekte.
	 * <br>Laufzeit: O(1)
	 *
	 * @return eine Liste aller {@link StundenplanPausenzeit}-Objekte.
	 */
	public @NotNull List<@NotNull StundenplanPausenzeit> pausenzeitGetMengeAsList() {
		return _pausenzeitmenge;
	}

	/**
	 * Liefert eine Liste aller {@link StundenplanPausenzeit}-Objekte eines bestimmten Wochentages, oder eine leere Liste.
	 * <br> Laufzeit: O(1)
	 *
	 * @param wochentag  Die ID des ENUMS {@link Wochentag}.
	 *
	 * @return eine Liste aller {@link StundenplanPausenzeit}-Objekte eines bestimmten Wochentages, oder eine leere Liste.
	 */
	public @NotNull List<@NotNull StundenplanPausenzeit> pausenzeitGetMengeByWochentagOrEmptyList(final int wochentag) {
		return MapUtils.getOrCreateArrayList(_pausenzeitmenge_by_wochentag, wochentag);
	}

	/**
	 * Liefert eine Liste aller {@link StundenplanPausenzeit}-Objekte, die mindestens eine {@link StundenplanPausenaufsicht} beinhalten.
	 * <br> Laufzeit: O(1)
	 *
	 * @return eine Liste aller {@link StundenplanPausenzeit}-Objekte, die mindestens eine {@link StundenplanPausenaufsicht} beinhalten.
	 */
	public @NotNull List<@NotNull StundenplanPausenzeit> pausenzeitGetMengeNichtLeereAsList() {
		return _pausenzeitmengeOhneLeere;
	}

	/**
	 * Liefert das Minimum aller {@link StundenplanPausenzeit#beginn}-Objekte, oder 480 (8 Uhr) falls keines vorhanden ist.
	 * <br>Laufzeit: O(1)
	 *
	 * @return das Minimum aller {@link StundenplanPausenzeit#beginn}-Objekte, oder 480 (8 Uhr) falls keines vorhanden ist.
	 */
	public int pausenzeitGetMinutenMin() {
		return _pausenzeitMinutenMin == null ? 480 : _pausenzeitMinutenMin;
	}

	/**
	 * Liefert das Maximum aller {@link StundenplanPausenzeit#ende}-Objekte, oder 480 (8 Uhr) falls keines vorhanden ist.
	 * <br>Laufzeit: O(1)
	 *
	 * @return das Maximum aller {@link StundenplanPausenzeit#ende}-Objekte, oder 480 (8 Uhr) falls keines vorhanden ist.
	 */
	public int pausenzeitGetMinutenMax() {
		return _pausenzeitMinutenMax == null ? 480 : _pausenzeitMinutenMax;
	}

	/**
	 * Liefert das Minimum aller {@link StundenplanPausenzeit#beginn}-Objekte und aller {@link StundenplanZeitraster#stundenbeginn}-Objekte, oder 480 (8 Uhr) falls keines vorhanden ist.
	 * <br>Laufzeit: O(1)
	 *
	 * @return das Minimum aller {@link StundenplanPausenzeit#beginn}-Objekte und aller {@link StundenplanZeitraster#stundenbeginn}-Objekte, oder 480 (8 Uhr) falls keines vorhanden ist.
	 */
	public int pausenzeitUndZeitrasterGetMinutenMin() {
		final Integer min = BlockungsUtils.minII(_pausenzeitMinutenMin, _zeitrasterMinutenMin);
		return min == null ? 480 : min;
	}

	/**
	 * Liefert das Minimum aller nicht leeren {@link StundenplanPausenzeit#beginn}-Objekte und aller {@link StundenplanZeitraster#stundenbeginn}-Objekte, oder 480 (8 Uhr) falls keines vorhanden ist.
	 * <br>Laufzeit: O(1)
	 *
	 * @return das Minimum aller nicht leeren {@link StundenplanPausenzeit#beginn}-Objekte und aller {@link StundenplanZeitraster#stundenbeginn}-Objekte, oder 480 (8 Uhr) falls keines vorhanden ist.
	 */
	public int pausenzeitUndZeitrasterGetMinutenMinOhneLeere() {
		final Integer min = BlockungsUtils.minII(_pausenzeitMinutenMinOhneLeere, _zeitrasterMinutenMinOhneLeere);
		return min == null ? 480 : min;
	}

	/**
	 * Liefert das Maximum aller {@link StundenplanPausenzeit#ende}-Objekte und aller {@link StundenplanZeitraster#stundenende}-Objekte, oder 480 (8 Uhr) falls keines vorhanden ist.
	 * <br>Laufzeit: O(1)
	 *
	 * @return das Maximum aller {@link StundenplanPausenzeit#ende}-Objekte und aller {@link StundenplanZeitraster#stundenende}-Objekte, oder 480 (8 Uhr) falls keines vorhanden ist.
	 */
	public int pausenzeitUndZeitrasterGetMinutenMax() {
		final Integer max = BlockungsUtils.maxII(_pausenzeitMinutenMax, _zeitrasterMinutenMax);
		return max == null ? 480 : max;
	}

	/**
	 * Liefert das Maximum aller nicht leeren {@link StundenplanPausenzeit#ende}-Objekte und aller {@link StundenplanZeitraster#stundenende}-Objekte, oder 480 (8 Uhr) falls keines vorhanden ist.
	 * <br>Laufzeit: O(1)
	 *
	 * @return das Maximum aller nicht leeren {@link StundenplanPausenzeit#ende}-Objekte und aller {@link StundenplanZeitraster#stundenende}-Objekte, oder 480 (8 Uhr) falls keines vorhanden ist.
	 */
	public int pausenzeitUndZeitrasterGetMinutenMaxOhneLeere() {
		final Integer max = BlockungsUtils.maxII(_pausenzeitMinutenMaxOhneLeere, _zeitrasterMinutenMaxOhneLeere);
		return max == null ? 480 : max;
	}

	/**
	 * Aktualisiert das vorhandene {@link StundenplanPausenzeit}-Objekt durch das neue Objekt.
	 * <br>Die folgenden Attribute werden nicht aktualisiert:
	 * <br>{@link StundenplanPausenzeit#id}
	 * <br>
	 * <br>Die folgenden Attribute werden kopiert:
	 * <br>{@link StundenplanPausenzeit#beginn}
	 * <br>{@link StundenplanPausenzeit#bezeichnung}
	 * <br>{@link StundenplanPausenzeit#ende}
	 * <br>{@link StundenplanPausenzeit#wochentag}
	 *
	 * @param pausenzeit  Das neue {@link StundenplanPausenzeit}-Objekt, dessen Attribute kopiert werden.
	 */
	public void pausenzeitPatchAttributes(final @NotNull StundenplanPausenzeit pausenzeit) {
		pausenzeitCheck(pausenzeit);

		// Altes Objekt durch neues Objekt ersetzen
		DeveloperNotificationException.ifMapRemoveFailes(_pausenzeit_by_id, pausenzeit.id);
		DeveloperNotificationException.ifMapPutOverwrites(_pausenzeit_by_id, pausenzeit.id, pausenzeit);

		pausenzeitRevalidate();
	}

	private void pausenzeitRemoveOhneUpdateById(final long idPausenzeit) {
		// Kaskade: StundenplanPausenaufsicht (der Pausenzeit)
		for (final @NotNull StundenplanPausenaufsicht a : DeveloperNotificationException.ifMapGetIsNull(_pausenaufsichtmenge_by_idPausenzeit, idPausenzeit))
			pausenaufsichtRemoveOhneUpdateById(a.id);

		// Entfernen
		DeveloperNotificationException.ifMapRemoveFailes(_pausenzeit_by_id, idPausenzeit);
	}

	/**
	 * Entfernt aus dem Stundenplan eine existierendes {@link StundenplanPausenzeit}-Objekt.
	 *
	 * @param idPausenzeit  Die ID des {@link StundenplanPausenzeit}-Objekts.
	 */
	public void pausenzeitRemoveById(final long idPausenzeit) {
		pausenzeitRemoveOhneUpdateById(idPausenzeit);
		pausenzeitRevalidate();
		pausenaufsichtRevalidate();
	}

	/**
	 * Entfernt alle {@link StundenplanPausenzeit}-Objekte.
	 *
	 * @param listPausenzeit  Die Liste der zu entfernenden {@link StundenplanPausenzeit}-Objekte.
	 */
	public void pausenzeitRemoveAll(final @NotNull List<@NotNull StundenplanPausenzeit> listPausenzeit) {
		for (final @NotNull StundenplanPausenzeit pausenzeit : listPausenzeit)
			pausenzeitRemoveOhneUpdateById(pausenzeit.id);
		pausenzeitRevalidate();
		pausenaufsichtRevalidate();
	}

	// #####################################################################
	// #################### StundenplanRaum ################################
	// #####################################################################

	private void raumRevalidate() {
		update_raummenge();
	}

	private void raumAddOhneUpdate(final @NotNull StundenplanRaum raum) {
		raumCheck(raum);
		DeveloperNotificationException.ifMapPutOverwrites(_raum_by_id, raum.id, raum);
	}

	/**
	 * Fügt ein {@link StundenplanRaum}-Objekt hinzu.
	 *
	 * @param raum  Das {@link StundenplanRaum}-Objekt, welches hinzugefügt werden soll.
	 */
	public void raumAdd(final @NotNull StundenplanRaum raum) {
		raumAddOhneUpdate(raum);
		raumRevalidate();
	}

	/**
	 * Fügt alle {@link StundenplanRaum}-Objekte hinzu.
	 *
	 * @param listRaum  Die Menge der {@link StundenplanRaum}-Objekte, welche hinzugefügt werden soll.
	 */
	public void raumAddAll(final @NotNull List<@NotNull StundenplanRaum> listRaum) {
		for (final @NotNull StundenplanRaum raum : listRaum)
			raumAddOhneUpdate(raum);
		raumRevalidate();
	}

	private static void raumCheck(final @NotNull StundenplanRaum raum) {
		DeveloperNotificationException.ifInvalidID("raum.id", raum.id);
		DeveloperNotificationException.ifStringIsBlank("raum.kuerzel", raum.kuerzel);
		// raum.beschreibung darf "blank" sein!
		DeveloperNotificationException.ifTrue("raum.groesse < 0", raum.groesse < 0);
	}

	/**
	 * Liefert das zur ID zugehörige {@link StundenplanRaum}-Objekt.
	 * <br>Laufzeit: O(1)
	 *
	 * @param idRaum Die ID des angefragten-Objektes.
	 *
	 * @return das zur ID zugehörige {@link StundenplanRaum}-Objekt.
	 */
	public @NotNull StundenplanRaum raumGetByIdOrException(final long idRaum) {
		return DeveloperNotificationException.ifMapGetIsNull(_raum_by_id, idRaum);
	}

	/**
	 * Liefert eine Liste aller {@link StundenplanRaum}-Objekte.
	 * <br>Laufzeit: O(1)
	 *
	 * @return eine Liste aller {@link StundenplanRaum}-Objekte.
	 */
	public @NotNull List<@NotNull StundenplanRaum> raumGetMengeAsList() {
		return _raummenge;
	}

	/**
	 * Aktualisiert das vorhandene {@link StundenplanRaum}-Objekt durch das neue Objekt.
	 * <br>Die folgenden Attribute werden nicht aktualisiert:
	 * <br>{@link StundenplanRaum#id}
	 * <br>
	 * <br>Die folgenden Attribute werden kopiert:
	 * <br>{@link StundenplanRaum#beschreibung}
	 * <br>{@link StundenplanRaum#groesse}
	 * <br>{@link StundenplanRaum#kuerzel}
	 *
	 * @param raum  Das neue {@link StundenplanRaum}-Objekt, dessen Attribute kopiert werden.
	 */
	public void raumPatchAttributes(final @NotNull StundenplanRaum raum) {
		raumCheck(raum);

		// Altes Objekt durch neues Objekt ersetzen
		DeveloperNotificationException.ifMapRemoveFailes(_raum_by_id, raum.id);
		DeveloperNotificationException.ifMapPutOverwrites(_raum_by_id, raum.id, raum);

		raumRevalidate();
	}

	private void raumRemoveOhneUpdateById(final long idRaum) {
		DeveloperNotificationException.ifMapRemoveFailes(_raum_by_id, idRaum);
	}

	/**
	 * Entfernt aus dem Stundenplan eine existierendes {@link StundenplanRaum}-Objekt.
	 *
	 * @param idRaum  Die ID des {@link StundenplanRaum}-Objekts.
	 */
	public void raumRemoveById(final long idRaum) {
		raumRemoveOhneUpdateById(idRaum);
		raumRevalidate();
	}

	/**
	 * Entfernt alle {@link StundenplanRaum}-Objekte.
	 *
	 * @param listRaum  Die Liste der zu entfernenden {@link StundenplanRaum}-Objekte.
	 */
	public void raumRemoveAll(final @NotNull List<@NotNull StundenplanRaum> listRaum) {
		for (final @NotNull StundenplanRaum raum : listRaum)
			raumRemoveOhneUpdateById(raum.id);
		raumRevalidate();
	}

	// #####################################################################
	// #################### StundenplanSchiene #############################
	// #####################################################################

	private void schieneRevalidate() {
		update_schienenmenge();
	}

	private void schieneAddOhneUpdate(final @NotNull StundenplanSchiene schiene) {
		schieneCheck(schiene);
		DeveloperNotificationException.ifMapPutOverwrites(_schiene_by_id, schiene.id, schiene);
	}

	/**
	 * Fügt ein {@link StundenplanSchiene}-Objekt hinzu.
	 *
	 * @param schiene  Das {@link StundenplanSchiene}-Objekt, welches hinzugefügt werden soll.
	 */
	public void schieneAdd(final @NotNull StundenplanSchiene schiene) {
		schieneAddOhneUpdate(schiene);
		schieneRevalidate();
	}

	/**
	 * Fügt alle {@link StundenplanSchiene}-Objekte hinzu.
	 *
	 * @param listSchiene  Die Menge der {@link StundenplanSchiene}-Objekte, welche hinzugefügt werden soll.
	 */
	public void schieneAddAll(final @NotNull List<@NotNull StundenplanSchiene> listSchiene) {
		for (final @NotNull StundenplanSchiene schiene : listSchiene)
			schieneAddOhneUpdate(schiene);
		schieneRevalidate();
	}

	private void schieneCheck(final @NotNull StundenplanSchiene schiene) {
		DeveloperNotificationException.ifInvalidID("schiene.id", schiene.id);
		DeveloperNotificationException.ifTrue("schiene.nummer <= 0", schiene.nummer <= 0);
		DeveloperNotificationException.ifStringIsBlank("schiene.bezeichnung", schiene.bezeichnung);
		DeveloperNotificationException.ifMapNotContains("_jahrgang_by_id", _jahrgang_by_id, schiene.idJahrgang);
	}

	// #####################################################################
	// #################### StundenplanSchueler ############################
	// #####################################################################

	private void schuelerRevalidate() {
		update_schuelermenge();
		update_schuelermenge_by_idKlasse();
		update_schuelermenge_by_idKurs();
		update_klassenunterrichtmenge_by_idSchueler();
		update_kursmenge_by_idSchueler();
	}

	private void schuelerAddOhneUpdate(final @NotNull StundenplanSchueler schueler) {
		schuelerCheck(schueler);
		DeveloperNotificationException.ifMapPutOverwrites(_schueler_by_id, schueler.id, schueler);
	}

	/**
	 * Fügt ein {@link StundenplanSchueler}-Objekt hinzu.
	 *
	 * @param schueler  Das {@link StundenplanSchueler}-Objekt, welches hinzugefügt werden soll.
	 */
	public void schuelerAdd(final @NotNull StundenplanSchueler schueler) {
		schuelerAddOhneUpdate(schueler);
		schuelerRevalidate();
	}

	/**
	 * Fügt alle {@link StundenplanSchueler}-Objekte hinzu.
	 *
	 * @param listSchueler  Die Menge der {@link StundenplanSchueler}-Objekte, welche hinzugefügt werden soll.
	 */
	public void schuelerAddAll(final @NotNull List<@NotNull StundenplanSchueler> listSchueler) {
		for (final @NotNull StundenplanSchueler schueler : listSchueler)
			schuelerAddOhneUpdate(schueler);
		schuelerRevalidate();
	}

	private static void schuelerCheck(final @NotNull StundenplanSchueler schueler) {
		DeveloperNotificationException.ifInvalidID("schueler.id", schueler.id);
		DeveloperNotificationException.ifStringIsBlank("schueler.nachname", schueler.nachname);
		DeveloperNotificationException.ifStringIsBlank("schueler.vorname", schueler.vorname);
		// schueler.idKlasse nicht nötig, ein Schüler kann auch keine Klasse haben. Die Zuordnung erfolgt über StundenplanKlasse.
	}

	/**
	 * Liefert alle {@link StundenplanSchueler}-Objekte der Klasse.
	 * <br>Laufzeit: O(1)
	 *
	 * @param idKlasse  Die Datenbank-ID der Klasse.
	 *
	 * @return alle {@link StundenplanSchueler}-Objekte der Klasse.
	 */
	public @NotNull List<@NotNull StundenplanSchueler> schuelerGetMengeByKlasseIdAsListOrException(final long idKlasse) {
		return DeveloperNotificationException.ifMapGetIsNull(schuelermenge_by_idKlasse, idKlasse);
	}

	/**
	 * Liefert die Anzahl der {@link StundenplanSchueler}-Objekte der Klasse.
	 * <br>Laufzeit: O(1)
	 *
	 * @param idKlasse  Die Datenbank-ID der Klasse.
	 *
	 * @return die Anzahl der {@link StundenplanSchueler}-Objekte der Klasse.
	 */
	public int schuelerGetAnzahlByKlasseIdOrException(final long idKlasse) {
		return DeveloperNotificationException.ifMapGetIsNull(schuelermenge_by_idKlasse, idKlasse).size();
	}

	/**
	 * Liefert alle {@link StundenplanSchueler}-Objekte des Kurses.
	 * <br>Laufzeit: O(1)
	 *
	 * @param idKurs  Die Datenbank-ID des Kurses.
	 *
	 * @return alle {@link StundenplanSchueler}-Objekte des Kurses.
	 */
	public @NotNull List<@NotNull StundenplanSchueler> schuelerGetMengeByKursIdAsListOrException(final long idKurs) {
		return DeveloperNotificationException.ifMapGetIsNull(_schuelermenge_by_idKurs, idKurs);
	}

	/**
	 * Liefert die Anzahl der  {@link StundenplanSchueler}-Objekte des Kurses.
	 * <br>Laufzeit: O(1)
	 *
	 * @param idKurs  Die Datenbank-ID des Kurses.
	 *
	 * @return die Anzahl der  {@link StundenplanSchueler}-Objekte des Kurses.
	 */
	public int schuelerGetAnzahlByKursIdAsListOrException(final long idKurs) {
		return DeveloperNotificationException.ifMapGetIsNull(_schuelermenge_by_idKurs, idKurs).size();
	}

	// #####################################################################
	// #################### Stundenplan ####################################
	// #####################################################################

	/**
	 * Liefert die ID des Schuljahresabschnitts des Stundenplans.
	 *
	 * @return die ID des Schuljahresabschnitts des Stundenplans.
	 */
	public long getIDSchuljahresabschnitt() {
		return _stundenplanSchuljahresAbschnittID;
	}

	/**
	 * Liefert das Datum, ab dem der Stundenplan gültig ist.
	 *
	 * @return das Datum, ab dem der Stundenplan gültig ist.
	 */
	public @NotNull String getGueltigAb() {
		return _stundenplanGueltigAb;
	}

	/**
	 * Liefert das Datum, bis wann der Stundenplan gültig ist.
	 *
	 * @return das Datum, bis wann der Stundenplan gültig ist.
	 */
	public @NotNull String getGueltigBis() {
		return _stundenplanGueltigBis;
	}

	/**
	 * Liefert die textuelle Beschreibung des Stundenplans.
	 *
	 * @return die textuelle Beschreibung des Stundenplans.
	 */
	public @NotNull String getBezeichnungStundenplan() {
		return _stundenplanBezeichnung;
	}

	/**
	 * Liefert das (globale) Wochentyp-Modell für die Wochen des Stundenplans. <br>
	 * 0: Stundenplan gilt jede Woche. <br>
	 * 1: Ungültiger Wert. <br>
	 * N: Stundenplan wiederholt sich alle N Wochen. <br>
	 * <br>Laufzeit: O(1)
	 *
	 * @return das (globale) Wochentyp-Modell für die Wochen des Stundenplans.
	 */
	public int getWochenTypModell() {
		return _stundenplanWochenTypModell;
	}

	/**
	 * Liefert die Datenbank-ID des Schülers.<br>
	 * Wirft eine Exception, falls in den Daten nicht genau ein Schüler geladen wurde.
	 *
	 * @return  Die Datenbank-ID des Schülers.
	 */
	public long schuelerGetIDorException() {
		final int size = _schuelermenge.size();
		DeveloperNotificationException.ifTrue("getSchuelerID() geht nicht bei " + size + " Schülern!", size != 1);
		return _schuelermenge.get(0).id;
	}

	/**
	 * Liefert das (globale) Wochentyp-Modell für die Wochen des Stundenplans. <br>
	 * 0: Stundenplan gilt jede Woche. <br>
	 * 1: Ungültiger Wert. <br>
	 * N: Stundenplan wiederholt sich alle N Wochen. <br>
	 * <br>Laufzeit: O(1)
	 *
	 * @return das (globale) Wochentyp-Modell für die Wochen des Stundenplans.
	 */
	public int stundenplanGetWochenTypModell() {
		return _stundenplanWochenTypModell;
	}

	/**
	 * Liefert zum übergebenen Wochentyp einen passenden String.
	 * <br>Beispiel: 0 -> "Alle", 1 -> "A-Woche", ...
	 * <br>Laufzeit: O(1)
	 *
	 * @param wochenTyp  Der umzuwandelnde Wochentyp.
	 *
	 * @return zum übergebenen Wochentyp einen passenden String.
	 */
	public @NotNull String stundenplanGetWochenTypAsString(final int wochenTyp) {
		if (wochenTyp <= 0)
			return "Alle";

		final int zahl = wochenTyp - 1; // 0-basierter Wochentyp
		final int z2 = zahl / 26;       // 2. Stelle im 26. System
		final int z1 = zahl - z2 * 26;  // 1. Stelle im 26. System

		return StringUtils.numberToLetterIndex1(z2) + StringUtils.numberToLetterIndex0(z1) + "-Woche";
	}

	/**
	 * Liefert die Datenbank-ID des Stundenplans.
	 * <br>Laufzeit: O(1)
	 *
	 * @return die Datenbank-ID des Stundenplans.
	 */
	public long stundenplanGetID() {
		return _stundenplanID;
	}

	// #####################################################################
	// #################### StundenplanUnterricht ##########################
	// #####################################################################

	private void unterrichtRevalidate() {
		update_unterrichtmenge();
		update_unterrichtmenge_by_idKlasse();
		update_unterrichtmenge_by_idKlasse_and_idFach();
		update_unterrichtmenge_by_idKurs();
		update_unterrichtmenge_by_idZeitraster();
		update_unterrichtmenge_by_idZeitraster_and_wochentyp();
		update_lehrermenge_by_idUnterricht();
		update_zeitrastermengeOhneLeereUnterrichtmenge();
	}

	private void unterrichtAddOhneUpdate(final @NotNull StundenplanUnterricht u) {
		unterrichtCheck(u);
		DeveloperNotificationException.ifMapPutOverwrites(_unterricht_by_id, u.id, u);
	}

	/**
	 * Fügt ein {@link StundenplanUnterricht}-Objekt hinzu.
	 *
	 * @param unterricht  Das {@link StundenplanUnterricht}-Objekt, welches hinzugefügt werden soll.
	 */
	public void unterrichtAdd(final @NotNull StundenplanUnterricht unterricht) {
		unterrichtAddOhneUpdate(unterricht);
		unterrichtRevalidate();
	}

	/**
	 * Fügt alle {@link StundenplanUnterricht}-Objekte hinzu.
	 *
	 * @param listUnterricht  Die Menge der {@link StundenplanUnterricht}-Objekte, welche hinzugefügt werden soll.
	 */
	public void unterrichtAddAll(final @NotNull List<@NotNull StundenplanUnterricht> listUnterricht) {
		for (final @NotNull StundenplanUnterricht unterricht : listUnterricht)
			unterrichtAddOhneUpdate(unterricht);
		unterrichtRevalidate();
	}

	private void unterrichtCheck(final @NotNull StundenplanUnterricht u) {
		DeveloperNotificationException.ifInvalidID("u.id", u.id);
		DeveloperNotificationException.ifMapNotContains("_zeitraster_by_id", _zeitraster_by_id, u.idZeitraster);
		DeveloperNotificationException.ifTrue("u.wochentyp > stundenplanWochenTypModell", u.wochentyp > _stundenplanWochenTypModell);
		DeveloperNotificationException.ifTrue("u.wochentyp < 0", u.wochentyp < 0); // 0 ist erlaubt!

		DeveloperNotificationException.ifMapNotContains("_fach_by_id", _fach_by_id, u.idFach);
		for (final @NotNull Long idLehrkraftDesUnterrichts : u.lehrer)
			DeveloperNotificationException.ifMapNotContains("_lehrer_by_id", _lehrer_by_id, idLehrkraftDesUnterrichts);
		for (final @NotNull Long idKlasseDesUnterrichts : u.klassen)
			DeveloperNotificationException.ifMapNotContains("_klasse_by_id", _klasse_by_id, idKlasseDesUnterrichts);
		for (final @NotNull Long idRaumDesUnterrichts : u.raeume)
			DeveloperNotificationException.ifMapNotContains("_raum_by_id", _raum_by_id, idRaumDesUnterrichts);
		for (final @NotNull Long idSchieneDesUnterrichts : u.schienen)
			DeveloperNotificationException.ifMapNotContains("_schiene_by_id", _schiene_by_id, idSchieneDesUnterrichts);
	}

	/**
	 * Liefert das {@link StundenplanUnterricht}-Objekt zur übergebenen ID.
	 * <br>Laufzeit: O(1)
	 * <br>Hinweis: Unnötige Methode, denn man bekommt die Objekte über Zeitraster-Abfragen.
	 *
	 * @param idUnterricht  Die Datenbank-ID des Unterrichts.
	 *
	 * @return das {@link StundenplanUnterricht}-Objekt zur übergebenen ID.
	 */
	public @NotNull StundenplanUnterricht unterrichtGetByIdOrException(final long idUnterricht) {
		return DeveloperNotificationException.ifMapGetIsNull(_unterricht_by_id, idUnterricht);
	}

	/**
	 * Liefert eine Liste aller {@link StundenplanUnterricht}-Objekte einer Klasse mit einem bestimmten Wochentyp.
	 *
	 * @param idKlasse   Die Datenbank-ID der Klasse.
	 * @param wochentyp  Der gewünschten Wochentyp. Der Wert 0 ist nur dann erlaubt, wenn wochenTypModell ebenfalls 0 ist.
	 *
	 * @return eine Liste aller {@link StundenplanUnterricht}-Objekte einer Klasse mit einem bestimmten Wochentyp.
	 */
	public @NotNull List<@NotNull StundenplanUnterricht> unterrichtGetMengeByKlasseIdAndWochentyp(final long idKlasse, final int wochentyp) {
		// Überprüfen
		DeveloperNotificationException.ifTrue("wochentyp > stundenplanWochenTypModell", wochentyp > _stundenplanWochenTypModell);
		// Filtern
		final @NotNull List<@NotNull StundenplanUnterricht> listU = DeveloperNotificationException.ifMapGetIsNull(_unterrichtmenge_by_idKlasse, idKlasse);
		return CollectionUtils.toFilteredArrayList(listU, (final @NotNull StundenplanUnterricht u) -> (u.wochentyp == 0) || (u.wochentyp == wochentyp));
	}

	/**
	 * Liefert eine Liste aller {@link StundenplanUnterricht}-Objekte einer Klasse in einer bestimmten Kalenderwoche.
	 *
	 * @param idKlasse       Die Datenbank-ID der Klasse.
	 * @param jahr           Das Jahr der Kalenderwoche (muss zwischen 2000 und 3000 liegen).
	 * @param kalenderwoche  Die gewünschten Kalenderwoche (muss zwischen 1 und 53 liegen).
	 *
	 * @return eine Liste aller {@link StundenplanUnterricht}-Objekte einer Klasse in einer bestimmten Kalenderwoche.
	 */
	public @NotNull List<@NotNull StundenplanUnterricht> unterrichtGetMengeByKlasseIdAndJahrAndKW(final long idKlasse, final int jahr, final int kalenderwoche) {
		final int wochentyp = kalenderwochenzuordnungGetWochentypOrDefault(jahr, kalenderwoche);
		return unterrichtGetMengeByKlasseIdAndWochentyp(idKlasse, wochentyp);
	}

	/**
	 * Liefert eine Liste aller {@link StundenplanUnterricht}-Objekte eines Klassenunterrichts (Klasse, Fach) mit einem bestimmten Wochentyp.
	 *
	 * @param idKlasse   Die Datenbank-ID der Klasse.
	 * @param idFach     Die Datenbank-ID des Faches.
	 * @param wochentyp  Der gewünschten Wochentyp. Der Wert 0 ist nur dann erlaubt, wenn wochenTypModell ebenfalls 0 ist.
	 *
	 * @return eine Liste aller {@link StundenplanUnterricht}-Objekte eines Klassenunterrichts (Klasse, Fach) mit einem bestimmten Wochentyp.
	 */
	public @NotNull List<@NotNull StundenplanUnterricht> unterrichtGetMengeByKlasseIdAndFachIdAndWochentyp(final long idKlasse, final long idFach, final int wochentyp) {
		// Überprüfen
		DeveloperNotificationException.ifTrue("wochentyp > stundenplanWochenTypModell", wochentyp > _stundenplanWochenTypModell);

		// Filtern
		final @NotNull List<@NotNull StundenplanUnterricht> listU = DeveloperNotificationException.ifMap2DGetIsNull(_unterrichtmenge_by_idKlasse_and_idFach, idKlasse, idFach);
		return CollectionUtils.toFilteredArrayList(listU, (final @NotNull StundenplanUnterricht u) -> (u.wochentyp == 0) || (u.wochentyp == wochentyp));
	}

	/**
	 * Liefert eine Liste aller {@link StundenplanUnterricht}-Objekte eines Klassenunterrichts (Klasse, Fach) in einer bestimmten Kalenderwoche.
	 *
	 * @param idKlasse       Die Datenbank-ID der Klasse.
	 * @param idFach         Die Datenbank-ID des Faches.
	 * @param jahr           Das Jahr der Kalenderwoche (muss zwischen 2000 und 3000 liegen).
	 * @param kalenderwoche  Die gewünschten Kalenderwoche (muss zwischen 1 und 53 liegen).
	 *
	 * @return eine Liste aller {@link StundenplanUnterricht}-Objekte eines Klassenunterrichts (Klasse, Fach) in einer bestimmten Kalenderwoche.
	 */
	public @NotNull List<@NotNull StundenplanUnterricht> unterrichtGetMengeByKlasseIdAndFachIdAndJahrAndKW(final long idKlasse, final long idFach, final int jahr, final int kalenderwoche) {
		final int wochentyp = kalenderwochenzuordnungGetWochentypOrDefault(jahr, kalenderwoche);
		return unterrichtGetMengeByKlasseIdAndFachIdAndWochentyp(idKlasse, idFach, wochentyp);
	}

	/**
	 * Liefert eine Liste aller {@link StundenplanUnterricht} eines Kurses mit einem bestimmten Wochentyp.
	 *
	 * @param idkurs     Die ID des Kurses.
	 * @param wochentyp  Der gewünschten Wochentyp. Der Wert 0 ist nur dann erlaubt, wenn wochenTypModell ebenfalls 0 ist.
	 *
	 * @return eine Liste aller {@link StundenplanUnterricht} eines Kurses in einer bestimmten Kalenderwoche.
	 */
	public @NotNull List<@NotNull StundenplanUnterricht> unterrichtGetMengeByKursIdAndWochentyp(final long idkurs, final int wochentyp) {
		// Überprüfen
		DeveloperNotificationException.ifTrue("wochentyp > stundenplanWochenTypModell", wochentyp > _stundenplanWochenTypModell);

		// Filtern
		final @NotNull List<@NotNull StundenplanUnterricht> listU = DeveloperNotificationException.ifMapGetIsNull(_unterrichtmenge_by_idKurs, idkurs);
		return CollectionUtils.toFilteredArrayList(listU, (final @NotNull StundenplanUnterricht u) -> (u.wochentyp == 0) || (u.wochentyp == wochentyp));
	}

	/**
	 * Liefert eine Liste aller {@link StundenplanUnterricht} einer Kursmenge mit einem bestimmten Wochentyp.
	 *
	 * @param idsKurs   Die IDs aller Kurse.
	 * @param wochentyp Der gewünschten Wochentyp. Der Wert 0 ist nur dann erlaubt, wenn wochenTypModell ebenfalls 0 ist.
	 *
	 * @return eine Liste aller {@link StundenplanUnterricht} einer Kursmenge mit einem bestimmten Wochentyp.
	 */
	public @NotNull List<@NotNull StundenplanUnterricht> unterrichtGetMengeByKursIdsAndWochentyp(final @NotNull long[] idsKurs, final int wochentyp) {
		// Daten filtern.
		final @NotNull ArrayList<@NotNull StundenplanUnterricht> result = new ArrayList<>();
		for (final long idKurs : idsKurs)
			result.addAll(unterrichtGetMengeByKursIdAndWochentyp(idKurs, wochentyp));
		return result;
	}

	/**
	 * Liefert eine Liste aller {@link StundenplanUnterricht} eines Kurses in einer bestimmten Kalenderwoche.
	 *
	 * @param idKurs        Die ID des Kurses.
	 * @param jahr          Das Jahr der Kalenderwoche (muss zwischen 2000 und 3000 liegen).
	 * @param kalenderwoche Die gewünschten Kalenderwoche (muss zwischen 1 und 53 liegen).
	 *
	 * @return eine Liste aller {@link StundenplanUnterricht} eines Kurses in einer bestimmten Kalenderwoche.
	 */
	public @NotNull List<@NotNull StundenplanUnterricht> unterrichtGetMengeByKursIdAndJahrAndKW(final long idKurs, final int jahr, final int kalenderwoche) {
		final int wochentyp = kalenderwochenzuordnungGetWochentypOrDefault(jahr, kalenderwoche);
		return unterrichtGetMengeByKursIdAndWochentyp(idKurs, wochentyp);
	}

	/**
	 * Liefert eine Liste aller {@link StundenplanUnterricht} einer Kursmenge in einer bestimmten Kalenderwoche.
	 *
	 * @param idsKurs       Die IDs aller Kurse.
	 * @param jahr          Das Jahr der Kalenderwoche (muss zwischen 2000 und 3000 liegen).
	 * @param kalenderwoche Die gewünschten Kalenderwoche (muss zwischen 1 und 53 liegen).
	 *
	 * @return eine Liste aller {@link StundenplanUnterricht} einer Kursmenge in einer bestimmten Kalenderwoche.
	 */
	public @NotNull List<@NotNull StundenplanUnterricht> unterrichtGetMengeByKursIdsAndJahrAndKW(final @NotNull long[] idsKurs, final int jahr, final int kalenderwoche) {
		final int wochentyp = kalenderwochenzuordnungGetWochentypOrDefault(jahr, kalenderwoche);
		return unterrichtGetMengeByKursIdsAndWochentyp(idsKurs, wochentyp);
	}

	/**
	 * Liefert eine Liste aller {@link StundenplanUnterricht}-Objekt, die im übergeben Zeitraster und Wochentyp liegen.
	 *
	 * @param idZeitraster  Die Datenbank-ID des Zeitrasters.
	 * @param wochentyp     Der Wochentyp (0 jede Woche, 1 nur Woche A, 2 nur Woche B, ...)
	 *
	 * @return eine Liste aller {@link StundenplanUnterricht}-Objekt, die im übergeben Zeitraster und Wochentyp liegen.
	 */
	public @NotNull List<@NotNull StundenplanUnterricht> unterrichtGetMengeByZeitrasterIdAndWochentypOrEmptyList(final long idZeitraster, final int wochentyp) {
		return Map2DUtils.getOrCreateArrayList(_unterrichtmenge_by_idZeitraster_and_wochentyp, idZeitraster, wochentyp);
	}

	/**
	 * Liefert eine Liste aller {@link StundenplanUnterricht}-Objekten, die im übergebenen Zeitraster und Wochentyp liegen.
	 * Falls der Parameter inklWoche0 TRUE ist, wird Unterricht des Wochentyps 0 hinzugefügt.
	 *
	 * @param idZeitraster  Die Datenbank-ID des Zeitrasters.
	 * @param wochentyp     Der Wochentyp
	 * @param inklWoche0    falls TRUE, wird Unterricht des Wochentyps 0 hinzugefügt.
	 *
	 * @return eine Liste aller {@link StundenplanUnterricht}-Objekten, die im übergebenen Zeitraster und Wochentyp liegen.
	 */
	public @NotNull List<@NotNull StundenplanUnterricht> unterrichtGetMengeByZeitrasterIdAndWochentypAndInklusiveOrEmptyList(final long idZeitraster, final int wochentyp, final boolean inklWoche0) {
		if ((wochentyp == 0) || (!inklWoche0))
			return unterrichtGetMengeByZeitrasterIdAndWochentypOrEmptyList(idZeitraster, wochentyp);

		final @NotNull List<@NotNull StundenplanUnterricht> list = new ArrayList<>();
		list.addAll(unterrichtGetMengeByZeitrasterIdAndWochentypOrEmptyList(idZeitraster, wochentyp));
		list.addAll(unterrichtGetMengeByZeitrasterIdAndWochentypOrEmptyList(idZeitraster, 0));
		return list;
	}

	/**
	 * Liefert eine Liste aller {@link StundenplanUnterricht}-Objekt, die im übergeben Zeitraster und Wochentyp liegen.
	 *
	 * @param wochentag  Der {@link Wochentag}-ENUM.
	 * @param stunde     Die Unterrichtsstunde.
	 * @param wochentyp  Der Wochentyp (0 jede Woche, 1 nur Woche A, 2 nur Woche B, ...)
	 *
	 * @return eine Liste aller {@link StundenplanUnterricht}-Objekt, die im übergeben Zeitraster und Wochentyp liegen.
	 */
	public @NotNull List<@NotNull StundenplanUnterricht> unterrichtGetMengeByWochentagAndStundeAndWochentypOrEmptyList(final @NotNull Wochentag wochentag, final int stunde, final int wochentyp) {
		final StundenplanZeitraster zeitraster = _zeitraster_by_wochentag_and_stunde.getOrNull(wochentag.id, stunde);
		if (zeitraster != null)
			return Map2DUtils.getOrCreateArrayList(_unterrichtmenge_by_idZeitraster_and_wochentyp, zeitraster.id, wochentyp);
		return new ArrayList<>();
	}

	/**
	 * Liefert eine Liste aller {@link StundenplanUnterricht}-Objekten, die in der Stundenplanzelle "wochentag, stunde" und "wochentyp" liegen.
	 * Falls der Parameter inklWoche0 TRUE ist, wird Unterricht des Wochentyps 0 hinzugefügt.
	 *
	 * @param wochentag     Der {@link Wochentag}-ENUM.
	 * @param stunde        Die Unterrichtsstunde.
	 * @param wochentyp     Der Wochentyp (0 jede Woche, 1 nur Woche A, 2 nur Woche B, ...)
	 * @param inklWoche0    falls TRUE, wird Unterricht des Wochentyps 0 hinzugefügt.
	 *
	 * @return eine Liste aller {@link StundenplanUnterricht}-Objekten, die in der Stundenplanzelle "wochentag, stunde" und "wochentyp" liegen.
	 */
	public @NotNull List<@NotNull StundenplanUnterricht> unterrichtGetMengeByWochentagAndStundeAndWochentypAndInklusiveOrEmptyList(final @NotNull Wochentag wochentag, final int stunde, final int wochentyp, final boolean inklWoche0) {
		final long idZeitraster = zeitrasterGetByWochentagAndStundeOrException(wochentag.id, stunde).id;
		return unterrichtGetMengeByZeitrasterIdAndWochentypAndInklusiveOrEmptyList(idZeitraster, wochentyp, inklWoche0);
	}

	/**
	 * Liefert eine Liste aller {@link StundenplanUnterricht}-Objekten, der Klasse am "wochentag, stunde, wochentyp".
	 * Falls der Parameter inklWoche0 TRUE ist und der wochentyp größer als 0 ist, wird der Unterricht des Wochentyps 0 auch hinzugefügt.
	 *
	 * @param idKlasse      Die Datenbank-ID der Klasse.
	 * @param wochentag     Die ID des {@link Wochentag}-ENUM.
	 * @param stunde        Die Unterrichtsstunde.
	 * @param wochentyp     Der Wochentyp (0 jede Woche, 1 nur Woche A, 2 nur Woche B, ...)
	 * @param inklWoche0    falls TRUE, wird Unterricht des Wochentyps 0 hinzugefügt.
	 *
	 * @return eine Liste aller {@link StundenplanUnterricht}-Objekten, der Klasse am "wochentag, stunde, wochentyp".
	 */
	public @NotNull List<@NotNull StundenplanUnterricht> unterrichtGetMengeByKlasseIdAndWochentagAndStundeAndWochentypAndInklusiveOrEmptyList(final long idKlasse, final int wochentag, final int stunde, final int wochentyp, final boolean inklWoche0) {
		final @NotNull List<@NotNull StundenplanUnterricht> list = new ArrayList<>();

		final StundenplanZeitraster z = _zeitraster_by_wochentag_and_stunde.getOrNull(wochentag, stunde);
		if (z != null)
			for (final @NotNull StundenplanUnterricht u : Map2DUtils.getOrCreateArrayList(_unterrichtmenge_by_idKlasse_and_idZeitraster, idKlasse, z.id))
				if  ((u.wochentyp == wochentyp) || ((wochentyp == 0) && inklWoche0))
					list.add(u);

		return list;
	}

	/**
	 * Liefert eine String-Repräsentation des das Fach- oder Kurs-Kürzel eines {@link StundenplanUnterricht}.
	 * <br>Beispiel: "M-LK1-Suffix" bei Kursen und "M" Fachkürzel bei Klassenunterricht.
	 * <br>Laufzeit: O(1)
	 * @param idUnterricht  Die Datenbank-ID des {@link StundenplanUnterricht}.
	 *
	 * @return eine String-Repräsentation des das Fach- oder Kurs-Kürzel eines {@link StundenplanUnterricht}.
	 */
	public @NotNull String unterrichtGetByIDStringOfFachOderKursKuerzel(final long idUnterricht) {
		final @NotNull StundenplanUnterricht unterricht =  DeveloperNotificationException.ifMapGetIsNull(_unterricht_by_id, idUnterricht);

		// Klassenunterricht?
		if (unterricht.idKurs == null) {
			final @NotNull StundenplanFach fach =  DeveloperNotificationException.ifMapGetIsNull(_fach_by_id, unterricht.idFach);
			return fach.kuerzel;
		}

		// Kursunterricht
		final @NotNull StundenplanKurs kurs =  DeveloperNotificationException.ifMapGetIsNull(_kurs_by_id, unterricht.idKurs);
		return kurs.bezeichnung;
	}

	/**
	 * Liefert eine String-Repräsentation der Klassenmenge des {@link StundenplanUnterricht}.
	 * <br>Beispiel: "5a" bei einer Klasse und "7a,7b" bei mehreren (z.B. Französisch...)
	 * <br>Laufzeit: O(1)
	 * @param idUnterricht  Die Datenbank-ID des {@link StundenplanUnterricht}.
	 *
	 * @return eine String-Repräsentation der Klassenmenge des {@link StundenplanUnterricht}.
	 */
	public @NotNull String unterrichtGetByIDStringOfKlassen(final long idUnterricht) {
		final @NotNull StundenplanUnterricht unterricht =  DeveloperNotificationException.ifMapGetIsNull(_unterricht_by_id, idUnterricht);

		// Klassenkürzel sammeln und sortieren.
		final @NotNull AVLSet<@NotNull String> kuerzel = new AVLSet<>();
		for (final @NotNull Long idKlasse : unterricht.klassen) {
			final @NotNull StundenplanKlasse klasse =  DeveloperNotificationException.ifMapGetIsNull(_klasse_by_id, idKlasse);
			kuerzel.add(klasse.kuerzel);
		}

		return StringUtils.collectionToCommaSeparatedString(kuerzel);
	}

	/**
	 * Liefert eine String-Repräsentation der Raummenge des {@link StundenplanUnterricht}.
	 * <br>Beispiel: "1.01" bei einem Raum und "T1, T2" bei mehreren (z.B. Sporthallen...)
	 * <br>Laufzeit: O(1)
	 * @param idUnterricht  Die Datenbank-ID des {@link StundenplanUnterricht}.
	 *
	 * @return eine String-Repräsentation der Raummenge des {@link StundenplanUnterricht}.
	 */
	public @NotNull String unterrichtGetByIDStringOfRaeume(final long idUnterricht) {
		final @NotNull StundenplanUnterricht unterricht =  DeveloperNotificationException.ifMapGetIsNull(_unterricht_by_id, idUnterricht);

		// Klassenkürzel sammeln und sortieren.
		final @NotNull AVLSet<@NotNull String> kuerzel = new AVLSet<>();
		for (final @NotNull Long idRaum : unterricht.raeume) {
			final @NotNull StundenplanRaum raum =  DeveloperNotificationException.ifMapGetIsNull(_raum_by_id, idRaum);
			kuerzel.add(raum.kuerzel);
		}

		return StringUtils.collectionToCommaSeparatedString(kuerzel);
	}

	/**
	 * Liefert die Menge aller {@link StundenplanLehrer}-Objekte des {@link StundenplanUnterricht}.
	 * <br>Laufzeit: O(1)
	 *
	 * @param idUnterricht  Die Datenbank-ID des {@link StundenplanUnterricht}.
	 *
	 * @return die Menge aller {@link StundenplanLehrer}-Objekte des {@link StundenplanUnterricht}.
	 */
	public @NotNull List<@NotNull StundenplanLehrer> unterrichtGetByIDLehrerMenge(final long idUnterricht) {
		return MapUtils.getOrCreateArrayList(_lehrermenge_by_idUnterricht, idUnterricht);
	}

	/**
	 * Liefert die Menge aller {@link StundenplanLehrer} des {@link StundenplanUnterricht} als kommaseparierter String.
	 * <br>Laufzeit: O(|Ergebnis|)
	 *
	 * @param idUnterricht  Die Datenbank-ID des {@link StundenplanUnterricht}.
	 *
	 * @return die Menge aller {@link StundenplanLehrer} des {@link StundenplanUnterricht} als kommaseparierter String.
	 */
	public @NotNull String unterrichtGetByIDLehrerMengeAsString(final long idUnterricht) {
		final @NotNull List<@NotNull StundenplanLehrer> lehrkraefteDesUnterrichts = MapUtils.getOrCreateArrayList(_lehrermenge_by_idUnterricht, idUnterricht);

		final @NotNull List<@NotNull String> listeDerKuerzel = new ArrayList<>();
		for (final @NotNull StundenplanLehrer lehkraft : lehrkraefteDesUnterrichts)
			listeDerKuerzel.add(lehkraft.kuerzel);

		return StringUtils.collectionToCommaSeparatedString(listeDerKuerzel);
	}

	/**
	 * Liefert die erste {@link StundenplanLehrer} des {@link StundenplanUnterricht} oder NULL falls nicht existent.
	 * <br>Laufzeit: O(|Ergebnis|)
	 *
	 * @param idUnterricht  Die Datenbank-ID des {@link StundenplanUnterricht}.
	 *
	 * @return eine String-Repräsentation der Raummenge des {@link StundenplanUnterricht}.
	 */
	public StundenplanLehrer unterrichtGetByIDLehrerFirstOrNull(final long idUnterricht) {
		final @NotNull List<@NotNull StundenplanLehrer> lehrerDesUnterrichts = MapUtils.getOrCreateArrayList(_lehrermenge_by_idUnterricht, idUnterricht);
		return lehrerDesUnterrichts.isEmpty() ? null : DeveloperNotificationException.ifListGetFirstFailes("lehrerDesUnterrichts.first", lehrerDesUnterrichts);
	}

	/**
	 * Liefert die erste {@link StundenplanLehrer} des {@link StundenplanUnterricht} oder NULL falls nicht existent.
	 * <br>Laufzeit: O(|Ergebnis|)
	 *
	 * @param idUnterricht  Die Datenbank-ID des {@link StundenplanUnterricht}.
	 *
	 * @return eine String-Repräsentation der Raummenge des {@link StundenplanUnterricht}.
	 */
	public @NotNull String unterrichtGetByIDLehrerFirstAsStringOrEmpty(final long idUnterricht) {
		final StundenplanLehrer lehrkraft = unterrichtGetByIDLehrerFirstOrNull(idUnterricht);
		return lehrkraft == null ? "" : lehrkraft.kuerzel;
	}

	/**
	 * Liefert TRUE, falls es {@link StundenplanUnterricht} gibt, der einen Wochentyp > 0 hat.
	 * <br>Laufzeit: O(1)
	 *
	 * @return TRUE, falls es {@link StundenplanUnterricht} gibt, der einen Wochentyp > 0 hat.
	 */
	public boolean unterrichtHatMultiWochen() {
		return _unterrichtHatMultiWochen;
	}

	private void unterrichtRemoveByIdOhneUpdate(final long idUnterricht) {
		DeveloperNotificationException.ifMapRemoveFailes(_unterricht_by_id, idUnterricht);
	}

	/**
	 * Entfernt aus dem Stundenplan ein existierendes {@link StundenplanUnterricht}-Objekt.
	 *
	 * @param idUnterricht  Die Datenbank-ID des {@link StundenplanUnterricht}-Objekts.
	 */
	public void unterrichtRemoveById(final long idUnterricht) {
		unterrichtRemoveByIdOhneUpdate(idUnterricht);
		unterrichtRevalidate();
	}

	/**
	 * Entfernt alle {@link StundenplanUnterricht}-Objekte.
	 *
	 * @param listUnterricht  Die Liste der zu entfernenden {@link StundenplanUnterricht}-Objekte.
	 */
	public void unterrichtRemoveAll(final @NotNull List<@NotNull StundenplanUnterricht> listUnterricht) {
		for (final @NotNull StundenplanUnterricht unterricht : listUnterricht)
			unterrichtRemoveByIdOhneUpdate(unterricht.id);
		unterrichtRevalidate();
	}

	/**
	 * Liefert eine String-Menge aller Uhrzeiten der Zeitraster einer bestimmten Unterrichtsstunde. Dabei werden identische Uhrzeiten zusammengefasst.
	 * <br>Beispiel:  "08:00-8:45", falls sie nicht abweichen.
	 * <br>Beispiel:  "Mo-Mi 08:00-8:45", "Do 07:55-8:40", "Fr 07:40-8:25", falls sie abweichen.
	 *
	 * @param stunde  Die Nr. der Unterrichtsstunde.
	 *
	 * @return eine String-Menge aller Uhrzeiten der Zeitraster einer bestimmten Unterrichtsstunde. Dabei werden identische Uhrzeiten zusammengefasst.
	 */
	public @NotNull List<@NotNull String> unterrichtsstundeGetUhrzeitenAsStrings(final int stunde) {
		final @NotNull List<@NotNull String> listUhrzeit = new ArrayList<>();
		final @NotNull List<@NotNull String> listWochentagVon = new ArrayList<>();
		final @NotNull List<@NotNull String> listWochentagBis = new ArrayList<>();

		for (int wochentag = _zeitrasterWochentagMin; wochentag <= _zeitrasterWochentagMax; wochentag++) {
			final @NotNull String sUhrzeit = unterrichtsstundeGetUhrzeitAsString(wochentag, stunde);
			final @NotNull String sWochentag = Wochentag.fromIDorException(wochentag).kuerzel;

			if (listUhrzeit.isEmpty()) {
				listUhrzeit.add(sUhrzeit);
				listWochentagVon.add(sWochentag);
				listWochentagBis.add(sWochentag);
				continue;
			}

			final @NotNull String sUhrzeitDavor = DeveloperNotificationException.ifListGetLastFailes("listUhrzeit", listUhrzeit);

			if (sUhrzeitDavor.equals(sUhrzeit)) {
				listWochentagBis.set(listWochentagBis.size() - 1, sWochentag);
			} else {
				listUhrzeit.add(sUhrzeit);
				listWochentagVon.add(sWochentag);
				listWochentagBis.add(sWochentag);
			}

		}

		// Fall: Alle Zeiten sind identisch.
		if (listUhrzeit.size() <= 1)
			return listUhrzeit;

		// Fall: Unterschiedliche Zeiten benötigen als Prefix den Wochentag.
		for (int i = 0; i < listUhrzeit.size(); i++) {
			final @NotNull String sUhrzeit = listUhrzeit.get(i);
			final @NotNull String sWochentagVon = listWochentagVon.get(i);
			final @NotNull String sWochentagBis = listWochentagBis.get(i);
			if (sWochentagVon.equals(sWochentagBis))
				listUhrzeit.set(i, sWochentagVon + " " + sUhrzeit);
			else
				listUhrzeit.set(i, sWochentagVon + "-" + sWochentagBis + " " + sUhrzeit);
		}

		return listUhrzeit;
	}

	private @NotNull String unterrichtsstundeGetUhrzeitAsString(final int wochentag, final int stunde) {
		final StundenplanZeitraster zeitraster = _zeitraster_by_wochentag_and_stunde.getOrNull(wochentag, stunde);

		if (zeitraster == null)
			return "???";

		final @NotNull String sBeginn = (zeitraster.stundenbeginn == null) ? "??:??" : DateUtils.getStringOfUhrzeitFromMinuten(zeitraster.stundenbeginn);
		final @NotNull String sEnde = (zeitraster.stundenende == null) ? "??:??" : DateUtils.getStringOfUhrzeitFromMinuten(zeitraster.stundenende);

		return sBeginn + " - " + sEnde + " Uhr";
	}

	// #####################################################################
	// #################### StundenplanZeitraster ##########################
	// #####################################################################

	private void zeitrasterRevalidate() {
		update_zeitrastermenge();
		update_zeitrastermenge_by_wochentag();
		update_zeitrastermenge_by_stunde();
		update_zeitraster_by_wochentag_and_stunde();
		update_unterrichtmenge_by_idZeitraster();
		update_unterrichtmenge_by_idZeitraster_and_wochentyp();
		update_zeitrastermengeOhneLeereUnterrichtmenge(); // benötigt 'update_unterrichtmenge_by_idZeitraster'
		update_unterrichtmenge_by_idKlasse_and_idZeitraster();
	}

	private void zeitrasterAddOhneUpdate(final @NotNull StundenplanZeitraster zeitraster) {
		zeitrasterCheck(zeitraster);
		DeveloperNotificationException.ifMapPutOverwrites(_zeitraster_by_id, zeitraster.id, zeitraster);
	}

	/**
	 * Fügt ein {@link StundenplanZeitraster}-Objekt hinzu.
	 *
	 * @param zeitraster  Das {@link StundenplanZeitraster}-Objekt, welches hinzugefügt werden soll.
	 */
	public void zeitrasterAdd(final @NotNull StundenplanZeitraster zeitraster) {
		zeitrasterAddOhneUpdate(zeitraster);
		zeitrasterRevalidate();
	}

	/**
	 * Fügt alle {@link StundenplanZeitraster}-Objekte hinzu.
	 *
	 * @param listZeitraster  Die Menge der {@link StundenplanZeitraster}-Objekte, welche hinzugefügt werden soll.
	 */
	public void zeitrasterAddAll(final @NotNull List<@NotNull StundenplanZeitraster> listZeitraster) {
		for (final @NotNull StundenplanZeitraster zeitraster : listZeitraster)
			zeitrasterAddOhneUpdate(zeitraster);
		zeitrasterRevalidate();
	}

	private static void zeitrasterCheck(final @NotNull StundenplanZeitraster zeitraster) {
		DeveloperNotificationException.ifInvalidID("zeitraster.id", zeitraster.id);
		Wochentag.fromIDorException(zeitraster.wochentag);
		DeveloperNotificationException.ifTrue("(zeit.unterrichtstunde < 0) || (zeit.unterrichtstunde > 29)", (zeitraster.unterrichtstunde < 0) || (zeitraster.unterrichtstunde > 29));
		if ((zeitraster.stundenbeginn != null) && (zeitraster.stundenende != null)) {
			final int beginn = zeitraster.stundenbeginn;
			final int ende = zeitraster.stundenende;
			DeveloperNotificationException.ifTrue("beginn >= ende", beginn >= ende);
		}
	}

	/**
	 * Liefert eine Liste aller {@link StundenplanZeitraster}-Objekte.
	 *
	 * @return eine Liste aller {@link StundenplanZeitraster}-Objekte.
	 */
	public @NotNull List<@NotNull StundenplanZeitraster> getListZeitraster() {
		return _zeitrastermenge;
	}

	/**
	 * Liefert eine Liste der {@link StundenplanZeitraster}-Objekte zu einem bestimmten Wochentag.
	 *
	 * @param wochentag der Wochentag der gewünschten Zeitraster-Objekte
	 *
	 * @return eine Liste aller {@link StundenplanZeitraster}-Objekte zum übergebenen Wochentag.
	 */
	public @NotNull List<@NotNull StundenplanZeitraster> getListZeitrasterZuWochentag(final @NotNull Wochentag wochentag) {
		return CollectionUtils.toFilteredArrayList(_zeitrastermenge, (final @NotNull StundenplanZeitraster z) -> (wochentag.id == z.wochentag));
	}

	/**
	 * Liefert eine Liste der {@link StundenplanZeitraster}-Objekte zu einer bestimmten Unterrichtsstunde.
	 *
	 * @param unterrichtstunde   die Unterrichtsstunde der gewünschten Zeitraster-Objekte
	 *
	 * @return eine Liste aller {@link StundenplanZeitraster}-Objekte zur übergebenen Unterrichtsstunde.
	 */
	public @NotNull List<@NotNull StundenplanZeitraster> getListZeitrasterZuStunde(final int unterrichtstunde) {
		return CollectionUtils.toFilteredArrayList(_zeitrastermenge, (final @NotNull StundenplanZeitraster z) -> (unterrichtstunde == z.unterrichtstunde));
	}

	/**
	 * Liefert die passende Menge an {@link StundenplanZeitraster}-Objekten, welche das Intervall berühren.
	 *
	 * @param zeitrasterStart    Das {@link StundenplanZeitraster} zu dem es startet.
	 * @param minutenVerstrichen Die verstrichene Zeit (in Minuten) seit der "startzeit" .
	 *
	 * @return die passende Menge an {@link StundenplanZeitraster}-Objekten.
	 */
	public @NotNull List<@NotNull StundenplanZeitraster> getZeitrasterByStartVerstrichen(final @NotNull StundenplanZeitraster zeitrasterStart, final int minutenVerstrichen) {
		final Wochentag wochentag = Wochentag.fromIDorException(zeitrasterStart.wochentag);
		final int startzeit = DeveloperNotificationException.ifNull("zeitrasterStart.stundenbeginn ist NULL", zeitrasterStart.stundenbeginn);
		return getZeitrasterByWochentagStartVerstrichen(wochentag, startzeit, minutenVerstrichen);
	}

	/**
	 * Liefert die passende Menge an {@link StundenplanZeitraster}-Objekten, welche das Zeit-Intervall berühren.<br>
	 *
	 * @param wochentag          Der {@link Wochentag} des Zeit-Intervalls.
	 * @param beginn             Der Beginn des Zeit-Intervalls.
	 * @param minutenVerstrichen Daraus ergibt sich das Ende des Zeit-Intervalls.
	 *
	 * @return die passende Menge an {@link StundenplanZeitraster}-Objekten, welche das Intervall berührt.
	 */
	public @NotNull List<@NotNull StundenplanZeitraster> getZeitrasterByWochentagStartVerstrichen(final @NotNull Wochentag wochentag, final int beginn, final int minutenVerstrichen) {
		final int ende = beginn + minutenVerstrichen;
		return CollectionUtils.toFilteredArrayList(_zeitrastermenge, (final @NotNull StundenplanZeitraster z) -> (wochentag.id == z.wochentag) &&  zeitrasterGetSchneidenSich(beginn, ende, z.stundenbeginn, z.stundenende));
	}

	/**
	 * Liefert das {@link StundenplanZeitraster}-Objekt der nächsten Stunde am selben Wochentag.
	 *
	 * @param zeitraster Das aktuelle {@link StundenplanZeitraster}-Objekt.
	 *
	 * @return das {@link StundenplanZeitraster}-Objekt der nächsten Stunde am selben Wochentag.
	 */
	public @NotNull StundenplanZeitraster getZeitrasterNext(final @NotNull StundenplanZeitraster zeitraster) {
		return _zeitraster_by_wochentag_and_stunde.getNonNullOrException(zeitraster.wochentag, zeitraster.unterrichtstunde + 1);
	}

	/**
	 * Liefert den kleinsten Minuten-Wert aller Zeitraster, oder 480 (8 Uhr).
	 * <br>Laufzeit: O(1)
	 *
	 * @return den kleinsten Minuten-Wert aller Zeitraster, oder 480 (8 Uhr).
	 */
	public int zeitrasterGetMinutenMin() {
		return (_zeitrasterMinutenMin == null) ? 480 : _zeitrasterMinutenMin;
	}

	/**
	 * Liefert das Minimum aller {@link StundenplanZeitraster#stundenbeginn}-Objekte einer bestimmten Unterrichtsstunde, oder 480 (8 Uhr) falls keines vorhanden ist.
	 * <br>Laufzeit: O(1)
	 *
	 * @param stunde  Die Unterrichtsstunde, deren Minimum gesucht wird.
	 *
	 * @return das Minimum aller {@link StundenplanZeitraster#stundenbeginn}-Objekte einer bestimmten Unterrichtsstunde, oder 480 (8 Uhr) falls keines vorhanden ist.
	 */
	public int zeitrasterGetMinutenMinDerStunde(final int stunde) {
		final Integer min = _zeitrasterMinutenMinByStunde.get(stunde); // Beide Fälle von NULL können auftreten!
		return (min == null) ? 480 : min;
	}

	/**
	 * Liefert den größten Minuten-Wert aller Zeitraster, oder 480 (8 Uhr).
	 * <br>Laufzeit: O(1)
	 *
	 * @return den größten Minuten-Wert aller Zeitraster, oder 480 (8 Uhr).
	 */
	public int zeitrasterGetMinutenMax() {
		return (_zeitrasterMinutenMax == null) ? 480 : _zeitrasterMinutenMax;
	}

	/**
	 * Liefert das Maximum aller {@link StundenplanZeitraster#stundenbeginn}-Objekte einer bestimmten Unterrichtsstunde, oder 480 (8 Uhr) falls keines vorhanden ist.
	 * <br>Laufzeit: O(1)
	 *
	 * @param stunde  Die Unterrichtsstunde, deren Maximum gesucht wird.
	 *
	 * @return das Maximum aller {@link StundenplanZeitraster#stundenbeginn}-Objekte einer bestimmten Unterrichtsstunde, oder 480 (8 Uhr) falls keines vorhanden ist.
	 */
	public int zeitrasterGetMinutenMaxDerStunde(final int stunde) {
		final Integer max = _zeitrasterMinutenMaxByStunde.get(stunde); // Beide Fälle von NULL können auftreten!
		return (max == null) ? 480 : max;
	}

	/**
	 * Liefert die kleinste Stunde aller Zeitraster, oder 1 falls es keine Zeitraster gibt.
	 * <br>Laufzeit: O(1)
	 *
	 * @return die kleinste Stunde aller Zeitraster, oder 1 falls es keine Zeitraster gibt.
	 */
	public int zeitrasterGetStundeMin() {
		return _zeitrasterStundeMin;
	}

	/**
	 * Liefert die kleinste nicht leere Stunde aller Zeitraster, oder 1 falls es keine Zeitraster gibt.
	 * <br>Laufzeit: O(1)
	 *
	 * @return die kleinste nicht leere Stunde aller Zeitraster, oder 1 falls es keine Zeitraster gibt.
	 */
	public int zeitrasterGetStundeMinOhneLeere() {
		return _zeitrasterStundeMinOhneLeere;
	}

	/**
	 * Liefert die größte Stunde aller Zeitraster, oder 1 falls es keine Zeitraster gibt.
	 * <br>Laufzeit: O(1)
	 *
	 * @return die größte Stunde aller Zeitraster, oder 1 falls es keine Zeitraster gibt.
	 */
	public int zeitrasterGetStundeMax() {
		return _zeitrasterStundeMax;
	}

	/**
	 * Liefert die größte nicht leere Stunde aller Zeitraster, oder 1 falls es keine Zeitraster gibt.
	 * <br>Laufzeit: O(1)
	 *
	 * @return die größte nicht leere Stunde aller Zeitraster, oder 1 falls es keine Zeitraster gibt.
	 */
	public int zeitrasterGetStundeMaxOhneLeere() {
		return _zeitrasterStundeMaxOhneLeere;
	}

	/**
	 * Liefert die ID des kleinsten {@link Wochentag} oder den Montag falls es keine Zeitraster gibt.
	 * <br>Laufzeit: O(1)
	 *
	 * @return die ID des kleinsten {@link Wochentag} oder den Montag falls es keine Zeitraster gibt.
	 */
	public int zeitrasterGetWochentagMin() {
		return _zeitrasterWochentagMin;
	}

	/**
	 * Liefert den kleinsten {@link Wochentag} oder den Montag falls es keine Zeitraster gibt.
	 * <br>Laufzeit: O(1)
	 *
	 * @return den kleinsten {@link Wochentag} oder den Montag falls es keine Zeitraster gibt.
	 */
	public @NotNull Wochentag zeitrasterGetWochentagMinEnum() {
		return Wochentag.fromIDorException(_zeitrasterWochentagMin);
	}

	/**
	 * Liefert die ID des größten {@link Wochentag} oder den Montag falls es keine Zeitraster gibt.
	 * <br>Laufzeit: O(1)
	 *
	 * @return die ID des größten {@link Wochentag} oder den Montag falls es keine Zeitraster gibt.
	 */
	public int zeitrasterGetWochentagMax() {
		return _zeitrasterWochentagMax;
	}

	/**
	 * Liefert den größten {@link Wochentag} oder den Montag falls es keine Zeitraster gibt.
	 * <br>Laufzeit: O(1)
	 *
	 * @return den größten {@link Wochentag} oder den Montag falls es keine Zeitraster gibt.
	 */
	public @NotNull Wochentag zeitrasterGetWochentagMaxEnum() {
		return Wochentag.fromIDorException(_zeitrasterWochentagMax);
	}

	/**
	 * Liefert das zur ID zugehörige {@link StundenplanZeitraster}-Objekt.
	 *
	 * @param idZeitraster  Die Datenbank-ID des Zeitrasters.
	 *
	 * @return das zur ID zugehörige {@link StundenplanZeitraster}-Objekt.
	 */
	public @NotNull StundenplanZeitraster zeitrasterGetByIdOrException(final long idZeitraster) {
		return DeveloperNotificationException.ifMapGetIsNull(_zeitraster_by_id, idZeitraster);
	}

	/**
	 * Liefert die Beginn-Uhrzeit des {@link StundenplanZeitraster} oder den leeren String, falls diese NULL ist.
	 * <br>Beispiel: "09:30" oder ""
	 * <br>Laufzeit: O(1)
	 *
	 * @param idZeitraster  Die Datenbank-ID des {@link StundenplanZeitraster}.
	 *
	 * @return die Beginn-Uhrzeit des {@link StundenplanZeitraster} oder den leeren String, falls diese NULL ist.
	 */
	public @NotNull String zeitrasterGetByIdStringOfUhrzeitBeginn(final long idZeitraster) {
		final @NotNull StundenplanZeitraster zeitraster =  DeveloperNotificationException.ifMapGetIsNull(_zeitraster_by_id, idZeitraster);
		return (zeitraster.stundenbeginn == null) ? "" : DateUtils.getStringOfUhrzeitFromMinuten(zeitraster.stundenbeginn);
	}

	/**
	 * Liefert die End-Uhrzeit des {@link StundenplanZeitraster} oder den leeren String, falls diese NULL ist.
	 * <br>Beispiel: "10:15" oder ""
	 * <br>Laufzeit: O(1)
	 *
	 * @param idZeitraster  Die Datenbank-ID des {@link StundenplanZeitraster}.
	 *
	 * @return die End-Uhrzeit des {@link StundenplanZeitraster} oder den leeren String, falls diese NULL ist.
	 */
	public @NotNull String zeitrasterGetByIdStringOfUhrzeitEnde(final long idZeitraster) {
		final @NotNull StundenplanZeitraster zeitraster =  DeveloperNotificationException.ifMapGetIsNull(_zeitraster_by_id, idZeitraster);
		return (zeitraster.stundenende == null) ? "" : DateUtils.getStringOfUhrzeitFromMinuten(zeitraster.stundenende);
	}

	/**
	 * Liefert das zu (wochentag, stunde) zugehörige {@link StundenplanZeitraster}-Objekt.
	 *
	 * @param wochentag  Die ENUM-ID des {@link Wochentag} des gesuchten Zeitrasters.
	 * @param stunde     Die Unterrichtsstunde des gesuchten Zeitrasters.
	 *
	 * @return das zu (wochentag, stunde) zugehörige {@link StundenplanZeitraster}-Objekt.
	 * @throws DeveloperNotificationException falls kein Zeitraster-Eintrag existiert
	 */
	public @NotNull StundenplanZeitraster zeitrasterGetByWochentagAndStundeOrException(final int wochentag, final int stunde) {
		return _zeitraster_by_wochentag_and_stunde.getNonNullOrException(wochentag, stunde);
	}

	/**
	 * Liefert das zu (wochentag, stunde) zugehörige {@link StundenplanZeitraster}-Objekt, falls es existiert, sonst NULL.
	 *
	 * @param wochentag  Die ENUM-ID des {@link Wochentag} des gesuchten Zeitrasters.
	 * @param stunde     Die Unterrichtsstunde des gesuchten Zeitrasters.
	 *
	 * @return das zu (wochentag, stunde) zugehörige {@link StundenplanZeitraster}-Objekt, falls es existiert, sonst NULL.
	 */
	public StundenplanZeitraster zeitrasterGetByWochentagAndStundeOrNull(final int wochentag, final int stunde) {
		return _zeitraster_by_wochentag_and_stunde.getOrNull(wochentag, stunde);
	}

	/**
	 * Liefert TRUE, falls die Intervalle [beginn1, ende1] und [beginn2, ende2] sich schneiden.
	 *
	 * @param beginn1  Der Anfang (inklusive) des ersten Intervalls (in Minuten) seit 0 Uhr.
	 * @param ende1    Das Ende (inklusive) des ersten Intervalls (in Minuten) seit 0 Uhr.
	 * @param iBeginn2 Der Anfang (inklusive) des zweiten Intervalls (in Minuten) seit 0 Uhr.
	 * @param iEnde2   Das Ende (inklusive) des zweiten Intervalls (in Minuten) seit 0 Uhr.
	 *
	 * @return TRUE, falls die Intervalle [beginn1, ende1] und [beginn2, ende2] sich schneiden.
	 */
	public boolean zeitrasterGetSchneidenSich(final int beginn1, final int ende1, final Integer iBeginn2, final Integer iEnde2) {
		final int beginn2 = DeveloperNotificationException.ifNull("zeitraster.stundenbeginn ist NULL!", iBeginn2);
		final int ende2 = DeveloperNotificationException.ifNull("zeitraster.stundenende ist NULL!", iEnde2);
		DeveloperNotificationException.ifTrue("beginn1 < 0", beginn1 < 0);
		DeveloperNotificationException.ifTrue("beginn2 < 0", beginn2 < 0);
		DeveloperNotificationException.ifTrue("beginn1 > ende1", beginn1 > ende1);
		DeveloperNotificationException.ifTrue("beginn2 > ende2", beginn2 > ende2);
		return !((ende1 < beginn2) || (ende2 < beginn1));
	}

	/**
	 * Liefert alle verwendeten sortierten Unterrichtsstunden der {@link StundenplanZeitraster}.
	 * Das Array beinhaltet alle Zahlen von {@link #zeitrasterGetStundeMin()} bis {@link #zeitrasterGetStundeMax()}.
	 * <br>Laufzeit: O(1), da Referenz auf ein Array.
	 *
	 * @return alle verwendeten sortierten Unterrichtsstunden der {@link StundenplanZeitraster}.
	 */
	public @NotNull int @NotNull [] zeitrasterGetStundenRange() {
		return _zeitrasterStundenRange;
	}

	/**
	 * Liefert alle verwendeten sortierten Unterrichtsstunden der nicht leeren {@link StundenplanZeitraster}.
	 * Das Array beinhaltet alle Zahlen von {@link #zeitrasterGetStundeMinOhneLeere()} bis {@link #zeitrasterGetStundeMaxOhneLeere()}.
	 * <br>Laufzeit: O(1), da Referenz auf ein Array.
	 *
	 * @return alle verwendeten sortierten Unterrichtsstunden der nicht leeren {@link StundenplanZeitraster}.
	 */
	public @NotNull int @NotNull [] zeitrasterGetStundenRangeOhneLeere() {
		return _zeitrasterStundenRangeOhneLeere;
	}

	/**
	 * Liefert alle verwendeten sortierten {@link Wochentag}-Objekte der {@link StundenplanZeitraster}.
	 * Das Array beinhaltet alle {@link Wochentag}-Objekte von {@link #zeitrasterGetWochentagMin} bis {@link #zeitrasterGetWochentagMax()}.
	 * <br>Laufzeit: O(1), da Referenz auf ein Array.
	 *
	 * @return alle verwendeten sortierten {@link Wochentag}-Objekte der {@link StundenplanZeitraster}.
	 */
	public @NotNull Wochentag @NotNull [] zeitrasterGetWochentageAlsEnumRange() {
		return _zeitrasterWochentageAlsEnumRange;
	}

	/**
	 * Liefert TRUE, falls es mindestens einen Unterricht im Zeitraster mit einem einen Wochentyp 0 gibt.
	 *
	 * @param idZeitraster  Die Datenbank-ID des Zeitrasters.
	 *
	 * @return TRUE, falls es mindestens einen Unterricht im Zeitraster mit einem einen Wochentyp 0 gibt.
	 */
	public boolean zeitrasterHatUnterrichtMitWochentyp0(final long idZeitraster) {
		return !Map2DUtils.getOrCreateArrayList(_unterrichtmenge_by_idZeitraster_and_wochentyp, idZeitraster, 0).isEmpty();
	}

	/**
	 * Liefert TRUE, falls das Zeitraster existiert und es mindestens einen Unterricht im Zeitraster mit einem einen Wochentyp 0 gibt.

	 * @param wochentag  Der {@link Wochentag}-ENUM.
	 * @param stunde     Die Unterrichtsstunde.
	 *
	 * @return TRUE, falls das Zeitraster existiert und es mindestens einen Unterricht im Zeitraster mit einem einen Wochentyp 0 gibt.
	 */
	public boolean zeitrasterHatUnterrichtMitWochentyp0ByWochentagAndStunde(final @NotNull Wochentag wochentag, final int stunde) {
		final StundenplanZeitraster z = _zeitraster_by_wochentag_and_stunde.getOrNull(wochentag.id, stunde);
		return (z != null) && zeitrasterHatUnterrichtMitWochentyp0(z.id);
	}

	/**
	 * Liefert TRUE, falls die Klasse am "wochtag, stunde" Unterricht mit Wochentyp 0 hat.

	 * @param idKlasse   Die Datenbank-ID der Klasse.
	 * @param wochentag  Die ID des {@link Wochentag}-ENUM.
	 * @param stunde     Die Unterrichtsstunde.
	 *
	 * @return TRUE, falls die Klasse am "wochtag, stunde" Unterricht mit Wochentyp 0 hat.
	 */
	public boolean zeitrasterHatUnterrichtMitWochentyp0ByKlasseIdWochentagAndStunde(final long idKlasse, final int wochentag, final int stunde) {
		final StundenplanZeitraster z = _zeitraster_by_wochentag_and_stunde.getOrNull(wochentag, stunde);
		return (z != null) && zeitrasterHatUnterrichtByKlasseIdWochentagAndStundeAndWochentyp(idKlasse, wochentag, stunde, 0);
	}

	/**
	 * Liefert TRUE, falls es mindestens einen Unterricht im Zeitraster mit einem einen Wochentyp 1 bis N gibt.
	 *
	 * @param idZeitraster  Die Datenbank-ID des Zeitrasters.
	 *
	 * @return TRUE, falls es mindestens einen Unterricht im Zeitraster mit einem einen Wochentyp 1 bis N gibt.
	 */
	public boolean zeitrasterHatUnterrichtMitWochentyp1BisN(final long idZeitraster) {
		for (int wochentyp = 1; wochentyp <= _stundenplanWochenTypModell; wochentyp++)
			if (!Map2DUtils.getOrCreateArrayList(_unterrichtmenge_by_idZeitraster_and_wochentyp, idZeitraster, wochentyp).isEmpty())
				return true;
		return false;
	}

	/**
	 * Liefert TRUE, falls das Zeitraster existiert und es mindestens einen Unterricht im Zeitraster mit einem Wochentyp 1 bis N gibt.
	 *
	 * @param wochentag  Der {@link Wochentag}-ENUM.
	 * @param stunde     Die Unterrichtsstunde.
	 *
	 * @return TRUE, falls das Zeitraster existiert und es mindestens einen Unterricht im Zeitraster mit einem Wochentyp 1 bis N gibt.
	 */
	public boolean zeitrasterHatUnterrichtMitWochentyp1BisNByWochentagAndStunde(final @NotNull Wochentag wochentag, final int stunde) {
		final StundenplanZeitraster z = _zeitraster_by_wochentag_and_stunde.getOrNull(wochentag.id, stunde);
		return (z != null) && zeitrasterHatUnterrichtMitWochentyp1BisN(z.id);
	}

	/**
	 * Liefert TRUE, falls die Klasse am "wochentag, stunde" Unterricht mit einem Wochentyp von 1 bis N hat.
	 *
	 * @param idKlasse   Die Datenbank-ID der Klasse.
	 * @param wochentag  Der ID des {@link Wochentag}-ENUM.
	 * @param stunde     Die Unterrichtsstunde.
	 *
	 * @return TRUE, falls die Klasse am "wochentag, stunde" Unterricht mit einem Wochentyp von 1 bis N hat.
	 */
	public boolean zeitrasterHatUnterrichtMitWochentyp1BisNByKlasseIdWochentagAndStunde(final long idKlasse, final int wochentag, final int stunde) {
		final StundenplanZeitraster z = _zeitraster_by_wochentag_and_stunde.getOrNull(wochentag, stunde);

		if (z != null)
			for (final @NotNull StundenplanUnterricht u : Map2DUtils.getOrCreateArrayList(_unterrichtmenge_by_idKlasse_and_idZeitraster, idKlasse, z.id))
				if (u.wochentyp >= 1)
					return true;

		return false;
	}

	/**
	 * Liefert TRUE, falls es in der Stundenplanzelle "wochtag, stunde" Unterricht eines "wochentyps" gibt.
	 *
	 * @param wochentag  Der {@link Wochentag}-ENUM.
	 * @param stunde     Die Unterrichtsstunde.
	 * @param wochentyp  Der Wochentyp (0 jede Woche, 1 nur Woche A, 2 nur Woche B, ...)
	 *
	 * @return TRUE, falls es in der Stundenplanzelle "wochtag, stunde" Unterricht eines "wochentyps" gibt.
	 */
	public boolean zeitrasterHatUnterrichtByWochentagAndStundeAndWochentyp(final @NotNull Wochentag wochentag, final int stunde, final int wochentyp) {
		final StundenplanZeitraster z = _zeitraster_by_wochentag_and_stunde.getOrNull(wochentag.id, stunde);

		if (z != null)
			return !Map2DUtils.getOrCreateArrayList(_unterrichtmenge_by_idZeitraster_and_wochentyp, z.id, wochentyp).isEmpty();

		return false;
	}

	/**
	 * Liefert TRUE, falls die Klasse in der Stundenplanzelle "wochtag, stunde" Unterricht eines "wochentyps" hat.
	 *
	 * @param idKlasse   Die Datenbank-ID der Klasse.
	 * @param wochentag  Die ENUM-ID des {@link Wochentag} des Zeitrasters.
	 * @param stunde     Die Unterrichtsstunde.
	 * @param wochentyp  Der Wochentyp (0 jede Woche, 1 nur Woche A, 2 nur Woche B, ...)
	 *
	 * @return TRUE, falls die Klasse in der Stundenplanzelle "wochtag, stunde" Unterricht eines "wochentyps" hat.
	 */
	public boolean zeitrasterHatUnterrichtByKlasseIdWochentagAndStundeAndWochentyp(final long idKlasse, final int wochentag, final int stunde, final int wochentyp) {
		final StundenplanZeitraster z = _zeitraster_by_wochentag_and_stunde.getOrNull(wochentag, stunde);

		if (z != null)
			for (final @NotNull StundenplanUnterricht u : Map2DUtils.getOrCreateArrayList(_unterrichtmenge_by_idKlasse_and_idZeitraster, idKlasse, z.id))
				if (u.wochentyp == wochentyp)
					return true;

		return false;
	}

	/**
	 * Liefert TRUE, falls zu (wochentag, stunde) ein zugehöriges {@link StundenplanZeitraster}-Objekt existiert.
	 *
	 * @param wochentag  Die ENUM-ID des {@link Wochentag} des Zeitrasters.
	 * @param stunde     Die Unterrichtsstunde des Zeitrasters.
	 *
	 * @return TRUE, falls zu (wochentag, stunde) ein zugehöriges {@link StundenplanZeitraster}-Objekt existiert.
	 */
	public boolean zeitrasterExistsByWochentagAndStunde(final int wochentag, final int stunde) {
		return _zeitraster_by_wochentag_and_stunde.contains(wochentag, stunde);
	}

	/**
	 * Liefert TRUE, falls ein {@link StundenplanZeitraster}-Objekt mit dem Wochentag existiert.
	 *
	 * @param wochentag  Der Wochentag, deren Zeitrastermenge überprüft wird.
	 *
	 * @return TRUE, falls ein {@link StundenplanZeitraster}-Objekt mit dem Wochentag existiert.
	 */
	public boolean zeitrasterExistsByWochentag(final int wochentag) {
		return !MapUtils.getOrCreateArrayList(_zeitrastermenge_by_wochentag, wochentag).isEmpty();
	}

	/**
	 * Aktualisiert das vorhandene {@link StundenplanZeitraster}-Objekt durch das neue Objekt.
	 * <br>Die folgenden Attribute werden nicht aktualisiert:
	 * <br>{@link StundenplanZeitraster#id}
	 * <br>
	 * <br>Die folgenden Attribute werden kopiert:
	 * <br>{@link StundenplanZeitraster#stundenbeginn}
	 * <br>{@link StundenplanZeitraster#stundenende}
	 * <br>{@link StundenplanZeitraster#unterrichtstunde}
	 * <br>{@link StundenplanZeitraster#wochentag}
	 *
	 * @param zeitraster  Das neue {@link StundenplanZeitraster}-Objekt, dessen Attribute kopiert werden.
	 */
	public void zeitrasterPatchAttributes(final @NotNull StundenplanZeitraster zeitraster) {
		zeitrasterCheck(zeitraster);

		// Altes Objekt durch neues Objekt ersetzen
		DeveloperNotificationException.ifMapRemoveFailes(_zeitraster_by_id, zeitraster.id);
		DeveloperNotificationException.ifMapPutOverwrites(_zeitraster_by_id, zeitraster.id, zeitraster);

		zeitrasterRevalidate();
	}

	private void zeitrasterRemoveOhneUpdate(final long idZeitraster) {
		// Kaskade: StundenplanUnterricht
		for (final @NotNull StundenplanUnterricht u : DeveloperNotificationException.ifMapGetIsNull(_unterrichtmenge_by_idZeitraster, idZeitraster))
			unterrichtRemoveByIdOhneUpdate(u.id);

		// Remove
		DeveloperNotificationException.ifMapRemoveFailes(_zeitraster_by_id, idZeitraster);
	}

	/**
	 * Entfernt aus dem Stundenplan ein existierendes {@link StundenplanZeitraster}-Objekt.
	 * <br> Hinweis: Kaskadierend werden auch alle {@link StundenplanUnterricht}-Objekte gelöscht.
	 *
	 * @param idZeitraster  Die Datenbank-ID des {@link StundenplanZeitraster}-Objekts.
	 */
	public void zeitrasterRemoveById(final long idZeitraster) {
		zeitrasterRemoveOhneUpdate(idZeitraster);
		zeitrasterRevalidate();
		unterrichtRevalidate();
	}

	/**
	 * Entfernt alle {@link StundenplanZeitraster}-Objekte aus dem Stundenplan.
	 * <br> Hinweis: Kaskadierend werden auch alle {@link StundenplanUnterricht}-Objekte gelöscht.
	 *
	 * @param listZeitraster  Die {@link StundenplanZeitraster}-Objekte, die entfernt werden sollen.
	 */
	public void zeitrasterRemoveAll(final @NotNull List<@NotNull StundenplanZeitraster> listZeitraster) {
		for (final @NotNull StundenplanZeitraster zeitraster : listZeitraster)
			zeitrasterRemoveOhneUpdate(zeitraster.id);
		zeitrasterRevalidate();
		unterrichtRevalidate();
	}

	/**
	 * Entfernt alle {@link StundenplanZeitraster}-Objekte, die einen bestimmten Wochentag haben.
	 *
	 * @param wochentagEnumID  Die ID des {@link Wochentag}.
	 */
	public void zeitrasterRemoveAllByWochentag(final int wochentagEnumID) {
		zeitrasterRemoveAll(MapUtils.getOrCreateArrayList(_zeitrastermenge_by_wochentag, wochentagEnumID));
	}

}
