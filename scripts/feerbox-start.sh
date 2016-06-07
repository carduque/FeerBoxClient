#!/bin/bash
echo ds1307 0x68 > /sys/class/i2c-adapter/i2c-1/new_device
sudo hwclock -s
cd /opt/FeerBoxClient/FeerBoxClient/target/classes/
sudo java -classpath .:classes:/opt/pi4j/lib/'*' com.feerbox.client.StartFeerBoxClient &>log.txt
disown
