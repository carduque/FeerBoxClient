#!/bin/bash
 cat /opt/FeerBoxClient/FeerBoxClient/logs/feerbox-client.log | grep -i $1 -C 2
