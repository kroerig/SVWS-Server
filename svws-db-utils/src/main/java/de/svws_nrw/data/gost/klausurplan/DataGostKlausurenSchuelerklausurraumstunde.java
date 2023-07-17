package de.svws_nrw.data.gost.klausurplan;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Stream;

import de.svws_nrw.core.data.gost.klausuren.GostCollectionSkrsKrs;
import de.svws_nrw.core.data.gost.klausuren.GostKlausurraum;
import de.svws_nrw.core.data.gost.klausuren.GostKlausurraumstunde;
import de.svws_nrw.core.data.gost.klausuren.GostKlausurvorgabe;
import de.svws_nrw.core.data.gost.klausuren.GostKursklausur;
import de.svws_nrw.core.data.gost.klausuren.GostSchuelerklausur;
import de.svws_nrw.core.data.gost.klausuren.GostSchuelerklausurraumstunde;
import de.svws_nrw.core.data.stundenplan.StundenplanZeitraster;
import de.svws_nrw.core.types.Wochentag;
import de.svws_nrw.core.utils.klausurplan.GostKlausurraumManager;
import de.svws_nrw.core.utils.klausurplan.GostKlausurvorgabenManager;
import de.svws_nrw.core.utils.klausurplan.GostKursklausurManager;
import de.svws_nrw.core.utils.stundenplan.StundenplanManager;
import de.svws_nrw.data.DataManager;
import de.svws_nrw.data.JSONMapper;
import de.svws_nrw.data.stundenplan.DataStundenplan;
import de.svws_nrw.db.DBEntityManager;
import de.svws_nrw.db.dto.current.gost.klausurplanung.DTOGostKlausurenKursklausuren;
import de.svws_nrw.db.dto.current.gost.klausurplanung.DTOGostKlausurenRaeume;
import de.svws_nrw.db.dto.current.gost.klausurplanung.DTOGostKlausurenRaeumeStunden;
import de.svws_nrw.db.dto.current.gost.klausurplanung.DTOGostKlausurenSchuelerklausuren;
import de.svws_nrw.db.dto.current.gost.klausurplanung.DTOGostKlausurenSchuelerklausurenRaeumeStunden;
import de.svws_nrw.db.dto.current.gost.klausurplanung.DTOGostKlausurenTermine;
import de.svws_nrw.db.dto.current.gost.klausurplanung.DTOGostKlausurenVorgaben;
import de.svws_nrw.db.dto.current.svws.db.DTODBAutoInkremente;
import de.svws_nrw.db.utils.OperationError;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * Diese Klasse erweitert den abstrakten {@link DataManager} für den Core-DTO
 * {@link GostSchuelerklausurraumstunde}.
 */
public final class DataGostKlausurenSchuelerklausurraumstunde extends DataManager<Long> {

	/**
	 * Erstellt einen neuen {@link DataManager} für den Core-DTO
	 * {@link GostSchuelerklausurraumstunde}.
	 *
	 * @param conn die Datenbank-Verbindung für den Datenbankzugriff
	 */
	public DataGostKlausurenSchuelerklausurraumstunde(final DBEntityManager conn) {
		super(conn);
	}

	@Override
	public Response getAll() {
		return this.getList();
	}

	/**
	 * Weist die übergebenen Schülerklausuren dem entsprechenden Klausurraum zu.
	 *
	 * @param idRaum               die ID des Klausurraums
	 * @param idsSchuelerklausuren die IDs der zuzuweisenden Schülerklausuren
	 *
	 * @return die Antwort
	 */
	public Response setzeRaumZuSchuelerklausuren(final long idRaum, final List<Long> idsSchuelerklausuren) {
		// Raum und Termin holen
		final GostKlausurraum raum = DataGostKlausurenRaum.dtoMapper.apply(conn.queryByKey(DTOGostKlausurenRaeume.class, idRaum));
		final DTOGostKlausurenTermine termin = conn.queryByKey(DTOGostKlausurenTermine.class, raum.idTermin);

		final List<GostSchuelerklausur> listSchuelerklausuren = new ArrayList<>();

		// Neue Schülerklausuren ermitteln
		final List<GostSchuelerklausur> listSchuelerklausurenNeu = conn.queryNamed("DTOGostKlausurenSchuelerklausuren.id.multiple", idsSchuelerklausuren, DTOGostKlausurenSchuelerklausuren.class)
				.stream().map(DataGostKlausurenSchuelerklausur.dtoMapper::apply).toList();
		if (listSchuelerklausurenNeu.isEmpty())
			throw OperationError.NOTHING_TO_DO.exception(); // TODO Exceptionhandling

		// Schon im Raum existente Schülerklausuren ermitteln
		final List<DTOGostKlausurenRaeumeStunden> listKlausurraumstunden = conn.queryNamed("DTOGostKlausurenRaeumeStunden.klausurraum_id", idRaum, DTOGostKlausurenRaeumeStunden.class);
		if (!listKlausurraumstunden.isEmpty()) {
			final List<DTOGostKlausurenSchuelerklausurenRaeumeStunden> listSchuelerklausurenSchonImRaumRaumStunden = conn.queryNamed(
					"DTOGostKlausurenSchuelerklausurenRaeumeStunden.klausurraumstunde_id.multiple", listKlausurraumstunden.stream().map(krs -> krs.ID).toList(),
					DTOGostKlausurenSchuelerklausurenRaeumeStunden.class);
			if (!listSchuelerklausurenSchonImRaumRaumStunden.isEmpty()) {
				listSchuelerklausuren
						.addAll(conn
								.queryNamed("DTOGostKlausurenSchuelerklausuren.id.multiple",
										listSchuelerklausurenSchonImRaumRaumStunden.stream().map(skrs -> skrs.Schuelerklausur_ID).distinct().toList(), DTOGostKlausurenSchuelerklausuren.class)
								.stream().map(DataGostKlausurenSchuelerklausur.dtoMapper::apply).toList());
			}
		}

		final List<GostKursklausur> listKursklausuren = conn.queryNamed("DTOGostKlausurenKursklausuren.id.multiple",
				Stream.concat(listSchuelerklausuren.stream().map(sk -> sk.idKursklausur).distinct(), listSchuelerklausurenNeu.stream().map(sk -> sk.idKursklausur).distinct()).distinct().toList(),
				DTOGostKlausurenKursklausuren.class).stream().map(DataGostKlausurenKursklausur.dtoMapper2::apply).toList();

		listSchuelerklausuren.addAll(listSchuelerklausurenNeu);

		// TODO: Richtige Stundenplan-ID verwenden
		final StundenplanManager stundenplanManager = new StundenplanManager(DataStundenplan.getStundenplan(conn, 1L), new ArrayList<>(), new ArrayList<>(), null);
		final GostKursklausurManager kursklausurManager = new GostKursklausurManager(listKursklausuren);

		final List<GostKlausurvorgabe> listVorgaben = conn
				.queryNamed("DTOGostKlausurenVorgaben.id.multiple", listKursklausuren.stream().map(k -> k.idVorgabe).distinct().toList(), DTOGostKlausurenVorgaben.class).stream()
				.map(DataGostKlausurenVorgabe.dtoMapper::apply).toList();
		final GostKlausurvorgabenManager vorgabenManager = new GostKlausurvorgabenManager(listVorgaben, null);

		final List<GostKlausurraumstunde> listRaumstunden = conn.queryNamed("DTOGostKlausurenRaeumeStunden.klausurraum_id", idRaum, DTOGostKlausurenRaeumeStunden.class).stream()
				.map(DataGostKlausurenRaumstunde.dtoMapper::apply).toList();

		final GostKlausurraumManager raumManager = new GostKlausurraumManager(raum, listRaumstunden, listSchuelerklausuren);

		// Zeitraster_min und _max ermitteln
		int minStart = termin.Startzeit;
		int maxEnd = -1;
		for (final GostKursklausur kk : listKursklausuren) {
			if (kk.startzeit != null && kk.startzeit < minStart)
				minStart = kk.startzeit;
			final int startzeit = kk.startzeit != null ? kk.startzeit : termin.Startzeit;
			final int endzeit = startzeit + vorgabenManager.gibGostKlausurvorgabe(kk.idVorgabe).dauer;
			if (startzeit + endzeit > maxEnd)
				maxEnd = endzeit;
		}
		for (final GostSchuelerklausur sk : listSchuelerklausuren) {
			if (sk.startzeit != null) {
				if (sk.startzeit < minStart)
					minStart = sk.startzeit;
				final int endzeit = sk.startzeit + vorgabenManager.gibGostKlausurvorgabe(kursklausurManager.gibKursklausur(sk.idKursklausur).idVorgabe).dauer;
				if (sk.startzeit + endzeit > maxEnd)
					maxEnd = endzeit;
			}
		}

		final LocalDate klausurdatum = LocalDate.parse(termin.Datum);
		final List<StundenplanZeitraster> zeitrasterRaum = stundenplanManager.getZeitrasterByWochentagStartVerstrichen(Wochentag.fromIDorException(klausurdatum.getDayOfWeek().getValue()), minStart,
				maxEnd - minStart);

		final GostCollectionSkrsKrs result = new GostCollectionSkrsKrs();

		try {
			conn.transactionBegin();
			// Bestimme die ID der ersten neuen Klausurraumstunde
			final DTODBAutoInkremente lastKrsID = conn.queryByKey(DTODBAutoInkremente.class, "Gost_Klausuren_Raeume_Stunden");
			long idKrs = lastKrsID == null ? 1 : lastKrsID.MaxID + 1;

			// Lege die Raumstunden für den gesamten Raum an (Befüllen der Tabelle
			// Gost_Klausuren_Raeume_Stunden)
			for (final StundenplanZeitraster stunde : zeitrasterRaum) {
				if (raumManager.getKlausurraumstundeByRaumZeitraster(idRaum, stunde.id) == null) {
					DTOGostKlausurenRaeumeStunden dtoStundeNeu = new DTOGostKlausurenRaeumeStunden(idKrs++, idRaum, stunde.id);
					GostKlausurraumstunde stundeNeu = DataGostKlausurenRaumstunde.dtoMapper.apply(dtoStundeNeu);
					raumManager.addKlausurraumstunde(stundeNeu);
					result.raumstunden.add(stundeNeu);
					conn.transactionPersist(dtoStundeNeu);
				}
			}

			// Lege die Schuelerklausurraumstunden für jede Klausur an (Befüllen der Tabelle
			// Gost_Klausuren_Schuelerklausuren_Raeume_Stunden)
			for (final GostSchuelerklausur sk : listSchuelerklausurenNeu) {
				final GostKursklausur kk = kursklausurManager.getKursklausurById(sk.idKursklausur);
				final GostKlausurvorgabe v = vorgabenManager.gibGostKlausurvorgabe(kk.idVorgabe);
				final int startzeit = sk.startzeit != null ? sk.startzeit : (kk.startzeit != null ? kk.startzeit : termin.Startzeit);
				final List<StundenplanZeitraster> zeitrasterSk = stundenplanManager.getZeitrasterByWochentagStartVerstrichen(Wochentag.fromIDorException(klausurdatum.getDayOfWeek().getValue()),
						startzeit, v.dauer);
				conn.transactionExecuteDelete("DELETE FROM DTOGostKlausurenSchuelerklausurenRaeumeStunden v WHERE v.Schuelerklausur_ID = %d".formatted(sk.idSchuelerklausur));
				for (final StundenplanZeitraster stunde : zeitrasterSk) {
					DTOGostKlausurenSchuelerklausurenRaeumeStunden skRaumStundeNeu = new DTOGostKlausurenSchuelerklausurenRaeumeStunden(sk.idSchuelerklausur, raumManager.getKlausurraumstundeByRaumZeitraster(idRaum, stunde.id).id);
					conn.transactionPersist(skRaumStundeNeu);
					result.skRaumstunden.add(DataGostKlausurenSchuelerklausurraumstunde.dtoMapper.apply(skRaumStundeNeu));
				}
			}

			if (!conn.transactionCommit())
				return OperationError.CONFLICT.getResponse("Datenbankfehler beim Persistieren der Gost-Klausurraumstunden");
		} catch (final Exception e) {
			if (e instanceof final WebApplicationException webApplicationException)
				return webApplicationException.getResponse();
			return OperationError.INTERNAL_SERVER_ERROR.getResponse();
		} finally {
			conn.transactionRollback();
		}

		return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(result).build();
	}

	/**
	 * Lambda-Ausdruck zum Umwandeln eines Datenbank-DTOs
	 * {@link DTOGostKlausurenRaeumeStunden} in einen Core-DTO
	 * {@link GostKlausurraumstunde}.
	 */
	public static final Function<DTOGostKlausurenSchuelerklausurenRaeumeStunden, GostSchuelerklausurraumstunde> dtoMapper = (final DTOGostKlausurenSchuelerklausurenRaeumeStunden z) -> {
		final GostSchuelerklausurraumstunde daten = new GostSchuelerklausurraumstunde();
		daten.idRaumstunde = z.KlausurRaumStunde_ID;
		daten.idSchuelerklausur = z.Schuelerklausur_ID;
		return daten;
	};

	/**
	 * Gibt die Liste der Klausurvorgaben einer Jahrgangsstufe im übergebenen
	 * Gost-Halbjahr zurück.
	 *
	 * @param idTermin die ID des Klausurtermins
	 *
	 * @return die Liste der Klausurraumstunden
	 */
	private List<GostKlausurraumstunde> getKlausurraumstunden(final Long idTermin) {

		final List<GostKlausurraum> listRaeume = new DataGostKlausurenRaum(conn).getKlausurraeume(idTermin);

		if (listRaeume.isEmpty()) {
			// TODO Errorhandling nötig?
			return new ArrayList<>();
		}

		final List<DTOGostKlausurenRaeumeStunden> stunden = conn.queryNamed("DTOGostKlausurenRaeumeStunden.klausurraum_id.multiple", listRaeume.stream().map(s -> s.idTermin).toList(),
				DTOGostKlausurenRaeumeStunden.class);

		final List<GostKlausurraumstunde> daten = new ArrayList<>();
		for (final DTOGostKlausurenRaeumeStunden s : stunden)
			daten.add(DataGostKlausurenRaumstunde.dtoMapper.apply(s));
		return daten;
	}

	@Override
	public Response get(final Long idTermin) {
		// Klausurraumstunden zu einem Klausurtermin
		return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(this.getKlausurraumstunden(idTermin)).build();
	}

	@Override
	public Response getList() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Erstellt eine neue Gost-Klausurraumstunde
	 *
	 * @param is Das JSON-Objekt mit den Daten
	 *
	 * @return Eine Response mit der neuen Gost-Klausurraumstunde
	 */
	public Response create(final InputStream is) {
		DTOGostKlausurenRaeumeStunden raumstunde = null;
		try {
			conn.transactionBegin();
			// Bestimme die ID der neuen Klausurraumstunde
			final DTODBAutoInkremente lastID = conn.queryByKey(DTODBAutoInkremente.class, "Gost_Klausuren_Raeume_Stunden");
			final Long id = lastID == null ? 1 : lastID.MaxID + 1;

			long klausurraum_ID = -1;
			long zeitraster_ID = -1;

			final Map<String, Object> map = JSONMapper.toMap(is);
			if (map.size() > 0) {
				for (final Entry<String, Object> entry : map.entrySet()) {
					final String key = entry.getKey();
					final Object value = entry.getValue();
					switch (key) {
					case "idRaum" -> klausurraum_ID = JSONMapper.convertToLong(value, false);
					case "idZeitraster" -> zeitraster_ID = JSONMapper.convertToLong(value, false);
					case "id" -> {
						/* do nothing */ }
					default -> throw OperationError.BAD_REQUEST.exception();
					}
				}
			}

			raumstunde = new DTOGostKlausurenRaeumeStunden(id, klausurraum_ID, zeitraster_ID);

			conn.transactionPersist(raumstunde);
			if (!conn.transactionCommit())
				return OperationError.CONFLICT.getResponse("Datenbankfehler beim Persistieren der Gost-Klausurraumsstunde");
		} catch (final Exception e) {
			if (e instanceof final WebApplicationException webApplicationException)
				return webApplicationException.getResponse();
			return OperationError.INTERNAL_SERVER_ERROR.getResponse();
		} finally {
			conn.transactionRollback();
		}

		final GostKlausurraumstunde daten = DataGostKlausurenRaumstunde.dtoMapper.apply(raumstunde);
		return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(daten).build();
	}

	/**
	 * Löscht eine Gost-Klausurraumstunde *
	 *
	 * @param id die ID des zu löschenden Klausurraums
	 *
	 * @return die Response
	 */
	public Response delete(final Long id) {
		// TODO use transaction
		// Bestimme die Raumstunde
		final DTOGostKlausurenRaeumeStunden stunde = conn.queryByKey(DTOGostKlausurenRaeumeStunden.class, id);
		if (stunde == null)
			return OperationError.NOT_FOUND.getResponse();
		// Entferne die Raumstunde
		conn.remove(stunde);
		return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(id).build();
	}

	@Override
	public Response patch(final Long id, final InputStream is) {
		throw new UnsupportedOperationException();
	}

}
