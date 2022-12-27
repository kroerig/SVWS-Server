import { computed, WritableComputedRef } from "vue";
import { RouteLocationNormalized } from "vue-router";
import { RouteNodeListView } from "../RouteNodeListView";

const SKatalogeAuswahl = () => import("~/components/kataloge/SKatalogeAuswahl.vue")
const SKatalogeApp = () => import("~/components/kataloge/SKatalogeApp.vue")

export class RouteKataloge extends RouteNodeListView<unknown, unknown> {

	protected defaultChildNode = undefined;

	public constructor() {
		super("kataloge", "/kataloge", SKatalogeAuswahl, SKatalogeApp);
		super.propHandler = (route) => this.getProps(route);
		super.text = "Kataloge";
		super.setView("liste", SKatalogeAuswahl, (route) => this.getNoProps(route));
		super.children = [
		];
	}

    protected getAuswahlComputedProperty(): WritableComputedRef<undefined> {
		// TODO
		return computed({ get(): undefined { return undefined; }, set(value: undefined) { }});
	}

	public getProps(to: RouteLocationNormalized): Record<string, any> {
		// TODO
		return { };
	}

}

export const routeKataloge = new RouteKataloge();
