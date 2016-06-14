package com.feerbox.client.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Command {
	private int id;
	private String command;
	private String output;
	private Date time;
	private Date startTime;
	private Date finishTime;
	private boolean upload;
	private int serverId;
	
	public String getOutput() {
		return output;
	}
	public void setOutput(String output) {
		this.output = output;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
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
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getFinishTime() {
		return finishTime;
	}
	public void setFinishTime(Date finishTime) {
		this.finishTime = finishTime;
	}
	public String getTimeFormatted() {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
		df.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
		return df.format(time);
	}
	public String getStartTimeFormatted() {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
		df.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
		return df.format(startTime);
	}
	public String getFinishTimeFormatted() {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
		df.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
		return df.format(finishTime);
	}
	public int getServerId() {
		return serverId;
	}
	public void setServerId(int serverId) {
		this.serverId = serverId;
	}
	public List<String> getParameters() {
		// TODO Auto-generated method stub
		return null;
	}

}
