package com.feerbox.client.services;

import com.pi4j.io.gpio.GpioPinDigitalOutput;

public class LedService {
	private static GpioPinDigitalOutput Led1 = null;
	private static GpioPinDigitalOutput Led2= null;
	private static GpioPinDigitalOutput Led3 = null;
	private static GpioPinDigitalOutput Led4 = null;
	private static GpioPinDigitalOutput Led5 = null;
	
	
	public static GpioPinDigitalOutput getLed1() {
		return Led1;
	}
	public static void setLed1(GpioPinDigitalOutput led1) {
		Led1 = led1;
	}
	public static GpioPinDigitalOutput getLed2() {
		return Led2;
	}
	public static void setLed2(GpioPinDigitalOutput led2) {
		Led2 = led2;
	}
	public static GpioPinDigitalOutput getLed3() {
		return Led3;
	}
	public static void setLed3(GpioPinDigitalOutput led3) {
		Led3 = led3;
	}
	public static GpioPinDigitalOutput getLed4() {
		return Led4;
	}
	public static void setLed4(GpioPinDigitalOutput led4) {
		Led4 = led4;
	}
	public static GpioPinDigitalOutput getLed5() {
		return Led5;
	}
	public static void setLed5(GpioPinDigitalOutput led5) {
		Led5 = led5;
	}
	
	public static void animation() {
		int time = 250;
		LedService.getLed1().pulse(time, true);
		LedService.getLed2().pulse(time, true);
		LedService.getLed3().pulse(time, true);
		LedService.getLed4().pulse(time, true);
		LedService.getLed5().pulse(time, true);
		LedService.getLed5().pulse(time, true);
		LedService.getLed4().pulse(time, true);
		LedService.getLed3().pulse(time, true);
		LedService.getLed2().pulse(time, true);
		LedService.getLed1().pulse(time, true);
	}
	
	
}
