#!/bin/bash
cd /opt/FeerBoxClient/FeerBoxClient/logs/
sudo rm -f "feerboxlogs-$(date +"%Y-%m-%d").zip"
zip "feerboxlogs-$(date +"%Y-%m-%d").zip" *.*
cd /var/log
sudo rm -f "syslogs-$(date +"%Y-%m-%d").tgz"
sudo find . -mtime -5 | sudo xargs tar --no-recursion -czf "syslogs-$(date +"%Y-%m-%d").tgz"