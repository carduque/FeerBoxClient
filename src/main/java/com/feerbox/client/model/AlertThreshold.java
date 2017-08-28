package com.feerbox.client.model;

public class AlertThreshold {
	public static enum TypeAlertThreshold {UPPER,BELOW;}
	private int weekDay;
	private long threshold;
	private TypeAlertThreshold type;
	
	public int getWeekDay() {
		return weekDay;
	}
	public void setWeekDay(int weekDay) {
		this.weekDay = weekDay;
	}
	public long getThreshold() {
		return threshold;
	}
	public void setThreshold(long threshold) {
		this.threshold = threshold;
	}
	public TypeAlertThreshold getType() {
		return type;
	}
	public void setType(TypeAlertThreshold type) {
		this.type = type;
	}

}
