import { JavaObject } from '../../../java/lang/JavaObject';
import { ArrayList } from '../../../java/util/ArrayList';
import type { List } from '../../../java/util/List';
import { SchuelerLernabschnittNachpruefung, cast_de_svws_nrw_core_data_schueler_SchuelerLernabschnittNachpruefung } from '../../../core/data/schueler/SchuelerLernabschnittNachpruefung';

export class SchuelerLernabschnittNachpruefungsdaten extends JavaObject {

	/**
	 * Die Kürzel der möglichen Nachprüfungsfächer.
	 */
	public moegliche : List<string> = new ArrayList();

	/**
	 * Die angesetzten bzw. durchgeführten Nachprüfungen
	 */
	public pruefungen : List<SchuelerLernabschnittNachpruefung> = new ArrayList();


	public constructor() {
		super();
	}

	transpilerCanonicalName(): string {
		return 'de.svws_nrw.core.data.schueler.SchuelerLernabschnittNachpruefungsdaten';
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.svws_nrw.core.data.schueler.SchuelerLernabschnittNachpruefungsdaten'].includes(name);
	}

	public static transpilerFromJSON(json : string): SchuelerLernabschnittNachpruefungsdaten {
		const obj = JSON.parse(json);
		const result = new SchuelerLernabschnittNachpruefungsdaten();
		if ((obj.moegliche !== undefined) && (obj.moegliche !== null)) {
			for (const elem of obj.moegliche) {
				result.moegliche?.add(elem);
			}
		}
		if ((obj.pruefungen !== undefined) && (obj.pruefungen !== null)) {
			for (const elem of obj.pruefungen) {
				result.pruefungen?.add(SchuelerLernabschnittNachpruefung.transpilerFromJSON(JSON.stringify(elem)));
			}
		}
		return result;
	}

	public static transpilerToJSON(obj : SchuelerLernabschnittNachpruefungsdaten) : string {
		let result = '{';
		if (!obj.moegliche) {
			result += '"moegliche" : []';
		} else {
			result += '"moegliche" : [ ';
			for (let i = 0; i < obj.moegliche.size(); i++) {
				const elem = obj.moegliche.get(i);
				result += '"' + elem + '"';
				if (i < obj.moegliche.size() - 1)
					result += ',';
			}
			result += ' ]' + ',';
		}
		if (!obj.pruefungen) {
			result += '"pruefungen" : []';
		} else {
			result += '"pruefungen" : [ ';
			for (let i = 0; i < obj.pruefungen.size(); i++) {
				const elem = obj.pruefungen.get(i);
				result += SchuelerLernabschnittNachpruefung.transpilerToJSON(elem);
				if (i < obj.pruefungen.size() - 1)
					result += ',';
			}
			result += ' ]' + ',';
		}
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

	public static transpilerToJSONPatch(obj : Partial<SchuelerLernabschnittNachpruefungsdaten>) : string {
		let result = '{';
		if (typeof obj.moegliche !== "undefined") {
			if (!obj.moegliche) {
				result += '"moegliche" : []';
			} else {
				result += '"moegliche" : [ ';
				for (let i = 0; i < obj.moegliche.size(); i++) {
					const elem = obj.moegliche.get(i);
					result += '"' + elem + '"';
					if (i < obj.moegliche.size() - 1)
						result += ',';
				}
				result += ' ]' + ',';
			}
		}
		if (typeof obj.pruefungen !== "undefined") {
			if (!obj.pruefungen) {
				result += '"pruefungen" : []';
			} else {
				result += '"pruefungen" : [ ';
				for (let i = 0; i < obj.pruefungen.size(); i++) {
					const elem = obj.pruefungen.get(i);
					result += SchuelerLernabschnittNachpruefung.transpilerToJSON(elem);
					if (i < obj.pruefungen.size() - 1)
						result += ',';
				}
				result += ' ]' + ',';
			}
		}
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

}

export function cast_de_svws_nrw_core_data_schueler_SchuelerLernabschnittNachpruefungsdaten(obj : unknown) : SchuelerLernabschnittNachpruefungsdaten {
	return obj as SchuelerLernabschnittNachpruefungsdaten;
}
