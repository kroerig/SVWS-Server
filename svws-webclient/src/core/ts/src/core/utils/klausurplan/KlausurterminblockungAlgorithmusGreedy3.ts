import { JavaObject, cast_java_lang_Object } from '../../../java/lang/JavaObject';
import { JavaInteger, cast_java_lang_Integer } from '../../../java/lang/JavaInteger';
import { Random, cast_java_util_Random } from '../../../java/util/Random';
import { KlausurterminblockungDynDaten, cast_de_nrw_schule_svws_core_utils_klausurplan_KlausurterminblockungDynDaten } from '../../../core/utils/klausurplan/KlausurterminblockungDynDaten';
import { KlausurterminblockungAlgorithmusAbstract, cast_de_nrw_schule_svws_core_utils_klausurplan_KlausurterminblockungAlgorithmusAbstract } from '../../../core/utils/klausurplan/KlausurterminblockungAlgorithmusAbstract';
import { JavaString, cast_java_lang_String } from '../../../java/lang/JavaString';
import { DeveloperNotificationException, cast_de_nrw_schule_svws_core_exceptions_DeveloperNotificationException } from '../../../core/exceptions/DeveloperNotificationException';
import { Vector, cast_java_util_Vector } from '../../../java/util/Vector';
import { System, cast_java_lang_System } from '../../../java/lang/System';

export class KlausurterminblockungAlgorithmusGreedy3 extends KlausurterminblockungAlgorithmusAbstract {

	/**
	 * Die kleinste Schienenanzahl, die bisher gefunden wurde.
	 */
	private _minTermine : number = 0;

	/**
	 * Bis zu welchem Zeitpunkt die Rekursion laufen darf.
	 */
	private _zeitEnde : number = 0;


	/**
	 *
	 * Konstruktor.
	 * 
	 * @param pRandom   Ein {@link Random}-Objekt zur Steuerung des Zufalls über einen Anfangs-Seed.
	 * @param pDynDaten Die aktuellen Blockungsdaten. 
	 */
	public constructor(pRandom : Random, pDynDaten : KlausurterminblockungDynDaten) {
		super(pRandom, pDynDaten);
	}

	public toString() : string {
		return "Termine nacheinander, Klausurgruppen mit Backtracking";
	}

	public berechne(pZeitEnde : number) : void {
		this._zeitEnde = pZeitEnde;
		this._dynDaten.aktion_Clear_TermineNacheinander_GruppeNachGrad();
		this._minTermine = this._dynDaten.gibKlausurgruppenAnzahl();
		this._dynDaten.aktionZustand1Speichern();
		this._dynDaten.aktionClear();
		this.berechneRekursiv();
		this._dynDaten.aktionZustand1Laden();
		if (this._dynDaten.gibIstBesserAlsZustand2() === true) 
			this._dynDaten.aktionZustand2Speichern();
	}

	private berechneRekursiv() : void {
		if (this._dynDaten.gibTerminAnzahl() > this._minTermine) 
			return;
		if (System.currentTimeMillis() > this._zeitEnde) 
			return;
		if (!this._dynDaten.gibExistierenNichtverteilteKlausuren()) {
			if (this._dynDaten.gibIstBesserAlsZustand1()) {
				this._minTermine = this._dynDaten.gibTerminAnzahl();
				this._dynDaten.aktionZustand1Speichern();
			}
			return;
		}
		let gruppe : Vector<number> = this._dynDaten.gibKlausurgruppeMitMinimalenTerminmoeglichkeiten();
		for (let terminNr : number = 0; terminNr < this._dynDaten.gibTerminAnzahl(); terminNr++){
			if (this._dynDaten.aktionSetzeKlausurgruppeInTermin(gruppe, terminNr)) {
				this.berechneRekursiv();
				this._dynDaten.aktionEntferneKlausurgruppeAusTermin(gruppe, terminNr);
			}
		}
		let terminNr : number = this._dynDaten.gibErzeugeNeuenTermin();
		if (!this._dynDaten.aktionSetzeKlausurgruppeInTermin(gruppe, terminNr)) 
			throw new DeveloperNotificationException("Ein Setzen muss hier möglich sein!")
		this.berechneRekursiv();
		this._dynDaten.aktionEntferneKlausurgruppeAusTermin(gruppe, terminNr);
		this._dynDaten.entferneLetztenTermin();
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.nrw.schule.svws.core.utils.klausurplan.KlausurterminblockungAlgorithmusGreedy3', 'de.nrw.schule.svws.core.utils.klausurplan.KlausurterminblockungAlgorithmusAbstract'].includes(name);
	}

}

export function cast_de_nrw_schule_svws_core_utils_klausurplan_KlausurterminblockungAlgorithmusGreedy3(obj : unknown) : KlausurterminblockungAlgorithmusGreedy3 {
	return obj as KlausurterminblockungAlgorithmusGreedy3;
}
