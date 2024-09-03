import type { RouteLocationNormalized, RouteLocationRaw, RouteParams } from "vue-router";

import { BenutzerKompetenz, GostKlausurplanManager, Schulform, ServerMode } from "@core";

import { RouteNode } from "~/router/RouteNode";
import { routeGostKlausurplanung, type RouteGostKlausurplanung } from "~/router/apps/gost/klausurplanung/RouteGostKlausurplanung";
import type { GostKlausurplanungDetailAnsichtProps } from "~/components/gost/klausurplanung/SGostKlausurplanungDetailAnsichtProps";
import SGostKlausurplanungDetailAnsichtVue from "~/components/gost/klausurplanung/SGostKlausurplanungDetailAnsicht.vue";
import { routeApp } from "../../RouteApp";

export class RouteGostKlausurplanungDetailAnsicht extends RouteNode<any, RouteGostKlausurplanung> {

	public constructor() {
		super(Schulform.getMitGymOb(), [
			BenutzerKompetenz.OBERSTUFE_KLAUSURPLANUNG_ANSEHEN_ALLGEMEIN,
			BenutzerKompetenz.OBERSTUFE_KLAUSURPLANUNG_ANSEHEN_FUNKTION,
		], "gost.klausurplanung.detailansicht", "detailansicht", SGostKlausurplanungDetailAnsichtVue);
		super.mode = ServerMode.STABLE;
		super.propHandler = (route) => this.getProps(route);
		super.text = "Detailplan";
		this.isHidden = (params?: RouteParams) => {
			return this.checkHidden(params);
		}
	}

	public checkHidden(params?: RouteParams) {
		if (!routeGostKlausurplanung.data.manager.getStundenplanManagerOrNull())
			return { name: routeGostKlausurplanung.defaultChild!.name, params };
		return false;
	}

	public getRoute(abiturjahr: number, halbjahr: number) : RouteLocationRaw {
		return { name: this.name, params: { idSchuljahresabschnitt: routeApp.data.idSchuljahresabschnitt, abiturjahr: abiturjahr, halbjahr: halbjahr }};
	}

	public getProps(to: RouteLocationNormalized): GostKlausurplanungDetailAnsichtProps {
		return {
			jahrgangsdaten: routeGostKlausurplanung.data.jahrgangsdaten,
			halbjahr: routeGostKlausurplanung.data.halbjahr,
			kMan: () => routeGostKlausurplanung.data.manager,
			quartalsauswahl: routeGostKlausurplanung.data.quartalsauswahl,
		}
	}

}

export const routeGostKlausurplanungDetailAnsicht = new RouteGostKlausurplanungDetailAnsicht();

