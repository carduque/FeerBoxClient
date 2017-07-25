#!/usr/bin/env python
#-*- coding: utf-8 -*-
import time
import sys
import sqlite3

db = sqlite3.connect('/opt/FeerBoxClient/FeerBoxClient/db/feerboxclient.db')

def main():
    print("Starting cleaning counterpeople")
    cursor = db.cursor()
    del_cursor = db.cursor();
    
    cursor.execute("select strftime('%S', time) as sec, id, strftime('%j:%H:%M', time) as day_hour_minute from counterpeople order by id asc")
    first = cursor.fetchone()
    for row in cursor:
        if first[2]==row[2]:
            if first[0]==row[0]:
                del_cursor.execute("delete from counterpeople where id="+str(row[1]))
                #print("delete from counterpeople where id="+str(row[1]))
        else:
             first = row
    cursor.close()
    del_cursor.close()
    db.commit()
    db.close()
    print("Cleaning finished")
    sys.exit(0)

main()
