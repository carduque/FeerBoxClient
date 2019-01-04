#!/bin/bash
from_date=$1
from_time=$2
to_date=$3
to_time=$4

echo "doing backup of database"
cp /opt/FeerBoxClient/FeerBoxClient/db/feerboxclient.db /opt/FeerBoxClient/FeerBoxClient/db/feerboxclient-resendweather.db

echo "select count(*) from weathersensor where time>'${from_date} ${from_time}' and time<'${to_date} ${to_time}' and upload=1;" 
sqlite3 /opt/FeerBoxClient/FeerBoxClient/db/feerboxclient.db "select count(*) from weathersensor where time>'${from_date} ${from_time}' and time<'${to_date} ${to_time}' and upload=1;"

echo "update weathersensor set upload=0 where time>'${from_date} ${from_time}' and time<'${to_date} ${to_time}';" 
sqlite3 /opt/FeerBoxClient/FeerBoxClient/db/feerboxclient.db "update weathersensor set upload=0 where time>'${from_date} ${from_time}' and time<'${to_date} ${to_time}' and upload=1;"

echo "select count(*) from weathersensor where time>'${from_date} ${from_time}' and time<'${to_date} ${to_time}' and upload=0;" 
sqlite3 /opt/FeerBoxClient/FeerBoxClient/db/feerboxclient.db "select count(*) from weathersensor where time>'${from_date} ${from_time}' and time<'${to_date} ${to_time}' and upload=0;"

