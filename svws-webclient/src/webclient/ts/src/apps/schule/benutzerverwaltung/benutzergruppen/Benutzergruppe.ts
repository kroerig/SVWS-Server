import { App } from "../../../BaseApp";
import { DataBenutzergruppe } from "./DataBenutzergruppe";
import {ListBenutzergruppe } from "./ListBenutzergruppe";
import { ListBenutzergruppenBenutzer } from "./ListBenutzergruppenBenutzer";



/**
 * Diese Klasse enthält den Teil der SVWS-Client-Applikation für die Benutzergruppe.
 * Beim Erstellen der SVWS-Client-Applikation wird ein Objekt dieser Klasse
 * erzeugt und steht unter "this.$app.apps.kataloge.benutzer" allen Vue-Komponenten zur
 * Verfügung. Hierdurch ist eine Kommunikation mit der Open-API-Schnittstelle
 * möglich, ohne das die Benutzerdaten, etc. über mehrere Komponenten hinweg
 * aktualisiert werden müssen.
 */
export class Benutzergruppe extends App {

	public auswahl!: ListBenutzergruppe;

	/**
	 * Das Objekt zur Verwaltung der Kommunikation bezüglich der Benutzergruppendaten mit dem
	 * SVWS-Server
	 */
	public dataBenutzergruppe!: DataBenutzergruppe;
	


	/**
	 * Initialisiert die Klasse und holt die relevanten Daten vom Server
	 *
	 * @returns {Promise<void>}
	 */
	public async init(): Promise<void> {
		this.auswahl = new ListBenutzergruppe;
		this.dataBenutzergruppe = new DataBenutzergruppe();
		this.auswahl.add_data([this.dataBenutzergruppe]);
	}

}