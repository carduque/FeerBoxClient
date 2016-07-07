#!/bin/bash
eval expression=$1
#echo $expression
grep $expression /opt/FeerBoxClient/FeerBoxClient/logs/feerbox-client.log


# grep "2016-07-03 06:2[5-6]" feerbox-client.log
# grep "^$(date -d -1hour +'%Y-%m-%d %H')" /opt/FeerBoxClient/FeerBoxClient/logs/feerbox-client.log
