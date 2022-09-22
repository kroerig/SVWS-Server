package de.nrw.schule.svws.core.utils.gost;

import java.util.Comparator;

import de.nrw.schule.svws.core.data.gost.GostBlockungsergebnis;
import de.nrw.schule.svws.core.data.gost.GostBlockungsergebnisListeneintrag;
import jakarta.validation.constraints.NotNull;

/** Dieser Klasse dient der GUI dazu, Blockungsergebnisse zu sortieren. <br>
 * Die GUI erhält von der Datenbank eine Liste von {@link GostBlockungsergebnisListeneintrag}, sobald man auf ein
 * Element der Liste klickt, wird ein {@link GostBlockungsergebnis} Objekt geladen. */
public class GostBlockungsergebnisComparator implements Comparator<@NotNull GostBlockungsergebnisListeneintrag> {

	/** Erzeugt einen Comparator für zwei Objekte des Typs {@link GostBlockungsergebnisListeneintrag}. Zwei Elemente
	 * werden nach folgender Priorität sortiert: <br>
	 * (1) Array an Regelverletzungen (weniger besser)<br>
	 * (2) Summe nicht zugeordneter Fachwahlen (weniger besser) <br>
	 * (3) Array an Kursdifferenzen (kleinste größte Kursdifferenz besser) <br>
	 * (4) Summe gleicher Facharten in der selben Schiene (weniger besser) <br>
	 */
	public GostBlockungsergebnisComparator() {
	}

	@Override
	public int compare(@NotNull GostBlockungsergebnisListeneintrag o1, @NotNull GostBlockungsergebnisListeneintrag o2) {
		// Bewertungskriterium 1: Je weniger nicht erfüllter Regeln, desto besser.
		if (o1.bewertungNichtErfuellteRegeln.length < o2.bewertungNichtErfuellteRegeln.length)
			return -1;
		if (o1.bewertungNichtErfuellteRegeln.length > o2.bewertungNichtErfuellteRegeln.length)
			return +1;

		// Bewertungskriterium 2: Je weniger nicht zugeordnete Fachwahlen, desto besser.
		if (o1.bewertungNichtZugeordneteFachwahlen < o2.bewertungNichtZugeordneteFachwahlen)
			return -1;
		if (o1.bewertungNichtZugeordneteFachwahlen > o2.bewertungNichtZugeordneteFachwahlen)
			return +1;

		// Bewertungskriterium 3: Je kleiner die Größte Kursdifferenz, desto besser.
		int length1 = o1.bewertungHistogrammDerKursdifferenzen.length;
		int length2 = o2.bewertungHistogrammDerKursdifferenzen.length;
		if (length1 < length2)
			return -1;
		if (length1 > length2)
			return +1;
		for (int i = length1 - 1; i >= 0; i--) {
			if (o1.bewertungHistogrammDerKursdifferenzen[i] < o2.bewertungHistogrammDerKursdifferenzen[i])
				return -1;
			if (o1.bewertungHistogrammDerKursdifferenzen[i] > o2.bewertungHistogrammDerKursdifferenzen[i])
				return -1;
		}

		// Bewertungskriterium 4: Je weniger Facharten in der selben Schiene sind, desto besser.
		if (o1.bewertungGleicheFachartProSchiene < o2.bewertungGleicheFachartProSchiene)
			return -1;
		if (o1.bewertungGleicheFachartProSchiene > o2.bewertungGleicheFachartProSchiene)
			return +1;

		// Bei absolut identischen Ergebnissen gewinnt die kleinere ID des Zwischenergebnisses.
		if (o1.id < o2.id)
			return -1;
		if (o1.id > o2.id)
			return +1;

		return 0; // Sollte niemals, da die IDs niemals gleich sind.
	}
}
