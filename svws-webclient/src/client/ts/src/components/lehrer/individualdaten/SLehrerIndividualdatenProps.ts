import type { LehrerStammdaten, OrtKatalogEintrag, OrtsteilKatalogEintrag } from "@svws-nrw/svws-core";

export interface LehrerIndividualdatenProps {
	patch: (data : Partial<LehrerStammdaten>) => Promise<void>;
	stammdaten: LehrerStammdaten;
	mapOrte: Map<number, OrtKatalogEintrag>;
	mapOrtsteile: Map<number, OrtsteilKatalogEintrag>;
}