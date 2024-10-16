import { JavaObject } from '../../../java/lang/JavaObject';
import { GostFachbereich } from '../../../core/types/gost/GostFachbereich';
import { IllegalStateException } from '../../../java/lang/IllegalStateException';
import { GostFach } from '../../../core/data/gost/GostFach';
import { GostHalbjahr } from '../../../core/types/gost/GostHalbjahr';
import { JavaString } from '../../../java/lang/JavaString';

export class GostFachUtils extends JavaObject {


	private constructor() {
		super();
		throw new IllegalStateException("Instantiation not allowed")
	}

	/**
	 * Gibt an, ob es sich bei dem Fach um ein Projektkursfach handelt.
	 *
	 * @param fach   das Fach der gymnasialen Oberstufe
	 *
	 * @return true, falls es sich um ein Projektkursfach handelt
	 */
	public static istProjektkurs(fach : GostFach) : boolean {
		return JavaObject.equalsTranspiler("PX", (fach.kuerzel));
	}

	/**
	 * Gibt an, ob es sich bei dem Fach um ein Vertiefungskursfach handelt.
	 *
	 * @param fach   das Fach der gymnasialen Oberstufe
	 *
	 * @return true, falls es sich um ein Vertiefungskursfach handelt
	 */
	public static istVertiefungskurs(fach : GostFach) : boolean {
		return JavaObject.equalsTranspiler("VX", (fach.kuerzel));
	}

	/**
	 * Gibt an, ob das Fach durchgehend von EF.1 bis Q2.2 belegbar ist;
	 *
	 * @param fach   das Fach der gymnasialen Oberstufe
	 *
	 * @return true, falls es so belegbar ist, sonst false
	 */
	public static istDurchgehendBelegbarBisQ22(fach : GostFach | null) : boolean {
		if (fach === null)
			return false;
		return fach.istMoeglichEF1 && fach.istMoeglichEF2 && fach.istMoeglichQ11 && fach.istMoeglichQ12 && fach.istMoeglichQ21 && fach.istMoeglichQ22;
	}

	/**
	 * Gibt an, ob das Fach durchgehend von EF.1 bis EF.2 belegbar ist;
	 *
	 * @param fach   das Fach der gymnasialen Oberstufe
	 *
	 * @return true, falls es so belegbar ist, sonst false
	 */
	public static istBelegbarBisEF2(fach : GostFach) : boolean {
		return fach.istMoeglichEF1 && fach.istMoeglichEF2;
	}

	/**
	 * Prüft, ob das Fach zu der angegebenen Sprache gehört
	 *
	 * @param fach   das Fach der gymnasialen Oberstufe
	 * @param sprache   das Kürzel der Sprache (1. Zeichen ohne Jahrgang!)
	 *
	 * @return true, falls das Fach zu der angegebenen Sprache passt, sonst false
	 */
	public static istFremdsprachenfach(fach : GostFach | null, sprache : string | null) : boolean {
		if ((fach === null) || (fach.kuerzel === null) || (JavaObject.equalsTranspiler("", (fach.kuerzel))) || !GostFachbereich.FREMDSPRACHE.hat(fach) || (sprache === null))
			return false;
		return (JavaString.compareToIgnoreCase(sprache, fach.kuerzel.substring(0, 1)) === 0);
	}

	/**
	 * Liefert das Kürzel der Sprache (ohne Jahrgang) zurück, falls es sich um eine Sprache handelt.
	 *
	 * @param fach   das Fach der gymnasialen Oberstufe
	 *
	 * @return das Kürzel der Sprache oder null
	 */
	public static getFremdsprache(fach : GostFach) : string | null {
		if ((fach.kuerzel === null) || (JavaObject.equalsTranspiler("", (fach.kuerzel))) || !GostFachbereich.FREMDSPRACHE.hat(fach))
			return null;
		return fach.kuerzel.substring(0, 1).toUpperCase();
	}

	/**
	 * Prüft, ob das Fach bilingual unterrichtet wird oder nicht.
	 *
	 * @param fach   das Fach der gymnasialen Oberstufe
	 *
	 * @return true, falls das Fach bilingual unterrichtet wird.
	 */
	public static istBilingual(fach : GostFach) : boolean {
		return ((fach.biliSprache !== null) && (!JavaObject.equalsTranspiler("", (fach.biliSprache))) && (!JavaObject.equalsTranspiler("D", (fach.biliSprache))));
	}

	/**
	 * Gibt zurück, ob das Fach in dem angegebenen Halbjahr wählbar ist oder nicht.
	 *
	 * @param fach   das Fach der gymnasialen Oberstufe
	 * @param halbjahr   das zu prüfende Halbjahr
	 *
	 * @return true, falls das Fach in dem Halbjahr wählbar ist, sonst false
	 */
	public static istWaehlbar(fach : GostFach | null, halbjahr : GostHalbjahr) : boolean {
		if (fach === null)
			return false;
		if (halbjahr as unknown === GostHalbjahr.EF1 as unknown)
			return fach.istMoeglichEF1;
		if (halbjahr as unknown === GostHalbjahr.EF2 as unknown)
			return fach.istMoeglichEF2;
		if (halbjahr as unknown === GostHalbjahr.Q11 as unknown)
			return fach.istMoeglichQ11;
		if (halbjahr as unknown === GostHalbjahr.Q12 as unknown)
			return fach.istMoeglichQ12;
		if (halbjahr as unknown === GostHalbjahr.Q21 as unknown)
			return fach.istMoeglichQ21;
		if (halbjahr as unknown === GostHalbjahr.Q22 as unknown)
			return fach.istMoeglichQ22;
		return false;
	}

	/**
	 * Prüft, ob das übergebene Fach als erster Leistungskurs wählbahr ist.
	 * Dafür muss es laut APO Gost §12 (4) "eine aus der Sekundarstufe I
	 * fortgeführte Fremdsprache oder Mathematik oder eine Naturwissenschaft
	 * oder Deutsch sein".
	 *
	 * @param fach   das Fach der gymnasialen Oberstufe
	 *
	 * @return true, falls das Fach an sich als erster Leistungskurs belegbar
	 *         ist und ansonsten false.
	 */
	public static istWaehlbarLeistungskurs1(fach : GostFach | null) : boolean {
		return ((fach !== null) && (fach.istMoeglichAbiLK) && (fach.istMoeglichQ11) && (fach.istMoeglichQ12) && (fach.istMoeglichQ21) && (fach.istMoeglichQ22)) && ((GostFachbereich.FREMDSPRACHE.hat(fach) && !fach.istFremdSpracheNeuEinsetzend) || (GostFachbereich.MATHEMATIK.hat(fach)) || (GostFachbereich.NATURWISSENSCHAFTLICH.hat(fach)) || (GostFachbereich.DEUTSCH.hat(fach)));
	}

	transpilerCanonicalName(): string {
		return 'de.svws_nrw.core.utils.gost.GostFachUtils';
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.svws_nrw.core.utils.gost.GostFachUtils'].includes(name);
	}

}

export function cast_de_svws_nrw_core_utils_gost_GostFachUtils(obj : unknown) : GostFachUtils {
	return obj as GostFachUtils;
}
