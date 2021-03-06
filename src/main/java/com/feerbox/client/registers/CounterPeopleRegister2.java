package com.feerbox.client.registers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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

public class CounterPeopleRegister2 extends Thread {
	final static Logger logger = Logger.getLogger(CounterPeopleRegister2.class);
	private final static float SOUND_SPEED = 340.29f;  // speed of sound in m/s (it could differ based on many factors like temperature)
	private static final int TIMEOUT = 20000;
	private final GpioPinDigitalInput echoPin;
    private final GpioPinDigitalOutput trigPin;
    private final GpioPinDigitalInput pirPin;
    private long lastPIRPersonDetected = 0;
    private int people_count_ds=0;
    private int people_count_pir = 0;
	
	public CounterPeopleRegister2(){
		GpioController gpio = GpioFactory.getInstance();
		this.echoPin = gpio.provisionDigitalInputPin(  RaspiPin.GPIO_11 ); //GPIO 08
        this.trigPin = gpio.provisionDigitalOutputPin(  RaspiPin.GPIO_10 ); //GPIO 07
        this.trigPin.low();
        this.pirPin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_29, PinPullResistance.PULL_DOWN);
	}
	
	public void run() {
		logger.debug("Starting CounterPeople");
		
		if(ClientRegister.getInstance().getCounterPeopleLCD()){
			LCDWrapper.clear();
		}
		RegisterPIRListener();
		DistanceSensorCounting();
		LaserCounting();
	}

	private void LaserCounting() {
		if(ClientRegister.getInstance().getCounterPeopleLaser()){
			ProcessBuilder pb = new ProcessBuilder("/bin/bash", "/opt/FeerBoxClient/FeerBoxClient/scripts/countpeople/laser.sh");
			//pb.directory(new File("/opt/FeerBoxClient/FeerBoxClient/scripts/countpeople"));
			//executeCommandLine("sudo python /opt/FeerBoxClient/FeerBoxClient/scripts/countpeople/laser_count.py");
			try {
				Process process = pb.inheritIO().start();
				logger.debug("Laser Count enabled");
			} catch (IOException e) {
				logger.error("Laser Count:"+ e.getMessage());
			}
		}
	}

	private void DistanceSensorCounting() {
		if(ClientRegister.getInstance().getCounterPeopleDistanceSensor()){
			boolean counted=false;
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
	}

	private void RegisterPIRListener() {
		logger.debug("Register PIR CounterPeople");
		// create and register gpio pin listener            
		this.pirPin.addListener(new GpioPinListenerDigital() {           

			@Override       
		    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {        

		        if(event.getState().isHigh()){
		        	if(lastPIRPersonDetected!=0 && (new Date().getTime() - lastPIRPersonDetected>ClientRegister.getInstance().getCounterPeoplePauseBetweenMesurements())){
		        		saveCounterPeople(0, CounterPeople.Type.PIR);
						if(ClientRegister.getInstance().getCounterPeopleLCD()){
							people_count_pir++;
							printLCD();
						}
		        	}
		        	lastPIRPersonDetected = new Date().getTime();
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
	
	protected String executeCommandLine(String command) {
		String out = "";
		BufferedReader in = null;
		Process proc = null;
		try {
			String[] cmd = { "/bin/sh", "-c", command };
			proc = Runtime.getRuntime().exec(cmd);
			in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			String line = null;
			 while ((line = in.readLine()) != null) {
				out += line;
			}
		} catch (IOException e) {
			logger.error("Error executing command: "+e.getMessage());
		}
		finally{
			if(in!=null){
				try {
					in.close();
				} catch (IOException e) {
					logger.error("Error executing command: "+e.getMessage());
				}
			}
			try {
				if(!proc.waitFor(1, TimeUnit.MINUTES)) {
				    //timeout - kill the process. 
				    proc.destroyForcibly();
				}
			} catch (InterruptedException e) {
				logger.error("Error executing command: "+e.getMessage());
			}
			
		}
        return out;
	}
}
