package de.nrw.schule.svws.db.dto.rev8.views.statkue;

import de.nrw.schule.svws.db.DBEntityManager;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
/**
 * Diese Klasse dient als DTO für die Datenbank-View Statkue_Herkunftsart.
 * Sie wurde automatisch per Skript generiert und sollte nicht verändert werden, 
 * da sie aufgrund von Änderungen am DB-Schema ggf. neu generiert und überschrieben wird.
 */
@Entity
@IdClass(Rev8DTOStatkueHerkunftsartPK.class)
@Cacheable(DBEntityManager.use_db_caching)
@Table(name = "Statkue_Herkunftsart")
@NamedQuery(name="Rev8DTOStatkueHerkunftsart.all", query="SELECT e FROM Rev8DTOStatkueHerkunftsart e")
@NamedQuery(name="Rev8DTOStatkueHerkunftsart.sf", query="SELECT e FROM Rev8DTOStatkueHerkunftsart e WHERE e.SF = :value")
@NamedQuery(name="Rev8DTOStatkueHerkunftsart.sf.multiple", query="SELECT e FROM Rev8DTOStatkueHerkunftsart e WHERE e.SF IN :value")
@NamedQuery(name="Rev8DTOStatkueHerkunftsart.art", query="SELECT e FROM Rev8DTOStatkueHerkunftsart e WHERE e.Art = :value")
@NamedQuery(name="Rev8DTOStatkueHerkunftsart.art.multiple", query="SELECT e FROM Rev8DTOStatkueHerkunftsart e WHERE e.Art IN :value")
@NamedQuery(name="Rev8DTOStatkueHerkunftsart.beschreibung", query="SELECT e FROM Rev8DTOStatkueHerkunftsart e WHERE e.Beschreibung = :value")
@NamedQuery(name="Rev8DTOStatkueHerkunftsart.beschreibung.multiple", query="SELECT e FROM Rev8DTOStatkueHerkunftsart e WHERE e.Beschreibung IN :value")
@NamedQuery(name="Rev8DTOStatkueHerkunftsart.flag", query="SELECT e FROM Rev8DTOStatkueHerkunftsart e WHERE e.Flag = :value")
@NamedQuery(name="Rev8DTOStatkueHerkunftsart.flag.multiple", query="SELECT e FROM Rev8DTOStatkueHerkunftsart e WHERE e.Flag IN :value")
@NamedQuery(name="Rev8DTOStatkueHerkunftsart.sortierung", query="SELECT e FROM Rev8DTOStatkueHerkunftsart e WHERE e.Sortierung = :value")
@NamedQuery(name="Rev8DTOStatkueHerkunftsart.sortierung.multiple", query="SELECT e FROM Rev8DTOStatkueHerkunftsart e WHERE e.Sortierung IN :value")
@NamedQuery(name="Rev8DTOStatkueHerkunftsart.geaendert", query="SELECT e FROM Rev8DTOStatkueHerkunftsart e WHERE e.geaendert = :value")
@NamedQuery(name="Rev8DTOStatkueHerkunftsart.geaendert.multiple", query="SELECT e FROM Rev8DTOStatkueHerkunftsart e WHERE e.geaendert IN :value")
@NamedQuery(name="Rev8DTOStatkueHerkunftsart.gueltigvon", query="SELECT e FROM Rev8DTOStatkueHerkunftsart e WHERE e.gueltigVon = :value")
@NamedQuery(name="Rev8DTOStatkueHerkunftsart.gueltigvon.multiple", query="SELECT e FROM Rev8DTOStatkueHerkunftsart e WHERE e.gueltigVon IN :value")
@NamedQuery(name="Rev8DTOStatkueHerkunftsart.gueltigbis", query="SELECT e FROM Rev8DTOStatkueHerkunftsart e WHERE e.gueltigBis = :value")
@NamedQuery(name="Rev8DTOStatkueHerkunftsart.gueltigbis.multiple", query="SELECT e FROM Rev8DTOStatkueHerkunftsart e WHERE e.gueltigBis IN :value")
@NamedQuery(name="Rev8DTOStatkueHerkunftsart.primaryKeyQuery", query="SELECT e FROM Rev8DTOStatkueHerkunftsart e WHERE e.SF = ?1 AND e.Art = ?2")
@JsonPropertyOrder({"SF","Art","Beschreibung","Flag","Sortierung","geaendert","gueltigVon","gueltigBis"})
public class Rev8DTOStatkueHerkunftsart {

	/** Zulässige Schulform für die Herkunft */
	@Id
	@Column(name = "SF")
	@JsonProperty
	public String SF;

	/** Das Kürzel für die Herkunftsart */
	@Id
	@Column(name = "Art")
	@JsonProperty
	public String Art;

	/** Textuelle Beschreibung der Herkunftsart */
	@Column(name = "Beschreibung")
	@JsonProperty
	public String Beschreibung;

	/** Ein Flag (hier zur Kompatibilität vorhanden) */
	@Column(name = "Flag")
	@JsonProperty
	public String Flag;

	/** Die Sortierung (hier zur Kompatibilität vorhanden) */
	@Column(name = "Sortierung")
	@JsonProperty
	public Integer Sortierung;

	/** Das Datum der letzten Änderung (hier zur Kompatibilität vorhanden) */
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
	 * Erstellt ein neues Objekt der Klasse Rev8DTOStatkueHerkunftsart ohne eine Initialisierung der Attribute.
	 */
	private Rev8DTOStatkueHerkunftsart() {
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Rev8DTOStatkueHerkunftsart other = (Rev8DTOStatkueHerkunftsart) obj;
		if (SF == null) {
			if (other.SF != null)
				return false;
		} else if (!SF.equals(other.SF))
			return false;

		if (Art == null) {
			if (other.Art != null)
				return false;
		} else if (!Art.equals(other.Art))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((SF == null) ? 0 : SF.hashCode());

		result = prime * result + ((Art == null) ? 0 : Art.hashCode());
		return result;
	}


	/**
	 * Konvertiert das Objekt in einen String. Dieser kann z.B. für Debug-Ausgaben genutzt werden.
	 *
	 * @return die String-Repräsentation des Objektes
	 */
	@Override
	public String toString() {
		return "Rev8DTOStatkueHerkunftsart(SF=" + this.SF + ", Art=" + this.Art + ", Beschreibung=" + this.Beschreibung + ", Flag=" + this.Flag + ", Sortierung=" + this.Sortierung + ", geaendert=" + this.geaendert + ", gueltigVon=" + this.gueltigVon + ", gueltigBis=" + this.gueltigBis + ")";
	}

}