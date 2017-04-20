package com.feerbox.client.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.feerbox.client.model.CounterPeople.Type;

public class CounterPeople {
	public static enum Type {PIR, DISTANCE_SENSOR};
	private long id;
	private Date time;
	private double distance;
	private String feerBoxReference;
	private boolean upload;
	private Type type;
	
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
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public Type getType() {
		return type;
	}
	
}
