package com.feerbox.client.registers;

import java.util.List;

import org.apache.log4j.Logger;

import com.feerbox.client.model.Command;
import com.feerbox.client.services.CommandService;

public class CommandRegister implements Runnable {
	final static Logger logger = Logger.getLogger(CommandRegister.class);

	@Override
	public void run() {
		//Check if there is commands on queue from server
		List<Command> commands = CommandService.getServerCommands(ClientRegister.getInstance().getReference());
		
		//store commands locally
		if(commands!=null) {
			CommandService.save(commands);
		}
		
		//Execute commands enqueued
		Command command = CommandService.readNext();
		CommandService.startExecution(command);
		String output = CommandService.execute(command);
		CommandService.finishExecution(command);
		CommandService.saveOutput(command, output);

	}

}
