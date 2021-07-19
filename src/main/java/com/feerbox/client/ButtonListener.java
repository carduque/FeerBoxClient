package com.feerbox.client;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

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

	final static ReentrantLock lock = new ReentrantLock();
	static protected Date exactTime = null;

	protected GpioPinDigitalInput Button = null;
	protected GpioPinDigitalOutput Led = null;
	protected int buttonNumber = 0;


	public ButtonListener(GpioPinDigitalInput button, GpioPinDigitalOutput led, int number) {
		Button = button;
		Led = led;
		buttonNumber = number;
	}

	@Override
	public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
		lock.lock();
		if (isActive()) {
			if (event.getState().equals(PinState.LOW)) {
				long seconds = (new Date().getTime() - exactTime.getTime()) / 1000;
				if (seconds > 10) {
					onLongClick();
				}
			}
			if (event.getState().equals(PinState.HIGH)) {
				this.exactTime = new Date();
				onClick();
			}
		}
		lock.unlock();
	}

	protected void onClick() {
		Led.pulse(1000, true); // set second argument to 'true' use a blocking call
		if (ClientRegister.getInstance().getButtonSoundEnabled()) {
			AudioService.playAnswerSound(buttonNumber);
		}
		AnswerService.saveAnswer(buttonNumber);
	}

	protected void onLongClick() {

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
