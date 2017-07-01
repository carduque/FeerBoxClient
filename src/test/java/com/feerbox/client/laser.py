#!/usr/bin/env python
import time

def dbg(msg):
    dbgfile.write(stamp() + ": " + msg + "\n")
    
def stamp():
    return time.strftime("%b %d %Y %H:%M:%S", time.localtime())

CVAR_LOGFILE = "/opt/FeerBoxClient/FeerBoxClient/logs/lasercounter.log"
dbgfile = open(CVAR_LOGFILE,"w")

def main():
    dbg("Ready.")
    
main()