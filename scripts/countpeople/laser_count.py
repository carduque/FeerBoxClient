#!/usr/bin/env python
#-*- coding: utf-8 -*-
import RPi.GPIO as GPIO
import time
import sys

# constants for utility, don't edit
CT_ANALOG = GPIO.PUD_DOWN
CT_DIGITAL = GPIO.PUD_OFF

# configuration variables, sensors
CVAR_LDR1_PIN = 12                          # BCM pin where LDR1 sensor is located
CVAR_LDR1_TYPE = CT_ANALOG                  # LDR1 sensor signal type (CT_ANALOG or CT_DIGITAL)
CVAR_LDR2_AVAILABLE = False                  # set to True if this is a dual sensor configuration
CVAR_LDR2_PIN = 27                          # BCM pin where LDR2 sensor is located, if any. Safe to ignore if LDR2_AVAILABLE is false.
CVAR_LDR2_TYPE = CT_ANALOG                  # LDR2 sensor signal type (CT_ANALOG or CT_DIGITAL)
CVAR_SENSOR_THRESHOLD = 0.1 if CVAR_LDR2_AVAILABLE else 0.2
CVAR_PURGE_THRESHOLD = 10.0                 # time given to person to walk before his entry is purged

# configuration variables, logging
CVAR_DEBUG = True                           # enables global debug mode
CVAR_DPRINT = True                          # prints debug information on standard error
CVAR_LOGFILE = "debug.log"                  # location of the log file

# configuration variables, output
CVAR_OUTPUT = "output.csv"                  # output file
CVAR_OPRINT = True                          # prints counter information on screen

dbgfile = open(CVAR_LOGFILE,"a")
outfile = open(CVAR_OUTPUT,"a")

def GPIO_init():
    GPIO.setmode(GPIO.BCM)
    GPIO.setup(CVAR_LDR1_PIN, GPIO.IN, pull_up_down=CVAR_LDR1_TYPE)
    if (CVAR_LDR2_AVAILABLE):
        GPIO.setup(CVAR_LDR2_PIN, GPIO.IN, pull_up_down=CVAR_LDR2_TYPE)

def stamp():
    return time.strftime("%b %d %Y %H:%M:%S", time.localtime())

def dbg(msg):
    if not CVAR_DEBUG:
        return
    if CVAR_DPRINT:
        sys.stderr.write(stamp() + ": " + msg + "\n")
    dbgfile.write(stamp() + ": " + msg + "\n")

def register(dire):
    global counter
    st=stamp()
    outfile.write("%s,%s\n" % (st,dire))
    if CVAR_DEBUG:
        dbg("counter increment with direction %d (count %d)" % (dire,counter))
    elif CVAR_OPRINT:
        sys.stderr.write("%s: detected movement%s (count %d)\n" % (stamp(),checkdir(dire),counter))

def destroy():
    dbg("Terminating.")
    GPIO.cleanup()
    sys.exit(0)

def check(states,pin,sensor):
    global counter,pool
    cstate = GPIO.input(pin)
    if (cstate!=states[sensor]):
            dbg("sensor %d state changed. curState=%d, lastState=%d" % (sensor+1,cstate,states[sensor]))
            states[sensor]=cstate
            if (cstate==1 and not CVAR_LDR2_AVAILABLE):
                counter=counter+1
                register(0)
            elif (cstate==1):
                if (pool!=None):
                    # pair with pool entry
                    (sen,st) = pool
                    if (sensor!=sen and (time.time()-st)<CVAR_PURGE_THRESHOLD):
                        counter=counter+1
                        register(sensor-sen)
                        pool=None
                    elif (time.time()-st>=CVAR_PURGE_THRESHOLD):
                        pool=(sensor,time.time()) # send to pool
                    else:
                        # same sensor blinking twice, false positive. Ignore
                        pass     
                else:
                    # send to pool
                    pool=(sensor,time.time())

def selftest():
    global CVAR_LDR2_AVAILABLE, CVAR_LDR1_PIN, CVAR_LDR2_PIN
    dbg("Performing sensor test.")
    ldr1=GPIO.input(CVAR_LDR1_PIN)
    dbg("> LDR1 raw value: " + str(ldr1))
    if (ldr1==0):
        dbg("WARNING: LDR1 not high. Check sensor connectivity, light conditions, and ensure beam is focusing sensor directly.")
        dbg("Sensor test failed. Continuing anyway.")
        return
    if (CVAR_LDR2_AVAILABLE):
        ldr2=GPIO.input(CVAR_LDR2_PIN)
        dbg("> LDR2 raw value: " + str(ldr2))
        if (ldr2==0):
            dbg("INFO: LDR2 enabled but not high. Disabling sensor 2.")
            dbg("If this is a mistake, check sensor connectivity, light conditions, and ensure beam is focusing sensor directly.")
            CVAR_LDR2_AVAILABLE=False
    dbg("End of sensor test.")

    
def main():
    global counter,pool
    dbg("Initialising.")
    GPIO_init()
    states = [GPIO.input(CVAR_LDR1_PIN)]
    if CVAR_LDR2_AVAILABLE:
        states.append(GPIO.input(CVAR_LDR2_PIN))
        pool=None
    counter = 0
    selftest()
    dbg("Ready.")
    while True:
        try:
            check(states,CVAR_LDR1_PIN,0)
            if CVAR_LDR2_AVAILABLE:
                check(states,CVAR_LDR2_PIN,1)
            time.sleep(CVAR_SENSOR_THRESHOLD)
        except KeyboardInterrupt:
            destroy()

if __name__=="__main__":
    main()
