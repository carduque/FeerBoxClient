package com.feerbox.client;

import org.apache.log4j.Logger;

import com.feerbox.client.oslevel.OSExecutor;
import com.feerbox.client.registers.StatusRegister;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;

public class ButtonListenerAndSendStatus extends ButtonListener {
	final static Logger logger = Logger.getLogger(ButtonListenerAndSendStatus.class);

	protected final OSExecutor oSExecutor;

	public ButtonListenerAndSendStatus(GpioPinDigitalInput button, GpioPinDigitalOutput led, int number, OSExecutor oSExecutor) {
		super(button, led, number);
		this.oSExecutor = oSExecutor;
	}

	@Override
	protected void onLongClick() {
		logger.debug("Going to send status");
		Led.blink(500, 10000); // continuously blink the led every 1/2 second for 10 seconds
		StatusRegister status = new StatusRegister(oSExecutor);
		status.run();
	}
}
