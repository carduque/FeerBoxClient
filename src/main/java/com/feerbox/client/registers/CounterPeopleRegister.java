package com.feerbox.client.registers;

import java.util.Date;

import org.apache.log4j.Logger;

import com.feerbox.client.db.SaveCounterPeople;
import com.feerbox.client.model.CounterPeople;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.RaspiPin;

public class CounterPeopleRegister extends Thread {
	final static Logger logger = Logger.getLogger(CounterPeopleRegister.class);
	private final static float SOUND_SPEED = 340.29f;  // speed of sound in m/s (it could differ based on many factors like temperature)
	private final GpioPinDigitalInput echoPin;
    private final GpioPinDigitalOutput trigPin;
	
	public CounterPeopleRegister(){
		GpioController gpio = GpioFactory.getInstance();
		this.echoPin = gpio.provisionDigitalInputPin(  RaspiPin.GPIO_11 ); //GPIO 08
        this.trigPin = gpio.provisionDigitalOutputPin(  RaspiPin.GPIO_10 ); //GPIO 07
        this.trigPin.low();
	}
	
	public void run() {
		logger.debug("Starting CounterPeople");
		int people_count=0;
		boolean counted=false;
		while(true){
			try {
				Thread.sleep(ClientRegister.getInstance().getCounterPeoplePauseBetweenMesurements());
			} catch (InterruptedException e) {
				logger.error("Error in pause between mesurements");
			}
			triggerSensor();
			while(echoPin.isLow());
			long startTime= System.nanoTime(); // Store the current time to calculate ECHO pin HIGH time.
			while(echoPin.isHigh());
			long endTime= System.nanoTime(); // Store the echo pin HIGH end time to calculate ECHO pin HIGH time.
			double distance = (((endTime-startTime)/1000.0)* SOUND_SPEED / (20000.0)); //distance in cm
			
			/*
			 * Min = distance from sensor to end of door in cm
			 * Max = distance from sensor to end of door in cm + some buffer. It has to always less than distance from sensor to end of wall in front.
			 * Min and Max should be the same, but we could have some buffer for error margin
			 */
			if(distance<ClientRegister.getInstance().getCounterPeopleMinThreshold() && !counted){
				people_count++;
				logger.debug("Another Person! - Total: "+people_count);
				saveCounterPeople(distance);
				counted=true;
			}
			else{
				if(distance>ClientRegister.getInstance().getCounterPeopleMaxThreshold()){
					counted=false;
				}
			}
			
		}
	}

	private void saveCounterPeople(double distance) {
		//Store information in DB
		CounterPeople counterPeople = new CounterPeople();
		counterPeople.setTime(new Date());
		counterPeople.setUpload(false);
		counterPeople.setDistance(distance);
		SaveCounterPeople.save(counterPeople);
	}

	private void triggerSensor() {
		try {
			this.trigPin.high(); // Make trigger pin HIGH
			Thread.sleep((long) 0.01);// Delay for 10 microseconds
			this.trigPin.low(); //Make trigger pin LOW
		} catch (InterruptedException e) {
			logger.error("Error triggering sensor");
		}
	}
}
