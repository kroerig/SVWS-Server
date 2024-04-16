import { ArrayList, StundenplanKomplett, StundenplanManager, UserNotificationException, StundenplanPausenzeit, DeveloperNotificationException } from "@core";

import { api } from "~/router/Api";
import { RouteData, type RouteStateInterface } from "~/router/RouteData";
import { RouteManager } from "~/router/RouteManager";

import { routeKatalogPausenzeitDaten } from "./RouteKatalogPausenzeitDaten";
import { routeKatalogPausenzeiten } from "./RouteKatalogPausenzeiten";

interface RouteStateKatalogPausenzeiten extends RouteStateInterface {
	auswahl: StundenplanPausenzeit | undefined;
	stundenplanManager: StundenplanManager | undefined;
}

const defaultState = <RouteStateKatalogPausenzeiten> {
	auswahl: undefined,
	stundenplanManager: undefined,
	view: routeKatalogPausenzeitDaten,
};

export class RouteDataKatalogPausenzeiten extends RouteData<RouteStateKatalogPausenzeiten> {

	public constructor() {
		super(defaultState);
	}

	get auswahl(): StundenplanPausenzeit | undefined {
		return this._state.value.auswahl;
	}

	get stundenplanManager(): StundenplanManager {
		if (this._state.value.stundenplanManager === undefined)
			throw new DeveloperNotificationException("Unerwarteter Fehler: Stundenplandaten nicht initialisiert");
		return this._state.value.stundenplanManager;
	}

	public async ladeListe() {
		const listKatalogeintraege = await api.server.getPausenzeiten(api.schema);
		const auswahl = listKatalogeintraege.size() > 0 ? listKatalogeintraege.get(0) : undefined;
		const stundenplanKomplett = new StundenplanKomplett();
		stundenplanKomplett.daten.gueltigAb = '1999-01-01';
		stundenplanKomplett.daten.gueltigBis = '2999-01-01';
		const stundenplanManager = new StundenplanManager(stundenplanKomplett);
		stundenplanManager.pausenzeitAddAll(listKatalogeintraege);
		this.setPatchedDefaultState({ auswahl, stundenplanManager })
	}

	setEintrag = async (auswahl: StundenplanPausenzeit) => this.setPatchedState({ auswahl });

	gotoEintrag = async (eintrag: StundenplanPausenzeit) => await RouteManager.doRoute(routeKatalogPausenzeiten.getRoute(eintrag.id));

	addEintraege = async (eintraege: Iterable<Partial<StundenplanPausenzeit>>) => {
		const list = new ArrayList<Partial<StundenplanPausenzeit>>();
		for (const eintrag of eintraege) {
			if (!eintrag.wochentag || !eintrag.beginn || !eintrag.ende || this.stundenplanManager.pausenzeitExistsByWochentagAndBeginnAndEnde(eintrag.wochentag, eintrag.beginn, eintrag.ende))
				throw new UserNotificationException('Eine Pausenzeit existiert bereits an diesem Tag und zu dieser Zeit');
			delete eintrag.id;
			list.add(eintrag);
		}
		if (list.isEmpty())
			return;
		const pausenzeiten = await api.server.addPausenzeiten(list, api.schema);
		const stundenplanManager = this.stundenplanManager;
		stundenplanManager.pausenzeitAddAll(pausenzeiten);
		this.setPatchedState({stundenplanManager});
		await this.gotoEintrag(pausenzeiten.get(0));
	}

	deleteEintraege = async (eintraege: Iterable<StundenplanPausenzeit>) => {
		const stundenplanManager = this.stundenplanManager;
		const list = new ArrayList<number>();
		for (const eintrag of eintraege)
			list.add(eintrag.id);
		if (list.isEmpty())
			return;
		const pausenzeiten = await api.server.deletePausenzeiten(list, api.schema);
		stundenplanManager.pausenzeitRemoveAll(pausenzeiten);
		const auswahl = stundenplanManager.pausenzeitGetMengeAsList().getFirst() || undefined;
		this.setPatchedState({auswahl, stundenplanManager});
	}

	patch = async (eintrag : Partial<StundenplanPausenzeit>) => {
		if (this.auswahl === undefined)
			throw new DeveloperNotificationException("Beim Aufruf der Patch-Methode sind keine gültigen Daten geladen.");
		await api.server.patchPausenzeit(eintrag, api.schema, this.auswahl.id);
		const auswahl = this.auswahl;
		Object.assign(auswahl, eintrag);
		this.stundenplanManager.pausenzeitPatchAttributes(auswahl);
		this.commit();
	}

	setKatalogRaeumeImportJSON = api.call(async (formData: FormData) => {
		const jsonFile = formData.get("data");
		if (!(jsonFile instanceof File))
			return;
		const json = await jsonFile.text();
		const pausenzeiten: Partial<StundenplanPausenzeit>[] = JSON.parse(json);
		const list = new ArrayList<Partial<StundenplanPausenzeit>>();
		for (const item of pausenzeiten)
			if (item.wochentag && item.beginn && item.ende && !this.stundenplanManager.pausenzeitExistsByWochentagAndBeginnAndEnde(item.wochentag, item.beginn, item.ende)) {
				// Muss nach JSON umgewandelt werden und zurück nach Pausenzeit, weil das reguläre JSON.parse ein Array als Array einliest.
				const p = JSON.stringify(item);
				const pp: Partial<StundenplanPausenzeit> = StundenplanPausenzeit.transpilerFromJSON(p);
				delete pp.id;
				delete pp.klassen;
				list.add(pp);
			}
		if (list.isEmpty())
			return;
		const res = await api.server.addPausenzeiten(list, api.schema);
		this.stundenplanManager.pausenzeitAddAll(res);
		this.setPatchedState({stundenplanManager: this.stundenplanManager});
	})
}