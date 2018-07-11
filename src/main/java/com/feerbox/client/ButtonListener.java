package com.feerbox.client;

import java.util.Date;

import org.apache.log4j.Logger;

import com.feerbox.client.services.AnswerService;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class ButtonListener implements GpioPinListenerDigital {
	
	private GpioPinDigitalInput Button = null;
	private GpioPinDigitalOutput Led = null;
	private int buttonNumber = 0;
	private Date exactTime = null;
	final static Logger logger = Logger.getLogger(ButtonListener.class);

	public ButtonListener(GpioPinDigitalInput button, GpioPinDigitalOutput led, int number) {
		Button = button;
		Led = led;
		buttonNumber = number;
	}

	public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        if(event.getState().equals(PinState.LOW)){
            AnswerService.saveAnswer(buttonNumber);
		}
        if(event.getState().equals(PinState.HIGH)){
        	logger.debug("button pulsed");
        	this.exactTime = new Date();
        	//We would like to light led when push
        	Led.pulse(1000, true); // set second argument to 'true' use a blocking call
        	logger.debug("light on");
        }
	}

}
