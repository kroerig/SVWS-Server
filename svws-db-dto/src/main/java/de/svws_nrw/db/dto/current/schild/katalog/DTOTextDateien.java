package de.svws_nrw.db.dto.current.schild.katalog;

import de.svws_nrw.db.DBEntityManager;
import de.svws_nrw.db.converter.current.BooleanPlusMinusDefaultPlusConverter;


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
import de.svws_nrw.csv.converter.current.BooleanPlusMinusDefaultPlusConverterSerializer;
import de.svws_nrw.csv.converter.current.BooleanPlusMinusDefaultPlusConverterDeserializer;

/**
 * Diese Klasse dient als DTO für die Datenbanktabelle K_Textdateien.
 * Sie wurde automatisch per Skript generiert und sollte nicht verändert werden,
 * da sie aufgrund von Änderungen am DB-Schema ggf. neu generiert und überschrieben wird.
 */
@Entity
@Cacheable(DBEntityManager.use_db_caching)
@Table(name = "K_Textdateien")
@NamedQuery(name = "DTOTextDateien.all", query = "SELECT e FROM DTOTextDateien e")
@NamedQuery(name = "DTOTextDateien.id", query = "SELECT e FROM DTOTextDateien e WHERE e.ID = :value")
@NamedQuery(name = "DTOTextDateien.id.multiple", query = "SELECT e FROM DTOTextDateien e WHERE e.ID IN :value")
@NamedQuery(name = "DTOTextDateien.bezeichnung", query = "SELECT e FROM DTOTextDateien e WHERE e.Bezeichnung = :value")
@NamedQuery(name = "DTOTextDateien.bezeichnung.multiple", query = "SELECT e FROM DTOTextDateien e WHERE e.Bezeichnung IN :value")
@NamedQuery(name = "DTOTextDateien.text_id", query = "SELECT e FROM DTOTextDateien e WHERE e.Text_ID = :value")
@NamedQuery(name = "DTOTextDateien.text_id.multiple", query = "SELECT e FROM DTOTextDateien e WHERE e.Text_ID IN :value")
@NamedQuery(name = "DTOTextDateien.text_body", query = "SELECT e FROM DTOTextDateien e WHERE e.Text_Body = :value")
@NamedQuery(name = "DTOTextDateien.text_body.multiple", query = "SELECT e FROM DTOTextDateien e WHERE e.Text_Body IN :value")
@NamedQuery(name = "DTOTextDateien.sichtbar", query = "SELECT e FROM DTOTextDateien e WHERE e.Sichtbar = :value")
@NamedQuery(name = "DTOTextDateien.sichtbar.multiple", query = "SELECT e FROM DTOTextDateien e WHERE e.Sichtbar IN :value")
@NamedQuery(name = "DTOTextDateien.sortierung", query = "SELECT e FROM DTOTextDateien e WHERE e.Sortierung = :value")
@NamedQuery(name = "DTOTextDateien.sortierung.multiple", query = "SELECT e FROM DTOTextDateien e WHERE e.Sortierung IN :value")
@NamedQuery(name = "DTOTextDateien.primaryKeyQuery", query = "SELECT e FROM DTOTextDateien e WHERE e.ID = ?1")
@NamedQuery(name = "DTOTextDateien.primaryKeyQuery.multiple", query = "SELECT e FROM DTOTextDateien e WHERE e.ID IN :value")
@NamedQuery(name = "DTOTextDateien.all.migration", query = "SELECT e FROM DTOTextDateien e WHERE e.ID IS NOT NULL")
@JsonPropertyOrder({"ID", "Bezeichnung", "Text_ID", "Text_Body", "Sichtbar", "Sortierung"})
public final class DTOTextDateien {

	/** ID des Textes der zum Ersatz für txt-Dateien genutzt wird */
	@Id
	@Column(name = "ID")
	@JsonProperty
	public long ID;

	/** Bezeichnungdes Textes der zum Ersatz für txt-Dateien genutzt wird */
	@Column(name = "Bezeichnung")
	@JsonProperty
	public String Bezeichnung;

	/** TextID des Textes der zum Ersatz für txt-Dateien genutzt wird */
	@Column(name = "Text_ID")
	@JsonProperty
	public long Text_ID;

	/** Text-Body des Textes der zum Ersatz für txt-Dateien genutzt wird */
	@Column(name = "Text_Body")
	@JsonProperty
	public String Text_Body;

	/** Sichbarkeit des Textes der zum Ersatz für txt-Dateien genutzt wird */
	@Column(name = "Sichtbar")
	@JsonProperty
	@Convert(converter = BooleanPlusMinusDefaultPlusConverter.class)
	@JsonSerialize(using = BooleanPlusMinusDefaultPlusConverterSerializer.class)
	@JsonDeserialize(using = BooleanPlusMinusDefaultPlusConverterDeserializer.class)
	public Boolean Sichtbar;

	/** Sortierung des Textes der zum Ersatz für txt-Dateien genutzt wird */
	@Column(name = "Sortierung")
	@JsonProperty
	public Integer Sortierung;

	/**
	 * Erstellt ein neues Objekt der Klasse DTOTextDateien ohne eine Initialisierung der Attribute.
	 */
	@SuppressWarnings("unused")
	private DTOTextDateien() {
	}

	/**
	 * Erstellt ein neues Objekt der Klasse DTOTextDateien ohne eine Initialisierung der Attribute.
	 * @param ID   der Wert für das Attribut ID
	 * @param Text_ID   der Wert für das Attribut Text_ID
	 */
	public DTOTextDateien(final long ID, final long Text_ID) {
		this.ID = ID;
		this.Text_ID = Text_ID;
	}


	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DTOTextDateien other = (DTOTextDateien) obj;
		return ID == other.ID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Long.hashCode(ID);
		return result;
	}


	/**
	 * Konvertiert das Objekt in einen String. Dieser kann z.B. für Debug-Ausgaben genutzt werden.
	 *
	 * @return die String-Repräsentation des Objektes
	 */
	@Override
	public String toString() {
		return "DTOTextDateien(ID=" + this.ID + ", Bezeichnung=" + this.Bezeichnung + ", Text_ID=" + this.Text_ID + ", Text_Body=" + this.Text_Body + ", Sichtbar=" + this.Sichtbar + ", Sortierung=" + this.Sortierung + ")";
	}

}
