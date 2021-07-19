package com.feerbox.client;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;

public class ButtonListenerAndReboot extends ButtonListener {
	final static Logger logger = Logger.getLogger(ButtonListenerAndReboot.class);


	public ButtonListenerAndReboot(GpioPinDigitalInput button, GpioPinDigitalOutput led, int number) {
		super(button, led, number);
	}

	@Override
	protected void onLongClick() {
		logger.debug("Going to reboot");
		Led.blink(500, 10000); // continuously blink the led every 1/2 second for 10 seconds
		try {
			Runtime.getRuntime().exec("reboot");
		} catch (IOException e) {
			logger.debug("IOException", e);
		}
	}
}
