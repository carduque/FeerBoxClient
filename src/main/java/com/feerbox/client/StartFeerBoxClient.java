package com.feerbox.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.feerbox.client.registers.*;
import org.apache.log4j.Logger;

import com.feerbox.client.db.SaveAnswerError;
import com.feerbox.client.model.Command;
import com.feerbox.client.oslevel.OSExecutor;
import com.feerbox.client.oslevel.OSExecutorRaspbian;
import com.feerbox.client.oslevel.OSExecutorWindows;
import com.feerbox.client.services.ButtonService;
import com.feerbox.client.services.CommandService;
import com.feerbox.client.services.LedService;
import com.feerbox.client.services.AnswerService;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class StartFeerBoxClient {
	public static final String version = "1.7.5.0.2";
	public static String app_path = "/opt/FeerBoxClient/FeerBoxClient";
	public static MACDetection sniffer;
	public static boolean windows = ClientRegister.getInstance().getWindows();
	public static OSExecutor oSExecutor;
	final static Logger logger = Logger.getLogger(StartFeerBoxClient.class);
	
	
	private static GpioController gpio = null;
	
	public static void main(String args[]) throws InterruptedException {
        logger.debug("FeerBoxClient Started version "+ version);
        if(!windows){
        	oSExecutor = new OSExecutorRaspbian();
        	gpio = GpioFactory.getInstance();
        	try {
    			Files.write(Paths.get(app_path+"/config/"+"version.txt"), version.getBytes());
    		} catch (IOException e) {
    			logger.error("Error writing version file: "+e.getMessage());
    		}
        	InitGPIO();
            lights();
            StartLedPower();
            StartNFCReaderThreat();
            StartCounterPeoplePolling();
            StartWeatherSensorThread();
            StartWifiDetectionThread();
            //StartTempSensorThread();
			StartPresenceDetectorThread();
            registerButtonListeners();
            restartEveryDay();
            MonitorInternetConnection();
            sendConfandLastLog();
        } else {
        	oSExecutor = new OSExecutorWindows();
        	System.out.println("TEST: "+System.getProperty("TEST"));
        	System.out.println(app_path);
        }
        // create gpio controller
        StartInternetAccessThread();
        StartStatusThreat();
        cleanCommandsUnderExecution();
        saveInformationServerThread();
        StartCommandServerPolling();
        StartCleanerServerPolling();
        //checkForAlertsThread();
        logger.debug("Initialization completed");
        // keep program running until user aborts (CTRL-C)
        for (;;) {
            //Thread.sleep(500);
        }
        
        // stop all GPIO activity/threads by shutting down the GPIO controller
        // (this method will forcefully shutdown all GPIO monitoring threads and scheduled tasks)
        // gpio.shutdown();   <--- implement this method call if you wish to terminate the Pi4J GPIO controller        
    }
	
	private static void StartTempSensorThread() {
		if(ClientRegister.getInstance().getTempSensor()){
			TempSensorRegister tempSensor = new TempSensorRegister();
			tempSensor.start();
		}
	}

	private static void cleanCommandsUnderExecution() {
		CommandService.cleanUnderExecution();
		CommandService.cleanOutputSentLogs();
	}

	private static void sendConfandLastLog() {
		if(!CommandService.wasExecutedToday("cat-yesterday-log.sh")) sendYesterdayLog();
		if(!CommandService.wasExecutedToday("cat-config.sh")) sendConfiguration();
		if(!CommandService.wasExecutedToday("cat-network-config.sh")) sendNetworkConfiguration();
	}
	
	private static void sendConfiguration() {
		Command command = new Command();
		command.setTime(new Date());
		command.setCommand("cat-config.sh");
		command.setRestart(false);
		command.setStartTime(new Date());
		command.setParameter("");
		command.setOutput(executeUnsoCommand(command));
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);
		calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
		command.setFinishTime(calendar.getTime());
		if(sendUnsoCommand(command)){
			command.setUpload(true);
			command.setFinishTime(new Date());
			CommandService.save(command);
		}
		
	}
	private static void sendNetworkConfiguration() {
		Command command = new Command();
		command.setTime(new Date());
		command.setCommand("cat-network-config.sh");
		command.setRestart(false);
		command.setStartTime(new Date());
		command.setParameter("");
		command.setOutput(executeUnsoCommand(command));
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);
		calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
		command.setFinishTime(calendar.getTime());
		if(sendUnsoCommand(command)){
			command.setUpload(true);
			command.setFinishTime(new Date());
			CommandService.save(command);
		}
		
	}


	private static void sendYesterdayLog() {
		Command command = new Command();
		command.setTime(new Date());
		command.setCommand("cat-yesterday-log.sh");
		command.setRestart(false);
		command.setStartTime(new Date());
		command.setParameter("");
		command.setOutput(executeUnsoCommand(command));
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);
		calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
		command.setFinishTime(calendar.getTime());
		if(command.getOutput()!=null && !command.getOutput().equals("")){
			if(sendUnsoCommand(command)){
				command.setUpload(true);
				command.setFinishTime(new Date());
				CommandService.save(command);
			}
		}
	}


	private static String executeUnsoCommand(Command command) {
		return oSExecutor.executeCommand(command);
	}


	private static boolean sendUnsoCommand(Command command) {
		return CommandService.sendUnsoCommand(command);
	}


	private static void MonitorInternetConnection() {
		if(ClientRegister.getInstance().getMonitorInternetConnection()){
			MonitorInternetConnectionRegister restartEveryDayRegister = new MonitorInternetConnectionRegister();
			Timer timer = new Timer();
			timer.schedule(restartEveryDayRegister, new Date(), TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES)); // period: 1 minute
			restartEveryDayRegister.setTimer(timer);
		}
	}


	private static void restartEveryDay() {
		Calendar today = Calendar.getInstance();
		today.add(Calendar.DAY_OF_YEAR, 1);
		today.set(Calendar.HOUR_OF_DAY, 3);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		RestartEveryDayRegister restartEveryDayRegister = new RestartEveryDayRegister();
		Timer timer = new Timer();
		timer.schedule(restartEveryDayRegister, today.getTime(), TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)); // period: 1 day
		restartEveryDayRegister.setTimer(timer);
	}


	private static void StartWeatherSensorThread() {
		if(ClientRegister.getInstance().getWeatherSensor()){
			WeatherSensorRegister weatherSensor = new WeatherSensorRegister();
			weatherSensor.start();
		}
	}


	private static void StartCounterPeoplePolling() {
		if(ClientRegister.getInstance().getCounterPeopleEnabled()){
			CounterPeopleRegister counterPeople = new CounterPeopleRegister();
			counterPeople.start();
		}
	}

	private static void StartPresenceDetectorThread() {
		if(ClientRegister.getInstance().getPresenceDetectorEnabled()){
			PresenceDetectorRegister presenceDetectorRegister = new PresenceDetectorRegister();
			presenceDetectorRegister.start();
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
	        CommandExecutor commandExecutor = new CommandExecutor(oSExecutor);
	        ScheduledFuture<?> future = ClientRegister.getInstance().getScheduler().scheduleAtFixedRate(commandQueue, 0, ClientRegister.getInstance().getCommandQueueRegisterInterval(), TimeUnit.MINUTES);
	        ScheduledFuture<?> future2 = ClientRegister.getInstance().getScheduler().scheduleAtFixedRate(commandExecutor, 0, ClientRegister.getInstance().getCommandExecutorInterval(), TimeUnit.MINUTES);
		}
		else{
			logger.debug("Command executor not enabled - Request commmands and execute once");
			//Poll for any command at start time even it is disabled, so in case of update software it could be just at starting point
			CommandQueueRegister commandQueue = new CommandQueueRegister();
			commandQueue.run();
	        CommandExecutor commandExecutor = new CommandExecutor(oSExecutor);
	        commandExecutor.run(); //Just start one next command pending
		}
	}
	
	private static void StartCleanerServerPolling() {
		if(ClientRegister.getInstance().getCleanerExecutorEnabled()){
	        CleanerRegister cleanerRegister = new CleanerRegister();
	        ScheduledFuture<?> future = ClientRegister.getInstance().getScheduler().scheduleAtFixedRate(cleanerRegister, 0, ClientRegister.getInstance().getCleanerRegisterInterval(), TimeUnit.MINUTES);
	        //cleanerRegister.setFuture(future);
		}
	}


	private static void StartNFCReaderThreat() {
		if(ClientRegister.getInstance().getNFCReaderEnabled()){
			try{
				if(ClientRegister.getInstance().getLCDActive()){
					LCDWrapper.init("");
				}
			} catch (Throwable e) {
				logger.error("Error initialicing LCD: "+e.getMessage());
			}
			try {
				NFCReader reader = new NFCReader();
				if(reader.init()){
					reader.start();
				}
			} catch (Exception e) {
				logger.error("Error initialicing NFC Reader: "+e.getMessage());
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
		StatusRegister ipRegister = new StatusRegister(oSExecutor);
		ScheduledFuture<?> future = ClientRegister.getInstance().getScheduler().scheduleAtFixedRate(ipRegister, 0, ClientRegister.getInstance().getSaveStatusInterval(), TimeUnit.MINUTES);
		ipRegister.setFuture(future);
	}
	
	private static void StartInternetAccessThread() {
		//check Internet & alivelights & KismetServer alive
		AliveRegister internetRegister = new AliveRegister(oSExecutor);
		ScheduledFuture<?> future = ClientRegister.getInstance().getScheduler().scheduleAtFixedRate(internetRegister, 0, 1, TimeUnit.MINUTES);
		internetRegister.setFuture(future);
	}
	
	private static void saveInformationServerThread() {
		InformationServerRegister informationServerRegister = new InformationServerRegister();
		ScheduledFuture<?> future = ClientRegister.getInstance().getScheduler().scheduleAtFixedRate(informationServerRegister, 0, 1, TimeUnit.MINUTES);
		informationServerRegister.setFuture(future);
	}
	
	private static void checkForAlertsThread() {
		if(ClientRegister.getInstance().getAlertsEnabled()){
			Calendar today = Calendar.getInstance();
			today.add(Calendar.DAY_OF_YEAR, 1);
			today.set(Calendar.HOUR_OF_DAY, 0);
			today.set(Calendar.MINUTE, 0);
			today.set(Calendar.SECOND, 0);
			DataAlertsRegister dataAlertsRegister = new DataAlertsRegister(oSExecutor);
			Timer timer = new Timer();
			timer.schedule(dataAlertsRegister, today.getTime(), TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)); // period: 1 day
			dataAlertsRegister.setTimer(timer);
		}
	}


	private static void registerButtonListeners() {
		ButtonListenerAndExecuteCommand buttonListener1 = new ButtonListenerAndExecuteCommand(ButtonService.getButton1(), LedService.getLed1(), 1, oSExecutor);
		ButtonService.getButton1().addListener(buttonListener1);
		ButtonListener buttonListener2 = new ButtonListener(ButtonService.getButton2(), LedService.getLed2(), 2);
		ButtonService.getButton2().addListener(buttonListener2);
		ButtonListenerAndPowerOff buttonListener3 = new ButtonListenerAndPowerOff(ButtonService.getButton3(), LedService.getLed3(), 3);
		ButtonService.getButton3().addListener(buttonListener3);
		ButtonListenerAndSendStatus buttonListener4 = new ButtonListenerAndSendStatus(ButtonService.getButton4(), LedService.getLed4(), 4, oSExecutor);
		ButtonService.getButton4().addListener(buttonListener4);
		//ButtonListener buttonListener5 = new ButtonListener(ButtonService.getButton5(), LedService.getLed5(), 5);
		ButtonListenerAndReboot buttonListener5 = new ButtonListenerAndReboot(ButtonService.getButton5(), LedService.getLed5(), 5);
		ButtonService.getButton5().addListener(buttonListener5);
		ButtonListener buttonListener6 = new ButtonListener(ButtonService.getButton6(), LedService.getLed6(), 6);
		ButtonService.getButton6().addListener(buttonListener6);
		ButtonListener buttonListener7 = new ButtonListener(ButtonService.getButton7(), LedService.getLed7(), 7);
		ButtonService.getButton7().addListener(buttonListener7);
	}


	private static void lights() {
		LedService.getLed1().pulse(500, true);
		LedService.getLed2().pulse(500, true);
		LedService.getLed3().pulse(500, true);
		LedService.getLed4().pulse(500, true);
		LedService.getLed5().pulse(500, true);
		LedService.getLed6().pulse(500, true);
		LedService.getLed7().pulse(500, true);
		try {
			AnswerService.tryConnection();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				logger.debug("InterruptedException", e);
			}
			LedService.getLed1().pulse(2000, false);
			LedService.getLed2().pulse(2000, false);
			LedService.getLed3().pulse(2000, false);
			LedService.getLed4().pulse(2000, false);
			LedService.getLed5().pulse(2000, false);
			LedService.getLed6().pulse(2000, false);
			LedService.getLed7().pulse(2000, false);
		} catch (SaveAnswerError e) {
			LedService.getLed3().pulse(2000, false);
			LedService.getLed4().pulse(2000, false);
		}
		
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
		
		//HOME
		ButtonService.setButton6(gpio.provisionDigitalInputPin(RaspiPin.GPIO_13, PinPullResistance.PULL_DOWN)); //21 
		ButtonService.getButton6().setDebounce(700);
		ButtonService.getButton6().setShutdownOptions(true);
		LedService.setLed6(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_14, "LED6", PinState.LOW)); //23
		LedService.getLed6().setShutdownOptions(true, PinState.LOW);
		//DONA
		ButtonService.setButton7(gpio.provisionDigitalInputPin(RaspiPin.GPIO_10, PinPullResistance.PULL_DOWN)); //24 
		ButtonService.getButton7().setDebounce(700);
		ButtonService.getButton7().setShutdownOptions(true);
		LedService.setLed7(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_11, "LED7", PinState.LOW)); //26
		LedService.getLed7().setShutdownOptions(true, PinState.LOW);
	}
	

}
