import { FoerderschwerpunktEintrag } from "@svws-nrw/svws-core-ts";
import { computed, WritableComputedRef } from "vue";
import { RouteLocationNormalized, RouteParams, RouteRecordRaw, useRouter } from "vue-router";
import { RouteNodeListView } from "~/router/RouteNodeListView";
import { routeKatalogFoerderschwerpunkteDaten } from "~/router/apps/foerderschwerpunkte/RouteKatalogFoerderschwerpunkteDaten";
import { ListFoerderschwerpunkte } from "~/apps/kataloge/foerderschwerpunkt/ListFoerderschwerpunkte";
import { RouteNode } from "~/router/RouteNode";
import { RouteApp } from "~/router/RouteApp";


export class RouteDataKatalogFoerderschwerpunkte {
	item: FoerderschwerpunktEintrag | undefined = undefined;
	auswahl: ListFoerderschwerpunkte = new ListFoerderschwerpunkte();
}

const SFoerderschwerpunkteAuswahl = () => import("~/components/kataloge/foerderschwerpunkte/SFoerderschwerpunkteAuswahl.vue")
const SFoerderschwerpunkteApp = () => import("~/components/kataloge/foerderschwerpunkte/SFoerderschwerpunkteApp.vue")

export class RouteKatalogFoerderschwerpunkte extends RouteNodeListView<FoerderschwerpunktEintrag, RouteDataKatalogFoerderschwerpunkte, RouteApp> {

	public constructor() {
		super("foerderschwerpunkte", "/kataloge/foerderschwerpunkte/:id(\\d+)?", SFoerderschwerpunkteAuswahl, SFoerderschwerpunkteApp, new RouteDataKatalogFoerderschwerpunkte());
		super.propHandler = (route) => this.getProps(route);
		super.text = "Förderschwerpunkte";
        super.setView("liste", SFoerderschwerpunkteAuswahl, (route) => RouteNodeListView.getPropsByAuswahlID(route, this.data.auswahl));
		super.children = [
			routeKatalogFoerderschwerpunkteDaten
		];
		super.defaultChild = routeKatalogFoerderschwerpunkteDaten;
	}

    public async beforeEach(to: RouteNode<unknown, any>, to_params: RouteParams, from: RouteNode<unknown, any> | undefined, from_params: RouteParams): Promise<any> {
		if ((to.name === this.name) && (to_params.id === undefined)) {
			const redirect_name: string = (this.selectedChild === undefined) ? this.defaultChild!.name : this.selectedChild.name;
			return { name: redirect_name, params: { id: this.data.auswahl.liste.at(0)?.id }};
		}
        return true;
    }

    public async enter(to: RouteNode<unknown, any>, to_params: RouteParams) {
		await this.data.auswahl.update_list();
	}

	protected onSelect(item?: FoerderschwerpunktEintrag) {
		if (item === this.data.item)
			return;
		if (item === undefined) {
			this.data.item = undefined;
		} else {
			this.data.item = item;
		}
	}

    protected getAuswahlComputedProperty(): WritableComputedRef<FoerderschwerpunktEintrag | undefined> {
		return this.getSelectorByID<FoerderschwerpunktEintrag, ListFoerderschwerpunkte>(this.data.auswahl);
	}

	public getProps(to: RouteLocationNormalized): Record<string, any> {
		const prop = RouteNodeListView.getPropsByAuswahlID(to, this.data.auswahl);
		this.onSelect(prop.item as FoerderschwerpunktEintrag | undefined);
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

export const routeKatalogFoerderschwerpunkte = new RouteKatalogFoerderschwerpunkte();
