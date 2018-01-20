#!/bin/bash
sqlite3 /opt/FeerBoxClient/FeerBoxClient/db/feerboxclient.db "drop table if exists $1;"
echo "table $1 removed (if exists) from db"
