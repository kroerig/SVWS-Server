import type { RouteLocationNormalized, RouteLocationRaw } from "vue-router";
import type { StundenplanZeitrasterPausenzeitProps } from "~/components/stundenplan/zeitrasterPausenzeit/SStundenplanZeitrasterPausenzeitProps";
import type { LehrerListeEintrag} from "@core";
import type { RouteKatalogZeitraster} from "~/router/apps/kataloge/zeitraster/RouteKatalogZeitraster";
import { ArrayList, BenutzerKompetenz, Schulform, ServerMode } from "@core";
import { RouteNode } from "~/router/RouteNode";
import { routeKatalogZeitraster } from "~/router/apps/kataloge/zeitraster/RouteKatalogZeitraster";
import { routeApp } from "../../RouteApp";

const SStundenplanZeitrasterPausenzeit = () => import("~/components/stundenplan/zeitrasterPausenzeit/SStundenplanZeitrasterPausenzeit.vue");

export class RouteKatalogZeitrasterDaten extends RouteNode<any, RouteKatalogZeitraster> {

	public constructor() {
		super(Schulform.values(), [ BenutzerKompetenz.KEINE ], "kataloge.zeitraster.daten", "daten", SStundenplanZeitrasterPausenzeit);
		super.mode = ServerMode.STABLE;
		super.propHandler = (route) => this.getProps(route);
		super.text = "Zeitraster";
	}

	public getRoute(id: number) : RouteLocationRaw {
		return { name: this.name, params: { idSchuljahresabschnitt: routeApp.data.idSchuljahresabschnitt }};
	}

	public getProps(to: RouteLocationNormalized): StundenplanZeitrasterPausenzeitProps {
		return {
			stundenplanManager: () => routeKatalogZeitraster.data.stundenplanManager,
			listLehrer: new ArrayList<LehrerListeEintrag>(),
			patchPausenzeit: async ()=>{},//routeKatalogZeitraster.data.patchPausenzeit,
			removePausenzeiten: async ()=>{},//routeKatalogZeitraster.data.removePausenzeiten,
			importPausenzeiten: async ()=>{},//routeKatalogZeitraster.data.importPausenzeiten,
			patchZeitraster: routeKatalogZeitraster.data.patchZeitraster,
			addZeitraster: routeKatalogZeitraster.data.addZeitraster,
			removeZeitraster: routeKatalogZeitraster.data.removeZeitraster,
			importZeitraster: undefined,//routeKatalogZeitraster.data.importZeitraster,
			selected: routeKatalogZeitraster.data.selected,
			setSelection: routeKatalogZeitraster.data.setSelection,
		};
	}

}

export const routeKatalogZeitrasterDaten = new RouteKatalogZeitrasterDaten();

