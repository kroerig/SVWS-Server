import { JavaObject } from '../../../java/lang/JavaObject';
import { KAOAKategorie, cast_de_svws_nrw_core_types_kaoa_KAOAKategorie } from '../../../core/types/kaoa/KAOAKategorie';
import { NullPointerException } from '../../../java/lang/NullPointerException';
import { ArrayList } from '../../../java/util/ArrayList';
import { Schulgliederung } from '../../../core/types/schule/Schulgliederung';
import type { List } from '../../../java/util/List';
import { cast_java_util_List } from '../../../java/util/List';
import { KAOAMerkmaleOptionsarten, cast_de_svws_nrw_core_types_kaoa_KAOAMerkmaleOptionsarten } from '../../../core/types/kaoa/KAOAMerkmaleOptionsarten';

export class KAOAMerkmalEintrag extends JavaObject {

	/**
	 * Die ID des Katalog-Eintrags.
	 */
	public id : number = 0;

	/**
	 * Das Kürzel des Merkmals.
	 */
	public kuerzel : string = "";

	/**
	 * Die Beschreibung des Merkmals.
	 */
	public beschreibung : string = "";

	/**
	 * Die Kategorie, welcher das Merkmal zugeordnet ist.
	 */
	public kategorie : string = "";

	/**
	 * Die Optionsart des Merkmals.
	 */
	public optionsart : string | null = null;

	/**
	 * Die Anlagen des Berufskollegs bei denen der Eintrag gemacht werden darf
	 */
	public bkAnlagen : List<string> = new ArrayList<string>();

	/**
	 * Gibt an, in welchem Schuljahr der Eintrag einführt wurde. Ist kein Schuljahr bekannt, so ist null gesetzt.
	 */
	public gueltigVon : number | null = null;

	/**
	 * Gibt an, bis zu welchem Schuljahr der Eintrag gültig ist. Ist kein Schuljahr bekannt, so ist null gesetzt.
	 */
	public gueltigBis : number | null = null;


	/**
	 * Erstellt einen KAoA-Merkmal-Eintrag mit Standardwerten
	 */
	public constructor();

	/**
	 * Erstellt einen KAoA-Merkmal-Eintrag mit den angegebenen Werten
	 *
	 * @param id             die ID
	 * @param kuerzel        das Kürzel
	 * @param beschreibung   die Beschreibung
	 * @param kategorie      die Kategorie, dem das Merkmal zugeordnet ist
	 * @param optionsart     die Optionsart bei dem Merkmal
	 * @param bkAnlagen      die zulässigen Anlagen beim Berufskolleg
	 * @param gueltigVon     das Schuljahr, wann der Eintrag eingeführt wurde oder null, falls es nicht bekannt ist und "schon immer gültig war"
	 * @param gueltigBis     das Schuljahr, bis zu welchem der Eintrag gültig ist
	 */
	public constructor(id : number, kuerzel : string, beschreibung : string, kategorie : KAOAKategorie, optionsart : KAOAMerkmaleOptionsarten, bkAnlagen : List<Schulgliederung>, gueltigVon : number | null, gueltigBis : number | null);

	/**
	 * Implementation for method overloads of 'constructor'
	 */
	public constructor(__param0? : number, __param1? : string, __param2? : string, __param3? : KAOAKategorie, __param4? : KAOAMerkmaleOptionsarten, __param5? : List<Schulgliederung>, __param6? : null | number, __param7? : null | number) {
		super();
		if ((__param0 === undefined) && (__param1 === undefined) && (__param2 === undefined) && (__param3 === undefined) && (__param4 === undefined) && (__param5 === undefined) && (__param6 === undefined) && (__param7 === undefined)) {
			// empty method body
		} else if (((__param0 !== undefined) && typeof __param0 === "number") && ((__param1 !== undefined) && (typeof __param1 === "string")) && ((__param2 !== undefined) && (typeof __param2 === "string")) && ((__param3 !== undefined) && ((__param3 instanceof JavaObject) && ((__param3 as JavaObject).isTranspiledInstanceOf('de.svws_nrw.core.types.kaoa.KAOAKategorie')))) && ((__param4 !== undefined) && ((__param4 instanceof JavaObject) && ((__param4 as JavaObject).isTranspiledInstanceOf('de.svws_nrw.core.types.kaoa.KAOAMerkmaleOptionsarten')))) && ((__param5 !== undefined) && ((__param5 instanceof JavaObject) && ((__param5 as JavaObject).isTranspiledInstanceOf('java.util.List'))) || (__param5 === null)) && ((__param6 !== undefined) && (typeof __param6 === "number") || (__param6 === null)) && ((__param7 !== undefined) && (typeof __param7 === "number") || (__param7 === null))) {
			const id : number = __param0 as number;
			const kuerzel : string = __param1;
			const beschreibung : string = __param2;
			const kategorie : KAOAKategorie = cast_de_svws_nrw_core_types_kaoa_KAOAKategorie(__param3);
			const optionsart : KAOAMerkmaleOptionsarten = cast_de_svws_nrw_core_types_kaoa_KAOAMerkmaleOptionsarten(__param4);
			const bkAnlagen : List<Schulgliederung> = cast_java_util_List(__param5);
			const gueltigVon : number | null = __param6;
			const gueltigBis : number | null = __param7;
			this.id = id;
			this.kuerzel = kuerzel;
			this.beschreibung = beschreibung;
			this.kategorie = kategorie.daten.kuerzel;
			this.optionsart = optionsart.kuerzel;
			for (const gl of bkAnlagen) {
				if (gl.daten.bkAnlage === null)
					throw new NullPointerException("Es wurde keine Gliederung des Berufskollges als Anlage angegeben.")
				this.bkAnlagen.add(gl.daten.kuerzel);
			}
			this.gueltigVon = gueltigVon;
			this.gueltigBis = gueltigBis;
		} else throw new Error('invalid method overload');
	}

	transpilerCanonicalName(): string {
		return 'de.svws_nrw.core.data.kaoa.KAOAMerkmalEintrag';
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.svws_nrw.core.data.kaoa.KAOAMerkmalEintrag'].includes(name);
	}

	public static transpilerFromJSON(json : string): KAOAMerkmalEintrag {
		const obj = JSON.parse(json) as Partial<KAOAMerkmalEintrag>;
		const result = new KAOAMerkmalEintrag();
		if (obj.id === undefined)
			throw new Error('invalid json format, missing attribute id');
		result.id = obj.id;
		if (obj.kuerzel === undefined)
			throw new Error('invalid json format, missing attribute kuerzel');
		result.kuerzel = obj.kuerzel;
		if (obj.beschreibung === undefined)
			throw new Error('invalid json format, missing attribute beschreibung');
		result.beschreibung = obj.beschreibung;
		if (obj.kategorie === undefined)
			throw new Error('invalid json format, missing attribute kategorie');
		result.kategorie = obj.kategorie;
		result.optionsart = (obj.optionsart === undefined) ? null : obj.optionsart === null ? null : obj.optionsart;
		if (obj.bkAnlagen !== undefined) {
			for (const elem of obj.bkAnlagen) {
				result.bkAnlagen.add(elem);
			}
		}
		result.gueltigVon = (obj.gueltigVon === undefined) ? null : obj.gueltigVon === null ? null : obj.gueltigVon;
		result.gueltigBis = (obj.gueltigBis === undefined) ? null : obj.gueltigBis === null ? null : obj.gueltigBis;
		return result;
	}

	public static transpilerToJSON(obj : KAOAMerkmalEintrag) : string {
		let result = '{';
		result += '"id" : ' + obj.id.toString() + ',';
		result += '"kuerzel" : ' + JSON.stringify(obj.kuerzel) + ',';
		result += '"beschreibung" : ' + JSON.stringify(obj.beschreibung) + ',';
		result += '"kategorie" : ' + JSON.stringify(obj.kategorie) + ',';
		result += '"optionsart" : ' + ((!obj.optionsart) ? 'null' : JSON.stringify(obj.optionsart)) + ',';
		result += '"bkAnlagen" : [ ';
		for (let i = 0; i < obj.bkAnlagen.size(); i++) {
			const elem = obj.bkAnlagen.get(i);
			result += '"' + elem + '"';
			if (i < obj.bkAnlagen.size() - 1)
				result += ',';
		}
		result += ' ]' + ',';
		result += '"gueltigVon" : ' + ((!obj.gueltigVon) ? 'null' : obj.gueltigVon.toString()) + ',';
		result += '"gueltigBis" : ' + ((!obj.gueltigBis) ? 'null' : obj.gueltigBis.toString()) + ',';
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

	public static transpilerToJSONPatch(obj : Partial<KAOAMerkmalEintrag>) : string {
		let result = '{';
		if (obj.id !== undefined) {
			result += '"id" : ' + obj.id.toString() + ',';
		}
		if (obj.kuerzel !== undefined) {
			result += '"kuerzel" : ' + JSON.stringify(obj.kuerzel) + ',';
		}
		if (obj.beschreibung !== undefined) {
			result += '"beschreibung" : ' + JSON.stringify(obj.beschreibung) + ',';
		}
		if (obj.kategorie !== undefined) {
			result += '"kategorie" : ' + JSON.stringify(obj.kategorie) + ',';
		}
		if (obj.optionsart !== undefined) {
			result += '"optionsart" : ' + ((!obj.optionsart) ? 'null' : JSON.stringify(obj.optionsart)) + ',';
		}
		if (obj.bkAnlagen !== undefined) {
			result += '"bkAnlagen" : [ ';
			for (let i = 0; i < obj.bkAnlagen.size(); i++) {
				const elem = obj.bkAnlagen.get(i);
				result += '"' + elem + '"';
				if (i < obj.bkAnlagen.size() - 1)
					result += ',';
			}
			result += ' ]' + ',';
		}
		if (obj.gueltigVon !== undefined) {
			result += '"gueltigVon" : ' + ((!obj.gueltigVon) ? 'null' : obj.gueltigVon.toString()) + ',';
		}
		if (obj.gueltigBis !== undefined) {
			result += '"gueltigBis" : ' + ((!obj.gueltigBis) ? 'null' : obj.gueltigBis.toString()) + ',';
		}
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

}

export function cast_de_svws_nrw_core_data_kaoa_KAOAMerkmalEintrag(obj : unknown) : KAOAMerkmalEintrag {
	return obj as KAOAMerkmalEintrag;
}
