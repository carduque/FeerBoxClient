package com.feerbox.client.registers;

import java.io.File;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.feerbox.client.model.Command;
import com.feerbox.client.services.CommandService;

public class RestartEveryDayRegister extends TimerTask {
	final static Logger logger = Logger.getLogger(RestartEveryDayRegister.class);
	private Timer timer;
	
	
	@Override
	public void run() {
		try {
			ProcessBuilder reboot = new ProcessBuilder("/bin/bash", "restart.sh");
			reboot.directory(new File("/opt/FeerBoxClient/FeerBoxClient/scripts"));
			logger.info("Scheduled restarted as planned");
			reboot.start();
			
		} catch (Throwable  t) {
			logger.error("Error in RestartEveryDayRegister");
		}
	}


	public Timer getTimer() {
		return timer;
	}
	public void setTimer(Timer timer) {
		this.timer = timer;
	}
	

}
