import type { LehrerListeEintrag, Schuljahresabschnitt } from "@core";

export interface LehrerAuswahlProps {
		auswahl: LehrerListeEintrag | undefined;
		mapLehrer: Map<number, LehrerListeEintrag>;
		gotoLehrer: (value: LehrerListeEintrag | undefined) => Promise<void>;
		abschnitte: Map<number, Schuljahresabschnitt>;
		aktAbschnitt: Schuljahresabschnitt;
		aktSchulabschnitt: number;
		setAbschnitt: (abschnitt: Schuljahresabschnitt) => void;
	}