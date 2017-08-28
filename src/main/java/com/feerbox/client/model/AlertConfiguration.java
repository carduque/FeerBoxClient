package com.feerbox.client.model;

import java.util.AbstractMap;
import java.util.List;

public class AlertConfiguration {
	public static enum TypeAlertConfiguration {ANSWERS,COUNTERPEOPLE,RASPBIAN,CLIENT;}
	private int id;
	private List<AlertTimeTable> alertTimeTables;
	private List<AlertThreshold> alertThresholds;
	private TypeAlertConfiguration type;
	private String name;
	private boolean active;
	private AbstractMap<AlertOS.AlertOSType, AlertOS> alertOS;
	
	public List<AlertTimeTable> getAlertTimeTables() {
		return alertTimeTables;
	}
	public void setAlertTimeTables(List<AlertTimeTable> timeTables) {
		this.alertTimeTables = timeTables;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List<AlertThreshold> getAlertThresholds() {
		return alertThresholds;
	}
	public void setAlertThresholds(List<AlertThreshold> alertThresholds) {
		this.alertThresholds = alertThresholds;
	}
	public TypeAlertConfiguration getType() {
		return type;
	}
	public void setType(TypeAlertConfiguration type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean getActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public AbstractMap<AlertOS.AlertOSType, AlertOS> getAlertOS() {
		return alertOS;
	}
	public void setAlertOS(AbstractMap<AlertOS.AlertOSType, AlertOS> alertOS) {
		this.alertOS = alertOS;
	}
}
