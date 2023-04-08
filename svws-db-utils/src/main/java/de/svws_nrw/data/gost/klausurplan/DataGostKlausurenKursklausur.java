package de.svws_nrw.data.gost.klausurplan;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.svws_nrw.core.data.gost.klausuren.GostKlausurvorgabe;
import de.svws_nrw.core.data.gost.klausuren.GostKursklausur;
import de.svws_nrw.core.types.gost.GostHalbjahr;
import de.svws_nrw.data.DataManager;
import de.svws_nrw.data.JSONMapper;
import de.svws_nrw.db.DBEntityManager;
import de.svws_nrw.db.dto.current.gost.klausurplanung.DTOGostKlausurenKursklausuren;
import de.svws_nrw.db.dto.current.gost.klausurplanung.DTOGostKlausurenSchuelerklausuren;
import de.svws_nrw.db.dto.current.gost.klausurplanung.DTOGostKlausurenTermine;
import de.svws_nrw.db.dto.current.gost.klausurplanung.DTOGostKlausurenVorgaben;
import de.svws_nrw.db.dto.current.schild.kurse.DTOKurs;
import de.svws_nrw.db.utils.OperationError;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * Diese Klasse erweitert den abstrakten {@link DataManager} für den Core-DTO
 * {@link GostKursklausur}.
 */
public final class DataGostKlausurenKursklausur extends DataManager<Long> {

	private final int _abiturjahr;

	/**
	 * Erstellt einen neuen {@link DataManager} für den Core-DTO
	 * {@link GostKursklausur}.
	 *
	 * @param conn       die Datenbank-Verbindung für den Datenbankzugriff
	 * @param abiturjahr das Jahr, in welchem der Jahrgang Abitur machen wird
	 */
	public DataGostKlausurenKursklausur(final DBEntityManager conn, final int abiturjahr) {
		super(conn);
		_abiturjahr = abiturjahr;
	}

	@Override
	public Response getAll() {
		return this.getList();
	}

	/**
	 * Gibt die Liste der Kursklausuren einer Jahrgangsstufe im übergebenen
	 * Gost-Halbjahr zurück.
	 *
	 * @param halbjahr das Gost-Halbjahr
	 *
	 * @return die Liste der Kursklausuren
	 */
	private List<GostKursklausur> getKursKlausuren(final int halbjahr) {
		final List<GostKursklausur> daten = new ArrayList<>();

		final Map<Long, DTOGostKlausurenVorgaben> mapVorgaben = conn.query("SELECT v FROM DTOGostKlausurenVorgaben v WHERE v.Abi_Jahrgang = :jgid AND v.Halbjahr = :hj", DTOGostKlausurenVorgaben.class)
				.setParameter("jgid", _abiturjahr).setParameter("hj", GostHalbjahr.fromID(halbjahr)).getResultList().stream().collect(Collectors.toMap(v -> v.ID, v -> v));

		if (mapVorgaben.isEmpty()) {
			// TODO Errorhandling nötig?
			return daten;
		}

		final List<DTOGostKlausurenKursklausuren> kursklausuren = conn.queryNamed("DTOGostKlausurenKursklausuren.vorgabe_id.multiple", mapVorgaben.keySet(), DTOGostKlausurenKursklausuren.class);

		if (kursklausuren.isEmpty()) {
			// TODO Errorhandling nötig?
			return daten;
		}

		final Map<Long, List<DTOGostKlausurenSchuelerklausuren>> mapSchuelerklausuren = conn
				.queryNamed("DTOGostKlausurenSchuelerklausuren.kursklausur_id.multiple", kursklausuren.stream().map(k -> k.ID).collect(Collectors.toList()), DTOGostKlausurenSchuelerklausuren.class)
				.stream().collect(Collectors.groupingBy(s -> s.Kursklausur_ID));

		if (mapSchuelerklausuren.isEmpty()) {
			// TODO Errorhandling nötig?
			return daten;
		}

		final List<Long> kursIDs = kursklausuren.stream().map(k -> k.Kurs_ID).distinct().toList();
		final Map<Long, DTOKurs> mapKurse = conn.queryNamed("DTOKurs.id.multiple", kursIDs, DTOKurs.class).stream().collect(Collectors.toMap(k -> k.ID, k -> k));

		kursklausuren.stream().forEach(k -> {
//			GostKursklausur kk = new GostKursklausur();
			final DTOGostKlausurenVorgaben v = mapVorgaben.get(k.Vorgabe_ID);
			final DTOKurs kurs = mapKurse.get(k.Kurs_ID);

			final List<DTOGostKlausurenSchuelerklausuren> sKlausuren = mapSchuelerklausuren.get(k.ID);
			if (sKlausuren != null && !sKlausuren.isEmpty()) {
				final GostKursklausur kk = dtoMapper.apply(k, DataGostKlausurenVorgabe.dtoMapper.apply(v), kurs, sKlausuren);
				daten.add(kk);
			}
		});
		return daten;
	}

	/**
	 * TODO
	 *
	 * @param <One> TODO
	 * @param <Two> TODO
	 * @param <Three> TODO
	 * @param <Four> TODO
	 * @param <Five> TODO
	 */
	@FunctionalInterface interface Function5<One, Two, Three, Four, Five> {
		/**
		 * TODO
		 *
		 * @param one     TODO
		 * @param two     TODO
		 * @param three   TODO
		 * @param four    TODO
		 *
		 * @return TODO
		 */
		Five apply(One one, Two two, Three three, Four four);
	}

	/**
	 * Lambda-Ausdruck zum Umwandeln eines Datenbank-DTOs
	 * {@link DTOGostKlausurenVorgaben} in einen Core-DTO
	 * {@link GostKlausurvorgabe}.
	 */
	public static Function5<DTOGostKlausurenKursklausuren, GostKlausurvorgabe, DTOKurs, List<DTOGostKlausurenSchuelerklausuren>, GostKursklausur> dtoMapper = (final DTOGostKlausurenKursklausuren k,
			final GostKlausurvorgabe v, final DTOKurs kurs, final List<DTOGostKlausurenSchuelerklausuren> sKlausuren) -> {
		final GostKursklausur kk = new GostKursklausur();
		kk.id = k.ID;
		kk.idVorgabe = k.Vorgabe_ID;
		kk.abijahr = v.abiJahrgang;
		kk.auswahlzeit = v.auswahlzeit;
		kk.bemerkungVorgabe = v.bemerkungVorgabe;
		kk.dauer = v.dauer;
		kk.kursart = v.kursart;
		kk.idFach = v.idFach;
		kk.quartal = v.quartal;
		kk.halbjahr = v.halbjahr;
		kk.idKurs = k.Kurs_ID;
		kk.kursKurzbezeichnung = kurs.KurzBez;
		kk.idLehrer = kurs.Lehrer_ID;
		try {
			kk.kursSchiene = Stream.of(kurs.Schienen.split(",")).mapToInt(Integer::parseInt).toArray();
//			String ss[] = kurs.Schienen.split(",");
//			for (int i = 0; i < ss.length; i++)
//				kk.kursSchiene[i] = Integer.parseInt(ss[i]);
		} catch (@SuppressWarnings("unused") final NumberFormatException nfe) {
			// TODO ExceptionHandling?
		}
		kk.idTermin = k.Termin_ID;
		kk.istAudioNotwendig = v.istAudioNotwendig;
		kk.istMdlPruefung = v.istMdlPruefung;
		kk.istVideoNotwendig = v.istVideoNotwendig;
		kk.startzeit = k.Startzeit;
		if (sKlausuren != null)
			kk.schuelerIds = sKlausuren.stream().map(s -> s.Schueler_ID).collect(Collectors.toList());
		return kk;
	};

	@Override public Response get(final Long halbjahr) {
		// Kursklausuren für einen Abiturjahrgang in einem Gost-Halbjahr
		return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(this.getKursKlausuren(halbjahr.intValue())).build();
	}

	@Override public Response patch(final Long id, final InputStream is) {
		final Map<String, Object> map = JSONMapper.toMap(is);
		if (map.size() > 0) {
			try {
				conn.transactionBegin();
				final DTOGostKlausurenKursklausuren kursklausur = conn.queryByKey(DTOGostKlausurenKursklausuren.class, id);
				if (kursklausur == null)
					throw OperationError.NOT_FOUND.exception();
				for (final Entry<String, Object> entry : map.entrySet()) {
					final String key = entry.getKey();
					final Object value = entry.getValue();
					switch (key) {
					case "id" -> {
						final Long patch_id = JSONMapper.convertToLong(value, true);
						if ((patch_id == null) || (patch_id.longValue() != id.longValue()))
							throw OperationError.BAD_REQUEST.exception();
					}
					case "idVorgabe" -> {
						final long patch_vorgabeid = JSONMapper.convertToLong(value, false);
						if ((patch_vorgabeid != kursklausur.Vorgabe_ID))
							throw OperationError.BAD_REQUEST.exception();
					}
					case "idKurs" -> {
						final long patch_kursid = JSONMapper.convertToLong(value, false);
						if ((patch_kursid != kursklausur.Kurs_ID))
							throw OperationError.BAD_REQUEST.exception();
					}
					case "idTermin" -> {
						final Long newTermin = JSONMapper.convertToLong(value, true);
						if (newTermin != null) {
							final DTOGostKlausurenTermine termin = conn.queryByKey(DTOGostKlausurenTermine.class, newTermin);
							final DTOGostKlausurenVorgaben vorgabe = conn.queryByKey(DTOGostKlausurenVorgaben.class, kursklausur.Vorgabe_ID);
							if (termin.Quartal != vorgabe.Quartal)
								throw OperationError.CONFLICT.exception("Klausur-Quartal entspricht nicht Termin-Quartal.");
						}
						kursklausur.Termin_ID = newTermin;
					}
					case "startzeit" -> kursklausur.Startzeit = JSONMapper.convertToString(value, true, false);

					// TODO Was ist mit anderen Attributen, falls sie im InputStream vorkommen?

					default -> throw OperationError.BAD_REQUEST.exception();
					}
				}
				conn.transactionPersist(kursklausur);
				if (!conn.transactionCommit()) {
					// TODO so richtig? Fremdschlüsselbedingung schlägt fehl
					throw OperationError.CONFLICT.exception();
				}
			} catch (final Exception e) {
				if (e instanceof final WebApplicationException webAppException)
					return webAppException.getResponse();
				return OperationError.INTERNAL_SERVER_ERROR.getResponse();
			} finally {
				// Perform a rollback if necessary
				conn.transactionRollback();
			}
		}
		return Response.status(Status.OK).build();
	}

	@Override public Response getList() {
		throw new UnsupportedOperationException();
	}

}
