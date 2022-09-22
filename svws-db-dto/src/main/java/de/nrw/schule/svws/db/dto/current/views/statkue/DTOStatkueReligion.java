package de.nrw.schule.svws.db.dto.current.views.statkue;

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
 * Diese Klasse dient als DTO für die Datenbank-View Statkue_Religionen.
 * Sie wurde automatisch per Skript generiert und sollte nicht verändert werden, 
 * da sie aufgrund von Änderungen am DB-Schema ggf. neu generiert und überschrieben wird.
 */
@Entity
@Cacheable(DBEntityManager.use_db_caching)
@Table(name = "Statkue_Religionen")
@NamedQuery(name="DTOStatkueReligion.all", query="SELECT e FROM DTOStatkueReligion e")
@NamedQuery(name="DTOStatkueReligion.schluessel", query="SELECT e FROM DTOStatkueReligion e WHERE e.Schluessel = :value")
@NamedQuery(name="DTOStatkueReligion.schluessel.multiple", query="SELECT e FROM DTOStatkueReligion e WHERE e.Schluessel IN :value")
@NamedQuery(name="DTOStatkueReligion.klartext", query="SELECT e FROM DTOStatkueReligion e WHERE e.Klartext = :value")
@NamedQuery(name="DTOStatkueReligion.klartext.multiple", query="SELECT e FROM DTOStatkueReligion e WHERE e.Klartext IN :value")
@NamedQuery(name="DTOStatkueReligion.geaendert", query="SELECT e FROM DTOStatkueReligion e WHERE e.geaendert = :value")
@NamedQuery(name="DTOStatkueReligion.geaendert.multiple", query="SELECT e FROM DTOStatkueReligion e WHERE e.geaendert IN :value")
@NamedQuery(name="DTOStatkueReligion.gueltigvon", query="SELECT e FROM DTOStatkueReligion e WHERE e.gueltigVon = :value")
@NamedQuery(name="DTOStatkueReligion.gueltigvon.multiple", query="SELECT e FROM DTOStatkueReligion e WHERE e.gueltigVon IN :value")
@NamedQuery(name="DTOStatkueReligion.gueltigbis", query="SELECT e FROM DTOStatkueReligion e WHERE e.gueltigBis = :value")
@NamedQuery(name="DTOStatkueReligion.gueltigbis.multiple", query="SELECT e FROM DTOStatkueReligion e WHERE e.gueltigBis IN :value")
@NamedQuery(name="DTOStatkueReligion.primaryKeyQuery", query="SELECT e FROM DTOStatkueReligion e WHERE e.Schluessel = ?1")
@JsonPropertyOrder({"Schluessel","Klartext","geaendert","gueltigVon","gueltigBis"})
public class DTOStatkueReligion {

	/** Das Kürzel der Religion bzw. Konfession */
	@Id
	@Column(name = "Schluessel")
	@JsonProperty
	public String Schluessel;

	/** Die Beschreibung der Religion bzw. Konfession */
	@Column(name = "Klartext")
	@JsonProperty
	public String Klartext;

	/** Datum der letzten Änderung (hier zur Kompatibilität vorhanden) */
	@Column(name = "geaendert")
	@JsonProperty
	public String geaendert;

	/** Gibt die Gültigkeit ab welchem Schuljahr an */
	@Column(name = "gueltigVon")
	@JsonProperty
	public String gueltigVon;

	/** Gibt die Gültigkeit bis zu welchem Schuljahr an */
	@Column(name = "gueltigBis")
	@JsonProperty
	public String gueltigBis;

	/**
	 * Erstellt ein neues Objekt der Klasse DTOStatkueReligion ohne eine Initialisierung der Attribute.
	 */
	private DTOStatkueReligion() {
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DTOStatkueReligion other = (DTOStatkueReligion) obj;
		if (Schluessel == null) {
			if (other.Schluessel != null)
				return false;
		} else if (!Schluessel.equals(other.Schluessel))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Schluessel == null) ? 0 : Schluessel.hashCode());
		return result;
	}


	/**
	 * Konvertiert das Objekt in einen String. Dieser kann z.B. für Debug-Ausgaben genutzt werden.
	 *
	 * @return die String-Repräsentation des Objektes
	 */
	@Override
	public String toString() {
		return "DTOStatkueReligion(Schluessel=" + this.Schluessel + ", Klartext=" + this.Klartext + ", geaendert=" + this.geaendert + ", gueltigVon=" + this.gueltigVon + ", gueltigBis=" + this.gueltigBis + ")";
	}

}