package de.svws_nrw.db.dto.current.schild.stundenplan;

import de.svws_nrw.db.DBEntityManager;
import de.svws_nrw.db.converter.current.UhrzeitConverter;


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
import de.svws_nrw.csv.converter.current.UhrzeitConverterSerializer;
import de.svws_nrw.csv.converter.current.UhrzeitConverterDeserializer;

/**
 * Diese Klasse dient als DTO für die Datenbanktabelle Katalog_Zeitraster.
 * Sie wurde automatisch per Skript generiert und sollte nicht verändert werden,
 * da sie aufgrund von Änderungen am DB-Schema ggf. neu generiert und überschrieben wird.
 */
@Entity
@Cacheable(DBEntityManager.use_db_caching)
@Table(name = "Katalog_Zeitraster")
@NamedQuery(name = "DTOKatalogZeitraster.all", query = "SELECT e FROM DTOKatalogZeitraster e")
@NamedQuery(name = "DTOKatalogZeitraster.id", query = "SELECT e FROM DTOKatalogZeitraster e WHERE e.ID = :value")
@NamedQuery(name = "DTOKatalogZeitraster.id.multiple", query = "SELECT e FROM DTOKatalogZeitraster e WHERE e.ID IN :value")
@NamedQuery(name = "DTOKatalogZeitraster.tag", query = "SELECT e FROM DTOKatalogZeitraster e WHERE e.Tag = :value")
@NamedQuery(name = "DTOKatalogZeitraster.tag.multiple", query = "SELECT e FROM DTOKatalogZeitraster e WHERE e.Tag IN :value")
@NamedQuery(name = "DTOKatalogZeitraster.stunde", query = "SELECT e FROM DTOKatalogZeitraster e WHERE e.Stunde = :value")
@NamedQuery(name = "DTOKatalogZeitraster.stunde.multiple", query = "SELECT e FROM DTOKatalogZeitraster e WHERE e.Stunde IN :value")
@NamedQuery(name = "DTOKatalogZeitraster.beginn", query = "SELECT e FROM DTOKatalogZeitraster e WHERE e.Beginn = :value")
@NamedQuery(name = "DTOKatalogZeitraster.beginn.multiple", query = "SELECT e FROM DTOKatalogZeitraster e WHERE e.Beginn IN :value")
@NamedQuery(name = "DTOKatalogZeitraster.ende", query = "SELECT e FROM DTOKatalogZeitraster e WHERE e.Ende = :value")
@NamedQuery(name = "DTOKatalogZeitraster.ende.multiple", query = "SELECT e FROM DTOKatalogZeitraster e WHERE e.Ende IN :value")
@NamedQuery(name = "DTOKatalogZeitraster.primaryKeyQuery", query = "SELECT e FROM DTOKatalogZeitraster e WHERE e.ID = ?1")
@NamedQuery(name = "DTOKatalogZeitraster.primaryKeyQuery.multiple", query = "SELECT e FROM DTOKatalogZeitraster e WHERE e.ID IN :value")
@NamedQuery(name = "DTOKatalogZeitraster.all.migration", query = "SELECT e FROM DTOKatalogZeitraster e WHERE e.ID IS NOT NULL")
@JsonPropertyOrder({"ID", "Tag", "Stunde", "Beginn", "Ende"})
public final class DTOKatalogZeitraster {

	/** Eine ID, die einen Zeitraster-Eintrag eindeutig identifiziert */
	@Id
	@Column(name = "ID")
	@JsonProperty
	public long ID;

	/** Der Wochentag laut ISO-8601 Standard: (1 - Montag, 2 - Dienstag, ...) */
	@Column(name = "Tag")
	@JsonProperty
	public int Tag;

	/** Die Stunde laut Stundenplan (1, 2, ...) */
	@Column(name = "Stunde")
	@JsonProperty
	public int Stunde;

	/** Die Uhrzeit, wann die Stunde beginnt */
	@Column(name = "Beginn")
	@JsonProperty
	@Convert(converter = UhrzeitConverter.class)
	@JsonSerialize(using = UhrzeitConverterSerializer.class)
	@JsonDeserialize(using = UhrzeitConverterDeserializer.class)
	public Integer Beginn;

	/** Die Uhrzeit, wann die Stunde endet */
	@Column(name = "Ende")
	@JsonProperty
	@Convert(converter = UhrzeitConverter.class)
	@JsonSerialize(using = UhrzeitConverterSerializer.class)
	@JsonDeserialize(using = UhrzeitConverterDeserializer.class)
	public Integer Ende;

	/**
	 * Erstellt ein neues Objekt der Klasse DTOKatalogZeitraster ohne eine Initialisierung der Attribute.
	 */
	@SuppressWarnings("unused")
	private DTOKatalogZeitraster() {
	}

	/**
	 * Erstellt ein neues Objekt der Klasse DTOKatalogZeitraster ohne eine Initialisierung der Attribute.
	 * @param ID   der Wert für das Attribut ID
	 * @param Tag   der Wert für das Attribut Tag
	 * @param Stunde   der Wert für das Attribut Stunde
	 * @param Beginn   der Wert für das Attribut Beginn
	 * @param Ende   der Wert für das Attribut Ende
	 */
	public DTOKatalogZeitraster(final long ID, final int Tag, final int Stunde, final Integer Beginn, final Integer Ende) {
		this.ID = ID;
		this.Tag = Tag;
		this.Stunde = Stunde;
		if (Beginn == null) {
			throw new NullPointerException("Beginn must not be null");
		}
		this.Beginn = Beginn;
		if (Ende == null) {
			throw new NullPointerException("Ende must not be null");
		}
		this.Ende = Ende;
	}


	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DTOKatalogZeitraster other = (DTOKatalogZeitraster) obj;
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
		return "DTOKatalogZeitraster(ID=" + this.ID + ", Tag=" + this.Tag + ", Stunde=" + this.Stunde + ", Beginn=" + this.Beginn + ", Ende=" + this.Ende + ")";
	}

}
