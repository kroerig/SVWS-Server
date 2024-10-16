package de.svws_nrw.db.dto.current.schild.faecher;

import de.svws_nrw.db.DBEntityManager;
import de.svws_nrw.db.converter.current.Boolean01Converter;


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
import de.svws_nrw.csv.converter.current.Boolean01ConverterSerializer;
import de.svws_nrw.csv.converter.current.Boolean01ConverterDeserializer;

/**
 * Diese Klasse dient als DTO für die Datenbanktabelle FachKatalog.
 * Sie wurde automatisch per Skript generiert und sollte nicht verändert werden,
 * da sie aufgrund von Änderungen am DB-Schema ggf. neu generiert und überschrieben wird.
 */
@Entity
@Cacheable(DBEntityManager.use_db_caching)
@Table(name = "FachKatalog")
@NamedQuery(name = "DTOFaecherKatalog.all", query = "SELECT e FROM DTOFaecherKatalog e")
@NamedQuery(name = "DTOFaecherKatalog.id", query = "SELECT e FROM DTOFaecherKatalog e WHERE e.ID = :value")
@NamedQuery(name = "DTOFaecherKatalog.id.multiple", query = "SELECT e FROM DTOFaecherKatalog e WHERE e.ID IN :value")
@NamedQuery(name = "DTOFaecherKatalog.kuerzelasd", query = "SELECT e FROM DTOFaecherKatalog e WHERE e.KuerzelASD = :value")
@NamedQuery(name = "DTOFaecherKatalog.kuerzelasd.multiple", query = "SELECT e FROM DTOFaecherKatalog e WHERE e.KuerzelASD IN :value")
@NamedQuery(name = "DTOFaecherKatalog.bezeichnung", query = "SELECT e FROM DTOFaecherKatalog e WHERE e.Bezeichnung = :value")
@NamedQuery(name = "DTOFaecherKatalog.bezeichnung.multiple", query = "SELECT e FROM DTOFaecherKatalog e WHERE e.Bezeichnung IN :value")
@NamedQuery(name = "DTOFaecherKatalog.kuerzel", query = "SELECT e FROM DTOFaecherKatalog e WHERE e.Kuerzel = :value")
@NamedQuery(name = "DTOFaecherKatalog.kuerzel.multiple", query = "SELECT e FROM DTOFaecherKatalog e WHERE e.Kuerzel IN :value")
@NamedQuery(name = "DTOFaecherKatalog.aufgabenfeld", query = "SELECT e FROM DTOFaecherKatalog e WHERE e.Aufgabenfeld = :value")
@NamedQuery(name = "DTOFaecherKatalog.aufgabenfeld.multiple", query = "SELECT e FROM DTOFaecherKatalog e WHERE e.Aufgabenfeld IN :value")
@NamedQuery(name = "DTOFaecherKatalog.fachgruppe", query = "SELECT e FROM DTOFaecherKatalog e WHERE e.Fachgruppe = :value")
@NamedQuery(name = "DTOFaecherKatalog.fachgruppe.multiple", query = "SELECT e FROM DTOFaecherKatalog e WHERE e.Fachgruppe IN :value")
@NamedQuery(name = "DTOFaecherKatalog.jahrgangab", query = "SELECT e FROM DTOFaecherKatalog e WHERE e.JahrgangAb = :value")
@NamedQuery(name = "DTOFaecherKatalog.jahrgangab.multiple", query = "SELECT e FROM DTOFaecherKatalog e WHERE e.JahrgangAb IN :value")
@NamedQuery(name = "DTOFaecherKatalog.istfremdsprache", query = "SELECT e FROM DTOFaecherKatalog e WHERE e.IstFremdsprache = :value")
@NamedQuery(name = "DTOFaecherKatalog.istfremdsprache.multiple", query = "SELECT e FROM DTOFaecherKatalog e WHERE e.IstFremdsprache IN :value")
@NamedQuery(name = "DTOFaecherKatalog.isthkfs", query = "SELECT e FROM DTOFaecherKatalog e WHERE e.IstHKFS = :value")
@NamedQuery(name = "DTOFaecherKatalog.isthkfs.multiple", query = "SELECT e FROM DTOFaecherKatalog e WHERE e.IstHKFS IN :value")
@NamedQuery(name = "DTOFaecherKatalog.istausregufach", query = "SELECT e FROM DTOFaecherKatalog e WHERE e.IstAusRegUFach = :value")
@NamedQuery(name = "DTOFaecherKatalog.istausregufach.multiple", query = "SELECT e FROM DTOFaecherKatalog e WHERE e.IstAusRegUFach IN :value")
@NamedQuery(name = "DTOFaecherKatalog.istersatzpflichtfs", query = "SELECT e FROM DTOFaecherKatalog e WHERE e.IstErsatzPflichtFS = :value")
@NamedQuery(name = "DTOFaecherKatalog.istersatzpflichtfs.multiple", query = "SELECT e FROM DTOFaecherKatalog e WHERE e.IstErsatzPflichtFS IN :value")
@NamedQuery(name = "DTOFaecherKatalog.istkonfkoop", query = "SELECT e FROM DTOFaecherKatalog e WHERE e.IstKonfKoop = :value")
@NamedQuery(name = "DTOFaecherKatalog.istkonfkoop.multiple", query = "SELECT e FROM DTOFaecherKatalog e WHERE e.IstKonfKoop IN :value")
@NamedQuery(name = "DTOFaecherKatalog.nursii", query = "SELECT e FROM DTOFaecherKatalog e WHERE e.NurSII = :value")
@NamedQuery(name = "DTOFaecherKatalog.nursii.multiple", query = "SELECT e FROM DTOFaecherKatalog e WHERE e.NurSII IN :value")
@NamedQuery(name = "DTOFaecherKatalog.exportasd", query = "SELECT e FROM DTOFaecherKatalog e WHERE e.ExportASD = :value")
@NamedQuery(name = "DTOFaecherKatalog.exportasd.multiple", query = "SELECT e FROM DTOFaecherKatalog e WHERE e.ExportASD IN :value")
@NamedQuery(name = "DTOFaecherKatalog.gueltigvon", query = "SELECT e FROM DTOFaecherKatalog e WHERE e.gueltigVon = :value")
@NamedQuery(name = "DTOFaecherKatalog.gueltigvon.multiple", query = "SELECT e FROM DTOFaecherKatalog e WHERE e.gueltigVon IN :value")
@NamedQuery(name = "DTOFaecherKatalog.gueltigbis", query = "SELECT e FROM DTOFaecherKatalog e WHERE e.gueltigBis = :value")
@NamedQuery(name = "DTOFaecherKatalog.gueltigbis.multiple", query = "SELECT e FROM DTOFaecherKatalog e WHERE e.gueltigBis IN :value")
@NamedQuery(name = "DTOFaecherKatalog.primaryKeyQuery", query = "SELECT e FROM DTOFaecherKatalog e WHERE e.ID = ?1")
@NamedQuery(name = "DTOFaecherKatalog.primaryKeyQuery.multiple", query = "SELECT e FROM DTOFaecherKatalog e WHERE e.ID IN :value")
@NamedQuery(name = "DTOFaecherKatalog.all.migration", query = "SELECT e FROM DTOFaecherKatalog e WHERE e.ID IS NOT NULL")
@JsonPropertyOrder({"ID", "KuerzelASD", "Bezeichnung", "Kuerzel", "Aufgabenfeld", "Fachgruppe", "JahrgangAb", "IstFremdsprache", "IstHKFS", "IstAusRegUFach", "IstErsatzPflichtFS", "IstKonfKoop", "NurSII", "ExportASD", "gueltigVon", "gueltigBis"})
public final class DTOFaecherKatalog {

	/** ID des Faches */
	@Id
	@Column(name = "ID")
	@JsonProperty
	public long ID;

	/** Das eindeutige Kürzel des Faches entsprechend der Vorgaben der amtlichen Schulstatistik */
	@Column(name = "KuerzelASD")
	@JsonProperty
	public String KuerzelASD;

	/** Die texttuelle Beschreibung des Faches */
	@Column(name = "Bezeichnung")
	@JsonProperty
	public String Bezeichnung;

	/** Das atomare Kürzel des Faches (z.B. bei Fremdsprachen - für das Sprachenkürzel) - Teil des Kürzels für die amtliche Schulstatistik */
	@Column(name = "Kuerzel")
	@JsonProperty
	public String Kuerzel;

	/** Das Aufgabenfeld, welchem das Fach ggf. zugeordnet ist (1, 2 oder 3) */
	@Column(name = "Aufgabenfeld")
	@JsonProperty
	public Integer Aufgabenfeld;

	/** Das Kürzel der zugeordneten Fachgruppe */
	@Column(name = "Fachgruppe")
	@JsonProperty
	public String Fachgruppe;

	/** Der ASD-Jahrgang, ab dem das Fach zulässig ist (z.B. bei Fremdsprachen) */
	@Column(name = "JahrgangAb")
	@JsonProperty
	public String JahrgangAb;

	/** Gibt an, ob es sich um ein Fach der Herkuntftsprache handelt (Unterrichts in der Herkunftssprache oder Herkunftssprache anstelle einer Pflichtfremdsprache) */
	@Column(name = "IstFremdsprache")
	@JsonProperty
	@Convert(converter = Boolean01Converter.class)
	@JsonSerialize(using = Boolean01ConverterSerializer.class)
	@JsonDeserialize(using = Boolean01ConverterDeserializer.class)
	public Boolean IstFremdsprache;

	/** Gibt an, ob es sich um ein Fach der Herkuntftsprache handelt (Unterrichts in der Herkunftssprache oder Herkunftssprache anstelle einer Pflichtfremdsprache) */
	@Column(name = "IstHKFS")
	@JsonProperty
	@Convert(converter = Boolean01Converter.class)
	@JsonSerialize(using = Boolean01ConverterSerializer.class)
	@JsonDeserialize(using = Boolean01ConverterDeserializer.class)
	public Boolean IstHKFS;

	/** Gibt an, ob das Fach außerhalb des regulären Fachunterichts unterrichtet wird */
	@Column(name = "IstAusRegUFach")
	@JsonProperty
	@Convert(converter = Boolean01Converter.class)
	@JsonSerialize(using = Boolean01ConverterSerializer.class)
	@JsonDeserialize(using = Boolean01ConverterDeserializer.class)
	public Boolean IstAusRegUFach;

	/** Gibt an, ob es sich bei dem Fach um einen Ersatz für eine Pflichtfremdsprache handelt (siehe auch istHKFS) */
	@Column(name = "IstErsatzPflichtFS")
	@JsonProperty
	@Convert(converter = Boolean01Converter.class)
	@JsonSerialize(using = Boolean01ConverterSerializer.class)
	@JsonDeserialize(using = Boolean01ConverterDeserializer.class)
	public Boolean IstErsatzPflichtFS;

	/** Gibt an, ob das Religionsfach konfessionell kooperativ unterrichtet wird oder nicht - Teil des Kürzels für die amtliche Schulstatistik */
	@Column(name = "IstKonfKoop")
	@JsonProperty
	@Convert(converter = Boolean01Converter.class)
	@JsonSerialize(using = Boolean01ConverterSerializer.class)
	@JsonDeserialize(using = Boolean01ConverterDeserializer.class)
	public Boolean IstKonfKoop;

	/** Gibt an, ob das Fach nur in der Sekundarstufe II unterrichtet wird */
	@Column(name = "NurSII")
	@JsonProperty
	@Convert(converter = Boolean01Converter.class)
	@JsonSerialize(using = Boolean01ConverterSerializer.class)
	@JsonDeserialize(using = Boolean01ConverterDeserializer.class)
	public Boolean NurSII;

	/** Gibt an, ob das Fach bei Export der amtlichen Schulstatistik berücksichtigt werden soll oder nicht */
	@Column(name = "ExportASD")
	@JsonProperty
	@Convert(converter = Boolean01Converter.class)
	@JsonSerialize(using = Boolean01ConverterSerializer.class)
	@JsonDeserialize(using = Boolean01ConverterDeserializer.class)
	public Boolean ExportASD;

	/** Gibt die Gültigkeit ab welchem Schuljahr an */
	@Column(name = "gueltigVon")
	@JsonProperty
	public Integer gueltigVon;

	/** Gibt die Gültigkeit bis zu welchem Schuljahr an */
	@Column(name = "gueltigBis")
	@JsonProperty
	public Integer gueltigBis;

	/**
	 * Erstellt ein neues Objekt der Klasse DTOFaecherKatalog ohne eine Initialisierung der Attribute.
	 */
	@SuppressWarnings("unused")
	private DTOFaecherKatalog() {
	}

	/**
	 * Erstellt ein neues Objekt der Klasse DTOFaecherKatalog ohne eine Initialisierung der Attribute.
	 * @param ID   der Wert für das Attribut ID
	 * @param KuerzelASD   der Wert für das Attribut KuerzelASD
	 * @param Bezeichnung   der Wert für das Attribut Bezeichnung
	 * @param Kuerzel   der Wert für das Attribut Kuerzel
	 * @param IstFremdsprache   der Wert für das Attribut IstFremdsprache
	 * @param IstHKFS   der Wert für das Attribut IstHKFS
	 * @param IstAusRegUFach   der Wert für das Attribut IstAusRegUFach
	 * @param IstErsatzPflichtFS   der Wert für das Attribut IstErsatzPflichtFS
	 * @param IstKonfKoop   der Wert für das Attribut IstKonfKoop
	 * @param NurSII   der Wert für das Attribut NurSII
	 * @param ExportASD   der Wert für das Attribut ExportASD
	 */
	public DTOFaecherKatalog(final long ID, final String KuerzelASD, final String Bezeichnung, final String Kuerzel, final Boolean IstFremdsprache, final Boolean IstHKFS, final Boolean IstAusRegUFach, final Boolean IstErsatzPflichtFS, final Boolean IstKonfKoop, final Boolean NurSII, final Boolean ExportASD) {
		this.ID = ID;
		if (KuerzelASD == null) {
			throw new NullPointerException("KuerzelASD must not be null");
		}
		this.KuerzelASD = KuerzelASD;
		if (Bezeichnung == null) {
			throw new NullPointerException("Bezeichnung must not be null");
		}
		this.Bezeichnung = Bezeichnung;
		if (Kuerzel == null) {
			throw new NullPointerException("Kuerzel must not be null");
		}
		this.Kuerzel = Kuerzel;
		this.IstFremdsprache = IstFremdsprache;
		this.IstHKFS = IstHKFS;
		this.IstAusRegUFach = IstAusRegUFach;
		this.IstErsatzPflichtFS = IstErsatzPflichtFS;
		this.IstKonfKoop = IstKonfKoop;
		this.NurSII = NurSII;
		this.ExportASD = ExportASD;
	}


	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DTOFaecherKatalog other = (DTOFaecherKatalog) obj;
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
		return "DTOFaecherKatalog(ID=" + this.ID + ", KuerzelASD=" + this.KuerzelASD + ", Bezeichnung=" + this.Bezeichnung + ", Kuerzel=" + this.Kuerzel + ", Aufgabenfeld=" + this.Aufgabenfeld + ", Fachgruppe=" + this.Fachgruppe + ", JahrgangAb=" + this.JahrgangAb + ", IstFremdsprache=" + this.IstFremdsprache + ", IstHKFS=" + this.IstHKFS + ", IstAusRegUFach=" + this.IstAusRegUFach + ", IstErsatzPflichtFS=" + this.IstErsatzPflichtFS + ", IstKonfKoop=" + this.IstKonfKoop + ", NurSII=" + this.NurSII + ", ExportASD=" + this.ExportASD + ", gueltigVon=" + this.gueltigVon + ", gueltigBis=" + this.gueltigBis + ")";
	}

}
