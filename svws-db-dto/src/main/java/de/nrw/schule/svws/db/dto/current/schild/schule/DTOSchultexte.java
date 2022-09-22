package de.nrw.schule.svws.db.dto.current.schild.schule;

import de.nrw.schule.svws.db.DBEntityManager;
import de.nrw.schule.svws.db.converter.current.BooleanPlusMinusDefaultPlusConverter;


import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.nrw.schule.svws.csv.converter.current.BooleanPlusMinusDefaultPlusConverterSerializer;
import de.nrw.schule.svws.csv.converter.current.BooleanPlusMinusDefaultPlusConverterDeserializer;

/**
 * Diese Klasse dient als DTO für die Datenbanktabelle EigeneSchule_Texte.
 * Sie wurde automatisch per Skript generiert und sollte nicht verändert werden, 
 * da sie aufgrund von Änderungen am DB-Schema ggf. neu generiert und überschrieben wird.
 */
@Entity
@Cacheable(DBEntityManager.use_db_caching)
@Table(name = "EigeneSchule_Texte")
@NamedQuery(name="DTOSchultexte.all", query="SELECT e FROM DTOSchultexte e")
@NamedQuery(name="DTOSchultexte.id", query="SELECT e FROM DTOSchultexte e WHERE e.ID = :value")
@NamedQuery(name="DTOSchultexte.id.multiple", query="SELECT e FROM DTOSchultexte e WHERE e.ID IN :value")
@NamedQuery(name="DTOSchultexte.kuerzel", query="SELECT e FROM DTOSchultexte e WHERE e.Kuerzel = :value")
@NamedQuery(name="DTOSchultexte.kuerzel.multiple", query="SELECT e FROM DTOSchultexte e WHERE e.Kuerzel IN :value")
@NamedQuery(name="DTOSchultexte.inhalt", query="SELECT e FROM DTOSchultexte e WHERE e.Inhalt = :value")
@NamedQuery(name="DTOSchultexte.inhalt.multiple", query="SELECT e FROM DTOSchultexte e WHERE e.Inhalt IN :value")
@NamedQuery(name="DTOSchultexte.beschreibung", query="SELECT e FROM DTOSchultexte e WHERE e.Beschreibung = :value")
@NamedQuery(name="DTOSchultexte.beschreibung.multiple", query="SELECT e FROM DTOSchultexte e WHERE e.Beschreibung IN :value")
@NamedQuery(name="DTOSchultexte.aenderbar", query="SELECT e FROM DTOSchultexte e WHERE e.Aenderbar = :value")
@NamedQuery(name="DTOSchultexte.aenderbar.multiple", query="SELECT e FROM DTOSchultexte e WHERE e.Aenderbar IN :value")
@NamedQuery(name="DTOSchultexte.primaryKeyQuery", query="SELECT e FROM DTOSchultexte e WHERE e.ID = ?1")
@NamedQuery(name="DTOSchultexte.all.migration", query="SELECT e FROM DTOSchultexte e WHERE e.ID IS NOT NULL")
@JsonPropertyOrder({"ID","Kuerzel","Inhalt","Beschreibung","Aenderbar"})
public class DTOSchultexte {

	/** ID des Textes unter Schulverwaltung Eigene Schule bearbeiten */
	@Id
	@Column(name = "ID")
	@JsonProperty
	public Long ID;

	/** Kürzel des Schultextes */
	@Column(name = "Kuerzel")
	@JsonProperty
	public String Kuerzel;

	/** Inhalt des Schultextes */
	@Column(name = "Inhalt")
	@JsonProperty
	public String Inhalt;

	/** Beschreibung des Schultextes */
	@Column(name = "Beschreibung")
	@JsonProperty
	public String Beschreibung;

	/** Gibt an ob der Text änderbar ist */
	@Column(name = "Aenderbar")
	@JsonProperty
	@Convert(converter=BooleanPlusMinusDefaultPlusConverter.class)
	@JsonSerialize(using=BooleanPlusMinusDefaultPlusConverterSerializer.class)
	@JsonDeserialize(using=BooleanPlusMinusDefaultPlusConverterDeserializer.class)
	public Boolean Aenderbar;

	/**
	 * Erstellt ein neues Objekt der Klasse DTOSchultexte ohne eine Initialisierung der Attribute.
	 */
	@SuppressWarnings("unused")
	private DTOSchultexte() {
	}

	/**
	 * Erstellt ein neues Objekt der Klasse DTOSchultexte ohne eine Initialisierung der Attribute.
	 * @param ID   der Wert für das Attribut ID
	 */
	public DTOSchultexte(final Long ID) {
		if (ID == null) { 
			throw new NullPointerException("ID must not be null");
		}
		this.ID = ID;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DTOSchultexte other = (DTOSchultexte) obj;
		if (ID == null) {
			if (other.ID != null)
				return false;
		} else if (!ID.equals(other.ID))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ID == null) ? 0 : ID.hashCode());
		return result;
	}


	/**
	 * Konvertiert das Objekt in einen String. Dieser kann z.B. für Debug-Ausgaben genutzt werden.
	 *
	 * @return die String-Repräsentation des Objektes
	 */
	@Override
	public String toString() {
		return "DTOSchultexte(ID=" + this.ID + ", Kuerzel=" + this.Kuerzel + ", Inhalt=" + this.Inhalt + ", Beschreibung=" + this.Beschreibung + ", Aenderbar=" + this.Aenderbar + ")";
	}

}