package com.feerbox.client.registers;

import java.io.File;
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
			sendConfandLastLog();
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
	
	private static void sendConfandLastLog() {
		sendYesterdayLog();
		sendConfiguration();
	}
	
	private static void sendConfiguration() {
		Command command = new Command();
		command.setCommand("cat-config.sh");
		command.setRestart(false);
		command.setStartTime(new Date());
		command.setParameter("");
		command.setOutput(executeUnsoCommand(command));
		command.setFinishTime(new Date());
		sendUnsoCommand(command);
	}


	private static void sendYesterdayLog() {
		Command command = new Command();
		command.setCommand("cat-yesterday-log.sh");
		command.setRestart(false);
		command.setStartTime(new Date());
		command.setParameter("");
		command.setOutput(executeUnsoCommand(command));
		command.setFinishTime(new Date());
		if(command.getOutput()!=null && !command.getOutput().equals("")) sendUnsoCommand(command);
	}


	private static String executeUnsoCommand(Command command) {
		return CommandExecutor.executeCommand(command);
	}


	private static void sendUnsoCommand(Command command) {
		CommandService.sendUnsoCommand(command);
	}

}
