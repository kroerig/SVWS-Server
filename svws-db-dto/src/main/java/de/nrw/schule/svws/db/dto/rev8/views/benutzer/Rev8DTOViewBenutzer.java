package de.nrw.schule.svws.db.dto.rev8.views.benutzer;

import de.nrw.schule.svws.db.DBEntityManager;
import de.nrw.schule.svws.db.converter.current.Boolean01Converter;


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
import de.nrw.schule.svws.csv.converter.current.Boolean01ConverterSerializer;
import de.nrw.schule.svws.csv.converter.current.Boolean01ConverterDeserializer;

/**
 * Diese Klasse dient als DTO für die Datenbank-View V_Benutzer.
 * Sie wurde automatisch per Skript generiert und sollte nicht verändert werden, 
 * da sie aufgrund von Änderungen am DB-Schema ggf. neu generiert und überschrieben wird.
 */
@Entity
@Cacheable(DBEntityManager.use_db_caching)
@Table(name = "V_Benutzer")
@NamedQuery(name="Rev8DTOViewBenutzer.all", query="SELECT e FROM Rev8DTOViewBenutzer e")
@NamedQuery(name="Rev8DTOViewBenutzer.id", query="SELECT e FROM Rev8DTOViewBenutzer e WHERE e.ID = :value")
@NamedQuery(name="Rev8DTOViewBenutzer.id.multiple", query="SELECT e FROM Rev8DTOViewBenutzer e WHERE e.ID IN :value")
@NamedQuery(name="Rev8DTOViewBenutzer.anzeigename", query="SELECT e FROM Rev8DTOViewBenutzer e WHERE e.AnzeigeName = :value")
@NamedQuery(name="Rev8DTOViewBenutzer.anzeigename.multiple", query="SELECT e FROM Rev8DTOViewBenutzer e WHERE e.AnzeigeName IN :value")
@NamedQuery(name="Rev8DTOViewBenutzer.benutzername", query="SELECT e FROM Rev8DTOViewBenutzer e WHERE e.Benutzername = :value")
@NamedQuery(name="Rev8DTOViewBenutzer.benutzername.multiple", query="SELECT e FROM Rev8DTOViewBenutzer e WHERE e.Benutzername IN :value")
@NamedQuery(name="Rev8DTOViewBenutzer.passwordhash", query="SELECT e FROM Rev8DTOViewBenutzer e WHERE e.PasswordHash = :value")
@NamedQuery(name="Rev8DTOViewBenutzer.passwordhash.multiple", query="SELECT e FROM Rev8DTOViewBenutzer e WHERE e.PasswordHash IN :value")
@NamedQuery(name="Rev8DTOViewBenutzer.istadmin", query="SELECT e FROM Rev8DTOViewBenutzer e WHERE e.IstAdmin = :value")
@NamedQuery(name="Rev8DTOViewBenutzer.istadmin.multiple", query="SELECT e FROM Rev8DTOViewBenutzer e WHERE e.IstAdmin IN :value")
@NamedQuery(name="Rev8DTOViewBenutzer.primaryKeyQuery", query="SELECT e FROM Rev8DTOViewBenutzer e WHERE e.ID = ?1")
@JsonPropertyOrder({"ID","AnzeigeName","Benutzername","PasswordHash","IstAdmin"})
public class Rev8DTOViewBenutzer {

	/** Die eindeutige ID des Benutzers */
	@Id
	@Column(name = "ID")
	@JsonProperty
	public Long ID;

	/** Der Anzeige-Name des Benutzers (z.B. Max Mustermann) */
	@Column(name = "AnzeigeName")
	@JsonProperty
	public String AnzeigeName;

	/** Der Anmeldename des Benutzers (z.B. max.mustermann) */
	@Column(name = "Benutzername")
	@JsonProperty
	public String Benutzername;

	/** Der bcrypt-Password-Hash zur Überprüfung des Benutzer-Kennwortes */
	@Column(name = "PasswordHash")
	@JsonProperty
	public String PasswordHash;

	/** Gibt an, ob es sich um einen administrativen Benutzer handelt oder nicht */
	@Column(name = "IstAdmin")
	@JsonProperty
	@Convert(converter=Boolean01Converter.class)
	@JsonSerialize(using=Boolean01ConverterSerializer.class)
	@JsonDeserialize(using=Boolean01ConverterDeserializer.class)
	public Boolean IstAdmin;

	/**
	 * Erstellt ein neues Objekt der Klasse Rev8DTOViewBenutzer ohne eine Initialisierung der Attribute.
	 */
	private Rev8DTOViewBenutzer() {
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Rev8DTOViewBenutzer other = (Rev8DTOViewBenutzer) obj;
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
		return "Rev8DTOViewBenutzer(ID=" + this.ID + ", AnzeigeName=" + this.AnzeigeName + ", Benutzername=" + this.Benutzername + ", PasswordHash=" + this.PasswordHash + ", IstAdmin=" + this.IstAdmin + ")";
	}

}