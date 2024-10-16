package de.svws_nrw.data.gost;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import de.svws_nrw.config.SVWSKonfiguration;
import de.svws_nrw.core.data.SimpleOperationResponse;
import de.svws_nrw.core.logger.LogConsumerConsole;
import de.svws_nrw.core.logger.LogConsumerList;
import de.svws_nrw.core.logger.Logger;
import de.svws_nrw.data.SimpleBinaryMultipartBody;
import de.svws_nrw.db.DBEntityManager;
import de.svws_nrw.db.utils.ApiOperationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * Diese Klasse stellt Methoden für den Import und Export von Kurs 42 - Blockungen
 * zur Vefügung.
 */
public final class DataKurs42 {

	private DataKurs42() {
		throw new IllegalStateException("Instantiation of " + DataKurs42.class.getName() + " not allowed");
	}

	private static final Random _random = new Random();


	private static boolean removeTempFiles(final Path path) {
		try {
			final Path pBlockung = path.resolve("Blockung.txt");
			if (Files.exists(pBlockung))
				Files.delete(pBlockung);
			final Path pSchueler = path.resolve("Schueler.txt");
			if (Files.exists(pSchueler))
				Files.delete(pSchueler);
			final Path pFaecher = path.resolve("Faecher.txt");
			if (Files.exists(pFaecher))
				Files.delete(pFaecher);
			final Path pKurse = path.resolve("Kurse.txt");
			if (Files.exists(pKurse))
				Files.delete(pKurse);
			final Path pSchienen = path.resolve("Schienen.txt");
			if (Files.exists(pSchienen))
				Files.delete(pSchienen);
			final Path pBlockplan = path.resolve("Blockplan.txt");
			if (Files.exists(pBlockplan))
				Files.delete(pBlockplan);
			final Path pFachwahlen = path.resolve("Fachwahlen.txt");
			if (Files.exists(pFachwahlen))
				Files.delete(pFachwahlen);
			Files.delete(path);
		} catch (@SuppressWarnings("unused") final IOException e) {
			return false;
		}
		return true;
	}


	private static boolean unzipMultipartData(final Path path, final byte[] data) {
    	try {
    		Files.createDirectories(path);
    		try (ZipInputStream zipInput = new ZipInputStream(new ByteArrayInputStream(data))) {
	    		ZipEntry zipFile;
	    		while ((zipFile = zipInput.getNextEntry()) != null) {
	    			final String filename = switch (zipFile.getName()) {
	    				case "Blockung.txt", "Schueler.txt", "Faecher.txt", "Kurse.txt", "Schienen.txt", "Blockplan.txt", "Fachwahlen.txt" -> zipFile.getName();
	    				default -> null;
	    			};
	    			if (filename != null) {
	    				final Path filePath = path.resolve(filename);
	    				Files.write(filePath, zipInput.readAllBytes(), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
	    			}
	    			zipInput.closeEntry();
	    		}
    		}
		} catch (@SuppressWarnings("unused") final IOException e) {
			return false;
		}
    	return true;
	}


    /**
     * Importiert die in dem Multipart übergebene Datei.
     *
     * @param conn        die Datenbank-Verbindung
     * @param multipart   der Multipart-Body mmit der Datei
     *
     * @return die HTTP-Response mit dem Log
     */
    public static Response importZip(final DBEntityManager conn, final SimpleBinaryMultipartBody multipart) {
    	final Logger logger = new Logger();
    	final LogConsumerList log = new LogConsumerList();
    	logger.addConsumer(log);
    	logger.addConsumer(new LogConsumerConsole());

    	boolean success = true;
    	Status statusCode = Status.OK;
    	try {
	    	// Erstelle temporär eine Zip-Datei aus dem übergebenen Byte-Array
	    	final String tmpDirectory = SVWSKonfiguration.get().getTempPath();
	        final String tmpFilename = conn.getUser().connectionManager.getConfig().getDBSchema() +  "_" + _random.ints(48, 123)  // from 0 to z
	          .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))  // filter some unicode characters
	          .limit(40)
	          .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
	          .toString() + ".zip";
	        logger.logLn("Erstelle eine temporäres Verzechnis mit dem Inhalt der Zip-Datei unter dem Namen \"" + tmpDirectory + "/" + tmpFilename + "\"");
			final Path path = Paths.get(tmpDirectory).resolve(tmpFilename);
			if (!unzipMultipartData(path, multipart.data)) {
				logger.logLn(2, "Fehler beim Erstellen der temporären Zip-Datei unter dem Namen \"" + tmpDirectory + "/" + tmpFilename + "\"");
				throw new ApiOperationException(Status.CONFLICT, "Fehler beim Erstellen der temporären Zip-Datei unter dem Namen \"" + tmpDirectory + "/" + tmpFilename + "\"");
			}

	    	logger.logLn("Importiere die Blockung mithilfe der extrahierten Daten:");

			try {
				DBUtilsGostBlockung.importKurs42(conn, logger, path);
				logger.logLn("  Import beendet");
			} finally {
				// Entferne die temporär angelegten Dateien wieder...
				logger.logLn("Löschen des temporären Verzeichnis \"" + tmpDirectory + "/" + tmpFilename + "\".");
				if (removeTempFiles(path)) {
					logger.logLn(2, "[OK]");
				} else {
					success = false;
					logger.logLn(2, "[FEHLER]");
				}
			}
    	} catch (final Exception e) {
			success = false;
    		if (e instanceof final ApiOperationException aoe) {
    			statusCode = aoe.getStatus();
    		} else {
    			logger.logLn("  [FEHLER] Unerwarteter Fehler: " + e.getLocalizedMessage());
    			statusCode = Status.INTERNAL_SERVER_ERROR;
    		}
    	}
		final SimpleOperationResponse daten = new SimpleOperationResponse();
		daten.success = success;
		daten.log = log.getStrings();
		return Response.status(statusCode).type(MediaType.APPLICATION_JSON).entity(daten).build();
    }

}
