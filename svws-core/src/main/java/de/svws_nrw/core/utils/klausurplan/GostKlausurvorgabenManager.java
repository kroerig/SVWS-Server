package de.svws_nrw.core.utils.klausurplan;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.svws_nrw.core.adt.map.HashMap2D;
import de.svws_nrw.core.adt.map.HashMap3D;
import de.svws_nrw.core.data.gost.GostFach;
import de.svws_nrw.core.data.gost.klausuren.GostKlausurvorgabe;
import de.svws_nrw.core.exceptions.DeveloperNotificationException;
import de.svws_nrw.core.utils.Map2DUtils;
import de.svws_nrw.core.utils.MapUtils;
import de.svws_nrw.core.utils.gost.GostFaecherManager;
import jakarta.validation.constraints.NotNull;

/**
 * Ein Manager zur Handhabung von Daten des Typs {@link GostKlausurvorgabe}.
 * Hierbei werden auch Hilfsmethoden zur Interpretation der Daten erzeugt. Es
 * werden Klausurvorgaben eines Gost-Halbjahres eines Abiturjahrgangs verwaltet.
 */
public class GostKlausurvorgabenManager {

	private GostFaecherManager _faecherManager = new GostFaecherManager();

	/** Die GostKlausurvorgaben, die im Manager vorhanden sind */
	private final @NotNull List<@NotNull GostKlausurvorgabe> _vorgaben = new ArrayList<>();

	/** Eine Map quartal -> Liste von GostKlausurvorgaben */
	private final @NotNull Map<@NotNull Integer, @NotNull List<@NotNull GostKlausurvorgabe>> _mapQuartalKlausurvorgaben = new HashMap<>();

	/** Eine Map id -> GostKlausurvorgabe */
	private final @NotNull Map<@NotNull Long, @NotNull GostKlausurvorgabe> _mapIdKlausurvorgabe = new HashMap<>();

	/** Eine Map quartal -> kursartAllg -> fachId -> GostKlausurvorgabe */
	private final @NotNull HashMap3D<@NotNull Integer, @NotNull String, @NotNull Long, @NotNull GostKlausurvorgabe> _mapQuartalKursartFachKlausurvorgabe = new HashMap3D<>();

	/** Eine Map kursartAllg -> fachId -> Liste von GostKlausurvorgabe */
	private final @NotNull HashMap2D<@NotNull String, @NotNull Long, @NotNull List<@NotNull GostKlausurvorgabe>> _mapKursartFachKlausurvorgaben = new HashMap2D<>();

	/** Ein Comparator für die Klausurvorgaben. */
	private final @NotNull Comparator<@NotNull GostKlausurvorgabe> _compVorgabe = (final @NotNull GostKlausurvorgabe a, final @NotNull GostKlausurvorgabe b) -> {
		if (_faecherManager != null) {
			final GostFach aFach = _faecherManager.get(a.idFach);
			final GostFach bFach = _faecherManager.get(b.idFach);
			if (aFach != null && bFach != null) {
				if (aFach.sortierung > bFach.sortierung)
					return +1;
				if (aFach.sortierung < bFach.sortierung)
					return -1;
			}
		}
		if (a.kursart.compareTo(b.kursart) < 0)
			return +1;
		if (a.kursart.compareTo(b.kursart) > 0)
			return -1;
		return Integer.compare(a.quartal, b.quartal);
	};

	/**
	 * Erstellt einen neuen Manager mit den als Liste angegebenen
	 * GostKlausurvorgaben und erzeugt die privaten Attribute.
	 *
	 * @param vorgaben       die Liste der GostKlausurvorgaben eines Abiturjahrgangs
	 *                       und Gost-Halbjahres
	 * @param faecherManager der Gost-Fächermanager
	 *
	 */
	public GostKlausurvorgabenManager(final @NotNull List<@NotNull GostKlausurvorgabe> vorgaben, final GostFaecherManager faecherManager) {
		_faecherManager = faecherManager;
		for (final @NotNull GostKlausurvorgabe v : vorgaben) {
			_mapIdKlausurvorgabe.put(v.idVorgabe, v);
			addVorgabeToInternalMaps(v);
		}
	}

	private void addVorgabeToInternalMaps(final @NotNull GostKlausurvorgabe v) {
		_vorgaben.add(v);
		_vorgaben.sort(_compVorgabe);
		_mapIdKlausurvorgabe.put(v.idVorgabe, v);

		// Füllen von _mapQuartalKlausurvorgaben
		MapUtils.getOrCreateArrayList(_mapQuartalKlausurvorgaben, v.quartal).add(v);

		// Füllen von _mapQuartalKursartFachKlausurvorgabe
		_mapQuartalKursartFachKlausurvorgabe.put(v.quartal, v.kursart, v.idFach, v);

		// Füllen von _mapKursartFachKlausurvorgaben
		Map2DUtils.getOrCreateArrayList(_mapKursartFachKlausurvorgaben, v.kursart, v.idFach).add(v);
		_mapKursartFachKlausurvorgaben.getNonNullOrException(v.kursart, v.idFach).sort(_compVorgabe);
	}

	/**
	 * Aktualisiert die internen Strukturen, nachdem sich Informationen einer
	 * Klausurvorgabe geändert hat.
	 *
	 * @param vorgabe das GostKlausurvorgabe-Objekt
	 */
	public void updateKlausurvorgabe(final @NotNull GostKlausurvorgabe vorgabe) {
		@NotNull final GostKlausurvorgabe vorgabeOrig = DeveloperNotificationException.ifMapGetIsNull(_mapIdKlausurvorgabe, vorgabe.idVorgabe);
		removeUpdateKlausurvorgabeCommons(vorgabeOrig);
		addVorgabeToInternalMaps(vorgabe);
	}

	/**
	 * Fügt die Klausurvorgabe den internen Strukturen hinzu.
	 *
	 * @param vorgabe das GostKlausurvorgabe-Objekt
	 */
	public void addKlausurvorgabe(final @NotNull GostKlausurvorgabe vorgabe) {
		_vorgaben.add(vorgabe);
		_vorgaben.sort(_compVorgabe);
		_mapIdKlausurvorgabe.put(vorgabe.idVorgabe, vorgabe);
		removeUpdateKlausurvorgabeCommons(vorgabe);
		addVorgabeToInternalMaps(vorgabe);
	}

	private void removeUpdateKlausurvorgabeCommons(final @NotNull GostKlausurvorgabe vorgabe) {
		_vorgaben.remove(vorgabe);
		_mapIdKlausurvorgabe.remove(vorgabe.idVorgabe);

		// aus _mapQuartalKlausurvorgaben löschen
		DeveloperNotificationException.ifListRemoveFailes("_mapQuartalKlausurvorgabenList", DeveloperNotificationException.ifMapGetIsNull(_mapQuartalKlausurvorgaben, vorgabe.quartal), vorgabe);

		// aus _mapQuartalKursartFachKlausurvorgabe löschen
		_mapQuartalKursartFachKlausurvorgabe.removeOrException(vorgabe.quartal, vorgabe.kursart, vorgabe.idFach);

		// aus _mapKursartFachKlausurvorgaben löschen
		DeveloperNotificationException.ifListRemoveFailes("_mapQuartalKlausurvorgabenList",
				DeveloperNotificationException.ifMap2DGetIsNull(_mapKursartFachKlausurvorgaben, vorgabe.kursart, vorgabe.idFach), vorgabe);
	}

	/**
	 * Löscht eine Klausurvorgabe aus den internen Strukturen
	 *
	 * @param vId das GostKlausurvorgabe-Objekt
	 */
	public void removeVorgabe(final @NotNull Long vId) {
		@NotNull final GostKlausurvorgabe vorgabe = DeveloperNotificationException.ifMapGetIsNull(_mapIdKlausurvorgabe, vId);
		removeUpdateKlausurvorgabeCommons(vorgabe);
	}

	/**
	 * Liefert eine Liste von GostKlausurvorgabe-Objekten des Halbjahres
	 *
	 * @return die Liste von GostKlausurvorgabe-Objekten
	 */
	public @NotNull List<@NotNull GostKlausurvorgabe> getKlausurvorgaben() {
		return _vorgaben;
	}

	/**
	 * Liefert eine Liste von GostKlausurvorgabe-Objekten zum übergebenen Quartal
	 *
	 * @param quartal die Nummer des Quartals
	 *
	 * @return die Liste von GostKlausurvorgabe-Objekten
	 */
	public List<@NotNull GostKlausurvorgabe> getKlausurvorgaben(final int quartal) {
		return _mapQuartalKlausurvorgaben.get(quartal);
	}

	/**
	 * Gibt das GostKlausurvorgabe-Objekt zur übergebenen id zurück.
	 *
	 * @param idVorgabe die ID der Klausurvorgabe
	 *
	 * @return das GostKlausurvorgabe-Objekt
	 */
	public GostKlausurvorgabe gibGostKlausurvorgabe(final long idVorgabe) {
		return _mapIdKlausurvorgabe.get(idVorgabe);
	}

	/**
	 * Gibt das GostKlausurvorgabe-Objekt zu den übergebenen Parametern zurück.
	 *
	 * @param quartal     das Quartal
	 * @param kursartAllg die Kursart
	 * @param idFach      die ID des Fachs
	 *
	 * @return das GostKlausurvorgabe-Objekt
	 */
	public GostKlausurvorgabe gibGostKlausurvorgabeByQuartalKursartFach(final int quartal, final @NotNull String kursartAllg, final long idFach) {
		return _mapQuartalKursartFachKlausurvorgabe.getOrNull(quartal, kursartAllg, idFach);
	}

	/**
	 * Gibt die Liste der GostKlausurvorgabe-Objekte zu den übergebenen Parametern
	 * zurück.
	 *
	 * @param quartal     das Quartal, wenn 0, Vorgaben für alle Quartale
	 * @param kursartAllg die Kursart
	 * @param idFach      die ID des Fachs
	 *
	 * @return die Liste der GostKlausurvorgabe-Objekte
	 */
	public List<@NotNull GostKlausurvorgabe> gibGostKlausurvorgabenByQuartalKursartFach(final int quartal, final @NotNull String kursartAllg, final long idFach) {
		if (quartal > 0) {
			final List<@NotNull GostKlausurvorgabe> retList = new ArrayList<>();
			final GostKlausurvorgabe vorgabe = gibGostKlausurvorgabeByQuartalKursartFach(quartal, kursartAllg, idFach);
			if (vorgabe != null)
				retList.add(vorgabe);
			return retList;
		}
		return gibGostKlausurvorgabenByKursartFach(kursartAllg, idFach);
	}

	/**
	 * Gibt die Liste der GostKlausurvorgabe-Objekte zu den übergebenen Parametern
	 * zurück.
	 *
	 * @param kursartAllg die Kursart
	 * @param idFach      die ID des Fachs
	 *
	 * @return die Liste der GostKlausurvorgabe-Objekte
	 */
	public List<@NotNull GostKlausurvorgabe> gibGostKlausurvorgabenByKursartFach(final @NotNull String kursartAllg, final long idFach) {
		final List<@NotNull GostKlausurvorgabe> list = _mapKursartFachKlausurvorgaben.getOrNull(kursartAllg, idFach);
		return list != null ? list : new ArrayList<>();
	}

}
