#!/bin/bash
# Grabs and kill a process from the pidlist that has the word myapp

cd /opt/FeerBoxClient/FeerBoxClient/target/classes/
sudo kill -9 $(ps aux | grep 'com.feerbox.client.StartFeerBoxClient' | awk '{print $2}')