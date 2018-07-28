package com.feerbox.client;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class ListNetwork {

	public static void main(String[] args) {
		try {
			  InetAddress localhost = InetAddress.getLocalHost();
			  System.out.println(" IP Addr: " + localhost.getHostAddress());
			  // Just in case this host has multiple IP addresses....
			  InetAddress[] allMyIps = InetAddress.getAllByName(localhost.getCanonicalHostName());
			  if (allMyIps != null && allMyIps.length > 1) {
			    System.out.println(" Full list of IP addresses:");
			    for (int i = 0; i < allMyIps.length; i++) {
			      System.out.println("    " + allMyIps[i]);
			    }
			  }
			} catch (UnknownHostException e) {
			  System.out.println(" (error retrieving server host name)");
			}

			try {
			  System.out.println("Full list of Network Interfaces:");
			  for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
			    NetworkInterface intf = en.nextElement();
			    System.out.println("    " + intf.getName() + " " + intf.getDisplayName());
			    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
			    	InetAddress address = enumIpAddr.nextElement();
			      System.out.println("        " + address.toString());
			      System.out.println(address.getHostAddress().matches("\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b"));
			    }
			  }
			} catch (SocketException e) {
			  System.out.println(" (error retrieving network interface list)");
			}
	}

}
