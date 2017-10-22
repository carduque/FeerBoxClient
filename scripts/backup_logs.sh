#!/bin/sh
CONFIG='/opt/FeerBoxClient/FeerBoxClient/target/classes/config.properties'
if [ -f "$CONFIG" ]
then
  while IFS='=' read -r key value
  do
    if [ "$key" = "reference" ]; then
        reference=$value
    fi
  done  < "$CONFIG"
fi
FILE='/opt/FeerBoxClient/FeerBoxClient/logs'
now=$(date +"%d_%m_%Y")
FILE_OUT="${reference}_feerbox-client-logs${now}.tar.gz"
tar -czvf $FILE_OUT -C $FILE .
curl --ftp-ssl -k ftp.feerbox.com --user db_backup@happycustomerbox.com:feerboxcompany2015 -T $FILE_OUT
rm $FILE_OUT
exit 0