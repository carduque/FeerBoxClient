#!/usr/bin/python
# Enable wlan interface in monitor mode

from __future__ import print_function
import subprocess
import traceback
import sys

########################################################################
## PARAMS
verbose = True #True for stdout output, False for logfile output
logFile = '/var/log/prepareIface.log'
wlanIface = "wlan1" #If not set, script will prompt for one
########################################################################

def showMessage(messageStr):
    #Print messages to stdout or file
	global verbose, logFile
	if verbose:
		print("%s" %(messageStr))
	else:
		f=open(logFile, 'a')
		print("%s" %(messageStr), file=f)
	return


def enableMonitorMode(wlanIface):
    #Enable monitor mode in given wlan interface
    try:
        wlanIndex = wlanIface[-1]
        phyIface = "phy0" #+ wlanIndex
        monIface="mon" + wlanIndex
        
        #1. Take wlan interface down 
        ifdownOut = subprocess.call("ifconfig " + wlanIface + " down", shell=True)
        if ifdownOut != 0 :
            showMessage("Could not take " + wlanIface + " interface down.")
        
        #2. Add monitoring interface
        addMonCmd = "iw phy " + phyIface + " interface add " + monIface +  " type monitor" 
        addMonOut = subprocess.call(addMonCmd, shell=True)
        if addMonOut != 0 :
            showMessage("Could not add " + phyIface + " monitor interface.")

        #3. (OPTIONAL) Delete managed wlan interface  
        delWlanOut = subprocess.call("iw dev " + wlanIface + " del", shell=True)
        if delWlanOut != 0 :
            showMessage("Could not delete " + wlanIface + " interface.")

        #4. Take monitor interface up 
        ifupOut = subprocess.call("ifconfig " + monIface + " up", shell=True)
        if ifupOut != 0 :
            showMessage("Could not take " + wlanIface + " interface down.")

        showMessage("New monitor interface " + monIface + " enabled in mode monitor.")

    except Exception:
        showMessage(traceback.format_exc())
        showMessage("Give wireless interface could not be enabled in monitor mode. (HINTS: right chipset? not using sudo?)")


#Main  
if wlanIface is "" :
    #If the wlan interface is not added as a parameter, it prompts for one
    wlanIface=raw_input("Enter the interface name to be changed to monitor mode: ")

enableMonitorMode(wlanIface)
