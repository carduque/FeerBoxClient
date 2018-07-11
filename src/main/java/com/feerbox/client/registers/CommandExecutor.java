package com.feerbox.client.registers;

import org.apache.log4j.Logger;

import com.feerbox.client.model.Command;
import com.feerbox.client.oslevel.OSExecutor;
import com.feerbox.client.services.CommandService;

public class CommandExecutor extends Register {
	final static Logger logger = Logger.getLogger(CommandExecutor.class);
	
	public CommandExecutor(OSExecutor oSExecutor) {
		super(oSExecutor);
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
				executeCommand(command);
			} else{
				logger.info("Command is under execution");
				if(CommandService.forceCleanQueue()){
					//In case commands are hang
					CommandService.cleanQueue();
				}
				if(CommandService.forceRestart()){
					//In case commands are hang
					this.oSExecutor.restart();
				}
			}
		} catch (Exception e) {
			logger.error("Exception at CommandExecutor",e);
		}
	}

	

	public String executeCommand(Command command) {
		return this.oSExecutor.executeCommand(command);
	}

}
