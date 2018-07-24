package com.feerbox.client.registers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public class MonitorInternetConnectionRegister extends TimerTask {
	private Logger logger = Logger.getLogger(MonitorInternetConnectionRegister.class);
	private Timer timer;
	
	@Override
	public void run() {
		if(checkInternetAccessByPing()){
			logger.debug("Internet connection ALIVE");
		}
		else{
			logger.warn("Internet connection NOT ALIVE");
			//TODO maybe restart all connections?
			//service networking restart
			String line = executeCommandLine("sudo systemctl restart networking");
			if(line!=null && "".equals(line)){
				logger.info("All interfaces restarted succesfully");
			} else {
				logger.info("All interfaces were already up or there is an issue with it: "+line);
			}
		}
	}
	
	private boolean checkInternetAccessByPing(){
		String line = executeCommandLine("ping -c 2 google.com | grep received | awk '{print $6}'");
		if(line!=null && "0%".equals(line)){
			return true;
		} else {
			return false;
		}
	}
	
	public String executeCommandLine(String command) {
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

	public Timer getTimer() {
		return timer;
	}

	public void setTimer(Timer timer) {
		this.timer = timer;
	}

}
