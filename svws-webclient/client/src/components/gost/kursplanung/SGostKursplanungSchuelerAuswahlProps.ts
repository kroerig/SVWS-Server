import type { SchuelerListeEintrag, GostBlockungsergebnisManager, GostFaecherManager, GostBlockungsdatenManager, GostBlockungRegel, List, GostBlockungRegelUpdate } from "@core";
import type { GostKursplanungSchuelerFilter } from "./GostKursplanungSchuelerFilter";

export interface KursplanungSchuelerAuswahlProps {
	hatBlockung: boolean,
	hatErgebnis: boolean,
	setSchueler: (schueler: SchuelerListeEintrag) => Promise<void>;
	getErgebnismanager: () => GostBlockungsergebnisManager;
	getDatenmanager: () => GostBlockungsdatenManager;
	schueler: SchuelerListeEintrag | undefined;
	schuelerFilter: () => GostKursplanungSchuelerFilter;
	faecherManager: GostFaecherManager;
	addRegel: (regel: GostBlockungRegel) => Promise<GostBlockungRegel | undefined>;
	removeRegel: (id: number) => Promise<GostBlockungRegel | undefined>;
	regelnUpdate: (update: GostBlockungRegelUpdate) => Promise<void>;
	isSchuelerFilterOpen: () => boolean;
	setIsSchuelerFilterOpen: (value: boolean) => void;
	showGeschlecht: () => boolean;
	setShowGeschlecht: (value: boolean) => void;
}