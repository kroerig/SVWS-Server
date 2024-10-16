import { KursblockungStatic } from '../../core/kursblockung/KursblockungStatic';
import { Random } from '../../java/util/Random';
import { KursblockungDynDaten } from '../../core/kursblockung/KursblockungDynDaten';
import { KursblockungAlgorithmusK, cast_de_svws_nrw_core_kursblockung_KursblockungAlgorithmusK } from '../../core/kursblockung/KursblockungAlgorithmusK';
import { KursblockungDynSchueler } from '../../core/kursblockung/KursblockungDynSchueler';
import { Logger } from '../../core/logger/Logger';
import { System } from '../../java/lang/System';

export class KursblockungAlgorithmusKFachwahlmatrix2 extends KursblockungAlgorithmusK {

	/**
	 *  Die Anzahl an Runden ohne Verbesserung, bevor es zum Abbruch kommt.
	 */
	private static readonly MAX_RUNDEN_IN_FOLGE_OHNE_VERBESSERUNG : number = 2000;

	private readonly schuelerAlle : Array<KursblockungDynSchueler>;


	/**
	 * Im Konstruktor kann die Klasse die jeweiligen Datenstrukturen aufbauen. Kurse dürfen in diese Methode noch nicht
	 * auf Schienen verteilt werden.
	 *
	 * @param pRandom Ein {@link Random}-Objekt zur Steuerung des Zufalls über einen Anfangs-Seed.
	 * @param pLogger Logger für Benutzerhinweise, Warnungen und Fehler.
	 * @param pDynDat Die dynamischen Blockungsdaten.
	 */
	public constructor(pRandom : Random, pLogger : Logger, pDynDat : KursblockungDynDaten) {
		super(pRandom, pLogger, pDynDat);
		this.schuelerAlle = this.dynDaten.gibSchuelerArray(false);
	}

	/**
	 * Der Algorithmus entfernt zunächst alle SuS aus ihren Kursen. Anschließend werden die Kurse zufällig verteilt.
	 * Anschließend verändert der Algorithmus die Lage eines zufälligen Kurses. Falls sich die Bewertung verschlechtert,
	 * wird die Veränderung rückgängig gemacht.
	 */
	public berechne(pMaxTimeMillis : number) : void {
		if (this.dynDaten.gibKurseDieFreiSindAnzahl() === 0) {
			return;
		}
		const timeStart : number = System.currentTimeMillis();
		this.dynDaten.aktionSchuelerAusAllenKursenEntfernen();
		this.dynDaten.aktionKurseFreieZufaelligVerteilen();
		this.dynDaten.aktionZustandSpeichernK();
		let countKeineVerbesserung : number = 0;
		do {
			countKeineVerbesserung = this.verteileKurse() ? 0 : countKeineVerbesserung + 1;
		} while ((countKeineVerbesserung < KursblockungAlgorithmusKFachwahlmatrix2.MAX_RUNDEN_IN_FOLGE_OHNE_VERBESSERUNG) && (System.currentTimeMillis() - timeStart < pMaxTimeMillis));
	}

	/**
	 * Die Lage einiger Kurse wird verändert. Falls sich die Bewertung verschlechtert, wird die Veränderung rückgängig
	 * gemacht.
	 *
	 * @return true, wenn der Zustand angepasst wurde
	 */
	private verteileKurse() : boolean {
		do {
			this.dynDaten.aktionSchuelerAusAllenKursenEntfernen();
			this.dynDaten.aktionKursVerteilenEinenZufaelligenFreien();
			this.verteileSuS();
			if (this.dynDaten.gibBewertungK_FW_NW_KD_JetztBesser() > 0) {
				this.dynDaten.aktionZustandSpeichernK();
				return true;
			}
		} while (this._random.nextBoolean());
		this.dynDaten.aktionZustandLadenK();
		return false;
	}

	/**
	 * Verteilt die SuS. Multikurse der SuS werden zufällig verteilt, die anderen Kurse werden mit Hilfe eines
	 * Matching-Algorithmus verteilt.
	 */
	private verteileSuS() : void {
		const perm : Array<number> = KursblockungStatic.gibPermutation(this._random, this.schuelerAlle.length);
		for (let p : number = 0; p < perm.length; p++) {
			const i : number = perm[p];
			const schueler : KursblockungDynSchueler | null = this.schuelerAlle[i];
			schueler.aktionKurseVerteilenNurMultikurseZufaellig();
			schueler.aktionKurseVerteilenMitBipartiteMatching();
		}
	}

	transpilerCanonicalName(): string {
		return 'de.svws_nrw.core.kursblockung.KursblockungAlgorithmusKFachwahlmatrix2';
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.svws_nrw.core.kursblockung.KursblockungAlgorithmusKFachwahlmatrix2', 'de.svws_nrw.core.kursblockung.KursblockungAlgorithmusK'].includes(name);
	}

}

export function cast_de_svws_nrw_core_kursblockung_KursblockungAlgorithmusKFachwahlmatrix2(obj : unknown) : KursblockungAlgorithmusKFachwahlmatrix2 {
	return obj as KursblockungAlgorithmusKFachwahlmatrix2;
}
