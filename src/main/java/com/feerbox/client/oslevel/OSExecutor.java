package com.feerbox.client.oslevel;

import org.apache.log4j.Logger;

import com.feerbox.client.model.Command;

public interface OSExecutor {
	final static Logger logger = Logger.getLogger(OSExecutor.class);
	
	public String executeCommand(Command command);
	public String executeCommandLine(String command);
	public void restart();
	public void checkInternetAccess();
}
