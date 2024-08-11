import { JavaObject } from '../../../java/lang/JavaObject';

export class ENMLehrer extends JavaObject {

	/**
	 * Die ID des Lehrers aus der SVWS-DB (z.B. 42)
	 */
	public id : number = 0;

	/**
	 * Das Kürzel des Lehrers für die Anzeige im Notenmodel (z.B. Mus)
	 */
	public kuerzel : string | null = null;

	/**
	 * Der Nachname des Lehrers (z.B. Mustermann)
	 */
	public nachname : string | null = null;

	/**
	 * Der Vorname des Lehrers (z.B. Max)
	 */
	public vorname : string | null = null;

	/**
	 * Das Geschlecht des Lehrers (m,w,d,x)
	 */
	public geschlecht : string | null = null;

	/**
	 * Die Dienst-EMail-Adresse des Lehrers
	 */
	public eMailDienstlich : string | null = null;


	public constructor() {
		super();
	}

	transpilerCanonicalName(): string {
		return 'de.svws_nrw.core.data.enm.ENMLehrer';
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.svws_nrw.core.data.enm.ENMLehrer'].includes(name);
	}

	public static transpilerFromJSON(json : string): ENMLehrer {
		const obj = JSON.parse(json) as Partial<ENMLehrer>;
		const result = new ENMLehrer();
		if (obj.id === undefined)
			throw new Error('invalid json format, missing attribute id');
		result.id = obj.id;
		result.kuerzel = (obj.kuerzel === undefined) ? null : obj.kuerzel === null ? null : obj.kuerzel;
		result.nachname = (obj.nachname === undefined) ? null : obj.nachname === null ? null : obj.nachname;
		result.vorname = (obj.vorname === undefined) ? null : obj.vorname === null ? null : obj.vorname;
		result.geschlecht = (obj.geschlecht === undefined) ? null : obj.geschlecht === null ? null : obj.geschlecht;
		result.eMailDienstlich = (obj.eMailDienstlich === undefined) ? null : obj.eMailDienstlich === null ? null : obj.eMailDienstlich;
		return result;
	}

	public static transpilerToJSON(obj : ENMLehrer) : string {
		let result = '{';
		result += '"id" : ' + obj.id.toString() + ',';
		result += '"kuerzel" : ' + ((!obj.kuerzel) ? 'null' : JSON.stringify(obj.kuerzel)) + ',';
		result += '"nachname" : ' + ((!obj.nachname) ? 'null' : JSON.stringify(obj.nachname)) + ',';
		result += '"vorname" : ' + ((!obj.vorname) ? 'null' : JSON.stringify(obj.vorname)) + ',';
		result += '"geschlecht" : ' + ((!obj.geschlecht) ? 'null' : JSON.stringify(obj.geschlecht)) + ',';
		result += '"eMailDienstlich" : ' + ((!obj.eMailDienstlich) ? 'null' : JSON.stringify(obj.eMailDienstlich)) + ',';
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

	public static transpilerToJSONPatch(obj : Partial<ENMLehrer>) : string {
		let result = '{';
		if (obj.id !== undefined) {
			result += '"id" : ' + obj.id.toString() + ',';
		}
		if (obj.kuerzel !== undefined) {
			result += '"kuerzel" : ' + ((!obj.kuerzel) ? 'null' : JSON.stringify(obj.kuerzel)) + ',';
		}
		if (obj.nachname !== undefined) {
			result += '"nachname" : ' + ((!obj.nachname) ? 'null' : JSON.stringify(obj.nachname)) + ',';
		}
		if (obj.vorname !== undefined) {
			result += '"vorname" : ' + ((!obj.vorname) ? 'null' : JSON.stringify(obj.vorname)) + ',';
		}
		if (obj.geschlecht !== undefined) {
			result += '"geschlecht" : ' + ((!obj.geschlecht) ? 'null' : JSON.stringify(obj.geschlecht)) + ',';
		}
		if (obj.eMailDienstlich !== undefined) {
			result += '"eMailDienstlich" : ' + ((!obj.eMailDienstlich) ? 'null' : JSON.stringify(obj.eMailDienstlich)) + ',';
		}
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

}

export function cast_de_svws_nrw_core_data_enm_ENMLehrer(obj : unknown) : ENMLehrer {
	return obj as ENMLehrer;
}
