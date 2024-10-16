package de.svws_nrw.data.kataloge;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.ObjLongConsumer;

import de.svws_nrw.core.data.stundenplan.StundenplanPausenzeit;
import de.svws_nrw.data.DTOMapper;
import de.svws_nrw.data.DataBasicMapper;
import de.svws_nrw.data.DataManager;
import de.svws_nrw.data.JSONMapper;
import de.svws_nrw.db.DBEntityManager;
import de.svws_nrw.db.dto.current.schild.stundenplan.DTOKatalogPausenzeit;
import de.svws_nrw.db.utils.ApiOperationException;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * Diese Klasse erweitert den abstrakten {@link DataManager} für den
 * Core-DTO {@link StundenplanPausenzeit}.
 */
public final class DataKatalogPausenzeiten extends DataManager<Long> {

	/**
	 * Erstellt einen neuen {@link DataManager} für den Core-DTO {@link StundenplanPausenzeit}.
	 *
	 * @param conn            die Datenbank-Verbindung für den Datenbankzugriff
	 */
	public DataKatalogPausenzeiten(final DBEntityManager conn) {
		super(conn);
	}


	/**
	 * Lambda-Ausdruck zum Umwandeln eines Datenbank-DTOs {@link DTOKatalogPausenzeit} in einen Core-DTO {@link StundenplanPausenzeit}.
	 */
	private static final DTOMapper<DTOKatalogPausenzeit, StundenplanPausenzeit> dtoMapper = (final DTOKatalogPausenzeit p) -> {
		final StundenplanPausenzeit daten = new StundenplanPausenzeit();
		daten.id = p.ID;
		daten.wochentag = p.Tag;
		daten.beginn = p.Beginn;
		daten.ende = p.Ende;
		daten.bezeichnung = p.Bezeichnung;
		return daten;
	};


	@Override
	public Response getAll() throws ApiOperationException {
		return this.getList();
	}

	/**
	 * Gibt die Pausenzeiten des Katalogs zurück.
	 *
	 * @param conn   die Datenbankverbindung
	 *
	 * @return die Liste der Pausenzeiten
	 *
	 * @throws ApiOperationException   im Fehlerfall
	 */
	public static List<StundenplanPausenzeit> getPausenzeiten(final @NotNull DBEntityManager conn) throws ApiOperationException {
		final List<DTOKatalogPausenzeit> pausenzeiten = conn.queryAll(DTOKatalogPausenzeit.class);
		final ArrayList<StundenplanPausenzeit> daten = new ArrayList<>();
		for (final DTOKatalogPausenzeit p : pausenzeiten)
			daten.add(dtoMapper.apply(p));
		return daten;
	}

	@Override
	public Response getList() throws ApiOperationException {
		final List<StundenplanPausenzeit> daten = getPausenzeiten(conn);
        return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(daten).build();
	}

	@Override
	public Response get(final Long id) throws ApiOperationException {
		if (id == null)
			throw new ApiOperationException(Status.BAD_REQUEST, "Eine Anfrage zu einer Pausenzeit mit der ID null ist unzulässig.");
		final DTOKatalogPausenzeit pausenzeit = conn.queryByKey(DTOKatalogPausenzeit.class, id);
		if (pausenzeit == null)
			throw new ApiOperationException(Status.NOT_FOUND, "Es wurde keine Pausenzeit mit der ID %d gefunden.".formatted(id));
		final StundenplanPausenzeit daten = dtoMapper.apply(pausenzeit);
        return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(daten).build();
	}


	private static final Map<String, DataBasicMapper<DTOKatalogPausenzeit>> patchMappings = Map.ofEntries(
		Map.entry("id", (conn, dto, value, map) -> {
			final Long patch_id = JSONMapper.convertToLong(value, true);
			if ((patch_id == null) || (patch_id.longValue() != dto.ID))
				throw new ApiOperationException(Status.BAD_REQUEST);
		}),
		Map.entry("wochentag", (conn, dto, value, map) -> dto.Tag = JSONMapper.convertToIntegerInRange(value, false, 1, 8)),
		Map.entry("beginn", (conn, dto, value, map) -> dto.Beginn = JSONMapper.convertToIntegerInRange(value, true, 0, 1440)),
		Map.entry("ende", (conn, dto, value, map) -> dto.Ende = JSONMapper.convertToIntegerInRange(value, true, 0, 1440)),
		Map.entry("bezeichnung", (conn, dto, value, map) -> dto.Bezeichnung = JSONMapper.convertToString(value, false, false, 40))
	);

	@Override
	public Response patch(final Long id, final InputStream is) throws ApiOperationException {
		return super.patchBasic(id, is, DTOKatalogPausenzeit.class, patchMappings);
	}


	private static final Set<String> requiredCreateAttributes = Set.of("wochentag", "beginn", "ende");

	private static final ObjLongConsumer<DTOKatalogPausenzeit> initDTO = (dto, id) -> dto.ID = id;

	/**
	 * Fügt eine Pausenzeit mit den übergebenen JSON-Daten der Datenbank hinzu und gibt das zugehörige CoreDTO
	 * zurück. Falls ein Fehler auftritt wird ein entsprechender Response-Code zurückgegeben.
	 *
	 * @param is   der InputStream mit den JSON-Daten
	 *
	 * @return die Response mit den Daten
	 *
	 * @throws ApiOperationException   im Fehlerfall
	 */
	public Response add(final InputStream is) throws ApiOperationException {
		return super.addBasic(is, DTOKatalogPausenzeit.class, initDTO, dtoMapper, requiredCreateAttributes, patchMappings);
	}


	/**
	 * Fügt mehrere Pausenzeiten mit den übergebenen JSON-Daten der Datenbank hinzu und gibt die zugehörigen CoreDTOs
	 * zurück. Falls ein Fehler auftritt wird ein entsprechender Response-Code zurückgegeben.
	 *
	 * @param is   der InputStream mit den JSON-Daten
	 *
	 * @return die Response mit den Daten
	 *
	 * @throws ApiOperationException   im Fehlerfall
	 */
	public Response addMultiple(final InputStream is) throws ApiOperationException {
		return super.addBasicMultiple(is, DTOKatalogPausenzeit.class, initDTO, dtoMapper, requiredCreateAttributes, patchMappings);
	}


	/**
	 * Löscht eine Pausenzeit
	 *
	 * @param id   die ID der Pausenzeit
	 *
	 * @return die HTTP-Response, welchen den Erfolg der Lösch-Operation angibt.
	 *
	 * @throws ApiOperationException   im Fehlerfall
	 */
	public Response delete(final Long id) throws ApiOperationException {
		return super.deleteBasic(id, DTOKatalogPausenzeit.class, dtoMapper);
	}


	/**
	 * Löscht mehrere Pausenzeiten
	 *
	 * @param ids   die IDs der Pausenzeiten
	 *
	 * @return die HTTP-Response, welchen den Erfolg der Lösch-Operation angibt.
	 *
	 * @throws ApiOperationException   im Fehlerfall
	 */
	public Response deleteMultiple(final List<Long> ids) throws ApiOperationException {
		return super.deleteBasicMultiple(ids, DTOKatalogPausenzeit.class, dtoMapper);
	}

}
