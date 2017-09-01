#!/usr/bin/env python
#-*- coding: utf-8 -*-
import time
import sys
import sqlite3
import Adafruit_DHT

# configuration variables, sensors
CVAR_PIN = 25  
CVAR_SENSOR = 2302                       
CVAR_PULL_FRECUENCE = 60                 # time given pull temp and humidity from sensor in SECONDS

# configuration variables, logging
CVAR_DEBUG = True                           # enables global debug mode
CVAR_DPRINT = True                          # prints debug information on standard error
CVAR_LOGFILE = "/opt/FeerBoxClient/FeerBoxClient/logs/temhum.log"                  # location of the log file

dbgfile = open(CVAR_LOGFILE,"w")
outfile = open(CVAR_OUTPUT,"a")
db = sqlite3.connect('/opt/FeerBoxClient/FeerBoxClient/db/feerboxclient.db')
cursor = db.cursor()

def stamp():
    return time.strftime("%b %d %Y %H:%M:%S", time.localtime())

def dbg(msg):
    if not CVAR_DEBUG:
        return
    if CVAR_DPRINT:
        sys.stderr.write(stamp() + ": " + msg + "\n")
    dbgfile.write(stamp() + ": " + msg + "\n")
    dbgfile.flush()

def register(temperature, humidity):
    global counter
    st=stamp()
    #outfile.write("%s,%s\n" % (st,dire))
    #cursor.execute('''INSERT INTO CounterPeople(time, reference, type, distance, upload)  VALUES(STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW', 'localtime'),?,?,?,?)''', (reference,'LASER', dire,0))
    #db.commit()
    if CVAR_DEBUG:
        dbg("temperature %d and humidity %d" % (temperature,humidity))
    elif CVAR_OPRINT:
        sys.stderr.write("%s: temperature %d and humidity %d\n" % (stamp(),temperature,humidity))

def destroy():
    dbg("Terminating.")
    sys.exit(0)

def check(sensor, pin):
    humidity, temperature = Adafruit_DHT.read_retry(sensor, pin)
    register(humidity, temperature)

def getReferenceFeerBox():
    global reference
    myvars = {}
    with open("/opt/FeerBoxClient/FeerBoxClient/target/classes/config.properties") as myfile:
        for line in myfile:
            name, var = line.partition("=")[::2]
            myvars[name.strip()] = var.strip()
    reference = myvars["reference"]

    
def main():
    global counter,pool, reference
    dbg("Initialising.")
    getReferenceFeerBox();
    dbg("Reference:" + reference)
    dbg("Ready.")
    while True:
        try:
            check(CVAR_SENSOR,CVAR_PIN)
            time.sleep(CVAR_PULL_FRECUENCE)
        except KeyboardInterrupt:
            destroy()

main()
