package de.svws_nrw.core.data.fach;

import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlRootElement;
import de.svws_nrw.core.transpiler.TranspilerDTO;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Diese Klasse wird bei der Kommunikation über die Open-API-Schnittstelle verwendet.
 * Sie beschreibt, wie die Daten für die Fächer übergeben werden.
 */
@XmlRootElement
@Schema(description = "ein Eintrag eines Faches in der Fächerliste.")
@TranspilerDTO
public class FaecherListeEintrag {

	/** Die ID des Faches. */
	@Schema(description = "die ID des Faches", example = "4711")
	public long id = -1;

	/** Das Kürzel des Faches. */
	@Schema(description = "das Kürzel des Faches", example = "BI")
	public @NotNull String kuerzel = "";

	/** Das dem Fach zugeordnete Statistik-Kürzel. */
	@Schema(description = "das dem Fach zugeordnete Statistik-Kürzel", example = "BI")
	public @NotNull String kuerzelStatistik = "";

	/** Der Name / die Bezeichnung des Faches. */
	@Schema(description = "der Name / die Bezeichnung des Faches", example = "Biologie")
	public @NotNull String bezeichnung = "";

	/** Die Sortierreihenfolge des Fächerlisten-Eintrags. */
	@Schema(description = "die Sortierreihenfolge des Fächerlisten-Eintrags", example = "1")
	public int sortierung = 32000;

	/** Gibt an, ob es sich um ein Fach der Oberstufe handelt oder nicht. */
	@Schema(description = "gibt an, ob es sich um ein Fach der Oberstufe handelt oder nicht", example = "true")
	public boolean istOberstufenFach = false;

	/** Gibt an, ob es sich um ein Fach handelt, welches relevant für die Pürfungsordnung ist oder nicht (z.B. bei Belegprüfungen). */
	@Schema(description = "gibt an, ob es sich um ein Fach handelt, welches relevant für die Pürfungsordnung ist oder nicht (z.B. bei Belegprüfungen).", example = "true")
	public boolean istPruefungsordnungsRelevant = false;

	/** Gibt an, ob der Eintrag in der Anwendung sichtbar sein soll oder nicht. */
	@Schema(description = "gibt an, ob der Eintrag in der Anwendung sichtbar sein soll oder nicht", example = "true")
	public boolean istSichtbar = false;

}
