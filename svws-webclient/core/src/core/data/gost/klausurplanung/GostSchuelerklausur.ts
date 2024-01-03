import { JavaObject } from '../../../../java/lang/JavaObject';
import { ArrayList } from '../../../../java/util/ArrayList';
import type { List } from '../../../../java/util/List';
import { GostSchuelerklausurTermin } from '../../../../core/data/gost/klausurplanung/GostSchuelerklausurTermin';

export class GostSchuelerklausur extends JavaObject {

	/**
	 * Die ID des Stundenplans.
	 */
	public id : number = -1;

	/**
	 * Die textuelle Beschreibung des Stundenplans.
	 */
	public idKursklausur : number = -1;

	/**
	 * Das Zeitraster des Stundenplans.
	 */
	public idSchueler : number = -1;

	/**
	 * Die Liste der IDs der zugehörigen Termine.
	 */
	public schuelerklausurTermine : List<GostSchuelerklausurTermin> = new ArrayList();

	/**
	 * Die textuelle Bemerkung zur Schülerklausur, sofern vorhanden.
	 */
	public bemerkung : string | null = null;


	public constructor() {
		super();
	}

	transpilerCanonicalName(): string {
		return 'de.svws_nrw.core.data.gost.klausurplanung.GostSchuelerklausur';
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.svws_nrw.core.data.gost.klausurplanung.GostSchuelerklausur'].includes(name);
	}

	public static transpilerFromJSON(json : string): GostSchuelerklausur {
		const obj = JSON.parse(json);
		const result = new GostSchuelerklausur();
		if (typeof obj.id === "undefined")
			 throw new Error('invalid json format, missing attribute id');
		result.id = obj.id;
		if (typeof obj.idKursklausur === "undefined")
			 throw new Error('invalid json format, missing attribute idKursklausur');
		result.idKursklausur = obj.idKursklausur;
		if (typeof obj.idSchueler === "undefined")
			 throw new Error('invalid json format, missing attribute idSchueler');
		result.idSchueler = obj.idSchueler;
		if ((obj.schuelerklausurTermine !== undefined) && (obj.schuelerklausurTermine !== null)) {
			for (const elem of obj.schuelerklausurTermine) {
				result.schuelerklausurTermine?.add(GostSchuelerklausurTermin.transpilerFromJSON(JSON.stringify(elem)));
			}
		}
		result.bemerkung = typeof obj.bemerkung === "undefined" ? null : obj.bemerkung === null ? null : obj.bemerkung;
		return result;
	}

	public static transpilerToJSON(obj : GostSchuelerklausur) : string {
		let result = '{';
		result += '"id" : ' + obj.id + ',';
		result += '"idKursklausur" : ' + obj.idKursklausur + ',';
		result += '"idSchueler" : ' + obj.idSchueler + ',';
		if (!obj.schuelerklausurTermine) {
			result += '"schuelerklausurTermine" : []';
		} else {
			result += '"schuelerklausurTermine" : [ ';
			for (let i = 0; i < obj.schuelerklausurTermine.size(); i++) {
				const elem = obj.schuelerklausurTermine.get(i);
				result += GostSchuelerklausurTermin.transpilerToJSON(elem);
				if (i < obj.schuelerklausurTermine.size() - 1)
					result += ',';
			}
			result += ' ]' + ',';
		}
		result += '"bemerkung" : ' + ((!obj.bemerkung) ? 'null' : JSON.stringify(obj.bemerkung)) + ',';
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

	public static transpilerToJSONPatch(obj : Partial<GostSchuelerklausur>) : string {
		let result = '{';
		if (typeof obj.id !== "undefined") {
			result += '"id" : ' + obj.id + ',';
		}
		if (typeof obj.idKursklausur !== "undefined") {
			result += '"idKursklausur" : ' + obj.idKursklausur + ',';
		}
		if (typeof obj.idSchueler !== "undefined") {
			result += '"idSchueler" : ' + obj.idSchueler + ',';
		}
		if (typeof obj.schuelerklausurTermine !== "undefined") {
			if (!obj.schuelerklausurTermine) {
				result += '"schuelerklausurTermine" : []';
			} else {
				result += '"schuelerklausurTermine" : [ ';
				for (let i = 0; i < obj.schuelerklausurTermine.size(); i++) {
					const elem = obj.schuelerklausurTermine.get(i);
					result += GostSchuelerklausurTermin.transpilerToJSON(elem);
					if (i < obj.schuelerklausurTermine.size() - 1)
						result += ',';
				}
				result += ' ]' + ',';
			}
		}
		if (typeof obj.bemerkung !== "undefined") {
			result += '"bemerkung" : ' + ((!obj.bemerkung) ? 'null' : JSON.stringify(obj.bemerkung)) + ',';
		}
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

}

export function cast_de_svws_nrw_core_data_gost_klausurplanung_GostSchuelerklausur(obj : unknown) : GostSchuelerklausur {
	return obj as GostSchuelerklausur;
}
