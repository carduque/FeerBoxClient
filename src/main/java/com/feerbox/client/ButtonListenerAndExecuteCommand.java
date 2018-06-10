package com.feerbox.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import org.apache.log4j.Logger;

import com.feerbox.client.db.SaveCommand;
import com.feerbox.client.model.Command;
import com.feerbox.client.registers.CommandExecutor;
import com.feerbox.client.registers.CommandQueueRegister;
import com.feerbox.client.services.AnswerService;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class ButtonListenerAndExecuteCommand implements GpioPinListenerDigital {
	
	private GpioPinDigitalInput Button = null;
	private GpioPinDigitalOutput Led = null;
	private int buttonNumber = 0;
	private Date exactTime = null;
	private PinState lastState = PinState.HIGH;
	final static Logger logger = Logger.getLogger(ButtonListenerAndExecuteCommand.class);

	public ButtonListenerAndExecuteCommand(GpioPinDigitalInput button, GpioPinDigitalOutput led, int number) {
		Button = button;
		Led = led;
		buttonNumber = number;
	}

	public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
		// display pin state on console
        //logger.debug(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
        if(event.getState().equals(PinState.LOW) && lastState==PinState.HIGH){
        	this.lastState = PinState.LOW;
        	//logger.debug("LOW");
        	if(exactTime!=null){
        		long seconds = (new Date().getTime()-exactTime.getTime())/1000;  //5 seconds
        		if(seconds>10){
        			logger.debug("Going to retrieve command and execute it");
        			Led.blink(500, 10000); // continuously blink the led every 1/2 second for 10 seconds
        			updateScripts();
        			CommandQueueRegister commandQueue = new CommandQueueRegister();
        			commandQueue.run();
        	        CommandExecutor commandExecutor = new CommandExecutor();
        	        commandExecutor.run(); //Just start one next command pending
        	        //Send information to server
        	        commandQueue = new CommandQueueRegister();
        			commandQueue.run();
        		}
        		else{
                    AnswerService.saveAnswer(buttonNumber);
        		}
        	}
		}
        if(event.getState().equals(PinState.HIGH)){
        	this.exactTime = new Date();
        	this.lastState = PinState.HIGH;
        	//We would like to light led when push
        	Led.pulse(1000, true); // set second argument to 'true' use a blocking call
        }
	}

	private void updateScripts() {
		ProcessBuilder pb = new ProcessBuilder("/bin/bash", "update-scripts.sh", "");
		/*Map<String, String> env = pb.environment();
		env.put("VAR1", "myValue");
		env.remove("OTHERVAR");
		env.put("VAR2", env.get("VAR1") + "suffix");*/
		pb.directory(new File("/opt/FeerBoxClient/FeerBoxClient/scripts"));
		try {
			Process process = pb.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			StringBuilder builder = new StringBuilder();
			String line = null;
			while ( (line = reader.readLine()) != null) {
			   builder.append(line);
			   builder.append(System.getProperty("line.separator"));
			}
			logger.info(builder.toString());
			logger.debug("Scripts updated succesfully");
		} catch (IOException e) {
			logger.error("IOException", e);
		}
	}

}
