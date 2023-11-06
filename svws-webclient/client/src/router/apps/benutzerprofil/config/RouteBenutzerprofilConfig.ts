import type { RouteLocationNormalized, RouteLocationRaw } from "vue-router";
import type { BenutzerprofilConfigProps } from "~/components/benutzerprofil/config/SBenutzerProfilConfigProps";
import type { RouteBenutzerprofil} from "../RouteBenutzerprofil";
import { routeBenutzerprofil } from "../RouteBenutzerprofil";
import { BenutzerKompetenz, Schulform, ServerMode } from "@core";
import { RouteNode } from "~/router/RouteNode";

const SBenutzerprofilConfig = () => import("~/components/benutzerprofil/config/SBenutzerprofilConfig.vue");

export class RouteBenutzerprofilConfig extends RouteNode<unknown, RouteBenutzerprofil> {

	public constructor() {
		super(Schulform.values(), [ BenutzerKompetenz.KEINE ], "benutzerprofil.config", "config", SBenutzerprofilConfig);
		super.mode = ServerMode.ALPHA;
		super.propHandler = (route) => this.getProps(route);
		super.text = "Benutzereinstellungen";
	}

	public getRoute(id: number) : RouteLocationRaw {
		return { name: this.name };
	}

	public getProps(to: RouteLocationNormalized): BenutzerprofilConfigProps {
		return {
			benutzer: routeBenutzerprofil.data.benutzer,
			patch: routeBenutzerprofil.data.patch,
		};
	}

}

export const routeBenutzerprofilConfig = new RouteBenutzerprofilConfig();

