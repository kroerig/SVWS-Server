package de.svws_nrw.data.gost.klausurplan;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import de.svws_nrw.core.data.gost.klausurplanung.GostSchuelerklausur;
import de.svws_nrw.data.DataBasicMapper;
import de.svws_nrw.data.DataManager;
import de.svws_nrw.data.JSONMapper;
import de.svws_nrw.db.DBEntityManager;
import de.svws_nrw.db.dto.current.gost.klausurplanung.DTOGostKlausurenKursklausuren;
import de.svws_nrw.db.dto.current.gost.klausurplanung.DTOGostKlausurenSchuelerklausuren;
import de.svws_nrw.db.dto.current.gost.klausurplanung.DTOGostKlausurenTermine;
import de.svws_nrw.db.utils.OperationError;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * Diese Klasse erweitert den abstrakten {@link DataManager} für den Core-DTO
 * {@link GostSchuelerklausur}.
 */
public final class DataGostKlausurenSchuelerklausur extends DataManager<Long> {

	/**
	 * Erstellt einen neuen {@link DataManager} für den Core-DTO
	 * {@link GostSchuelerklausur}.
	 *
	 * @param conn die Datenbank-Verbindung für den Datenbankzugriff
	 */
	public DataGostKlausurenSchuelerklausur(final DBEntityManager conn) {
		super(conn);
	}

	@Override
	public Response getAll() {
		return this.getList();
	}

	private List<GostSchuelerklausur> getSchuelerKlausuren(final long terminId) {
		if (conn.queryByKey(DTOGostKlausurenTermine.class, terminId) == null)
			throw OperationError.BAD_REQUEST.exception("Klausurtermin nicht gefunden, ID: " + terminId);
		final List<Long> kursKlausurIds = conn.queryNamed("DTOGostKlausurenKursklausuren.termin_id", terminId, DTOGostKlausurenKursklausuren.class).stream().map(k -> k.ID).distinct().toList();
		final List<DTOGostKlausurenSchuelerklausuren> listSchuelerklausuren = conn.queryNamed("DTOGostKlausurenSchuelerklausuren.kursklausur_id.multiple", kursKlausurIds,
				DTOGostKlausurenSchuelerklausuren.class);
		// Schuelerklausuren entfernen, die an anderem Termin stattfinden sollen.
		listSchuelerklausuren.removeAll(listSchuelerklausuren.stream().filter(sk -> sk.Termin_ID != null && sk.Termin_ID != terminId).toList());
		// Schuelerklausuren ohne zugehörige Kursklausur hinzufügen (z.B. Nachschreiber)
		listSchuelerklausuren.addAll(conn.queryNamed("DTOGostKlausurenSchuelerklausuren.termin_id", terminId, DTOGostKlausurenSchuelerklausuren.class));
		return listSchuelerklausuren.stream().map(dtoMapper::apply).toList();
	}

	/**
	 * Lambda-Ausdruck zum Umwandeln eines Datenbank-DTOs
	 * {@link DTOGostKlausurenSchuelerklausuren} in einen Core-DTO
	 * {@link GostSchuelerklausur}.
	 */
	public static final Function<DTOGostKlausurenSchuelerklausuren, GostSchuelerklausur> dtoMapper = (final DTOGostKlausurenSchuelerklausuren z) -> {
		final GostSchuelerklausur daten = new GostSchuelerklausur();
		daten.idKursklausur = z.Kursklausur_ID;
		daten.idSchueler = z.Schueler_ID;
		daten.idSchuelerklausur = z.ID;
		daten.idTermin = z.Termin_ID;
		daten.startzeit = z.Startzeit;
		return daten;
	};

	private final Map<String, DataBasicMapper<DTOGostKlausurenSchuelerklausuren>> patchMappings =
			Map.ofEntries(
				Map.entry("idSchuelerklausur", (dto, value, map) -> {
					final Long patch_id = JSONMapper.convertToLong(value, false);
					if ((patch_id == null) || (patch_id.longValue() != dto.ID))
						throw OperationError.BAD_REQUEST.exception();
				}),
				Map.entry("idKursklausur", (dto, value, map) -> {
					final Long patch_id = JSONMapper.convertToLong(value, false);
					if ((patch_id == null) || (patch_id.longValue() != dto.Kursklausur_ID))
						throw OperationError.BAD_REQUEST.exception();
				}),
				Map.entry("idSchueler", (dto, value, map) -> {
					final Long patch_id = JSONMapper.convertToLong(value, false);
					if ((patch_id == null) || (patch_id.longValue() != dto.Schueler_ID))
						throw OperationError.BAD_REQUEST.exception();
				}),
				Map.entry("idTermin", (dto, value, map) -> {
					dto.Termin_ID = JSONMapper.convertToLong(value, true);
					if (conn.queryByKey(DTOGostKlausurenTermine.class, dto.Termin_ID) == null)
						throw OperationError.BAD_REQUEST.exception("Klausurtermin nicht gefunden, ID: " + dto.Termin_ID);
				}),
				Map.entry("startzeit", (dto, value, map) -> dto.Startzeit = JSONMapper.convertToIntegerInRange(value, true, 0, 1440))
			);

	@Override
	public Response patch(final Long id, final InputStream is) {
		return super.patchBasic(id, is, DTOGostKlausurenSchuelerklausuren.class, patchMappings);
//
//		final Map<String, Object> map = JSONMapper.toMap(is);
//		if (map.size() > 0) {
//			final DTOGostKlausurenSchuelerklausuren schuelerklausur = conn.queryByKey(DTOGostKlausurenSchuelerklausuren.class, id);
//			if (schuelerklausur == null)
//				throw OperationError.NOT_FOUND.exception();
//			for (final Entry<String, Object> entry : map.entrySet()) {
//				final String key = entry.getKey();
//				final Object value = entry.getValue();
//				switch (key) {
//				case "idSchuelerklausur" -> {
//					final Long patch_id = JSONMapper.convertToLong(value, false);
//					if ((patch_id == null) || (patch_id.longValue() != id.longValue()))
//						throw OperationError.BAD_REQUEST.exception();
//				}
//				case "idKursklausur" -> {
//					final Long patch_id = JSONMapper.convertToLong(value, false);
//					if ((patch_id == null) || (patch_id.longValue() != id.longValue()))
//						throw OperationError.BAD_REQUEST.exception();
//				}
//				case "idSchueler" -> {
//					final Long patch_id = JSONMapper.convertToLong(value, false);
//					if ((patch_id == null) || (patch_id.longValue() != id.longValue()))
//						throw OperationError.BAD_REQUEST.exception();
//				}
//				case "idTermin" -> {
//					final Long newTermin = JSONMapper.convertToLong(value, true);
//					schuelerklausur.Termin_ID = newTermin;
//				}
//				case "startzeit" -> schuelerklausur.Startzeit = JSONMapper.convertToIntegerInRange(value, true, 0, 1440);
//				default -> throw OperationError.BAD_REQUEST.exception();
//				}
//			}
//			if (!conn.transactionPersist(schuelerklausur)) {
//				throw OperationError.CONFLICT.exception();
//			}
//		}
//		return Response.status(Status.OK).build();
	}

	@Override
	public Response getList() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Response get(final Long id) {
		return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(this.getSchuelerKlausuren(id)).build();
	}

}
