package com.feerbox.client;


import java.io.Console;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;

public class DistanceSensor3Test {
	//GPIO Pins
		private static GpioPinDigitalOutput sensorTriggerPin ;
		private static GpioPinDigitalInput sensorEchoPin ;
		final static GpioController gpio = GpioFactory.getInstance();
		
		public static void main(String [] args) throws InterruptedException{
			new DistanceSensor3Test().run();
		}
		public void run() throws InterruptedException{
			sensorTriggerPin =  gpio.provisionDigitalOutputPin(RaspiPin.GPIO_10); // Trigger pin as OUTPUT
			sensorEchoPin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_11,PinPullResistance.PULL_DOWN); // Echo pin as INPUT
			int people_count=0;
			boolean counted=false;
			Console console = System.console();
			while(true){
				try {
				Thread.sleep(700);
				sensorTriggerPin.high(); // Make trigger pin HIGH
				Thread.sleep((long) 0.01);// Delay for 10 microseconds
				sensorTriggerPin.low(); //Make trigger pin LOW
			
				while(sensorEchoPin.isLow()){ //Wait until the ECHO pin gets HIGH
					System.out.println("Waiting echo low");
				}
				long startTime= System.nanoTime(); // Store the surrent time to calculate ECHO pin HIGH time.
				while(sensorEchoPin.isHigh()){ //Wait until the ECHO pin gets LOW
					System.out.println("Waiting echo high");
				}
				long endTime= System.nanoTime(); // Store the echo pin HIGH end time to calculate ECHO pin HIGH time.
			
				//System.out.println("Distance :"+((((endTime-startTime)/1e3)/2) / 29.1) +" cm"); //Printing out the distance in cm
				//instance.setText(0, ""+(((endTime-startTime)/1e3)/2) / 29.1);
				double distance = (((endTime-startTime)/1e3)/2) / 29.1;
				if(distance<120.0 && !counted){
					people_count++;
					counted=true;
					System.out.println("Person "+people_count);
				}
				else{
					if(distance>=120.0){
						counted=false;
						System.out.println("");
						console.printf("%s","\u0008 Distance: "+distance);
					}
					else{
						System.out.println("");
						console.printf("%s","\u0008 Distance: "+distance);
					}
				}
				//Thread.sleep(100);
				
			} catch (InterruptedException e) {
				e.printStackTrace();
				}
			}
		}
}
