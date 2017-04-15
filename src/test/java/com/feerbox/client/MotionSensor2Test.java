package com.feerbox.client;

import java.util.Date;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class MotionSensor2Test {
	private static int counter = 1;
	private static Date date1 = null;
	private static Date date2 = null;
	public static void main(String[] args) throws InterruptedException {
		System.out.println("Starting Pi4J Motion Sensor Example");                  

		// create gpio controller           
		final GpioController gpioSensor = GpioFactory.getInstance(); 
		final GpioPinDigitalInput sensor = gpioSensor.provisionDigitalInputPin(RaspiPin.GPIO_29, PinPullResistance.PULL_DOWN);          


		// create and register gpio pin listener            
		sensor.addListener(new GpioPinListenerDigital() {           
		    @Override       
		    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {        

		        if(event.getState().isHigh()){
		        	long seconds = 0;
		        	Date now = new Date();
		        	if(date1!=null){
		        		seconds = (now.getTime()-date1.getTime())/1000;
		        	}
		        	date1 = now;
		            System.out.println("Motion Detected: "+(counter++)+" in seconds:"+seconds); 
		        }   

		        if(event.getState().isLow()){
		        	long seconds = 0;
		        	Date now = new Date();
		        	if(date1!=null){
		        		seconds = (now.getTime()-date1.getTime())/1000;
		        	}
		            System.out.println("All is quiet..."+seconds);
		        }   

		    }       

		});         

		try {           
		    // keep program running until user aborts       
		    for (;;) {      
		        Thread.sleep(500);  
		    }       
		}           
		catch (final Exception e) {         
		    System.out.println(e.getMessage());     
		}
	}
}
