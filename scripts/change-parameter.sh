#!/bin/bash
PARAMETER=$1
VALUE=$2

sed -i 's:^[ \t]*${PARAMETER}[ \t]*=\([ \t]*.*\)$:${PARAMETER} = '${VALUE}':' /opt/FeerBoxClient/FeerBoxClient/target/classes/config.properties