package de.nrw.schule.svws.core.data.abschluss;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.nrw.schule.svws.core.transpiler.TranspilerDTO;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Diese Klasse enthält die Beschreibung eines Faches mit Zusatzinformationen, 
 * welches zur Abschlussberechnung an der Gesamtschule verwendet wird.
 * Sie ist Bestandteil der JSON-API für die Abschlussberechnung.
 */
@XmlRootElement
@Schema(description="die Fachinformationen eines Faches für die Abschlussberechnung.")
@TranspilerDTO
public class GEAbschlussFach {

	/** Das Kürzel des Faches */
	@Schema(required = true, description = "Das Kürzel des Faches", example="D")	
    public String kuerzel = null;

	/** Die Bezeichnung des Faches */
	@Schema(required = false, defaultValue = "", description = "Die Bezeichnung des Faches", example="Deutsch")	
	public String bezeichnung = null;
	
	/** Die Note, die in dem Fach erteilt wurde */
	@Schema(required = true, description = "Die Note in dem Fach", example="2")	
	public int note = -1;
	
	/** Gibt an, ob das Fach eine Fremdsprache ist oder nicht */
	@Schema(required = true, defaultValue = "false", description = "Gibt an, ob das Fach eine Fremdsprache ist oder nicht", example="false")
	public Boolean istFremdsprache = false;

	/** Gibt die Art der Leistungsdifferenzierung bei dem Fach an: E-Kurs, G-Kurs oder sonstiger Kurs */
	@Schema(required = true, description = "Gibt die Art der Leistungsdifferenzierung bei dem Fach an", example="G")	
	public String kursart = "";

	/** Gibt an, ob das Fach als Ausgleich genutzt wurde oder nicht. */
    @JsonIgnore
    public Boolean ausgleich = false;

    /** Gibt an, ob in diesem Fach ein Defizit ausgeglichen wurde. */
    @JsonIgnore
    public Boolean ausgeglichen = false;

}
