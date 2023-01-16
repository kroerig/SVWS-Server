package de.nrw.schule.svws.db.dto.rev9.gost.klausurplanung;

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
 * Diese Klasse dient als DTO für die Datenbanktabelle Gost_Klausuren_NtaZeiten.
 * Sie wurde automatisch per Skript generiert und sollte nicht verändert werden, 
 * da sie aufgrund von Änderungen am DB-Schema ggf. neu generiert und überschrieben wird.
 */
@Entity
@IdClass(Rev9DTOGostKlausurenNtaZeitenPK.class)
@Cacheable(DBEntityManager.use_db_caching)
@Table(name = "Gost_Klausuren_NtaZeiten")
@NamedQuery(name="Rev9DTOGostKlausurenNtaZeiten.all", query="SELECT e FROM Rev9DTOGostKlausurenNtaZeiten e")
@NamedQuery(name="Rev9DTOGostKlausurenNtaZeiten.schueler_id", query="SELECT e FROM Rev9DTOGostKlausurenNtaZeiten e WHERE e.Schueler_ID = :value")
@NamedQuery(name="Rev9DTOGostKlausurenNtaZeiten.schueler_id.multiple", query="SELECT e FROM Rev9DTOGostKlausurenNtaZeiten e WHERE e.Schueler_ID IN :value")
@NamedQuery(name="Rev9DTOGostKlausurenNtaZeiten.vorgabe_id", query="SELECT e FROM Rev9DTOGostKlausurenNtaZeiten e WHERE e.Vorgabe_ID = :value")
@NamedQuery(name="Rev9DTOGostKlausurenNtaZeiten.vorgabe_id.multiple", query="SELECT e FROM Rev9DTOGostKlausurenNtaZeiten e WHERE e.Vorgabe_ID IN :value")
@NamedQuery(name="Rev9DTOGostKlausurenNtaZeiten.zeitzugabe", query="SELECT e FROM Rev9DTOGostKlausurenNtaZeiten e WHERE e.Zeitzugabe = :value")
@NamedQuery(name="Rev9DTOGostKlausurenNtaZeiten.zeitzugabe.multiple", query="SELECT e FROM Rev9DTOGostKlausurenNtaZeiten e WHERE e.Zeitzugabe IN :value")
@NamedQuery(name="Rev9DTOGostKlausurenNtaZeiten.bemerkungen", query="SELECT e FROM Rev9DTOGostKlausurenNtaZeiten e WHERE e.Bemerkungen = :value")
@NamedQuery(name="Rev9DTOGostKlausurenNtaZeiten.bemerkungen.multiple", query="SELECT e FROM Rev9DTOGostKlausurenNtaZeiten e WHERE e.Bemerkungen IN :value")
@NamedQuery(name="Rev9DTOGostKlausurenNtaZeiten.primaryKeyQuery", query="SELECT e FROM Rev9DTOGostKlausurenNtaZeiten e WHERE e.Schueler_ID = ?1 AND e.Vorgabe_ID = ?2")
@NamedQuery(name="Rev9DTOGostKlausurenNtaZeiten.all.migration", query="SELECT e FROM Rev9DTOGostKlausurenNtaZeiten e WHERE e.Schueler_ID IS NOT NULL AND e.Vorgabe_ID IS NOT NULL")
@JsonPropertyOrder({"Schueler_ID","Vorgabe_ID","Zeitzugabe","Bemerkungen"})
public class Rev9DTOGostKlausurenNtaZeiten {

	/** ID des Schülers */
	@Id
	@Column(name = "Schueler_ID")
	@JsonProperty
	public Long Schueler_ID;

	/** ID der Klausurvorgaben */
	@Id
	@Column(name = "Vorgabe_ID")
	@JsonProperty
	public Long Vorgabe_ID;

	/** Das Dauer der Zeitzugabe in Minuten */
	@Column(name = "Zeitzugabe")
	@JsonProperty
	public Integer Zeitzugabe;

	/** Text für Ergänzungen/Bemerkungen zum Nachteilsausgleich */
	@Column(name = "Bemerkungen")
	@JsonProperty
	public String Bemerkungen;

	/**
	 * Erstellt ein neues Objekt der Klasse Rev9DTOGostKlausurenNtaZeiten ohne eine Initialisierung der Attribute.
	 */
	@SuppressWarnings("unused")
	private Rev9DTOGostKlausurenNtaZeiten() {
	}

	/**
	 * Erstellt ein neues Objekt der Klasse Rev9DTOGostKlausurenNtaZeiten ohne eine Initialisierung der Attribute.
	 * @param Schueler_ID   der Wert für das Attribut Schueler_ID
	 * @param Vorgabe_ID   der Wert für das Attribut Vorgabe_ID
	 * @param Zeitzugabe   der Wert für das Attribut Zeitzugabe
	 */
	public Rev9DTOGostKlausurenNtaZeiten(final Long Schueler_ID, final Long Vorgabe_ID, final Integer Zeitzugabe) {
		if (Schueler_ID == null) { 
			throw new NullPointerException("Schueler_ID must not be null");
		}
		this.Schueler_ID = Schueler_ID;
		if (Vorgabe_ID == null) { 
			throw new NullPointerException("Vorgabe_ID must not be null");
		}
		this.Vorgabe_ID = Vorgabe_ID;
		if (Zeitzugabe == null) { 
			throw new NullPointerException("Zeitzugabe must not be null");
		}
		this.Zeitzugabe = Zeitzugabe;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Rev9DTOGostKlausurenNtaZeiten other = (Rev9DTOGostKlausurenNtaZeiten) obj;
		if (Schueler_ID == null) {
			if (other.Schueler_ID != null)
				return false;
		} else if (!Schueler_ID.equals(other.Schueler_ID))
			return false;

		if (Vorgabe_ID == null) {
			if (other.Vorgabe_ID != null)
				return false;
		} else if (!Vorgabe_ID.equals(other.Vorgabe_ID))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Schueler_ID == null) ? 0 : Schueler_ID.hashCode());

		result = prime * result + ((Vorgabe_ID == null) ? 0 : Vorgabe_ID.hashCode());
		return result;
	}


	/**
	 * Konvertiert das Objekt in einen String. Dieser kann z.B. für Debug-Ausgaben genutzt werden.
	 *
	 * @return die String-Repräsentation des Objektes
	 */
	@Override
	public String toString() {
		return "Rev9DTOGostKlausurenNtaZeiten(Schueler_ID=" + this.Schueler_ID + ", Vorgabe_ID=" + this.Vorgabe_ID + ", Zeitzugabe=" + this.Zeitzugabe + ", Bemerkungen=" + this.Bemerkungen + ")";
	}

}