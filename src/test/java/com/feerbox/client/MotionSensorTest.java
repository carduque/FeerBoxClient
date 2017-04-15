package com.feerbox.client;

import java.util.concurrent.Callable;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.trigger.GpioCallbackTrigger;

public class MotionSensorTest {
	public static void main(String[] args) throws InterruptedException {

        System.out.printf("PIR Module Test (CTRL+C to exit)\n");

        // create gpio controller
        final GpioController gpio = GpioFactory.getInstance();
        // provision gpio pin #29, (header pin 40) as an input pin with its internal pull down resistor enabled
        final GpioPinDigitalInput pir = gpio.provisionDigitalInputPin(RaspiPin.GPIO_29);
        System.out.printf("Ready\n");

        // create a gpio callback trigger on the gpio pin
        Callable<Void> callback = new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				System.out.println(" --> GPIO TRIGGER CALLBACK RECEIVED ");
	            return null;
			}
            
        };
        // create a gpio callback trigger on the PIR device pin for when it's state goes high
        pir.addTrigger(new GpioCallbackTrigger(PinState.HIGH, callback));

        // stop all GPIO activity/threads by shutting down the GPIO controller
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("Interrupted, stopping...\n");
                gpio.shutdown();
            }
        });

        // keep program running until user aborts (CTRL-C)
        for (;;) {
            Thread.sleep(100);
        }

    }
}
