package com.feerbox.client.registers;

import java.util.List;

import org.apache.log4j.Logger;

import com.feerbox.client.model.Command;
import com.feerbox.client.services.CommandService;

public class CommandQueueRegister extends Thread {
	final static Logger logger = Logger.getLogger(CommandQueueRegister.class);

	public CommandQueueRegister() {
	}

	@Override
	public void run() {
		try {
			logger.debug("Going to check remote commands active");
			//Check if there is commands on queue from server
			List<Command> commands = CommandService.getServerCommands(ClientRegister.getInstance().getReference());
			
			//store commands locally
			if(commands!=null) {
				for(Command command:commands){
					CommandService.save(command);
				}
			}
			else{
				logger.debug("No commands on server");
			}
			
		} catch (Exception e) {
			logger.error("Exception at CommandQueueRegister");
		}
	}

}
