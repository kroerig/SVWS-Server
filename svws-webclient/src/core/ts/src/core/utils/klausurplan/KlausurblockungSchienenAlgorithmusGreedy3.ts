import { JavaObject, cast_java_lang_Object } from '../../../java/lang/JavaObject';
import { KlausurblockungSchienenAlgorithmusAbstract, cast_de_nrw_schule_svws_core_utils_klausurplan_KlausurblockungSchienenAlgorithmusAbstract } from '../../../core/utils/klausurplan/KlausurblockungSchienenAlgorithmusAbstract';
import { KlausurblockungSchienenDynDaten, cast_de_nrw_schule_svws_core_utils_klausurplan_KlausurblockungSchienenDynDaten } from '../../../core/utils/klausurplan/KlausurblockungSchienenDynDaten';
import { Random, cast_java_util_Random } from '../../../java/util/Random';
import { JavaString, cast_java_lang_String } from '../../../java/lang/JavaString';
import { System, cast_java_lang_System } from '../../../java/lang/System';

export class KlausurblockungSchienenAlgorithmusGreedy3 extends KlausurblockungSchienenAlgorithmusAbstract {

	/**
	 * Die kleinste Schienenanzahl, die bisher gefunden wurde.
	 */
	private _minSchienen : number = 0;

	/**
	 * Bis zu welchem Zeitpunkt die Rekursion laufen darf.
	 */
	private _zeitEnde : number = 0;

	/**
	 * TRUE, falls mindestens eine Lösung gefunden wurde.
	 */
	private _saved : boolean = false;


	/**
	 *Konstruktor.
	 *
	 * @param pRandom   Ein {@link Random}-Objekt zur Steuerung des Zufalls über einen Anfangs-Seed.
	 * @param pDynDaten Die aktuellen Blockungsdaten.
	 */
	public constructor(pRandom : Random, pDynDaten : KlausurblockungSchienenDynDaten) {
		super(pRandom, pDynDaten);
	}

	public toString() : string {
		return "Backtracking";
	}

	public berechne(pZeitEnde : number) : void {
		this._minSchienen = this._dynDaten.gibAnzahlKlausuren();
		this._zeitEnde = pZeitEnde;
		this._dynDaten.aktionKlausurenAusSchienenEntfernen();
		this._saved = false;
		this.berechneRekursiv();
		this._dynDaten.aktionZustand1Laden();
		if (this._dynDaten.gibIstBesserAlsZustand2() === true)
			this._dynDaten.aktionZustand2Speichern();
	}

	private berechneRekursiv() : void {
		if (this._dynDaten.gibAnzahlSchienen() > this._minSchienen)
			return;
		if ((this._saved) && (System.currentTimeMillis() > this._zeitEnde))
			return;
		const klausurNr : number = this._dynDaten.gibAnzahlSchienen() === 0 ? this._dynDaten.gibKlausurDieFreiIstMitDenMeistenFreienNachbarn() : this._dynDaten.gibKlausurDieFreiIstMitDenMeistenNachbarsfarben();
		if (klausurNr < 0) {
			if (!this._saved || this._dynDaten.gibIstBesserAlsZustand1()) {
				this._minSchienen = this._dynDaten.gibAnzahlSchienen();
				this._dynDaten.aktionZustand1Speichern();
				this._saved = true;
			}
			return;
		}
		for (let schiene : number = 0; schiene < this._minSchienen; schiene++) {
			const schienenAnzahl : number = this._dynDaten.gibAnzahlSchienen();
			const differenz : number = schiene < schienenAnzahl ? 0 : (schiene - schienenAnzahl + 1);
			this._dynDaten.aktionSchienenAnzahlVeraendern(+differenz);
			if (this._dynDaten.aktionSetzeKlausurInSchiene(klausurNr, schiene) === true) {
				this.berechneRekursiv();
				this._dynDaten.aktionEntferneKlausurAusSchiene(klausurNr);
			}
			this._dynDaten.aktionSchienenAnzahlVeraendern(-differenz);
		}
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.nrw.schule.svws.core.utils.klausurplan.KlausurblockungSchienenAlgorithmusGreedy3', 'de.nrw.schule.svws.core.utils.klausurplan.KlausurblockungSchienenAlgorithmusAbstract'].includes(name);
	}

}

export function cast_de_nrw_schule_svws_core_utils_klausurplan_KlausurblockungSchienenAlgorithmusGreedy3(obj : unknown) : KlausurblockungSchienenAlgorithmusGreedy3 {
	return obj as KlausurblockungSchienenAlgorithmusGreedy3;
}
