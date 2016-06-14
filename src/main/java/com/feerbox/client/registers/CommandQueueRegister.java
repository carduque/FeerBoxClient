package com.feerbox.client.registers;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import com.feerbox.client.model.Command;
import com.feerbox.client.services.CommandService;

public class CommandQueueRegister extends Thread {
	final static Logger logger = Logger.getLogger(CommandQueueRegister.class);

	public CommandQueueRegister() {
	}

	@Override
	public void run() {
		//Check if there is commands on queue from server
		List<Command> commands = CommandService.getServerCommands(ClientRegister.getInstance().getReference());
		
		//store commands locally
		if(commands!=null) {
			for(Command command:commands){
				CommandService.save(command);
			}
		}
		
		//Check if there are commands finished to send output
		commands = CommandService.getCommandsToUpload();
		if(commands!=null) {
			for(Command command:commands){
				boolean ok = CommandService.saveServer(command);
				if(ok){
					CommandService.upload(command);
				}
			}
		}
	}

}
