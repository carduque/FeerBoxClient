package com.feerbox.client;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.feerbox.client.db.SaveAnswerError;
import com.feerbox.client.registers.IPRegister;
import com.feerbox.client.registers.InternetAccessRegister;
import com.feerbox.client.registers.UploadAnswersRegister;
import com.feerbox.client.services.SaveAnswerService;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.trigger.GpioCallbackTrigger;
import com.pi4j.io.gpio.trigger.GpioSetStateTrigger;

public class StartFeerBoxClient {
	private static GpioPinDigitalInput Button1 = null;
	private static GpioPinDigitalOutput Led1 = null;
	private static GpioPinDigitalInput Button2 = null;
	private static GpioPinDigitalOutput Led2= null;
	private static GpioPinDigitalInput Button3 = null;
	private static GpioPinDigitalOutput Led3 = null;
	private static GpioPinDigitalInput Button4 = null;
	private static GpioPinDigitalOutput Led4 = null;
	private static GpioPinDigitalInput Button5 = null;
	private static GpioPinDigitalOutput Led5 = null;
	private static UploadAnswersRegister uploadJob = null;
	
	private static final GpioController gpio = GpioFactory.getInstance();
	
	public static void main(String args[]) throws InterruptedException {
        System.out.println("FeerBoxClient Started");
        // create gpio controller
        
        InitGPIO();
        lights();
        
        StartInternetAccessThreat();
        StartIPRegisterThreat();
        saveAnswersOnlineThreat();
        
        // create and register gpio pin listener
        registerButtonListeners();
        
        // keep program running until user aborts (CTRL-C)
        for (;;) {
            //Thread.sleep(500);
        }
        
        // stop all GPIO activity/threads by shutting down the GPIO controller
        // (this method will forcefully shutdown all GPIO monitoring threads and scheduled tasks)
        // gpio.shutdown();   <--- implement this method call if you wish to terminate the Pi4J GPIO controller        
    }


	private static void StartIPRegisterThreat() {
		IPRegister ipRegister = new IPRegister();
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(ipRegister, 0, 60, TimeUnit.MINUTES);
	}
	
	private static void StartInternetAccessThreat() {
		InternetAccessRegister internetRegister = new InternetAccessRegister();
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(internetRegister, 0, 1, TimeUnit.MINUTES);
	}
	
	private static void saveAnswersOnlineThreat() {
		UploadAnswersRegister uploadAnswersRegister = new UploadAnswersRegister();
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(uploadAnswersRegister, 0, 1, TimeUnit.MINUTES);
	}


	private static void registerButtonListeners() {
		ButtonListener buttonListener1 = new ButtonListener(Button1, Led1, 1);
		Button1.addListener(buttonListener1);
		ButtonListener buttonListener2 = new ButtonListener(Button2, Led2, 2);
		Button2.addListener(buttonListener2);
		ButtonListenerAndPowerOff buttonListener3 = new ButtonListenerAndPowerOff(Button3, Led3, 3);
		Button3.addListener(buttonListener3);
		ButtonListener buttonListener4 = new ButtonListener(Button4, Led4, 4);
		Button4.addListener(buttonListener4);
		ButtonListener buttonListener5 = new ButtonListener(Button5, Led5, 5);
		Button5.addListener(buttonListener5);
	}


	private static void lights() {
		Led1.pulse(500, true);
		Led2.pulse(500, true);
		Led3.pulse(500, true);
		Led4.pulse(500, true);
		Led5.pulse(500, true);
		try {
			SaveAnswerService.tryConnection();
		} catch (SaveAnswerError e) {
			Led3.pulse(2000, true);
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Led2.pulse(2000, false);
		Led3.pulse(2000, false);
		Led4.pulse(2000, false);
		Led5.pulse(2000, false);
		Led1.pulse(2000, false);
	}


	private static void InitGPIO() {
		Button1 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00, PinPullResistance.PULL_DOWN); //17 
		Led1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "LED1", PinState.LOW); //27
		Led1.setShutdownOptions(true, PinState.LOW);
		Button2 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_21, PinPullResistance.PULL_DOWN); //5
		Led2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_22, "LED2", PinState.LOW); //6
		Led2.setShutdownOptions(true, PinState.LOW);
		Button3 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_24, PinPullResistance.PULL_DOWN); //19
		Led3 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_25, "LED3", PinState.LOW); //26
		Led3.setShutdownOptions(true, PinState.LOW);
		Button4 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_01, PinPullResistance.PULL_DOWN); //18
		Led4 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "LED4", PinState.LOW); //23
		Led4.setShutdownOptions(true, PinState.LOW);
		Button5 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_27, PinPullResistance.PULL_DOWN); //16
		Led5 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_28, "LED5", PinState.LOW); //20
		Led5.setShutdownOptions(true, PinState.LOW);
	}
	

}
