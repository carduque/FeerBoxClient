package com.feerbox.client;

import com.pi4j.component.temperature.TemperatureSensor;
import com.pi4j.io.w1.W1Master;
import com.pi4j.temperature.TemperatureScale;

import java.io.IOException;

/**
* @author Peter Schuebl
*/
public class W1TempExample {

   public static  void main(String args[]) throws InterruptedException, IOException {
       //W1Master w1Master = new W1Master("C:\\work\\pi4j\\pi4j-device\\target\\test-classes\\w1\\sys\\bus\\w1\\devices");
       W1Master w1Master = new W1Master();

       System.out.println(w1Master);

       for (TemperatureSensor device : w1Master.getDevices(TemperatureSensor.class)) {
           System.out.printf("%-20s %3.1f°C %3.1f°F\n", device.getName(), device.getTemperature(),
                   device.getTemperature(TemperatureScale.FARENHEIT));
       }

       System.out.println("Exiting W1TempExample");
   }
}
