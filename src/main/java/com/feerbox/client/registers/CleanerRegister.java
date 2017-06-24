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
		try {
			logger.debug("Going to check remote cleaner updates");
			//Check if there is commands on queue from server
			List<Cleaner> cleaners = CleanerService.getPendingUpdates(ClientRegister.getInstance().getReference());
			
			//store commands locally
			if(cleaners!=null) {
				int companyId = 0;
				logger.debug("Cleaners recieved: "+cleaners.size());
				for(Cleaner cleaner:cleaners){
					CleanerService.saveOrUpdate(cleaner);
					companyId = cleaner.getCompany();
				}
				if(companyId!=0){
					int removed = CleanerService.removeOtherCompanies(companyId);
					logger.debug(removed+" cleaners removed from other companies than "+companyId);
				}
			}
			else{
				logger.debug("No cleaners to update");
			}
		} catch (Exception e) {
			logger.error("Exception at CleanerRegister");
		}
		
	}

}
