package com.feerbox.client;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.diozero.HD44780Lcd;
import com.diozero.HD44780Lcd.LcdConnection;
import com.diozero.api.I2CConstants;
import com.diozero.util.SleepUtil;

public class LCDWrapper {
	static{
		System.setProperty("com.diozero.devicefactory", "com.diozero.internal.provider.pi4j.Pi4jDeviceFactory");
	}
	private static LcdConnection lcd_connection = new HD44780Lcd.PCF8574LcdConnection(I2CConstants.BUS_1, HD44780Lcd.PCF8574LcdConnection.DEFAULT_DEVICE_ADDRESS);
	private static HD44780Lcd  instance = new HD44780Lcd(lcd_connection, 16, 2);
	//I2CLcd instance = new I2CLcd(16, 2)

	public static void setTextRow0(String text){
		instance.displayControl(true, false, false);
		instance.setText(0, text);
	}
	
	public static void setTextRow1(String text){
		instance.displayControl(true, false, false);
		instance.setText(1, text);
	}
	
	public static void init(String text){
		instance.setText(0, text);
		SleepUtil.sleepSeconds(10);
		instance.clear();
	}
	
	public static void clear(){
		instance.clear();
	}

	public static void setCurrentTimeRow1() {
		String now = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(convertTimeZone(new Date(), TimeZone.getDefault(),TimeZone.getTimeZone("Europe/Madrid")));
		setTextRow1(now);
	}
	
	protected static java.util.Date convertTimeZone(java.util.Date date, TimeZone fromTZ , TimeZone toTZ)
	{
	    long fromTZDst = 0;
	    if(fromTZ.inDaylightTime(date))
	    {
	        fromTZDst = fromTZ.getDSTSavings();
	    }
	 
	    long fromTZOffset = fromTZ.getRawOffset() + fromTZDst;
	 
	    long toTZDst = 0;
	    if(toTZ.inDaylightTime(date))
	    {
	        toTZDst = toTZ.getDSTSavings();
	    }
	    long toTZOffset = toTZ.getRawOffset() + toTZDst;
	 
	    return new java.util.Date(date.getTime() + (toTZOffset - fromTZOffset));
	}
}
