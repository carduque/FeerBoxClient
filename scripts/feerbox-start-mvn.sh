#!/bin/bash
sudo echo ds1307 0x68 > /sys/class/i2c-adapter/i2c-1/new_device
sudo hwclock -s
cd /opt/FeerBoxClient/FeerBoxClient/
sudo mvn exec:java -Dsun.security.smartcardio.library=/usr/lib/arm-linux-gnueabihf/libpcsclite.so.1 -Dexec.mainClass="com.feerbox.client.StartFeerBoxClient" &
disown
sudo echo $! > /opt/FeerBoxClient/FeerBoxClient/feerbox.pid
sudo pgrep java > /opt/FeerBoxClient/FeerBoxClient/feerbox2.pid