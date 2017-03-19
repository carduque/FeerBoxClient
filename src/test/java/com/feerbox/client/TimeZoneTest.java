package com.feerbox.client;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/*
 * System time:18/03/2017 11:11:33.786
Default Timezone: sun.util.calendar.ZoneInfo[id="Europe/Madrid",offset=3600000,dstSavings=3600000,useDaylight=true,transitions=165,lastRule=java.util.SimpleTimeZone[id=Europe/Madrid,offset=3600000,dstSavings=3600000,useDaylight=true,startYear=0,startMode=2,startMonth=2,startDay=-1,startDayOfWeek=1,startTime=3600000,startTimeMode=2,endMode=2,endMonth=9,endDay=-1,endDayOfWeek=1,endTime=3600000,endTimeMode=2]]
pi@raspberrypi ~/timezone $ date
Sat Mar 18 11:11:39 CET 2017
 */

public class TimeZoneTest {
	public static void main(String args[]){
		Date now = new Date(System.currentTimeMillis());
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
		System.out.println("System time:"+df.format(now));
		System.out.println("Default Timezone: "+df.getTimeZone());
		System.out.println("Default Timezone2: "+TimeZone.getDefault());
	}
}
