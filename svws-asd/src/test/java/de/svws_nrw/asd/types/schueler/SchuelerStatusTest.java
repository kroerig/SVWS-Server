package de.svws_nrw.asd.types.schueler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.svws_nrw.asd.utils.ASDCoreTypeUtils;

/**
 * Diese Klasse stellt JUnit-Tests für den Core-Type {@link SchuelerStatus}
 * zur Verfügung.
 */
@DisplayName("Teste den Core-Type SchuelerStatus")
class SchuelerStatusTest {

	/**
	 * Initialisiert die Core-Types, damit die Tests ausgeführt werden können.
	 * Beim Laden der Core-Type-Daten werden die JSON-Dateien auf Plausibilität
	 * geprüft.
	 */
	@BeforeAll
	static void setup() {
		ASDCoreTypeUtils.initAll();
	}


	// TODO Schreiben von Tests

	/**
	 * Führt einfache Tests zu dem Core-Type SchuelerStatus aus.
	 */
	@Test
	@DisplayName("Prüfe die Anzahl der Anlagen am Berufskolleg.")
	void testSchuelerStatus() {
		assertEquals(8, SchuelerStatus.data().getWerte().size());
	}

	/**
	 * Führt einfache Tests zu dem Core-Type SchuelerStatus aus.
	 */
	@Test
	@DisplayName("Prüfe ob der richtige Name enthalten ist")
	void testSchuelerStatusName() {
		assertEquals("Beurlaubt", SchuelerStatus.data().getHistorieByWert(SchuelerStatus.BEURLAUBT).getFirst().text);
	}

}
