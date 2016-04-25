package com.feerbox.client;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.feerbox.client.db.SaveAnswerError;
import com.feerbox.client.registers.ClientRegister;
import com.feerbox.client.registers.AliveRegister;
import com.feerbox.client.registers.KismetClient;
import com.feerbox.client.registers.StatusRegister;
import com.feerbox.client.registers.UploadAnswersRegister;
import com.feerbox.client.services.ButtonService;
import com.feerbox.client.services.LedService;
import com.feerbox.client.services.SaveAnswerService;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class StartFeerBoxClient {
	public static final String version = "1.2.8";
	public static KismetClient kismet;
	
	
	private static final GpioController gpio = GpioFactory.getInstance();
	private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
	
	public static void main(String args[]) throws InterruptedException {
        System.out.println("FeerBoxClient Started");
        // create gpio controller
        
        InitGPIO();
        lights();
        
        StartInternetAccessThreat();
        StartStatusThreat();
        saveAnswersOnlineThreat();
        StartWifiDetectionThreat();
        
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


	private static void StartWifiDetectionThreat() {
		if(ClientRegister.getInstance().getWifiDetection()){
			kismet = new KismetClient();
			kismet.connectToServer();
		}
	}


	private static void StartStatusThreat() {
		StatusRegister ipRegister = new StatusRegister();
		scheduler.scheduleAtFixedRate(ipRegister, 0, 60, TimeUnit.MINUTES);
	}
	
	private static void StartInternetAccessThreat() {
		//check Internet & alivelights & KismetServer alive
		AliveRegister internetRegister = new AliveRegister();
		scheduler.scheduleAtFixedRate(internetRegister, 0, 1, TimeUnit.MINUTES);
	}
	
	private static void saveAnswersOnlineThreat() {
		UploadAnswersRegister uploadAnswersRegister = new UploadAnswersRegister();
		scheduler.scheduleAtFixedRate(uploadAnswersRegister, 0, 1, TimeUnit.MINUTES);
	}


	private static void registerButtonListeners() {
		ButtonListener buttonListener1 = new ButtonListener(ButtonService.getButton1(), LedService.getLed1(), 1);
		ButtonService.getButton1().addListener(buttonListener1);
		ButtonListener buttonListener2 = new ButtonListener(ButtonService.getButton2(), LedService.getLed2(), 2);
		ButtonService.getButton2().addListener(buttonListener2);
		ButtonListenerAndPowerOff buttonListener3 = new ButtonListenerAndPowerOff(ButtonService.getButton3(), LedService.getLed3(), 3);
		ButtonService.getButton3().addListener(buttonListener3);
		ButtonListener buttonListener4 = new ButtonListener(ButtonService.getButton4(), LedService.getLed4(), 4);
		ButtonService.getButton4().addListener(buttonListener4);
		//ButtonListener buttonListener5 = new ButtonListener(ButtonService.getButton5(), LedService.getLed5(), 5);
		ButtonListenerAndReboot buttonListener5 = new ButtonListenerAndReboot(ButtonService.getButton5(), LedService.getLed5(), 5);
		ButtonService.getButton5().addListener(buttonListener5);
	}


	private static void lights() {
		LedService.getLed1().pulse(500, true);
		LedService.getLed2().pulse(500, true);
		LedService.getLed3().pulse(500, true);
		LedService.getLed4().pulse(500, true);
		LedService.getLed5().pulse(500, true);
		try {
			SaveAnswerService.tryConnection();
		} catch (SaveAnswerError e) {
			LedService.getLed3().pulse(2000, true);
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LedService.getLed2().pulse(2000, false);
		LedService.getLed3().pulse(2000, false);
		LedService.getLed4().pulse(2000, false);
		LedService.getLed5().pulse(2000, false);
		LedService.getLed1().pulse(2000, false);
	}


	private static void InitGPIO() {
		ButtonService.setButton1(gpio.provisionDigitalInputPin(RaspiPin.GPIO_00, PinPullResistance.PULL_DOWN)); //17 
		LedService.setLed1(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "LED1", PinState.LOW)); //27
		LedService.getLed1().setShutdownOptions(true, PinState.LOW);
		ButtonService.setButton2(gpio.provisionDigitalInputPin(RaspiPin.GPIO_21, PinPullResistance.PULL_DOWN)); //5
		LedService.setLed2(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_22, "LED2", PinState.LOW)); //6
		LedService.getLed2().setShutdownOptions(true, PinState.LOW);
		ButtonService.setButton3(gpio.provisionDigitalInputPin(RaspiPin.GPIO_24, PinPullResistance.PULL_DOWN)); //19
		LedService.setLed3(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_25, "LED3", PinState.LOW)); //26
		LedService.getLed3().setShutdownOptions(true, PinState.LOW);
		ButtonService.setButton4(gpio.provisionDigitalInputPin(RaspiPin.GPIO_01, PinPullResistance.PULL_DOWN)); //18
		LedService.setLed4(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "LED4", PinState.LOW)); //23
		LedService.getLed4().setShutdownOptions(true, PinState.LOW);
		ButtonService.setButton5(gpio.provisionDigitalInputPin(RaspiPin.GPIO_27, PinPullResistance.PULL_DOWN)); //16
		LedService.setLed5(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_28, "LED5", PinState.LOW)); //20
		LedService.getLed5().setShutdownOptions(true, PinState.LOW);
	}
	

}
