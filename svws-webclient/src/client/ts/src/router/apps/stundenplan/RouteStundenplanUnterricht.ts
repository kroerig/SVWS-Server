import { BenutzerKompetenz, Schulform } from "@core";
import type { RouteLocationNormalized, RouteLocationRaw, RouteParams } from "vue-router";
import { RouteNode } from "~/router/RouteNode";
import type { RouteStundenplan} from "../RouteStundenplan";

const SStundenplanUnterricht = () => import("~/components/stundenplan/unterricht/SStundenplanUnterricht.vue");

export class RouteStundenplanUnterricht extends RouteNode<unknown, RouteStundenplan> {

	public constructor() {
		super(Schulform.values(), [ BenutzerKompetenz.KEINE ], "stundenplan.unterricht", "unterricht", SStundenplanUnterricht);
		super.propHandler = (route) => this.getProps(route);
		super.text = "Unterricht";
	}

	public async update(to: RouteNode<unknown, any>, to_params: RouteParams): Promise<any> {
	}

	public getRoute(id: number) : RouteLocationRaw {
		return { name: this.name, params: { id }};
	}

	public getProps(to: RouteLocationNormalized): Record<string, any> {
		return {
		};
	}

}

export const routeStundenplanUnterricht = new RouteStundenplanUnterricht();

