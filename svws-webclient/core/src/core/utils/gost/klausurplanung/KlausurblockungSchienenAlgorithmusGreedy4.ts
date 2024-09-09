import { KlausurblockungSchienenAlgorithmusAbstract } from '../../../../core/utils/gost/klausurplanung/KlausurblockungSchienenAlgorithmusAbstract';
import { KlausurblockungSchienenDynDaten } from '../../../../core/utils/gost/klausurplanung/KlausurblockungSchienenDynDaten';
import { Random } from '../../../../java/util/Random';
import { Class } from '../../../../java/lang/Class';
import { System } from '../../../../java/lang/System';

export class KlausurblockungSchienenAlgorithmusGreedy4 extends KlausurblockungSchienenAlgorithmusAbstract {


	/**
	 * Konstruktor.
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
			if (this._dynDaten.gibIstBesserAlsZustand1())
				this._dynDaten.aktionZustand1Speichern();
			else
				this._dynDaten.aktionZustand1Laden();
		}
		if (this._dynDaten.gibIstBesserAlsZustand2())
			this._dynDaten.aktionZustand2Speichern();
	}

	transpilerCanonicalName(): string {
		return 'de.svws_nrw.core.utils.gost.klausurplanung.KlausurblockungSchienenAlgorithmusGreedy4';
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.svws_nrw.core.utils.gost.klausurplanung.KlausurblockungSchienenAlgorithmusGreedy4', 'de.svws_nrw.core.utils.gost.klausurplanung.KlausurblockungSchienenAlgorithmusAbstract'].includes(name);
	}

	public static class = new Class<KlausurblockungSchienenAlgorithmusGreedy4>('de.svws_nrw.core.utils.gost.klausurplanung.KlausurblockungSchienenAlgorithmusGreedy4');

}

export function cast_de_svws_nrw_core_utils_gost_klausurplanung_KlausurblockungSchienenAlgorithmusGreedy4(obj : unknown) : KlausurblockungSchienenAlgorithmusGreedy4 {
	return obj as KlausurblockungSchienenAlgorithmusGreedy4;
}
