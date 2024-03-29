#!/bin/bash
sudo echo ds1307 0x68 > /sys/class/i2c-adapter/i2c-1/new_device
sudo hwclock -s
cd /opt/FeerBoxClient/FeerBoxClient/target/classes/
sudo java -Dsun.security.smartcardio.library=/usr/lib/arm-linux-gnueabihf/libpcsclite.so.1 -classpath .:classes:/opt/pi4j/lib/'*' com.feerbox.client.StartFeerBoxClient &
disown
echo $! > /opt/FeerBoxClient/FeerBoxClient/feerbox.pid
pgrep java > /opt/FeerBoxClient/FeerBoxClient/feerbox2.pid

#Sound config
amixer set PCM 100%
amixer cset numid=3 1