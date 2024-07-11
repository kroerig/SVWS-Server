import type { RouteLocationNormalized, RouteLocationRaw, RouteParams } from "vue-router";

import { BenutzerKompetenz, DeveloperNotificationException, Schulform, ServerMode } from "@core";

import { RouteManager } from "~/router/RouteManager";
import { RouteNode } from "~/router/RouteNode";

import type { RouteApp } from "~/router/apps/RouteApp";
import { routeApp } from "~/router/apps/RouteApp";
import { routeKataloge } from "~/router/apps/kataloge/RouteKataloge";
import { routeKatalogEinwilligungsartenDaten } from "~/router/apps/kataloge/einwilligungsarten/RouteKatalogEinwilligungsartenDaten";

import type { AuswahlChildData } from "~/components/AuswahlChildData";
import type { SEinwilligungsartenAppProps } from "~/components/kataloge/einwilligungsarten/SEinwilligungsartenAppProps";
import type { SEinwilligungsartenAuswahlProps } from "~/components/kataloge/einwilligungsarten/SEinwilligungsartenAuswahlProps";
import { RouteDataKatalogEinwilligungsarten } from "./RouteDataKatalogEinwilligungsarten";

const SEinwilligungsartenAuswahl = () => import("~/components/kataloge/einwilligungsarten/SEinwilligungsartenAuswahl.vue")
const SEinwilligungsartenApp = () => import("~/components/kataloge/einwilligungsarten/SEinwilligungsartenApp.vue")

export class RouteKatalogEinwilligungsarten extends RouteNode<RouteDataKatalogEinwilligungsarten, RouteApp> {

	public constructor() {
		super(Schulform.values(), [BenutzerKompetenz.KEINE], "kataloge.einwilligungsarten", "kataloge/einwilligungsarten/:id(\\d+)?", SEinwilligungsartenApp, new RouteDataKatalogEinwilligungsarten());
		super.mode = ServerMode.DEV;
		super.propHandler = (route) => this.getProps(route);
		super.text = "Einwilligungsarten";
		super.setView("liste", SEinwilligungsartenAuswahl, (route) => this.getAuswahlProps(route));
		super.children = [
			routeKatalogEinwilligungsartenDaten
		];
		super.defaultChild = routeKatalogEinwilligungsartenDaten;
	}

	protected async update(to: RouteNode<any, any>, to_params: RouteParams, from: RouteNode<any, any> | undefined, from_params: RouteParams, isEntering: boolean): Promise<void | Error | RouteLocationRaw> {
		if (isEntering)
			await this.data.ladeListe();
		if (to_params.id instanceof Array)
			throw new DeveloperNotificationException("Fehler: Die Parameter der Route dürfen keine Arrays sein");
		if (to_params.id === undefined) {
			await this.data.ladeListe();
		} else {
			const id = parseInt(to_params.id);
			const eintrag = this.data.mapKatalogeintraege.get(id);
			if ((eintrag === undefined) && (this.data.auswahl !== undefined)) {
				await this.data.ladeListe();
				return this.getRoute(this.data.auswahl.id);
			} else if (eintrag)
				this.data.setEintrag(eintrag);
		}
		if ((to.name === this.name) && (this.data.auswahl !== undefined))
			return this.getRoute(this.data.auswahl.id);
	}

	public getRoute(id: number | undefined): RouteLocationRaw {
		return { name: this.defaultChild!.name, params: { idSchuljahresabschnitt: routeApp.data.idSchuljahresabschnitt, id: id } };
	}

	public getAuswahlProps(to: RouteLocationNormalized): SEinwilligungsartenAuswahlProps {
		return {
			schuljahresabschnittsauswahl: () => routeApp.data.getSchuljahresabschnittsauswahl(false),
			auswahl: () => this.data.auswahl,
			mapKatalogeintraege: this.data.mapKatalogeintraege,
			addEintrag: this.data.addEintrag,
			deleteEintraege: this.data.deleteEintraege,
			gotoEintrag: this.data.gotoEintrag,
			returnToKataloge: routeKataloge.returnToKataloge
		};
	}

	public getProps(to: RouteLocationNormalized): SEinwilligungsartenAppProps {
		return {
			auswahl: () => this.data.auswahl,
			// Props für die Navigation
			setTab: this.setTab,
			tab: this.getTab(),
			tabs: this.getTabs(),
			tabsHidden: this.children_hidden().value,
		};
	}

	private getTab(): AuswahlChildData {
		return {name: this.data.view.name, text: this.data.view.text};
	}

	private getTabs(): AuswahlChildData[] {
		const result: AuswahlChildData[] = [];
		for (const c of super.children)
			if (c.hatEineKompetenz() && c.hatSchulform())
				result.push({name: c.name, text: c.text});
		return result;
	}

	private setTab = async (value: AuswahlChildData) => {
		if (value.name === this.data.view.name)
			return;
		const node = RouteNode.getNodeByName(value.name);
		if (node === undefined)
			throw new DeveloperNotificationException("Unbekannte Route");
		await RouteManager.doRoute({ name: value.name, params: { idSchuljahresabschnitt: routeApp.data.idSchuljahresabschnitt, id: this.data.auswahl?.id } });
		this.data.setView(node, this.children);
	}
}

export const routeKatalogEinwilligungsarten = new RouteKatalogEinwilligungsarten();
