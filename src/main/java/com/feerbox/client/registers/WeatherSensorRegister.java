package com.feerbox.client.registers;

import java.io.IOException;

import org.apache.log4j.Logger;

public class WeatherSensorRegister {
	final static Logger logger = Logger.getLogger(WeatherSensorRegister.class);

	public void start() {
		ProcessBuilder pb = new ProcessBuilder("/bin/bash", "/opt/FeerBoxClient/FeerBoxClient/scripts/temperatura-humidity/temphum.sh");
		//pb.directory(new File("/opt/FeerBoxClient/FeerBoxClient/scripts/countpeople"));
		//executeCommandLine("sudo python /opt/FeerBoxClient/FeerBoxClient/scripts/countpeople/laser_count.py");
		try {
			Process process = pb.inheritIO().start();
			logger.debug("Weather sensor enabled");
		} catch (IOException e) {
			logger.error("Weather sensor:"+ e.getMessage());
		}
	}
	
}
