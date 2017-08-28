package com.feerbox.client.model;

import java.util.Date;

public class Alert {
	public enum AlertSeverity { HIGH, MEDIUM, LOW, INFORMATIONAL;}
	public enum AlertGenerator { DB, LOG, CATCH_EXCEPTION, RASPBIAN;}
	public enum AlertType { NotEnoughDataBeenCollected,TooMuchDataBeenCollected, PoorUpTime, TuptimeNotInstalled;}
	
	private AlertSeverity severity;
	private AlertGenerator generator;
	private Long threshold;
	private String name;
	private String reference;
	private Date time;
	private AlertType type;
	private int weekday;
	
	public AlertSeverity getSeverity() {
		return severity;
	}
	public void setSeverity(AlertSeverity severity) {
		this.severity = severity;
	}
	public AlertGenerator getGenerator() {
		return generator;
	}
	public void setGenerator(AlertGenerator generator) {
		this.generator = generator;
	}
	public Long getThreshold() {
		return threshold;
	}
	public void setThreshold(Long threshold) {
		this.threshold = threshold;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public AlertType getType() {
		return type;
	}
	public void setType(AlertType type) {
		this.type = type;
	}
	public void setWeekday(int day_of_week) {
		this.weekday = day_of_week;
	}
	public int getWeekday() {
		return weekday;
	}

}
