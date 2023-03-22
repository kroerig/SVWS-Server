import { JavaObject, cast_java_lang_Object } from '../../../java/lang/JavaObject';
import { KlausurblockungSchienenAlgorithmusAbstract, cast_de_nrw_schule_svws_core_utils_klausurplan_KlausurblockungSchienenAlgorithmusAbstract } from '../../../core/utils/klausurplan/KlausurblockungSchienenAlgorithmusAbstract';
import { KlausurblockungSchienenDynDaten, cast_de_nrw_schule_svws_core_utils_klausurplan_KlausurblockungSchienenDynDaten } from '../../../core/utils/klausurplan/KlausurblockungSchienenDynDaten';
import { Random, cast_java_util_Random } from '../../../java/util/Random';
import { JavaString, cast_java_lang_String } from '../../../java/lang/JavaString';
import { System, cast_java_lang_System } from '../../../java/lang/System';

export class KlausurblockungSchienenAlgorithmusGreedy4 extends KlausurblockungSchienenAlgorithmusAbstract {


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
		return "DSatur";
	}

	public berechne(pZeitEnde : number) : void {
		this._dynDaten.aktion_EntferneAlles_KlausurenMitDenMeistenNachbarsfarben_SchienenZufaellig();
		this._dynDaten.aktionZustand1Speichern();
		while (System.currentTimeMillis() < pZeitEnde) {
			this._dynDaten.aktion_EntferneAlles_KlausurenMitDenMeistenNachbarsfarben_SchienenZufaellig();
			if (this._dynDaten.gibIstBesserAlsZustand1() === true)
				this._dynDaten.aktionZustand1Speichern();
			else
				this._dynDaten.aktionZustand1Laden();
		}
		if (this._dynDaten.gibIstBesserAlsZustand2() === true)
			this._dynDaten.aktionZustand2Speichern();
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.nrw.schule.svws.core.utils.klausurplan.KlausurblockungSchienenAlgorithmusGreedy4', 'de.nrw.schule.svws.core.utils.klausurplan.KlausurblockungSchienenAlgorithmusAbstract'].includes(name);
	}

}

export function cast_de_nrw_schule_svws_core_utils_klausurplan_KlausurblockungSchienenAlgorithmusGreedy4(obj : unknown) : KlausurblockungSchienenAlgorithmusGreedy4 {
	return obj as KlausurblockungSchienenAlgorithmusGreedy4;
}
