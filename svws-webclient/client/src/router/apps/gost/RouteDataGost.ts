import type { RouteParams } from "vue-router";
import type { GostJahrgang, GostJahrgangsdaten, JahrgangsDaten, GostFach } from "@core";
import { NullPointerException, DeveloperNotificationException, GostAbiturjahrUtils, Schulgliederung, GostFaecherManager, ArrayList, Jahrgaenge } from "@core";

import { api } from "~/router/Api";
import { RouteManager } from "~/router/RouteManager";
import { RouteData, type RouteStateInterface } from "~/router/RouteData";
import { routeApp } from "~/router/apps/RouteApp";
import { routeGost } from "~/router/apps/gost/RouteGost";

import { routeGostBeratung } from "~/router/apps/gost/beratung/RouteGostBeratung";

interface RouteStateGost extends RouteStateInterface {
	params: RouteParams;
	idSchuljahresabschnitt: number,
	auswahl: GostJahrgang | undefined;
	jahrgangsdaten: GostJahrgangsdaten | undefined;
	faecherManager: GostFaecherManager;
	mapAbiturjahrgaenge: Map<number, GostJahrgang>;
	mapJahrgaenge: Map<number, JahrgangsDaten>;
	mapJahrgaengeOhneAbiJahrgang: Map<number, JahrgangsDaten>;
}

const defaultState = <RouteStateGost> {
	params: {abiturjahr: '-1'},
	idSchuljahresabschnitt: -1,
	auswahl: undefined,
	jahrgangsdaten: undefined,
	faecherManager: new GostFaecherManager(new ArrayList()),
	mapAbiturjahrgaenge: new Map(),
	mapJahrgaenge: new Map(),
	mapJahrgaengeOhneAbiJahrgang: new Map(),
	view: routeGostBeratung
};

export class RouteDataGost extends RouteData<RouteStateGost> {

	public constructor() {
		super(defaultState);
	}

	private firstAbiturjahrgang(mapAbiturjahrgaenge: Map<number, GostJahrgang>): GostJahrgang | undefined {
		if (mapAbiturjahrgaenge.size === 0)
			return undefined;
		return mapAbiturjahrgaenge.values().next().value;
	}

	private async ladeAbiturjahrgaenge(): Promise<Map<number, GostJahrgang>> {
		const listAbiturjahrgaenge = await api.server.getGostAbiturjahrgaenge(api.schema);
		const mapAbiturjahrgaenge = new Map<number, GostJahrgang>();
		for (const l of listAbiturjahrgaenge)
			mapAbiturjahrgaenge.set(l.abiturjahr, l);
		return mapAbiturjahrgaenge;
	}

	private ladeJahrgaengeOhneAbiJahrgang(mapAbiturjahrgaenge: Map<number, GostJahrgang>, mapJahrgaenge: Map<number, JahrgangsDaten>): Map<number, JahrgangsDaten> {
		const jahrgaengeMitAbiturjahrgang = new Set();
		for (const j of mapAbiturjahrgaenge.values())
			jahrgaengeMitAbiturjahrgang.add(j.jahrgang);
		const map = new Map<number, JahrgangsDaten>();
		for (const j of mapJahrgaenge.values()) {
			if (!jahrgaengeMitAbiturjahrgang.has(j.kuerzel)) {
				const abiturjahr = this.getAbiturjahrFuerJahrgangMitMap(j.id, mapJahrgaenge);
				if (abiturjahr !== null)
					map.set(j.id, j);
			}
		}
		return map;
	}

	private async ladeJahrgaenge(): Promise<Map<number, JahrgangsDaten>> {
		// Lade die Liste der Jahrgänge, für welche Abiturjahrgänge ggf. angelegt werden können.
		const listJahrgaenge = await api.server.getJahrgaenge(api.schema);
		const mapJahrgaenge = new Map<number, JahrgangsDaten>();
		for (const j of listJahrgaenge) {
			const jg : Jahrgaenge | null = Jahrgaenge.getByKuerzel(j.kuerzelStatistik);
			if ((jg !== null) && (jg.hasSchulform(api.schulform)))
				mapJahrgaenge.set(j.id, j);
		}
		return mapJahrgaenge;
	}

	private async ladeJahrgangsdaten(auswahl: GostJahrgang | undefined) {
		if (auswahl === undefined)
			return;
		return await api.server.getGostAbiturjahrgang(api.schema, auswahl.abiturjahr);
	}

	private async ladeFaecherManager(auswahl: GostJahrgang | undefined): Promise<GostFaecherManager | undefined> {
		if (auswahl === undefined)
			return;
		const listFaecher = await api.server.getGostAbiturjahrgangFaecher(api.schema, auswahl.abiturjahr);
		return new GostFaecherManager(listFaecher);
	}


	private async ladeDatenFuerSchuljahresabschnitt(idSchuljahresabschnitt: number) : Promise<Partial<RouteStateGost>> {
		// TODO Lade die Lehrerliste in Abhängigkeit von dem angegebenen Schuljahresabschnitt, sobald die API-Methode dafür existiert
		const mapAbiturjahrgaenge = await this.ladeAbiturjahrgaenge();
		const auswahl = this.firstAbiturjahrgang(mapAbiturjahrgaenge);
		const mapJahrgaenge = await this.ladeJahrgaenge();
		const mapJahrgaengeOhneAbiJahrgang = this.ladeJahrgaengeOhneAbiJahrgang(mapAbiturjahrgaenge, mapJahrgaenge);
		const jahrgangsdaten = await this.ladeJahrgangsdaten(undefined);
		const faecherManager = await this.ladeFaecherManager(undefined);
		return <Partial<RouteStateGost>>{
			idSchuljahresabschnitt,
			auswahl,
			mapAbiturjahrgaenge,
			mapJahrgaenge,
			mapJahrgaengeOhneAbiJahrgang,
			jahrgangsdaten,
			faecherManager,
			view: this._state.value.view,
		};
	}

	/**
	 * Setzt den Schuljahresabschnitt und triggert damit das Laden der Defaults für diesen Abschnitt
	 *
	 * @param {number} idSchuljahresabschnitt   die ID des Schuljahresabschnitts
	 */
	public async setSchuljahresabschnitt(idSchuljahresabschnitt: number) {
		this.setPatchedDefaultState(await this.ladeDatenFuerSchuljahresabschnitt(idSchuljahresabschnitt));
	}

	get jahrgangsdaten(): GostJahrgangsdaten {
		if (this._state.value.jahrgangsdaten === undefined)
			throw new DeveloperNotificationException("Unerwarteter Fehler: Jahrgangsdaten nicht initialisiert");
		return this._state.value.jahrgangsdaten;
	}

	get params() {
		return this._state.value.params;
	}

	set params(value) {
		this.setPatchedState({ params: value })
	}

	public get idSchuljahresabschnitt(): number {
		return this._state.value.idSchuljahresabschnitt;
	}

	get auswahl(): GostJahrgang | undefined {
		return this._state.value.auswahl;
	}

	get faecherManager(): GostFaecherManager {
		return this._state.value.faecherManager;
	}

	get mapAbiturjahrgaenge() {
		return this._state.value.mapAbiturjahrgaenge;
	}

	get mapJahrgaenge() {
		return this._state.value.mapJahrgaenge;
	}

	get mapJahrgaengeOhneAbiJahrgang() {
		return this._state.value.mapJahrgaengeOhneAbiJahrgang;
	}

	patchJahrgangsdaten = async (data: Partial<GostJahrgangsdaten>, abiturjahr : number) => {
		if (this.jahrgangsdaten === undefined)
			return false;
		await api.server.patchGostAbiturjahrgang(data, api.schema, abiturjahr)
		this.setPatchedState({ jahrgangsdaten: Object.assign(this.jahrgangsdaten, data) })
		return true;
	}

	addAbiturjahrgang = async (idJahrgang: number) => {
		const abiturjahr : number | null = await api.server.createGostAbiturjahrgang(api.schema, idJahrgang);
		if (abiturjahr === null)
			throw new DeveloperNotificationException("Abiturjahrgang konnte nicht erstellt werden.");
		let daten : Partial<RouteStateGost> = await this.ladeDatenFuerSchuljahresabschnitt(this._state.value.idSchuljahresabschnitt);
		const jahrgang: GostJahrgang | undefined = daten.mapAbiturjahrgaenge?.get(abiturjahr);
		if (jahrgang === undefined)
			throw new DeveloperNotificationException("Der neu erstelle Abiturjahrgang konnte nicht geladen werden.");
		daten = await this.ladeDatenFuerAbiturjahrgang(jahrgang, daten);
		this.setPatchedDefaultState(daten);
		await RouteManager.doRoute(routeGost.getRoute(jahrgang.abiturjahr));
	}

	patchFach = async (data: Partial<GostFach>, fach_id: number) => {
		if (this.jahrgangsdaten === undefined)
			return;
		await api.server.patchGostAbiturjahrgangFach(data, api.schema, this.jahrgangsdaten.abiturjahr, fach_id);
		const fach = this.faecherManager.get(fach_id);
		if (fach !== null)
			Object.assign(fach, data);
		this.setPatchedState({ faecherManager: this.faecherManager })
		return;
	}

	private async ladeDatenFuerAbiturjahrgang(jahrgang: GostJahrgang | undefined, curState : Partial<RouteDataGost>) : Promise<Partial<RouteDataGost>> {
		if (jahrgang && jahrgang.abiturjahr === curState.auswahl?.abiturjahr && curState.jahrgangsdaten !== undefined)
			return curState;
		if (jahrgang === undefined || jahrgang === null || this.mapJahrgaenge.size === 0) {
			return Object.assign({ ... this._defaultState }, {
				idSchuljahresabschnitt: this._state.value.idSchuljahresabschnitt,
			});
		}
		let auswahl : GostJahrgang | undefined = jahrgang;
		if (curState.mapAbiturjahrgaenge?.get(jahrgang.abiturjahr) === undefined) {
			if (curState.mapAbiturjahrgaenge?.size === 0)
				auswahl = undefined;
			else
				auswahl = curState.mapAbiturjahrgaenge?.values().next().value;
		}
		const jahrgangsdaten = await this.ladeJahrgangsdaten(auswahl);
		const faecherManager = await this.ladeFaecherManager(auswahl);
		return Object.assign({ ... curState }, {
			auswahl,
			jahrgangsdaten,
			faecherManager,
			view: this._state.value.view,
		});
	}

	setAbiturjahrgang = async (jahrgang: GostJahrgang | undefined) => {
		const daten = await this.ladeDatenFuerAbiturjahrgang(jahrgang, this._state.value);
		this.setPatchedDefaultState(daten);
	}

	gotoAbiturjahrgang = async (value: GostJahrgang | undefined) => {
		if (value === undefined || value === null) {
			// TODO: Das ist ein Bug in der Tabelle, die bei gleicher Auswahl undefined schickt
			// await RouteManager.doRoute({ name: routeGost.name, params: { idSchuljahresabschnitt: routeApp.data.idSchuljahresabschnitt } });
			return;
		}
		const redirect_name: string = (routeGost.selectedChild === undefined) ? routeGostBeratung.name : routeGost.selectedChild.name;
		await RouteManager.doRoute({ name: redirect_name, params: { idSchuljahresabschnitt: routeApp.data.idSchuljahresabschnitt, abiturjahr: value.abiturjahr } });
	}

	private getAbiturjahrFuerJahrgangMitMap(idJahrgang : number, mapJahrgaenge : Map<number, JahrgangsDaten>) : number | null {
		const jahrgang = mapJahrgaenge.get(idJahrgang);
		if (jahrgang === undefined)
			throw new DeveloperNotificationException("Konnte den Jahrgang für die ID " + idJahrgang + " nicht bestimmen.");
		const schulgliederung: Schulgliederung | null = Schulgliederung.getByKuerzel(jahrgang.kuerzelSchulgliederung);
		if (schulgliederung === null)
			throw new DeveloperNotificationException("Dem Jahrgang mit der ID " + idJahrgang + " ist eine unbekannte Schulgliederung " + jahrgang.kuerzelSchulgliederung + " zugeordnet.");
		const abiturjahr = GostAbiturjahrUtils.getGostAbiturjahr(api.schulform, schulgliederung, routeApp.data.aktAbschnitt.value.schuljahr, jahrgang.kuerzelStatistik);
		if (abiturjahr === null)
			return null;
		return abiturjahr;
	}

	getAbiturjahrFuerJahrgang = (idJahrgang : number) => {
		const abiturjahr = this.getAbiturjahrFuerJahrgangMitMap(idJahrgang, this._state.value.mapJahrgaenge);
		if (abiturjahr === null)
			throw new NullPointerException("Dem Jahrgang mit der ID " + idJahrgang + " konnte kein Abiturjahr zugeordnet werden.");
		return abiturjahr;
	}


	removeAbiturjahrgang = async () => {
		await api.server.deleteGostAbiturjahrgang(api.schema, this.jahrgangsdaten.abiturjahr);
		let state = await this.ladeDatenFuerSchuljahresabschnitt(this.idSchuljahresabschnitt);
		state = await this.ladeDatenFuerAbiturjahrgang(state.auswahl, state);
		this.setPatchedDefaultState(state);
	}

}

