import { JahrgangsListeEintrag } from "@svws-nrw/svws-core-ts";
import { computed, WritableComputedRef } from "vue";
import { RouteLocationNormalized, RouteParams, RouteRecordRaw, useRouter } from "vue-router";
import { mainApp } from "~/apps/Main";
import { RouteNodeListView } from "~/router/RouteNodeListView";
import { routeKatalogJahrgaengeDaten } from "~/router/apps/jahrgaenge/RouteKatalogJahrgaengeDaten";
import { ListJahrgaenge } from "~/apps/jahrgaenge/ListJahrgaenge";
import { RouteNode } from "~/router/RouteNode";
import { RouteApp } from "~/router/RouteApp";

export class RouteDataKatalogJahrgaenge {
	item: JahrgangsListeEintrag | undefined = undefined;
}

const SJahrgaengeAuswahl = () => import("~/components/jahrgaenge/SJahrgaengeAuswahl.vue")
const SJahrgaengeApp = () => import("~/components/jahrgaenge/SJahrgaengeApp.vue")

export class RouteKatalogJahrgaenge extends RouteNodeListView<JahrgangsListeEintrag, RouteDataKatalogJahrgaenge, RouteApp> {

	public constructor() {
		super("jahrgaenge", "/kataloge/jahrgaenge/:id(\\d+)?", SJahrgaengeAuswahl, SJahrgaengeApp, new RouteDataKatalogJahrgaenge());
		super.propHandler = (route) => this.getProps(route);
		super.text = "Jahrgänge";
        super.setView("liste", SJahrgaengeAuswahl, (route) => RouteNodeListView.getPropsByAuswahlID(route, mainApp.apps.jahrgaenge.auswahl));
		super.children = [
			routeKatalogJahrgaengeDaten
		];
		super.defaultChild = routeKatalogJahrgaengeDaten;
	}

    public async beforeEach(to: RouteNode<unknown, any>, to_params: RouteParams, from: RouteNode<unknown, any> | undefined, from_params: RouteParams): Promise<any> {
		if ((to.name === this.name) && (to_params.id === undefined)) {
			const redirect_name: string = (this.selectedChild === undefined) ? this.defaultChild!.name : this.selectedChild.name;
			return { name: redirect_name, params: { id: mainApp.apps.jahrgaenge.auswahl.liste.at(0)?.id }};
		}
        return true;
    }

	protected onSelect(item?: JahrgangsListeEintrag) {
		if (item === this.data.item)
			return;
		if (item === undefined) {
			this.data.item = undefined;
		} else {
			this.data.item = item;
		}
	}

    protected getAuswahlComputedProperty(): WritableComputedRef<JahrgangsListeEintrag | undefined> {
		return this.getSelectorByID<JahrgangsListeEintrag, ListJahrgaenge>(mainApp.apps.jahrgaenge.auswahl);
	}

	public getProps(to: RouteLocationNormalized): Record<string, any> {
		const prop = RouteNodeListView.getPropsByAuswahlID(to, mainApp.apps.jahrgaenge.auswahl);
		this.onSelect(prop.item as JahrgangsListeEintrag | undefined);
		return prop;
	}

    /**
     * TODO
     * 
     * @returns 
     */
    public getChildRouteSelector() {
        const router = useRouter();
        const selectedRoute: WritableComputedRef<RouteRecordRaw> = computed({
            get: () => this.selectedChildRecord || this.defaultChild!.record,
            set: (value) => {
                this.selectedChildRecord = value;
				const id = (this.data.item === undefined) ? undefined : "" + this.data.item.id;
                router.push({ name: value.name, params: { id: id } });
            }
        });
        return selectedRoute;
    }

}

export const routeKatalogJahrgaenge = new RouteKatalogJahrgaenge();
