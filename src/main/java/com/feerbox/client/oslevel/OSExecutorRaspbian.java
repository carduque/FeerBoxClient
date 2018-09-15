package com.feerbox.client.oslevel;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.feerbox.client.model.Command;
import com.feerbox.client.registers.ClientRegister;
import com.feerbox.client.registers.InternetAccess;

public class OSExecutorRaspbian implements OSExecutor {
	
	
	@Override
	public String executeCommand(Command command) {
		List<String> parameters = new ArrayList<String>();
		parameters.add("/bin/bash");
		parameters.add(command.getCommand());
		if(command.getParameter()!=null){
			String[] parametersArray = command.getParameter().split("\\s+");
			parameters.addAll(Arrays.asList(parametersArray));
		}
		ProcessBuilder pb = new ProcessBuilder(parameters);
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
			return builder.toString();
		} catch (IOException e) {
			logger.error("IOException", e);
		}
		return null;
	}
	
	@Override
	public void restart() {
		try{
			ProcessBuilder reboot = new ProcessBuilder("/bin/bash", "restart.sh");
			reboot.directory(new File("/opt/FeerBoxClient/FeerBoxClient/scripts"));
			logger.info("System is going to restart");
			reboot.start();
		} catch (IOException e) {
			logger.error("IOException", e);
		}
	}

	@Override
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
			logger.error("Error executing command: "+e.getMessage(), e);
		}
		finally{
			if(in!=null){
				try {
					in.close();
				} catch (IOException e) {
					logger.error("Error executing command: "+e.getMessage(), e);
				}
			}
			try {
				if(!proc.waitFor(1, TimeUnit.MINUTES)) {
				    //timeout - kill the process. 
				    proc.destroyForcibly();
				}
			} catch (InterruptedException e) {
				logger.error("Error executing command: "+e.getMessage(), e);
			}
			
		}
        return out;
	}

	@Override
	public void checkInternetAccess() {
		String line = executeCommandLine("ping -c 2 google.com | grep received | awk '{print $6}'");
		if(line!=null && "0%".equals(line)){
			if (ClientRegister.getInstance().getInternet()) {
				InternetAccess.getInstance().setAccess(true);
			} else {
				logger.debug("FORCED No Internet connection");
				InternetAccess.getInstance().setAccess(false);
			}
		} else {
			InternetAccess.getInstance().setAccess(false);
			if(ClientRegister.getInstance().getUSB3G()){
				String line2 = executeCommandLine("sudo systemctl restart networking");
				if(line2!=null && "".equals(line)){
					logger.info("All interfaces restarted succesfully");
				} else {
					logger.warn("All interfaces was already up or there is an issue with it: "+line2);
				}
			}
		}
	}

}
