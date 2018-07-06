package com.feerbox.client.services;

import java.util.Date;

import com.feerbox.client.db.ReadStatus;
import com.feerbox.client.db.SaveStatus;
import com.feerbox.client.model.Status;

public class StatusService{

	public static void save(Status status) {
		SaveStatus.save(status);
	}

	public static Date getLastStatusTime() {
		return ReadStatus.getLastStatusTime();
	}
	
}
