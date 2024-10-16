#!/bin/bash

if [ -z "$TESTDB_PASSWORD" ]; then
	exit 1
fi

CURRENT_SCRIPT=$(readlink -f "$0")
CURRENT_DIR=$(dirname "$CURRENT_SCRIPT")

# Testdatenbanken von github als zip-File herunterladen
echo "Lade Testdatenbanken herunter ..."
wget https://github.com/SVWS-NRW/SVWS-TestMDBs/archive/refs/heads/main.zip -O $CURRENT_DIR/databases.zip

# Access-Datenbanken aus den heruntergeladenen zip-File extrahieren
echo "Extrahiere Access-Datenbanken ..."
unzip $CURRENT_DIR/databases.zip -d $CURRENT_DIR/databases "*.mdb"
rm -rf $CURRENT_DIR/databases.zip

# Import (MigrateDB) der Access-Datenbank(en) durchführen ...
MDBFILE="$CURRENT_DIR/databases/SVWS-TestMDBs-main/GOST_Abitur/Abi-Test-Daten-01/GymAbi.mdb"
echo "Importiere Datenbank: ${MDBFILE} ..."

# Überprüfe, ob die Umgebungsvariable MariaDB_ROOT_USER gesetzt ist
if [ -n "$MariaDB_ROOT_USER" ]; then
    # Wenn MariaDB_ROOT_USER gesetzt ist, führe den Code für den Fall aus
	java -cp "svws-server-app-*.jar:./*:./lib/*" de.svws_nrw.db.utils.app.MigrateDB -j -r -1 -sd "MDB" \
	   -sl "${MDBFILE}" -sp "${TESTDB_PASSWORD}" \
	   -td "MARIA_DB" \
	   -tl ${MariaDB_HOST} \
	   -ts ${MariaDB_DATABASE} \
	   -tu ${MariaDB_USER} \
	   -tp ${MariaDB_PASSWORD} \
	   -tg ${MariaDB_ROOT_USER} \
	   -tr ${MariaDB_ROOT_PASSWORD}
else
    # Wenn MariaDB_ROOT_USER nicht gesetzt ist, führe den Code für den anderen Fall aus
	java -cp "svws-server-app-*.jar:./*:./lib/*" de.svws_nrw.db.utils.app.MigrateDB -j -r -1 -sd "MDB" \
	   -sl "${MDBFILE}" -sp "${TESTDB_PASSWORD}" \
	   -td "MARIA_DB" \
	   -tl ${MariaDB_HOST} \
	   -ts ${MariaDB_DATABASE} \
	   -tu ${MariaDB_USER} \
	   -tp ${MariaDB_PASSWORD} \
	   -tr ${MariaDB_ROOT_PASSWORD}
fi



