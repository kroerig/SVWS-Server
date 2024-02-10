package de.svws_nrw.data.gost;

import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import de.svws_nrw.core.data.gost.GostFach;
import de.svws_nrw.core.utils.gost.GostFaecherManager;
import de.svws_nrw.data.DataManager;
import de.svws_nrw.data.JSONMapper;
import de.svws_nrw.data.faecher.DBUtilsFaecherGost;
import de.svws_nrw.data.schule.SchulUtils;
import de.svws_nrw.db.DBEntityManager;
import de.svws_nrw.db.dto.current.gost.DTOGostJahrgangFaecher;
import de.svws_nrw.db.dto.current.gost.DTOGostJahrgangsdaten;
import de.svws_nrw.db.dto.current.schild.faecher.DTOFach;
import de.svws_nrw.db.dto.current.schild.schule.DTOEigeneSchule;
import de.svws_nrw.db.utils.OperationError;

/**
 * Diese Klasse erweitert den abstrakten {@link DataManager} für den
 * Core-DTO {@link GostFach}.
 */
public final class DataGostFaecher extends DataManager<Long> {

	private final int abijahr;

	/**
	 * Erstellt einen neuen {@link DataManager} für den Core-DTO {@link GostFach}.
	 *
	 * @param conn      die Datenbank-Verbindung für den Datenbankzugriff
	 * @param abijahr   der Abi-Jahrgang, für welchen die Gost-Fächer verwaltet werden sollen,
	 *                  null für die allgemeinen Jahrgangsübergreifenden Gost-Fachinformationen
	 */
	public DataGostFaecher(final DBEntityManager conn, final int abijahr) {
		super(conn);
		this.abijahr = abijahr;
	}

	/**
	 * Bestimmt die Liste der Fächer der gymnasialen Oberstufe für den
	 * spezifizierten Abiturjahrgang.
	 *
	 * @param conn      die Datenbankverbindung
	 * @param abijahr   der Abiturjahrgang
	 *
	 * @return der Manager für die Liste der Fächer der gymnasialen Oberstufe
	 */
	public static GostFaecherManager getFaecherManager(final DBEntityManager conn, final int abijahr) {
		final @NotNull DTOEigeneSchule schule = SchulUtils.getDTOSchule(conn);
    	if ((schule.Schulform == null) || (schule.Schulform.daten == null) || (!schule.Schulform.daten.hatGymOb))
    		return null;
    	return DBUtilsFaecherGost.getFaecherManager(conn, abijahr);
	}

	@Override
	public Response getAll() {
		return this.getList();
	}

	@Override
	public Response getList() {
		final Collection<GostFach> daten = getFaecherManager(conn, abijahr).faecher();
        return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(daten).build();
	}

	@Override
	public Response get(final Long id) {
		DBUtilsGost.pruefeSchuleMitGOSt(conn);
    	final Map<Long, DTOFach> faecher = conn.queryAll(DTOFach.class).stream().collect(Collectors.toMap(f -> f.ID, f -> f));
    	if (faecher == null)
    		return OperationError.NOT_FOUND.getResponse();
		final DTOFach fach = faecher.get(id);
    	if (fach == null)
    		return OperationError.NOT_FOUND.getResponse();
		GostFach daten = null;
		if (abijahr == -1) {
	    	daten = DBUtilsFaecherGost.mapFromDTOFach(fach, faecher);
		} else {
    		if (conn.queryByKey(DTOGostJahrgangsdaten.class, abijahr) == null)
    			throw OperationError.NOT_FOUND.exception();
	    	final DTOGostJahrgangFaecher jf = conn.queryByKey(DTOGostJahrgangFaecher.class, abijahr, id);
	    	daten = DBUtilsFaecherGost.mapFromDTOGostJahrgangFaecher(id, jf, faecher);
		}
        return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(daten).build();
	}

	@Override
	public Response patch(final Long id, final InputStream is) {
    	final Map<String, Object> map = JSONMapper.toMap(is);
    	if (map.size() > 0) {
			DBUtilsGost.pruefeSchuleMitGOSt(conn);
	    	final Map<Long, DTOFach> faecher = conn.queryAll(DTOFach.class).stream().collect(Collectors.toMap(f -> f.ID, f -> f));
	    	if (faecher == null)
	    		return OperationError.NOT_FOUND.getResponse();
			final DTOFach fach = faecher.get(id);
	    	if (fach == null)
	    		return OperationError.NOT_FOUND.getResponse();
			if (abijahr == -1) {
		    	for (final Entry<String, Object> entry : map.entrySet()) {
		    		final String key = entry.getKey();
		    		final Object value = entry.getValue();
		    		switch (key) {
						case "id" -> {
							final Long patch_id = JSONMapper.convertToLong(value, true);
							if ((patch_id == null) || (patch_id.longValue() != id.longValue()))
								throw OperationError.BAD_REQUEST.exception();
						}
		    			// Änderungen von allgemeinen Fachinformationen sind hier nicht erlaubt, nur GOSt-spezifische
		    			case "kuerzel" -> throw OperationError.BAD_REQUEST.exception();
		    			case "kuerzelAnzeige" -> throw OperationError.BAD_REQUEST.exception();
		    			case "bezeichnung" -> throw OperationError.BAD_REQUEST.exception();
		    			case "sortierung" -> throw OperationError.BAD_REQUEST.exception();
		    			case "istPruefungsordnungsRelevant" -> throw OperationError.BAD_REQUEST.exception();
		    			case "istFremdsprache" -> throw OperationError.BAD_REQUEST.exception();
		    			case "istFremdSpracheNeuEinsetzend" -> throw OperationError.BAD_REQUEST.exception();
		    			case "biliSprache" -> throw OperationError.BAD_REQUEST.exception();
		    			case "istMoeglichAbiLK" -> fach.IstMoeglichAbiLK = JSONMapper.convertToBoolean(value, false);
		    			case "istMoeglichAbiGK" -> fach.IstMoeglichAbiGK = JSONMapper.convertToBoolean(value, false);
		    			case "istMoeglichEF1" -> fach.IstMoeglichEF1 = JSONMapper.convertToBoolean(value, false);
		    			case "istMoeglichEF2" -> fach.IstMoeglichEF2 = JSONMapper.convertToBoolean(value, false);
		    			case "istMoeglichQ11" -> fach.IstMoeglichQ11 = JSONMapper.convertToBoolean(value, false);
		    			case "istMoeglichQ12" -> fach.IstMoeglichQ12 = JSONMapper.convertToBoolean(value, false);
		    			case "istMoeglichQ21" -> fach.IstMoeglichQ21 = JSONMapper.convertToBoolean(value, false);
		    			case "istMoeglichQ22" -> fach.IstMoeglichQ22 = JSONMapper.convertToBoolean(value, false);
		    			case "wochenstundenEF1" -> throw OperationError.BAD_REQUEST.exception();  // derzeit nicht unterstützt
		    			case "wochenstundenEF2" -> throw OperationError.BAD_REQUEST.exception();  // derzeit nicht unterstützt
		    			case "wochenstundenQualifikationsphase" -> {
		    				// TODO Prüfe, ob die Wochenstunden bei dem Fach gesetzt werden dürfen (z.B. PJK) sonst: throw OperationError.BAD_REQUEST.exception();
		    				fach.WochenstundenQualifikationsphase = JSONMapper.convertToInteger(value, false);
		    			}
		    			case "projektKursLeitfach1ID" -> {
		    				fach.ProjektKursLeitfach1_ID = JSONMapper.convertToLong(value, true);
		    		    	if ((fach.ProjektKursLeitfach1_ID != null) && (fach.ProjektKursLeitfach1_ID < 0))
		    		    		throw OperationError.CONFLICT.exception();
	    			    	if ((fach.ProjektKursLeitfach1_ID != null) && (faecher.get(fach.ProjektKursLeitfach1_ID) == null))
	    			    		throw OperationError.NOT_FOUND.exception();
		    			}
		    			case "projektKursLeitfach2ID" -> {
		    				fach.ProjektKursLeitfach2_ID = JSONMapper.convertToLong(value, true);
		    		    	if ((fach.ProjektKursLeitfach2_ID != null) && (fach.ProjektKursLeitfach2_ID < 0))
		    		    		throw OperationError.CONFLICT.exception();
	    			    	if ((fach.ProjektKursLeitfach2_ID != null) && (faecher.get(fach.ProjektKursLeitfach2_ID) == null))
	    			    		throw OperationError.NOT_FOUND.exception();
		    			}
		    			case "projektKursLeitfach1Kuerzel" -> throw OperationError.BAD_REQUEST.exception();
		    			case "projektKursLeitfach2Kuerzel" -> throw OperationError.BAD_REQUEST.exception();
		    			default -> throw OperationError.BAD_REQUEST.exception();
		    		}
		    	}
		    	conn.transactionPersist(fach);
	    	} else {
	    		if (conn.queryByKey(DTOGostJahrgangsdaten.class, abijahr) == null)
	    			throw OperationError.BAD_REQUEST.exception();
    	    	DTOGostJahrgangFaecher jf = conn.queryByKey(DTOGostJahrgangFaecher.class, abijahr, id);
    	    	if (jf == null) {
    	    		jf = new DTOGostJahrgangFaecher(abijahr, fach.ID, false, false, false, false, false, false, false, false);
    	    		jf.WochenstundenQPhase = fach.WochenstundenQualifikationsphase;
    	    	}
		    	for (final Entry<String, Object> entry : map.entrySet()) {
		    		final String key = entry.getKey();
		    		final Object value = entry.getValue();
		    		switch (key) {
						case "id" -> {
							final Long patch_id = JSONMapper.convertToLong(value, true);
							if ((patch_id == null) || (!Objects.equals(patch_id, id)))
								throw OperationError.BAD_REQUEST.exception();
						}
		    			// Änderungen von allgemeinen Fachinformationen sind hier nicht erlaubt, nur GOSt-spezifische
		    			case "kuerzel" -> throw OperationError.BAD_REQUEST.exception();
		    			case "kuerzelAnzeige" -> throw OperationError.BAD_REQUEST.exception();
		    			case "bezeichnung" -> throw OperationError.BAD_REQUEST.exception();
		    			case "sortierung" -> throw OperationError.BAD_REQUEST.exception();
		    			case "istFremdsprache" -> throw OperationError.BAD_REQUEST.exception();
		    			case "istFremdSpracheNeuEinsetzend" -> throw OperationError.BAD_REQUEST.exception();
		    			case "biliSprache" -> throw OperationError.BAD_REQUEST.exception();
		    			case "istMoeglichAbiLK" -> jf.WaehlbarAbiLK = JSONMapper.convertToBoolean(value, false);
		    			case "istMoeglichAbiGK" -> jf.WaehlbarAbiGK = JSONMapper.convertToBoolean(value, false);
		    			case "istMoeglichEF1" -> jf.WaehlbarEF1 = JSONMapper.convertToBoolean(value, false);
		    			case "istMoeglichEF2" -> jf.WaehlbarEF2 = JSONMapper.convertToBoolean(value, false);
		    			case "istMoeglichQ11" -> jf.WaehlbarQ11 = JSONMapper.convertToBoolean(value, false);
		    			case "istMoeglichQ12" -> jf.WaehlbarQ12 = JSONMapper.convertToBoolean(value, false);
		    			case "istMoeglichQ21" -> jf.WaehlbarQ21 = JSONMapper.convertToBoolean(value, false);
		    			case "istMoeglichQ22" -> jf.WaehlbarQ22 = JSONMapper.convertToBoolean(value, false);
		    			case "wochenstundenEF1" -> throw OperationError.BAD_REQUEST.exception();  // derzeit nicht unterstützt
		    			case "wochenstundenEF2" -> throw OperationError.BAD_REQUEST.exception();  // derzeit nicht unterstützt
		    			case "wochenstundenQualifikationsphase" -> {
		    				// TODO Prüfe, ob die Wochenstunden bei dem Fach gesetzt werden dürfen (z.B. PJK) sonst: throw OperationError.BAD_REQUEST.exception();
		    				jf.WochenstundenQPhase = JSONMapper.convertToInteger(value, false);
		    			}
		    			case "projektKursLeitfach1ID" -> throw OperationError.BAD_REQUEST.exception();
		    			case "projektKursLeitfach2ID" -> throw OperationError.BAD_REQUEST.exception();
		    			case "projektKursLeitfach1Kuerzel" -> throw OperationError.BAD_REQUEST.exception();
		    			case "projektKursLeitfach2Kuerzel" -> throw OperationError.BAD_REQUEST.exception();
		    			default -> throw OperationError.BAD_REQUEST.exception();
		    		}
		    	}
		    	conn.transactionPersist(jf);
	    	}
		}
        return Response.status(Status.OK).build();
	}

}
