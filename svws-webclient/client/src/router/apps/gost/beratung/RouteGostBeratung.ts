import type { RouteLocationNormalized, RouteLocationRaw, RouteParams } from "vue-router";

import { BenutzerKompetenz, DeveloperNotificationException, Schulform, ServerMode } from "@core";

import { RouteNode } from "~/router/RouteNode";
import { routeGost, type RouteGost } from "~/router/apps/gost/RouteGost";

import type { GostBeratungProps } from "~/components/gost/beratung/SGostBeratungProps";
import { RouteDataGostBeratung } from "~/router/apps/gost/beratung/RouteDataGostBeratung";
import { routeError } from "~/router/error/RouteError";
import { routeApp } from "../../RouteApp";

const SGostBeratung = () => import("~/components/gost/beratung/SGostBeratung.vue");

export class RouteGostBeratung extends RouteNode<RouteDataGostBeratung, RouteGost> {

	public constructor() {
		super(Schulform.getMitGymOb(), [ BenutzerKompetenz.KEINE ], "gost.beratung", "beratung", SGostBeratung, new RouteDataGostBeratung());
		super.mode = ServerMode.STABLE;
		super.propHandler = (route) => this.getProps(route);
		super.text = "Beratung";
	}

	public async update(to: RouteNode<unknown, any>, to_params: RouteParams) : Promise<void | Error | RouteLocationRaw> {
		if (to_params.abiturjahr instanceof Array)
			return routeError.getRoute(new DeveloperNotificationException("Fehler: Die Parameter der Route dürfen keine Arrays sein"));
		if (this.parent === undefined)
			return routeError.getRoute(new DeveloperNotificationException("Fehler: Die Route ist ungültig - Parent ist nicht definiert"));
		if (to_params.abiturjahr === undefined)
			return routeError.getRoute(new DeveloperNotificationException("Fehler: Die Route ist ungültig - Ein Abiturjahrgang muss angegeben sein"));
		const abiturjahr = parseInt(to_params.abiturjahr);
		try {
			await this.data.ladeDaten(abiturjahr);
		} catch(error) {
			return routeError.getRoute(new DeveloperNotificationException("Fehler: Die Route ist ungültig - Fehler beim Laden der Daten"));
		}
	}


	public getRoute(abiturjahr: number) : RouteLocationRaw {
		return { name: this.name, params: { idSchuljahresabschnitt: routeApp.data.idSchuljahresabschnitt, abiturjahr }};
	}


	public getProps(to: RouteLocationNormalized): GostBeratungProps {
		return {
			patchJahrgangsdaten: routeGost.data.patchJahrgangsdaten,
			jahrgangsdaten: () => routeGost.data.jahrgangsdaten,
			setWahl: this.data.setWahl,
			setGostBelegpruefungsArt: this.data.setGostBelegpruefungsArt,
			gostBelegpruefungsArt: () => this.data.gostBelegpruefungsArt,
			gostBelegpruefungErgebnis: () => this.data.gostBelegpruefungErgebnis,
			abiturdatenManager: () => this.data.abiturdatenManager,
			mapLehrer: this.data.mapLehrer,
			id: this.data.id,
			resetFachwahlen: this.data.resetFachwahlen,
			resetFachwahlenAlle: this.data.resetFachwahlenAlle,
			beratungslehrer: () => this.data.beratungslehrer,
			addBeratungslehrer: this.data.addBeratungslehrer,
			removeBeratungslehrer: this.data.removeBeratungslehrer,
			gotoKursblockung: this.data.gotoKursblockung,
		};
	}

}

export const routeGostBeratung = new RouteGostBeratung();
