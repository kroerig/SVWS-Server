import { JavaEnum } from '../../../java/lang/JavaEnum';
import { HashMap } from '../../../java/util/HashMap';
import { LehrerKatalogMehrleistungsartEintrag } from '../../../core/data/lehrer/LehrerKatalogMehrleistungsartEintrag';

export class LehrerMehrleistungArt extends JavaEnum<LehrerMehrleistungArt> {

	/** an array containing all values of this enumeration */
	static readonly all_values_by_ordinal : Array<LehrerMehrleistungArt> = [];

	/** an array containing all values of this enumeration indexed by their name*/
	static readonly all_values_by_name : Map<string, LehrerMehrleistungArt> = new Map<string, LehrerMehrleistungArt>();

	/**
	 * Mehrleistungsart 'Beschäftigungsphase Sabbatjahr'
	 */
	public static readonly ID_100 : LehrerMehrleistungArt = new LehrerMehrleistungArt("ID_100", 0, [new LehrerKatalogMehrleistungsartEintrag(1, "100", "Beschäftigungsphase Sabbatjahr", null, null)]);

	/**
	 * Mehrleistungsart 'Mehrarbeit (angeordnet und regelmäßig)'
	 */
	public static readonly ID_110 : LehrerMehrleistungArt = new LehrerMehrleistungArt("ID_110", 1, [new LehrerKatalogMehrleistungsartEintrag(2, "110", "Mehrarbeit (angeordnet und regelmäßig)", null, null)]);

	/**
	 * Mehrleistungsart 'Aufrundung der Pflichtstundenzahl wegen Abrundung im folgenden Schuljahr '
	 */
	public static readonly ID_150 : LehrerMehrleistungArt = new LehrerMehrleistungArt("ID_150", 2, [new LehrerKatalogMehrleistungsartEintrag(3, "150", "Aufrundung der Pflichtstundenzahl wegen Abrundung im folgenden Schuljahr ", null, null)]);

	/**
	 * Mehrleistungsart 'Überschreitung der Pflichtstundenzahl aus organisatorischen Gründen (z. B. Epochenunterricht)'
	 */
	public static readonly ID_160 : LehrerMehrleistungArt = new LehrerMehrleistungArt("ID_160", 3, [new LehrerKatalogMehrleistungsartEintrag(4, "160", "Überschreitung der Pflichtstundenzahl aus organisatorischen Gründen (z. B. Epochenunterricht)", null, null)]);

	/**
	 * Mehrleistungsart 'Überschreitung der Pflichtstundenzahl wegen COVID-19'
	 */
	public static readonly ID_165 : LehrerMehrleistungArt = new LehrerMehrleistungArt("ID_165", 4, [new LehrerKatalogMehrleistungsartEintrag(6, "165", "Überschreitung der Pflichtstundenzahl wegen COVID-19", null, null)]);

	/**
	 * Mehrleistungsart 'Überschreitung der Pflichtstundenzahl wegen Pflichtstunden-Bandbreite'
	 */
	public static readonly ID_170 : LehrerMehrleistungArt = new LehrerMehrleistungArt("ID_170", 5, [new LehrerKatalogMehrleistungsartEintrag(5, "170", "Überschreitung der Pflichtstundenzahl wegen Pflichstunden-Bandbreite", null, null)]);

	/**
	 * Die Version dieses Core-Types, um beim Datenbank Update-Process die Version des Core-Types feststellen zu können.
	 */
	public static readonly VERSION : number = 1;

	/**
	 * Der aktuellen Daten der Art von Mehrleistung, wenn keine Beschränkung der Gültigkeit vorliegen - sonst null
	 */
	public readonly daten : LehrerKatalogMehrleistungsartEintrag;

	/**
	 * Die Historie mit den Einträgen der Art von Mehrleistung
	 */
	public readonly historie : Array<LehrerKatalogMehrleistungsartEintrag>;

	/**
	 * Eine Hashmap mit allen Arten von Mehrleistungen, welche ihrer ID zugeordnet sind.
	 */
	private static readonly _artenByID : HashMap<number, LehrerMehrleistungArt | null> = new HashMap();

	/**
	 * Eine Hashmap mit allen Arten von Mehrleistungen, welche dem Kürzel bzw. ASD-Schlüssel zugeordnet sind.
	 */
	private static readonly _artenByKuerzel : HashMap<string, LehrerMehrleistungArt | null> = new HashMap();

	/**
	 * Erzeugt eine neue Art von Mehrleistung in der Aufzählung.
	 *
	 * @param historie   die Historie der Art von Mehrleistung, welches ein Array von {@link LehrerKatalogMehrleistungsartEintrag} ist
	 */
	private constructor(name : string, ordinal : number, historie : Array<LehrerKatalogMehrleistungsartEintrag>) {
		super(name, ordinal);
		LehrerMehrleistungArt.all_values_by_ordinal.push(this);
		LehrerMehrleistungArt.all_values_by_name.set(name, this);
		this.historie = historie;
		this.daten = historie[historie.length - 1];
	}

	/**
	 * Gibt eine Map von den IDs der Mehrleistungsarten auf die zugehörigen Mehrleistungsarten
	 * zurück. Sollte diese noch nicht initialisiert sein, so wird sie initialisiert.
	 *
	 * @return die Map von den IDs der Mehrleistungsarten auf die zugehörigen Mehrleistungsarten
	 */
	private static getMapArtenByID() : HashMap<number, LehrerMehrleistungArt | null> {
		if (LehrerMehrleistungArt._artenByID.size() === 0)
			for (const g of LehrerMehrleistungArt.values())
				LehrerMehrleistungArt._artenByID.put(g.daten.id, g);
		return LehrerMehrleistungArt._artenByID;
	}

	/**
	 * Gibt eine Map von den Kürzeln der Mehrleistungsarten auf die zugehörigen Mehrleistungsarten
	 * zurück. Sollte diese noch nicht initialisiert sein, so wird sie initialisiert.
	 *
	 * @return die Map von den Kürzeln der Mehrleistungsarten auf die zugehörigen Mehrleistungsarten
	 */
	private static getMapArtenByKuerzel() : HashMap<string, LehrerMehrleistungArt | null> {
		if (LehrerMehrleistungArt._artenByKuerzel.size() === 0)
			for (const g of LehrerMehrleistungArt.values())
				LehrerMehrleistungArt._artenByKuerzel.put(g.daten.kuerzel, g);
		return LehrerMehrleistungArt._artenByKuerzel;
	}

	/**
	 * Gibt die Art der Mehrleistung anhand der angegebenen ID zurück.
	 *
	 * @param id   die ID der Art der Mehrleistung
	 *
	 * @return die Art der Mehrleistung oder null, falls die ID ungültig ist
	 */
	public static getByID(id : number) : LehrerMehrleistungArt | null {
		return LehrerMehrleistungArt.getMapArtenByID().get(id);
	}

	/**
	 * Gibt die Art der Mehrleistung anhand des angegebenen Kürzels zurück.
	 *
	 * @param kuerzel   das Kürzel der Art der Mehrleistung
	 *
	 * @return die Art der Mehrleistung oder null, falls das Kürzel ungültig ist
	 */
	public static getByKuerzel(kuerzel : string | null) : LehrerMehrleistungArt | null {
		return LehrerMehrleistungArt.getMapArtenByKuerzel().get(kuerzel);
	}

	/**
	 * Returns an array with enumeration values.
	 *
	 * @returns the array with enumeration values
	 */
	public static values() : Array<LehrerMehrleistungArt> {
		return [...this.all_values_by_ordinal];
	}

	/**
	 * Returns the enumeration value with the specified name.
	 *
	 * @param name   the name of the enumeration value
	 *
	 * @returns the enumeration values or null
	 */
	public static valueOf(name : string) : LehrerMehrleistungArt | null {
		const tmp = this.all_values_by_name.get(name);
		return (!tmp) ? null : tmp;
	}

	transpilerCanonicalName(): string {
		return 'de.svws_nrw.core.types.lehrer.LehrerMehrleistungArt';
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.svws_nrw.core.types.lehrer.LehrerMehrleistungArt', 'java.lang.Enum', 'java.lang.Comparable'].includes(name);
	}

}

export function cast_de_svws_nrw_core_types_lehrer_LehrerMehrleistungArt(obj : unknown) : LehrerMehrleistungArt {
	return obj as LehrerMehrleistungArt;
}
