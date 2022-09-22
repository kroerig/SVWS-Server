package de.nrw.schule.svws.db.dto.rev8.schild.schule;

import java.io.Serializable;

/**
 * Diese Klasse dient als DTO für den Primärschlüssel der Datenbanktabelle OrganisationsformenKatalog_Schulformen.
 * Sie wurde automatisch per Skript generiert und sollte nicht verändert werden, 
 * da sie aufgrund von Änderungen am DB-Schema ggf. neu generiert und überschrieben wird.
 */
public class Rev8DTOOrganisationsformenKatalogSchulformenPK implements Serializable {

	/** Die UID für diese Klasse */
	private static final long serialVersionUID = 1L;

	/** die ID der Organisationsform */
	public Long Organisationsform_ID;

	/** das Kürzel der Schulform */
	public String Schulform_Kuerzel;

	/**
	 * Erstellt ein neues Objekt der Klasse Rev8DTOOrganisationsformenKatalogSchulformenPK ohne eine Initialisierung der Attribute.
	 */
	@SuppressWarnings("unused")
	private Rev8DTOOrganisationsformenKatalogSchulformenPK() {
	}

	/**
	 * Erstellt ein neues Objekt der Klasse Rev8DTOOrganisationsformenKatalogSchulformenPK.
	 * @param Organisationsform_ID   der Wert für das Attribut Organisationsform_ID
	 * @param Schulform_Kuerzel   der Wert für das Attribut Schulform_Kuerzel
	 */
	public Rev8DTOOrganisationsformenKatalogSchulformenPK(final Long Organisationsform_ID, final String Schulform_Kuerzel) {
		if (Organisationsform_ID == null) { 
			throw new NullPointerException("Organisationsform_ID must not be null");
		}
		this.Organisationsform_ID = Organisationsform_ID;
		if (Schulform_Kuerzel == null) { 
			throw new NullPointerException("Schulform_Kuerzel must not be null");
		}
		this.Schulform_Kuerzel = Schulform_Kuerzel;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Rev8DTOOrganisationsformenKatalogSchulformenPK other = (Rev8DTOOrganisationsformenKatalogSchulformenPK) obj;
		if (Organisationsform_ID == null) {
			if (other.Organisationsform_ID != null)
				return false;
		} else if (!Organisationsform_ID.equals(other.Organisationsform_ID))
			return false;

		if (Schulform_Kuerzel == null) {
			if (other.Schulform_Kuerzel != null)
				return false;
		} else if (!Schulform_Kuerzel.equals(other.Schulform_Kuerzel))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Organisationsform_ID == null) ? 0 : Organisationsform_ID.hashCode());

		result = prime * result + ((Schulform_Kuerzel == null) ? 0 : Schulform_Kuerzel.hashCode());
		return result;
	}
}