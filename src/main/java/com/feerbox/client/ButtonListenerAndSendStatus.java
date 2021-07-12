package com.feerbox.client;

import java.io.IOException;
import java.util.Date;

import com.feerbox.client.services.AudioService;
import org.apache.log4j.Logger;

import com.feerbox.client.oslevel.OSExecutor;
import com.feerbox.client.registers.StatusRegister;
import com.feerbox.client.services.AnswerService;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class ButtonListenerAndSendStatus implements GpioPinListenerDigital {
	
	private GpioPinDigitalInput Button = null;
	private GpioPinDigitalOutput Led = null;
	private int buttonNumber = 0;
	private Date exactTime = null;
	private PinState lastState = PinState.HIGH;
	protected final OSExecutor oSExecutor;
	final static Logger logger = Logger.getLogger(ButtonListenerAndSendStatus.class);

	public ButtonListenerAndSendStatus(GpioPinDigitalInput button, GpioPinDigitalOutput led, int number, OSExecutor oSExecutor) {
		Button = button;
		Led = led;
		buttonNumber = number;
		this.oSExecutor = oSExecutor;
	}

	public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
		// display pin state on console
        //logger.debug(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
        if(event.getState().equals(PinState.LOW) && lastState==PinState.HIGH){
        	this.lastState = PinState.LOW;
        	//logger.debug("LOW");
        	if(exactTime!=null){
        		long seconds = (new Date().getTime()-exactTime.getTime())/500;  //5 seconds
        		if(seconds>10){
        			logger.debug("Going to send status");
        			Led.blink(500, 10000); // continuously blink the led every 1/2 second for 10 seconds
        			StatusRegister status = new StatusRegister(oSExecutor);
					status.run();
        		}
        		else{
					logger.debug("Button clicked");
					AudioService.playAnswerSound(buttonNumber);
					logger.debug("Save answer");
                    AnswerService.saveAnswer(buttonNumber);
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
