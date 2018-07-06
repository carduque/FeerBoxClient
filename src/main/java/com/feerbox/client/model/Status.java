package com.feerbox.client.model;

import java.util.Date;
import java.util.Map;

public class Status {
	private String reference;
	private Map<String, String> info;
	public enum infoKeys {INTERNET,LAST_ANSWER,TIME_UP, SYSTEM_TIME, SW_VERSION, IP, CommandExecutor, CommandQueue, PendingAnswersToUpload, CPU, MemoryProcess, 
		JavaMemory, FreeMemory, AverageUptime, PendingCPToUpload, PendingWeatherToUpload, LAST_CP, LAST_WEATHER, FreeDiskSpace, SSID_CONNECTED, OS_KERNEL_Version};
	private int upload;
	private Date time;
	
	public Status(String reference) {
		super();
		this.reference=reference;
	}
	public Status() {
		super();
	}
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public Map<String, String> getInfo() {
		return info;
	}
	public void setInfo(Map<String, String> info) {
		this.info = info;
	}
	public int getUpload() {
		return upload;
	}
	public void setUpload(int upload) {
		this.upload = upload;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	

}
