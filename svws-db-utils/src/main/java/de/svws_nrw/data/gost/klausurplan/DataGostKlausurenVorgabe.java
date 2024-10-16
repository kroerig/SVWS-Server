package de.svws_nrw.data.gost.klausurplan;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.ObjLongConsumer;
import java.util.stream.Collectors;

import de.svws_nrw.core.data.gost.GostFach;
import de.svws_nrw.core.data.gost.klausurplanung.GostKlausurenDataCollection;
import de.svws_nrw.core.data.gost.klausurplanung.GostKlausurtermin;
import de.svws_nrw.core.data.gost.klausurplanung.GostKlausurvorgabe;
import de.svws_nrw.core.data.gost.klausurplanung.GostKursklausur;
import de.svws_nrw.core.types.SchuelerStatus;
import de.svws_nrw.core.types.gost.GostHalbjahr;
import de.svws_nrw.core.types.gost.GostKursart;
import de.svws_nrw.core.utils.gost.klausurplanung.GostKlausurvorgabenManager;
import de.svws_nrw.data.DTOMapper;
import de.svws_nrw.data.DataBasicMapper;
import de.svws_nrw.data.DataManager;
import de.svws_nrw.data.JSONMapper;
import de.svws_nrw.data.faecher.DBUtilsFaecherGost;
import de.svws_nrw.db.DBEntityManager;
import de.svws_nrw.db.dto.current.gost.DTOGostJahrgangsdaten;
import de.svws_nrw.db.dto.current.gost.klausurplanung.DTOGostKlausurenKursklausuren;
import de.svws_nrw.db.dto.current.gost.klausurplanung.DTOGostKlausurenSchuelerklausuren;
import de.svws_nrw.db.dto.current.gost.klausurplanung.DTOGostKlausurenSchuelerklausurenTermine;
import de.svws_nrw.db.dto.current.gost.klausurplanung.DTOGostKlausurenVorgaben;
import de.svws_nrw.db.dto.current.schild.faecher.DTOFach;
import de.svws_nrw.db.dto.current.schild.kurse.DTOKurs;
import de.svws_nrw.db.dto.current.schild.schueler.DTOSchuelerLernabschnittsdaten;
import de.svws_nrw.db.dto.current.schild.schule.DTOSchuljahresabschnitte;
import de.svws_nrw.db.schema.Schema;
import de.svws_nrw.db.utils.ApiOperationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * Diese Klasse erweitert den abstrakten {@link DataManager} für den Core-DTO
 * {@link GostKlausurvorgabe}.
 */
public final class DataGostKlausurenVorgabe extends DataManager<Long> {

	private final int _abiturjahr;

	/**
	 * Erstellt einen neuen {@link DataManager} für den Core-DTO
	 * {@link GostKlausurtermin}.
	 *
	 * @param conn       die Datenbank-Verbindung für den Datenbankzugriff
	 * @param abiturjahr das Jahr, in welchem der Jahrgang Abitur machen wird
	 *
	 * @throws ApiOperationException   im Fehlerfall
	 */
	public DataGostKlausurenVorgabe(final DBEntityManager conn, final int abiturjahr) throws ApiOperationException {
		super(conn);
		_abiturjahr = abiturjahr;
		if (abiturjahr != -1 && conn.queryByKey(DTOGostJahrgangsdaten.class, abiturjahr) == null)
			throw new ApiOperationException(Status.BAD_REQUEST, "Jahrgang nicht gefunden, ID: " + abiturjahr);
	}

	@Override
	public Response getAll() {
		return this.getList();
	}

	/**
	 * Erzeugt die Gost-Kursklausuren und Gost-Schülerklausuren aus den Klausurvorlagen einer Jahrgangsstufe
	 * im übergebenen Gost-Halbjahr für die existierenden Kurse.
	 *
	 * @param hj      das Gost-Halbjahr
	 * @param quartal das Quartal
	 *
	 * @return die Anzahl der erzeugten Kursklausuren
	 *
	 * @throws ApiOperationException   im Fehlerfall
	 */
	public GostKlausurenDataCollection createKlausuren(final int hj, final int quartal) throws ApiOperationException {
		final GostHalbjahr halbjahr = GostHalbjahr.fromID(hj);

		final List<GostKlausurvorgabe> vorgaben = DataGostKlausurenVorgabe.getKlausurvorgaben(conn, _abiturjahr, hj, false);
		if (vorgaben.isEmpty())
			throw new ApiOperationException(Status.NOT_FOUND, "Keine Klausurvorgaben für dieses Halbjahr definiert.");

		final GostKlausurvorgabenManager manager = new GostKlausurvorgabenManager(vorgaben);

		final List<GostKursklausur> existingKlausuren = DataGostKlausurenKursklausur.getKursklausurenZuVorgaben(conn, vorgaben);
		final Map<Long, Map<Long, GostKursklausur>> mapKursidVorgabeIdKursklausur = existingKlausuren.stream()
				.collect(Collectors.groupingBy(k -> k.idKurs, Collectors.toMap(k -> k.idVorgabe, Function.identity())));

		final List<DTOSchuljahresabschnitte> sjaList = conn.query("SELECT s FROM DTOSchuljahresabschnitte s WHERE s.Jahr = :jahr AND s.Abschnitt = :abschnitt", DTOSchuljahresabschnitte.class)
				.setParameter("jahr", halbjahr.getSchuljahrFromAbiturjahr(_abiturjahr))
				.setParameter("abschnitt", halbjahr.id % 2 + 1)
				.getResultList();
		if (sjaList == null || sjaList.size() != 1)
			throw new ApiOperationException(Status.NOT_FOUND, "Noch kein Schuljahresabschnitt für dieses Halbjahr definiert.");

		final DTOSchuljahresabschnitte sja = sjaList.get(0);
		// Kurse ermitteln
		final List<DTOKurs> kurse = conn.query("SELECT k FROM DTOKurs k WHERE k.Schuljahresabschnitts_ID = :sja AND k.ASDJahrgang = :jg", DTOKurs.class).setParameter("sja", sja.ID) // TODO
																																														// Quartalsmodus
				.setParameter("jg", halbjahr.jahrgang).getResultList();

		final List<DTOGostKlausurenKursklausuren> kursklausuren = new ArrayList<>();
		final List<DTOGostKlausurenSchuelerklausuren> schuelerklausuren = new ArrayList<>();
		long idNextKursklausur = conn.transactionGetNextID(DTOGostKlausurenKursklausuren.class);
		for (final DTOKurs kurs : kurse) {
			final List<DTOSchuelerLernabschnittsdaten> laDaten = getLernabschnittsdatenZuKurs(hj, kurs);
			final List<GostKlausurvorgabe> listKursVorgaben = manager.vorgabeGetMengeByHalbjahrAndQuartalAndKursartallgAndFachid(_abiturjahr, halbjahr, quartal, GostKursart.fromKuerzelOrException(kurs.KursartAllg), kurs.Fach_ID);
			if (listKursVorgaben.isEmpty() && !laDaten.isEmpty())
				//TODO Fehlermeldung an Client, dass es zu diesem Kurs/Fach keine Vorgaben gibt
				throw new ApiOperationException(Status.NOT_FOUND, "Keine Klausurvorgaben für diesen Kurs definiert: " + kurs.KurzBez);
			for (final GostKlausurvorgabe vorgabe : listKursVorgaben) {
				if (!(mapKursidVorgabeIdKursklausur.containsKey(kurs.ID) && mapKursidVorgabeIdKursklausur.get(kurs.ID).containsKey(vorgabe.idVorgabe))) {
					final DTOGostKlausurenKursklausuren kursklausur = new DTOGostKlausurenKursklausuren(idNextKursklausur, vorgabe.idVorgabe, kurs.ID);
					final List<DTOGostKlausurenSchuelerklausuren> listSk = createSchuelerklausurenZuKursklausur(kursklausur, laDaten);
					if (!listSk.isEmpty()) {
						idNextKursklausur++;
						kursklausuren.add(kursklausur);
						schuelerklausuren.addAll(listSk);
					}
				}
			}
		}
		// ID in Schülerklausuren einfügen
		long idNextSchuelerklausur = conn.transactionGetNextID(DTOGostKlausurenSchuelerklausuren.class);
		for (final DTOGostKlausurenSchuelerklausuren sk : schuelerklausuren)
			sk.ID = idNextSchuelerklausur++;

		// SchülerklausurTermine erstellen und ID in SchülerklausurTermine einfügen
		final List<DTOGostKlausurenSchuelerklausurenTermine> sktermine = createSchuelerklausurenTermineZuSchuelerklausuren(schuelerklausuren);
		long idNextSchuelerklausurTermin = conn.transactionGetNextID(DTOGostKlausurenSchuelerklausurenTermine.class);
		for (final DTOGostKlausurenSchuelerklausurenTermine skt : sktermine)
			skt.ID = idNextSchuelerklausurTermin++;

		if (!conn.transactionPersistAll(kursklausuren) || !conn.transactionPersistAll(schuelerklausuren) || !conn.transactionPersistAll(sktermine))
			throw new ApiOperationException(Status.INTERNAL_SERVER_ERROR);
		final GostKlausurenDataCollection retKlausuren = new GostKlausurenDataCollection();
		retKlausuren.kursklausuren = DTOMapper.mapList(kursklausuren, DataGostKlausurenKursklausur.dtoMapper);
		retKlausuren.schuelerklausuren = DTOMapper.mapList(schuelerklausuren, DataGostKlausurenSchuelerklausur.dtoMapper);
		retKlausuren.schuelerklausurtermine = DTOMapper.mapList(sktermine, DataGostKlausurenSchuelerklausurTermin.dtoMapper);
		return retKlausuren;
	}

	private static List<DTOGostKlausurenSchuelerklausuren> createSchuelerklausurenZuKursklausur(final DTOGostKlausurenKursklausuren kursklausur, final List<DTOSchuelerLernabschnittsdaten> lernDaten) {
		final List<DTOGostKlausurenSchuelerklausuren> listSchuelerklausuren = new ArrayList<>();
		for (final DTOSchuelerLernabschnittsdaten lad : lernDaten) {
			listSchuelerklausuren.add(new DTOGostKlausurenSchuelerklausuren(-1L, kursklausur.ID, lad.Schueler_ID));
		}
		return listSchuelerklausuren;
	}

	private static List<DTOGostKlausurenSchuelerklausurenTermine> createSchuelerklausurenTermineZuSchuelerklausuren(final List<DTOGostKlausurenSchuelerklausuren> sks) {
		final List<DTOGostKlausurenSchuelerklausurenTermine> listSchuelerklausurenTermine = new ArrayList<>();
		for (final DTOGostKlausurenSchuelerklausuren sk : sks) {
			listSchuelerklausurenTermine.add(new DTOGostKlausurenSchuelerklausurenTermine(-1L, sk.ID, 0));
		}
		return listSchuelerklausurenTermine;
	}

	private List<DTOSchuelerLernabschnittsdaten> getLernabschnittsdatenZuKurs(final int hj, final DTOKurs kurs) {
		final SchuelerStatus[] validStatus = {SchuelerStatus.AKTIV, SchuelerStatus.EXTERN};
		return conn.query(
				"SELECT lad FROM DTOSchuelerLernabschnittsdaten lad JOIN DTOSchuelerLeistungsdaten sld ON sld.Abschnitt_ID = lad.ID JOIN DTOSchueler s ON lad.Schueler_ID = s.ID WHERE sld.Kurs_ID = :kursid AND sld.Kursart IN :kursart AND s.Status IN :sstatus AND s.Geloescht = :sgeloescht",
				DTOSchuelerLernabschnittsdaten.class).setParameter("kursid", kurs.ID)
				.setParameter("kursart", Arrays.asList(hj == 5 ? new String[] { "LK1", "LK2", "AB3" } : new String[] { "LK1", "LK2", "AB3", "AB4", "GKS" }))
				.setParameter("sstatus", Arrays.asList(validStatus))
				.setParameter("sgeloescht", false)
				.getResultList();
	}

	/**
	 * Lambda-Ausdruck zum Umwandeln eines Datenbank-DTOs
	 * {@link DTOGostKlausurenVorgaben} in einen Core-DTO
	 * {@link GostKlausurvorgabe}.
	 */
	public static final DTOMapper<DTOGostKlausurenVorgaben, GostKlausurvorgabe> dtoMapper = (final DTOGostKlausurenVorgaben z) -> {
		final GostKlausurvorgabe daten = new GostKlausurvorgabe();
		daten.idVorgabe = z.ID;
		daten.abiJahrgang = z.Abi_Jahrgang;
		daten.idFach = z.Fach_ID;
		daten.kursart = z.Kursart.kuerzel;
		daten.halbjahr = z.Halbjahr.id;
		daten.quartal = z.Quartal;
		daten.bemerkungVorgabe = z.Bemerkungen;
		daten.auswahlzeit = z.Auswahlzeit;
		daten.dauer = z.Dauer;
		daten.istAudioNotwendig = z.IstAudioNotwendig;
		daten.istVideoNotwendig = z.IstVideoNotwendig;
		daten.istMdlPruefung = z.IstMdlPruefung;
		return daten;
	};

	/**
	 * Liefert zu einer Halbjahres-ID das entsprechende Gost-Halbjahr.
	 *
	 * @param halbjahr das Gost-Halbjahr
	 *
	 * @return das Gost-Halbjahr
	 *
	 * @throws ApiOperationException   im Fehlerfall
	 */
	public static GostHalbjahr checkHalbjahr(final int halbjahr) throws ApiOperationException {
		final GostHalbjahr hj = GostHalbjahr.fromID(halbjahr);
		if (hj == null)
			throw new ApiOperationException(Status.BAD_REQUEST, "Kein gültiges GostHalbjahr angegeben: " + halbjahr);
		return hj;
	}

	/**
	 * Überprüft, ob der Wert für ein Quartal gültig ist.
	 *
	 * @param quartal das Quartal
	 *
	 * @return das das Quartal
	 *
	 * @throws ApiOperationException   im Fehlerfall
	 */
	public static int checkQuartal(final int quartal) throws ApiOperationException {
		if (quartal < 0)
			throw new ApiOperationException(Status.BAD_REQUEST, "Quartal ungültig: " + quartal);
		return quartal;
	}

	private static GostKursart checkKursart(final String kursart) throws ApiOperationException {
		final GostKursart ka = GostKursart.fromKuerzel(kursart);
		if (ka == null)
			throw new ApiOperationException(Status.BAD_REQUEST, "Keine gültige Kursart angegeben: " + kursart);
		return ka;
	}

	/**
	 * Gibt die Liste der Klausurvorgaben einer Jahrgangsstufe im übergebenen
	 * Gost-Halbjahr zurück.
	 *
	 * @param conn       die Datenbank-Verbindung für den Datenbankzugriff
	 * @param abiturjahr das Jahr, in welchem der Jahrgang Abitur machen wird
	 * @param halbjahr das Gost-Halbjahr
	 * @param ganzesSchuljahr true, um Vorgaben für das gesamte Schuljahr zu erhalten, false nur für das übergeben Halbjahr
	 *
	 * @return die Liste der Kursklausuren
	 *
	 * @throws ApiOperationException   im Fehlerfall
	 */
	public static List<GostKlausurvorgabe> getKlausurvorgaben(final DBEntityManager conn, final int abiturjahr, final int halbjahr, final boolean ganzesSchuljahr) throws ApiOperationException {
		final List<DTOGostKlausurenVorgaben> vorgaben = (halbjahr <= 0)
			? conn.query("SELECT v FROM DTOGostKlausurenVorgaben v WHERE v.Abi_Jahrgang = :jgid", DTOGostKlausurenVorgaben.class)
				.setParameter("jgid", abiturjahr)
				.getResultList()
			: conn.query("SELECT v FROM DTOGostKlausurenVorgaben v WHERE v.Abi_Jahrgang = :jgid AND v.Halbjahr IN :hj", DTOGostKlausurenVorgaben.class)
				.setParameter("jgid", abiturjahr)
				.setParameter("hj", Arrays.asList(ganzesSchuljahr ? GostHalbjahr.fromIDorException(halbjahr).getSchuljahr() : new GostHalbjahr[]{GostHalbjahr.fromIDorException(halbjahr)}))
				.getResultList();
		return DTOMapper.mapList(vorgaben, dtoMapper);
	}

	/**
	 * Gibt die Liste der Klausurvorgaben zu einer Menge von IDs zurück.
	 *
	 * @param conn       die Datenbank-Verbindung für den Datenbankzugriff
	 * @param vids die IDs der Kursklausuren, zu denen die Vorgaben gesucht werden.
	 *
	 * @return die Liste der Klausurvorgaben
	 *
	 * @throws ApiOperationException   im Fehlerfall
	 */
	public static List<GostKlausurvorgabe> getKlausurvorgabenZuIds(final DBEntityManager conn, final List<Long> vids) throws ApiOperationException {
		final List<DTOGostKlausurenVorgaben> vorgaben = getKlausurvorgabDTOsZuIds(conn, vids);
		return DTOMapper.mapList(vorgaben, dtoMapper);
	}

	/**
	 * Gibt die Liste der Klausurvorgaben-DTOs zu den übergebenen IDs zurück.
	 *
	 * @param conn       die Datenbank-Verbindung für den Datenbankzugriff
	 * @param vids die IDs der Kursklausuren, zu denen die Vorgaben gesucht werden.
	 *
	 * @return die Liste der Klausurvorgaben
	 *
	 * @throws ApiOperationException   im Fehlerfall
	 */
	public static List<DTOGostKlausurenVorgaben> getKlausurvorgabDTOsZuIds(final DBEntityManager conn, final List<Long> vids) throws ApiOperationException {
		if (vids.isEmpty())
			return new ArrayList<>();
		final List<DTOGostKlausurenVorgaben> vorgaben = conn.queryNamed("DTOGostKlausurenVorgaben.id.multiple", vids, DTOGostKlausurenVorgaben.class);
		if (vorgaben.isEmpty())
			throw new ApiOperationException(Status.NOT_FOUND, "Klausurvorgabe-DTOs zu angegebenen IDs nicht gefunden.");
		return vorgaben;
	}

	/**
	 * Gibt die Liste der Klausurvorgaben-DTOs zu den übergebenen IDs zurück.
	 *
	 * @param conn       die Datenbank-Verbindung für den Datenbankzugriff
	 * @param kks die Kursklausur-DTOs, zu denen die Vorgaben gesucht werden.
	 *
	 * @return die Liste der Klausurvorgaben
	 *
	 * @throws ApiOperationException   im Fehlerfall
	 */
	public static List<DTOGostKlausurenVorgaben> getKlausurvorgabeDTOsZuKursklausurDTOs(final DBEntityManager conn, final List<DTOGostKlausurenKursklausuren> kks) throws ApiOperationException {
		return getKlausurvorgabDTOsZuIds(conn, kks.stream().map(kk -> kk.Vorgabe_ID).toList());
	}

	/**
	 * Gibt die Liste der Klausurvorgaben-DTOs zu den übergebenen IDs zurück.
	 *
	 * @param conn       die Datenbank-Verbindung für den Datenbankzugriff
	 * @param kks die Kursklausur-DTOs, zu denen die Vorgaben gesucht werden.
	 *
	 * @return die Liste der Klausurvorgaben
	 *
	 * @throws ApiOperationException   im Fehlerfall
	 */
	public static List<GostKlausurvorgabe> getKlausurvorgabenZuKursklausurDTOs(final DBEntityManager conn, final List<DTOGostKlausurenKursklausuren> kks) throws ApiOperationException {
		final List<DTOGostKlausurenVorgaben> vorgaben = getKlausurvorgabeDTOsZuKursklausurDTOs(conn, kks);
		return DTOMapper.mapList(vorgaben, dtoMapper);
	}

	/**
	 * Gibt die Liste der Klausurvorgaben zu einer Menge von Kursklausuren zurück.
	 *
	 * @param conn       die Datenbank-Verbindung für den Datenbankzugriff
	 * @param kks die Kursklausuren, zu denen die Vorgaben gesucht werden.
	 *
	 * @return die Liste der Klausurvorgaben
	 *
	 * @throws ApiOperationException   im Fehlerfall
	 */
	public static List<GostKlausurvorgabe> getKlausurvorgabenZuKursklausuren(final DBEntityManager conn, final List<GostKursklausur> kks) throws ApiOperationException {
		return getKlausurvorgabenZuIds(conn, kks.stream().map(kk -> kk.idVorgabe).toList());
	}

	@Override
	public Response get(final Long halbjahr) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Response patch(final Long id, final InputStream is) throws ApiOperationException {
		return super.patchBasicFiltered(id, is, DTOGostKlausurenVorgaben.class, patchMappings, requiredCreateAttributes);
	}

	@Override
	public Response getList() {
		throw new UnsupportedOperationException();
	}

	private static final Set<String> requiredCreateAttributes = Set.of("abiJahrgang", "halbjahr", "quartal", "idFach", "kursart");

	private final Map<String, DataBasicMapper<DTOGostKlausurenVorgaben>> patchMappings = Map.ofEntries(Map.entry("idVorgabe", (conn, dto, value, map) -> {
		final Long patch_id = JSONMapper.convertToLong(value, true);
		if ((patch_id == null) || (patch_id.longValue() != dto.ID))
			throw new ApiOperationException(Status.BAD_REQUEST);
	}), Map.entry("abiJahrgang", (conn, dto, value, map) -> {
		dto.Abi_Jahrgang = JSONMapper.convertToInteger(value, false);
		if (conn.queryByKey(DTOGostJahrgangsdaten.class, dto.Abi_Jahrgang) == null)
			throw new ApiOperationException(Status.BAD_REQUEST, "Jahrgang nicht gefunden, ID: " + dto.Abi_Jahrgang);
	}), Map.entry("halbjahr", (conn, dto, value, map) -> dto.Halbjahr = checkHalbjahr(JSONMapper.convertToInteger(value, false))),
			Map.entry("quartal", (conn, dto, value, map) -> dto.Quartal = checkQuartal(JSONMapper.convertToInteger(value, false))), Map.entry("idFach", (conn, dto, value, map) -> {
				dto.Fach_ID = JSONMapper.convertToLong(value, false);
				if (conn.queryByKey(DTOFach.class, dto.Fach_ID) == null)
					throw new ApiOperationException(Status.BAD_REQUEST, "Fach nicht gefunden, ID: " + dto.Fach_ID);
			}), Map.entry("kursart", (conn, dto, value, map) -> dto.Kursart = checkKursart(JSONMapper.convertToString(value, false, false, null))),
			Map.entry("dauer", (conn, dto, value, map) -> dto.Dauer = JSONMapper.convertToInteger(value, false)),
			Map.entry("auswahlzeit", (conn, dto, value, map) -> dto.Auswahlzeit = JSONMapper.convertToInteger(value, false)),
			Map.entry("istMdlPruefung", (conn, dto, value, map) -> dto.IstMdlPruefung = JSONMapper.convertToBoolean(value, false)),
			Map.entry("istAudioNotwendig", (conn, dto, value, map) -> dto.IstAudioNotwendig = JSONMapper.convertToBoolean(value, false)),
			Map.entry("istVideoNotwendig", (conn, dto, value, map) -> dto.IstVideoNotwendig = JSONMapper.convertToBoolean(value, false)), Map.entry("bemerkungVorgabe",
					(conn, dto, value, map) -> dto.Bemerkungen = JSONMapper.convertToString(value, true, true, Schema.tab_Gost_Klausuren_Vorgaben.col_Bemerkungen.datenlaenge())));

	/**
	 * Erstellt eine neue Gost-Klausurvorgabe
	 *
	 * @param is Das JSON-Objekt mit den Daten
	 *
	 * @return Eine Response mit der neuen Gost-Klausurvorgabe
	 *
	 * @throws ApiOperationException   im Fehlerfall
	 */
	public Response create(final InputStream is) throws ApiOperationException {
		final ObjLongConsumer<DTOGostKlausurenVorgaben> initDTO = (dto, id) -> dto.ID = id;
		return super.addBasic(is, DTOGostKlausurenVorgaben.class, initDTO, dtoMapper, requiredCreateAttributes, patchMappings);
	}

	/**
	 * Löscht eine Gost-Klausurvorgabe *
	 *
	 * @param id die ID der zu löschenden Klausurvorgabe
	 *
	 * @return die Response
	 *
	 * @throws ApiOperationException   im Fehlerfall
	 */
	public Response delete(final Long id) throws ApiOperationException {
		return super.deleteBasic(id, DTOGostKlausurenVorgaben.class, dtoMapper);
	}

	/**
	 * Kopiert die Klausurvorgaben in einen Abiturjahrgang
	 *
	 * @param halbjahr das Halbjahr der gymnasialen Oberstufe
	 * @param quartal  das Quartal, 0 für das gesamte Halbjahr
	 *
	 * @return erfolgreich / nicht erfolgreich
	 *
	 * @throws ApiOperationException   im Fehlerfall
	 */
	public List<GostKlausurvorgabe> copyVorgaben(final int halbjahr, final int quartal) throws ApiOperationException {
		checkQuartal(quartal);
		return copyVorgabenToJahrgang(conn, _abiturjahr, checkHalbjahr(halbjahr), checkQuartal(quartal));
	}

	/**
	 * Kopiert die Klausurvorgaben in einen Abiturjahrgang
	 *
	 * @param conn       die Datenbankverbindung
	 * @param abiturjahr das Abiturjahr
	 * @param halbjahr   das Halbjahr der gymnasialen Oberstufe
	 * @param quartal    das Quartal, 0 für das gesamte Halbjahr
	 *
	 * @return erfolgreich / nicht erfolgreich
	 *
	 * @throws ApiOperationException   im Fehlerfall
	 */
	public static List<GostKlausurvorgabe> copyVorgabenToJahrgang(final DBEntityManager conn, final int abiturjahr, final GostHalbjahr halbjahr, final int quartal) throws ApiOperationException {
		final List<DTOGostKlausurenVorgaben> vorgabenVorlage = conn.queryNamed("DTOGostKlausurenVorgaben.abi_jahrgang", -1, DTOGostKlausurenVorgaben.class);
		final List<DTOGostKlausurenVorgaben> vorgabenJg = conn.queryNamed("DTOGostKlausurenVorgaben.abi_jahrgang", abiturjahr, DTOGostKlausurenVorgaben.class);
		// Prüfe, ob die Vorlage eingelesen werden kann
		if (vorgabenVorlage == null)
			throw new ApiOperationException(Status.INTERNAL_SERVER_ERROR);

		// Bestimme die ID, für welche der Datensatz eingefügt wird
		long idNMK = conn.transactionGetNextID(DTOGostKlausurenVorgaben.class);
		final List<DTOGostKlausurenVorgaben> vorgabenNeu = new ArrayList<>();
		for (final DTOGostKlausurenVorgaben vorgabe : vorgabenVorlage) {
			if (halbjahr != null && vorgabe.Halbjahr != halbjahr || quartal > 0 && quartal != vorgabe.Quartal)
				continue;
			boolean exists = false;
			for (final DTOGostKlausurenVorgaben v : vorgabenJg) {
				if ((vorgabe.Halbjahr.id == v.Halbjahr.id) && (vorgabe.Quartal == v.Quartal) && (vorgabe.Fach_ID == v.Fach_ID) && vorgabe.Kursart.equals(v.Kursart)) {
					exists = true;
					break;
				}
			}
			if (!exists) {
				final DTOGostKlausurenVorgaben k = new DTOGostKlausurenVorgaben(idNMK++, abiturjahr, vorgabe.Halbjahr, vorgabe.Quartal, vorgabe.Fach_ID, vorgabe.Kursart, vorgabe.Dauer,
						vorgabe.Auswahlzeit, vorgabe.IstMdlPruefung, vorgabe.IstAudioNotwendig, vorgabe.IstVideoNotwendig);
				k.Bemerkungen = vorgabe.Bemerkungen;
				vorgabenNeu.add(k);
			}
		}
		if (!conn.transactionPersistAll(vorgabenNeu))
			throw new ApiOperationException(Status.INTERNAL_SERVER_ERROR, "Fehler beim Persistieren der Gost-Klausurvorgaben.");
		return DTOMapper.mapList(vorgabenNeu, dtoMapper);
	}

	/**
	 * Legt für alle Jahrgänge die Klausurvorgaben laut APO-GOSt an.
	 *
	 * @param conn     die Datenbankverbindung
	 * @param halbjahr das Halbjahr der gymnasialen Oberstufe
	 * @param quartal  das Quartal, 0 für das gesamte Halbjahr
	 *
	 * @return die Liste der neuen Klausurvorgaben
	 *
	 * @throws ApiOperationException   im Fehlerfall
	 */
	public static List<GostKlausurvorgabe> createDefaultVorgaben(final DBEntityManager conn, final GostHalbjahr halbjahr, final int quartal) throws ApiOperationException {
		final List<DTOGostKlausurenVorgaben> vorgabenVorlage = conn.queryNamed("DTOGostKlausurenVorgaben.abi_jahrgang", -1, DTOGostKlausurenVorgaben.class);
		// Prüfe, ob die Vorlage eingelesen werden kann
		if (vorgabenVorlage == null)
			throw new ApiOperationException(Status.INTERNAL_SERVER_ERROR);
		final EnumMap<GostHalbjahr, GostKlausurvorgabenManager> manager = new EnumMap<>(GostHalbjahr.class);
		for (final GostHalbjahr hj : GostHalbjahr.values())
			manager.put(hj, new GostKlausurvorgabenManager(DTOMapper.mapList(vorgabenVorlage.stream().filter(v -> v.Halbjahr == hj).toList(), dtoMapper)));
		final List<GostFach> faecher = DBUtilsFaecherGost.getFaecherManager(conn, null).getFaecherSchriftlichMoeglich();
		final List<DTOGostKlausurenVorgaben> neueVorgaben = new ArrayList<>();
		// Bestimme die ID, für welche der Datensatz eingefügt wird
		long idNMK = conn.transactionGetNextID(DTOGostKlausurenVorgaben.class);
		final Set<Integer> quartale = new HashSet<>();
		if (quartal == 0) {
			quartale.add(1);
			quartale.add(2);
		} else
			quartale.add(quartal);
		final GostKursart[] arten = halbjahr.istEinfuehrungsphase() ? new GostKursart[] { GostKursart.GK } : new GostKursart[] { GostKursart.GK, GostKursart.LK };
		for (final GostFach fach : faecher) {
			for (final GostKursart ka : arten) {
				if (ka == GostKursart.LK && !fach.istMoeglichAbiLK || halbjahr == GostHalbjahr.Q22 && !(fach.istMoeglichAbiGK || fach.istMoeglichAbiLK))
					continue;
				for (final int q : quartale) {
					final DTOGostKlausurenVorgaben vorgabeNeu = new DTOGostKlausurenVorgaben(idNMK++, -1, halbjahr, q, fach.id, ka, berechneApoKlausurdauer(halbjahr, ka, fach), 0, false, false, false);
					if (manager.get(vorgabeNeu.Halbjahr).vorgabeGetByHalbjahrAndQuartalAndKursartallgAndFachid(-1, halbjahr, vorgabeNeu.Quartal, vorgabeNeu.Kursart, vorgabeNeu.Fach_ID) == null)
						neueVorgaben.add(vorgabeNeu);
				}
			}
		}
		if (!conn.transactionPersistAll(neueVorgaben))
			throw new ApiOperationException(Status.INTERNAL_SERVER_ERROR, "Fehler beim Persistieren der Gost-Klausurvorgaben.");
		return DTOMapper.mapList(neueVorgaben, dtoMapper);
	}

	private static int berechneApoKlausurdauer(final GostHalbjahr halbjahr, final GostKursart kursart, final GostFach fach) {
		if (halbjahr.istEinfuehrungsphase())
			return 90;
		if (kursart == GostKursart.LK) {
			if (halbjahr.id <= 3)
				return 180;
			if (halbjahr.id == 4)
				return 225;
			return fach.istFremdsprache ? 315 : 300;
		}
		if (halbjahr.id <= 3)
			return 135;
		if (halbjahr.id == 4)
			return 180;
		return fach.istFremdsprache ? 285 : 255;
	}

}
