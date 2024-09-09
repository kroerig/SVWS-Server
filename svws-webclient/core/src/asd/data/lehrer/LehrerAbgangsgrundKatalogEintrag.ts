import { Class } from '../../../java/lang/Class';
import { CoreTypeData } from '../../../asd/data/CoreTypeData';

export class LehrerAbgangsgrundKatalogEintrag extends CoreTypeData {


	public constructor() {
		super();
	}

	transpilerCanonicalName(): string {
		return 'de.svws_nrw.asd.data.lehrer.LehrerAbgangsgrundKatalogEintrag';
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.svws_nrw.asd.data.lehrer.LehrerAbgangsgrundKatalogEintrag', 'de.svws_nrw.asd.data.CoreTypeData'].includes(name);
	}

	public static class = new Class<LehrerAbgangsgrundKatalogEintrag>('de.svws_nrw.asd.data.lehrer.LehrerAbgangsgrundKatalogEintrag');

	public static transpilerFromJSON(json : string): LehrerAbgangsgrundKatalogEintrag {
		const obj = JSON.parse(json) as Partial<LehrerAbgangsgrundKatalogEintrag>;
		const result = new LehrerAbgangsgrundKatalogEintrag();
		if (obj.id === undefined)
			throw new Error('invalid json format, missing attribute id');
		result.id = obj.id;
		if (obj.schluessel === undefined)
			throw new Error('invalid json format, missing attribute schluessel');
		result.schluessel = obj.schluessel;
		if (obj.kuerzel === undefined)
			throw new Error('invalid json format, missing attribute kuerzel');
		result.kuerzel = obj.kuerzel;
		if (obj.text === undefined)
			throw new Error('invalid json format, missing attribute text');
		result.text = obj.text;
		result.gueltigVon = (obj.gueltigVon === undefined) ? null : obj.gueltigVon === null ? null : obj.gueltigVon;
		result.gueltigBis = (obj.gueltigBis === undefined) ? null : obj.gueltigBis === null ? null : obj.gueltigBis;
		return result;
	}

	public static transpilerToJSON(obj : LehrerAbgangsgrundKatalogEintrag) : string {
		let result = '{';
		result += '"id" : ' + obj.id.toString() + ',';
		result += '"schluessel" : ' + JSON.stringify(obj.schluessel) + ',';
		result += '"kuerzel" : ' + JSON.stringify(obj.kuerzel) + ',';
		result += '"text" : ' + JSON.stringify(obj.text) + ',';
		result += '"gueltigVon" : ' + ((!obj.gueltigVon) ? 'null' : obj.gueltigVon.toString()) + ',';
		result += '"gueltigBis" : ' + ((!obj.gueltigBis) ? 'null' : obj.gueltigBis.toString()) + ',';
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

	public static transpilerToJSONPatch(obj : Partial<LehrerAbgangsgrundKatalogEintrag>) : string {
		let result = '{';
		if (obj.id !== undefined) {
			result += '"id" : ' + obj.id.toString() + ',';
		}
		if (obj.schluessel !== undefined) {
			result += '"schluessel" : ' + JSON.stringify(obj.schluessel) + ',';
		}
		if (obj.kuerzel !== undefined) {
			result += '"kuerzel" : ' + JSON.stringify(obj.kuerzel) + ',';
		}
		if (obj.text !== undefined) {
			result += '"text" : ' + JSON.stringify(obj.text) + ',';
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

export function cast_de_svws_nrw_asd_data_lehrer_LehrerAbgangsgrundKatalogEintrag(obj : unknown) : LehrerAbgangsgrundKatalogEintrag {
	return obj as LehrerAbgangsgrundKatalogEintrag;
}
