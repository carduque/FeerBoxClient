package com.feerbox.client;

import java.util.concurrent.TimeoutException;

import com.diozero.HD44780Lcd;
import com.diozero.HD44780Lcd.LcdConnection;
import com.diozero.api.I2CConstants;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.RaspiPin;

public class DistanceSensorTest {
	static{
		System.setProperty("com.diozero.devicefactory", "com.diozero.internal.provider.pi4j.Pi4jDeviceFactory");
	}
	private static LcdConnection lcd_connection = new HD44780Lcd.PCF8574LcdConnection(I2CConstants.BUS_1, HD44780Lcd.PCF8574LcdConnection.DEFAULT_DEVICE_ADDRESS);
	private static HD44780Lcd  instance = new HD44780Lcd(lcd_connection, 16, 2);
	 private final static float SOUND_SPEED = 340.29f;  // speed of sound in m/s
	    
	    private final static int TRIG_DURATION_IN_MICROS = 10; // trigger duration of 10 micro s
	    private final static int WAIT_DURATION_IN_MILLIS = 60; // wait 60 milli s

	    private final static int TIMEOUT = 21000;
	    
	    private final static GpioController gpio = GpioFactory.getInstance();
	    
	    private static GpioPinDigitalInput echoPin;
	    private static GpioPinDigitalOutput trigPin;
	    
	public static void main(String[] args) throws TimeoutException {
		instance.clear();
		instance.displayControl(true, false, false);
        echoPin = gpio.provisionDigitalInputPin( RaspiPin.GPIO_00 ); // PI4J custom numbering (pin 11)
        trigPin = gpio.provisionDigitalOutputPin( RaspiPin.GPIO_07 ); // PI4J custom numbering (pin 7)
        trigPin.low();
		for(;;){
			System.out.printf( "%1$d,%2$.3f%n", System.currentTimeMillis(), measureDistance() );
			instance.setText(0, String.format("%2$.3f%n",measureDistance()));
		}
	}
	
	public static float measureDistance() throws TimeoutException {
        triggerSensor();
        waitForSignal();
        long duration = measureSignal();
        
        return duration * SOUND_SPEED / ( 2 * 10000 );
    }
	
	private static void triggerSensor() {
        try {
            trigPin.high();
            Thread.sleep( 0, TRIG_DURATION_IN_MICROS * 1000 );
            trigPin.low();
        } catch (InterruptedException ex) {
            System.err.println( "Interrupt during trigger" );
        }
    }
	
	 private static void waitForSignal() throws TimeoutException {
	        int countdown = TIMEOUT;
	        
	        while( echoPin.isLow() && countdown > 0 ) {
	            countdown--;
	        }
	        
	        if( countdown <= 0 ) {
	            throw new TimeoutException( "Timeout waiting for signal start" );
	        }
	    }
	 private static long measureSignal() throws TimeoutException {
	        int countdown = TIMEOUT;
	        long start = System.nanoTime();
	        while( echoPin.isHigh() && countdown > 0 ) {
	            countdown--;
	        }
	        long end = System.nanoTime();
	        
	        if( countdown <= 0 ) {
	            throw new TimeoutException( "Timeout waiting for signal end" );
	        }
	        
	        return (long)Math.ceil( ( end - start ) / 1000.0 );  // Return micro seconds
	    }
}
