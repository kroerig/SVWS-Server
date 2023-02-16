import { Abiturdaten, AbiturdatenManager, GostBelegpruefungErgebnis, GostBelegpruefungsArt, GostFach,
	GostFaecherManager, GostJahrgang, GostJahrgangFachkombination, GostJahrgangsdaten, GostSchuelerFachwahl, List, SchuelerListeEintrag, Vector } from "@svws-nrw/svws-core-ts";
import { RouteLocationNormalized, RouteLocationRaw, RouteParams } from "vue-router";
import { RouteNode } from "~/router/RouteNode";
import { RouteSchueler, routeSchueler } from "~/router/apps/RouteSchueler";
import { routeLogin } from "~/router/RouteLogin";
import { shallowRef, ShallowRef } from "vue";

export class RouteDataSchuelerLaufbahnplanung {
	item: SchuelerListeEintrag | undefined = undefined;
	abiturdaten: Abiturdaten | undefined = undefined;
	_abiturdatenManager: ShallowRef<AbiturdatenManager | undefined> = shallowRef(undefined);
	_faecherManager: ShallowRef<GostFaecherManager | undefined> = shallowRef(undefined);
	_gostBelegpruefungsArt: ShallowRef<GostBelegpruefungsArt> = shallowRef(GostBelegpruefungsArt.GESAMT);
	_gostBelegpruefungErgebnis: ShallowRef<GostBelegpruefungErgebnis> = shallowRef(new GostBelegpruefungErgebnis());
	gostJahrgang: GostJahrgang = new GostJahrgang();
	gostJahrgangsdaten: GostJahrgangsdaten = new GostJahrgangsdaten();
	listGostFaecher: List<GostFach> = new Vector();
	mapFachkombinationen: Map<number, GostJahrgangFachkombination> | undefined = undefined;

	public async ladeFachkombinationen() {
		if (this.gostJahrgang === undefined) {
			return;
		}
	}

	get faechermanager(): GostFaecherManager {
		if (this._faecherManager.value === undefined)
			throw new Error("Unerwarteter Fehler: Fächer-Manager nicht initialisiert");
		return this._faecherManager.value;
	}

	set faecherManager(manager: GostFaecherManager | undefined) {
		this._faecherManager.value = manager;
	}

	get abiturdatenManager(): AbiturdatenManager {
		if (this._abiturdatenManager.value === undefined)
			throw new Error("Unerwarteter Fehler: Abiturdaten-Manager nicht initialisiert");
		return this._abiturdatenManager.value;
	}

	set abiturdatenManager(manager: AbiturdatenManager | undefined) {
		this._abiturdatenManager.value = manager;
	}

	setGostBelegpruefungsArt = async (value: GostBelegpruefungsArt) => {
		this._gostBelegpruefungsArt.value = value;
		this.setGostBelegpruefungErgebnis();
	}

	setGostBelegpruefungErgebnis = () => {
		if (this.abiturdaten === undefined)
			return;
		this._abiturdatenManager.value = new AbiturdatenManager(this.abiturdaten, this.listGostFaecher, this._gostBelegpruefungsArt.value);
		this._gostBelegpruefungErgebnis.value = this._abiturdatenManager.value.getBelegpruefungErgebnis();
	}

	setWahl = async (fachID: number, wahl: GostSchuelerFachwahl) => {
		if (this.item === undefined)
			return;
		await routeLogin.data.api.patchGostSchuelerFachwahl(wahl, routeLogin.data.schema, this.item.id, fachID);
		this.abiturdaten = await routeLogin.data.api.getGostSchuelerLaufbahnplanung(routeLogin.data.schema, this.item.id);
		this.setGostBelegpruefungErgebnis();
	}

	getPdfWahlbogen = async() => {
		if (this.item == undefined)
			throw Error("Keine Schülerauswahl zur Bestimmung des PDF-Wahlbogens vorhanden.");
		return await routeLogin.data.api.getGostSchuelerPDFWahlbogen(routeLogin.data.schema, this.item.id);
	}

}

const SSchuelerLaufbahnplanung = () => import("~/components/schueler/laufbahnplanung/SSchuelerLaufbahnplanung.vue");

export class RouteSchuelerLaufbahnplanung extends RouteNode<RouteDataSchuelerLaufbahnplanung, RouteSchueler> {

	public constructor() {
		super("schueler_laufbahnplanung", "laufbahnplanung", SSchuelerLaufbahnplanung, new RouteDataSchuelerLaufbahnplanung());
		super.propHandler = (route) => this.getProps(route);
		super.text = "Laufbahnplanung";
		super.isHidden = (params?: RouteParams) => {
			if (routeSchueler.item === undefined)
				return false;
			const abiturjahr = routeSchueler.item?.abiturjahrgang;
			const jahrgang = routeSchueler.data.listeAbiturjahrgaenge.liste.find(j => (j.abiturjahr === abiturjahr));
			return (jahrgang === undefined);
		}
	}

	public async update(to: RouteNode<unknown, any>, to_params: RouteParams) : Promise<any>  {
		if (to_params.id === undefined) {
			await this.onSelect(undefined);
		} else {
			const tmp = parseInt(to_params.id as string);
			const success = await this.onSelect(this.parent!.liste.liste.find(s => s.id === tmp));
			if (!success)
				return routeSchueler.getRoute(tmp);
		}
	}

	protected async onSelect(item?: SchuelerListeEintrag) : Promise<boolean> {
		if (item === this.data.item)
			return true;
		if (item === undefined) {
			this.data.item = undefined;
			this.data.abiturdaten = undefined;
			this.data.listGostFaecher.clear();
			this.data.gostJahrgang = new GostJahrgang();
			this.data.gostJahrgangsdaten = new GostJahrgangsdaten();
			this.data.faecherManager = undefined;
			this.data.mapFachkombinationen = undefined;
		} else {
			this.data.item = item;
			this.data.abiturdaten = await routeLogin.data.api.getGostSchuelerLaufbahnplanung(routeLogin.data.schema, item.id);
			if (this.data.abiturdaten === undefined)
				return false;
			if (this.data.item.abiturjahrgang !== null) {
				this.data.gostJahrgang.abiturjahr = this.data.item.abiturjahrgang;
				this.data.gostJahrgang.jahrgang = this.data.item.jahrgang;
				this.data.gostJahrgangsdaten = await routeLogin.data.api.getGostAbiturjahrgang(routeLogin.data.schema, this.data.gostJahrgang.abiturjahr);
				this.data.listGostFaecher = await routeLogin.data.api.getGostAbiturjahrgangFaecher(routeLogin.data.schema, this.data.gostJahrgang.abiturjahr);
				this.data.faecherManager = new GostFaecherManager(this.data.listGostFaecher);
				this.data.setGostBelegpruefungErgebnis();
				const listfachkombinationen	= await routeLogin.data.api.getGostAbiturjahrgangFachkombinationen(routeLogin.data.schema, this.data.gostJahrgang.abiturjahr);
				const mapFachkombinationen = new Map<number, GostJahrgangFachkombination>();
				for (const fk of listfachkombinationen)
					mapFachkombinationen.set(fk.id, fk);
				this.data.mapFachkombinationen = mapFachkombinationen;
			}
		}
		return true;
	}

	public getRoute(id: number) : RouteLocationRaw {
		return { name: this.name, params: { id: id }};
	}

	public getProps(to: RouteLocationNormalized): Record<string, any> {
		return {
			setWahl: this.data.setWahl,
			setGostBelegpruefungsArt: this.data.setGostBelegpruefungsArt,
			getPdfWahlbogen: this.data.getPdfWahlbogen,
			schueler: this.data.item,
			gostJahrgangsdaten: this.data.gostJahrgangsdaten,
			gostBelegpruefungsArt: this.data._gostBelegpruefungsArt.value,
			gostBelegpruefungErgebnis: this.data._gostBelegpruefungErgebnis.value,
			abiturdatenManager: this.data.abiturdatenManager,
			faechermanager: this.data.faechermanager,
			mapFachkombinationen: this.data.mapFachkombinationen
		};
	}

}

export const routeSchuelerLaufbahnplanung = new RouteSchuelerLaufbahnplanung();

