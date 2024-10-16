import { JavaEnum } from '../../../java/lang/JavaEnum';
import { HashMap } from '../../../java/util/HashMap';
import { Schulform } from '../../../core/types/schule/Schulform';
import { OrganisationsformKatalogEintrag } from '../../../core/data/schule/OrganisationsformKatalogEintrag';
import { Arrays } from '../../../java/util/Arrays';

export class BerufskollegOrganisationsformen extends JavaEnum<BerufskollegOrganisationsformen> {

	/** an array containing all values of this enumeration */
	static readonly all_values_by_ordinal : Array<BerufskollegOrganisationsformen> = [];

	/** an array containing all values of this enumeration indexed by their name*/
	static readonly all_values_by_name : Map<string, BerufskollegOrganisationsformen> = new Map<string, BerufskollegOrganisationsformen>();

	/**
	 * Organisationsform: Teilzeitunterricht (außerhalb der TZ-Berufsschule)
	 */
	public static readonly TEILZEIT : BerufskollegOrganisationsformen = new BerufskollegOrganisationsformen("TEILZEIT", 0, [new OrganisationsformKatalogEintrag(1001000, "T", "Teilzeitunterricht (außerhalb der TZ-Berufsschule)", Arrays.asList(Schulform.BK, Schulform.SB), null, null)]);

	/**
	 * Organisationsform: Teilzeitunterricht ohne Blockunterricht (Normalklasse) TZ-Berufsschule
	 */
	public static readonly TEILZEIT_NORMALKLASSE : BerufskollegOrganisationsformen = new BerufskollegOrganisationsformen("TEILZEIT_NORMALKLASSE", 1, [new OrganisationsformKatalogEintrag(1002000, "A", "Teilzeitunterricht ohne Blockunterricht (Normalklasse) TZ-Berufsschule", Arrays.asList(Schulform.BK, Schulform.SB), null, null)]);

	/**
	 * Organisationsform: Teilzeitunterricht ohne Blockunterricht (Bezirksfachklasse) TZ-Berufsschule
	 */
	public static readonly TEILZEIT_BEZIRKSFACHKLASSE : BerufskollegOrganisationsformen = new BerufskollegOrganisationsformen("TEILZEIT_BEZIRKSFACHKLASSE", 2, [new OrganisationsformKatalogEintrag(1003000, "C", "Teilzeitunterricht ohne Blockunterricht (Bezirksfachklasse) TZ-Berufsschule", Arrays.asList(Schulform.BK, Schulform.SB), null, null)]);

	/**
	 * Organisationsform: Teilzeitunterricht ohne Blockunterricht (Landesfachklasse) TZ-Berufsschule
	 */
	public static readonly TEILZEIT_LANDESFACHKLASSE : BerufskollegOrganisationsformen = new BerufskollegOrganisationsformen("TEILZEIT_LANDESFACHKLASSE", 3, [new OrganisationsformKatalogEintrag(1004000, "I", "Teilzeitunterricht ohne Blockunterricht (Landesfachklasse) TZ-Berufsschule", Arrays.asList(Schulform.BK, Schulform.SB), null, null)]);

	/**
	 * Organisationsform: Vollzeitunterricht
	 */
	public static readonly VOLLZEIT : BerufskollegOrganisationsformen = new BerufskollegOrganisationsformen("VOLLZEIT", 4, [new OrganisationsformKatalogEintrag(1005000, "V", "Vollzeitunterricht", Arrays.asList(Schulform.BK, Schulform.SB), null, null)]);

	/**
	 * Organisationsform: Blockunterricht z.Zt. im Unterricht (Normalklasse) im dualen System
	 */
	public static readonly DUAL_BLOCK_IM_UNTERRICHT : BerufskollegOrganisationsformen = new BerufskollegOrganisationsformen("DUAL_BLOCK_IM_UNTERRICHT", 5, [new OrganisationsformKatalogEintrag(1006000, "B", "Blockunterricht z.Zt. im Unterricht (Normalklasse) im dualen System", Arrays.asList(Schulform.BK, Schulform.SB), null, null)]);

	/**
	 * Organisationsform: Blockunterricht z.Zt. nicht im Unterricht (Normalklasse) im dualen System
	 */
	public static readonly DUAL_BLOCK_NICHT_IM_UNTERRICHT : BerufskollegOrganisationsformen = new BerufskollegOrganisationsformen("DUAL_BLOCK_NICHT_IM_UNTERRICHT", 6, [new OrganisationsformKatalogEintrag(1007000, "E", "Blockunterricht z.Zt. nicht im Unterricht (Normalklasse) im dualen System", Arrays.asList(Schulform.BK, Schulform.SB), null, null)]);

	/**
	 * Organisationsform: Block- und Teilzeitunterr. z.Zt. im Block- oder TZ-Unterricht (Normalklasse) im dualen System
	 */
	public static readonly DUAL_BLOCK_TEILZEIT_IM_UNTERRICHT : BerufskollegOrganisationsformen = new BerufskollegOrganisationsformen("DUAL_BLOCK_TEILZEIT_IM_UNTERRICHT", 7, [new OrganisationsformKatalogEintrag(1008000, "G", "Block- und Teilzeitunterr. z.Zt. im Block- oder TZ-Unterricht (Normalklasse) im dualen System", Arrays.asList(Schulform.BK, Schulform.SB), null, null)]);

	/**
	 * Organisationsform: Ganztagsunterricht (Normalklasse) im dualen System
	 */
	public static readonly DUAL_GANZTAG : BerufskollegOrganisationsformen = new BerufskollegOrganisationsformen("DUAL_GANZTAG", 8, [new OrganisationsformKatalogEintrag(1000, "K", "Ganztagsunterricht (Normalklasse) im dualen System", Arrays.asList(Schulform.BK, Schulform.SB), null, null)]);

	/**
	 * Organisationsform: Blockunterricht z.Zt. im Unterricht (Bezirksfachklasse) im dualen System
	 */
	public static readonly DUAL_BLOCK_IM_UNTERRICHT_BEZIRKSFACHKLASSE : BerufskollegOrganisationsformen = new BerufskollegOrganisationsformen("DUAL_BLOCK_IM_UNTERRICHT_BEZIRKSFACHKLASSE", 9, [new OrganisationsformKatalogEintrag(1009000, "D", "Blockunterricht z.Zt. im Unterricht (Bezirksfachklasse) im dualen System", Arrays.asList(Schulform.BK, Schulform.SB), null, null)]);

	/**
	 * Organisationsform: Blockunterricht z.Zt. nicht im Unterricht (Bezirksfachklasse) im dualen System
	 */
	public static readonly DUAL_BLOCK_NICHT_IM_UNTERRICHT_BEZIRKSFACHKLASSE : BerufskollegOrganisationsformen = new BerufskollegOrganisationsformen("DUAL_BLOCK_NICHT_IM_UNTERRICHT_BEZIRKSFACHKLASSE", 10, [new OrganisationsformKatalogEintrag(1010000, "F", "Blockunterricht z.Zt. nicht im Unterricht (Bezirksfachklasse) im dualen System", Arrays.asList(Schulform.BK, Schulform.SB), null, null)]);

	/**
	 * Organisationsform: Block- und Teilzeitunterr. z.Zt. im Block- oder TZ-Unterricht (Bezirksfachklasse) im dualen System
	 */
	public static readonly DUAL_BLOCK_TEILZEIT_IM_UNTERRICHT_BEZIRKSFACHKLASSE : BerufskollegOrganisationsformen = new BerufskollegOrganisationsformen("DUAL_BLOCK_TEILZEIT_IM_UNTERRICHT_BEZIRKSFACHKLASSE", 11, [new OrganisationsformKatalogEintrag(1011000, "H", "Block- und Teilzeitunterr. z.Zt. im Block- oder TZ-Unterricht (Bezirksfachklasse) im dualen System", Arrays.asList(Schulform.BK, Schulform.SB), null, null)]);

	/**
	 * Organisationsform: Ganztagsunterricht (Bezirksfachklasse) im dualen System
	 */
	public static readonly DUAL_GANZTAG_BEZIRKSFACHKLASSE : BerufskollegOrganisationsformen = new BerufskollegOrganisationsformen("DUAL_GANZTAG_BEZIRKSFACHKLASSE", 12, [new OrganisationsformKatalogEintrag(1012000, "L", "Ganztagsunterricht (Bezirksfachklasse) im dualen System", Arrays.asList(Schulform.BK, Schulform.SB), null, null)]);

	/**
	 * Organisationsform: Blockunterricht z.Zt. im Unterricht (Landesfachklasse) im dualen System
	 */
	public static readonly DUAL_BLOCK_IM_UNTERRICHT_LANDESFACHKLASSE : BerufskollegOrganisationsformen = new BerufskollegOrganisationsformen("DUAL_BLOCK_IM_UNTERRICHT_LANDESFACHKLASSE", 13, [new OrganisationsformKatalogEintrag(1013000, "O", "Blockunterricht z.Zt. im Unterricht (Landesfachklasse) im dualen System", Arrays.asList(Schulform.BK, Schulform.SB), null, null)]);

	/**
	 * Organisationsform: Blockunterricht z.Zt. nicht im Unterricht (Landesfachklasse) im dualen System
	 */
	public static readonly DUAL_BLOCK_NICHT_IM_UNTERRICHT_LANDESFACHKLASSE : BerufskollegOrganisationsformen = new BerufskollegOrganisationsformen("DUAL_BLOCK_NICHT_IM_UNTERRICHT_LANDESFACHKLASSE", 14, [new OrganisationsformKatalogEintrag(1014000, "P", "Blockunterricht z.Zt. nicht im Unterricht (Landesfachklasse) im dualen System", Arrays.asList(Schulform.BK, Schulform.SB), null, null)]);

	/**
	 * Organisationsform: Block- und Teilzeitunterr. z.Zt. im Block- oder TZ-Unterricht (Landesfachklasse) im dualen System
	 */
	public static readonly DUAL_BLOCK_TEILZEIT_IM_UNTERRICHT_LANDESFACHKLASSE : BerufskollegOrganisationsformen = new BerufskollegOrganisationsformen("DUAL_BLOCK_TEILZEIT_IM_UNTERRICHT_LANDESFACHKLASSE", 15, [new OrganisationsformKatalogEintrag(1015000, "U", "Block- und Teilzeitunterr. z.Zt. im Block- oder TZ-Unterricht (Landesfachklasse) im dualen System", Arrays.asList(Schulform.BK, Schulform.SB), null, null)]);

	/**
	 * Organisationsform: Ganztagsunterricht (Landesfachklasse) im dualen System
	 */
	public static readonly DUAL_GANZTAG_LANDESFACHKLASSE : BerufskollegOrganisationsformen = new BerufskollegOrganisationsformen("DUAL_GANZTAG_LANDESFACHKLASSE", 16, [new OrganisationsformKatalogEintrag(1016000, "M", "Ganztagsunterricht (Landesfachklasse) im dualen System", Arrays.asList(Schulform.BK, Schulform.SB), null, null)]);

	/**
	 * Die Version dieses Core-Types, um beim Datenbank Update-Process die Version des Core-Types feststellen zu können.
	 */
	public static readonly VERSION : number = 1;

	/**
	 * Der aktuellen Daten der Organisationsform
	 */
	public readonly daten : OrganisationsformKatalogEintrag;

	/**
	 * Die Historie mit den Einträgen der Organisationsform
	 */
	public readonly historie : Array<OrganisationsformKatalogEintrag>;

	/**
	 * Eine Hashmap mit allen definierten Organisationsformen, zugeordnet zu ihren IDs
	 */
	private static readonly _mapByID : HashMap<number, BerufskollegOrganisationsformen> = new HashMap();

	/**
	 * Eine Hashmap mit allen definierten Organisationsformen, zugeordnet zu ihren Kürzeln
	 */
	private static readonly _mapKuerzel : HashMap<string, BerufskollegOrganisationsformen> = new HashMap();

	/**
	 * Erzeugt eine neue Organisationsform in der Aufzählung.
	 *
	 * @param historie   die Historie der Organisationsform, welche ein Array von
	 *                   {@link OrganisationsformKatalogEintrag} ist
	 */
	private constructor(name : string, ordinal : number, historie : Array<OrganisationsformKatalogEintrag>) {
		super(name, ordinal);
		BerufskollegOrganisationsformen.all_values_by_ordinal.push(this);
		BerufskollegOrganisationsformen.all_values_by_name.set(name, this);
		this.historie = historie;
		this.daten = historie[historie.length - 1];
	}

	/**
	 * Gibt eine Map von den IDs der Organisationsformen auf die
	 * zugehörigen Organisationsformen zurück.
	 * Sollte diese noch nicht initialisiert sein, so wird sie initialisiert.
	 *
	 * @return die Map von den IDs auf die zugehörigen Organisationsformen
	 */
	private static getMapByID() : HashMap<number, BerufskollegOrganisationsformen> {
		if (BerufskollegOrganisationsformen._mapByID.size() === 0)
			for (const s of BerufskollegOrganisationsformen.values())
				for (const k of s.historie)
					BerufskollegOrganisationsformen._mapByID.put(k.id, s);
		return BerufskollegOrganisationsformen._mapByID;
	}

	/**
	 * Gibt eine Map von den Kürzeln der Organisationsformen auf die
	 * zugehörigen Organisationsformen zurück.
	 * Sollte diese noch nicht initialisiert sein, so wird sie initialisiert.
	 *
	 * @return die Map von den Kürzeln auf die zugehörigen Organisationsformen
	 */
	private static getMapByKuerzel() : HashMap<string, BerufskollegOrganisationsformen> {
		if (BerufskollegOrganisationsformen._mapKuerzel.size() === 0) {
			for (const s of BerufskollegOrganisationsformen.values()) {
				if (s.daten !== null)
					BerufskollegOrganisationsformen._mapKuerzel.put(s.daten.kuerzel, s);
			}
		}
		return BerufskollegOrganisationsformen._mapKuerzel;
	}

	/**
	 * Gibt die Organisationsform für die angegebene ID zurück.
	 *
	 * @param id   die ID der Organisationsform
	 *
	 * @return die Organisationsform oder null, falls die ID ungültig ist
	 */
	public static getByID(id : number | null) : BerufskollegOrganisationsformen | null {
		if (id === null)
			return null;
		return BerufskollegOrganisationsformen.getMapByID().get(id);
	}

	/**
	 * Gibt die Organisationsform für das angegebene Kürzel zurück.
	 *
	 * @param kuerzel   das Kürzel der Organisationsform
	 *
	 * @return die Organisationsform oder null, falls das Kürzel ungültig ist
	 */
	public static getByKuerzel(kuerzel : string | null) : BerufskollegOrganisationsformen | null {
		return BerufskollegOrganisationsformen.getMapByKuerzel().get(kuerzel);
	}

	/**
	 * Returns an array with enumeration values.
	 *
	 * @returns the array with enumeration values
	 */
	public static values() : Array<BerufskollegOrganisationsformen> {
		return [...this.all_values_by_ordinal];
	}

	/**
	 * Returns the enumeration value with the specified name.
	 *
	 * @param name   the name of the enumeration value
	 *
	 * @returns the enumeration values or null
	 */
	public static valueOf(name : string) : BerufskollegOrganisationsformen | null {
		const tmp = this.all_values_by_name.get(name);
		return (!tmp) ? null : tmp;
	}

	transpilerCanonicalName(): string {
		return 'de.svws_nrw.core.types.schule.BerufskollegOrganisationsformen';
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.svws_nrw.core.types.schule.BerufskollegOrganisationsformen', 'java.lang.Enum', 'java.lang.Comparable'].includes(name);
	}

}

export function cast_de_svws_nrw_core_types_schule_BerufskollegOrganisationsformen(obj : unknown) : BerufskollegOrganisationsformen {
	return obj as BerufskollegOrganisationsformen;
}
