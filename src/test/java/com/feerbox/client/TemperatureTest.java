package com.feerbox.client;

import java.io.IOException;
import java.util.Arrays;

import org.apache.log4j.Logger;

import com.pi4j.util.NativeLibraryLoader;
/**
 * Sensor implementation for DHT22/AM2302 Digital Temperature And Humidity Sensor. Implemented using Adafruit Python library installed on Pi.
 * https://github.com/adafruit/Adafruit_Python_DHT.
 * 
 * @author Lubos Housa
 * @since Aug 4, 2014 7:27:57 PM
 */
public class TemperatureTest  {

    private static boolean initiated = true;

    static {
	try {
	    System.loadLibrary("/libdht.so");
	} catch (UnsatisfiedLinkError e) {
	    System.out.println("Unable to load the shared library dht"+ e.getMessage());
	    initiated = false;
	}
    }

    private static final int gpioPin = 25;

    /**
     * Create new instance of this sensor
     * 
     * @param gpioPin
     *            gpioPin that the Dht sensor is connected to
     */
    public TemperatureTest(int gpioPin) {
    	gpioPin = gpioPin;
    }

    public static void main() {
		if (isInitiated()) {
		    double[] numbers = executeNative(gpioPin);
		    if (numbers != null && numbers.length == 2) {
		    	System.out.println("Read temperature: "+numbers[0]);
		    	System.out.println("Read humidity: "+numbers[1]+"%");
		    } else {
		    	System.out.println("Some error ocurred when attempting to read the values from the sensor. Output: " + Arrays.asList(numbers));
		    }
		} else {
			System.out.println("Init failed before, not attempting to read anything from the sensor.");
		}
    }

    public static boolean isInitiated() {
    	return initiated;
    }

    /**
     * Invoke the method via JNI
     * 
     * @return temperature and humidity, in this order
     * @param gpioPin
     *            pin number
     * @return
     */
    private native static double[] executeNative(int gpioPin);
}
