package com.feerbox.client;

import java.util.List;

import org.junit.Test;

import com.feerbox.client.model.Cleaner;
import com.feerbox.client.services.CleanerService;

public class CleanerServiceTest {
	@Test
	public void getPendingCleaners(){
		List<Cleaner> cleaners = CleanerService.getPendingUpdates("2016007");
		for(Cleaner cleaner:cleaners){
			System.out.println(cleaner.getName());
		}
		
	}
}
