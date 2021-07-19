package com.feerbox.client;

import java.io.File;
import java.net.URL;
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

	protected static Date exactTime = null;
	protected static PinState lastState = null;

	protected GpioPinDigitalInput Button = null;
	protected GpioPinDigitalOutput Led = null;
	protected int buttonNumber = 0;
	protected URL soundUrl = null;


	public ButtonListener(GpioPinDigitalInput button, GpioPinDigitalOutput led, int number) {
		Button = button;
		Led = led;
		buttonNumber = number;
		soundUrl = getSoundUrl();
	}

	@Override
	public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
		lock.lock();
		if (isActive()) {
			if (event.getState().equals(PinState.LOW)) {
				this.lastState = PinState.LOW;
				if (exactTime != null) {
					long seconds = (new Date().getTime() - exactTime.getTime()) / 1000;
					if (seconds > 10) {
						onLongClick();
					}
				}
			}
			if (event.getState().equals(PinState.HIGH)) {
				this.exactTime = new Date();
				this.lastState = PinState.HIGH;
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
		// Overridable in custom listeners
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

	protected URL getSoundUrl() {
		try {
			File file = new File("/opt/FeerBoxClient/audios/answer_" + buttonNumber + ".wav");

			URL url;
			if (file.exists()) { // Custom audio
				url = file.toURI().toURL();
			} else { // Default audio
				url = this.getClass().getClassLoader().getResource("audios/answer_" + buttonNumber + ".wav");
			}
			return url;
		} catch (Exception e) {
			return null;
		}
	}
}
