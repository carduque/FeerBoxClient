package com.feerbox.client.services;

import com.pi4j.io.gpio.GpioPinDigitalInput;

public class ButtonService {
	private static GpioPinDigitalInput Button1 = null;
	private static GpioPinDigitalInput Button2 = null;
	private static GpioPinDigitalInput Button3 = null;
	private static GpioPinDigitalInput Button4 = null;
	private static GpioPinDigitalInput Button5 = null;
	
	public static GpioPinDigitalInput getButton1() {
		return Button1;
	}
	public static void setButton1(GpioPinDigitalInput button1) {
		Button1 = button1;
	}
	public static GpioPinDigitalInput getButton2() {
		return Button2;
	}
	public static void setButton2(GpioPinDigitalInput button2) {
		Button2 = button2;
	}
	public static GpioPinDigitalInput getButton3() {
		return Button3;
	}
	public static void setButton3(GpioPinDigitalInput button3) {
		Button3 = button3;
	}
	public static GpioPinDigitalInput getButton4() {
		return Button4;
	}
	public static void setButton4(GpioPinDigitalInput button4) {
		Button4 = button4;
	}
	public static GpioPinDigitalInput getButton5() {
		return Button5;
	}
	public static void setButton5(GpioPinDigitalInput button5) {
		Button5 = button5;
	}

	

}
