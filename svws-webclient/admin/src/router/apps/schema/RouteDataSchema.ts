import { type Ref, ref, shallowRef } from "vue";

import type { BenutzerKennwort , BenutzerListeEintrag, Comparator,  List, SchuleInfo, SchulenKatalogEintrag } from "@core";
import { ArrayList, DatenbankVerbindungsdaten, DeveloperNotificationException, JavaString, MigrateBody, OpenApiError, SchemaListeEintrag, SimpleOperationResponse } from "@core";

import { api } from "~/router/Api";
import { RouteManager } from "~/router/RouteManager";
import type { RouteNode } from "~/router/RouteNode";

import { routeApp } from "../RouteApp";
import { routeSchema } from "~/router/apps/schema/RouteSchema";
import { routeSchemaUebersicht } from "~/router/apps/schema/uebersicht/RouteSchemaUebersicht";
import type { SchemaMigrationQuelle } from "~/components/schema/SchemaMigrationQuelle";


interface RouteStateSchema {
	auswahl: SchemaListeEintrag | undefined;
	auswahlGruppe: SchemaListeEintrag[];
	mapSchema: Map<string, SchemaListeEintrag>;
	revision: number | null;
	schuleInfo: SchuleInfo | undefined;
	schulen: List<SchulenKatalogEintrag>;
	admins: List<BenutzerListeEintrag>;
	view: RouteNode<any, any>;
}

export class RouteDataSchema {

	private static _defaultState : RouteStateSchema = {
		auswahl: undefined,
		auswahlGruppe: [],
		mapSchema: new Map(),
		revision: null,
		schuleInfo: undefined,
		schulen: new ArrayList<SchulenKatalogEintrag>(),
		admins: new ArrayList<BenutzerListeEintrag>(),
		view: routeSchemaUebersicht,
	};

	private _state = shallowRef(RouteDataSchema._defaultState);

	private _migrationQuellinformationen = ref<SchemaMigrationQuelle>({
		dbms: 'mdb',
		schildzentral: false,
		schulnummer: "",
		location: "",
		schema: "",
		user: "",
		password: "",
	});

	private setPatchedDefaultState(patch: Partial<RouteStateSchema>) {
		this._state.value = Object.assign({ ... RouteDataSchema._defaultState }, patch);
	}

	private setPatchedState(patch: Partial<RouteStateSchema>) {
		this._state.value = Object.assign({ ... this._state.value }, patch);
	}

	private commit(): void {
		this._state.value = { ... this._state.value };
	}

	public get migrationQuellinformationen() : Ref<SchemaMigrationQuelle> {
		return this._migrationQuellinformationen;
	}

	public resetMigrationQuellinformationen() {
		this._migrationQuellinformationen.value.dbms = 'mdb';
		this._migrationQuellinformationen.value.schildzentral = false;
		this._migrationQuellinformationen.value.schulnummer = "";
		this._migrationQuellinformationen.value.location = "";
		this._migrationQuellinformationen.value.schema = "";
		this._migrationQuellinformationen.value.user = "";
		this._migrationQuellinformationen.value.password = "";
	}

	public get view(): RouteNode<any,any> {
		return this._state.value.view;
	}

	get auswahl(): SchemaListeEintrag | undefined {
		return this._state.value.auswahl;
	}

	get admins(): List<BenutzerListeEintrag> {
		return new ArrayList<BenutzerListeEintrag>(this._state.value.admins);
	}

	get auswahlGruppe(): SchemaListeEintrag[] {
		return this._state.value.auswahlGruppe;
	}

	get hatAuswahlSVWSSchema(): boolean {
		if (this.auswahl === undefined)
			return false;
		return (this.auswahl.revision >= 0);
	}

	get auswahlSVWSSchema(): SchemaListeEintrag | null {
		if ((this.auswahl === undefined) || (this.auswahl.revision < 0))
			return null;
		return this.auswahl;
	}

	get mapSchema(): Map<string, SchemaListeEintrag> {
		return this._state.value.mapSchema;
	}

	get revision(): number | null {
		return this._state.value.revision;
	}

	get schuleInfo(): SchuleInfo | undefined {
		return this._state.value.schuleInfo;
	}

	get schulen(): List<SchulenKatalogEintrag> {
		return this._state.value.schulen;
	}

	private getEmpty(name: string) : SchemaListeEintrag {
		const entry = new SchemaListeEintrag();
		entry.name = name;
		entry.revision = -1;
		entry.isTainted = false;
		return entry;
	}

	/**
	 * Initialisiert die Schema-Liste
	 */
	public async init(schemaname : string | undefined) {
		api.status.start();
		const mapSchema = new Map<string, SchemaListeEintrag>();
		const listSchema : List<SchemaListeEintrag> = await api.privileged.getSchemaListe();
		listSchema.sort(<Comparator<SchemaListeEintrag>>{ compare(s1 : SchemaListeEintrag, s2 : SchemaListeEintrag) { return JavaString.compareToIgnoreCase(s1.name, s2.name); } });
		for (const s of listSchema)
			mapSchema.set(s.name.toLocaleLowerCase(), s);
		let auswahl : SchemaListeEintrag | undefined = undefined;
		if (mapSchema.size > 0) {
			auswahl = schemaname === undefined ? listSchema.get(0) : mapSchema.get(schemaname.toLocaleLowerCase());
			if (auswahl === undefined)
				auswahl = mapSchema.values().next().value;
		}
		const revision = await api.server.getServerDBRevision();
		const view = routeSchemaUebersicht;
		this.setPatchedDefaultState({
			mapSchema,
			auswahl,
			revision,
			view
		});
		api.status.stop();
	}

	/**
	 * Setzt das ausgewählte Schema.
	 *
	 * @param schema   das ausgewählte Schema
	 */
	public async setSchema(schema: SchemaListeEintrag | undefined) {
		if ((schema === undefined) || (this.mapSchema.size === 0))
			return;
		const auswahl = this.mapSchema.has(schema.name.toLocaleLowerCase()) ? schema : undefined;
		let schuleInfo = undefined;
		let schulen: List<SchulenKatalogEintrag> = new ArrayList();
		let admins: List<BenutzerListeEintrag> = new ArrayList();
		if (auswahl !== undefined && auswahl.revision > 0) {
			// Es liegt ein SVWS-Schema vor ...
			try {
				// ... versuche die Informationen zur Schule zu laden
				schuleInfo = await api.privileged.getSchuleInfo(auswahl.name);
			} catch (e) {
				// Gelingt dies nicht, so muss die Initialisierung über den Schul-Katalog angeboten werden
				console.log("Die Information zur Schule konnten für das Schema " + auswahl.name + " nicht gefunden werden, biete Möglichkeit zur Initialisierung mit Schulnummer.")
				schulen = await api.privileged.getAllgemeinenKatalogSchulen();
			}
			// Wenn die Revision des Schemas aktuell ist, dann lade auch die Informationen zu den Admin-Benutzern
			if (auswahl.revision === this.revision)
				admins = await api.privileged.getSchemaAdmins(auswahl.name);
		}
		this.setPatchedState({ auswahl, schuleInfo, admins, schulen });
	}

	public async setView(view: RouteNode<any,any>) {
		if (routeSchema.children.includes(view))
			this.setPatchedState({ view });
		else
			throw new Error("Diese für das Schema gewählte Ansicht wird nicht unterstützt.");
	}

	gotoSchema = async (auswahl: SchemaListeEintrag | undefined) => {
		if (auswahl === undefined || auswahl === null) {
			await RouteManager.doRoute({ name: routeSchema.name });
			return;
		}
		const redirect_name: string = (routeSchema.selectedChild === undefined) ? routeSchemaUebersicht.name : routeSchema.selectedChild.name;
		await RouteManager.doRoute({ name: redirect_name, params: { schema: auswahl.name } });
		this.setPatchedState({auswahl});
	}

	setAuswahlGruppe = (auswahlGruppe: SchemaListeEintrag[]) =>	{
		this.setPatchedState({ auswahlGruppe });
		if (auswahlGruppe.length > 0 && routeApp.selectedChild?.name === 'schema')
			RouteManager.doRoute('/schemagruppe');
		else if (auswahlGruppe.length === 0 && routeApp.selectedChild?.name === 'schemagruppe')
			RouteManager.doRoute('schema');
	}

	upgradeSchema = async () => {
		if (this.auswahl === undefined)
			throw new DeveloperNotificationException("Es soll ein Backup angelegt werden, aber es ist kein Schema ausgewählt.");
		api.status.start();
		const result = await api.privileged.updateSchemaToCurrent(this.auswahl.name);
		api.status.stop();
		await this.init(this.auswahl.name);
		return result;
	}

	removeSchemata = async () => {
		api.status.start();
		for (const schema of this.auswahlGruppe) {
			await api.privileged.destroySchema(schema.name);
			this.mapSchema.delete(schema.name.toLocaleLowerCase());
		}
		api.status.stop();
		if (this.auswahl && this.auswahlGruppe.includes(this.auswahl)) {
			this._state.value.auswahl = undefined;
			await this.gotoSchema(undefined);
		}
		this.setAuswahlGruppe([]);
	}

	addSchema = async (data: BenutzerKennwort, schema: string) => {
		api.status.start();
		const result = await api.privileged.createSchemaCurrent(data, schema);
		const list = await api.privileged.getSVWSSchemaListe();
		for (const item of list)
			if (item.name === schema) {
				this.mapSchema.set(item.name.toLocaleLowerCase(), item);
				this.setPatchedState({mapSchema: this.mapSchema});
				await this.gotoSchema(item);
				break;
			}
		api.status.stop();
		return result;
	}

	importSchema = async (data: FormData, schema: string) => {
		api.status.start();
		const result = await api.privileged.importSQLite2Schema(data, schema);
		api.status.stop();
		await this.init(schema);
		api.status.start();
		await this.setSchema(this.auswahl);
		api.status.stop();
		return result;
	}

	backupSchema = async () => {
		if (this.auswahl === undefined)
			throw new DeveloperNotificationException("Es soll ein Backup angelegt werden, aber es ist kein Schema ausgewählt.");
		api.status.start();
		const data = await api.privileged.exportSQLiteFrom(this.auswahl.name);
		api.status.stop();
		return data;
	}

	restoreSchema = async (data: FormData) => {
		if (this.auswahl === undefined)
			throw new DeveloperNotificationException("Es soll ein Backup wiederhergestellt werden, aber es ist kein Schema ausgewählt.");
		api.status.start();
		let result = new SimpleOperationResponse();
		try {
			result = await api.privileged.importSQLiteInto(data, this.auswahl.name);
		} catch (error) {
			if ((error instanceof OpenApiError) && (error.response instanceof Response)) {
				try {
					const json = await error.response.text();
					result = SimpleOperationResponse.transpilerFromJSON(json);
				} catch(e) {
					result = new SimpleOperationResponse();
					result.success = false;
					result.log.add("Fehler beim Aufruf der API-Methode " + error.response.statusText + " (" + error.response.status + ")");
				}
			} else {
				result = new SimpleOperationResponse();
				result.success = false;
				result.log.add("Es soll ein Backup wiederhergestellt werden, aber es gab einen unterwarteten Fehler: " + error);
			}
		}
		api.status.stop();
		await this.init(this.auswahl.name);
		api.status.start();
		await this.setSchema(this.auswahl);
		api.status.stop();
		return result;
	}

	duplicateSchema = async (formData: FormData, duplikat: string) => {
		if (this.auswahl === undefined)
			throw new DeveloperNotificationException("Es soll ein Duplikat angelegt werden, aber es ist kein Schema ausgewählt.");
		api.status.start();
		const { data } = await api.privileged.exportSQLiteFrom(this.auswahl.name);
		formData.append("database", data);
		const result = await this.importSchema(formData, duplikat);
		api.status.stop();
		return result;
	}

	migrateSchema = async (formData: FormData) => {
		const currSchema = this.auswahl?.name;
		const migrateBody = new MigrateBody();
		const datenbankVerbindungsdaten = new DatenbankVerbindungsdaten();
		const db = formData.get('db')?.toString() || null;
		const schulnummer = parseInt(formData.get('schulnummer')?.toString() || '');
		let schema = formData.get('schema')?.toString() || null;
		if (schema === currSchema) {
			datenbankVerbindungsdaten.location = formData.get('srcLocation')?.toString() || null;
			datenbankVerbindungsdaten.schema = formData.get('srcSchema')?.toString() || null;
			datenbankVerbindungsdaten.username = formData.get('srcUsername')?.toString() || null;
			datenbankVerbindungsdaten.password = formData.get('srcPassword')?.toString() || null;
		} else {
			migrateBody.srcLocation = formData.get('srcLocation')?.toString() || null;
			migrateBody.srcSchema = formData.get('srcSchema')?.toString() || null;
			migrateBody.srcUsername = formData.get('srcUsername')?.toString() || null;
			migrateBody.srcPassword = formData.get('srcPassword')?.toString() || null;
			migrateBody.schemaUsername = formData.get('schemaUsername')?.toString() || api.username;
			migrateBody.schemaUserPassword = formData.get('schemaUserPassword')?.toString() || null;
		}
		if (schema === null || db === null)
			throw new DeveloperNotificationException("Es muss ein Schema und eine Datenbank für eine Migration angegeben werden.");
		api.status.start();
		let result = new SimpleOperationResponse();
		try {
			switch (db) {
				case 'mariadb':
					if (schulnummer)
						if (schema === currSchema)
							result = await api.privileged.migrateMariaDBSchulnummerInto(datenbankVerbindungsdaten, schema, schulnummer);
						else
							result = await api.privileged.migrateMariaDB2SchemaSchulnummer(migrateBody, schema, schulnummer);
					else
						if (schema === currSchema)
							result = await api.privileged.migrateMariaDBInto(datenbankVerbindungsdaten, schema);
						else
							result = await api.privileged.migrateMariaDB2Schema(migrateBody, schema);
					break;
				case 'mysql':
					if (schulnummer)
						if (schema === currSchema)
							result = await api.privileged.migrateMySqlSchulnummerInto(datenbankVerbindungsdaten, schema, schulnummer);
						else
							result = await api.privileged.migrateMySQL2SchemaSchulnummer(migrateBody, schema, schulnummer);
					else
						if (schema === currSchema)
							result = await api.privileged.migrateMySqlInto(datenbankVerbindungsdaten, schema);
						else
							result = await api.privileged.migrateMySQL2Schema(migrateBody, schema);
					break;
				case 'mssql':
					if (schulnummer)
						if (schema === currSchema)
							result = await api.privileged.migrateMsSqlServerSchulnummerInto(datenbankVerbindungsdaten, schema, schulnummer);
						else
							result = await api.privileged.migrateMSSQL2SchemaSchulnummer(migrateBody, schema, schulnummer);
					else
						if (schema === currSchema)
							result = await api.privileged.migrateMsSqlServerInto(datenbankVerbindungsdaten, schema);
						else
							result = await api.privileged.migrateMSSQL2Schema(migrateBody, schema);
					break;
				case 'mdb':
					if (schema === currSchema)
						result = await api.privileged.migrateMDBInto(formData, schema);
					else
						result = await api.privileged.migrateMDB2Schema(formData, schema);
					break;
				default:
					throw new DeveloperNotificationException("Es ist ein Fehler aufgetreten bei der Migration");
			}
		} catch(error) {
			schema = currSchema ?? null;
			if ((error instanceof OpenApiError) && (error.response instanceof Response)) {
				try {
					const json = await error.response.text();
					result = SimpleOperationResponse.transpilerFromJSON(json);
				} catch(e) {
					result = new SimpleOperationResponse();
					result.success = false;
					result.log.add("Fehler beim Aufruf der API-Methode " + error.response.statusText + " (" + error.response.status + ")");
				}
			} else {
				result = new SimpleOperationResponse();
				result.success = false;
				result.log.add("Beim Migrieren gab es einen unterwarteten Fehler: " + error);
			}
		}
		await this.init(schema ?? undefined);
		await this.setSchema(this.auswahl);
		api.status.stop();
		return result;
	}

	initSchema = async (schulnummer: number) => {
		if (this.auswahl === undefined)
			throw new DeveloperNotificationException("Es soll ein Schema initialisiert werden, aber es ist kein Schema ausgewählt.");
		api.status.start();
		const result = await api.privileged.initSchemaMitSchule(this.auswahl.name, schulnummer);
		await this.setSchema(this.auswahl);
		api.status.stop();
		return result;
	}

	addExistingSchemaToConfig = async(data: BenutzerKennwort, schema: string) => {
		if ((schema === undefined) || (schema === ""))
			throw new DeveloperNotificationException("Es soll ein Schema zur Konfiguration hinzugefügt werden, aber es ist kein Schemaname angegeben.");
		api.status.start();
		await api.privileged.importExistingSchema(data, schema);
		const eintrag = this.mapSchema.get(schema);
		if (eintrag !== undefined)
			eintrag.isInConfig = true;
		api.status.stop();
		this.commit();
	}

	refresh = async () => await this.init(undefined);
}
