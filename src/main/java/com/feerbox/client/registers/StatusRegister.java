package com.feerbox.client.registers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.feerbox.client.StartFeerBoxClient;
import com.feerbox.client.db.ReadAnswer;
import com.feerbox.client.model.Status;
import com.feerbox.client.services.StatusService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class StatusRegister implements Runnable {
	private boolean checkStatusTime = true;
	private String ip = "";
	private ScheduledFuture<?> future;
	private int interval = ClientRegister.getInstance().getSaveStatusInterval();
	final static Logger logger = Logger.getLogger(StatusRegister.class);
	private static Date lastStatusTime = null;
	
	public StatusRegister(){
		super();
	}
	public StatusRegister(boolean checkStatusTime){
		super();
		this.checkStatusTime = checkStatusTime;
	}
	public void run() {
		try{
			Status status = new Status();
			OutputStream os = null;
			HttpURLConnection conn = null;
			try {
				logger.debug("Going to update status for "+ClientRegister.getInstance().getReference());
				status.setReference(ClientRegister.getInstance().getReference());
				//logger.debug("Status1");
				HashMap<String, String> info = new HashMap<String, String>();
				info.put(Status.infoKeys.INTERNET.name(), getInternetStatus());
				//logger.debug("Status2");
				info.put(Status.infoKeys.IP.name(), getIp());
				//logger.debug("Status3");
				info.put(Status.infoKeys.SW_VERSION.name(), getSoftwareVersion());
				//logger.debug("Status4");
				info.put(Status.infoKeys.LAST_ANSWER.name(), getLastAnswerTime());
				//logger.debug("Status5");
				info.put(Status.infoKeys.TIME_UP.name(), getTimeSystemUp());
				//logger.debug("Status6");
				info.put(Status.infoKeys.SYSTEM_TIME.name(), getSystemTime());
				info.put(Status.infoKeys.CommandExecutor.name(), getLastCommandExecutor());
				info.put(Status.infoKeys.CommandQueue.name(), getLastGetCommands());
				info.put(Status.infoKeys.PendingAnswersToUpload.name(), getPendingAnswersToUpload());
				//logger.debug("Status7");
				status.setInfo(info);
				
				//Save to Internet
				URL myURL = new URL(ClientRegister.getInstance().getEnvironment()+"/status/add");
				conn = (HttpURLConnection) myURL.openConnection();
				conn.setRequestProperty("Content-Length", "1000");
				conn.setRequestProperty("Content-Type", "application/json");
				conn.setDoOutput(true);
				conn.setRequestMethod("POST");
				//String json = "{\"button\":\""+answer.getButton()+"\",\"reference\":\""+answer.getReference()+"\", \"time\":\""+answer.getTimeText()+"\"}";
				JsonObject json = statusToJson(status);
				
				os = conn.getOutputStream();
				os.write(json.toString().getBytes());
				os.flush();
	
				if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
					logger.info("Failed to send status: HTTP error code : "+ conn.getResponseCode());
					status.setUpload(0);
				}
				else{
					status.setUpload(1);
					logger.debug("Status updated on server succesfully");
				}
				
			} 
			catch (UnknownHostException e) {
				if(ClientRegister.getInstance().getShowInternetConnectionError()){
					logger.error("UnknownHostException - No Internet connection: " + e.getMessage());
				}
			}
			catch (SocketException e) {
				logger.error( "SocketException", e );
			} catch (IOException e) {
				logger.error( "IOException", e );
			} catch (InterruptedException e) {
				logger.error( "InterruptedException", e );
			}
			finally {
				try {
					if(os!=null){
						os.close();
					}
				} catch (IOException e) {
					logger.error( "IOException", e );
				}
				if(conn!=null) conn.disconnect();
			}
			changeDelay();
			//Save locally
			if(ClientRegister.getInstance().getSaveStatusLocally()){
				StatusService.save(status);
			}
			checkStatusTime();
		}catch(Throwable  t){
			logger.error("Exception in StatusRegister");
		}
	}

	private String getPendingAnswersToUpload() {
		int total = 0;
		try {
			total = ReadAnswer.countAnswersNotUploaded();
		} catch (Exception e) {
			logger.error("Error counting Pending Answers to upload: "+e.getMessage());
		}
		return ""+total;
	}
	private String getLastCommandExecutor() {
		String out = "false";
		try {
			if(ClientRegister.getInstance().getCommandExecutorEnabled()){
				if(ClientRegister.getInstance().getLastExecuteCommand()!=null){
					Calendar cal = Calendar.getInstance();
					cal.setTime(ClientRegister.getInstance().getLastExecuteCommand());
					cal.add(Calendar.MINUTE, ClientRegister.getInstance().getCommandExecutorInterval());
					DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
					out = df.format(cal.getTime());
				}
				else{
					out="true";
				}
			}
			
		} catch (Exception e) {
			logger.error("getLastCommandExecutor: "+e.getMessage());
		}
		return out;
	}

	private String getLastGetCommands() {
		String out = "false";
		try {
			if(ClientRegister.getInstance().getCommandExecutorEnabled()){
				if(ClientRegister.getInstance().getLastGetCommands()!=null){
					Calendar cal = Calendar.getInstance();
					cal.setTime(ClientRegister.getInstance().getLastGetCommands());
					cal.add(Calendar.MINUTE, ClientRegister.getInstance().getCommandQueueRegisterInterval());
					DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
					out = df.format(cal.getTime());
				}
			}
			
		} catch (Exception e) {
			logger.error("getLastGetCommands: "+e.getMessage());
		}
		return out;
	}

	private void checkStatusTime() {
		if(checkStatusTime){
			if(lastStatusTime==null){
				lastStatusTime = new Date();
			}
			else{
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
				Date now = new Date();
				long interval = now.getTime() - lastStatusTime.getTime();
				int buffer = 10 * 60 * 1000; //10 minutes
				int statusInterval = ClientRegister.getInstance().getSaveStatusInterval() * 60 * 1000;
				if(interval>statusInterval+buffer){
					logger.error("Time ERROR - Time moved forward. Now: "+ df.format(now)+" - LastStatus: "+ df.format(lastStatusTime));
				}
				else if(interval<statusInterval-buffer){
					logger.error("Time ERROR - Time moved backward. Now: "+ df.format(now)+" - LastStatus: "+ df.format(lastStatusTime));
				}
				lastStatusTime = now;
			}
		}
	}

	private void changeDelay() {
		if(ClientRegister.getInstance().getSaveStatusInterval()!=this.interval){
			boolean res = future.cancel(false);
	        logger.info("Previous StatusRegister cancelled: " + res);
	        StatusRegister ipRegister = new StatusRegister();
	        future = ClientRegister.getInstance().getScheduler().scheduleAtFixedRate(ipRegister, ClientRegister.getInstance().getSaveStatusInterval(), ClientRegister.getInstance().getSaveStatusInterval(), TimeUnit.MINUTES);
	        this.interval = ClientRegister.getInstance().getSaveStatusInterval();
		}
		
	}

	private String getIp() throws SocketException, InterruptedException {
		if(this.ip!=null && this.ip.equals("")){
			getInternetStatus();
		}
		return this.ip;
	}

	private String getSystemTime() {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
		//logger.debug("getSystemTime:"+df.format(new Date(System.currentTimeMillis()))+" - Timezone: "+df.getTimeZone());
		return df.format(new Date(System.currentTimeMillis()));
	}

	private String getTimeSystemUp() throws IOException {
		String uptime = "";
		Process uptimeProc = Runtime.getRuntime().exec("uptime");
        BufferedReader in = new BufferedReader(new InputStreamReader(uptimeProc.getInputStream()));
        String line = in.readLine();
        //07:33:54 up 11 min,  1 user,  load average: 1.14, 0.96, 0.55
        //16:30:34 up  6:40,  1 user,  load average: 0.01, 0.01, 0.00
        if (line != null) {
        	//logger.debug(line);
            Pattern parse = Pattern.compile("((\\d+) days,)? (\\d+):(\\d+)");
            Matcher matcher = parse.matcher(line);
            if (matcher.find()) {
                String _days = matcher.group(2);
                String _hours = matcher.group(3);
                String _minutes = matcher.group(4);
                int days = _days != null ? Integer.parseInt(_days) : 0;
                int hours = _hours != null ? Integer.parseInt(_hours) : 0;
                int minutes = _minutes != null ? Integer.parseInt(_minutes) : 0;
                //uptime = (minutes * 60000) + (hours * 60000 * 60) + (days * 6000 * 60 * 24);
                uptime = days + "d - "+hours+"h - "+minutes+"m";
            }
        }
		return uptime;
	}

	private String getLastAnswerTime() {
		String out = "";
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
		Date last = ClientRegister.getInstance().getLastAnswerSaved();
		if(last!=null){
			out = df.format(last);
		}
		return out;
	}

	private String getSoftwareVersion() {
		return StartFeerBoxClient.version;
	}

	private String getInternetStatus() throws SocketException, InterruptedException {
		//logger.debug("going to check wifi conection");
		String out = "false";
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		while (interfaces.hasMoreElements()) {
		    NetworkInterface iface = interfaces.nextElement();
		    // filters out 127.0.0.1 and inactive interfaces
		    if (iface.isLoopback() || !iface.isUp())
		        continue;

		    Enumeration<InetAddress> addresses = iface.getInetAddresses();
		    while(addresses.hasMoreElements()) {
		        InetAddress addr = addresses.nextElement();
		        //logger.debug(iface.getName());
		        if(iface.getName().toUpperCase().contains("WLAN0")){
		        	this.ip = addr.getHostAddress();
		        	out = "true";
		        }
		    }
		}
		if("true".equals(out) && !ClientRegister.getInstance().getInternet()){
			logger.info("WLAN detected, but no internet on ClientRegister");
		}
		return out;
	}
	
	protected JsonObject statusToJson(Status status) {
		JsonObject json = new JsonObject();
		json.addProperty("reference", status.getReference());
		//JsonElement element = new JsonParser().parse(new Gson().toJson(status.getInfo()));
		JsonArray array = new JsonArray();
		for(String key : status.getInfo().keySet()){
			JsonObject element = new JsonObject();
			element.add(key, new JsonPrimitive(status.getInfo().get(key)));
			array.add(element);
		}
		json.add("info", array);
		return json;
	}

	public void setFuture(ScheduledFuture<?> future) {
		this.future = future;
	}
	
}
