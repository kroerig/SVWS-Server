
import type {
	GostHalbjahr,
	GostJahrgangsdaten,
	GostKlausurraumManager,
	GostKlausurtermin,
	GostKursklausurManager,
} from "@core";
import type { WritableComputedRef } from "vue";

export interface GostKlausurplanungNachschreibAnsichtProps {
	jahrgangsdaten: GostJahrgangsdaten;
	halbjahr: GostHalbjahr;
	kMan: () => GostKursklausurManager;
	erzeugeKlausurraummanager: (termin: GostKlausurtermin) => Promise<GostKlausurraumManager>;
	quartalsauswahl: WritableComputedRef<0 | 1 | 2>;
}
