#!/usr/bin/python
# Sniff wireless packets via given interface in monitor mode

from __future__ import print_function
from scapy.all import *

########################################################################
## PARAMS
verbose = False #True for stdout output, False for logfile output
monIface = "mon1" #If not set, script will prompt for one
logFile = "/opt/FeerBoxClient/FeerBoxClient/logs/macs.log"
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

def PacketHandler(packet) :
    if packet.haslayer(Dot11) :
    
        #We filter by management requests packages
        # 0: Association Request
        # 2: Reassociation Request
        # 4: Probe Request
        # More info: http://www.wildpackets.com/resources/compendium/wireless_lan/wlan_packet_types        
        managementRequests = (0, 2, 4)
    
        if packet.type == 0 and packet.subtype in managementRequests :
            if packet.addr2 not in macAddrList :
                macAddrList.append(packet.addr2)
                showMessage("Device MAC: " + packet.addr2)

if monIface is "" :
    #If the wlan interface is not added as a parameter, it prompts for one
    monIface=raw_input("Enter the interface name in monitor mode: ")

showMessage("Starting sniffing on interface " + monIface)

macAddrList = []
sniff(iface=monIface, prn = PacketHandler, store=0)
