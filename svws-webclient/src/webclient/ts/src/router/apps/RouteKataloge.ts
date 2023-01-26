import { computed, WritableComputedRef } from "vue";
import { RouteLocationNormalized, RouteLocationRaw } from "vue-router";
import { RouteApp } from "~/router/RouteApp";
import { RouteNodeListView } from "~/router/RouteNodeListView";
import { routeKatalogFaecher } from "~/router/apps/RouteKatalogFaecher";
import { routeKatalogFoerderschwerpunkte } from "~/router/apps/RouteKatalogFoerderschwerpunkte";
import { routeKatalogJahrgaenge } from "~/router/apps/RouteKatalogJahrgaenge";
import { routeKatalogReligion } from "~/router/apps/RouteKatalogReligion";
import { ListNone } from "~/apps/ListNone";

const SKatalogeAuswahl = () => import("~/components/kataloge/SKatalogeAuswahl.vue")
const SKatalogeApp = () => import("~/components/kataloge/SKatalogeApp.vue")

export class RouteKataloge extends RouteNodeListView<ListNone, unknown, unknown, RouteApp> {

	public constructor() {
		super("kataloge", "/kataloge", SKatalogeAuswahl, SKatalogeApp);
		super.propHandler = (route) => this.getProps(route);
		super.text = "Kataloge";
		super.setView("liste", SKatalogeAuswahl, (route) => this.getNoProps(route));
		super.children = [
		];
		super.menu = [
			routeKatalogFaecher,
			routeKatalogReligion,
			routeKatalogJahrgaenge,
			routeKatalogFoerderschwerpunkte
			// TODO { title: "Haltestellen", value: "haltestellen" },
			// TODO { title: "Betriebe", value: "betriebe" }
		];
		super.defaultChild = routeKatalogFaecher;
	}

	protected getAuswahlComputedProperty(): WritableComputedRef<undefined> {
		// TODO
		return computed({ get(): undefined { return undefined; }, set(value: undefined) { }});
	}

	public getRoute(id: number) : RouteLocationRaw {
		return { name: this.defaultChild!.name, params: { id: id }};
	}

	public getProps(to: RouteLocationNormalized): Record<string, any> {
		// TODO
		return { };
	}

}

export const routeKataloge = new RouteKataloge();
