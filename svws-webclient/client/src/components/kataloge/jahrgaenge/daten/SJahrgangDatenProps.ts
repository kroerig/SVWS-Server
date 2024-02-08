import type { JahrgangsDaten, JahrgangsListeEintrag } from "@core";

export interface JahrgangDatenProps {
	patch: (data : Partial<JahrgangsDaten>) => Promise<void>;
	data: () => JahrgangsDaten;
	mapJahrgaenge: Map<number, JahrgangsListeEintrag>;
}