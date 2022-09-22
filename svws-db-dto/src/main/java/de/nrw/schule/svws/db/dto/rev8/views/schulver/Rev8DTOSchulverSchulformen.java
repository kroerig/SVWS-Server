package de.nrw.schule.svws.db.dto.rev8.views.schulver;

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
 * Diese Klasse dient als DTO für die Datenbank-View Schulver_Schulformen.
 * Sie wurde automatisch per Skript generiert und sollte nicht verändert werden, 
 * da sie aufgrund von Änderungen am DB-Schema ggf. neu generiert und überschrieben wird.
 */
@Entity
@Cacheable(DBEntityManager.use_db_caching)
@Table(name = "Schulver_Schulformen")
@NamedQuery(name="Rev8DTOSchulverSchulformen.all", query="SELECT e FROM Rev8DTOSchulverSchulformen e")
@NamedQuery(name="Rev8DTOSchulverSchulformen.schulform", query="SELECT e FROM Rev8DTOSchulverSchulformen e WHERE e.Schulform = :value")
@NamedQuery(name="Rev8DTOSchulverSchulformen.schulform.multiple", query="SELECT e FROM Rev8DTOSchulverSchulformen e WHERE e.Schulform IN :value")
@NamedQuery(name="Rev8DTOSchulverSchulformen.sf", query="SELECT e FROM Rev8DTOSchulverSchulformen e WHERE e.SF = :value")
@NamedQuery(name="Rev8DTOSchulverSchulformen.sf.multiple", query="SELECT e FROM Rev8DTOSchulverSchulformen e WHERE e.SF IN :value")
@NamedQuery(name="Rev8DTOSchulverSchulformen.bezeichnung", query="SELECT e FROM Rev8DTOSchulverSchulformen e WHERE e.Bezeichnung = :value")
@NamedQuery(name="Rev8DTOSchulverSchulformen.bezeichnung.multiple", query="SELECT e FROM Rev8DTOSchulverSchulformen e WHERE e.Bezeichnung IN :value")
@NamedQuery(name="Rev8DTOSchulverSchulformen.flag", query="SELECT e FROM Rev8DTOSchulverSchulformen e WHERE e.Flag = :value")
@NamedQuery(name="Rev8DTOSchulverSchulformen.flag.multiple", query="SELECT e FROM Rev8DTOSchulverSchulformen e WHERE e.Flag IN :value")
@NamedQuery(name="Rev8DTOSchulverSchulformen.geaendert", query="SELECT e FROM Rev8DTOSchulverSchulformen e WHERE e.geaendert = :value")
@NamedQuery(name="Rev8DTOSchulverSchulformen.geaendert.multiple", query="SELECT e FROM Rev8DTOSchulverSchulformen e WHERE e.geaendert IN :value")
@NamedQuery(name="Rev8DTOSchulverSchulformen.gueltigvon", query="SELECT e FROM Rev8DTOSchulverSchulformen e WHERE e.gueltigVon = :value")
@NamedQuery(name="Rev8DTOSchulverSchulformen.gueltigvon.multiple", query="SELECT e FROM Rev8DTOSchulverSchulformen e WHERE e.gueltigVon IN :value")
@NamedQuery(name="Rev8DTOSchulverSchulformen.gueltigbis", query="SELECT e FROM Rev8DTOSchulverSchulformen e WHERE e.gueltigBis = :value")
@NamedQuery(name="Rev8DTOSchulverSchulformen.gueltigbis.multiple", query="SELECT e FROM Rev8DTOSchulverSchulformen e WHERE e.gueltigBis IN :value")
@NamedQuery(name="Rev8DTOSchulverSchulformen.primaryKeyQuery", query="SELECT e FROM Rev8DTOSchulverSchulformen e WHERE e.Schulform = ?1")
@JsonPropertyOrder({"Schulform","SF","Bezeichnung","Flag","geaendert","gueltigVon","gueltigBis"})
public class Rev8DTOSchulverSchulformen {

	/** Eindeutige Nummer der Schulform */
	@Id
	@Column(name = "Schulform")
	@JsonProperty
	public String Schulform;

	/** Das Kürzel der Schulform */
	@Column(name = "SF")
	@JsonProperty
	public String SF;

	/** Die Bezeichnung der Schulform */
	@Column(name = "Bezeichnung")
	@JsonProperty
	public String Bezeichnung;

	/** Flag zur Kompatibilität zur Schulver */
	@Column(name = "Flag")
	@JsonProperty
	public String Flag;

	/** Das Datum der letzten Änderung */
	@Column(name = "geaendert")
	@JsonProperty
	public String geaendert;

	/** Gibt das Schuljahr an, ab dem die Schulform verwendet werden kann oder null, falls es keine Einschränkung gibt */
	@Column(name = "gueltigVon")
	@JsonProperty
	public Integer gueltigVon;

	/** Gibt das Schuljahr an, bis zu welchem die Schulform verwendet werden kann oder null, falls es keine Einschränkung gibt */
	@Column(name = "gueltigBis")
	@JsonProperty
	public Integer gueltigBis;

	/**
	 * Erstellt ein neues Objekt der Klasse Rev8DTOSchulverSchulformen ohne eine Initialisierung der Attribute.
	 */
	private Rev8DTOSchulverSchulformen() {
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Rev8DTOSchulverSchulformen other = (Rev8DTOSchulverSchulformen) obj;
		if (Schulform == null) {
			if (other.Schulform != null)
				return false;
		} else if (!Schulform.equals(other.Schulform))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Schulform == null) ? 0 : Schulform.hashCode());
		return result;
	}


	/**
	 * Konvertiert das Objekt in einen String. Dieser kann z.B. für Debug-Ausgaben genutzt werden.
	 *
	 * @return die String-Repräsentation des Objektes
	 */
	@Override
	public String toString() {
		return "Rev8DTOSchulverSchulformen(Schulform=" + this.Schulform + ", SF=" + this.SF + ", Bezeichnung=" + this.Bezeichnung + ", Flag=" + this.Flag + ", geaendert=" + this.geaendert + ", gueltigVon=" + this.gueltigVon + ", gueltigBis=" + this.gueltigBis + ")";
	}

}