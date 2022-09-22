package de.nrw.schule.svws.db.dto.rev8.coretypes;

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
 * Diese Klasse dient als DTO für die Datenbanktabelle SchuelerStatus.
 * Sie wurde automatisch per Skript generiert und sollte nicht verändert werden, 
 * da sie aufgrund von Änderungen am DB-Schema ggf. neu generiert und überschrieben wird.
 */
@Entity
@Cacheable(DBEntityManager.use_db_caching)
@Table(name = "SchuelerStatus")
@NamedQuery(name="Rev8DTOSchuelerStatus.all", query="SELECT e FROM Rev8DTOSchuelerStatus e")
@NamedQuery(name="Rev8DTOSchuelerStatus.id", query="SELECT e FROM Rev8DTOSchuelerStatus e WHERE e.ID = :value")
@NamedQuery(name="Rev8DTOSchuelerStatus.id.multiple", query="SELECT e FROM Rev8DTOSchuelerStatus e WHERE e.ID IN :value")
@NamedQuery(name="Rev8DTOSchuelerStatus.bezeichnung", query="SELECT e FROM Rev8DTOSchuelerStatus e WHERE e.Bezeichnung = :value")
@NamedQuery(name="Rev8DTOSchuelerStatus.bezeichnung.multiple", query="SELECT e FROM Rev8DTOSchuelerStatus e WHERE e.Bezeichnung IN :value")
@NamedQuery(name="Rev8DTOSchuelerStatus.gueltigvon", query="SELECT e FROM Rev8DTOSchuelerStatus e WHERE e.gueltigVon = :value")
@NamedQuery(name="Rev8DTOSchuelerStatus.gueltigvon.multiple", query="SELECT e FROM Rev8DTOSchuelerStatus e WHERE e.gueltigVon IN :value")
@NamedQuery(name="Rev8DTOSchuelerStatus.gueltigbis", query="SELECT e FROM Rev8DTOSchuelerStatus e WHERE e.gueltigBis = :value")
@NamedQuery(name="Rev8DTOSchuelerStatus.gueltigbis.multiple", query="SELECT e FROM Rev8DTOSchuelerStatus e WHERE e.gueltigBis IN :value")
@NamedQuery(name="Rev8DTOSchuelerStatus.primaryKeyQuery", query="SELECT e FROM Rev8DTOSchuelerStatus e WHERE e.ID = ?1")
@NamedQuery(name="Rev8DTOSchuelerStatus.all.migration", query="SELECT e FROM Rev8DTOSchuelerStatus e WHERE e.ID IS NOT NULL")
@JsonPropertyOrder({"ID","Bezeichnung","gueltigVon","gueltigBis"})
public class Rev8DTOSchuelerStatus {

	/** ID des Schüler-Status */
	@Id
	@Column(name = "ID")
	@JsonProperty
	public Integer ID;

	/** Die Bezeichnung des Schüler-Status */
	@Column(name = "Bezeichnung")
	@JsonProperty
	public String Bezeichnung;

	/** Der Datensatz ist gültig ab dem Schuljahr */
	@Column(name = "gueltigVon")
	@JsonProperty
	public Integer gueltigVon;

	/** Der Datensatz ist gültig bis zu dem Schuljahr */
	@Column(name = "gueltigBis")
	@JsonProperty
	public Integer gueltigBis;

	/**
	 * Erstellt ein neues Objekt der Klasse Rev8DTOSchuelerStatus ohne eine Initialisierung der Attribute.
	 */
	@SuppressWarnings("unused")
	private Rev8DTOSchuelerStatus() {
	}

	/**
	 * Erstellt ein neues Objekt der Klasse Rev8DTOSchuelerStatus ohne eine Initialisierung der Attribute.
	 * @param ID   der Wert für das Attribut ID
	 */
	public Rev8DTOSchuelerStatus(final Integer ID) {
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
		Rev8DTOSchuelerStatus other = (Rev8DTOSchuelerStatus) obj;
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
		return "Rev8DTOSchuelerStatus(ID=" + this.ID + ", Bezeichnung=" + this.Bezeichnung + ", gueltigVon=" + this.gueltigVon + ", gueltigBis=" + this.gueltigBis + ")";
	}

}