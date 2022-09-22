package de.nrw.schule.svws.db.dto.rev8.schild.lehrer;

import java.io.Serializable;

/**
 * Diese Klasse dient als DTO für den Primärschlüssel der Datenbanktabelle LehrerLehramt.
 * Sie wurde automatisch per Skript generiert und sollte nicht verändert werden, 
 * da sie aufgrund von Änderungen am DB-Schema ggf. neu generiert und überschrieben wird.
 */
public class Rev8DTOLehrerLehramtPK implements Serializable {

	/** Die UID für diese Klasse */
	private static final long serialVersionUID = 1L;

	/** LehrerID zu der das Lehramt gehört */
	public Long Lehrer_ID;

	/** Lehramtskürzel */
	public String LehramtKrz;

	/**
	 * Erstellt ein neues Objekt der Klasse Rev8DTOLehrerLehramtPK ohne eine Initialisierung der Attribute.
	 */
	@SuppressWarnings("unused")
	private Rev8DTOLehrerLehramtPK() {
	}

	/**
	 * Erstellt ein neues Objekt der Klasse Rev8DTOLehrerLehramtPK.
	 * @param Lehrer_ID   der Wert für das Attribut Lehrer_ID
	 * @param LehramtKrz   der Wert für das Attribut LehramtKrz
	 */
	public Rev8DTOLehrerLehramtPK(final Long Lehrer_ID, final String LehramtKrz) {
		if (Lehrer_ID == null) { 
			throw new NullPointerException("Lehrer_ID must not be null");
		}
		this.Lehrer_ID = Lehrer_ID;
		if (LehramtKrz == null) { 
			throw new NullPointerException("LehramtKrz must not be null");
		}
		this.LehramtKrz = LehramtKrz;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Rev8DTOLehrerLehramtPK other = (Rev8DTOLehrerLehramtPK) obj;
		if (Lehrer_ID == null) {
			if (other.Lehrer_ID != null)
				return false;
		} else if (!Lehrer_ID.equals(other.Lehrer_ID))
			return false;

		if (LehramtKrz == null) {
			if (other.LehramtKrz != null)
				return false;
		} else if (!LehramtKrz.equals(other.LehramtKrz))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Lehrer_ID == null) ? 0 : Lehrer_ID.hashCode());

		result = prime * result + ((LehramtKrz == null) ? 0 : LehramtKrz.hashCode());
		return result;
	}
}