package de.nrw.schule.svws.data.lehrer;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Vector;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.nrw.schule.svws.core.data.lehrer.LehrerKatalogRechtsverhaeltnisEintrag;
import de.nrw.schule.svws.core.types.statkue.LehrerRechtsverhaeltnis;
import de.nrw.schule.svws.data.DataManager;
import de.nrw.schule.svws.db.DBEntityManager;

/**
 * Diese Klasse erweitert den abstrakten {@link DataManager} für den
 * Core-DTO {@link LehrerKatalogRechtsverhaeltnisEintrag}.
 */
public class DataKatalogLehrerRechtsverhaeltnis extends DataManager<Long> {

	/**
	 * Erstellt einen neuen {@link DataManager} für den Core-DTO {@link LehrerKatalogRechtsverhaeltnisEintrag}.
	 * 
	 * @param conn   die Datenbank-Verbindung für den Datenbankzugriff
	 */
	public DataKatalogLehrerRechtsverhaeltnis(DBEntityManager conn) {
		super(conn);
	}
	
	@Override
	public Response getAll() {
		Vector<LehrerKatalogRechtsverhaeltnisEintrag> daten = new Vector<>();
		for (LehrerRechtsverhaeltnis verhaeltnis : LehrerRechtsverhaeltnis.values())
			daten.addAll(Arrays.asList(verhaeltnis.historie));
        return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(daten).build();
	}

	@Override
	public Response getList() {
		return this.getAll();
	}

	@Override
	public Response get(Long id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Response patch(Long id, InputStream is) {
		throw new UnsupportedOperationException();
	}
	
}
