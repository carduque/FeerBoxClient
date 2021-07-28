package com.feerbox.client.registers;

import com.feerbox.client.LCDWrapper;
import com.feerbox.client.db.SaveCounterPeople;
import com.feerbox.client.model.CounterPeople;
import com.feerbox.client.model.CounterPeople.Type;
import com.feerbox.client.services.AudioService;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class PresenceDetectorRegister extends Thread {
	final static Logger logger = Logger.getLogger(PresenceDetectorRegister.class);

	private final GpioPinDigitalInput pirPin;
	final long pauseBetween = ClientRegister.getInstance().getPresenceDetectorPauseBetweenMesurements();
    private long lastPIRDetection = 0;

	public PresenceDetectorRegister(){
		GpioController gpio = GpioFactory.getInstance();
        this.pirPin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_29, PinPullResistance.PULL_DOWN);
	}
	
	public void run() {
		logger.debug("Starting PresenceDetector");
		RegisterPIRListener();
	}

	// create and register gpio pin listener
	private void RegisterPIRListener() {
		this.pirPin.addListener(new GpioPinListenerDigital() {
			@Override       
		    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {        

		        if(event.getState().isHigh()){
		        	if(lastPIRDetection != 0 && ((new Date().getTime() - lastPIRDetection) > pauseBetween)) {
						logger.debug("Presence detected");
						AudioService.playSound("welcome");
		        	}
		        	lastPIRDetection = new Date().getTime();
		        }
		    }
		});    
	}
}
