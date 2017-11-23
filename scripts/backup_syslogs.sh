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
FILE1='/var/log/daemon.log'
FILE2='/var/log/syslog'
FILE3='/var/log/lastlog'
FILE4='/var/log/messages'
FILE5='/var/log/kern.log'
FILE6='/var/log/user.log'
FILE7='/var/log/debug'
FILE8='/var/log/lightdm/lightdm.log'
FILE9='/var/log/boot.log'
FILE10='/var/log/lighttpd/error.log'
now=$(date +"%d_%m_%Y")
FILE_OUT="${reference}_feerbox-client-syslogs${now}.tar.gz"
sudo tar -czvf $FILE_OUT $FILE1 $FILE2 $FILE3 $FILE4 $FILE5 $FILE6 $FILE7 $FILE8 $FILE9 $FILE10
sudo curl --verbose --ftp-ssl -k ftp.feerbox.com --user db_backup@happycustomerbox.com:feerboxcompany2015 -T $FILE_OUT
sudo rm -f $FILE_OUT
exit 0