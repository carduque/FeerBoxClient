#!/bin/bash
sqlite3 /opt/pi4j/examples/feerbox2.db "delete from Status;"
sqlite3 /opt/pi4j/examples/feerbox2.db "delete from Answers;"
sqlite3 /opt/pi4j/examples/feerbox2.db "delete from logtable;"
sqlite3 /opt/pi4j/examples/feerbox2.db "delete from Commands;"
sqlite3 /opt/pi4j/examples/feerbox2.db "delete from MACS;"
echo "feerbox2 db cleaned"
