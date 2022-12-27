import { RouteRecordRaw } from "vue-router";

import { routeSchule } from "~/router/apps/RouteSchule";
import { RouteSchuleBenutzerverwaltung } from "~/router/apps/RouteSchuleBenutzerverwaltung";
import { routeKataloge } from "~/router/apps/RouteKataloge";
import { RouteKatalogFaecher } from "~/router/apps/RouteKatalogFaecher";
import { RouteKatalogReligion } from "~/router/apps/RouteKatalogReligion";
import { RouteKatalogJahrgaenge } from "~/router/apps/RouteKatalogJahrgaenge";
import { routeSchueler } from "~/router/apps/RouteSchueler";
import { routeLehrer } from "~/router/apps/RouteLehrer";
import { RouteKlassen } from "~/router/apps/RouteKlassen";
import { routeKurse } from "~/router/apps/RouteKurse";
import { routeGost } from "~/router/apps/RouteGost";
import { routeStatistik } from "~/router/apps/RouteStatistik";

import SApp from "~/components/SApp.vue";


export const RouteApp : RouteRecordRaw = {
    path: "/",
    component: SApp,
    name: "app",
    children: [
        routeSchule.record,
        RouteSchuleBenutzerverwaltung,
        routeKataloge.record,
        RouteKatalogFaecher,
        RouteKatalogReligion,
        RouteKatalogJahrgaenge,
        routeSchueler.record,
        routeLehrer.record,
        RouteKlassen,
        routeKurse.record,
        routeGost.record,
        routeStatistik.record
    ],
    meta: {
        auth_required: false,   // gibt an, ob eine Authentifizierung für diese Seite benötigt wird
        schulformen: undefined  // undefined bedeutet, dass diese Seite bei allen Schulformen angezeigt wird
    }
}
