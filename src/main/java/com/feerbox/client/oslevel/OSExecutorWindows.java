package com.feerbox.client.oslevel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.feerbox.client.model.Command;
import com.feerbox.client.registers.ClientRegister;
import com.feerbox.client.registers.InternetAccess;

public class OSExecutorWindows implements OSExecutor {

	@Override
	public String executeCommand(Command command) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String executeCommandLine(String command) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void restart() {
		// TODO Auto-generated method stub

	}

	@Override
	public void checkInternetAccess() {
		try {
			Process proc = Runtime.getRuntime().exec("ping -n 2 google.com");
			StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT");
			outputGobbler.start();
			proc.waitFor();
			if (ClientRegister.getInstance().getInternet()) {
				InternetAccess.getInstance().setAccess(checkAvailability(outputGobbler.getOutputLines()));
			} else {
				logger.debug("FORCED No Internet connection");
				InternetAccess.getInstance().setAccess(false);
			}

		} catch (IOException | InterruptedException ex) {
			logger.error("IOException | InterruptedException: " + ex.getMessage());
		}
	}
	
	private static boolean checkAvailability(List<String> outputLines) {

		for (String line : outputLines) {
			if (line.contains("unreachable")) {
				return false;
			}
			if (line.contains("TTL=")) {
				return true;
			}
		}
		return false;

	}
	
	class StreamGobbler extends Thread {
		final Logger logger = Logger.getLogger(StreamGobbler.class);
		protected InputStream is;
		protected String type;
		protected List<String> outputLines;

		StreamGobbler(InputStream is, String type) {
			this.is = is;
			this.type = type;
			outputLines = new ArrayList<>();
		}

		public List<String> getOutputLines() {
			return outputLines;
		}

		@Override
		public void run() {
			try {
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line;
				while ((line = br.readLine()) != null) {
					outputLines.add(line);
				}
			} catch (IOException ex) {
				logger.error("IOException | InterruptedException: " + ex.getMessage());
			}

		}
	}

}
