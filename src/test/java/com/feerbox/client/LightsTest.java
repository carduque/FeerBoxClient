package com.feerbox.client;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class LightsTest {

	public static void main(String[] args) {
		System.out.println("Led test");
		GpioController gpio = GpioFactory.getInstance();
		GpioPinDigitalOutput led1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "LED1", PinState.LOW);
		GpioPinDigitalOutput led2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_22, "LED2", PinState.LOW);
		GpioPinDigitalOutput led3 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_25, "LED3", PinState.LOW);
		GpioPinDigitalOutput led4 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "LED4", PinState.LOW);
		GpioPinDigitalOutput led5 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_28, "LED5", PinState.LOW);
		led1.setShutdownOptions(true, PinState.LOW);
		led1.pulse(5000, true);
		led2.setShutdownOptions(true, PinState.LOW);
		led2.pulse(5000, true);
		led3.setShutdownOptions(true, PinState.LOW);
		led3.pulse(5000, true);
		led4.setShutdownOptions(true, PinState.LOW);
		led4.pulse(5000, true);
		led5.setShutdownOptions(true, PinState.LOW);
		led5.pulse(5000, true);
		System.out.println("Led test done");
	}

}
