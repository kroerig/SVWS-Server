import type { List } from "@core";
import {
	HashMap,
	BaseApi,
	DeveloperNotificationException,
	HashSet,
	ArrayList,
	SchulformKatalogEintrag,
	CoreTypeDataManager,
	Schulform,
	BerufskollegAnlageKatalogEintrag,
	BerufskollegAnlage,
	OrganisationsformKatalogEintrag,
	AllgemeinbildendOrganisationsformen,
	BerufskollegOrganisationsformen,
	WeiterbildungskollegOrganisationsformen,
	SchulabschlussAllgemeinbildendKatalogEintrag,
	SchulabschlussAllgemeinbildend,
	SchulabschlussBerufsbildendKatalogEintrag,
	SchulabschlussBerufsbildend,
	HerkunftBildungsgangKatalogEintrag,
	HerkunftBildungsgang,
	HerkunftBildungsgangTypKatalogEintrag,
	HerkunftBildungsgangTyp,
	JahrgaengeKatalogEintrag,
	Jahrgaenge,
	PrimarstufeSchuleingangsphaseBesuchsjahreKatalogEintrag,
	PrimarstufeSchuleingangsphaseBesuchsjahre,
	ReligionKatalogEintrag,
	Religion,
	KindergartenbesuchKatalogEintrag,
	Kindergartenbesuch,
	SchuelerStatusKatalogEintrag,
	SchuelerStatus,
	NoteKatalogEintrag,
	Note,
	SprachreferenzniveauKatalogEintrag,
	Sprachreferenzniveau,
	BildungsgangTypKatalogEintrag,
	BerufskollegBildungsgangTyp,
	WeiterbildungskollegBildungsgangTyp,
	SchulgliederungKatalogEintrag,
	Schulgliederung,
	FachgruppeKatalogEintrag,
	Fachgruppe,
	FachKatalogEintrag,
	Fach,
	LehrerAbgangsgrundKatalogEintrag,
	LehrerAbgangsgrund,
	LehrerBeschaeftigungsartKatalogEintrag,
	LehrerBeschaeftigungsart,
	LehrerEinsatzstatusKatalogEintrag,
	LehrerEinsatzstatus,
	LehrerFachrichtungKatalogEintrag,
	LehrerFachrichtung,
	LehrerLehrbefaehigungKatalogEintrag,
	LehrerLehrbefaehigung,
	LehrerFachrichtungAnerkennungKatalogEintrag,
	LehrerFachrichtungAnerkennung,
	LehrerLehramtKatalogEintrag,
	LehrerLehramt,
	LehrerLehramtAnerkennungKatalogEintrag,
	LehrerLehramtAnerkennung,
	LehrerLehrbefaehigungAnerkennungKatalogEintrag,
	LehrerLehrbefaehigungAnerkennung,
	LehrerLeitungsfunktionKatalogEintrag,
	LehrerLeitungsfunktion,
	LehrerRechtsverhaeltnisKatalogEintrag,
	LehrerRechtsverhaeltnis,
	LehrerZugangsgrundKatalogEintrag,
	LehrerZugangsgrund,
	BilingualeSpracheKatalogEintrag,
	BilingualeSprache,
	KAOABerufsfeldKatalogEintrag,
	KAOABerufsfeld,
	KAOAMerkmaleOptionsartenKatalogEintrag,
	KAOAMerkmaleOptionsarten,
	KAOAZusatzmerkmaleOptionsartenKatalogEintrag,
	KAOAZusatzmerkmaleOptionsarten,
	KAOAEbene4KatalogEintrag,
	KAOAEbene4,
	KAOAZusatzmerkmalKatalogEintrag,
	KAOAZusatzmerkmal,
	KAOAAnschlussoptionenKatalogEintrag,
	KAOAAnschlussoptionen,
	KAOAKategorieKatalogEintrag,
	KAOAKategorie,
	KAOAMerkmalKatalogEintrag,
	KAOAMerkmal,
	KlassenartKatalogEintrag,
	Klassenart,
	UebergangsempfehlungKatalogEintrag,
	Uebergangsempfehlung,
	ZulaessigeKursartKatalogEintrag,
	ZulaessigeKursart,
	FoerderschwerpunktKatalogEintrag,
	Foerderschwerpunkt,
	LehrerAnrechnungsgrundKatalogEintrag,
	LehrerAnrechnungsgrund,
	LehrerMehrleistungsartKatalogEintrag,
	LehrerMehrleistungsarten,
	LehrerMinderleistungsartKatalogEintrag,
	LehrerMinderleistungsarten,
	ValidatorFehlerartKontext,
	ValidatorManager,
	CoreTypeSimple,
} from "@core";
import assert from "assert";

import * as fs from 'fs';
import path from "path";

interface JsonCoreTypeEntry<T> {
	bezeichner: string;
	idHistorie: number;
	historie: T[];
}

interface JsonCoreTypeData<T> {
	version: number;
	daten: JsonCoreTypeEntry<T>[];
}

interface JsonCoreTypeDataResult<T> {
	version: number;
	mapData: HashMap<string, List<T>>;
	mapHistorienIDs: HashMap<string, number>;
}


export class JsonCoreTypeReader {

	private readonly api: BaseApi;

	private readonly pathPrefix : string = "./../../../../svws-asd/src/main/resources/de/svws_nrw/asd";

	public constructor(url: string) {
		this.api = new BaseApi(url, "", "");
		this.fillPathMap();
	}

	mapCoreTypNameJsonData = new HashMap<string, string>();
	mapCoreTypNameJsonPath = new HashMap<string, string>();

	private buildPath(suffixPathToJason : string) : string {
		const pathToJson = path.resolve(__dirname, this.pathPrefix + suffixPathToJason);

		// teste ob der Pfad legal ist
		if (!fs.existsSync(pathToJson)){
			throw new DeveloperNotificationException(`Illegaler Pfad für Core Type: "${pathToJson}"`);
		};
		return pathToJson
	}

	private fillPathMap() {
		this.mapCoreTypNameJsonPath.put("Schulform", this.buildPath("/types/schule/Schulform.json"));
		this.mapCoreTypNameJsonPath.put("BerufskollegAnlage", this.buildPath("/types/schule/BerufskollegAnlage.json"));
		this.mapCoreTypNameJsonPath.put("AllgemeinbildendOrganisationsformen", this.buildPath("/types/schule/AllgemeinbildendOrganisationsformen.json"));
		this.mapCoreTypNameJsonPath.put("BerufskollegOrganisationsformen", this.buildPath("/types/schule/BerufskollegOrganisationsformen.json"));
		this.mapCoreTypNameJsonPath.put("WeiterbildungskollegOrganisationsformen", this.buildPath("/types/schule/WeiterbildungskollegOrganisationsformen.json"));
		this.mapCoreTypNameJsonPath.put("SchulabschlussAllgemeinbildend", this.buildPath("/types/schule/SchulabschlussAllgemeinbildend.json"));
		this.mapCoreTypNameJsonPath.put("SchulabschlussBerufsbildend", this.buildPath("/types/schule/SchulabschlussBerufsbildend.json"));
		this.mapCoreTypNameJsonPath.put("HerkunftBildungsgang", this.buildPath("/types/schueler/HerkunftBildungsgang.json"));
		this.mapCoreTypNameJsonPath.put("HerkunftBildungsgangTyp", this.buildPath("/types/schueler/HerkunftBildungsgangTyp.json"));
		this.mapCoreTypNameJsonPath.put("Jahrgaenge", this.buildPath("/types/jahrgang/Jahrgaenge.json"));
		this.mapCoreTypNameJsonPath.put("PrimarstufeSchuleingangsphaseBesuchsjahre", this.buildPath("/types/jahrgang/PrimarstufeSchuleingangsphaseBesuchsjahre.json"));
		this.mapCoreTypNameJsonPath.put("Religion", this.buildPath("/types/schule/Religion.json"));
		this.mapCoreTypNameJsonPath.put("Kindergartenbesuch", this.buildPath("/types/schule/Kindergartenbesuch.json"));
		this.mapCoreTypNameJsonPath.put("SchuelerStatus", this.buildPath("/types/schueler/SchuelerStatus.json"));
		this.mapCoreTypNameJsonPath.put("Note", this.buildPath("/types/Note.json"));
		this.mapCoreTypNameJsonPath.put("Sprachreferenzniveau", this.buildPath("/types/fach/Sprachreferenzniveau.json"));
		this.mapCoreTypNameJsonPath.put("BerufskollegBildungsgangTyp", this.buildPath("/types/schule/BerufskollegBildungsgangTyp.json"));
		this.mapCoreTypNameJsonPath.put("WeiterbildungskollegBildungsgangTyp", this.buildPath("/types/schule/WeiterbildungskollegBildungsgangTyp.json"));
		this.mapCoreTypNameJsonPath.put("Schulgliederung", this.buildPath("/types/schule/Schulgliederung.json"));
		this.mapCoreTypNameJsonPath.put("Fachgruppe", this.buildPath("/types/fach/Fachgruppe.json"));
		this.mapCoreTypNameJsonPath.put("Fach", this.buildPath("/types/fach/Fach.json"));
		this.mapCoreTypNameJsonPath.put("LehrerAbgangsgrund", this.buildPath("/types/Lehrer/LehrerAbgangsgrund.json"));
		this.mapCoreTypNameJsonPath.put("LehrerBeschaeftigungsart", this.buildPath("/types/Lehrer/LehrerBeschaeftigungsart.json"));
		this.mapCoreTypNameJsonPath.put("LehrerEinsatzstatus", this.buildPath("/types/Lehrer/LehrerEinsatzstatus.json"));
		this.mapCoreTypNameJsonPath.put("LehrerFachrichtung", this.buildPath("/types/Lehrer/LehrerFachrichtung.json"));
		this.mapCoreTypNameJsonPath.put("LehrerLehrbefaehigung", this.buildPath("/types/Lehrer/LehrerLehrbefaehigung.json"));
		this.mapCoreTypNameJsonPath.put("LehrerFachrichtungAnerkennung", this.buildPath("/types/Lehrer/LehrerFachrichtungAnerkennung.json"));
		this.mapCoreTypNameJsonPath.put("LehrerLehramt", this.buildPath("/types/Lehrer/LehrerLehramt.json"));
		this.mapCoreTypNameJsonPath.put("LehrerLehramtAnerkennung", this.buildPath("/types/Lehrer/LehrerLehramtAnerkennung.json"));
		this.mapCoreTypNameJsonPath.put("LehrerLehrbefaehigungAnerkennung", this.buildPath("/types/Lehrer/LehrerLehrbefaehigungAnerkennung.json"));
		this.mapCoreTypNameJsonPath.put("LehrerLeitungsfunktion", this.buildPath("/types/Lehrer/LehrerLeitungsfunktion.json"));
		this.mapCoreTypNameJsonPath.put("LehrerRechtsverhaeltnis", this.buildPath("/types/Lehrer/LehrerRechtsverhaeltnis.json"));
		this.mapCoreTypNameJsonPath.put("LehrerZugangsgrund", this.buildPath("/types/Lehrer/LehrerZugangsgrund.json"));
		this.mapCoreTypNameJsonPath.put("BilingualeSprache", this.buildPath("/types/fach/BilingualeSprache.json"));
		this.mapCoreTypNameJsonPath.put("KAOABerufsfeld", this.buildPath("/types/kaoa/KAOABerufsfeld.json"));
		this.mapCoreTypNameJsonPath.put("KAOAMerkmaleOptionsarten", this.buildPath("/types/kaoa/KAOAMerkmaleOptionsarten.json"));
		this.mapCoreTypNameJsonPath.put("KAOAZusatzmerkmaleOptionsarten", this.buildPath("/types/kaoa/KAOAZusatzmerkmaleOptionsarten.json"));
		this.mapCoreTypNameJsonPath.put("KAOAEbene4", this.buildPath("/types/kaoa/KAOAEbene4.json"));
		this.mapCoreTypNameJsonPath.put("KAOAZusatzmerkmal", this.buildPath("/types/kaoa/KAOAZusatzmerkmal.json"));
		this.mapCoreTypNameJsonPath.put("KAOAAnschlussoptionen", this.buildPath("/types/kaoa/KAOAAnschlussoptionen.json"));
		this.mapCoreTypNameJsonPath.put("KAOAKategorie", this.buildPath("/types/kaoa/KAOAKategorie.json"));
		this.mapCoreTypNameJsonPath.put("KAOAMerkmal", this.buildPath("/types/kaoa/KAOAMerkmal.json"));
		this.mapCoreTypNameJsonPath.put("Klassenart", this.buildPath("/types/klassen/Klassenart.json"));
		this.mapCoreTypNameJsonPath.put("Uebergangsempfehlung", this.buildPath("/types/schueler/Uebergangsempfehlung.json"));
		this.mapCoreTypNameJsonPath.put("ZulaessigeKursart", this.buildPath("/types/kurse/ZulaessigeKursart.json"));
		this.mapCoreTypNameJsonPath.put("Foerderschwerpunkt", this.buildPath("/types/schule/Foerderschwerpunkt.json"));
		this.mapCoreTypNameJsonPath.put("LehrerAnrechnungsgrund", this.buildPath("/types/Lehrer/LehrerAnrechnungsgrund.json"));
		this.mapCoreTypNameJsonPath.put("LehrerMehrleistungsarten", this.buildPath("/types/Lehrer/LehrerMehrleistungsarten.json"));
		this.mapCoreTypNameJsonPath.put("LehrerMinderleistungsarten", this.buildPath("/types/Lehrer/LehrerMinderleistungsarten.json"));
		this.mapCoreTypNameJsonPath.put("ValidatorenFehlerartKontext", this.buildPath("/validate/ValidatorenFehlerartKontext.json"));
	}

	private async loadJsonFromFileSystem(coreTypeName : string) {

		if (coreTypeName === "") {
			throw new DeveloperNotificationException(
				"Für das Einlesen eines Core-Types muss ein gültiger Name angegeben werden"
			);
		}

		const jsonFilePath : string | null = this.mapCoreTypNameJsonPath.get(coreTypeName);

		if (jsonFilePath === null) {
			throw new DeveloperNotificationException(
				"Kein Pfad für den angegebenen Core-Type gefunden: " + coreTypeName
			);
		}


		const jsonData = await fs.promises.readFile(jsonFilePath, 'utf-8');

		this.mapCoreTypNameJsonData.put(coreTypeName, jsonData);
	}

	private async loadJsonFromAPI(coreTypeName: string) {
		if (coreTypeName === "")
			throw new DeveloperNotificationException(
				"Für das Einlesen eines Core-Types muss ein gültiger Name angegeben werden"
			);

		const jsonData: string = await this.api.getJSON("/types/" + coreTypeName + ".json");
		this.mapCoreTypNameJsonData.put(coreTypeName, jsonData);
	}

	private read<T>(name: string, mapper: (json: string) => T): JsonCoreTypeDataResult<T> {
		if (name === "")
			throw new DeveloperNotificationException(
				"Für das Einlesen eines Core-Types muss ein gültiger Name angegeben werden"
			);

		const json: string | null = this.mapCoreTypNameJsonData.get(name);

		if (json === null) {
			throw new DeveloperNotificationException(
				`Für den den CoreType "${name}" liegen keine JSON Daten vor. Stelle sicher dass loadJsonFromAPI (loadAll) ausgeführt wurde.`
			);
		}

		const data = JSON.parse(json) as JsonCoreTypeData<T>;
		const idsHistorien = new HashSet<number>();
		const result = <JsonCoreTypeDataResult<T>>{
			version: data.version,
			mapData: new HashMap<string, List<T>>(),
			mapHistorienIDs: new HashMap<string, number>(),
		};
		for (const eintrag of data.daten) {
			const bezeichner = eintrag.bezeichner;
			const idHistorie = eintrag.idHistorie;
			const historie = eintrag.historie;
			if (idsHistorien.contains(idHistorie))
				throw new DeveloperNotificationException("Fehler beim Einlesen der Core-Type-Daten für den Core-Type " + name);
			idsHistorien.add(idHistorie);
			result.mapHistorienIDs.put(bezeichner, idHistorie);
			const list = new ArrayList<T>();
			result.mapData.put(bezeichner, list);
			for (const obj of historie) {
				const tmpJson = JSON.stringify(obj);
				const deserialized = mapper(tmpJson);
				list.add(deserialized);
			}
		}
		return result;
	}

	public readSchulform() {
		const data = this.read('Schulform', (json) => SchulformKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<SchulformKatalogEintrag, Schulform>(data.version, Schulform.class, Schulform.values(), data.mapData, data.mapHistorienIDs);
		Schulform.init(manager);
	}

	public readBerufskollegAnlage() {
		const data = this.read('BerufskollegAnlage', (json) => BerufskollegAnlageKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<BerufskollegAnlageKatalogEintrag, BerufskollegAnlage>(data.version, BerufskollegAnlage.class, BerufskollegAnlage.values(), data.mapData, data.mapHistorienIDs);
		BerufskollegAnlage.init(manager);
	}

	public readAllgemeinbildendOrganisationsformen() {
		const data = this.read('AllgemeinbildendOrganisationsformen', (json) => OrganisationsformKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<OrganisationsformKatalogEintrag, AllgemeinbildendOrganisationsformen>(data.version, AllgemeinbildendOrganisationsformen.class, AllgemeinbildendOrganisationsformen.values(), data.mapData, data.mapHistorienIDs);
		AllgemeinbildendOrganisationsformen.init(manager);
	}

	public readBerufskollegOrganisationsformen() {
		const data = this.read('BerufskollegOrganisationsformen', (json) => OrganisationsformKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<OrganisationsformKatalogEintrag, BerufskollegOrganisationsformen>(data.version, BerufskollegOrganisationsformen.class, BerufskollegOrganisationsformen.values(), data.mapData, data.mapHistorienIDs);
		BerufskollegOrganisationsformen.init(manager);
	}

	public readWeiterbildungskollegOrganisationsformen() {
		const data = this.read('WeiterbildungskollegOrganisationsformen', (json) => OrganisationsformKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<OrganisationsformKatalogEintrag, WeiterbildungskollegOrganisationsformen>(data.version, WeiterbildungskollegOrganisationsformen.class,
			WeiterbildungskollegOrganisationsformen.values(), data.mapData, data.mapHistorienIDs);
		WeiterbildungskollegOrganisationsformen.init(manager);
	}

	public readSchulabschlussAllgemeinbildend() {
		const data = this.read('SchulabschlussAllgemeinbildend', (json) => SchulabschlussAllgemeinbildendKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<SchulabschlussAllgemeinbildendKatalogEintrag, SchulabschlussAllgemeinbildend>(data.version, SchulabschlussAllgemeinbildend.class, SchulabschlussAllgemeinbildend.values(), data.mapData, data.mapHistorienIDs);
		SchulabschlussAllgemeinbildend.init(manager);
	}

	public readSchulabschlussBerufsbildend() {
		const data = this.read('SchulabschlussBerufsbildend', (json) => SchulabschlussBerufsbildendKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<SchulabschlussBerufsbildendKatalogEintrag, SchulabschlussBerufsbildend>(data.version, SchulabschlussBerufsbildend.class, SchulabschlussBerufsbildend.values(), data.mapData, data.mapHistorienIDs);
		SchulabschlussBerufsbildend.init(manager);
	}

	public readHerkunftBildungsgang() {
		const data = this.read('HerkunftBildungsgang', (json) => HerkunftBildungsgangKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<HerkunftBildungsgangKatalogEintrag, HerkunftBildungsgang>(data.version, HerkunftBildungsgang.class, HerkunftBildungsgang.values(), data.mapData, data.mapHistorienIDs);
		HerkunftBildungsgang.init(manager);
	}

	public readHerkunftBildungsgangTyp() {
		const data = this.read('HerkunftBildungsgangTyp', (json) => HerkunftBildungsgangTypKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<HerkunftBildungsgangTypKatalogEintrag, HerkunftBildungsgangTyp>(data.version, HerkunftBildungsgangTyp.class, HerkunftBildungsgangTyp.values(), data.mapData, data.mapHistorienIDs);
		HerkunftBildungsgangTyp.init(manager);
	}

	public readJahrgaenge() {
		const data = this.read('Jahrgaenge', (json) => JahrgaengeKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<JahrgaengeKatalogEintrag, Jahrgaenge>(data.version, Jahrgaenge.class, Jahrgaenge.values(), data.mapData, data.mapHistorienIDs);
		Jahrgaenge.init(manager);
	}

	public readPrimarstufeSchuleingangsphaseBesuchsjahre() {
		const data = this.read('PrimarstufeSchuleingangsphaseBesuchsjahre', (json) => PrimarstufeSchuleingangsphaseBesuchsjahreKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<PrimarstufeSchuleingangsphaseBesuchsjahreKatalogEintrag, PrimarstufeSchuleingangsphaseBesuchsjahre>(data.version, PrimarstufeSchuleingangsphaseBesuchsjahre.class, PrimarstufeSchuleingangsphaseBesuchsjahre.values(), data.mapData, data.mapHistorienIDs);
		PrimarstufeSchuleingangsphaseBesuchsjahre.init(manager);
	}

	public readReligion() {
		const data = this.read('Religion', (json) => ReligionKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<ReligionKatalogEintrag, Religion>(data.version, Religion.class, Religion.values(), data.mapData, data.mapHistorienIDs);
		Religion.init(manager);
	}

	public readKindergartenbesuch() {
		const data = this.read('Kindergartenbesuch', (json) => KindergartenbesuchKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<KindergartenbesuchKatalogEintrag, Kindergartenbesuch>(data.version, Kindergartenbesuch.class, Kindergartenbesuch.values(), data.mapData, data.mapHistorienIDs);
		Kindergartenbesuch.init(manager);
	}

	public readSchuelerStatus() {
		const data = this.read('SchuelerStatus', (json) => SchuelerStatusKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<SchuelerStatusKatalogEintrag, SchuelerStatus>(data.version, SchuelerStatus.class, SchuelerStatus.values(), data.mapData, data.mapHistorienIDs);
		SchuelerStatus.init(manager);
	}

	public readNote() {
		const data = this.read('Note', (json) => NoteKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<NoteKatalogEintrag, Note>(data.version, Note.class, Note.values(), data.mapData, data.mapHistorienIDs);
		Note.init(manager);
	}

	public readSprachreferenzniveau() {
		const data = this.read('Sprachreferenzniveau', (json) => SprachreferenzniveauKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<SprachreferenzniveauKatalogEintrag, Sprachreferenzniveau>(data.version, Sprachreferenzniveau.class, Sprachreferenzniveau.values(), data.mapData, data.mapHistorienIDs);
		Sprachreferenzniveau.init(manager);
	}

	public readBerufskollegBildungsgangTyp() {
		const data = this.read('BerufskollegBildungsgangTyp', (json) => BildungsgangTypKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<BildungsgangTypKatalogEintrag, BerufskollegBildungsgangTyp>(data.version, BerufskollegBildungsgangTyp.class, BerufskollegBildungsgangTyp.values(), data.mapData, data.mapHistorienIDs);
		BerufskollegBildungsgangTyp.init(manager);
	}

	public readWeiterbildungskollegBildungsgangTyp() {
		const data = this.read('WeiterbildungskollegBildungsgangTyp', (json) => BildungsgangTypKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<BildungsgangTypKatalogEintrag, WeiterbildungskollegBildungsgangTyp>(data.version, WeiterbildungskollegBildungsgangTyp.class, WeiterbildungskollegBildungsgangTyp.values(), data.mapData, data.mapHistorienIDs);
		WeiterbildungskollegBildungsgangTyp.init(manager);
	}

	public readSchulgliederung() {
		const data = this.read('Schulgliederung', (json) => SchulgliederungKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<SchulgliederungKatalogEintrag, Schulgliederung>(data.version, Schulgliederung.class, Schulgliederung.values(), data.mapData, data.mapHistorienIDs);
		Schulgliederung.init(manager);
	}

	public readFachgruppe() {
		const data = this.read('Fachgruppe', (json) => FachgruppeKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<FachgruppeKatalogEintrag, Fachgruppe>(data.version, Fachgruppe.class, Fachgruppe.values(), data.mapData, data.mapHistorienIDs);
		Fachgruppe.init(manager);
	}

	public readFach() {
		const data = this.read('Fach', (json) => FachKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<FachKatalogEintrag, Fach>(data.version, Fach.class, Fach.values(), data.mapData, data.mapHistorienIDs);
		Fach.init(manager);
	}

	public readLehrerAbgangsgrund() {
		const data = this.read('LehrerAbgangsgrund', (json) => LehrerAbgangsgrundKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<LehrerAbgangsgrundKatalogEintrag, LehrerAbgangsgrund>(data.version, LehrerAbgangsgrund.class, LehrerAbgangsgrund.values(), data.mapData, data.mapHistorienIDs);
		LehrerAbgangsgrund.init(manager);
	}

	public readLehrerBeschaeftigungsart() {
		const data = this.read('LehrerBeschaeftigungsart', (json) => LehrerBeschaeftigungsartKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<LehrerBeschaeftigungsartKatalogEintrag, LehrerBeschaeftigungsart>(data.version, LehrerBeschaeftigungsart.class, LehrerBeschaeftigungsart.values(), data.mapData, data.mapHistorienIDs);
		LehrerBeschaeftigungsart.init(manager);
	}

	public readLehrerEinsatzstatus() {
		const data = this.read('LehrerEinsatzstatus', (json) => LehrerEinsatzstatusKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<LehrerEinsatzstatusKatalogEintrag, LehrerEinsatzstatus>(data.version, LehrerEinsatzstatus.class, LehrerEinsatzstatus.values(), data.mapData, data.mapHistorienIDs);
		LehrerEinsatzstatus.init(manager);
	}

	public readLehrerFachrichtung() {
		const data = this.read('LehrerFachrichtung', (json) => LehrerFachrichtungKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<LehrerFachrichtungKatalogEintrag, LehrerFachrichtung>(data.version, LehrerFachrichtung.class, LehrerFachrichtung.values(), data.mapData, data.mapHistorienIDs);
		LehrerFachrichtung.init(manager);
	}

	public readLehrerLehrbefaehigung() {
		const data = this.read('LehrerLehrbefaehigung', (json) => LehrerLehrbefaehigungKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<LehrerLehrbefaehigungKatalogEintrag, LehrerLehrbefaehigung>(data.version, LehrerLehrbefaehigung.class, LehrerLehrbefaehigung.values(), data.mapData, data.mapHistorienIDs);
		LehrerLehrbefaehigung.init(manager);
	}

	public readLehrerFachrichtungAnerkennung() {
		const data = this.read('LehrerFachrichtungAnerkennung', (json) => LehrerFachrichtungAnerkennungKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<LehrerFachrichtungAnerkennungKatalogEintrag, LehrerFachrichtungAnerkennung>(data.version, LehrerFachrichtungAnerkennung.class, LehrerFachrichtungAnerkennung.values(), data.mapData, data.mapHistorienIDs);
		LehrerFachrichtungAnerkennung.init(manager);
	}

	public readLehrerLehramt() {
		const data = this.read('LehrerLehramt', (json) => LehrerLehramtKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<LehrerLehramtKatalogEintrag, LehrerLehramt>(data.version, LehrerLehramt.class, LehrerLehramt.values(), data.mapData, data.mapHistorienIDs);
		LehrerLehramt.init(manager);
	}

	public readLehrerLehramtAnerkennung() {
		const data = this.read('LehrerLehramtAnerkennung', (json) => LehrerLehramtAnerkennungKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<LehrerLehramtAnerkennungKatalogEintrag, LehrerLehramtAnerkennung>(data.version, LehrerLehramtAnerkennung.class, LehrerLehramtAnerkennung.values(), data.mapData, data.mapHistorienIDs);
		LehrerLehramtAnerkennung.init(manager);
	}

	public readLehrerLehrbefaehigungAnerkennung() {
		const data = this.read('LehrerLehrbefaehigungAnerkennung', (json) => LehrerLehrbefaehigungAnerkennungKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<LehrerLehrbefaehigungAnerkennungKatalogEintrag, LehrerLehrbefaehigungAnerkennung>(data.version, LehrerLehrbefaehigungAnerkennung.class, LehrerLehrbefaehigungAnerkennung.values(), data.mapData, data.mapHistorienIDs);
		LehrerLehrbefaehigungAnerkennung.init(manager);
	}

	public readLehrerLeitungsfunktion() {
		const data = this.read('LehrerLeitungsfunktion', (json) => LehrerLeitungsfunktionKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<LehrerLeitungsfunktionKatalogEintrag, LehrerLeitungsfunktion>(data.version, LehrerLeitungsfunktion.class, LehrerLeitungsfunktion.values(), data.mapData, data.mapHistorienIDs);
		LehrerLeitungsfunktion.init(manager);
	}

	public readLehrerRechtsverhaeltnis() {
		const data = this.read('LehrerRechtsverhaeltnis', (json) => LehrerRechtsverhaeltnisKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<LehrerRechtsverhaeltnisKatalogEintrag, LehrerRechtsverhaeltnis>(data.version, LehrerRechtsverhaeltnis.class, LehrerRechtsverhaeltnis.values(), data.mapData, data.mapHistorienIDs);
		LehrerRechtsverhaeltnis.init(manager);
	}

	public readLehrerZugangsgrund() {
		const data = this.read('LehrerZugangsgrund', (json) => LehrerZugangsgrundKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<LehrerZugangsgrundKatalogEintrag, LehrerZugangsgrund>(data.version, LehrerZugangsgrund.class, LehrerZugangsgrund.values(), data.mapData, data.mapHistorienIDs);
		LehrerZugangsgrund.init(manager);
	}

	public readBilingualeSprache() {
		const data = this.read('BilingualeSprache', (json) => BilingualeSpracheKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<BilingualeSpracheKatalogEintrag, BilingualeSprache>(data.version, BilingualeSprache.class, BilingualeSprache.values(), data.mapData, data.mapHistorienIDs);
		BilingualeSprache.init(manager);
	}

	public readKAOABerufsfeld() {
		const data = this.read('KAOABerufsfeld', (json) => KAOABerufsfeldKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<KAOABerufsfeldKatalogEintrag, KAOABerufsfeld>(data.version, KAOABerufsfeld.class, KAOABerufsfeld.values(), data.mapData, data.mapHistorienIDs);
		KAOABerufsfeld.init(manager);
	}

	public readKAOAMerkmaleOptionsarten() {
		const data = this.read('KAOAMerkmaleOptionsarten', (json) => KAOAMerkmaleOptionsartenKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<KAOAMerkmaleOptionsartenKatalogEintrag, KAOAMerkmaleOptionsarten>(data.version, KAOAMerkmaleOptionsarten.class, KAOAMerkmaleOptionsarten.values(), data.mapData, data.mapHistorienIDs);
		KAOAMerkmaleOptionsarten.init(manager);
	}

	public readKAOAZusatzmerkmaleOptionsarten() {
		const data = this.read('KAOAZusatzmerkmaleOptionsarten', (json) => KAOAZusatzmerkmaleOptionsartenKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<KAOAZusatzmerkmaleOptionsartenKatalogEintrag, KAOAZusatzmerkmaleOptionsarten>(data.version, KAOAZusatzmerkmaleOptionsarten.class, KAOAZusatzmerkmaleOptionsarten.values(), data.mapData, data.mapHistorienIDs);
		KAOAZusatzmerkmaleOptionsarten.init(manager);
	}

	public readKAOAEbene4() {
		const data = this.read('KAOAEbene4', (json) => KAOAEbene4KatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<KAOAEbene4KatalogEintrag, KAOAEbene4>(data.version, KAOAEbene4.class, KAOAEbene4.values(), data.mapData, data.mapHistorienIDs);
		KAOAEbene4.init(manager);
	}

	public readKAOAZusatzmerkmal() {
		const data = this.read('KAOAZusatzmerkmal', (json) => KAOAZusatzmerkmalKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<KAOAZusatzmerkmalKatalogEintrag, KAOAZusatzmerkmal>(data.version, KAOAZusatzmerkmal.class, KAOAZusatzmerkmal.values(), data.mapData, data.mapHistorienIDs);
		KAOAZusatzmerkmal.init(manager);
	}

	public readKAOAAnschlussoptionen() {
		const data = this.read('KAOAAnschlussoptionen', (json) => KAOAAnschlussoptionenKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<KAOAAnschlussoptionenKatalogEintrag, KAOAAnschlussoptionen>(data.version, KAOAAnschlussoptionen.class, KAOAAnschlussoptionen.values(), data.mapData, data.mapHistorienIDs);
		KAOAAnschlussoptionen.init(manager);
	}

	public readKAOAKategorie() {
		const data = this.read('KAOAKategorie', (json) => KAOAKategorieKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<KAOAKategorieKatalogEintrag, KAOAKategorie>(data.version, KAOAKategorie.class, KAOAKategorie.values(), data.mapData, data.mapHistorienIDs);
		KAOAKategorie.init(manager);
	}

	public readKAOAMerkmal() {
		const data = this.read('KAOAMerkmal', (json) => KAOAMerkmalKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<KAOAMerkmalKatalogEintrag, KAOAMerkmal>(data.version, KAOAMerkmal.class, KAOAMerkmal.values(), data.mapData, data.mapHistorienIDs);
		KAOAMerkmal.init(manager);
	}


	public readKlassenart() {
		const data = this.read('Klassenart', (json) => KlassenartKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<KlassenartKatalogEintrag, Klassenart>(data.version, Klassenart.class, Klassenart.values(), data.mapData, data.mapHistorienIDs);
		Klassenart.init(manager);
	}

	public readUebergangsempfehlung() {
		const data = this.read('Uebergangsempfehlung', (json) => UebergangsempfehlungKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<UebergangsempfehlungKatalogEintrag, Uebergangsempfehlung>(data.version, Uebergangsempfehlung.class, Uebergangsempfehlung.values(), data.mapData, data.mapHistorienIDs);
		Uebergangsempfehlung.init(manager);
	}

	public readZulaessigeKursart() {
		const data = this.read('ZulaessigeKursart', (json) => ZulaessigeKursartKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<ZulaessigeKursartKatalogEintrag, ZulaessigeKursart>(data.version, ZulaessigeKursart.class, ZulaessigeKursart.values(), data.mapData, data.mapHistorienIDs);
		ZulaessigeKursart.init(manager);
	}

	public readFoerderschwerpunkt() {
		const data = this.read('Foerderschwerpunkt', (json) => FoerderschwerpunktKatalogEintrag.transpilerFromJSON(json));
		const manager = new CoreTypeDataManager<FoerderschwerpunktKatalogEintrag, Foerderschwerpunkt>(data.version, Foerderschwerpunkt.class, Foerderschwerpunkt.values(), data.mapData, data.mapHistorienIDs);
		Foerderschwerpunkt.init(manager);
	}

	public readLehrerAnrechnungsgrund() {
		const data = this.read('LehrerAnrechnungsgrund', (json) => LehrerAnrechnungsgrundKatalogEintrag.transpilerFromJSON(json));
		CoreTypeSimple.initValues(new LehrerAnrechnungsgrund(), LehrerAnrechnungsgrund.class, data.mapData);
		const manager = new CoreTypeDataManager<LehrerAnrechnungsgrundKatalogEintrag, LehrerAnrechnungsgrund>(data.version, LehrerAnrechnungsgrund.class, LehrerAnrechnungsgrund.values(), data.mapData, data.mapHistorienIDs);
		LehrerAnrechnungsgrund.init(manager);
	}

	public readLehrerMehrleistungsarten() {
		const data = this.read('LehrerMehrleistungsarten', (json) => LehrerMehrleistungsartKatalogEintrag.transpilerFromJSON(json));
		CoreTypeSimple.initValues(new LehrerMehrleistungsarten(), LehrerMehrleistungsarten.class, data.mapData);
		const manager = new CoreTypeDataManager<LehrerMehrleistungsartKatalogEintrag, LehrerMehrleistungsarten>(data.version, LehrerMehrleistungsarten.class, LehrerMehrleistungsarten.values(), data.mapData, data.mapHistorienIDs);
		LehrerMehrleistungsarten.init(manager);
	}

	public readLehrerMinderleistungsarten() {
		const data = this.read('LehrerMinderleistungsarten', (json) => LehrerMinderleistungsartKatalogEintrag.transpilerFromJSON(json));
		CoreTypeSimple.initValues(new LehrerMinderleistungsarten(), LehrerMinderleistungsarten.class, data.mapData);
		const manager = new CoreTypeDataManager<LehrerMinderleistungsartKatalogEintrag, LehrerMinderleistungsarten>(data.version, LehrerMinderleistungsarten.class, LehrerMinderleistungsarten.values(), data.mapData, data.mapHistorienIDs);
		LehrerMinderleistungsarten.init(manager);
	}

	public readValidatorenFehlerartKontext() {
		// TODO
		// const data = this.readValidator('ValidatorenFehlerartKontext', (json) => ValidatorFehlerartKontext.transpilerFromJSON(json));
		// ValidatorManager.init(data.version, data.mapData);
	}


	public async loadAllFromFileSystem() : Promise<HashMap<string, string>> {
		console.log("Lade die Json Files");
		await Promise.all([
			this.loadJsonFromFileSystem("Schulform"),
			this.loadJsonFromFileSystem("BerufskollegAnlage"),
			this.loadJsonFromFileSystem("AllgemeinbildendOrganisationsformen"),
			this.loadJsonFromFileSystem("BerufskollegOrganisationsformen"),
			this.loadJsonFromFileSystem("WeiterbildungskollegOrganisationsformen"),
			this.loadJsonFromFileSystem("SchulabschlussAllgemeinbildend"),
			this.loadJsonFromFileSystem("SchulabschlussBerufsbildend"),
			this.loadJsonFromFileSystem("HerkunftBildungsgang"),
			this.loadJsonFromFileSystem("HerkunftBildungsgangTyp"),
			this.loadJsonFromFileSystem("Jahrgaenge"),
			this.loadJsonFromFileSystem("PrimarstufeSchuleingangsphaseBesuchsjahre"),
			this.loadJsonFromFileSystem("Religion"),
			this.loadJsonFromFileSystem("Kindergartenbesuch"),
			this.loadJsonFromFileSystem("SchuelerStatus"),
			this.loadJsonFromFileSystem("Note"),
			this.loadJsonFromFileSystem("Sprachreferenzniveau"),
			this.loadJsonFromFileSystem("BerufskollegBildungsgangTyp"),
			this.loadJsonFromFileSystem("WeiterbildungskollegBildungsgangTyp"),
			this.loadJsonFromFileSystem("Schulgliederung"),
			this.loadJsonFromFileSystem("Fachgruppe"),
			this.loadJsonFromFileSystem("Fach"),
			this.loadJsonFromFileSystem("LehrerAbgangsgrund"),
			this.loadJsonFromFileSystem("LehrerBeschaeftigungsart"),
			this.loadJsonFromFileSystem("LehrerEinsatzstatus"),
			this.loadJsonFromFileSystem("LehrerFachrichtung"),
			this.loadJsonFromFileSystem("LehrerLehrbefaehigung"),
			this.loadJsonFromFileSystem("LehrerFachrichtungAnerkennung"),
			this.loadJsonFromFileSystem("LehrerLehramt"),
			this.loadJsonFromFileSystem("LehrerLehramtAnerkennung"),
			this.loadJsonFromFileSystem("LehrerLehrbefaehigungAnerkennung"),
			this.loadJsonFromFileSystem("LehrerLeitungsfunktion"),
			this.loadJsonFromFileSystem("LehrerRechtsverhaeltnis"),
			this.loadJsonFromFileSystem("LehrerZugangsgrund"),
			this.loadJsonFromFileSystem("BilingualeSprache"),
			this.loadJsonFromFileSystem("KAOABerufsfeld"),
			this.loadJsonFromFileSystem("KAOAMerkmaleOptionsarten"),
			this.loadJsonFromFileSystem("KAOAZusatzmerkmaleOptionsarten"),
			this.loadJsonFromFileSystem("KAOAEbene4"),
			this.loadJsonFromFileSystem("KAOAZusatzmerkmal"),
			this.loadJsonFromFileSystem("KAOAAnschlussoptionen"),
			this.loadJsonFromFileSystem("KAOAKategorie"),
			this.loadJsonFromFileSystem("KAOAMerkmal"),
			this.loadJsonFromFileSystem("Klassenart"),
			this.loadJsonFromFileSystem("Uebergangsempfehlung"),
			this.loadJsonFromFileSystem("ZulaessigeKursart"),
			this.loadJsonFromFileSystem("Foerderschwerpunkt"),
			this.loadJsonFromFileSystem("LehrerAnrechnungsgrund"),
			this.loadJsonFromFileSystem("LehrerMehrleistungsarten"),
			this.loadJsonFromFileSystem("LehrerMinderleistungsarten"),
			this.loadJsonFromFileSystem("ValidatorenFehlerartKontext"),
		]);

		return this.mapCoreTypNameJsonData;
	}


	public async loadAllFromAPI() : Promise<HashMap<string, string>> {
		console.log("Lade die Json Files");
		await Promise.all([
			this.loadJsonFromAPI("Schulform"),
			this.loadJsonFromAPI("BerufskollegAnlage"),
			this.loadJsonFromAPI("AllgemeinbildendOrganisationsformen"),
			this.loadJsonFromAPI("BerufskollegOrganisationsformen"),
			this.loadJsonFromAPI("WeiterbildungskollegOrganisationsformen"),
			this.loadJsonFromAPI("SchulabschlussAllgemeinbildend"),
			this.loadJsonFromAPI("SchulabschlussBerufsbildend"),
			this.loadJsonFromAPI("HerkunftBildungsgang"),
			this.loadJsonFromAPI("HerkunftBildungsgangTyp"),
			this.loadJsonFromAPI("Jahrgaenge"),
			this.loadJsonFromAPI("PrimarstufeSchuleingangsphaseBesuchsjahre"),
			this.loadJsonFromAPI("Religion"),
			this.loadJsonFromAPI("Kindergartenbesuch"),
			this.loadJsonFromAPI("SchuelerStatus"),
			this.loadJsonFromAPI("Note"),
			this.loadJsonFromAPI("Sprachreferenzniveau"),
			this.loadJsonFromAPI("BerufskollegBildungsgangTyp"),
			this.loadJsonFromAPI("WeiterbildungskollegBildungsgangTyp"),
			this.loadJsonFromAPI("Schulgliederung"),
			this.loadJsonFromAPI("Fachgruppe"),
			this.loadJsonFromAPI("Fach"),
			this.loadJsonFromAPI("LehrerAbgangsgrund"),
			this.loadJsonFromAPI("LehrerBeschaeftigungsart"),
			this.loadJsonFromAPI("LehrerEinsatzstatus"),
			this.loadJsonFromAPI("LehrerFachrichtung"),
			this.loadJsonFromAPI("LehrerLehrbefaehigung"),
			this.loadJsonFromAPI("LehrerFachrichtungAnerkennung"),
			this.loadJsonFromAPI("LehrerLehramt"),
			this.loadJsonFromAPI("LehrerLehramtAnerkennung"),
			this.loadJsonFromAPI("LehrerLehrbefaehigungAnerkennung"),
			this.loadJsonFromAPI("LehrerLeitungsfunktion"),
			this.loadJsonFromAPI("LehrerRechtsverhaeltnis"),
			this.loadJsonFromAPI("LehrerZugangsgrund"),
			this.loadJsonFromAPI("BilingualeSprache"),
			this.loadJsonFromAPI("KAOABerufsfeld"),
			this.loadJsonFromAPI("KAOAMerkmaleOptionsarten"),
			this.loadJsonFromAPI("KAOAZusatzmerkmaleOptionsarten"),
			this.loadJsonFromAPI("KAOAEbene4"),
			this.loadJsonFromAPI("KAOAZusatzmerkmal"),
			this.loadJsonFromAPI("KAOAAnschlussoptionen"),
			this.loadJsonFromAPI("KAOAKategorie"),
			this.loadJsonFromAPI("KAOAMerkmal"),
			this.loadJsonFromAPI("Klassenart"),
			this.loadJsonFromAPI("Uebergangsempfehlung"),
			this.loadJsonFromAPI("ZulaessigeKursart"),
			this.loadJsonFromAPI("Foerderschwerpunkt"),
			this.loadJsonFromAPI("LehrerAnrechnungsgrund"),
			this.loadJsonFromAPI("LehrerMehrleistungsarten"),
			this.loadJsonFromAPI("LehrerMinderleistungsarten"),
			this.loadJsonFromAPI("ValidatorenFehlerartKontext"),
		]);

		return this.mapCoreTypNameJsonData;
	}

	public readAll() {
		console.log("Initialisiere die Core Types");
		try {
			this.readSchulform();
			this.readBerufskollegAnlage();
			this.readAllgemeinbildendOrganisationsformen();
			this.readBerufskollegOrganisationsformen();
			this.readWeiterbildungskollegOrganisationsformen();
			this.readSchulabschlussAllgemeinbildend();
			this.readSchulabschlussBerufsbildend();
			this.readHerkunftBildungsgang();
			this.readHerkunftBildungsgangTyp();
			this.readJahrgaenge();
			this.readPrimarstufeSchuleingangsphaseBesuchsjahre();
			this.readReligion();
			this.readKindergartenbesuch();
			this.readSchuelerStatus();
			this.readNote();
			this.readSprachreferenzniveau();
			this.readBerufskollegBildungsgangTyp();
			this.readWeiterbildungskollegBildungsgangTyp();
			this.readSchulgliederung();
			this.readFachgruppe();
			this.readFach();
			this.readLehrerAbgangsgrund();
			this.readLehrerBeschaeftigungsart();
			this.readLehrerEinsatzstatus();
			this.readLehrerFachrichtung();
			this.readLehrerLehrbefaehigung();
			this.readLehrerFachrichtungAnerkennung();
			this.readLehrerLehramt();
			this.readLehrerLehramtAnerkennung();
			this.readLehrerLehrbefaehigungAnerkennung();
			this.readLehrerLeitungsfunktion();
			this.readLehrerRechtsverhaeltnis();
			this.readLehrerZugangsgrund();
			this.readBilingualeSprache();
			this.readKAOABerufsfeld();
			this.readKAOAMerkmaleOptionsarten();
			this.readKAOAZusatzmerkmaleOptionsarten();
			this.readKAOAEbene4();
			this.readKAOAZusatzmerkmal();
			this.readKAOAAnschlussoptionen();
			this.readKAOAKategorie();
			this.readKAOAMerkmal();
			this.readKlassenart();
			this.readUebergangsempfehlung();
			this.readZulaessigeKursart();
			this.readFoerderschwerpunkt();
			this.readLehrerAnrechnungsgrund();
			this.readLehrerMehrleistungsarten();
			this.readLehrerMinderleistungsarten();
			this.readValidatorenFehlerartKontext();
		} catch (e) {
			console.log(e)
		}
	}
}
