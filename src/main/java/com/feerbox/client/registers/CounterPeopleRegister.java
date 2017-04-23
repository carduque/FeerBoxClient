package com.feerbox.client.registers;

import java.util.Date;

import org.apache.log4j.Logger;

import com.feerbox.client.LCDWrapper;
import com.feerbox.client.db.SaveCounterPeople;
import com.feerbox.client.model.CounterPeople;
import com.feerbox.client.model.CounterPeople.Type;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class CounterPeopleRegister extends Thread {
	final static Logger logger = Logger.getLogger(CounterPeopleRegister.class);
	private final static float SOUND_SPEED = 340.29f;  // speed of sound in m/s (it could differ based on many factors like temperature)
	private static final int TIMEOUT = 20000;
	private final GpioPinDigitalInput echoPin;
    private final GpioPinDigitalOutput trigPin;
    private final GpioPinDigitalInput pirPin;
    private int people_count_ds=0;
    private int people_count_pir = 0;
	
	public CounterPeopleRegister(){
		GpioController gpio = GpioFactory.getInstance();
		this.echoPin = gpio.provisionDigitalInputPin(  RaspiPin.GPIO_11 ); //GPIO 08
        this.trigPin = gpio.provisionDigitalOutputPin(  RaspiPin.GPIO_10 ); //GPIO 07
        this.trigPin.low();
        this.pirPin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_29, PinPullResistance.PULL_DOWN);
	}
	
	public void run() {
		logger.debug("Starting CounterPeople");
		
		boolean counted=false;
		if(ClientRegister.getInstance().getCounterPeopleLCD()){
			LCDWrapper.clear();
		}
		RegisterPIRListener();
		logger.debug("Starting DS CounterPeople");
		while(true){
			try {
				Thread.sleep(ClientRegister.getInstance().getCounterPeoplePauseBetweenMesurements());
			} catch (InterruptedException e) {
				logger.error("Error in pause between mesurements");
			}
			int countdown = TIMEOUT;
			triggerSensor();
			while(echoPin.isLow() && countdown > 0 ){
				countdown--;
			}
			long startTime= System.nanoTime(); // Store the current time to calculate ECHO pin HIGH time.
			if(countdown>0){
				countdown = TIMEOUT;
				while(echoPin.isHigh() && countdown > 0 ){
					countdown--;
				}
				long endTime= System.nanoTime(); // Store the echo pin HIGH end time to calculate ECHO pin HIGH time.
				if(countdown>0){
					double distance = (((endTime-startTime)/1000.0)* SOUND_SPEED / (20000.0)); //distance in cm
					
					/*
					 * Min = distance from sensor to end of door in cm
					 * Max = distance from sensor to end of door in cm + some buffer. It has to always less than distance from sensor to end of wall in front.
					 * Min and Max should be the same, but we could have some buffer for error margin
					 */
					if(ClientRegister.getInstance().getCounterPeopleDebugMode() && ClientRegister.getInstance().getCounterPeopleLCD()){
						LCDWrapper.setTextRow1("Distance: "+distance);
					}
					if(distance<ClientRegister.getInstance().getCounterPeopleMinThreshold() && !counted){
						counted=true;
						people_count_ds++;
						//logger.debug("Another Person! - Total: "+people_count);
						saveCounterPeople(distance, CounterPeople.Type.DISTANCE_SENSOR);
						if(ClientRegister.getInstance().getCounterPeopleDebugMode()){
							logger.debug("Person!");
						}
						if(ClientRegister.getInstance().getCounterPeopleLCD()){
							printLCD();
						}
					}
					else{
						if(distance>ClientRegister.getInstance().getCounterPeopleMaxThreshold()){
							counted=false;
						}
					}
				}
			}
		}
	}

	private void RegisterPIRListener() {
		logger.debug("Register PIR CounterPeople");
		// create and register gpio pin listener            
		this.pirPin.addListener(new GpioPinListenerDigital() {           

			@Override       
		    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {        

		        if(event.getState().isHigh()){
		        	saveCounterPeople(0, CounterPeople.Type.PIR);
					if(ClientRegister.getInstance().getCounterPeopleLCD()){
						people_count_pir++;
						printLCD();
					}
		        }   

		        if(event.getState().isLow()){
		        }   

		    }       

		});    
	}

	private void saveCounterPeople(double distance, Type type) {
		//Store information in DB
		CounterPeople counterPeople = new CounterPeople();
		counterPeople.setTime(new Date());
		counterPeople.setUpload(false);
		counterPeople.setDistance(distance);
		counterPeople.setFeerBoxReference(ClientRegister.getInstance().getReference());
		counterPeople.setType(type);
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
	private void printLCD(){
		LCDWrapper.clear();
		LCDWrapper.setTextRow0("DS: "+people_count_ds);
		LCDWrapper.setTextRow1("PIR: "+people_count_pir);
	}
}
