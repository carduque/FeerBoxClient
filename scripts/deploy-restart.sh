#!/bin/bash
cd /opt/FeerBoxClient/FeerBoxClient/scripts
./feerbox-stop.sh
cd /opt/FeerBoxClient/FeerBoxClient/
sudo git fetch origin
sudo git reset --hard origin/master

sudo mvn clean install -Dmaven.test.skip=true
cd /opt/FeerBoxClient/FeerBoxClient/scripts
sudo chmod 777 *.sh
./feerbox-start.sh
