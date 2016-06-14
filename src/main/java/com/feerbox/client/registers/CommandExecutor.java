package com.feerbox.client.registers;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import com.feerbox.client.model.Command;
import com.feerbox.client.services.CommandService;

public class CommandExecutor implements Runnable {
	final static Logger logger = Logger.getLogger(CommandExecutor.class);

	public CommandExecutor() {
	}

	@Override
	public void run() {
		if(!CommandService.isCommandInExecution()){
			//Execute commands enqueued
			Command command = CommandService.startNextExecution();
			List<String> commandParameters = command.getParameters();
			commandParameters.add(0, command.getCommand());
			ProcessBuilder pb = new ProcessBuilder(commandParameters);
			/*Map<String, String> env = pb.environment();
			env.put("VAR1", "myValue");
			env.remove("OTHERVAR");
			env.put("VAR2", env.get("VAR1") + "suffix");*/
			pb.directory(new File("/opt/FeerBoxClient/FeerBoxClient/scripts"));
			try {
				Process p = pb.start();
			} catch (IOException e) {
				logger.error("IOException", e);
			}
			
			//CommandService.finishExecution(command);
			//CommandService.saveOutput(command, output);
		} else{
			if(CommandService.forceCleanQueue()){
				//In case commands are hang
				CommandService.cleanQueue();
			}
			if(CommandService.forceRestart()){
				//In case commands are hang
				CommandService.restart();
			}
		}
	}

}
