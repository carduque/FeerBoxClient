#!/bin/bash
sudo git fetch origin
sudo git reset --hard origin/master

sudo mvn clean install -Dmaven.test.skip=true
