import { CoreTypeData } from '../../../core/data/CoreTypeData';

export class SchulformKatalogEintrag extends CoreTypeData {

	/**
	 * Das Kürzel der Schulform, welches im Rahmen der amtlichen Schulstatistik verwendet wird
	 */
	public kuerzel : string = "";

	/**
	 *  Die Nummer, welche im Rahmen der amtlichen Schulstatistik verwendet wird. Diese wird zwar
	 *  in der SVWS-DB bei der Schule gespeichert, aber dort aus dem Schulverzeichnis genommen.
	 *  Der Wert hier sollte i.A. nicht benötigt werden, da eine Unterscheidung anhand des Kürzels
	 *  stattfindet.
	 *  Stand 4.1.2021: Bei der "Hibernia"-Schulform und der Schulform "Schule für Kranke" ist der
	 *                  Wert der Nummer fehlerhaft, da dort eine Doppelung beim Kürzel vorliegt und
	 *                  diese somit nicht korrekt erfasst werden.
	 */
	public nummer : string = "";

	/**
	 * Die Bezeichnung der Schulform.
	 */
	public bezeichnung : string = "";

	/**
	 * Gibt an, ob es sich um eine Schulform für allgemeinbildende Schulen handelt oder nicht.
	 */
	public istAllgemeinbildend : boolean = false;

	/**
	 * Gibt an, ob es sich um eine Schulform für berufsbildende Schulen handelt oder nicht
	 */
	public istBerufsbildend : boolean = false;

	/**
	 * Gibt an, ob es sich um eine Schulform für Weiterbildung handelt oder nicht
	 */
	public istWeiterbildung : boolean = false;

	/**
	 * Gibt an, ob eine Schule der Schulform eine gymnasiale Oberstufe haben kann oder nicht.
	 */
	public hatGymOb : boolean = false;


	/**
	 * Erstellt einen Schulform-Eintrag mit Standardwerten
	 */
	public constructor();

	/**
	 * Erstellt einen Schulform-Eintrag mit den angegebenen Werten
	 *
	 * @param id           die ID
	 * @param kuerzel      das Kürzel
	 * @param nummer       die Nummer
	 * @param bezeichnung  die Bezeichnung
	 * @param istAllgemeinbildend   gibt an, ob es sich um eine Schulform für allgemeinbildende Schulen handelt oder nicht
	 * @param istBerufsbildend      gibt an, ob es sich um eine Schulform für berufsbildende Schulen handelt oder nicht
	 * @param istWeiterbildung      gibt an, ob es sich um eine Schulform für Weiterbildung handelt oder nicht
	 * @param hatGymOb     gibt an, ob die Schulform eien gymnasiale Oberstufe hat
	 * @param gueltigVon   das Schuljahr, wann der Eintrag eingeführt wurde oder null, falls es nicht bekannt ist und "schon immer gültig war"
	 * @param gueltigBis   das Schuljahr, bis zu welchem der Eintrag gültig ist
	 */
	public constructor(id : number, kuerzel : string, nummer : string, bezeichnung : string, istAllgemeinbildend : boolean, istBerufsbildend : boolean, istWeiterbildung : boolean, hatGymOb : boolean, gueltigVon : number | null, gueltigBis : number | null);

	/**
	 * Implementation for method overloads of 'constructor'
	 */
	public constructor(__param0? : number, __param1? : string, __param2? : string, __param3? : string, __param4? : boolean, __param5? : boolean, __param6? : boolean, __param7? : boolean, __param8? : null | number, __param9? : null | number) {
		super();
		if ((typeof __param0 === "undefined") && (typeof __param1 === "undefined") && (typeof __param2 === "undefined") && (typeof __param3 === "undefined") && (typeof __param4 === "undefined") && (typeof __param5 === "undefined") && (typeof __param6 === "undefined") && (typeof __param7 === "undefined") && (typeof __param8 === "undefined") && (typeof __param9 === "undefined")) {
			// empty method body
		} else if (((typeof __param0 !== "undefined") && typeof __param0 === "number") && ((typeof __param1 !== "undefined") && (typeof __param1 === "string")) && ((typeof __param2 !== "undefined") && (typeof __param2 === "string")) && ((typeof __param3 !== "undefined") && (typeof __param3 === "string")) && ((typeof __param4 !== "undefined") && typeof __param4 === "boolean") && ((typeof __param5 !== "undefined") && typeof __param5 === "boolean") && ((typeof __param6 !== "undefined") && typeof __param6 === "boolean") && ((typeof __param7 !== "undefined") && typeof __param7 === "boolean") && ((typeof __param8 !== "undefined") && (typeof __param8 === "number") || (__param8 === null)) && ((typeof __param9 !== "undefined") && (typeof __param9 === "number") || (__param9 === null))) {
			const id : number = __param0 as number;
			const kuerzel : string = __param1;
			const nummer : string = __param2;
			const bezeichnung : string = __param3;
			const istAllgemeinbildend : boolean = __param4 as boolean;
			const istBerufsbildend : boolean = __param5 as boolean;
			const istWeiterbildung : boolean = __param6 as boolean;
			const hatGymOb : boolean = __param7 as boolean;
			const gueltigVon : number | null = __param8;
			const gueltigBis : number | null = __param9;
			this.id = id;
			this.kuerzel = kuerzel;
			this.nummer = nummer;
			this.bezeichnung = bezeichnung;
			this.istAllgemeinbildend = istAllgemeinbildend;
			this.istBerufsbildend = istBerufsbildend;
			this.istWeiterbildung = istWeiterbildung;
			this.hatGymOb = hatGymOb;
			this.gueltigVon = gueltigVon;
			this.gueltigBis = gueltigBis;
		} else throw new Error('invalid method overload');
	}

	transpilerCanonicalName(): string {
		return 'de.svws_nrw.core.data.schule.SchulformKatalogEintrag';
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.svws_nrw.core.data.schule.SchulformKatalogEintrag', 'de.svws_nrw.core.data.CoreTypeData'].includes(name);
	}

	public static transpilerFromJSON(json : string): SchulformKatalogEintrag {
		const obj = JSON.parse(json);
		const result = new SchulformKatalogEintrag();
		if (typeof obj.id === "undefined")
			 throw new Error('invalid json format, missing attribute id');
		result.id = obj.id;
		result.gueltigVon = typeof obj.gueltigVon === "undefined" ? null : obj.gueltigVon === null ? null : obj.gueltigVon;
		result.gueltigBis = typeof obj.gueltigBis === "undefined" ? null : obj.gueltigBis === null ? null : obj.gueltigBis;
		if (typeof obj.kuerzel === "undefined")
			 throw new Error('invalid json format, missing attribute kuerzel');
		result.kuerzel = obj.kuerzel;
		if (typeof obj.nummer === "undefined")
			 throw new Error('invalid json format, missing attribute nummer');
		result.nummer = obj.nummer;
		if (typeof obj.bezeichnung === "undefined")
			 throw new Error('invalid json format, missing attribute bezeichnung');
		result.bezeichnung = obj.bezeichnung;
		if (typeof obj.istAllgemeinbildend === "undefined")
			 throw new Error('invalid json format, missing attribute istAllgemeinbildend');
		result.istAllgemeinbildend = obj.istAllgemeinbildend;
		if (typeof obj.istBerufsbildend === "undefined")
			 throw new Error('invalid json format, missing attribute istBerufsbildend');
		result.istBerufsbildend = obj.istBerufsbildend;
		if (typeof obj.istWeiterbildung === "undefined")
			 throw new Error('invalid json format, missing attribute istWeiterbildung');
		result.istWeiterbildung = obj.istWeiterbildung;
		if (typeof obj.hatGymOb === "undefined")
			 throw new Error('invalid json format, missing attribute hatGymOb');
		result.hatGymOb = obj.hatGymOb;
		return result;
	}

	public static transpilerToJSON(obj : SchulformKatalogEintrag) : string {
		let result = '{';
		result += '"id" : ' + obj.id + ',';
		result += '"gueltigVon" : ' + ((!obj.gueltigVon) ? 'null' : obj.gueltigVon) + ',';
		result += '"gueltigBis" : ' + ((!obj.gueltigBis) ? 'null' : obj.gueltigBis) + ',';
		result += '"kuerzel" : ' + JSON.stringify(obj.kuerzel!) + ',';
		result += '"nummer" : ' + JSON.stringify(obj.nummer!) + ',';
		result += '"bezeichnung" : ' + JSON.stringify(obj.bezeichnung!) + ',';
		result += '"istAllgemeinbildend" : ' + obj.istAllgemeinbildend + ',';
		result += '"istBerufsbildend" : ' + obj.istBerufsbildend + ',';
		result += '"istWeiterbildung" : ' + obj.istWeiterbildung + ',';
		result += '"hatGymOb" : ' + obj.hatGymOb + ',';
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

	public static transpilerToJSONPatch(obj : Partial<SchulformKatalogEintrag>) : string {
		let result = '{';
		if (typeof obj.id !== "undefined") {
			result += '"id" : ' + obj.id + ',';
		}
		if (typeof obj.gueltigVon !== "undefined") {
			result += '"gueltigVon" : ' + ((!obj.gueltigVon) ? 'null' : obj.gueltigVon) + ',';
		}
		if (typeof obj.gueltigBis !== "undefined") {
			result += '"gueltigBis" : ' + ((!obj.gueltigBis) ? 'null' : obj.gueltigBis) + ',';
		}
		if (typeof obj.kuerzel !== "undefined") {
			result += '"kuerzel" : ' + JSON.stringify(obj.kuerzel!) + ',';
		}
		if (typeof obj.nummer !== "undefined") {
			result += '"nummer" : ' + JSON.stringify(obj.nummer!) + ',';
		}
		if (typeof obj.bezeichnung !== "undefined") {
			result += '"bezeichnung" : ' + JSON.stringify(obj.bezeichnung!) + ',';
		}
		if (typeof obj.istAllgemeinbildend !== "undefined") {
			result += '"istAllgemeinbildend" : ' + obj.istAllgemeinbildend + ',';
		}
		if (typeof obj.istBerufsbildend !== "undefined") {
			result += '"istBerufsbildend" : ' + obj.istBerufsbildend + ',';
		}
		if (typeof obj.istWeiterbildung !== "undefined") {
			result += '"istWeiterbildung" : ' + obj.istWeiterbildung + ',';
		}
		if (typeof obj.hatGymOb !== "undefined") {
			result += '"hatGymOb" : ' + obj.hatGymOb + ',';
		}
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

}

export function cast_de_svws_nrw_core_data_schule_SchulformKatalogEintrag(obj : unknown) : SchulformKatalogEintrag {
	return obj as SchulformKatalogEintrag;
}
