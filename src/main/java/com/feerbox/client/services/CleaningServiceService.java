package com.feerbox.client.services;

import java.sql.Timestamp;
import java.util.Date;

import com.feerbox.client.db.SaveCleaningService;
import com.feerbox.client.model.CleaningService;
import com.feerbox.client.registers.ClientRegister;

public class CleaningServiceService {
	public static Integer save(String cleanerReference) {
		Integer id = null;
		CleaningService cleaningService = new CleaningService();
		cleaningService.setCleanerReference(cleanerReference);
		cleaningService.setFeerboxReference(ClientRegister.getInstance().getReference());
		cleaningService.setTime(new Timestamp(new Date().getTime()));
		id = SaveCleaningService.save(cleaningService);
		if(id==0) return null;		
		return id;
	}
}
