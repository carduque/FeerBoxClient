package com.feerbox.client.registers;

import java.util.List;

import org.apache.log4j.Logger;

import com.feerbox.client.model.Cleaner;
import com.feerbox.client.services.CleanerService;

public class CleanerRegister extends Thread {
	final static Logger logger = Logger.getLogger(CleanerRegister.class);

	public CleanerRegister() {
	}

	@Override
	public void run() {
		logger.debug("Going to check remote cleaner updates");
		//Check if there is commands on queue from server
		List<Cleaner> cleaners = CleanerService.getPendingUpdates(ClientRegister.getInstance().getReference());
		
		//store commands locally
		if(cleaners!=null) {
			for(Cleaner cleaner:cleaners){
				CleanerService.saveOrUpdate(cleaner);
			}
		}
		
	}

}
