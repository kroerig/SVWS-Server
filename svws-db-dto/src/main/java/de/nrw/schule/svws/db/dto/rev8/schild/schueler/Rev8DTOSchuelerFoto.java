package de.nrw.schule.svws.db.dto.rev8.schild.schueler;

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
 * Diese Klasse dient als DTO für die Datenbanktabelle SchuelerFotos.
 * Sie wurde automatisch per Skript generiert und sollte nicht verändert werden, 
 * da sie aufgrund von Änderungen am DB-Schema ggf. neu generiert und überschrieben wird.
 */
@Entity
@Cacheable(DBEntityManager.use_db_caching)
@Table(name = "SchuelerFotos")
@NamedQuery(name="Rev8DTOSchuelerFoto.all", query="SELECT e FROM Rev8DTOSchuelerFoto e")
@NamedQuery(name="Rev8DTOSchuelerFoto.schueler_id", query="SELECT e FROM Rev8DTOSchuelerFoto e WHERE e.Schueler_ID = :value")
@NamedQuery(name="Rev8DTOSchuelerFoto.schueler_id.multiple", query="SELECT e FROM Rev8DTOSchuelerFoto e WHERE e.Schueler_ID IN :value")
@NamedQuery(name="Rev8DTOSchuelerFoto.fotobase64", query="SELECT e FROM Rev8DTOSchuelerFoto e WHERE e.FotoBase64 = :value")
@NamedQuery(name="Rev8DTOSchuelerFoto.fotobase64.multiple", query="SELECT e FROM Rev8DTOSchuelerFoto e WHERE e.FotoBase64 IN :value")
@NamedQuery(name="Rev8DTOSchuelerFoto.primaryKeyQuery", query="SELECT e FROM Rev8DTOSchuelerFoto e WHERE e.Schueler_ID = ?1")
@NamedQuery(name="Rev8DTOSchuelerFoto.all.migration", query="SELECT e FROM Rev8DTOSchuelerFoto e WHERE e.Schueler_ID IS NOT NULL")
@JsonPropertyOrder({"Schueler_ID","FotoBase64"})
public class Rev8DTOSchuelerFoto {

	/** SchülerID zum Foto */
	@Id
	@Column(name = "Schueler_ID")
	@JsonProperty
	public Long Schueler_ID;

	/** Schülerfoto im Base64-Format */
	@Column(name = "FotoBase64")
	@JsonProperty
	public String FotoBase64;

	/**
	 * Erstellt ein neues Objekt der Klasse Rev8DTOSchuelerFoto ohne eine Initialisierung der Attribute.
	 */
	@SuppressWarnings("unused")
	private Rev8DTOSchuelerFoto() {
	}

	/**
	 * Erstellt ein neues Objekt der Klasse Rev8DTOSchuelerFoto ohne eine Initialisierung der Attribute.
	 * @param Schueler_ID   der Wert für das Attribut Schueler_ID
	 */
	public Rev8DTOSchuelerFoto(final Long Schueler_ID) {
		if (Schueler_ID == null) { 
			throw new NullPointerException("Schueler_ID must not be null");
		}
		this.Schueler_ID = Schueler_ID;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Rev8DTOSchuelerFoto other = (Rev8DTOSchuelerFoto) obj;
		if (Schueler_ID == null) {
			if (other.Schueler_ID != null)
				return false;
		} else if (!Schueler_ID.equals(other.Schueler_ID))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Schueler_ID == null) ? 0 : Schueler_ID.hashCode());
		return result;
	}


	/**
	 * Konvertiert das Objekt in einen String. Dieser kann z.B. für Debug-Ausgaben genutzt werden.
	 *
	 * @return die String-Repräsentation des Objektes
	 */
	@Override
	public String toString() {
		return "Rev8DTOSchuelerFoto(Schueler_ID=" + this.Schueler_ID + ", FotoBase64=" + this.FotoBase64 + ")";
	}

}