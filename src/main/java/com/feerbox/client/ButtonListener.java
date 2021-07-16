package com.feerbox.client;

import java.util.Date;

import com.feerbox.client.registers.ClientRegister;
import com.feerbox.client.services.AudioService;
import org.apache.log4j.Logger;

import com.feerbox.client.services.AnswerService;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class ButtonListener implements GpioPinListenerDigital {
	final static Logger logger = Logger.getLogger(ButtonListener.class);

	protected GpioPinDigitalInput Button = null;
	protected GpioPinDigitalOutput Led = null;
	protected int buttonNumber = 0;
	protected Date exactTime = null;

	public ButtonListener(GpioPinDigitalInput button, GpioPinDigitalOutput led, int number) {
		Button = button;
		Led = led;
		buttonNumber = number;
	}

	@Override
	public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
		if (isActive()) {
			if (event.getState().equals(PinState.LOW)) {
				AnswerService.saveAnswer(buttonNumber);
			}
			if (event.getState().equals(PinState.HIGH)) {
				this.exactTime = new Date();
				//We would like to light led when push
				Led.pulse(1000, true); // set second argument to 'true' use a blocking call
				if (ClientRegister.getInstance().getButtonSoundEnabled()) {
					AudioService.playAnswerSound(buttonNumber);
				}
			}
		}
	}

	protected boolean isActive() {
		long pauseBetween = ClientRegister.getInstance().getButtonPauseBetweenMesurements();

		if (exactTime == null) {
			return true;
		} else {
			long lastTime = exactTime.getTime();
			long currentTime = new Date().getTime();

			if (lastTime == 0 || ((currentTime - lastTime) > pauseBetween)) {
				return true;
			}
		}

		return false;
	}
}
