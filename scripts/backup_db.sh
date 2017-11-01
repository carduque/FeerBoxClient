#!/bin/sh
CONFIG='/opt/FeerBoxClient/FeerBoxClient/target/classes/config.properties'
FILE='/opt/FeerBoxClient/FeerBoxClient/db/feerboxclient.db'
now=$(date +"%d_%m_%Y")
FILE_OUT="/opt/FeerBoxClient/FeerBoxClient/db/feerboxclient${now}.db.gz"

gzip -c $FILE > $FILE_OUT
if [ -f "$CONFIG" ]
then
  while IFS='=' read -r key value
  do
    if [ "$key" = "reference" ]; then
        reference=$value
    fi
  done  < "$CONFIG"
fi

curl --ftp-ssl -k ftp.feerbox.com --user db_backup@happycustomerbox.com:feerboxcompany2015 -T $FILE_OUT -Q "-RNFR feerboxclient${now}.db.gz" -Q "-RNTO ${reference}_feerboxclient${now}.db.gz"
rm $FILE_OUT
exit 0