package com.feerbox.client.model;

import java.sql.Timestamp;

public class CleaningService {
	private String cleanerReference;
	private Timestamp time;
	private String feerboxReference;
	
	public String getCleanerReference() {
		return cleanerReference;
	}
	public void setCleanerReference(String cleanerReference) {
		this.cleanerReference = cleanerReference;
	}
	public Timestamp getTime() {
		return time;
	}
	public void setTime(Timestamp time) {
		this.time = time;
	}
	public String getFeerboxReference() {
		return feerboxReference;
	}
	public void setFeerboxReference(String feerboxReference) {
		this.feerboxReference = feerboxReference;
	}
	
}
