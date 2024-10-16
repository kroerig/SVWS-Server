import type { ReligionEintrag, Schuljahresabschnitt } from "@core";

export interface ReligionenAuswahlProps {
	auswahl: ReligionEintrag | undefined;
	mapKatalogeintraege: Map<number, ReligionEintrag>;
	addEintrag: (religion: Partial<ReligionEintrag>) => Promise<void>;
	deleteEintraege: (eintraege: Iterable<ReligionEintrag>) => Promise<void>;
	gotoEintrag: (religion: ReligionEintrag) => Promise<void>;
	abschnitte: Map<number, Schuljahresabschnitt>;
	aktAbschnitt: Schuljahresabschnitt;
	aktSchulabschnitt: number;
	setAbschnitt: (abschnitt: Schuljahresabschnitt) => void;
	returnToKataloge: () => Promise<void>;
}