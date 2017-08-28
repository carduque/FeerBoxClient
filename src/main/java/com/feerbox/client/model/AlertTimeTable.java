package com.feerbox.client.model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class AlertTimeTable {
	private LocalTime startingTime;
	private LocalTime closingTime;
	private long threshold;
	private int weekDay;
	
	public LocalTime getStartingTime() {
		return startingTime;
	}
	public void setStartingTime(LocalTime startingTime) {
		this.startingTime = startingTime;
	}
	public LocalTime getClosingTime() {
		return closingTime;
	}
	public void setClosingTime(LocalTime closingTime) {
		this.closingTime = closingTime;
	}
	public long getThreshold() {
		return this.threshold;
	}
	public void setThreshold(long threshold) {
		this.threshold = threshold;
	}
	public int getWeekDay() {
		return weekDay;
	}
	public void setWeekDay(int weekDay) {
		this.weekDay = weekDay;
	}
	public String getStartingTimeDateFormatted() {
		DateTimeFormatter df = DateTimeFormatter.ofPattern("HH:mm:ss");
		return df.format(this.startingTime);
	}
	public String getClosingTimeDateFormatted() {
		DateTimeFormatter df = DateTimeFormatter.ofPattern("HH:mm:ss");
		return df.format(this.closingTime);
	}
}
