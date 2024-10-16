package de.svws_nrw.data.stundenplan;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import de.svws_nrw.core.data.stundenplan.StundenplanFach;
import de.svws_nrw.data.DataManager;
import de.svws_nrw.db.DBEntityManager;
import de.svws_nrw.db.dto.current.schild.faecher.DTOFach;
import de.svws_nrw.db.dto.current.schild.schule.DTOEigeneSchule;
import de.svws_nrw.db.dto.current.schild.stundenplan.DTOStundenplan;
import de.svws_nrw.db.dto.current.schild.stundenplan.DTOStundenplanUnterricht;
import de.svws_nrw.db.dto.current.schild.stundenplan.DTOStundenplanZeitraster;
import de.svws_nrw.db.utils.ApiOperationException;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * Diese Klasse erweitert den abstrakten {@link DataManager} für den
 * Core-DTO {@link StundenplanFach}.
 */
public final class DataStundenplanFaecher extends DataManager<Long> {

	private final Long stundenplanID;

	/**
	 * Erstellt einen neuen {@link DataManager} für den Core-DTO {@link StundenplanFach}.
	 *
	 * @param conn            die Datenbank-Verbindung für den Datenbankzugriff
	 * @param stundenplanID   die ID des Stundenplans, dessen Fächer abgefragt werden
	 */
	public DataStundenplanFaecher(final DBEntityManager conn, final Long stundenplanID) {
		super(conn);
		this.stundenplanID = stundenplanID;
	}


	/**
	 * Lambda-Ausdruck zum Umwandeln eines Datenbank-DTOs {@link DTOFach} in einen Core-DTO {@link StundenplanFach}.
	 */
	private static final Function<DTOFach, StundenplanFach> dtoMapper = (final DTOFach f) -> {
		final StundenplanFach daten = new StundenplanFach();
		daten.id = f.ID;
		daten.kuerzel = f.Kuerzel;
		daten.kuerzelStatistik = f.StatistikFach.daten.kuerzelASD;
		daten.bezeichnung = f.Bezeichnung;
		if (f.SortierungAllg != null)
			daten.sortierung = f.SortierungAllg;
		else
			daten.sortierung = ((f.SortierungSekII == null) ? 32000 : f.SortierungSekII);
		daten.farbe = f.StatistikFach.getFarbe();
		return daten;
	};



	@Override
	public Response getAll() throws ApiOperationException {
		return this.getList();
	}

	/**
	 * Gibt die Fächer, die im Stundenplan vorkommen, zurück.
	 *
	 * @param conn            die Datenbankverbindung
	 * @param idStundenplan   die ID des Stundenplans
	 *
	 * @return die Liste der Fächer
	 *
	 * @throws ApiOperationException im Fehlerfall
	 */
	public static List<StundenplanFach> getFaecher(final @NotNull DBEntityManager conn, final long idStundenplan) throws ApiOperationException {
		final DTOEigeneSchule schule = conn.querySingle(DTOEigeneSchule.class);
		if (schule == null)
			throw new ApiOperationException(Status.NOT_FOUND);
		final DTOStundenplan stundenplan = conn.queryByKey(DTOStundenplan.class, idStundenplan);
		if (stundenplan == null)
			throw new ApiOperationException(Status.NOT_FOUND, "Es wurde kein Stundenplan mit der ID %d gefunden.".formatted(idStundenplan));
		// Bestimme zunächst alle Zeitraster-IDs des Stundenplans
		final List<Long> zeitrasterIDs = conn.queryNamed("DTOStundenplanZeitraster.stundenplan_id", idStundenplan, DTOStundenplanZeitraster.class)
				.stream().map(z -> z.ID).toList();
		// Bestimme alle zunächst alle Fächer-IDs der Unterrichte ...
		List<Long> faecherIDs = new ArrayList<>();
		if (!zeitrasterIDs.isEmpty())
			faecherIDs.addAll(conn.queryNamed("DTOStundenplanUnterricht.zeitraster_id.multiple", zeitrasterIDs, DTOStundenplanUnterricht.class)
					.stream().map(u -> u.Fach_ID).filter(f -> f != null).toList());
		// ... und dann der ggf. noch nicht zugeordneten Kurs- und Klassenunterrichte
		faecherIDs = Stream.concat(faecherIDs.stream(), DataStundenplanKlassenunterricht.getKlassenunterrichte(conn, idStundenplan).stream().map(ku -> ku.idFach).distinct()).distinct().toList();
		faecherIDs = Stream.concat(faecherIDs.stream(), DataStundenplanKurse.getKurse(conn, idStundenplan).stream().map(ku -> ku.idFach).distinct()).distinct().toList();
		if (faecherIDs.isEmpty())
			return new ArrayList<>();
		// Bestimme nun die Fächer-Daten...
		final List<DTOFach> faecherListe = conn.queryNamed("DTOFach.id.multiple", faecherIDs, DTOFach.class);
		final ArrayList<StundenplanFach> daten = new ArrayList<>();
		for (final DTOFach f : faecherListe) {
			final StundenplanFach fach = dtoMapper.apply(f);
			daten.add(fach);
		}
		return daten;
	}


	@Override
	public Response getList() throws ApiOperationException {
		final List<StundenplanFach> daten = getFaecher(conn, this.stundenplanID);
        return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(daten).build();
	}


	@Override
	public Response get(final Long id) throws ApiOperationException {
		final DTOEigeneSchule schule = conn.querySingle(DTOEigeneSchule.class);
		if (schule == null)
			throw new ApiOperationException(Status.NOT_FOUND);
		final DTOStundenplan stundenplan = conn.queryByKey(DTOStundenplan.class, stundenplanID);
		if (stundenplan == null)
			throw new ApiOperationException(Status.NOT_FOUND, "Es wurde kein Stundenplan mit der ID %d gefunden.".formatted(stundenplanID));
		if (id == null)
			throw new ApiOperationException(Status.BAD_REQUEST, "Eine Anfrage zu einem Fach mit der ID null ist unzulässig.");
		final DTOFach fach = conn.queryByKey(DTOFach.class, id);
		if (fach == null)
			throw new ApiOperationException(Status.NOT_FOUND, "Es wurde kein Fach mit der ID %d gefunden.".formatted(id));
		final StundenplanFach daten = dtoMapper.apply(fach);
        return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(daten).build();
	}


	@Override
	public Response patch(final Long id, final InputStream is) {
		throw new UnsupportedOperationException();
	}

}
