package com.feerbox.client.registers;

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
			Command command = CommandService.readNext();
			CommandService.startExecution(command);
			String output = CommandService.execute(command);
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
