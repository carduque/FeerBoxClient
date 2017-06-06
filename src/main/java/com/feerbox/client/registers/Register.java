package com.feerbox.client.registers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public abstract class Register implements Runnable {
	final static Logger logger = Logger.getLogger(Register.class);

	abstract public void run();
	
	protected String executeCommandLine(String command) {
		String out = "";
		BufferedReader in = null;
		Process proc = null;
		try {
			String[] cmd = { "/bin/sh", "-c", command };
			proc = Runtime.getRuntime().exec(cmd);
			in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			String line = null;
			 while ((line = in.readLine()) != null) {
				out += line;
			}
		} catch (IOException e) {
			logger.error("Error executing command: "+e.getMessage());
		}
		finally{
			if(in!=null){
				try {
					in.close();
				} catch (IOException e) {
					logger.error("Error executing command: "+e.getMessage());
				}
			}
			try {
				if(!proc.waitFor(1, TimeUnit.MINUTES)) {
				    //timeout - kill the process. 
				    proc.destroyForcibly();
				}
			} catch (InterruptedException e) {
				logger.error("Error executing command: "+e.getMessage());
			}
			
		}
        return out;
	}

}
