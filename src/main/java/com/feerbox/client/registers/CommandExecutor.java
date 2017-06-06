package com.feerbox.client.registers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import org.apache.log4j.Logger;

import com.feerbox.client.db.SaveCommand;
import com.feerbox.client.model.Command;
import com.feerbox.client.services.CommandService;

public class CommandExecutor implements Runnable {
	final static Logger logger = Logger.getLogger(CommandExecutor.class);

	public CommandExecutor() {
	}

	@Override
	public void run() {
		try {
			//logger.debug("Command Executor");
			if(!CommandService.isCommandInExecution()){
				//logger.debug("No commands under execution");
				//Execute commands enqueued
				Command command = CommandService.startNextExecution();
				//logger.debug("Command: "+command);
				if(command!=null){
					logger.debug("Going to execute a command: "+command.getCommand()+" "+command.getParameter());
					ClientRegister.getInstance().setLastExecuteCommand(new Date());
					SaveCommand.startExecution(command);
					//List<String> commandParameters = command.getParameters();
					//commandParameters.add(0, command.getCommand());
					ProcessBuilder pb = new ProcessBuilder("/bin/bash", command.getCommand(), command.getParameter());
					/*Map<String, String> env = pb.environment();
					env.put("VAR1", "myValue");
					env.remove("OTHERVAR");
					env.put("VAR2", env.get("VAR1") + "suffix");*/
					pb.directory(new File("/opt/FeerBoxClient/FeerBoxClient/scripts"));
					try {
						Process process = pb.start();
						BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
						StringBuilder builder = new StringBuilder();
						String line = null;
						while ( (line = reader.readLine()) != null) {
						   builder.append(line);
						   builder.append(System.getProperty("line.separator"));
						}
						command.setOutput(builder.toString());
						logger.debug("Command executed succesfully: "+command.getCommand());
						SaveCommand.saveFinishExecution(command);
						if(command.getRestart()){
							logger.debug("Going to restart");
							restart();
						}
					} catch (IOException e) {
						logger.error("IOException", e);
					}
				}
				else{
					//logger.debug("There is no command to be executed");
				}
			} else{
				if(CommandService.forceCleanQueue()){
					//In case commands are hang
					CommandService.cleanQueue();
				}
				if(CommandService.forceRestart()){
					//In case commands are hang
					try {
						restart();
					} catch (IOException e) {
						logger.error("IOException", e);
					}
				}
			}
		} catch (Exception e) {
			logger.error("Exception at CommandExecutor");
		}
	}

	private void restart() throws IOException {
		ProcessBuilder reboot = new ProcessBuilder("/bin/bash", "restart.sh");
		reboot.directory(new File("/opt/FeerBoxClient/FeerBoxClient/scripts"));
		logger.info("System is going to restart");
		reboot.start();
	}

}
