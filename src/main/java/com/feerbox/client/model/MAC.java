package com.feerbox.client.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class MAC {
	private long id;
	private Date time;
	private String mac;
	private int request;
	private String feerBoxReference;
	private boolean upload;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public int getRequest() {
		return request;
	}
	public void setRequest(int request) {
		this.request = request;
	}
	public String getFeerBoxReference() {
		return feerBoxReference;
	}
	public void setFeerBoxReference(String reference) {
		this.feerBoxReference = reference;
	}
	public boolean getUpload() {
		return upload;
	}
	public void setUpload(boolean upload) {
		this.upload = upload;
	}
	
	public String getTimeFormatted() {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		df.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
		return df.format(time);
	}
	
}
