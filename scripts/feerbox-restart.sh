#!/bin/bash
# Grabs and kill a process from the pidlist that has the word myapp

echo "Going to kill FeerBoxClient process"
sudo kill -9 $(ps aux | grep 'com.feerbox.client.StartFeerBoxClient' | awk '{print $2}')
echo "Going to start FeerBoxClient"
cd /opt/FeerBoxClient/FeerBoxClient/target/classes/
sudo java -classpath .:classes:/opt/pi4j/lib/'*' com.feerbox.client.StartFeerBoxClient &