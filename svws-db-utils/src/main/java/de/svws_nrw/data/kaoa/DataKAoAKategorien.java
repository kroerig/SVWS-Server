package de.svws_nrw.data.kaoa;

import java.io.InputStream;
import java.util.ArrayList;

import de.svws_nrw.asd.data.kaoa.KAOAKategorieKatalogEintrag;
import de.svws_nrw.asd.types.kaoa.KAOAKategorie;
import de.svws_nrw.data.DataManager;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * Diese Klasse erweitert den abstrakten {@link DataManager} für den
 * Core-DTO {@link KAOAKategorieKatalogEintrag}.
 */
public final class DataKAoAKategorien extends DataManager<Long> {

	/**
	 * Erstellt einen neuen {@link DataManager} für den Core-DTO {@link KAOAKategorieKatalogEintrag}.
	 */
	public DataKAoAKategorien() {
		super(null);
	}

	@Override
	public Response getAll() {
		final ArrayList<KAOAKategorieKatalogEintrag> daten = new ArrayList<>();
		for (final KAOAKategorie kategorie : KAOAKategorie.values())
			daten.addAll(kategorie.historie());
		return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(daten).build();
	}

	@Override
	public Response getList() {
		return this.getAll();
	}

	@Override
	public Response get(final Long id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Response patch(final Long id, final InputStream is) {
		throw new UnsupportedOperationException();
	}

}
