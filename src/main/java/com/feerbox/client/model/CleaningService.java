package com.feerbox.client.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class CleaningService {
	private int id;
	private String cleanerReference;
	private Date time;
	private String feerboxReference;
	private boolean upload;
	
	public String getCleanerReference() {
		return cleanerReference;
	}
	public void setCleanerReference(String cleanerReference) {
		this.cleanerReference = cleanerReference;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public String getFeerboxReference() {
		return feerboxReference;
	}
	public void setFeerboxReference(String feerboxReference) {
		this.feerboxReference = feerboxReference;
	}
	public boolean getUpload() {
		return upload;
	}
	public void setUpload(boolean upload) {
		this.upload = upload;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTimeFormatted() {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		df.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
		return df.format(time);
	}
	
}
