package com.feerbox.client.registers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.feerbox.client.db.SaveCommand;
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
		if(command!=null){
			logger.debug("Going to execute a command: "+command.getCommand()+" "+command.getParameter());
			ClientRegister.getInstance().setLastExecuteCommand(new Date());
			SaveCommand.startExecution(command);
			String output = this.oSExecutor.executeCommand(command);
			command.setOutput(output);
			logger.debug("Command executed succesfully: "+command.getCommand());
			if(command.getId()!=0) SaveCommand.saveFinishExecution(command);
			if(command.getRestart()){
				logger.debug("Going to restart");
				this.oSExecutor.restart();
			}
			return command.getOutput();
		}
		return null;
	}

}
