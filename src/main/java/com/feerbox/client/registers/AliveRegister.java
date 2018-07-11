package com.feerbox.client.registers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;

import org.apache.log4j.Logger;

import com.feerbox.client.oslevel.OSExecutor;
import com.feerbox.client.services.LedService;

public class AliveRegister extends Register {
	final static Logger logger = Logger.getLogger(AliveRegister.class);
	private static boolean firstTimeTethering = false;
	private ScheduledFuture<?> future;

	public AliveRegister(OSExecutor oSExecutor) {
		super(oSExecutor);
	}
	
	public void run() {
		try {
			boolean before = InternetAccess.getInstance().getAccess();
			oSExecutor.checkInternetAccess();
			boolean after = InternetAccess.getInstance().getAccess();
			if (before != after && after == true) {
				StatusRegister status = new StatusRegister(this.oSExecutor);
				status.run();
				/*if (ClientRegister.getInstance().getLastGetCommands() != null) {
					long diff = System.currentTimeMillis() - ClientRegister.getInstance().getLastGetCommands().getTime();
					long diffMinutes = diff / (60 * 1000) % 60;
					if (diffMinutes > ClientRegister.getInstance().getCommandQueueRegisterInterval()) {
						new CommandQueueRegister().run();
					}
				}*/
			}
			aliveLights();
			checkTetheringDetection();
		} catch (Throwable  t) {
			logger.error("Error at AliveRegister");
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
					if ((line.trim().equals("feerbox-wifi") || line.trim().equals("feerbox.com")) && firstTimeTethering == false) {
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

	private void aliveLights() {
		if (ClientRegister.getInstance().getAliveLights()) {
			Date lastAnswer = ClientRegister.getInstance().getLastAnswerSaved();
			long seconds = (new Date().getTime() - lastAnswer.getTime()) / 1000;
			if (seconds > 120) {
				LedService.animation();
			}
		}
	}

	public ScheduledFuture<?> getFuture() {
		return future;
	}

	public void setFuture(ScheduledFuture<?> future) {
		this.future = future;
	}
}
	