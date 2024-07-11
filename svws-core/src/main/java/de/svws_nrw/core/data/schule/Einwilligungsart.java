package de.svws_nrw.core.data.schule;

import de.svws_nrw.transpiler.TranspilerDTO;
import de.svws_nrw.core.types.schule.PersonTyp;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Die Klasse beschreibt den schulspezifischen Katalog der Einwilligungsarten.
 */
@XmlRootElement
@Schema(description = "ein Eintrag in dem Katalog der schulspezifischen Einwilligungsarten.")
@TranspilerDTO
public class Einwilligungsart {

	/** Die ID des Katalog-Eintrags. */
	@Schema(description = "die ID des Katalog-Eintrags", example = "4711")
	public long id = -1;

	/** Die Bezeichnung der Einwilligungsart. */
	@Schema(description = "die Bezeichnung der Einwilligungsart", example = "Verwendung Foto")
	public @NotNull String bezeichnung = "";

	/** Gibt die Position in der Sortierreihenfolge für die Katalog-Einträge an. */
	@Schema(description = "gibt die Position in der Sortierreihenfolge für die Katalog-Einträge an", example = "1")
	public int sortierung = 1;

	/** Gibt an, für welche Personengruppe die Einwilligungsart relevant ist. */
	@Schema(description = "gibt an, für welche Personengruppe die Einwilligungsart relevant ist", example = "2")
	public int personTyp = PersonTyp.SCHUELER.id;

	/** Der Schlüssel der Einwilligungsart. */
	@Schema(description = "der Schlüssel der Einwilligungsart", example = "FOTO")
	public @NotNull String schluessel = "";

}
