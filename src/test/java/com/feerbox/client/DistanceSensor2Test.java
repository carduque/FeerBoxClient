package com.feerbox.client;


import com.diozero.HD44780Lcd;
import com.diozero.HD44780Lcd.LcdConnection;
import com.diozero.api.I2CConstants;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;

public class DistanceSensor2Test {
	static{
		System.setProperty("com.diozero.devicefactory", "com.diozero.internal.provider.pi4j.Pi4jDeviceFactory");
	}
	//GPIO Pins
		private static GpioPinDigitalOutput sensorTriggerPin ;
		private static GpioPinDigitalInput sensorEchoPin ;
		private static LcdConnection lcd_connection = new HD44780Lcd.PCF8574LcdConnection(I2CConstants.BUS_1, HD44780Lcd.PCF8574LcdConnection.DEFAULT_DEVICE_ADDRESS);
		private static HD44780Lcd  instance = new HD44780Lcd(lcd_connection, 16, 2);
		
		final static GpioController gpio = GpioFactory.getInstance();
		
		public static void main(String [] args) throws InterruptedException{
			new DistanceSensor2Test().run();
		}
		public void run() throws InterruptedException{
			instance.clear();
			instance.displayControl(true, false, false);
			sensorTriggerPin =  gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07); // Trigger pin as OUTPUT
			sensorEchoPin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00,PinPullResistance.PULL_DOWN); // Echo pin as INPUT
			int people_count=0;
			boolean counted=false;
			instance.setText(0, people_count+"");
			while(true){
				try {
				Thread.sleep(700);
				sensorTriggerPin.high(); // Make trigger pin HIGH
				Thread.sleep((long) 0.01);// Delay for 10 microseconds
				sensorTriggerPin.low(); //Make trigger pin LOW
			
				while(sensorEchoPin.isLow()){ //Wait until the ECHO pin gets HIGH
					
				}
				long startTime= System.nanoTime(); // Store the surrent time to calculate ECHO pin HIGH time.
				while(sensorEchoPin.isHigh()){ //Wait until the ECHO pin gets LOW
					
				}
				long endTime= System.nanoTime(); // Store the echo pin HIGH end time to calculate ECHO pin HIGH time.
			
				//System.out.println("Distance :"+((((endTime-startTime)/1e3)/2) / 29.1) +" cm"); //Printing out the distance in cm
				//instance.setText(0, ""+(((endTime-startTime)/1e3)/2) / 29.1);
				double distance = (((endTime-startTime)/1e3)/2) / 29.1;
				if(distance<30.0 && !counted){
					System.out.println("Person!");
					people_count++;
					instance.setText(0, people_count+"");
					counted=true;
				}
				else{
					if(distance>60.0){
						counted=false;
						//System.out.println("Clean!");
					}
					else{
						//System.out.println("Distance :"+distance);
					}
				}
				//Thread.sleep(100);
				
			} catch (InterruptedException e) {
				e.printStackTrace();
				}
			}
		}
}
