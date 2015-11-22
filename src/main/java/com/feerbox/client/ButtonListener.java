package com.feerbox.client;

import java.util.Date;

import com.feerbox.client.db.SaveAnswer;
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

	public ButtonListener(GpioPinDigitalInput button, GpioPinDigitalOutput led, int number) {
		Button = button;
		Led = led;
		buttonNumber = number;
	}

	public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
		// display pin state on console
        //System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
        if(event.getState().equals(PinState.LOW)){
            SaveAnswer.saveAnswer(buttonNumber);
        	//FileStore.saveAnswer(buttonNumber);
			Led.pulse(2000, true); // set second argument to 'true' use a blocking call
		}
        if(event.getState().equals(PinState.HIGH)){
        	this.exactTime = new Date();
        }
	}

}
