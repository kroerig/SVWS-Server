import { JavaObject } from '../../../java/lang/JavaObject';

export class ENMTeilleistungsart extends JavaObject {

	/**
	 * Die ID der Teilleistungsart (z.B. 42)
	 */
	public id : number = 0;

	/**
	 * Die Bezeichnung der Teilleistungsart (z.B. Somi-1)
	 */
	public bezeichnung : string | null = null;

	/**
	 * Sortierung der Teilleistungsarten (z.B. 12)
	 */
	public sortierung : number | null = null;

	/**
	 * Die Gewichtung der Teilleistungsart in Bezug auf die Leistungsdaten (z.B. 0,25)
	 */
	public gewichtung : number | null = null;


	public constructor() {
		super();
	}

	transpilerCanonicalName(): string {
		return 'de.svws_nrw.core.data.enm.ENMTeilleistungsart';
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.svws_nrw.core.data.enm.ENMTeilleistungsart'].includes(name);
	}

	public static transpilerFromJSON(json : string): ENMTeilleistungsart {
		const obj = JSON.parse(json);
		const result = new ENMTeilleistungsart();
		if (typeof obj.id === "undefined")
			 throw new Error('invalid json format, missing attribute id');
		result.id = obj.id;
		result.bezeichnung = typeof obj.bezeichnung === "undefined" ? null : obj.bezeichnung === null ? null : obj.bezeichnung;
		result.sortierung = typeof obj.sortierung === "undefined" ? null : obj.sortierung === null ? null : obj.sortierung;
		result.gewichtung = typeof obj.gewichtung === "undefined" ? null : obj.gewichtung === null ? null : obj.gewichtung;
		return result;
	}

	public static transpilerToJSON(obj : ENMTeilleistungsart) : string {
		let result = '{';
		result += '"id" : ' + obj.id + ',';
		result += '"bezeichnung" : ' + ((!obj.bezeichnung) ? 'null' : JSON.stringify(obj.bezeichnung)) + ',';
		result += '"sortierung" : ' + ((!obj.sortierung) ? 'null' : obj.sortierung) + ',';
		result += '"gewichtung" : ' + ((!obj.gewichtung) ? 'null' : obj.gewichtung) + ',';
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

	public static transpilerToJSONPatch(obj : Partial<ENMTeilleistungsart>) : string {
		let result = '{';
		if (typeof obj.id !== "undefined") {
			result += '"id" : ' + obj.id + ',';
		}
		if (typeof obj.bezeichnung !== "undefined") {
			result += '"bezeichnung" : ' + ((!obj.bezeichnung) ? 'null' : JSON.stringify(obj.bezeichnung)) + ',';
		}
		if (typeof obj.sortierung !== "undefined") {
			result += '"sortierung" : ' + ((!obj.sortierung) ? 'null' : obj.sortierung) + ',';
		}
		if (typeof obj.gewichtung !== "undefined") {
			result += '"gewichtung" : ' + ((!obj.gewichtung) ? 'null' : obj.gewichtung) + ',';
		}
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

}

export function cast_de_svws_nrw_core_data_enm_ENMTeilleistungsart(obj : unknown) : ENMTeilleistungsart {
	return obj as ENMTeilleistungsart;
}
