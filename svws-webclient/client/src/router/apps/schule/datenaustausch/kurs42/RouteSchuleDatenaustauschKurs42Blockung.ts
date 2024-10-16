import type { RouteLocationNormalized, RouteLocationRaw } from "vue-router";

import { BenutzerKompetenz, Schulform, ServerMode } from "@core";

import { RouteNode } from "~/router/RouteNode";
import { type RouteSchuleDatenaustauschKurs42 } from "~/router/apps/schule/datenaustausch/kurs42/RouteSchuleDatenaustauschKurs42";

import type { SchuleDatenaustauschKurs42BlockungProps } from "~/components/schule/datenaustausch/kurs42/SSchuleDatenaustauschKurs42BlockungProps";
import { routeApp } from "../../../RouteApp";
import { routeSchuleDatenaustauschKurs42 } from "./RouteSchuleDatenaustauschKurs42";

const SSchuleDatenaustauschKurs42Blockung = () => import("~/components/schule/datenaustausch/kurs42/SSchuleDatenaustauschKurs42Blockung.vue");

export class RouteSchuleDatenaustauschKurs42Blockung extends RouteNode<unknown, RouteSchuleDatenaustauschKurs42> {

	public constructor() {
		super(Schulform.values(), [ BenutzerKompetenz.KEINE ], "schule.datenaustausch.kurs42.blockung", "blockung", SSchuleDatenaustauschKurs42Blockung);
		super.mode = ServerMode.STABLE;
		super.propHandler = (route) => this.getProps(route);
		super.text = "Kurs42 Blockung";
	}

	public getRoute() : RouteLocationRaw {
		return { name: this.name, params: { idSchuljahresabschnitt: routeApp.data.idSchuljahresabschnitt }};
	}

	public getProps(to: RouteLocationNormalized): SchuleDatenaustauschKurs42BlockungProps {
		return {
			setGostKurs42ImportZip: routeSchuleDatenaustauschKurs42.data.setGostKurs42ImportZip,
		};
	}
}

export const routeSchuleDatenaustauschKurs42Blockung = new RouteSchuleDatenaustauschKurs42Blockung();

