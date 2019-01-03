#!/usr/bin/env python
import os
import glob
import subprocess
import calendar
import time
import urllib2
import json
import datetime

#initialize
os.system('modprobe w1-gpio')
os.system('modprobe w1-therm')

#device
base_dir = '/sys/bus/w1/devices/'
device_folder = glob.glob(base_dir + '28*')[0]
device_file = device_folder + '/w1_slave'

# Opens raw device, code changed to reflect issue in Raspian
def read_temp_raw():
    catdata = subprocess.Popen(['cat',device_file], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    out,err = catdata.communicate()
    out_decode = out.decode('utf-8')
    lines = out_decode.split('\n')
    return lines

# Reads temperature, outputs farenhiet
def read_temp():
    lines = read_temp_raw()
    while lines[0].strip()[-3:] != 'YES':
        time.sleep(0.2)
        lines = read_temp_raw()
    equals_pos = lines[1].find('t=')
    if equals_pos != -1:
        temp_string = lines[1][equals_pos+2:]
        temp_c = float(temp_string) / 1000.0
        temp_f = temp_c * 9.0 / 5.0 + 32.0
        return temp_c

def getReferenceFeerBox():
    global reference
    myvars = {}
    with open("/opt/FeerBoxClient/FeerBoxClient/target/classes/config.properties") as myfile:
        for line in myfile:
            name, var = line.partition("=")[::2]
            myvars[name.strip()] = var.strip()
    reference = myvars["reference"]


dbgfile = open(CVAR_LOGFILE,"w")
db = sqlite3.connect('/opt/FeerBoxClient/FeerBoxClient/db/feerboxclient.db')
cursor = db.cursor()
global reference
while True:
        getReferenceFeerBox()
        postdata = {
        'date': str(datetime.datetime.now()),
        'temp': str(read_temp())
        }
        st=stamp()
        cursor.execute('''INSERT INTO WeatherSensor(time, reference, temperature, humidity, upload)  VALUES(STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW', 'localtime'),?,?,null,?)''', (reference,str(read_temp()),null,0))
        db.commit()
        ##print str(data);
        time.sleep(600)