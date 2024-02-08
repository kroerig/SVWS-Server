package de.svws_nrw.data.gost;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.svws_nrw.core.data.gost.GostJahrgang;
import de.svws_nrw.core.data.gost.GostJahrgangsdaten;
import de.svws_nrw.core.types.Note;
import de.svws_nrw.core.types.gost.GostHalbjahr;
import de.svws_nrw.core.types.gost.GostKursart;
import de.svws_nrw.core.types.jahrgang.Jahrgaenge;
import de.svws_nrw.core.types.kurse.ZulaessigeKursart;
import de.svws_nrw.core.types.schule.Schulform;
import de.svws_nrw.core.utils.gost.GostAbiturjahrUtils;
import de.svws_nrw.core.utils.jahrgang.JahrgangsUtils;
import de.svws_nrw.data.DataManager;
import de.svws_nrw.data.gost.klausurplan.DataGostKlausurenVorgabe;
import de.svws_nrw.db.DBEntityManager;
import de.svws_nrw.db.dto.current.gost.DTOGostJahrgangFachkombinationen;
import de.svws_nrw.db.dto.current.gost.DTOGostJahrgangFaecher;
import de.svws_nrw.db.dto.current.gost.DTOGostJahrgangsdaten;
import de.svws_nrw.db.dto.current.gost.DTOGostSchueler;
import de.svws_nrw.db.dto.current.gost.DTOGostSchuelerFachbelegungen;
import de.svws_nrw.db.dto.current.schild.faecher.DTOFach;
import de.svws_nrw.db.dto.current.schild.schueler.DTOSchueler;
import de.svws_nrw.db.dto.current.schild.schueler.DTOSchuelerLeistungsdaten;
import de.svws_nrw.db.dto.current.schild.schueler.DTOSchuelerLernabschnittsdaten;
import de.svws_nrw.db.dto.current.schild.schule.DTOEigeneSchule;
import de.svws_nrw.db.dto.current.schild.schule.DTOJahrgang;
import de.svws_nrw.db.dto.current.schild.schule.DTOSchuljahresabschnitte;
import de.svws_nrw.db.utils.OperationError;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * Diese Klasse erweitert den abstrakten {@link DataManager} für den Core-DTO
 * {@link GostJahrgang}.
 */
public final class DataGostJahrgangsliste extends DataManager<Integer> {

	/**
	 * Erstellt einen neuen {@link DataManager} für den Core-DTO
	 * {@link GostJahrgang}.
	 *
	 * @param conn die Datenbank-Verbindung für den Datenbankzugriff
	 */
	public DataGostJahrgangsliste(final DBEntityManager conn) {
		super(conn);
	}

	@Override
	public Response getAll() {
		final DTOEigeneSchule schule = DBUtilsGost.pruefeSchuleMitGOSt(conn);

		// Bestimme den aktuellen Schuljahresabschnitt der Schule
		final DTOSchuljahresabschnitte aktuellerAbschnitt = conn.queryByKey(DTOSchuljahresabschnitte.class, schule.Schuljahresabschnitts_ID);
		if (aktuellerAbschnitt == null)
			return OperationError.NOT_FOUND.getResponse();

		// Bestimme die Jahrgaenge der Schule
		final List<DTOJahrgang> dtosJahrgaenge = conn.queryAll(DTOJahrgang.class);
		if ((dtosJahrgaenge == null) || (dtosJahrgaenge.isEmpty()))
			return OperationError.NOT_FOUND.getResponse();

		// Lese alle Abiturjahrgänge aus der Datenbank ein und ergänze diese im Vektor
		final ArrayList<GostJahrgang> daten = new ArrayList<>();
		List<DTOGostJahrgangsdaten> dtos = conn.queryAll(DTOGostJahrgangsdaten.class);
		if (dtos != null) {
			dtos = dtos.stream().sorted((a, b) -> Integer.compare(a.Abi_Jahrgang, b.Abi_Jahrgang)).toList();
			for (final DTOGostJahrgangsdaten jahrgangsdaten : dtos) {
				if (jahrgangsdaten.Abi_Jahrgang < 0)
					continue;
				final GostJahrgang eintrag = new GostJahrgang();
				eintrag.abiturjahr = jahrgangsdaten.Abi_Jahrgang;
				final int restjahre = jahrgangsdaten.Abi_Jahrgang - aktuellerAbschnitt.Jahr;
				for (final DTOJahrgang jahrgang : dtosJahrgaenge) {
					Integer jahrgangRestjahre = JahrgangsUtils.getRestlicheJahre(schule.Schulform, jahrgang.Gliederung, jahrgang.ASDJahrgang);
					if ((jahrgangRestjahre != null) && (schule.Schulform != Schulform.GY) && JahrgangsUtils.istSekI(jahrgang.ASDJahrgang))
						jahrgangRestjahre += 3;
					if (jahrgangRestjahre != null && restjahre == jahrgangRestjahre) {
						eintrag.jahrgang = jahrgang.ASDJahrgang;
						if (JahrgangsUtils.istGymOb(jahrgang.ASDJahrgang))
							break;
					}
				}
				eintrag.bezeichnung = "Abi " + eintrag.abiturjahr + ((eintrag.jahrgang == null) ? "" : " (" + eintrag.jahrgang + ")");
				eintrag.istAbgeschlossen = (restjahre < 1);
				daten.add(eintrag);
			}
		}

		// Füge einen Eintrag ein, der für keinen Abiturjahrgang steht - also für
		// allgemeine Informationen zu Abiturjahrgängen
		final GostJahrgang eintrag = new GostJahrgang();
		eintrag.abiturjahr = -1;
		eintrag.jahrgang = null;
		eintrag.bezeichnung = "Allgemein / Vorlage";
		eintrag.istAbgeschlossen = false;
		daten.add(0, eintrag);
		return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(daten).build();
	}

	@Override
	public Response getList() {
		return this.getAll();
	}

	@Override
	public Response get(final Integer id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Response patch(final Integer id, final InputStream is) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Erstellt einen neuen Abiturjahrgang für die gymnasiale Oberstufe. Dabei
	 * werden die Daten des Jahrgangs mit der übergebenen ID als Grundlage
	 * verwendet.
	 *
	 * @param jahrgang_id die ID des Jahrgangs
	 *
	 * @return die HTTP-Response, im Erfolgsfall mit dem Abiturjahrgang
	 */
	public Response create(final long jahrgang_id) {
		// Prüfe die Schuldaten
		final DTOEigeneSchule schule = DBUtilsGost.pruefeSchuleMitGOSt(conn);
		final DTOSchuljahresabschnitte aktuellerAbschnitt = conn.queryByKey(DTOSchuljahresabschnitte.class, schule.Schuljahresabschnitts_ID);
		if (aktuellerAbschnitt == null)
			return OperationError.NOT_FOUND.getResponse();

		// Ermittle die Jahrgangsdaten und bestimme das Abiturjahr
		final DTOJahrgang jahrgang = conn.queryByKey(DTOJahrgang.class, jahrgang_id);
		if (jahrgang == null)
			return OperationError.NOT_FOUND.getResponse();
		final Integer abiturjahr = GostAbiturjahrUtils.getGostAbiturjahr(schule.Schulform, jahrgang.Gliederung, aktuellerAbschnitt.Jahr, jahrgang.ASDJahrgang);
		if (abiturjahr == null)
			return OperationError.NOT_FOUND.getResponse();

		// Prüfe, ob der Abiturjahrgang bereits angelegt wurde
		DTOGostJahrgangsdaten jahrgangsdaten = conn.queryByKey(DTOGostJahrgangsdaten.class, abiturjahr);
		if (jahrgangsdaten != null)
			return OperationError.CONFLICT.getResponse();

		// Lade die Vorlage für den neuen Abiturjahrgang
		final DTOGostJahrgangsdaten jahrgangsdatenVorlage = DataGostJahrgangsdaten.getVorlage(conn);

		// Erstelle die Jahrgangsdaten mit Default-Werten, Beratungslehrer sind zunächst
		// nicht zugeordnet
		jahrgangsdaten = new DTOGostJahrgangsdaten(abiturjahr);
		jahrgangsdaten.ZusatzkursGEErstesHalbjahr = jahrgangsdatenVorlage.ZusatzkursGEErstesHalbjahr;
		jahrgangsdaten.ZusatzkursGEVorhanden = jahrgangsdatenVorlage.ZusatzkursGEVorhanden;
		jahrgangsdaten.ZusatzkursSWErstesHalbjahr = jahrgangsdatenVorlage.ZusatzkursSWErstesHalbjahr;
		jahrgangsdaten.ZusatzkursSWVorhanden = jahrgangsdatenVorlage.ZusatzkursSWVorhanden;
		jahrgangsdaten.TextBeratungsbogen = jahrgangsdatenVorlage.TextBeratungsbogen;
		jahrgangsdaten.TextMailversand = jahrgangsdatenVorlage.TextMailversand;
		if (!conn.transactionPersist(jahrgangsdaten))
			return OperationError.INTERNAL_SERVER_ERROR.getResponse();
		conn.transactionFlush();
		// Kopiere die Fächer der Gymnasialen Oberstufe aus der allgemeinen Vorlage
		final List<DTOFach> faecher = conn.queryNamed("DTOFach.istoberstufenfach", true, DTOFach.class);
		if (faecher == null)
			throw new NullPointerException();
		for (final DTOFach fach : faecher) {
			if ((fach.Sichtbar == null) || (!fach.Sichtbar))
				continue;
			final DTOGostJahrgangFaecher gostFach = new DTOGostJahrgangFaecher(abiturjahr, fach.ID, fach.IstMoeglichEF1, fach.IstMoeglichEF2, fach.IstMoeglichQ11, fach.IstMoeglichQ12, fach.IstMoeglichQ21,
					fach.IstMoeglichQ22, fach.IstMoeglichAbiGK, fach.IstMoeglichAbiLK);
			gostFach.WochenstundenQPhase = fach.WochenstundenQualifikationsphase;
			if (!conn.transactionPersist(gostFach))
				throw OperationError.INTERNAL_SERVER_ERROR.exception("Fehler beim Persistieren des Faches der gymnasialen Oberstufe");
		}
		conn.transactionFlush();
		// Kopiere die Informationen zu nicht möglichen und geforderten
		// Fachkombinationen aus der Vorlage
		final List<DTOGostJahrgangFachkombinationen> faecherKombis = conn.queryNamed("DTOGostJahrgangFachkombinationen.abi_jahrgang", -1, DTOGostJahrgangFachkombinationen.class);
		if (faecherKombis == null)
			throw new NullPointerException();
		if (!faecherKombis.isEmpty()) {
			// Bestimme die ID, für welche der Datensatz eingefügt wird
			long idNMK = conn.transactionGetNextID(DTOGostJahrgangFachkombinationen.class);
			for (final DTOGostJahrgangFachkombinationen kombi : faecherKombis) {
				final DTOGostJahrgangFachkombinationen k = new DTOGostJahrgangFachkombinationen(idNMK++, abiturjahr, kombi.Fach1_ID, kombi.Fach2_ID, kombi.EF1, kombi.EF2, kombi.Q11, kombi.Q12, kombi.Q21,
						kombi.Q22, kombi.Typ, kombi.Hinweistext);
				k.Abi_Jahrgang = abiturjahr;
				k.Kursart1 = kombi.Kursart1;
				k.Kursart2 = kombi.Kursart2;
				if (!conn.transactionPersist(k))
					return OperationError.INTERNAL_SERVER_ERROR.getResponse();
			}
		}
		conn.transactionFlush();
		// Kopiere die Vorlage für neue Laufbahnplanungen aus dem Vorlage-Jahrgang
		try {
			DataGostJahrgangLaufbahnplanung.transactionResetJahrgang(conn, jahrgangsdaten);
		} catch (final Exception e) {
			if (e instanceof final WebApplicationException webAppException)
				return webAppException.getResponse();
			return OperationError.INTERNAL_SERVER_ERROR.getResponse();
		}
		conn.transactionFlush();
		// Bestimme die Fachwahlen aus ggf. schon bestehenden Lernabschnitten
		final Jahrgaenge jg = Jahrgaenge.getByKuerzel(jahrgang.ASDJahrgang);
		if ((jg == Jahrgaenge.JG_EF) || (jg == Jahrgaenge.JG_Q1) || (jg == Jahrgaenge.JG_Q2)) {
			// Bestimme alle Schüler-IDs des angegebenen Abiturjahrgangs
			final Map<Long, DTOFach> mapFaecher = faecher.stream().collect(Collectors.toMap(f -> f.ID, f -> f));
			final List<DTOSchueler> schueler = DBUtilsGostLaufbahn.getSchuelerOfAbiturjahrgang(conn, abiturjahr);
			if ((schueler != null) && (!schueler.isEmpty())) {
				final List<Long> schuelerIDs = schueler.stream().map(s -> s.ID).toList();
				final List<Integer> abschnitte = Arrays.asList(1, 2);
				final List<DTOSchuljahresabschnitte> schuljahresabschnitte = conn.queryNamed("DTOSchuljahresabschnitte.abschnitt.multiple", abschnitte, DTOSchuljahresabschnitte.class);
				final List<Long> schuljahresabschnittIDs = schuljahresabschnitte.stream().map(a -> a.ID).toList();
				final Map<Long, DTOSchuljahresabschnitte> mapSchuljahresabschnitte = schuljahresabschnitte.stream().collect(Collectors.toMap(s -> s.ID, s -> s));
				final List<DTOSchuelerLernabschnittsdaten> lernabschnitte = conn.queryList(
						"SELECT e FROM DTOSchuelerLernabschnittsdaten e WHERE e.Schueler_ID IN ?1 AND e.WechselNr = 0 AND e.ASDJahrgang IN ('EF', 'Q1', 'Q2') AND e.Schuljahresabschnitts_ID IN ?2 AND e.SemesterWertung = true",
						DTOSchuelerLernabschnittsdaten.class, schuelerIDs, schuljahresabschnittIDs);
				final List<Long> lernabschnittIDs = lernabschnitte.stream().map(l -> l.ID).toList();
				final Map<Long, List<DTOSchuelerLernabschnittsdaten>> mapLernabschnitte = lernabschnitte.stream().collect(Collectors.groupingBy(l -> l.Schueler_ID));
				final List<DTOSchuelerLeistungsdaten> leistungsdaten = conn.queryNamed("DTOSchuelerLeistungsdaten.abschnitt_id.multiple", lernabschnittIDs, DTOSchuelerLeistungsdaten.class);
				final Map<Long, List<DTOSchuelerLeistungsdaten>> mapLeistungsdaten = leistungsdaten.stream().collect(Collectors.groupingBy(l -> l.Abschnitt_ID));

				for (final long schueler_id : schuelerIDs) {
					final List<DTOSchuelerLernabschnittsdaten> slas = mapLernabschnitte.get(schueler_id);
					if ((slas == null) || (slas.isEmpty()))
						continue;
					final HashMap<Long, DTOGostSchuelerFachbelegungen> fachbelegungen = new HashMap<>();
					final DTOGostSchuelerFachbelegungen[] abifach = new DTOGostSchuelerFachbelegungen[4];
					final GostHalbjahr[] abifachHalbjahr = new GostHalbjahr[4];
					for (final DTOSchuelerLernabschnittsdaten sla : slas) {
						final List<DTOSchuelerLeistungsdaten> slds = mapLeistungsdaten.get(sla.ID);
						if ((slds == null) || (slds.isEmpty()))
							continue;
						final DTOSchuljahresabschnitte schuljahresabschnitt = mapSchuljahresabschnitte.get(sla.Schuljahresabschnitts_ID);
						if (schuljahresabschnitt == null)
							continue;
						final GostHalbjahr halbjahr = GostHalbjahr.fromJahrgangUndHalbjahr(sla.ASDJahrgang, schuljahresabschnitt.Abschnitt);
						if (halbjahr == null)
							continue;
						for (final DTOSchuelerLeistungsdaten sld : slds) {
							final DTOFach fach = mapFaecher.get(sld.Fach_ID);
							if ((fach == null) || (!fach.IstOberstufenFach))
								continue;
							DTOGostSchuelerFachbelegungen fachbelegung = fachbelegungen.get(fach.ID);
							if (fachbelegung == null) {
								fachbelegung = new DTOGostSchuelerFachbelegungen(schueler_id, fach.ID);
								fachbelegungen.put(fach.ID, fachbelegung);
							}
							// Prüfe, ob das Abiturfach in dem Halbjahr gesetzt wurde. Nehme den jeweils
							// letzten Eintrag in den Halbjahren
							try {
								final int abifachNr = Integer.parseInt(sld.AbiFach);
								if (((abifachNr > 0) && (abifachNr < 5))
										&& ((abifachHalbjahr[abifachNr - 1] == null) || (abifachHalbjahr[abifachNr - 1].id < halbjahr.id))) {
									abifach[abifachNr - 1] = fachbelegung;
									abifachHalbjahr[abifachNr - 1] = halbjahr;
								}
							} catch (@SuppressWarnings("unused") final NumberFormatException nfe) {
								// kein gültiges Abbiturfach bei dem Lernabschnitt angegeben
							}
							// Setze Fachwahl für das Halbjahr
							switch (halbjahr) {
								case EF1 -> {
									fachbelegung.EF1_Kursart = funcGetKursart.apply(sld, halbjahr);
									fachbelegung.EF1_Punkte = funcGetNotenpunkte.apply(sld.NotenKrz);
								}
								case EF2 -> {
									fachbelegung.EF2_Kursart = funcGetKursart.apply(sld, halbjahr);
									fachbelegung.EF2_Punkte = funcGetNotenpunkte.apply(sld.NotenKrz);
								}
								case Q11 -> {
									fachbelegung.Q11_Kursart = funcGetKursart.apply(sld, halbjahr);
									fachbelegung.Q11_Punkte = funcGetNotenpunkte.apply(sld.NotenKrz);
								}
								case Q12 -> {
									fachbelegung.Q12_Kursart = funcGetKursart.apply(sld, halbjahr);
									fachbelegung.Q12_Punkte = funcGetNotenpunkte.apply(sld.NotenKrz);
								}
								case Q21 -> {
									fachbelegung.Q21_Kursart = funcGetKursart.apply(sld, halbjahr);
									fachbelegung.Q21_Punkte = funcGetNotenpunkte.apply(sld.NotenKrz);
								}
								case Q22 -> {
									fachbelegung.Q22_Kursart = funcGetKursart.apply(sld, halbjahr);
									fachbelegung.Q22_Punkte = funcGetNotenpunkte.apply(sld.NotenKrz);
								}
							}
						}
					}
					for (int i = 1; i <= 4; i++)
						if (abifach[i - 1] != null)
							abifach[i - 1].AbiturFach = i;
					if (!conn.transactionPersist(new DTOGostSchueler(schueler_id, false)))
						return OperationError.INTERNAL_SERVER_ERROR.getResponse();
					conn.transactionFlush();
					for (final Map.Entry<Long, DTOGostSchuelerFachbelegungen> entry : fachbelegungen.entrySet()) {
						if (!conn.transactionPersist(entry.getValue()))
							return OperationError.INTERNAL_SERVER_ERROR.getResponse();
					}
					conn.transactionFlush();
				}
			}
		}

		// Kopiere die Informationen zu Gost-Klausurvorgaben aus der Vorlage
		DataGostKlausurenVorgabe.copyVorgabenToJahrgang(conn, abiturjahr, null, 0);

		return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(abiturjahr).build();
	}

	private final Function<Note, String> funcGetNotenpunkte = (final Note note) -> {
		if (note == null)
			return null;
		if (note.istNote())
			return "" + note.notenpunkte;
		return switch (note) {
			case ATTEST, E1_MIT_BESONDEREM_ERFOLG_TEILGENOMMEN, E2_MIT_ERFOLG_TEILGENOMMEN, E3_TEILGENOMMEN -> note.kuerzel;
			default -> null;
		};
	};

	private final BiFunction<DTOSchuelerLeistungsdaten, GostHalbjahr, String> funcGetKursart = (final DTOSchuelerLeistungsdaten sld, final GostHalbjahr halbjahr) -> {
		final GostKursart kursart = GostKursart.fromKuerzel(sld.KursartAllg);
		final ZulaessigeKursart zulkursart = ZulaessigeKursart.getByASDKursart(sld.Kursart);
		if ((kursart == null) || (zulkursart == null))
			return null;
		if (((kursart == GostKursart.LK) || kursart == GostKursart.GK) && (sld.NotenKrz == Note.ATTEST))
			return "AT";
		return switch (kursart) {
			case LK -> "LK";
			case GK -> ((zulkursart == ZulaessigeKursart.GKS) || ((zulkursart == ZulaessigeKursart.AB3) || ((zulkursart == ZulaessigeKursart.AB3) && (halbjahr != GostHalbjahr.Q22)))) ? "S" : "M";
			case ZK -> "ZK";
			case PJK -> "M";
			case VTF -> "M";
		};
	};


	/**
	 * Entfernt den angegebenen Abiturjahrgang aus der Datenbank, sofern dieser
	 * nicht bereits persistierte Leistungsdaten hat oder bereits abgeschlossen ist.
	 *
	 * @param abiturjahrgang   der zu entfernende Abiturjahrgang
	 *
	 * @return die HTTP-Response im Erfolgsfall
	 */
	public Response delete(final int abiturjahrgang) {
		// Prüfe, ob der Abiturjahrgang existiert und bereits persistierte Leistungsdaten hat...
		final @NotNull GostJahrgangsdaten jahrgangsdaten = DataGostJahrgangsdaten.getJahrgangsdaten(conn, abiturjahrgang);
		for (final GostHalbjahr hj : GostHalbjahr.values())
			if (jahrgangsdaten.istBlockungFestgelegt[hj.id])
				throw OperationError.BAD_REQUEST.exception("Ein Abiturjahrgang mit bereits vorhandenen Leistungsdaten kann nicht entfernt werden.");
		if (jahrgangsdaten.istAbgeschlossen)
			throw OperationError.BAD_REQUEST.exception("Ein abgeschlossener Abiturjahrgang kann nicht entfernt werden.");
		// Entferne die Jahrgangsdaten des Abiturjahrgangs aus der Datenbank. Die zugehörigen Fachwahlen, etc. werden dann kaskadierend entfernt.
		final DTOGostJahrgangsdaten dto = conn.queryByKey(DTOGostJahrgangsdaten.class, abiturjahrgang);
		if (dto == null)
			return OperationError.NOT_FOUND.getResponse();
		conn.transactionRemove(dto);
		conn.transactionFlush();
		return Response.status(Status.NO_CONTENT).build();
	}

}
