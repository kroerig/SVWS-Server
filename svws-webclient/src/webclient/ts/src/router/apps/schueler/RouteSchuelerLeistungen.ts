import { RouteLocationRaw, RouteParams } from "vue-router";
import { RouteNode } from "~/router/RouteNode";
import { RouteSchueler } from "~/router/apps/RouteSchueler";
import { routeSchuelerLeistungenDaten } from "~/router/apps/schueler/leistungsdaten/RouteSchuelerLeistungenDaten";
import { List, SchuelerLernabschnittListeEintrag, Vector } from "@svws-nrw/svws-core-ts";
import { routeApp } from "~/router/RouteApp";
import { App } from "~/apps/BaseApp";

export class RouteDataSchuelerLeistungenDaten {

	idSchueler: number | undefined;
	listAbschnitte: List<SchuelerLernabschnittListeEintrag> = new Vector<SchuelerLernabschnittListeEintrag>();

	public getEntry(idSchuljahresabschnitt: number, wechselNr: number | null) : SchuelerLernabschnittListeEintrag | undefined {
		for (const current of this.listAbschnitte)
			if ((current.schuljahresabschnitt === idSchuljahresabschnitt) && (current.wechselNr === wechselNr))
				return current;
		return undefined;
	}

	public getEntryDefault() : SchuelerLernabschnittListeEintrag | undefined {
		if (routeApp.data.schule.daten === undefined)
			throw new Error("Keine Daten für die Schule geladen!");
		const entry = this.getEntry(routeApp.data.schule.daten.idSchuljahresabschnitt, null);
		if (entry !== undefined)
			return entry;
		if (this.listAbschnitte.size() > 0)
			return this.listAbschnitte.get(0);
		return undefined;
	}

}

const SSchuelerLeistungen = () => import("~/components/schueler/leistungsdaten/SSchuelerLeistungen.vue");

export class RouteSchuelerLeistungen extends RouteNode<RouteDataSchuelerLeistungenDaten, RouteSchueler> {

	public constructor() {
		super("schueler_leistungen", "leistungsdaten", SSchuelerLeistungen, new RouteDataSchuelerLeistungenDaten());
		super.propHandler = (route) => this.getNoProps(route);
		super.text = "Leistungsdaten";
		super.children = [
			routeSchuelerLeistungenDaten
		];
		super.defaultChild = routeSchuelerLeistungenDaten;
	}

	public async beforeEach(to: RouteNode<unknown, any>, to_params: RouteParams, from: RouteNode<unknown, any> | undefined, from_params: RouteParams): Promise<any> {
		return true;
	}

	protected async update(to: RouteNode<unknown, any>, to_params: RouteParams): Promise<any> {
		if (to_params.id === undefined)
			return false;
		const id = parseInt(to_params.id as string);
		if ((this.data.idSchueler !== id) || (to_params.abschnitt === undefined)) {
			const liste = await App.api.getSchuelerLernabschnittsliste(App.schema, id);
			if (liste.size() <= 0)
				return false;
			this.data.idSchueler = id;
			this.data.listAbschnitte = liste;
			if (to_params.abschnitt !== undefined) {
				const abschnitt = parseInt(to_params.abschnitt as string);
				const wechselNr = (to_params.wechselNr === undefined) || (to_params.wechselNr === "") ? null : parseInt(to_params.wechselNr as string);
				const lernabschnitt = this.data.getEntry(abschnitt, wechselNr);
				if (lernabschnitt !== undefined)
					return routeSchuelerLeistungenDaten.getRoute(id, lernabschnitt.schuljahresabschnitt, lernabschnitt.wechselNr === null ? undefined : lernabschnitt.wechselNr);
				if (wechselNr !== null) {
					const lernabschnitt = this.data.getEntry(abschnitt, null);
					if (lernabschnitt !== undefined)
						return routeSchuelerLeistungenDaten.getRoute(id, lernabschnitt.schuljahresabschnitt, lernabschnitt.wechselNr === null ? undefined : lernabschnitt.wechselNr);
				}
			}
			const lernabschnitt = this.data.getEntryDefault();
			if (lernabschnitt === undefined)
				return false;
			return routeSchuelerLeistungenDaten.getRoute(id, lernabschnitt.schuljahresabschnitt, lernabschnitt.wechselNr === null ? undefined : lernabschnitt.wechselNr);
		}
	}

	public getRoute(id: number) : RouteLocationRaw {
		return { name: this.name, params: { id: id }};
	}

}

export const routeSchuelerLeistungen = new RouteSchuelerLeistungen();

