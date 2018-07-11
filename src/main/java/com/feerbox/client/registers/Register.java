package com.feerbox.client.registers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.feerbox.client.oslevel.OSExecutor;

public abstract class Register implements Runnable {
	final static Logger logger = Logger.getLogger(Register.class);
	protected final OSExecutor oSExecutor;
	
	public Register(OSExecutor oSExecutor) {
		this.oSExecutor = oSExecutor;
	}
	abstract public void run();
	
	public String executeCommandLine(String command) {
		return oSExecutor.executeCommandLine(command);
	}

}
