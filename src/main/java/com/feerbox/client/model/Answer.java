package com.feerbox.client.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Answer {
	private int Id;
	private Date time;
	private int button;
	private boolean upload;
	private String reference;
	
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public int getButton() {
		return button;
	}
	public void setButton(int button) {
		this.button = button;
	}
	public boolean isUpload() {
		return upload;
	}
	public void setUpload(boolean upload) {
		this.upload = upload;
	}
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public String getTimeText() {
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
		return df.format(this.time);
	}
	public String getTimeFormatted() {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
		df.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
		return df.format(time);
	}
	

}
