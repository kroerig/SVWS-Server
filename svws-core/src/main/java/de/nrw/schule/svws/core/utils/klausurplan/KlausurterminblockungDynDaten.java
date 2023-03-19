package de.nrw.schule.svws.core.utils.klausurplan;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import de.nrw.schule.svws.core.adt.collection.LinkedCollection;
import de.nrw.schule.svws.core.data.gost.klausuren.GostKursklausur;
import de.nrw.schule.svws.core.exceptions.DeveloperNotificationException;
import de.nrw.schule.svws.core.exceptions.UserNotificationException;
import jakarta.validation.constraints.NotNull;

/** 
 * Eine dynamische Datenstruktur zum Speichern der aktuellen Klausur-Termin-Lage.
 * 
 * @author Benjamin A. Bartsch 
 */
public class KlausurterminblockungDynDaten {

	/** Ein Maximal-Wert für die maximale Anzahl an Terminen. */
	private static final int MAX_TERMINE = 1000;
	
	/** Ein {@link Random}-Objekt zur Steuerung des Zufalls über einen Anfangs-Seed. */
	private final @NotNull Random _random;
	
	/** Die Anzahl der derzeitigen verwendeten Termine. */
	private int _terminAnzahl = 0;
	private int _terminAnzahl1 = MAX_TERMINE; // Speicherzustand 1
	private int _terminAnzahl2 = MAX_TERMINE; // Speicherzustand 2

	/** 
	 * Jeder KlausurNr wird eine TerminNr zugeordnet. Der Wert -1 definiert eine temporäre Nicht-Zuordnung. 
	 * Am Ende des Algorithmus hat jede Klausur eine zugeordnete TerminNr >= 0. 
	 */
	private final @NotNull int @NotNull [] _klausurZuTermin;
	private final @NotNull int @NotNull [] _klausurZuTermin1; // Speicherzustand 1
	private final @NotNull int @NotNull [] _klausurZuTermin2; // Speicherzustand 2

	/** Mapping, um eine Sammlung von Long-Werten in laufende Integer-Werte umzuwandeln. */
	private final @NotNull HashMap<@NotNull Long, @NotNull Integer> _mapKlausurZuNummer = new HashMap<>();
	
	/** Mapping, um eine Sammlung von Long-Werten in laufende Integer-Werte umzuwandeln. */
	private final @NotNull HashMap<@NotNull Integer, @NotNull GostKursklausur> _mapNummerZuKlausur = new HashMap<>();
	
	/** Die Anzahl der Klausuren. */
	private final int _klausurenAnzahl;

	/** Bestimmt, ob ein Klausurnummer-Paar am selben Termin verboten ist. */
	private final @NotNull boolean @NotNull [] @NotNull [] _verboten;
	
	/** Alle Klausurgruppen. */
	private final @NotNull Vector<@NotNull Vector<@NotNull Integer> > _klausurGruppen = new Vector<>();

	/** Alle Klausurgruppen, sortiert nach ihrem Knotengrad (Anzahl an Nachbarn). */
	private final @NotNull Vector<@NotNull Vector<@NotNull Integer> > _klausurGruppenGrad = new Vector<>();
	
	/** 
	 * Der Konstruktor konvertiert die Eingabedaten der GUI in eine dynamische Datenstruktur, 
	 * welche als Basis für alle Algorithmen zur schnellen Manipulation dient.
	 * 
	 * @param pRandom Ein {@link Random}-Objekt zur Steuerung des Zufalls über einen Anfangs-Seed.
	 * @param pInput  Die Eingabedaten (Schnittstelle zur GUI). 
	 * @param pConfig Informationen zur Konfiguration des Algorithmus.
	 */
	public KlausurterminblockungDynDaten(@NotNull Random pRandom, @NotNull List<@NotNull GostKursklausur> pInput, @NotNull KlausurterminblockungAlgorithmusConfig pConfig) {
		_random = pRandom;
		
		initialisiereMapKlausuren(pInput);
		_klausurenAnzahl = _mapKlausurZuNummer.size();
		_klausurZuTermin = new int[_klausurenAnzahl];
		_klausurZuTermin1 = new int[_klausurenAnzahl];
		_klausurZuTermin2 = new int[_klausurenAnzahl];
		
		_verboten = new boolean[_klausurenAnzahl][_klausurenAnzahl];
		initialisiereMatrixVerboten(pInput);
		
		initialisiereKlausurgruppen(pInput, pConfig);
		checkKlausurgruppenOrException();

		initialisiereKlausurgruppenGrad();
		
		aktionClear();
	}
	
	private void checkKlausurgruppenOrException() {
		// TODO BAR genauere Meldung der Exception
		for (@NotNull Vector<@NotNull Integer> gruppe : _klausurGruppen) {
			for (int nr1 : gruppe)
				for (int nr2 : gruppe)
					if (_verboten[nr1][nr2])
						throw new UserNotificationException("Klausurgruppe hat einen Schülerkonflikt!");
		}
	}

	private void initialisiereKlausurgruppen(@NotNull List<@NotNull GostKursklausur> pInput, @NotNull KlausurterminblockungAlgorithmusConfig pConfig) {
		// pConfig.set_algorithmus_faecherweise(); // TODO BAR remove fake algorithm
		
		switch (pConfig.get_algorithmus()) {
			// Jede Gruppe besteht aus einer einzelnen Klausur
			case KlausurterminblockungAlgorithmusConfig.ALGORITHMUS_NORMAL: {
				for (@NotNull GostKursklausur gostKursklausur : pInput) {
					Integer klausurNr = _mapKlausurZuNummer.get(gostKursklausur.id);
					if (klausurNr == null) throw new DeveloperNotificationException("Kein Mapping zu gostKursklausur.id = " + gostKursklausur.id);
					
					@NotNull Vector<@NotNull Integer> gruppe = new Vector<>();
					gruppe.add(klausurNr);
					_klausurGruppen.add(gruppe);
				}
				break;
			}
			// Jede Gruppe besteht aus allen Klausuren des selben Faches.
			case KlausurterminblockungAlgorithmusConfig.ALGORITHMUS_FAECHERWEISE: {
				@NotNull HashMap<@NotNull Long, @NotNull Vector<@NotNull Integer>> mapFachZuKlausurGruppe = new HashMap<>();
				for (@NotNull GostKursklausur gostKursklausur : pInput) {
					Integer klausurNr = _mapKlausurZuNummer.get(gostKursklausur.id);
					if (klausurNr == null) throw new DeveloperNotificationException("Kein Mapping zu gostKursklausur.id = " + gostKursklausur.id);
					
					long fachID = gostKursklausur.idFach;
					if (fachID < 0   ) {
						// Ohne FachID --> Erzeuge eigene Gruppe
						@NotNull Vector<@NotNull Integer> gruppe = new Vector<>();
						gruppe.add(klausurNr);
						_klausurGruppen.add(gruppe);
					} else {
						// Mit FachID --> Suche zugehörige Gruppe
						Vector<@NotNull Integer> gruppe = mapFachZuKlausurGruppe.get(fachID);
						if (gruppe == null) {
							gruppe = new Vector<>();
							mapFachZuKlausurGruppe.put(fachID, gruppe);
							_klausurGruppen.add(gruppe);
						}
						gruppe.add(klausurNr);
					}
					
				}
				break;
			}
			// Jede Gruppe besteht aus allen Klausuren der selben Schiene.
			case KlausurterminblockungAlgorithmusConfig.ALGORITHMUS_SCHIENENWEISE: {
				@NotNull HashMap<@NotNull Long, @NotNull Vector<@NotNull Integer>> mapSchieneZuKlausurGruppe = new HashMap<>();
				for (@NotNull GostKursklausur gostKursklausur : pInput) {
					Integer klausurNr = _mapKlausurZuNummer.get(gostKursklausur.id);
					if (klausurNr == null) throw new DeveloperNotificationException("Kein Mapping zu gostKursklausur.id = " + gostKursklausur.id);
					
					long schienenID = gostKursklausur.kursSchiene.length < 1 ? -1 : gostKursklausur.kursSchiene[0]; // TODO BAR besser?
					if (schienenID < 0   ) {
						// Ohne schienenID --> Erzeuge eigene Gruppe
						@NotNull Vector<@NotNull Integer> gruppe = new Vector<>();
						gruppe.add(klausurNr);
						_klausurGruppen.add(gruppe);
					} else {
						// Mit schienenID --> Suche zugehörige Gruppe
						Vector<@NotNull Integer> gruppe = mapSchieneZuKlausurGruppe.get(schienenID);
						if (gruppe == null) {
							gruppe = new Vector<>();
							mapSchieneZuKlausurGruppe.put(schienenID, gruppe);
							_klausurGruppen.add(gruppe);
						}
						gruppe.add(klausurNr);
					}
					
				}
				break;
			}
			default: {
				throw new DeveloperNotificationException("Der Algorithmus ist unbekannt!");
			}
		}
		
	}
	
	private void initialisiereKlausurgruppenGrad() {
		// Kopieren
		_klausurGruppenGrad.addAll(_klausurGruppen);

		// InsertionSort von '_klausurenSortiertGrad'.
		for (int i = 1; i < _klausurGruppenGrad.size(); i++)
			for (int j = i; j >= 1; j--) {
				@NotNull Vector<@NotNull Integer> gruppeR = _klausurGruppenGrad.get(j);
				@NotNull Vector<@NotNull Integer> gruppeL = _klausurGruppenGrad.get(j-1);
				int gradR = gibKnotengrad(gruppeR); 
				int gradL = gibKnotengrad(gruppeL);
				if (gradL >= gradR) break; // bereits richtig einsortiert.
				_klausurGruppenGrad.set(j, gruppeL);
				_klausurGruppenGrad.set(j-1, gruppeR);
			}
	}

	private int gibKnotengrad(@NotNull Vector<@NotNull Integer> pGruppe) {
		int grad = 0;
		for (@NotNull Vector<@NotNull Integer> gruppe : _klausurGruppen) 
			if (gibIstVerboten(pGruppe, gruppe))
				grad++;
		return grad;
	}

	private boolean gibIstVerboten(@NotNull Vector<@NotNull Integer> pGruppe1, @NotNull Vector<@NotNull Integer> pGruppe2) {
		for (int klausurNr1 : pGruppe1) 
			for (int klausurNr2 : pGruppe2) 
				if (_verboten[klausurNr1][klausurNr2])
					return true;
		return false;
	}

	private void initialisiereMapKlausuren(@NotNull List<@NotNull GostKursklausur> pInput) {
		for (@NotNull GostKursklausur gostKursklausur : pInput) {
			if (gostKursklausur.id < 0) throw new DeveloperNotificationException("Klausur-ID=" + gostKursklausur.id + " ist negativ!");
			if (_mapKlausurZuNummer.containsKey(gostKursklausur.id)) throw new DeveloperNotificationException("Klausur-ID=" + gostKursklausur.id + " ist doppelt!");				
			// Mapping: datenbankKlausurID --> laufende Nummer
			int klausurNummer = _mapKlausurZuNummer.size();
			_mapKlausurZuNummer.put(gostKursklausur.id, klausurNummer);
			_mapNummerZuKlausur.put(klausurNummer, gostKursklausur);
		}
	}

	private void initialisiereMatrixVerboten(@NotNull List<@NotNull GostKursklausur> pInput) {
		
		// Erzeuge eine Map: SchülerID --> Liste seiner KlausurNummern
		@NotNull HashMap<@NotNull Long, @NotNull LinkedCollection<@NotNull Integer>> mapSchuelerKlausuren = new HashMap<>();
		for (@NotNull GostKursklausur gostKursklausur : pInput) {
			for (@NotNull Long schuelerID : gostKursklausur.schuelerIds) {
				// Liste des Schülers holen
				LinkedCollection<@NotNull Integer> list = mapSchuelerKlausuren.get(schuelerID);
				if (list == null) {
					list = new LinkedCollection<>();
					mapSchuelerKlausuren.put(schuelerID, list);
				}
				// Liste des Schülers füllen
				Integer klausurNr = _mapKlausurZuNummer.get(gostKursklausur.id);
				if (klausurNr == null) throw new DeveloperNotificationException("Kein Mapping zu gostKursklausur.id = " + gostKursklausur.id);
				list.addLast(klausurNr);
			}
		}
		
		// Verbiete Klausur-Paare
		for (@NotNull Long schuelerID : mapSchuelerKlausuren.keySet()) {
			LinkedCollection<@NotNull Integer> list = mapSchuelerKlausuren.get(schuelerID);
			if (list == null) throw new DeveloperNotificationException("Die Liste darf nicht NULL sein.");
			for (int klausurNr1 : list) 
				for (int klausurNr2 : list) 
					if (klausurNr1 != klausurNr2)
						_verboten[klausurNr1][klausurNr2] = true;
		}

	}	
	
	/**
	 * Liefert die aktuelle Anzahl an Terminen.
	 * 
	 * @return die aktuelle Anzahl an Terminen.
	 */
	int gibTerminAnzahl() {
		return _terminAnzahl;
	}

	/** 
	 * Liefert die Anzahl noch nicht verteilter Klausuren.
	 * 
	 * @return die Anzahl noch nicht verteilter Klausuren. 
	 */
	private boolean gibExistierenNichtverteilteKlausuren() {
		for (int klausurNr = 0; klausurNr < _klausurenAnzahl; klausurNr++)
			if (_klausurZuTermin[klausurNr] < 0)
				return true;
		return false;
	}

	/** 
	 * Liefert die Nummer eines neu erzeugten Termins.
	 * 
	 * @return die Nummer eines neu erzeugten Termins. 
	 */
	private int gibErzeugeNeuenTermin() {
		_terminAnzahl++;
		return _terminAnzahl - 1;
	}

	/** 
	 * Liefert ein Array aller derzeit verwendeten Termine in zufälliger Reihenfolge.
	 * 
	 * @return ein Array aller derzeit verwendeten Termine in zufälliger Reihenfolge. 
	 */
	private @NotNull int[] gibTermineInZufaelligerReihenfolge() {
		int[] temp = new int[_terminAnzahl];

		for (int i = 0; i < _terminAnzahl; i++)
			temp[i] = i;

		for (int i1 = 0; i1 < _terminAnzahl; i1++) {
			int i2 = _random.nextInt(_terminAnzahl);
			int save1 = temp[i1];
			int save2 = temp[i2];
			temp[i1] = save2;
			temp[i2] = save1;
		}

		return temp;
	}
	
	/** 
	 * Liefert die Klausur-Gruppen in zufälliger Reihenfolge.
	 * 
	 * @return die Klausur-Gruppen in zufälliger Reihenfolge. 
	 */
	private @NotNull Vector<@NotNull Vector<@NotNull Integer>> gibKlausurgruppenInZufaelligerReihenfolge() {
		// Kopieren
		@NotNull Vector<@NotNull Vector<@NotNull Integer>> temp = new Vector<>();
		temp.addAll(_klausurGruppen);
		
		// Permutieren
		for (int i1 = 0; i1 < temp.size(); i1++) {
			int i2 = _random.nextInt(temp.size());
			@NotNull Vector<@NotNull Integer> save1 = temp.get(i1);
			@NotNull Vector<@NotNull Integer> save2 = temp.get(i2);
			temp.set(i1, save2);
			temp.set(i2, save1);
		}
		
		return temp;
	}
	
	/** 
	 * Liefert ein leicht permutiertes Array aller Klausurgruppen sortiert nach höheren Knotengrad zuerst.
	 * 
	 * @return ein leicht permutiertes Array aller Klausurgruppen sortiert nach höheren Knotengrad zuerst. 
	 */
	@NotNull @NotNull Vector<@NotNull Vector<@NotNull Integer>> gibKlausurgruppenMitHoeheremGradZuerstEtwasPermutiert() {
		// Kopieren
		@NotNull Vector<@NotNull Vector<@NotNull Integer>> temp = new Vector<>();
		temp.addAll(_klausurGruppenGrad);

		// Permutiere nahe beieinander liegende Indizes.
		int size = temp.size();
		for (int i1 = 0; i1 < size; i1++) {
			int i2 = _random.nextInt(size);
			if ((i1 - i2) * (i1 - i2) >= size) continue;
			// Tausche nur dann, wenn Abstand der Indizes kleiner als Wurzel(size) ist.
			@NotNull Vector<@NotNull Integer> save1 = temp.get(i1);
			@NotNull Vector<@NotNull Integer> save2 = temp.get(i2);
			temp.set(i1, save2);
			temp.set(i2, save1);
		}

		return temp;
	}
	

	private boolean gibVergleicheMitAktuellemZustand(int terminAnzahlX, @NotNull int @NotNull [] klausurZuTerminX) {
		// Kriterium 1: Die Anzahl an Terminen.
		if (_terminAnzahl < terminAnzahlX) return true;
		if (_terminAnzahl > terminAnzahlX) return false;
	
		// Kriterium 2: Besser, wenn mehr Paare bevorzugt
		/*int bevorzugt = gibSchienenBevorzugt(_klausurZuSchiene);
		int bevorzugtX = gibSchienenBevorzugt(klausurZuSchieneX);
		if (bevorzugt > bevorzugtX) return true;
		if (bevorzugt < bevorzugtX) return false;
		 */
		
		// Kriterium 3: Besser ist, wenn ein Termin möglichst wenig Klausuren hat.  
		int[] histogramm = new int[_terminAnzahl]; // TODO als Methode
		int[] histogrammX = new int[_terminAnzahl];
		for (int i = 0; i < _klausurenAnzahl; i++) {
			histogramm[_klausurZuTermin[i]]++;
			histogrammX[klausurZuTerminX[i]]++;
		}
	
		int minHisto = _klausurenAnzahl;
		int minHistoX = _klausurenAnzahl;
		for (int i = 0; i < _terminAnzahl; i++) {  // TODO als Methode
			minHisto = Math.min(minHisto, histogramm[i]);
			minHistoX = Math.min(minHistoX, histogrammX[i]);
		}
	
		return minHisto < minHistoX;
	}

	/** 
	 * Liefert TRUE, falls alle Klausuren der Gruppe noch nicht verteilt wurden.
	 * 
	 * @param pGruppe die Gruppe aller Klausuren.
	 * @return TRUE, falls alle Klausuren der Gruppe noch nicht verteilt wurden. 
	 */
	private boolean gibIstKlausurgruppeUnverteilt(@NotNull Vector<@NotNull Integer> pGruppe) {
		for (int klausurNr : pGruppe) 
			if (_klausurZuTermin[klausurNr] >= 0)
				return false;
		return true;
	}

	/** 
	 * Liefert TRUE, falls alle Klausuren der Gruppe in den übergebenen Termin gesetzt werden konnten.
	 * 
	 * @param  pGruppe die Gruppe aller Klausuren.
	 * @param  pTermin der Termin 
	 * @return TRUE, falls alle Klausuren der Gruppe in den übergebenen Termin gesetzt werden konnten. 
	 */
	private boolean aktionSetzeKlausurgruppeInTermin(@NotNull Vector<@NotNull Integer> pGruppe, int pTermin) {
		if (pTermin <              0) throw new DeveloperNotificationException("aktionSetzeKlausurGruppeInTermin("+pGruppe+", "+pTermin+") --> Termin zu klein!");
		if (pTermin >= _terminAnzahl) throw new DeveloperNotificationException("aktionSetzeKlausurGruppeInTermin("+pGruppe+", "+pTermin+") --> Termin zu groß!");
	
		// Überprüfe, ob das setzen aller Klausuren möglich ist.
		for (int nr2 = 0; nr2 < _klausurenAnzahl; nr2++)
			if (_klausurZuTermin[nr2] == pTermin)
				for (int nr : pGruppe)
					if (_verboten[nr][nr2])
						return false;
		
		// Setze alle Klausuren.
		for (int nr : pGruppe)
			_klausurZuTermin[nr] = pTermin;
		
		return true;
	}

	/** 
	 * Erhöht die Termin-Anzahl um 1, setzt alle Klausuren der übergebenen Gruppe in den neuen Termin.
	 * 
	 * @param pGruppe die Gruppe aller Klausuren
	 */
	private void aktionSetzeKlausurgruppeInNeuenTermin(@NotNull Vector<@NotNull Integer> pGruppe) {
		for (int klausurNr : pGruppe)
			if (_klausurZuTermin[klausurNr] >= 0) throw new DeveloperNotificationException("aktionSetzeKlausurGruppeInNeuenTermin("+klausurNr+") --> Die Klausur ist bereits einem Termin zugeordnet!");
		
		for (int klausurNr : pGruppe)
			_klausurZuTermin[klausurNr] = _terminAnzahl;
		
		_terminAnzahl++;
	}

	/** 
	 * Setzt alle Klausuren in einen zufälligen Termin. <br>
	 * Falls dies nicht möglich ist, wird die Gruppe in einen neuen Termin gesetzt.
	 * 
	 * @param pGruppe die Gruppe aller Klausuren, die in einen zufälligen Termin gesetzt werden sollen. 
	 */
	private void aktionSetzeKlausurgruppeInZufallsterminOderErzeugeNeuenTermin(@NotNull Vector<@NotNull Integer> pGruppe) {
		for (int terminNr : gibTermineInZufaelligerReihenfolge()) 
			if (aktionSetzeKlausurgruppeInTermin(pGruppe, terminNr))
				return; // Alle Klausuren wurden erfolgreich in den Termin gesetzt.
	
		aktionSetzeKlausurgruppeInNeuenTermin(pGruppe);
	}

	/** 
	 * Liefert TRUE, wenn der aktuelle Zustand besser als der gespeicherte Zustand 1 ist.
	 * 
	 * @return TRUE, wenn der aktuelle Zustand besser als der gespeicherte Zustand 1 ist. 
	 */
	boolean gibIstBesserAlsZustand1() {
		return gibVergleicheMitAktuellemZustand(_terminAnzahl1, _klausurZuTermin1);
	}

	/** 
	 * Liefert TRUE, wenn der aktuelle Zustand besser als der gespeicherte Zustand 2 ist.
	 * 
	 * @return TRUE, wenn der aktuelle Zustand besser als der gespeicherte Zustand 2 ist. 
	 */
	boolean gibIstBesserAlsZustand2() {
		return gibVergleicheMitAktuellemZustand(_terminAnzahl2, _klausurZuTermin2);
	}
	

	/** 
	 * Liefert die berechnete Zuordnung als Liste (Termine) von Listen (KlausurIDs).
	 * 
	 * @return die berechnete Zuordnung als Liste (Termine) von Listen (KlausurIDs).
	 */
	@NotNull List<@NotNull List<@NotNull Long>> gibErzeugeOutput() {
		
		@NotNull List<@NotNull List<@NotNull Long>> out = new Vector<>();
		for (int i = 0; i < _terminAnzahl; i++) 
			out.add(new Vector<>());
	
		for (@NotNull Long klausurID : _mapKlausurZuNummer.keySet()) {
			Integer klausurNr = _mapKlausurZuNummer.get(klausurID);
			if (klausurID == null) throw new DeveloperNotificationException("gibErzeugeOutput(): NULL-Wert bei 'klausurID'!");
			if (klausurNr == null) throw new DeveloperNotificationException("gibErzeugeOutput(): NULL-Wert bei 'klausurNr'!");
	
			int terminNr = _klausurZuTermin[klausurNr];
			if (terminNr <              0) throw new DeveloperNotificationException("gibErzeugeOutput(): negativer Wert bei 'terminNr'!");
			if (terminNr >= _terminAnzahl) throw new DeveloperNotificationException("gibErzeugeOutput(): zu großer Wert bei 'terminNr'!");
	
			// Termin-Klausur-Zuordnung
			out.get(terminNr).add(klausurID);
		}
	
		return out;
	}

	/** 
	 * Entfernt alle Klausur-Termin-Zuordnungen und passt die Datenstrukturen entsprechend an. 
	 */
	void aktionClear() {
		for (int i = 0; i < _klausurenAnzahl; i++)
			_klausurZuTermin[i] = -1;
	
		_terminAnzahl = 0;
	}

	/** 
	 * Speichert die aktuelle Klausur-Termin-Lage in Zustand 1. 
	 */
	void aktionZustand1Speichern() {
		_terminAnzahl1 = _terminAnzahl;

		for (int nr = 0; nr < _klausurenAnzahl; nr++)
			_klausurZuTermin1[nr] = _klausurZuTermin[nr];
	}

	/** 
	 * Lädt die aktuelle Klausur-Termin-Lage aus Zustand 1. 
	 */
	void aktionZustand1Laden() {
		aktionClear();
		_terminAnzahl = _terminAnzahl1;

		for (int nr = 0; nr < _klausurenAnzahl; nr++)
			_klausurZuTermin[nr] = _klausurZuTermin1[nr];
	}
	
	/** 
	 * Speichert die aktuelle Klausur-Termin-Lage in Zustand 2. 
	 */
	void aktionZustand2Speichern() {
		_terminAnzahl2 = _terminAnzahl;

		for (int nr = 0; nr < _klausurenAnzahl; nr++)
			_klausurZuTermin2[nr] = _klausurZuTermin[nr];
		
		//debug("BESSER");
	}

	/** 
	 * Lädt die aktuelle Klausur-Termin-Lage aus Zustand 2. 
	 */
	void aktionZustand2Laden() {
		aktionClear();
		_terminAnzahl = _terminAnzahl2;

		for (int nr = 0; nr < _klausurenAnzahl; nr++)
			_klausurZuTermin[nr] = _klausurZuTermin2[nr];
	}

	/** 
	 * Entfernt zunächst alle Klausuren aus ihren Terminen. <br>
	 * Geht dann alle Klausuren in zufälliger Reihenfolge durch und setzt sie dann in einen zufälligen Termin. <br> 
	 * Falls dies nicht klappt, wird ein neuer Termin erzeugt. 
	 */
	void aktion_Clear_KlausurgruppenZufaellig_TermineZufaellig() {
		aktionClear();

		for (@NotNull Vector<@NotNull Integer> gruppe : gibKlausurgruppenInZufaelligerReihenfolge())
			aktionSetzeKlausurgruppeInZufallsterminOderErzeugeNeuenTermin(gruppe);
	}

	/** 
	 * Entfernt zunächst alle Klausuren aus ihren Terminen. <br>
	 * Füllt dann die Termine nacheinander auf und wählt die Klausurgruppen zufällig. 
	 */
	void aktion_Clear_TermineNacheinander_GruppeZufaellig() {
		aktionClear();
	
		while (gibExistierenNichtverteilteKlausuren()) {
			int terminNr = gibErzeugeNeuenTermin();
			
			for (@NotNull Vector<@NotNull Integer> gruppe : gibKlausurgruppenInZufaelligerReihenfolge())
				if (gibIstKlausurgruppeUnverteilt(gruppe)) 
					aktionSetzeKlausurgruppeInTermin(gruppe, terminNr);
		}
	
	}

	/** 
	 * Entfernt zunächst alle Klausuren aus ihren Terminen. <br>
	 * Verteilt dann die Klausurgruppen mit höherem Knoten-Grad zuerst auf eine zufällige Schiene. 
	 * Falls dies nicht klappt, wird eine neue Schiene erzeugt. 
	 */
	void aktion_Clear_GruppeHoeherGradZuerst_TermineZufaellig() {
		aktionClear();
		
		for (@NotNull Vector<@NotNull Integer> gruppe : gibKlausurgruppenMitHoeheremGradZuerstEtwasPermutiert())
			aktionSetzeKlausurgruppeInZufallsterminOderErzeugeNeuenTermin(gruppe);
	}

	/** 
	 * Entfernt zunächst alle Klausuren aus ihren Terminen. <br>
	 * Füllt dann die Termine nacheinander auf und wählt die Klausurgruppen nach ihrem Grad. 
	 */
	public void aktion_Clear_TermineNacheinander_GruppeNachGrad() {
		aktionClear();
		
		while (gibExistierenNichtverteilteKlausuren()) {
			int terminNr = gibErzeugeNeuenTermin();
			
			for (@NotNull Vector<@NotNull Integer> gruppe : gibKlausurgruppenMitHoeheremGradZuerstEtwasPermutiert())
				if (gibIstKlausurgruppeUnverteilt(gruppe)) 
					aktionSetzeKlausurgruppeInTermin(gruppe, terminNr);
		}
	}

	/** 
	 * Ausgabe zum Debuggen der Tests.
	 * 
	 * @param header Überschrift der Debug-Ausgabe. 
	 */
	void debug(String header) {
		System.out.println();
		System.out.println(header);
	
		for (int s = 0; s < _terminAnzahl; s++) {
			String line = "";
			line += "    Schiene " + (s + 1) + ": ";
			for (int nr = 0; nr < _klausurenAnzahl; nr++)
				if (_klausurZuTermin[nr] == s) {
					GostKursklausur gostKlausur = _mapNummerZuKlausur.get(nr); 
					if (gostKlausur == null) throw new DeveloperNotificationException("Mapping _mapNummerZuKlausur.get("+nr+") ist NULL!");
					line += " "+gostKlausur.kursKurzbezeichnung + "/" + Arrays.toString(gostKlausur.kursSchiene);
				}
			System.out.println(line);
		}
	
		for (int nr = 0; nr < _klausurenAnzahl; nr++)
			if (_klausurZuTermin[nr] < 0)
				throw new DeveloperNotificationException("Klausur " + (nr + 1) + " --> ohne Schiene!");
	}


}
