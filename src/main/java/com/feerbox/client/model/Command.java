package com.feerbox.client.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
	private Date serverCreationTime;
	private boolean restart;
	private String parameter;
	private int retryCounter;
	
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
		if(startTime!=null){
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			df.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
			return df.format(startTime);
		}
		return null;
	}
	public String getFinishTimeFormatted() {
		if(finishTime!=null){
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			df.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
			return df.format(finishTime);
		}
		return null;
	}
	public int getServerId() {
		return serverId;
	}
	public void setServerId(int serverId) {
		this.serverId = serverId;
	}
	public Date getServerCreationTime() {
		return serverCreationTime;
	}
	public void setServerCreationTime(Date serverCreationTime) {
		this.serverCreationTime = serverCreationTime;
	}
	public String getServerCreationTimeFormatted(){
		if(serverCreationTime!=null){
			//dd-MMM-yyyy HH:mm:ss.SSS
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			return df.format(this.serverCreationTime);
		}
		return null;
	}
	public boolean getRestart() {
		return restart;
	}
	public void setRestart(boolean restart) {
		this.restart = restart;
	}
	public String getParameter() {
		return parameter;
	}
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
	public int getRetryCounter() {
		return retryCounter;
	}
	public void setRetryCounter(int retryCounter) {
		this.retryCounter = retryCounter;
	}
	@Override
	public String toString() {
		return "Command [id=" + id + ", command=" + command + ", output=" + output + ", time=" + time + ", startTime="
				+ startTime + ", finishTime=" + finishTime + ", upload=" + upload + ", serverId=" + serverId
				+ ", serverCreationTime=" + serverCreationTime + ", restart=" + restart + ", parameter=" + parameter
				+ ", retryCounter=" + retryCounter + "]";
	}
	
	
}
