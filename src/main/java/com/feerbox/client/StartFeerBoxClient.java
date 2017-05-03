package com.feerbox.client;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.feerbox.client.db.SaveAnswerError;
import com.feerbox.client.registers.AliveRegister;
import com.feerbox.client.registers.CleanerRegister;
import com.feerbox.client.registers.ClientRegister;
import com.feerbox.client.registers.CommandExecutor;
import com.feerbox.client.registers.CommandQueueRegister;
import com.feerbox.client.registers.CounterPeopleRegister;
import com.feerbox.client.registers.InformationServerRegister;
import com.feerbox.client.registers.MACDetection;
import com.feerbox.client.registers.NFCReader;
import com.feerbox.client.registers.StatusRegister;
import com.feerbox.client.services.ButtonService;
import com.feerbox.client.services.LedService;
import com.feerbox.client.services.SaveAnswerService;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class StartFeerBoxClient {
	public static final String version = "1.5.3";
	public static MACDetection sniffer;
	final static Logger logger = Logger.getLogger(StartFeerBoxClient.class);
	
	
	private static final GpioController gpio = GpioFactory.getInstance();
	
	public static void main(String args[]) throws InterruptedException {
        logger.debug("FeerBoxClient Started version "+version);
        // create gpio controller
        
        InitGPIO();
        lights();
        StartLedPower();
        StartInternetAccessThread();
        StartStatusThreat();
        saveInformationServerThread();
        StartWifiDetectionThread();
        StartNFCReaderThreat();
        StartCommandServerPolling();
        StartCleanerServerPolling();
        StartCounterPeoplePolling();
        
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


	private static void StartCounterPeoplePolling() {
		if(ClientRegister.getInstance().getCounterPeopleEnabled()){
			CounterPeopleRegister counterPeople = new CounterPeopleRegister();
			counterPeople.start();
		}
	}


	private static void StartLedPower() {
		if(ClientRegister.getInstance().getLedPowerOn()){
			LedService.getLedPower().high(); //Switch on forever
		}
	}


	private static void StartCommandServerPolling() {
		if(ClientRegister.getInstance().getCommandExecutorEnabled()){
	        CommandQueueRegister commandQueue = new CommandQueueRegister();
	        CommandExecutor commandExecutor = new CommandExecutor();
	        ClientRegister.getInstance().getScheduler().scheduleAtFixedRate(commandQueue, 0, ClientRegister.getInstance().getCommandQueueRegisterInterval(), TimeUnit.MINUTES);
	        ClientRegister.getInstance().getScheduler().scheduleAtFixedRate(commandExecutor, 0, ClientRegister.getInstance().getCommandExecutorInterval(), TimeUnit.MINUTES);
		}
		else{
			logger.debug("Command executor not enabled - Request commmands and execute once");
			//Poll for any command at start time even it is disabled, so in case of update software it could be just at starting point
			CommandQueueRegister commandQueue = new CommandQueueRegister();
			commandQueue.run();
	        CommandExecutor commandExecutor = new CommandExecutor();
	        commandExecutor.run(); //Just start one next command pending
		}
	}
	
	private static void StartCleanerServerPolling() {
		if(ClientRegister.getInstance().getCleanerExecutorEnabled()){
	        CleanerRegister cleanerRegister = new CleanerRegister();
	        ClientRegister.getInstance().getScheduler().scheduleAtFixedRate(cleanerRegister, 0, ClientRegister.getInstance().getCleanerRegisterInterval(), TimeUnit.MINUTES);
		}
	}


	private static void StartNFCReaderThreat() {
		if(ClientRegister.getInstance().getNFCReaderEnabled()){
			if(ClientRegister.getInstance().getLCDActive()){
				LCDWrapper.init("NFC Reader Starter...");
			}
			NFCReader reader = new NFCReader();
			if(reader.init()){
				reader.start();
			}
		}
	}


	private static void StartWifiDetectionThread() {
		if(ClientRegister.getInstance().getWifiDetection()){
			sniffer = new MACDetection();
			sniffer.execute();
		}
	}


	private static void StartStatusThreat() {
		StatusRegister ipRegister = new StatusRegister();
		ScheduledFuture<?> future = ClientRegister.getInstance().getScheduler().scheduleAtFixedRate(ipRegister, 0, ClientRegister.getInstance().getSaveStatusInterval(), TimeUnit.MINUTES);
		ipRegister.setFuture(future);
	}
	
	private static void StartInternetAccessThread() {
		//check Internet & alivelights & KismetServer alive
		AliveRegister internetRegister = new AliveRegister();
		ClientRegister.getInstance().getScheduler().scheduleAtFixedRate(internetRegister, 0, 1, TimeUnit.MINUTES);
	}
	
	private static void saveInformationServerThread() {
		InformationServerRegister informationServerRegister = new InformationServerRegister();
		ClientRegister.getInstance().getScheduler().scheduleAtFixedRate(informationServerRegister, 0, 1, TimeUnit.MINUTES);
	}


	private static void registerButtonListeners() {
		ButtonListener buttonListener1 = new ButtonListener(ButtonService.getButton1(), LedService.getLed1(), 1);
		ButtonService.getButton1().addListener(buttonListener1);
		ButtonListener buttonListener2 = new ButtonListener(ButtonService.getButton2(), LedService.getLed2(), 2);
		ButtonService.getButton2().addListener(buttonListener2);
		ButtonListenerAndPowerOff buttonListener3 = new ButtonListenerAndPowerOff(ButtonService.getButton3(), LedService.getLed3(), 3);
		ButtonService.getButton3().addListener(buttonListener3);
		ButtonListenerAndSendStatus buttonListener4 = new ButtonListenerAndSendStatus(ButtonService.getButton4(), LedService.getLed4(), 4);
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
			logger.debug("InterruptedException", e);
		}
		LedService.getLed2().pulse(2000, false);
		LedService.getLed3().pulse(2000, false);
		LedService.getLed4().pulse(2000, false);
		LedService.getLed5().pulse(2000, false);
		LedService.getLed1().pulse(2000, false);
	}


	private static void InitGPIO() {
		ButtonService.setButton1(gpio.provisionDigitalInputPin(RaspiPin.GPIO_00, PinPullResistance.PULL_DOWN)); //17 
		ButtonService.getButton1().setDebounce(700);
		ButtonService.getButton1().setShutdownOptions(true);
		LedService.setLed1(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "LED1", PinState.LOW)); //27
		LedService.getLed1().setShutdownOptions(true, PinState.LOW);
		ButtonService.setButton2(gpio.provisionDigitalInputPin(RaspiPin.GPIO_21, PinPullResistance.PULL_DOWN)); //5
		ButtonService.getButton2().setDebounce(700);
		ButtonService.getButton2().setShutdownOptions(true);
		LedService.setLed2(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_22, "LED2", PinState.LOW)); //6
		LedService.getLed2().setShutdownOptions(true, PinState.LOW);
		ButtonService.setButton3(gpio.provisionDigitalInputPin(RaspiPin.GPIO_24, PinPullResistance.PULL_DOWN)); //19
		ButtonService.getButton3().setDebounce(700);
		ButtonService.getButton3().setShutdownOptions(true);
		LedService.setLed3(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_25, "LED3", PinState.LOW)); //26
		LedService.getLed3().setShutdownOptions(true, PinState.LOW);
		ButtonService.setButton4(gpio.provisionDigitalInputPin(RaspiPin.GPIO_01, PinPullResistance.PULL_DOWN)); //18
		ButtonService.getButton4().setDebounce(700);
		ButtonService.getButton4().setShutdownOptions(true);
		LedService.setLed4(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "LED4", PinState.LOW)); //23
		LedService.getLed4().setShutdownOptions(true, PinState.LOW);
		ButtonService.setButton5(gpio.provisionDigitalInputPin(RaspiPin.GPIO_27, PinPullResistance.PULL_DOWN)); //16
		ButtonService.getButton5().setDebounce(700);
		ButtonService.getButton5().setShutdownOptions(true);
		LedService.setLed5(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_28, "LED5", PinState.LOW)); //20
		LedService.getLed5().setShutdownOptions(true, PinState.LOW);
		
		LedService.setLedPower(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_23, "LEDPOWER", PinState.LOW)); //13
	}
	

}
