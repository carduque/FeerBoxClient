#!/bin/bash
now=$(date +"%m_%d_%Y")
test -d "/opt/FeerBoxClient/FeerBoxClient/config" || sudo mkdir -p "/opt/FeerBoxClient/FeerBoxClient/config" && sudo cp -rf /opt/FeerBoxClient/FeerBoxClient/target/classes/config.properties /opt/FeerBoxClient/FeerBoxClient/config/config_$now.properties
cd /opt/FeerBoxClient/FeerBoxClient
sudo git fetch origin
sudo git reset --hard origin/master
if [ -z "$1" ]
	then
		sudo mvn clean install -Dmaven.test.skip=true
	else
		sudo mvn clean install -Dmaven.test.skip=true -Dparam=$1
fi
sudo chmod -R 777 /opt/FeerBoxClient/FeerBoxClient/scripts
sudo cp -rf /opt/FeerBoxClient/FeerBoxClient/config/config_$now.properties /opt/FeerBoxClient/FeerBoxClient/target/classes/config.properties
if [ -d "/opt/FeerBoxClient/feerbox-admin-web" ]; then
	#feerbox-admin-web alredy installed
	wget -q https://raw.githubusercontent.com/carduque/raspap-webgui/master/installers/raspbian-update.sh -O /tmp/raspap && bash /tmp/raspap
else
	#feerbox-admin-web to be installed
	wget -q https://raw.githubusercontent.com/carduque/raspap-webgui/master/installers/raspbian.sh -O /tmp/raspap && bash /tmp/raspap
fi