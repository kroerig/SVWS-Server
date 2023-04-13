import type { KursListeEintrag, LehrerListeEintrag } from "@svws-nrw/svws-core";
import type { AuswahlChildData } from "../AuswahlChildData";

export interface KurseAppProps {
		auswahl: KursListeEintrag | undefined;
		mapLehrer: Map<number, LehrerListeEintrag>;
		setTab: (value: AuswahlChildData) => Promise<void>;
		tab: AuswahlChildData;
		tabs: AuswahlChildData[];
		tabsHidden: boolean[];
	}