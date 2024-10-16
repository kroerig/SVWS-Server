import type { GostJahrgangsdaten, GostKursklausurManager, List, StundenplanKalenderwochenzuordnung, Wochentag } from "@core";
import { type StundenplanManager, type StundenplanZeitraster } from "@core";
import type { GostKlausurplanungDragData, GostKlausurplanungDropZone } from "./SGostKlausurplanung";

export interface SGostKlausurplanungKalenderStundenplanAnsichtProps {
	mode?: 'schueler' | 'lehrer' | 'klasse';
	jahrgangsdaten: GostJahrgangsdaten;
	ignoreEmpty?: boolean;
	id: number;
	kwAuswahl: StundenplanKalenderwochenzuordnung;
	manager: () => StundenplanManager;
	kMan: () => GostKursklausurManager;
	wochentyp: () => number;
	// kalenderwoche: () => StundenplanKalenderwochenzuordnung | undefined;
	useDragAndDrop?: boolean;
	kurseGefiltert: (day: Wochentag, stunde: number) => List<number>;
	sumSchreiber: (day: Wochentag, stunde: number) => number;
	dragData: () => GostKlausurplanungDragData;
	onDrag: (data: GostKlausurplanungDragData) => void;
	onDrop: (zone: GostKlausurplanungDropZone) => void;
	checkDropZoneZeitraster: (event: DragEvent, zeitraster: StundenplanZeitraster) => void;
}
