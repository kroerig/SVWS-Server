import { SchuldateiEintrag } from '../../../schulen/v1/data/SchuldateiEintrag';

export class SchuldateiOrganisationseinheitErreichbarkeit extends SchuldateiEintrag {

	/**
	 * Die ID des Erreichbarkeits-Eintrags.
	 */
	public id : number | null = null;

	/**
	 * Die Schulnummer.
	 */
	public schulnummer : number = 0;

	/**
	 * Die Nummer der Liegenschaft der Organisationseinheit
	 */
	public liegenschaft : number = 0;

	/**
	 * Kommgruppe des Eintrags
	 */
	public kommgruppe : number | null = null;

	/**
	 * codekey des Eintrags
	 */
	public codekey : string | null = null;

	/**
	 * codewert des Eintrags
	 */
	public codewert : string | null = null;


	/**
	 * Erstellt einen neuen Eintrag zur Erreichbarkeit einer Organisationseinheit der Schuldatei
	 */
	public constructor() {
		super();
	}

	transpilerCanonicalName(): string {
		return 'de.svws_nrw.schulen.v1.data.SchuldateiOrganisationseinheitErreichbarkeit';
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.svws_nrw.schulen.v1.data.SchuldateiEintrag', 'de.svws_nrw.schulen.v1.data.SchuldateiOrganisationseinheitErreichbarkeit'].includes(name);
	}

	public static transpilerFromJSON(json : string): SchuldateiOrganisationseinheitErreichbarkeit {
		const obj = JSON.parse(json) as Partial<SchuldateiOrganisationseinheitErreichbarkeit>;
		const result = new SchuldateiOrganisationseinheitErreichbarkeit();
		result.gueltigab = (obj.gueltigab === undefined) ? null : obj.gueltigab === null ? null : obj.gueltigab;
		result.gueltigbis = (obj.gueltigbis === undefined) ? null : obj.gueltigbis === null ? null : obj.gueltigbis;
		result.geaendertam = (obj.geaendertam === undefined) ? null : obj.geaendertam === null ? null : obj.geaendertam;
		result.id = (obj.id === undefined) ? null : obj.id === null ? null : obj.id;
		if (obj.schulnummer === undefined)
			throw new Error('invalid json format, missing attribute schulnummer');
		result.schulnummer = obj.schulnummer;
		if (obj.liegenschaft === undefined)
			throw new Error('invalid json format, missing attribute liegenschaft');
		result.liegenschaft = obj.liegenschaft;
		result.kommgruppe = (obj.kommgruppe === undefined) ? null : obj.kommgruppe === null ? null : obj.kommgruppe;
		result.codekey = (obj.codekey === undefined) ? null : obj.codekey === null ? null : obj.codekey;
		result.codewert = (obj.codewert === undefined) ? null : obj.codewert === null ? null : obj.codewert;
		return result;
	}

	public static transpilerToJSON(obj : SchuldateiOrganisationseinheitErreichbarkeit) : string {
		let result = '{';
		result += '"gueltigab" : ' + ((!obj.gueltigab) ? 'null' : JSON.stringify(obj.gueltigab)) + ',';
		result += '"gueltigbis" : ' + ((!obj.gueltigbis) ? 'null' : JSON.stringify(obj.gueltigbis)) + ',';
		result += '"geaendertam" : ' + ((!obj.geaendertam) ? 'null' : JSON.stringify(obj.geaendertam)) + ',';
		result += '"id" : ' + ((!obj.id) ? 'null' : obj.id.toString()) + ',';
		result += '"schulnummer" : ' + obj.schulnummer! + ',';
		result += '"liegenschaft" : ' + obj.liegenschaft! + ',';
		result += '"kommgruppe" : ' + ((!obj.kommgruppe) ? 'null' : obj.kommgruppe.toString()) + ',';
		result += '"codekey" : ' + ((!obj.codekey) ? 'null' : JSON.stringify(obj.codekey)) + ',';
		result += '"codewert" : ' + ((!obj.codewert) ? 'null' : JSON.stringify(obj.codewert)) + ',';
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

	public static transpilerToJSONPatch(obj : Partial<SchuldateiOrganisationseinheitErreichbarkeit>) : string {
		let result = '{';
		if (obj.gueltigab !== undefined) {
			result += '"gueltigab" : ' + ((!obj.gueltigab) ? 'null' : JSON.stringify(obj.gueltigab)) + ',';
		}
		if (obj.gueltigbis !== undefined) {
			result += '"gueltigbis" : ' + ((!obj.gueltigbis) ? 'null' : JSON.stringify(obj.gueltigbis)) + ',';
		}
		if (obj.geaendertam !== undefined) {
			result += '"geaendertam" : ' + ((!obj.geaendertam) ? 'null' : JSON.stringify(obj.geaendertam)) + ',';
		}
		if (obj.id !== undefined) {
			result += '"id" : ' + ((!obj.id) ? 'null' : obj.id.toString()) + ',';
		}
		if (obj.schulnummer !== undefined) {
			result += '"schulnummer" : ' + obj.schulnummer + ',';
		}
		if (obj.liegenschaft !== undefined) {
			result += '"liegenschaft" : ' + obj.liegenschaft + ',';
		}
		if (obj.kommgruppe !== undefined) {
			result += '"kommgruppe" : ' + ((!obj.kommgruppe) ? 'null' : obj.kommgruppe.toString()) + ',';
		}
		if (obj.codekey !== undefined) {
			result += '"codekey" : ' + ((!obj.codekey) ? 'null' : JSON.stringify(obj.codekey)) + ',';
		}
		if (obj.codewert !== undefined) {
			result += '"codewert" : ' + ((!obj.codewert) ? 'null' : JSON.stringify(obj.codewert)) + ',';
		}
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

}

export function cast_de_svws_nrw_schulen_v1_data_SchuldateiOrganisationseinheitErreichbarkeit(obj : unknown) : SchuldateiOrganisationseinheitErreichbarkeit {
	return obj as SchuldateiOrganisationseinheitErreichbarkeit;
}
