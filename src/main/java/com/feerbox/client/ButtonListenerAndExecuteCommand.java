package com.feerbox.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.log4j.Logger;

import com.feerbox.client.oslevel.OSExecutor;
import com.feerbox.client.registers.CommandExecutor;
import com.feerbox.client.registers.CommandQueueRegister;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;

public class ButtonListenerAndExecuteCommand extends ButtonListener {
	final static Logger logger = Logger.getLogger(ButtonListenerAndExecuteCommand.class);

	protected final OSExecutor oSExecutor;

	public ButtonListenerAndExecuteCommand(GpioPinDigitalInput button, GpioPinDigitalOutput led, int number, OSExecutor oSExecutor) {
		super(button, led, number);
		this.oSExecutor = oSExecutor;
	}

	@Override
	protected void onLongClick() {
		logger.debug("Going to retrieve command and execute it");
		Led.blink(500, 10000); // continuously blink the led every 1/2 second for 10 seconds
		updateScripts();
		CommandQueueRegister commandQueue = new CommandQueueRegister();
		commandQueue.run();
		CommandExecutor commandExecutor = new CommandExecutor(oSExecutor);
		commandExecutor.run(); //Just start one next command pending
		//Send information to server
		commandQueue = new CommandQueueRegister();
		commandQueue.run();
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
