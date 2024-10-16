package de.svws_nrw.db;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLInvalidAuthorizationSpecException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.sessions.server.ConnectionPool;
import org.eclipse.persistence.sessions.server.ServerSession;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;
import jakarta.validation.constraints.NotNull;

/**
 * Ein Manager für die Datenbank-Verbindungen der Anwendung.
 */
public final class ConnectionManager {

	/*
	 * Initialisiert den Shutdown-Hook, um alle nicht mehr benötigten
	 * Datenbank-Verbindungen, d.h. die zugehörigen {@link EntityManagerFactory}
	 * zu schließen.
	 */
	static {
		Runtime.getRuntime().addShutdownHook(new Thread(ConnectionManager::closeAll));
	}

	/** Ein Zufallszahlen-Generator */
	private static final Random random = new Random();

	/**
	 * Eine HashMap für den Zugriff auf einen Connection-Manager, der einer
	 * Datenbank-Konfiguration zugeordnet ist
	 */
	private static final HashMap<DBConfig, ConnectionManager> mapManager = new HashMap<>();

	/** Die verwendete Datenbank-Konfiguration {@link DBConfig} */
	private final @NotNull DBConfig config;

	/**
	 * Die zum Erzeugen der {@link EntityManager} verwendete Instanz der
	 * {@link EntityManagerFactory}
	 */
	private final @NotNull EntityManagerFactory emf;

	/**
	 * Erstellt einen neuen Connection-Manager
	 *
	 * @param config die Konfiguration für den Connection-Manager
	 */
	private ConnectionManager(@NotNull final DBConfig config) {
		this.config = config;
		this.emf = createEntityManagerFactory();
	}

	/**
	 * Gibt einen neuen JPA {@link EntityManager} zurück. Diese Methode wird
	 * innerhalb dieses Packages vom DBEntityManager bei der Erneuerung der
	 * Verbindung verwendet.
	 *
	 * @return der neue JPA {@link EntityManager}
	 */
	EntityManager getNewJPAEntityManager() {
		return emf.createEntityManager();
	}

	/**
	 * Gibt einen neuen JPA {@link EntityManager} zurück. Diese Methode wird
	 * innerhalb dieses Packages vom DBEntityManager bei der Erneuerung der
	 * Verbindung verwendet.
	 * Bei dieser Variante werden mehrere Versuche für einen Verbindungsaufbau
	 * durchgeführt. Zwischen den Versuchen wird eine angebene Zeit in Millisekunden
	 * abgewartet.
	 *
	 * @param connectionRetries   die Anzahl der Verbindungsversuche, bevor eine Exception weitergereicht wird
	 * @param retryTimeout   die Zeit in Millisekunden
	 *
	 * @return der neue JPA {@link EntityManager}
	 */
	EntityManager getNewJPAEntityManager(final int connectionRetries, final long retryTimeout) {
		PersistenceException resultingException = null;
		int triesLeft = connectionRetries + 1;
		do {
			triesLeft--;
			try {
				return getNewJPAEntityManager();
			} catch (final PersistenceException e) {
				resultingException = e;
				if (triesLeft <= 0) {
					throw resultingException;
				}
				try {
					Thread.sleep(retryTimeout);
				} catch (@SuppressWarnings("unused") final InterruptedException ie) {
					Thread.currentThread().interrupt();
				}
			}
		} while (triesLeft > 0);
		throw resultingException;
	}

	/**
	 * Gibt die Datenbank-Konfiguration dieses Verbindungs-Managers zurück.
	 *
	 * @return die Datenbank-Konfiguration dieses Verbindungs-Managers
	 */
	public DBConfig getConfig() {
		return this.config;
	}

	/**
	 * Intern genutzte Methode, um eine {@link EntityManagerFactory} für diesen
	 * {@link ConnectionManager} zu erstellen. Hierbei werden auch die
	 * Standardeinstellungen für die Datenbankverbindung in Form der
	 * Property-Map hinzugefügt (siehe
	 * {@link Persistence#createEntityManagerFactory(String, java.util.Map)})
	 *
	 * @return die {@link EntityManagerFactory}
	 */
	private @NotNull EntityManagerFactory createEntityManagerFactory() {
		final HashMap<String, Object> propertyMap = new HashMap<>();
		propertyMap.put("jakarta.persistence.jdbc.driver", config.getDBDriver().getJDBCDriver());
		final String username = config.getUsername();
		String password = config.getPassword();
		String url = config.getDBDriver().getJDBCUrl(config.getDBLocation(), config.getDBSchema());
		if (config.getDBDriver() == DBDriver.MDB) {
			try (Database db = DatabaseBuilder.open(new File(config.getDBLocation()))) {
				password = db.getDatabasePassword();
			} catch (@SuppressWarnings("unused") final IOException e) {
				password = "";
			}
			if (config.createDBFile())
				url += ";newdatabaseversion=V2000";
		}
		final String sessionName = "SVWSDB_url=" + url + "_user=" + config.getUsername() + "_random=" + random.ints(48, 123)  // from 0 to z
	        .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))  // filter some unicode characters
	        .limit(40)
	        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
	        .toString();
		propertyMap.put("jakarta.persistence.jdbc.url", url);
		propertyMap.put("jakarta.persistence.jdbc.user", username);
		propertyMap.put("jakarta.persistence.jdbc.password", password);
		propertyMap.put("eclipselink.session-name", sessionName);
		propertyMap.put("eclipselink.flush", "true");
		propertyMap.put("eclipselink.persistence-context.flush-mode", "commit");
		propertyMap.put("eclipselink.allow-zero-id", "true");
		propertyMap.put("eclipselink.logging.level", config.useDBLogging() ? "WARNING" : "OFF");
		// propertyMap.put("eclipselink.logging.level", config.useDBLogging() ? "INFO" : "OFF");
		// propertyMap.put("eclipselink.logging.level", "ALL");
		// propertyMap.put("eclipselink.logging.level.sql", "FINE");
		// propertyMap.put("eclipselink.logging.parameters", "true");
		// propertyMap.put("eclipselink.profiler","PerformanceProfiler");
		propertyMap.put("eclipselink.cache.shared.default", "false");
		// propertyMap.put("eclipselink.exception-handler",
		// "de.svws_nrw.db.DBExceptionHandler");
		if (config.getDBDriver() == DBDriver.SQLITE) {
			propertyMap.put("eclipselink.target-database", "Database");
			// Einstellungen des SQ-Lite-Treibers
			// READWRITE (2) + CREATE (4) + OPEN_URI (64) = 70 bzw. READWRITE (2) + OPEN_URI (64) = 66
			propertyMap.put("open_mode", (config.createDBFile()) ? "70" : "66");
			propertyMap.put("foreign_keys", "true");
		}
		return Persistence.createEntityManagerFactory("SVWSDB", propertyMap);
		// TODO avoid Persistence Unit "SVWSDB" as xml file
	}


	/**
	 * Schließt den Verbindungs-Manager
	 */
	private void close() {
		emf.close();
	}

	/**
	 * Gibt den Manager für die Datenbank-Verbindung für die übergebene
	 * Konfiguration zurück. Sollt keine Verbindung bestehen, so wird eine neue
	 * Verbindung erzeugt.
	 *
	 * @param config die Konfiguration der Datenbank-Verbindung
	 *
	 * @return der Manager
	 *
	 * @throws DBException bei einer fehlschlagenden Authentifizierung
	 */
	public static @NotNull ConnectionManager get(final DBConfig config) throws DBException {
		ConnectionManager man = mapManager.get(config);
		if (man != null) {
			final Map<String, Object> curProps = man.emf.getProperties();
			final String curUser = (String) curProps.get("jakarta.persistence.jdbc.user");
			final String curPassword = (String) curProps.get("jakarta.persistence.jdbc.password");
			if (!config.getUsername().equals(curUser) || !config.getPassword().equals(curPassword)) {
				mapManager.remove(config);
				man.close();
				man = null;
			}
		}
		if (man == null) {
			man = new ConnectionManager(config);
			try {
				try (EntityManager em = man.getNewJPAEntityManager()) {
					mapManager.put(config, man);
				}
			} catch (final PersistenceException pe) {
				if ((pe.getCause() instanceof final DatabaseException de) && (de.getCause() instanceof final SQLInvalidAuthorizationSpecException ae)) {
					man.close();
					throw new DBException("Fehler beim Aufbau der Verbindung. Überprüfen Sie Benutzername und Kennwort.");
				}
				if (pe.getCause() instanceof DatabaseException) {
					man.close();
					throw new DBException("Fehler beim Aufbau der Verbindung. Überprüfen Sie die Verbindungsparameter.");
				}
				if (pe.getMessage().startsWith("java.lang.IllegalStateException: Could not determine FileFormat")) {
					man.close();
					throw new DBException("Fehlerhaftes oder zu altes MDB-Datei-Format.");
				}
				throw pe;
			}
		} else {
			// Führe eine Dummy-DB-Abfrage aus, um Probleme mit der
			// Server-seitigen Beendung einer Verbindung zu erkennen
			try {
				try (EntityManager em = man.getNewJPAEntityManager()) {
					try {
						em.getTransaction().begin();
						@SuppressWarnings("resource")
						final Connection conn = em.unwrap(Connection.class);
						try (Statement stmt = conn.createStatement()) {
							try (ResultSet rs = stmt.executeQuery("SELECT 1")) {
								rs.next();
								rs.getInt(1);
							}
						}
						em.getTransaction().commit();
						em.clear();
					} catch (@SuppressWarnings("unused") SQLException | DatabaseException e) {
						// Bestimme die Anzahl der verfügbaren Verbindungen
						final ServerSession serverSession = em.unwrap(ServerSession.class);
						final ConnectionPool pool = serverSession.getConnectionPools().get("default");
						if (pool == null) {
							System.err.println("Fehler beim Zugriff auf den DB-Connection-Pool default");
						} else {
							System.err.println(
									"INFO: Verbindung zur Datenbank unterbrochen - versuche sie neu aufzubauen...");
							System.err.println("Total number of connections: " + pool.getTotalNumberOfConnections());
							System.err.println("Available number of connections: " + pool.getConnectionsAvailable().size());
							pool.resetConnections();
						}
					}
				}
			} catch (final PersistenceException pe) {
				if ((pe.getCause() instanceof final DatabaseException de) && (de.getCause() instanceof final SQLInvalidAuthorizationSpecException ae)) {
					mapManager.remove(config);
					man.close();
					throw new DBException(ae);
				}
				throw pe;
			}
		}
		return man;
	}

	/**
	 * Schließt den Connection-Manager für die übergebene Config und entfernt
	 * ihn aus der Liste der Manager
	 *
	 * @param config die Konfiguration des zu schließenden Managers
	 */
	private static void closeSingle(final DBConfig config) {
		final ConnectionManager manager = mapManager.get(config);
		if (manager == null) {
			System.err.println("Fehler beim Schließen des Verbindungs-Managers zu " + config.getDBLocation()
					+ " (Schema: " + config.getDBSchema() + "), Datenbank-Benutzer: " + config.getUsername());
			return;
		}
		manager.close();
		mapManager.remove(config);
		System.out.println("Verbindungs-Manager des Datenbank-Benutzers " + config.getUsername() + " zu "
				+ config.getDBLocation() + " (Schema: " + config.getDBSchema() + ") geschlossen.");
	}

	/**
	 * Schließt alle noch offenenen Datenbank-Verbindungen.
	 */
	private static void closeAll() {
		final List<DBConfig> configs = mapManager.keySet().stream().toList();
		for (final DBConfig config : configs)
			closeSingle(config);
	}

}
