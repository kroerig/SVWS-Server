import { JavaEnum } from '../../../java/lang/JavaEnum';
import { JavaObject } from '../../../java/lang/JavaObject';
import { HashMap } from '../../../java/util/HashMap';
import { SchulabschlussAllgemeinbildendKatalogEintrag } from '../../../core/data/schule/SchulabschlussAllgemeinbildendKatalogEintrag';

export class SchulabschlussAllgemeinbildend extends JavaEnum<SchulabschlussAllgemeinbildend> {

	/** an array containing all values of this enumeration */
	static readonly all_values_by_ordinal : Array<SchulabschlussAllgemeinbildend> = [];

	/** an array containing all values of this enumeration indexed by their name*/
	static readonly all_values_by_name : Map<string, SchulabschlussAllgemeinbildend> = new Map<string, SchulabschlussAllgemeinbildend>();

	/**
	 * Es liegt kein Abschluss vor
	 */
	public static readonly OA : SchulabschlussAllgemeinbildend = new SchulabschlussAllgemeinbildend("OA", 0, [new SchulabschlussAllgemeinbildendKatalogEintrag(0, "OA", "Ohne Abschluss", "A", null, null)]);

	/**
	 * Hauptschulabschluss nach Klasse 9 (ohne Berechtigung zum Besuch der Klasse 10 Typ B)
	 */
	public static readonly HA9A : SchulabschlussAllgemeinbildend = new SchulabschlussAllgemeinbildend("HA9A", 1, [new SchulabschlussAllgemeinbildendKatalogEintrag(1000, "HA9A", "Hauptschulabschluss nach Klasse 9 (ohne Berechtigung zum Besuch der Klasse 10 Typ B)", "B", null, 2021), new SchulabschlussAllgemeinbildendKatalogEintrag(1001, "ESAA", "Erster Schulabschluss (ohne Berechtigung zum Besuch der Klasse 10 Typ B)", "B", 2022, null)]);

	/**
	 * Hauptschulabschluss nach Klasse 9 (mit Berechtigung zum Besuch der Klasse 10 Typ B)
	 */
	public static readonly HA9 : SchulabschlussAllgemeinbildend = new SchulabschlussAllgemeinbildend("HA9", 2, [new SchulabschlussAllgemeinbildendKatalogEintrag(2000, "HA9", "Hauptschulabschluss nach Klasse 9 (mit Berechtigung zum Besuch der Klasse 10 Typ B)", "C", null, 2021), new SchulabschlussAllgemeinbildendKatalogEintrag(2001, "ESA", "Erster Schulabschluss (mit Berechtigung zum Besuch der Klasse 10 Typ B)", "C", 2022, null)]);

	/**
	 * Hauptschulabschluss nach Klasse 9 (ggf. mit Berechtigung zum Besuch eines weiterführenden Bildungsgangs am Berufskolleg bei internationalen Förderklassen) - siehe BK-Bildungsgang A12
	 */
	public static readonly HA9_FOE : SchulabschlussAllgemeinbildend = new SchulabschlussAllgemeinbildend("HA9_FOE", 3, [new SchulabschlussAllgemeinbildendKatalogEintrag(3000, "HA9_FOE", "Hauptschulabschluss nach Klasse 9 (ggf. mit Berechtigung zum Besuch eines weiterführenden Bildungsgangs am Berufskolleg bei internationalen Förderklassen)", "S", null, 2021), new SchulabschlussAllgemeinbildendKatalogEintrag(3001, "ESA_FOE", "Erster Schulabschluss (ggf. mit Berechtigung zum Besuch eines weiterführenden Bildungsgangs am Berufskolleg bei internationalen Förderklassen)", "S", 2022, null)]);

	/**
	 * Hauptschulabschluss nach Klasse 9 mit der Berechtigung zum Besuch der Gymnasialen Oberstufe
	 */
	public static readonly HA9_Q : SchulabschlussAllgemeinbildend = new SchulabschlussAllgemeinbildend("HA9_Q", 4, [new SchulabschlussAllgemeinbildendKatalogEintrag(4000, "HA9_Q", "Hauptschulabschluss nach Klasse 9 (mit der Berechtigung zum Besuch der Gymnasialen Oberstufe)", "O", null, 2021), new SchulabschlussAllgemeinbildendKatalogEintrag(4001, "ESA_Q", "Erster Schulabschluss (mit der Berechtigung zum Besuch der Gymnasialen Oberstufe)", "O", 2022, null)]);

	/**
	 * Hauptschulabschluss nach Klasse 10
	 */
	public static readonly HA10 : SchulabschlussAllgemeinbildend = new SchulabschlussAllgemeinbildend("HA10", 5, [new SchulabschlussAllgemeinbildendKatalogEintrag(5000, "HA10", "Hauptschulabschluss nach Klasse 10", "D", null, 2021), new SchulabschlussAllgemeinbildendKatalogEintrag(5001, "EESA", "Erweiterer Erster Schulabschluss", "D", 2022, null)]);

	/**
	 * Hauptschulabschluss nach Klasse 10 mit der Berechtigung zum Besuch der Gymnasialen Oberstufe
	 */
	public static readonly HA10_Q : SchulabschlussAllgemeinbildend = new SchulabschlussAllgemeinbildend("HA10_Q", 6, [new SchulabschlussAllgemeinbildendKatalogEintrag(6000, "HA10_Q", "Hauptschulabschluss nach Klasse 10 (mit der Berechtigung zum Besuch der Gymnasialen Oberstufe)", "U", null, 2021), new SchulabschlussAllgemeinbildendKatalogEintrag(6001, "EESA_Q", "Erweiterter Erster Schulabschluss (mit der Berechtigung zum Besuch der Gymnasialen Oberstufe)", "U", 2022, null)]);

	/**
	 * Der Mittlere Schulabschluss bzw. Fachoberschulreife
	 */
	public static readonly MSA : SchulabschlussAllgemeinbildend = new SchulabschlussAllgemeinbildend("MSA", 7, [new SchulabschlussAllgemeinbildendKatalogEintrag(10000, "MSA", "Mittlerer Schulabschluss", "F", null, null)]);

	/**
	 * Der Mittlere Schulabschluss mit der Berechtigung zum Besuch Gymnasialen Oberstufe
	 */
	public static readonly MSA_Q : SchulabschlussAllgemeinbildend = new SchulabschlussAllgemeinbildend("MSA_Q", 8, [new SchulabschlussAllgemeinbildendKatalogEintrag(11000, "MSA_Q", "Mittlerer Schulabschluss (mit der Berechtigung zum Besuch Gymnasialen Oberstufe)", "G", null, null)]);

	/**
	 * Der Mittlere Schulabschluss mit der Berechtigung zum Besuch der Qualifikationsphase der Gymnasialen Oberstufe
	 */
	public static readonly MSA_Q1 : SchulabschlussAllgemeinbildend = new SchulabschlussAllgemeinbildend("MSA_Q1", 9, [new SchulabschlussAllgemeinbildendKatalogEintrag(12000, "MSA_Q1", "Mittlerer Schulabschluss mit der Berechtigung zum Besuch der Qualifikationsphase Gymnasialen Oberstufe", "I", null, null)]);

	/**
	 * Versetzung in die Klasse 11 der Fachoberschule (BK)
	 */
	public static readonly VS_11 : SchulabschlussAllgemeinbildend = new SchulabschlussAllgemeinbildend("VS_11", 10, [new SchulabschlussAllgemeinbildendKatalogEintrag(13000, "VS_11", "Versetzung in die Klasse 11 der Fachoberschule (BK)", "P", null, null)]);

	/**
	 * Fachhochschulreife (nur schulischer Teil)
	 */
	public static readonly FHR_S : SchulabschlussAllgemeinbildend = new SchulabschlussAllgemeinbildend("FHR_S", 11, [new SchulabschlussAllgemeinbildendKatalogEintrag(20000, "FHR_S", "Fachhochschulreife (nur schulischer Teil)", "H", null, null)]);

	/**
	 * Fachhochschulreife
	 */
	public static readonly FHR : SchulabschlussAllgemeinbildend = new SchulabschlussAllgemeinbildend("FHR", 12, [new SchulabschlussAllgemeinbildendKatalogEintrag(21000, "FHR", "Fachhochschulreife", "J", null, null)]);

	/**
	 * fachgebundene Hochschulreife (BK)
	 */
	public static readonly FGHR : SchulabschlussAllgemeinbildend = new SchulabschlussAllgemeinbildend("FGHR", 13, [new SchulabschlussAllgemeinbildendKatalogEintrag(22000, "FGHR", "fachgebundene Hochschulreife (BK)", "Q", null, null)]);

	/**
	 * Abitur / Allgemeine Hochschulreife
	 */
	public static readonly ABITUR : SchulabschlussAllgemeinbildend = new SchulabschlussAllgemeinbildend("ABITUR", 14, [new SchulabschlussAllgemeinbildendKatalogEintrag(30000, "ABITUR", "Abitur / Allgemeine Hochschulreife", "K", null, null)]);

	/**
	 * Förderschule (Förderschwerpunkt geistige Entwicklung)
	 */
	public static readonly FOEG : SchulabschlussAllgemeinbildend = new SchulabschlussAllgemeinbildend("FOEG", 15, [new SchulabschlussAllgemeinbildendKatalogEintrag(40000, "FOEG", "Förderschule (Förderschwerpunkt geistige Entwicklung)", "M", null, null)]);

	/**
	 * Förderschule (Förderschwerpunkt Lernen)
	 */
	public static readonly FOEL : SchulabschlussAllgemeinbildend = new SchulabschlussAllgemeinbildend("FOEL", 16, [new SchulabschlussAllgemeinbildendKatalogEintrag(41000, "FOEL", "Förderschule (Förderschwerpunkt Lernen)", "V", null, null)]);

	/**
	 * Waldorfschule
	 */
	public static readonly WALD : SchulabschlussAllgemeinbildend = new SchulabschlussAllgemeinbildend("WALD", 17, [new SchulabschlussAllgemeinbildendKatalogEintrag(50000, "WALD", "Zeugnis der Waldorfschule", "W", null, null)]);

	/**
	 * Die Version dieses Core-Types, um beim Datenbank Update-Process die Version des Core-Types feststellen zu können.
	 */
	public static readonly VERSION : number = 2;

	/**
	 * Der aktuellen Daten der Abschlussart, wenn keine Beschränkung der Gültigkeit vorliegen - sonst null
	 */
	public readonly daten : SchulabschlussAllgemeinbildendKatalogEintrag;

	/**
	 * Die Historie mit den Einträgen der Abschlussarten
	 */
	public readonly historie : Array<SchulabschlussAllgemeinbildendKatalogEintrag>;

	/**
	 * Eine HashMap mit den Abschlussarten, welche ihren Kürzeln zugeordnet werden
	 */
	private static readonly _mapByKuerzel : HashMap<string, SchulabschlussAllgemeinbildend> = new HashMap();

	/**
	 * Eine HashMap mit den Abschlussarten, welche ihren Statistik-Kürzeln zugeordnet werden
	 */
	private static readonly _mapByKuerzelStatistik : HashMap<string, SchulabschlussAllgemeinbildend> = new HashMap();

	/**
	 * Erzeugt eine neue Abschlussart in der Aufzählung.
	 *
	 * @param historie   die Historie der Abschlussarten, welches ein Array von {@link SchulabschlussAllgemeinbildendKatalogEintrag} ist
	 */
	private constructor(name : string, ordinal : number, historie : Array<SchulabschlussAllgemeinbildendKatalogEintrag>) {
		super(name, ordinal);
		SchulabschlussAllgemeinbildend.all_values_by_ordinal.push(this);
		SchulabschlussAllgemeinbildend.all_values_by_name.set(name, this);
		this.historie = historie;
		this.daten = historie[historie.length - 1];
	}

	/**
	 * Gibt eine Map von den Kürzeln der Abschlussarten auf die zugehörigen Abschlussarten
	 * zurück. Sollte diese noch nicht initialisiert sein, so wird sie initialisiert.
	 *
	 * @return die Map von den Kürzeln der Abschlussarten auf die zugehörigen Abschlussarten
	 */
	private static getMapByKuerzel() : HashMap<string, SchulabschlussAllgemeinbildend> {
		if (SchulabschlussAllgemeinbildend._mapByKuerzel.size() === 0) {
			for (const s of SchulabschlussAllgemeinbildend.values()) {
				if (s.daten !== null)
					SchulabschlussAllgemeinbildend._mapByKuerzel.put(s.daten.kuerzel, s);
			}
		}
		return SchulabschlussAllgemeinbildend._mapByKuerzel;
	}

	/**
	 * Gibt die Abschlussart für das angegebene Kürzel zurück.
	 *
	 * @param kuerzel   das Kürzel der Abschlussart
	 *
	 * @return die Abschlussart oder null, falls das Kürzel ungültig ist
	 */
	public static getByKuerzel(kuerzel : string | null) : SchulabschlussAllgemeinbildend | null {
		return SchulabschlussAllgemeinbildend.getMapByKuerzel().get(kuerzel);
	}

	/**
	 * Gibt eine Map von den Statistik-Kürzeln der Abschlussarten auf die zugehörigen Abschlussarten
	 * zurück. Sollte diese noch nicht initialisiert sein, so wird sie initialisiert.
	 *
	 * @return die Map von den Statistik-Kürzeln der Abschlussarten auf die zugehörigen Abschlussarten
	 */
	private static getMapByKuerzelStatistik() : HashMap<string, SchulabschlussAllgemeinbildend> {
		if (SchulabschlussAllgemeinbildend._mapByKuerzelStatistik.size() === 0) {
			for (const s of SchulabschlussAllgemeinbildend.values()) {
				if (s.daten !== null)
					SchulabschlussAllgemeinbildend._mapByKuerzelStatistik.put(s.daten.kuerzelStatistik, s);
			}
		}
		return SchulabschlussAllgemeinbildend._mapByKuerzelStatistik;
	}

	/**
	 * Gibt die Abschlussart für das angegebene Kürzel zurück.
	 *
	 * @param kuerzel   das Statistik-Kürzel der Abschlussart
	 *
	 * @return die Abschlussart oder null, falls das Statistik-Kürzel ungültig ist
	 */
	public static getByKuerzelStatistik(kuerzel : string | null) : SchulabschlussAllgemeinbildend | null {
		return SchulabschlussAllgemeinbildend.getMapByKuerzelStatistik().get(kuerzel);
	}

	/**
	 * Prüft, ob dieser Abschluss dem im String-Parameter str übergebenen
	 * Abschluss entspricht.
	 *
	 * @param str   der Name des Abschlusses für den Vergleich als String
	 *
	 * @return true, falls beide Abschlüsse übereinstimmen und ansonsten false
	 */
	public is(str : string | null) : boolean {
		if (str === null)
			return false;
		try {
			const other : SchulabschlussAllgemeinbildend | null = SchulabschlussAllgemeinbildend.valueOf(str);
			return JavaObject.equalsTranspiler(this, (other));
		} catch(e) {
			return false;
		}
	}

	/**
	 * Returns an array with enumeration values.
	 *
	 * @returns the array with enumeration values
	 */
	public static values() : Array<SchulabschlussAllgemeinbildend> {
		return [...this.all_values_by_ordinal];
	}

	/**
	 * Returns the enumeration value with the specified name.
	 *
	 * @param name   the name of the enumeration value
	 *
	 * @returns the enumeration values or null
	 */
	public static valueOf(name : string) : SchulabschlussAllgemeinbildend | null {
		const tmp = this.all_values_by_name.get(name);
		return (!tmp) ? null : tmp;
	}

	transpilerCanonicalName(): string {
		return 'de.svws_nrw.core.types.schule.SchulabschlussAllgemeinbildend';
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.svws_nrw.core.types.schule.SchulabschlussAllgemeinbildend', 'java.lang.Enum', 'java.lang.Comparable'].includes(name);
	}

}

export function cast_de_svws_nrw_core_types_schule_SchulabschlussAllgemeinbildend(obj : unknown) : SchulabschlussAllgemeinbildend {
	return obj as SchulabschlussAllgemeinbildend;
}
