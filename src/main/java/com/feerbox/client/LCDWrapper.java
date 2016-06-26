package com.feerbox.client;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.diozero.I2CLcd;
import com.diozero.util.SleepUtil;

public class LCDWrapper {
	static{
		System.setProperty("com.diozero.devicefactory", "com.diozero.internal.provider.pi4j.Pi4jDeviceFactory");
	}
	private static I2CLcd instance = new I2CLcd(16, 2);


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
		String now = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
		setTextRow1(now);
	}
}
