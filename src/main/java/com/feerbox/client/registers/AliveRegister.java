package com.feerbox.client.registers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.feerbox.client.StartFeerBoxClient;
import com.feerbox.client.services.LedService;

public class AliveRegister implements Runnable {
	final static Logger logger = Logger.getLogger(AliveRegister.class);
	private static boolean firstTimeTethering = false;

	public void run() {
		try {
			boolean before = InternetAccess.getInstance().getAccess();
			checkInternetAccess();
			boolean after = InternetAccess.getInstance().getAccess();
			/*if (before != after && after == true) {
				new StatusRegister().run();
				if (ClientRegister.getInstance().getLastGetCommands() != null) {
					long diff = System.currentTimeMillis() - ClientRegister.getInstance().getLastGetCommands().getTime();
					long diffMinutes = diff / (60 * 1000) % 60;
					if (diffMinutes > ClientRegister.getInstance().getCommandQueueRegisterInterval()) {
						new CommandQueueRegister().run();
					}
				}
			}*/
			aliveLights();
			checkTetheringDetection();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	private void checkTetheringDetection() {
		if (ClientRegister.getInstance().getTetheringLightsEnabled()) {
			try {
				String ssid = "";
				Process uptimeProc = Runtime.getRuntime().exec("iwgetid -r");
				BufferedReader in = new BufferedReader(new InputStreamReader(uptimeProc.getInputStream()));
				String line = in.readLine();
				// 07:33:54 up 11 min, 1 user, load average: 1.14, 0.96, 0.55
				// 16:30:34 up 6:40, 1 user, load average: 0.01, 0.01, 0.00
				if (line != null) {
					if ((line.trim().equals("feerbox-wifi") || line.trim().equals("feerbox.com"))
							&& firstTimeTethering == false) {
						// Tethering activated
						LedService.animation();
						firstTimeTethering = true;
					} else {
						firstTimeTethering = false;
					}
				}
			} catch (IOException e) {
				logger.error("IOException", e);
			}
		}
	}

	private void checkInternetAccess() {
		HttpURLConnection urlConnect = null;
		InputStream in = null;
		try {
			// make a URL to a known source
			URL url = new URL("http://www.google.com");

			// open a connection to that source
			urlConnect = (HttpURLConnection) url.openConnection();

			// trying to retrieve data from the source. If there
			// is no connection, this line will fail
			urlConnect.setConnectTimeout(10000);
			urlConnect.setReadTimeout(10000);
			in = (InputStream) urlConnect.getContent();
			if (ClientRegister.getInstance().getInternet()) {
				// logger.debug("YES Internet connection "+new
				// SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				InternetAccess.getInstance().setAccess(true);
			} else {
				logger.debug("FORCED No Internet connection");
				InternetAccess.getInstance().setAccess(false);
			}

		} catch (UnknownHostException e) {
			if(ClientRegister.getInstance().getShowInternetConnectionError()){
				logger.error("UnknownHostException - No Internet connection: " + e.getMessage());
			}
			InternetAccess.getInstance().setAccess(false);
		} catch (IOException e) {
			logger.error("IOException - No Internet connection: " + e.getMessage());
			InternetAccess.getInstance().setAccess(false);
		}
		finally {
			try {
				if(in!=null){
					in.close();
				}
			} catch (IOException e) {
				logger.error( "IOException", e );
			}
			if(urlConnect!=null) urlConnect.disconnect();
		}
	}

	private void aliveLights() {
		if (ClientRegister.getInstance().getAliveLights()) {
			Date lastAnswer = ClientRegister.getInstance().getLastAnswerSaved();
			long seconds = (new Date().getTime() - lastAnswer.getTime()) / 1000;
			if (seconds > 120) {
				LedService.animation();
			}
		}
	}

	private void checkInternetAccessByPing() {
		try {
			Process proc = Runtime.getRuntime().exec("ping -c 2 google.com");
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

}

class StreamGobbler extends Thread {
	final static Logger logger = Logger.getLogger(StreamGobbler.class);
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