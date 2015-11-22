package com.feerbox.client.registers;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import com.feerbox.client.db.SaveAnswer;

public class IPRegister implements Runnable {
	private static final String FEERBOX_SERVER_URL = "http://feerbox.herokuapp.com/";

	public void run() {
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
			    NetworkInterface iface = interfaces.nextElement();
			    // filters out 127.0.0.1 and inactive interfaces
			    if (iface.isLoopback() || !iface.isUp())
			        continue;

			    Enumeration<InetAddress> addresses = iface.getInetAddresses();
			    while(addresses.hasMoreElements()) {
			        InetAddress addr = addresses.nextElement();
			        String ip = addr.getHostAddress();
			        SaveAnswer.saveIP(iface.getName(), ip);
			        Thread.sleep(1000);
			    }
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
