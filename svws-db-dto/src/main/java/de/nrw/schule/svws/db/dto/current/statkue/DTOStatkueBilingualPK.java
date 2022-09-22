package de.nrw.schule.svws.db.dto.current.statkue;

import java.io.Serializable;

/**
 * Diese Klasse dient als DTO für den Primärschlüssel der Datenbanktabelle Statkue_Bilingual.
 * Sie wurde automatisch per Skript generiert und sollte nicht verändert werden, 
 * da sie aufgrund von Änderungen am DB-Schema ggf. neu generiert und überschrieben wird.
 */
public class DTOStatkueBilingualPK implements Serializable {

	/** Die UID für diese Klasse */
	private static final long serialVersionUID = 1L;

	/** Statkue Tabelle IT.NRW: Fachkürzel Bilinguale Fächer */
	public String Fach;

	/** Statkue Tabelle IT.NRW: zulässige Schulform Bilinguale Fächer */
	public String SF;

	/**
	 * Erstellt ein neues Objekt der Klasse DTOStatkueBilingualPK ohne eine Initialisierung der Attribute.
	 */
	@SuppressWarnings("unused")
	private DTOStatkueBilingualPK() {
	}

	/**
	 * Erstellt ein neues Objekt der Klasse DTOStatkueBilingualPK.
	 * @param Fach   der Wert für das Attribut Fach
	 * @param SF   der Wert für das Attribut SF
	 */
	public DTOStatkueBilingualPK(final String Fach, final String SF) {
		if (Fach == null) { 
			throw new NullPointerException("Fach must not be null");
		}
		this.Fach = Fach;
		if (SF == null) { 
			throw new NullPointerException("SF must not be null");
		}
		this.SF = SF;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DTOStatkueBilingualPK other = (DTOStatkueBilingualPK) obj;
		if (Fach == null) {
			if (other.Fach != null)
				return false;
		} else if (!Fach.equals(other.Fach))
			return false;

		if (SF == null) {
			if (other.SF != null)
				return false;
		} else if (!SF.equals(other.SF))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Fach == null) ? 0 : Fach.hashCode());

		result = prime * result + ((SF == null) ? 0 : SF.hashCode());
		return result;
	}
}