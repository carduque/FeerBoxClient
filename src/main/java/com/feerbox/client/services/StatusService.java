package com.feerbox.client.services;

import com.feerbox.client.db.SaveStatus;
import com.feerbox.client.model.Status;

public class StatusService{

	public static void save(Status status) {
		SaveStatus.save(status);
	}
	
}
