#!/bin/bash
cp /opt/FeerBoxClient/FeerBoxClient/db/feerboxclient.db /opt/FeerBoxClient/FeerBoxClient/db/feerboxclient-original.db
sqlite3 /opt/FeerBoxClient/FeerBoxClient/db/feerboxclient.db "select count(*) from counterpeople where type='PIR';"

echo delete all data before 1.6.2017
sqlite3 /opt/FeerBoxClient/FeerBoxClient/db/feerboxclient.db "delete from counterpeople where time<date('2017/06/01');"

echo delete all data after now
sqlite3 /opt/FeerBoxClient/FeerBoxClient/db/feerboxclient.db "delete from counterpeople where time>date('now');"

-- keep one row for each second
(sudo python /opt/FeerBoxClient/FeerBoxClient/scripts/delete-counterpeople.py)&

-- sqlite3 /opt/FeerBoxClient/FeerBoxClient/db/feerboxclient.db "select count(*) from counterpeople where type='PIR';"

-- group by hour, by day

 
