package de.nrw.schule.svws.core.data.schule;

import java.util.Vector;

import javax.xml.bind.annotation.XmlRootElement;

import de.nrw.schule.svws.core.transpiler.TranspilerDTO;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * Diese Klasse wird bei der Kommunikation über die Open-API-Schnittstelle verwendet.
 * Sie beschreibt die Stammdaten der Schule.  
 */
@XmlRootElement
@Schema(description="Die Stammdaten der Schule.")
@TranspilerDTO
public class SchuleStammdaten {

	/** Die eindeutige Schulnummer der Schule */
	@Schema(required = true, description = "die Schulnummer der Schule", example="123456")
	public long schulNr;
	
	/** Die Schulform der Schule */
	@Schema(required = false, description = "Das Kürzel der Schulform", example="GY")
	public @NotNull String schulform = "";
	
	/** Der erste Teil (von dreien) der Bezeichnung der Schule */
	@Schema(required = false, description = "die Bezeichnung 1 der Schule", example="Städt. Gymnasium")
	public @NotNull String bezeichnung1 = "";
	
	/** Der zweite Teil (von dreien) der Bezeichnung der Schule */
	@Schema(required = false, description = "die Bezeichnung 2 der Schule", example="der Stadt Wuppertal")
	public String bezeichnung2;
	
	/** Der dritte Teil (von dreien) der Bezeichnung der Schule */
	@Schema(required = false, description = "die Bezeichnung 3 der Schule", example="Albert Einstein")
	public String bezeichnung3;
	
	/** Der Straßenname der Straße in der die Schule liegt. */
	@Schema(required = false, description = "der Straßenname der Straße in der die Schule liegt.", example="Musterweg")
	public String strassenname;
	
	/** Die Hausnummer zur Straße in der die Schule liegt. */
	@Schema(required = false, description = "Ggf. die Hausnummer zur Straße in der die Schule liegt.", example="4711")
	public String hausnummer;
	
	/** Ggf. der Hausnummerzusatz zur Straße in der die Schule liegt. */
	@Schema(required = false, description = "Ggf. der Hausnummerzusatz zur Straße in der die Schule liegt.", example="a-d")
	public String hausnummerZusatz;
	
	
	/** Die Postleitzahl des Gebietes in dem die Schule liegt. */
	@Schema(required = false, description = "die Postleitzahl der Schule", example="42287")
	public String plz;
	
	/** Der Ort in dem die Schule liegt. */
	@Schema(required = false, description = "der Ort der Schule", example="Düsseldorf")
	public String ort;
	
	/** Die Telefonnummer der Schule. */
	@Schema(required = false, description = "die Telefonnummer der Schule", example="0211-58670")
	public String telefon;
	
	/** Die Faxnummer der Schule. */
	@Schema(required = false, description = "die Faxnummer der Schule", example="0211-58671")
	public String fax;
	
	/** Die Mailadresse der Schule. */
	@Schema(required = false, description = "die Mailadresse der Schule", example="info@schule.de")
	public String email;
	
	/** Die Adresse der Homepage der Schule (Domain-Name) */
	@Schema(required = false, description = "die Adresse der Homepage der Schule", example="www.schule.de")
	public String webAdresse;
	
	/** Die ID des Schuljahresabschnittes, in welchem sich die Schule befindet. */
	@Schema(required = true, description = "die ID des Schuljahresabschnittes, in welchem sich die Schule befindet", example="14")
	public long idSchuljahresabschnitt;
	
	/** Die Anzahl der Abschnitte pro Jahrgangsstufe. */
	@Schema(required = true, description = "Anzahl der Abschnitte pro Jahrgangsstufe", example="1")
	public long anzJGS_Jahr;

	/** Die Informationen zu den Abschnitten pro Jahr. (meist Haljahre (2) oder Quartale (4) */
	@Schema(required = true, description = "Abschnittsinformatioenn zur Schule")
	public @NotNull SchuleAbschnitte schuleAbschnitte = new SchuleAbschnitte();
	
	/** Die Dauer einer Unterrichsteinheit in Minuten. */
	@Schema(required = true, description = "Dauer einer Unterrichtseinheit", example="45")
	public long dauerUnterrichtseinheit;
	
	/** Die Liste der Schuljahresabschnitte, welche an der Schule definiert sind. */
	@ArraySchema(schema = @Schema(implementation = Schuljahresabschnitt.class))
	public final @NotNull Vector<@NotNull Schuljahresabschnitt> abschnitte = new Vector<>();
	
}
