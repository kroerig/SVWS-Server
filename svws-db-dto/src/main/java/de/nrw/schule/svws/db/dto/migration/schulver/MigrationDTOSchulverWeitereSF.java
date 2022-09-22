package de.nrw.schule.svws.db.dto.migration.schulver;

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
 * Diese Klasse dient als DTO für die Datenbanktabelle Schulver_WeitereSF.
 * Sie wurde automatisch per Skript generiert und sollte nicht verändert werden, 
 * da sie aufgrund von Änderungen am DB-Schema ggf. neu generiert und überschrieben wird.
 */
@Entity
@IdClass(MigrationDTOSchulverWeitereSFPK.class)
@Cacheable(DBEntityManager.use_db_caching)
@Table(name = "Schulver_WeitereSF")
@NamedQuery(name="MigrationDTOSchulverWeitereSF.all", query="SELECT e FROM MigrationDTOSchulverWeitereSF e")
@NamedQuery(name="MigrationDTOSchulverWeitereSF.snr", query="SELECT e FROM MigrationDTOSchulverWeitereSF e WHERE e.SNR = :value")
@NamedQuery(name="MigrationDTOSchulverWeitereSF.snr.multiple", query="SELECT e FROM MigrationDTOSchulverWeitereSF e WHERE e.SNR IN :value")
@NamedQuery(name="MigrationDTOSchulverWeitereSF.sgl", query="SELECT e FROM MigrationDTOSchulverWeitereSF e WHERE e.SGL = :value")
@NamedQuery(name="MigrationDTOSchulverWeitereSF.sgl.multiple", query="SELECT e FROM MigrationDTOSchulverWeitereSF e WHERE e.SGL IN :value")
@NamedQuery(name="MigrationDTOSchulverWeitereSF.fsp", query="SELECT e FROM MigrationDTOSchulverWeitereSF e WHERE e.FSP = :value")
@NamedQuery(name="MigrationDTOSchulverWeitereSF.fsp.multiple", query="SELECT e FROM MigrationDTOSchulverWeitereSF e WHERE e.FSP IN :value")
@NamedQuery(name="MigrationDTOSchulverWeitereSF.gueltigvon", query="SELECT e FROM MigrationDTOSchulverWeitereSF e WHERE e.gueltigVon = :value")
@NamedQuery(name="MigrationDTOSchulverWeitereSF.gueltigvon.multiple", query="SELECT e FROM MigrationDTOSchulverWeitereSF e WHERE e.gueltigVon IN :value")
@NamedQuery(name="MigrationDTOSchulverWeitereSF.gueltigbis", query="SELECT e FROM MigrationDTOSchulverWeitereSF e WHERE e.gueltigBis = :value")
@NamedQuery(name="MigrationDTOSchulverWeitereSF.gueltigbis.multiple", query="SELECT e FROM MigrationDTOSchulverWeitereSF e WHERE e.gueltigBis IN :value")
@NamedQuery(name="MigrationDTOSchulverWeitereSF.primaryKeyQuery", query="SELECT e FROM MigrationDTOSchulverWeitereSF e WHERE e.SNR = ?1 AND e.SGL = ?2 AND e.FSP = ?3")
@NamedQuery(name="MigrationDTOSchulverWeitereSF.all.migration", query="SELECT e FROM MigrationDTOSchulverWeitereSF e WHERE e.SNR IS NOT NULL AND e.SGL IS NOT NULL AND e.FSP IS NOT NULL")
@JsonPropertyOrder({"SNR","SGL","FSP","gueltigVon","gueltigBis"})
public class MigrationDTOSchulverWeitereSF {

	/** Schulver Tabelle IT.NRW: Schulnummer der Schule */
	@Id
	@Column(name = "SNR")
	@JsonProperty
	public String SNR;

	/** Schulver Tabelle IT.NRW: Gliederung Statistikkürzel */
	@Id
	@Column(name = "SGL")
	@JsonProperty
	public String SGL;

	/** Schulver Tabelle IT.NRW: Förderschwerpunkt ASD-Kürzel */
	@Id
	@Column(name = "FSP")
	@JsonProperty
	public String FSP;

	/** Schulver Tabelle IT.NRW: Gibt die Gültigkeit ab welchem Schuljahr an */
	@Column(name = "gueltigVon")
	@JsonProperty
	public Integer gueltigVon;

	/** Schulver Tabelle IT.NRW: Gibt die Gültigkeit bis zu welchem Schuljahr an */
	@Column(name = "gueltigBis")
	@JsonProperty
	public Integer gueltigBis;

	/**
	 * Erstellt ein neues Objekt der Klasse MigrationDTOSchulverWeitereSF ohne eine Initialisierung der Attribute.
	 */
	@SuppressWarnings("unused")
	private MigrationDTOSchulverWeitereSF() {
	}

	/**
	 * Erstellt ein neues Objekt der Klasse MigrationDTOSchulverWeitereSF ohne eine Initialisierung der Attribute.
	 * @param SNR   der Wert für das Attribut SNR
	 * @param SGL   der Wert für das Attribut SGL
	 * @param FSP   der Wert für das Attribut FSP
	 */
	public MigrationDTOSchulverWeitereSF(final String SNR, final String SGL, final String FSP) {
		if (SNR == null) { 
			throw new NullPointerException("SNR must not be null");
		}
		this.SNR = SNR;
		if (SGL == null) { 
			throw new NullPointerException("SGL must not be null");
		}
		this.SGL = SGL;
		if (FSP == null) { 
			throw new NullPointerException("FSP must not be null");
		}
		this.FSP = FSP;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MigrationDTOSchulverWeitereSF other = (MigrationDTOSchulverWeitereSF) obj;
		if (SNR == null) {
			if (other.SNR != null)
				return false;
		} else if (!SNR.equals(other.SNR))
			return false;

		if (SGL == null) {
			if (other.SGL != null)
				return false;
		} else if (!SGL.equals(other.SGL))
			return false;

		if (FSP == null) {
			if (other.FSP != null)
				return false;
		} else if (!FSP.equals(other.FSP))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((SNR == null) ? 0 : SNR.hashCode());

		result = prime * result + ((SGL == null) ? 0 : SGL.hashCode());

		result = prime * result + ((FSP == null) ? 0 : FSP.hashCode());
		return result;
	}


	/**
	 * Konvertiert das Objekt in einen String. Dieser kann z.B. für Debug-Ausgaben genutzt werden.
	 *
	 * @return die String-Repräsentation des Objektes
	 */
	@Override
	public String toString() {
		return "MigrationDTOSchulverWeitereSF(SNR=" + this.SNR + ", SGL=" + this.SGL + ", FSP=" + this.FSP + ", gueltigVon=" + this.gueltigVon + ", gueltigBis=" + this.gueltigBis + ")";
	}

}