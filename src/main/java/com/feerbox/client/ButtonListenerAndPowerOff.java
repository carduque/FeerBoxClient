package com.feerbox.client;

import java.io.IOException;
import java.util.Date;

import com.feerbox.client.services.SaveAnswerService;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class ButtonListenerAndPowerOff implements GpioPinListenerDigital {
	
	private GpioPinDigitalInput Button = null;
	private GpioPinDigitalOutput Led = null;
	private int buttonNumber = 0;
	private Date exactTime = null;

	public ButtonListenerAndPowerOff(GpioPinDigitalInput button, GpioPinDigitalOutput led, int number) {
		Button = button;
		Led = led;
		buttonNumber = number;
	}

	public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
		// display pin state on console
        //System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
        if(event.getState().equals(PinState.LOW)){
        	System.out.println("LOW");
        	if(exactTime!=null){
        		long seconds = (new Date().getTime()-exactTime.getTime())/1000;
        		if(seconds>10){
        			System.out.println("Going to poweroff");
        			Led.blink(500, 10000); // continuously blink the led every 1/2 second for 10 seconds
        			try {
						Runtime.getRuntime().exec("shutdown -h now");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        		}
        		else{
        			Led.pulse(1000, true); // set second argument to 'true' use a blocking call
                    SaveAnswerService.saveAnswer(buttonNumber);
                	//FileStore.saveAnswer(buttonNumber);
        		}
        	}
		}
        if(event.getState().equals(PinState.HIGH)){
        	System.out.println("HIGH");
        	this.exactTime = new Date();
        }
	}

}
