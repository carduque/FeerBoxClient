package com.feerbox.client.registers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

public class MACDetection {
	final static Logger logger = Logger.getLogger(MACDetection.class);

	public void execute() {
		String wifiInterface = ClientRegister.getInstance().getWifiInterface();
		ProcessBuilder pb = new ProcessBuilder("/usr/bin/python", "prepareIface.py", wifiInterface);
		pb.directory(new File("/opt/FeerBoxClient/FeerBoxClient/scripts/macdetection"));
		try {
			Process process = pb.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			StringBuilder builder = new StringBuilder();
			String line = null;
			while ( (line = reader.readLine()) != null) {
			   builder.append(line);
			   builder.append(System.getProperty("line.separator"));
			}
			String output = builder.toString();
			if(output!=null && output.startsWith("New monitor interface")){
				logger.debug(output);
				ProcessBuilder pb2 = new ProcessBuilder("/bin/bash", "sniffMacs.sh", "mon"+wifiInterface.substring(wifiInterface.length() - 1)+" "+ClientRegister.getInstance().getReference());
				pb2.directory(new File("/opt/FeerBoxClient/FeerBoxClient/scripts/macdetection"));
				pb2.start();
				logger.debug("mac detection enabled in background. Check sqlite3 /opt/pi4j/examples/feerbox2.db \"select * from MACS;\"");
			}
			else{
				logger.error(output);
			}
		}catch (IOException e) {
			logger.error("IOException", e);
		}
	}

}
