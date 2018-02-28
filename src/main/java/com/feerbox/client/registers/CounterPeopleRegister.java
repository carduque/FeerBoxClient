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

public class CounterPeopleRegister extends Thread {
	final static Logger logger = Logger.getLogger(CounterPeopleRegister.class);
	private final static float SOUND_SPEED = 340.29f;  // speed of sound in m/s (it could differ based on many factors like temperature)
	private static final int TIMEOUT = 20000;
    private final GpioPinDigitalInput pirPin;
    private long lastPIRPersonDetected = 0;
    private int people_count_pir = 0;
	
	public CounterPeopleRegister(){
		GpioController gpio = GpioFactory.getInstance();
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
			logger.debug("CounterPeople Distance Sensor removed, it is not operational anymore");
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

	private void printLCD(){
		LCDWrapper.clear();
		LCDWrapper.setTextRow0("PIR: "+people_count_pir);
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
