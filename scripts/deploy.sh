#!/bin/bash
cd /opt/FeerBoxClient/FeerBoxClient
sudo git fetch origin
sudo git reset --hard origin/master
if [ -z "$1" ]
	then
		sudo mvn clean install -Dmaven.test.skip=true
	else
		sudo mvn clean install -Dmaven.test.skip=true -Dparam=$1
fi
