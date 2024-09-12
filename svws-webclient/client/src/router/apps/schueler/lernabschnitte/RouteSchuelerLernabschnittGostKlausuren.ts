import type { RouteLocationNormalized, RouteLocationRaw, RouteParams } from "vue-router";

import { BenutzerKompetenz, DeveloperNotificationException, JahrgangsUtils, ServerMode } from "@core";

import { RouteNode } from "~/router/RouteNode";
import { routeError } from "~/router/error/RouteError";
import { routeSchueler } from "~/router/apps/schueler/RouteSchueler";
import { routeSchuelerLernabschnitte, type RouteSchuelerLernabschnitte } from "~/router/apps/schueler/lernabschnitte/RouteSchuelerLernabschnitte";

import type { SchuelerLernabschnittGostKlausurenProps } from "~/components/schueler/lernabschnitte/gostKlausuren/SSchuelerLernabschnittGostKlausurenProps";
import { api } from "~/router/Api";
import { routeApp } from "../../RouteApp";
import { schulformenGymOb } from "~/router/RouteHelper";
import { routeSchuelerLernabschnittAllgemein } from "./RouteSchuelerLernabschnittAllgemein";

const SSchuelerLernabschnittGostKlausuren = () => import("~/components/schueler/lernabschnitte/gostKlausuren/SSchuelerLernabschnittGostKlausuren.vue");

export class RouteSchuelerLernabschnittGostKlausuren extends RouteNode<any, RouteSchuelerLernabschnitte> {

	public constructor() {
		super(schulformenGymOb, [
			BenutzerKompetenz.OBERSTUFE_KLAUSURPLANUNG_ANSEHEN_ALLGEMEIN,
			BenutzerKompetenz.OBERSTUFE_KLAUSURPLANUNG_ANSEHEN_FUNKTION
		], "schueler.lernabschnitt.gostklausuren", "gostklausuren", SSchuelerLernabschnittGostKlausuren);
		super.mode = ServerMode.STABLE;
		super.propHandler = (route) => this.getProps(route);
		super.text = "Klausuren";
		super.children = [
		];
		this.isHidden = (params?: RouteParams) => {
			if ((params === undefined) || (params.id === undefined) || (params.id instanceof Array) || (params.abschnitt === undefined) || (params.abschnitt instanceof Array) || (params.wechselNr === undefined) || (params.wechselNr instanceof Array))
				return routeError.getRoute(new DeveloperNotificationException("Fehler: Die Parameter der Route sind nicht gültig gesetzt."));
			if (!routeSchueler.data.schuelerListeManager.hasDaten()) {
				return false;
			}
			const abiturjahr = routeSchueler.data.schuelerListeManager.auswahl().abiturjahrgang;
			if (((abiturjahr !== null) && routeSchueler.data.schuelerListeManager.abiturjahrgaenge.get(abiturjahr))
				&& (api.benutzerHatKompetenz(BenutzerKompetenz.OBERSTUFE_KLAUSURPLANUNG_ANSEHEN_ALLGEMEIN)
					|| (api.benutzerHatKompetenz(BenutzerKompetenz.OBERSTUFE_KLAUSURPLANUNG_ANSEHEN_FUNKTION) && api.benutzerKompetenzenAbiturjahrgaenge.has(abiturjahr)))) {
				return false;
			}
			return routeSchuelerLernabschnittAllgemein.getRoute(parseInt(params.id), parseInt(params.abschnitt), parseInt(params.wechselNr));
		}
	}

	protected async update(to: RouteNode<any, any>, to_params: RouteParams) : Promise<void | Error | RouteLocationRaw> {
	}

	public getRoute(id: number, abschnitt: number, wechselNr: number) : RouteLocationRaw {
		return { name: this.name, params: { idSchuljahresabschnitt: routeApp.data.idSchuljahresabschnitt, id: id, abschnitt: abschnitt, wechselNr: wechselNr }};
	}

	public getProps(to: RouteLocationNormalized): SchuelerLernabschnittGostKlausurenProps {
		return {
			schule: api.schuleStammdaten,
			manager: () => routeSchuelerLernabschnitte.data.manager,
			kMan: () => routeSchuelerLernabschnitte.data.klausurManager,
			hatKlausurManager: () => routeSchuelerLernabschnitte.data.hatKlausurManager,
			createSchuelerklausurTermin: routeSchuelerLernabschnitte.data.createSchuelerklausurTermin,
			deleteSchuelerklausurTermin: routeSchuelerLernabschnitte.data.deleteSchuelerklausurTermin,
			patchSchuelerklausurTermin: routeSchuelerLernabschnitte.data.patchSchuelerklausurTermin,
			gotoPlanung: routeSchuelerLernabschnitte.data.gotoPlanung,
		};
	}

}

export const routeSchuelerLernabschnittGostKlausuren = new RouteSchuelerLernabschnittGostKlausuren();

