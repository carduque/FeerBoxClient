#!/bin/bash
OPTS=`getopt -o wc: --long webadmin,config: -n 'parse-options' -- "$@"`
if [ $? != 0 ] ; then echo "Failed parsing options." >&2 ; exit 1 ; fi
CONFIG="default"
DEPLOY_WEB_ADMIN=false
eval set -- "$OPTS"
while true; do
  case "$1" in
    -c | --config ) CONFIG=$2; shift ;;
    -w | --webadmin ) DEPLOY_WEB_ADMIN=true; shift ;;
    -- ) shift; break ;;
    * ) break ;;
  esac
done

now=$(date +"%m_%d_%Y")
test -d "/opt/FeerBoxClient/FeerBoxClient/config" || sudo mkdir -p "/opt/FeerBoxClient/FeerBoxClient/config" && sudo cp -rf /opt/FeerBoxClient/FeerBoxClient/target/classes/config.properties /opt/FeerBoxClient/FeerBoxClient/config/config_$now.properties
cd /opt/FeerBoxClient/FeerBoxClient
sudo git fetch origin
sudo git reset --hard origin/master
if [ "$CONFIG" = "default" ]
	then
		sudo mvn clean install -Dmaven.test.skip=true
	else
		sudo mvn clean install -Dmaven.test.skip=true -Dparam=$CONFIG
fi
sudo chmod -R 777 /opt/FeerBoxClient/FeerBoxClient/scripts
sudo cp -rf /opt/FeerBoxClient/FeerBoxClient/config/config_$now.properties /opt/FeerBoxClient/FeerBoxClient/target/classes/config.properties
if  $DEPLOY_WEB_ADMIN; then
	if [ -d "/opt/FeerBoxClient/feerbox-admin-web" ]; then
		#feerbox-admin-web alredy installed
		wget -q https://raw.githubusercontent.com/carduque/raspap-webgui/master/installers/raspbian-update.sh -O /tmp/raspap && bash /tmp/raspap
	else
		#feerbox-admin-web to be installed
		wget -q https://raw.githubusercontent.com/carduque/raspap-webgui/master/installers/raspbian.sh -O /tmp/raspap && bash /tmp/raspap
	fi
fi
