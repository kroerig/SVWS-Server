package de.nrw.schule.svws.db.dto.rev8.schild.klassen;

import de.nrw.schule.svws.db.DBEntityManager;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
/**
 * Diese Klasse dient als DTO für die Datenbanktabelle KlassenartenKatalog.
 * Sie wurde automatisch per Skript generiert und sollte nicht verändert werden, 
 * da sie aufgrund von Änderungen am DB-Schema ggf. neu generiert und überschrieben wird.
 */
@Entity
@Cacheable(DBEntityManager.use_db_caching)
@Table(name = "KlassenartenKatalog")
@NamedQuery(name="Rev8DTOKlassenartenKatalog.all", query="SELECT e FROM Rev8DTOKlassenartenKatalog e")
@NamedQuery(name="Rev8DTOKlassenartenKatalog.id", query="SELECT e FROM Rev8DTOKlassenartenKatalog e WHERE e.ID = :value")
@NamedQuery(name="Rev8DTOKlassenartenKatalog.id.multiple", query="SELECT e FROM Rev8DTOKlassenartenKatalog e WHERE e.ID IN :value")
@NamedQuery(name="Rev8DTOKlassenartenKatalog.kuerzel", query="SELECT e FROM Rev8DTOKlassenartenKatalog e WHERE e.Kuerzel = :value")
@NamedQuery(name="Rev8DTOKlassenartenKatalog.kuerzel.multiple", query="SELECT e FROM Rev8DTOKlassenartenKatalog e WHERE e.Kuerzel IN :value")
@NamedQuery(name="Rev8DTOKlassenartenKatalog.bezeichnung", query="SELECT e FROM Rev8DTOKlassenartenKatalog e WHERE e.Bezeichnung = :value")
@NamedQuery(name="Rev8DTOKlassenartenKatalog.bezeichnung.multiple", query="SELECT e FROM Rev8DTOKlassenartenKatalog e WHERE e.Bezeichnung IN :value")
@NamedQuery(name="Rev8DTOKlassenartenKatalog.gueltigvon", query="SELECT e FROM Rev8DTOKlassenartenKatalog e WHERE e.gueltigVon = :value")
@NamedQuery(name="Rev8DTOKlassenartenKatalog.gueltigvon.multiple", query="SELECT e FROM Rev8DTOKlassenartenKatalog e WHERE e.gueltigVon IN :value")
@NamedQuery(name="Rev8DTOKlassenartenKatalog.gueltigbis", query="SELECT e FROM Rev8DTOKlassenartenKatalog e WHERE e.gueltigBis = :value")
@NamedQuery(name="Rev8DTOKlassenartenKatalog.gueltigbis.multiple", query="SELECT e FROM Rev8DTOKlassenartenKatalog e WHERE e.gueltigBis IN :value")
@NamedQuery(name="Rev8DTOKlassenartenKatalog.primaryKeyQuery", query="SELECT e FROM Rev8DTOKlassenartenKatalog e WHERE e.ID = ?1")
@NamedQuery(name="Rev8DTOKlassenartenKatalog.all.migration", query="SELECT e FROM Rev8DTOKlassenartenKatalog e WHERE e.ID IS NOT NULL")
@JsonPropertyOrder({"ID","Kuerzel","Bezeichnung","gueltigVon","gueltigBis"})
public class Rev8DTOKlassenartenKatalog {

	/** ID der Klassenart */
	@Id
	@Column(name = "ID")
	@JsonProperty
	public Long ID;

	/** Kürzel der Klassenart */
	@Column(name = "Kuerzel")
	@JsonProperty
	public String Kuerzel;

	/** Die textuelle Bezeichnung der Klassenart */
	@Column(name = "Bezeichnung")
	@JsonProperty
	public String Bezeichnung;

	/** Gibt die Gültigkeit ab welchem Schuljahr an */
	@Column(name = "gueltigVon")
	@JsonProperty
	public Integer gueltigVon;

	/** Gibt die Gültigkeit bis zu welchem Schuljahr an */
	@Column(name = "gueltigBis")
	@JsonProperty
	public Integer gueltigBis;

	/**
	 * Erstellt ein neues Objekt der Klasse Rev8DTOKlassenartenKatalog ohne eine Initialisierung der Attribute.
	 */
	@SuppressWarnings("unused")
	private Rev8DTOKlassenartenKatalog() {
	}

	/**
	 * Erstellt ein neues Objekt der Klasse Rev8DTOKlassenartenKatalog ohne eine Initialisierung der Attribute.
	 * @param ID   der Wert für das Attribut ID
	 * @param Kuerzel   der Wert für das Attribut Kuerzel
	 * @param Bezeichnung   der Wert für das Attribut Bezeichnung
	 */
	public Rev8DTOKlassenartenKatalog(final Long ID, final String Kuerzel, final String Bezeichnung) {
		if (ID == null) { 
			throw new NullPointerException("ID must not be null");
		}
		this.ID = ID;
		if (Kuerzel == null) { 
			throw new NullPointerException("Kuerzel must not be null");
		}
		this.Kuerzel = Kuerzel;
		if (Bezeichnung == null) { 
			throw new NullPointerException("Bezeichnung must not be null");
		}
		this.Bezeichnung = Bezeichnung;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Rev8DTOKlassenartenKatalog other = (Rev8DTOKlassenartenKatalog) obj;
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
		return "Rev8DTOKlassenartenKatalog(ID=" + this.ID + ", Kuerzel=" + this.Kuerzel + ", Bezeichnung=" + this.Bezeichnung + ", gueltigVon=" + this.gueltigVon + ", gueltigBis=" + this.gueltigBis + ")";
	}

}