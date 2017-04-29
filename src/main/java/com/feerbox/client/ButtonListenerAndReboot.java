package com.feerbox.client;

import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;

import com.feerbox.client.services.SaveAnswerService;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class ButtonListenerAndReboot implements GpioPinListenerDigital {
	
	private GpioPinDigitalInput Button = null;
	private GpioPinDigitalOutput Led = null;
	private int buttonNumber = 0;
	private Date exactTime = null;
	private PinState lastState = PinState.HIGH;
	final static Logger logger = Logger.getLogger(ButtonListenerAndReboot.class);

	public ButtonListenerAndReboot(GpioPinDigitalInput button, GpioPinDigitalOutput led, int number) {
		Button = button;
		Led = led;
		buttonNumber = number;
	}

	public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
		// display pin state on console
        //logger.debug(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
        if(event.getState().equals(PinState.LOW) && lastState==PinState.HIGH){
        	this.lastState = PinState.LOW;
        	//logger.debug("LOW");
        	if(exactTime!=null){
        		long seconds = (new Date().getTime()-exactTime.getTime())/1000;
        		if(seconds>10){
        			logger.debug("Going to reboot");
        			Led.blink(500, 10000); // continuously blink the led every 1/2 second for 10 seconds
        			try {
						Runtime.getRuntime().exec("reboot");
					} catch (IOException e) {
						logger.debug("IOException", e);
					}
        		}
        		else{
                    SaveAnswerService.saveAnswer(buttonNumber);
        		}
        	}
		}
        if(event.getState().equals(PinState.HIGH)){
        	this.exactTime = new Date();
        	this.lastState = PinState.HIGH;
        	//We would like to light led when push
        	Led.pulse(1000, true); // set second argument to 'true' use a blocking call
        }
	}

}
