import { Service } from '../../../../../core/Service';
import { Abiturdaten } from '../../../../../core/data/gost/Abiturdaten';
import { LogLevel } from '../../../../../core/logger/LogLevel';

export class AbiturBlockIMarkierAlgorithmus extends Service<Abiturdaten | null, Abiturdaten | null> {


	/**
	 * Erzeugt einen Markierungs-Dienst zur Markierung der Kurse aus dem Block I der Abiturdaten, welche
	 * in die Punktewertung für die Abiturzulassung und in das Abitur einfliessen.
	 */
	public constructor() {
		super();
	}

	public handle(abidaten : Abiturdaten | null) : Abiturdaten | null {
		if (abidaten === null) {
			this.logger.logLn(LogLevel.ERROR, "Der Dienst " + this.getClass().getSimpleName()! + " hat keine gültigen Abiturdaten erhalten.");
			return null;
		}
		this.logger.logLn(LogLevel.ERROR, "Der Dienst " + this.getClass().getSimpleName()! + " ist noch nicht fertig programmiert...");
		return abidaten;
	}

	transpilerCanonicalName(): string {
		return 'de.svws_nrw.core.abschluss.gost.abitur.services.AbiturBlockIMarkierAlgorithmus';
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.svws_nrw.core.Service', 'de.svws_nrw.core.abschluss.gost.abitur.services.AbiturBlockIMarkierAlgorithmus'].includes(name);
	}

}

export function cast_de_svws_nrw_core_abschluss_gost_abitur_services_AbiturBlockIMarkierAlgorithmus(obj : unknown) : AbiturBlockIMarkierAlgorithmus {
	return obj as AbiturBlockIMarkierAlgorithmus;
}
