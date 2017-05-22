#!/bin/bash
cd /opt/FeerBoxClient/FeerBoxClient/scripts
sudo git fetch origin
sudo git reset --hard origin/master
sudo chmod -R 777 /opt/FeerBoxClient/FeerBoxClient/scripts
