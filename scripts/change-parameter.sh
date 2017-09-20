#!/bin/bash
PARAMETER=$1
VALUE=$2

sudo sed -i 's:^[ \t]*'${PARAMETER}'[ \t]*=\([ \t]*.*\)$:'${PARAMETER}' = '${VALUE}':' /opt/FeerBoxClient/FeerBoxClient/target/classes/config.properties
cat /opt/FeerBoxClient/FeerBoxClient/target/classes/config.properties