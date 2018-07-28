package com.feerbox.client.registers;

import java.io.IOException;
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
import com.feerbox.client.db.ReadCounterPeople;
import com.feerbox.client.db.ReadWeather;
import com.feerbox.client.db.SaveAlert;
import com.feerbox.client.model.Alert;
import com.feerbox.client.model.Answer;
import com.feerbox.client.model.CounterPeople;
import com.feerbox.client.model.Status;
import com.feerbox.client.model.Weather;
import com.feerbox.client.oslevel.OSExecutor;
import com.feerbox.client.services.AnswerService;
import com.feerbox.client.services.CounterPeopleService;
import com.feerbox.client.services.StatusService;
import com.feerbox.client.services.WeatherService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class StatusRegister extends Register {
	private boolean checkStatusTime = true;
	private String ip = "";
	private ScheduledFuture<?> future;
	private int interval = ClientRegister.getInstance().getSaveStatusInterval();
	final static Logger logger = Logger.getLogger(StatusRegister.class);
	private static Date lastStatusTime = null;
	
	public StatusRegister(OSExecutor oSExecutor) {
		super(oSExecutor);
	}
	
	public void run() {
		try{
			Status status = new Status();
			OutputStream os = null;
			HttpURLConnection conn = null;
			try {
				logger.debug("Going to update status for "+ClientRegister.getInstance().getReference());
				status.setReference(ClientRegister.getInstance().getReference());
				HashMap<String, String> info = new HashMap<String, String>();
				info.put(Status.infoKeys.INTERNET.name(), getInternetStatusPing());
				info.put(Status.infoKeys.IP.name(), getIp());
				info.put(Status.infoKeys.SW_VERSION.name(), getSoftwareVersion());
				info.put(Status.infoKeys.LAST_ANSWER.name(), getLastAnswerTime());
				info.put(Status.infoKeys.LAST_CP.name(), getLastCPime());
				info.put(Status.infoKeys.LAST_WEATHER.name(), getLastWeatherTime());
				info.put(Status.infoKeys.TIME_UP.name(), getTimeSystemUp());
				info.put(Status.infoKeys.SYSTEM_TIME.name(), getSystemTime());
				info.put(Status.infoKeys.CommandExecutor.name(), getLastCommandExecutor());
				info.put(Status.infoKeys.CommandQueue.name(), getLastGetCommands());
				info.put(Status.infoKeys.CPU.name(), getCPU());
				info.put(Status.infoKeys.FreeMemory.name(), getFreeMemory());
				info.put(Status.infoKeys.MemoryProcess.name(), getMemoryProcess());
				info.put(Status.infoKeys.JavaMemory.name(), getJavaMemory());
				//info.put(Status.infoKeys.AverageUptime.name(), getAverageUptime());
				info.put(Status.infoKeys.PendingAnswersToUpload.name(), getPendingAnswersToUpload());
				info.put(Status.infoKeys.PendingCPToUpload.name(), getPendingCP());
				info.put(Status.infoKeys.PendingWeatherToUpload.name(), getPendingWeather());
				info.put(Status.infoKeys.FreeDiskSpace.name(), getFreeDiskSpace());
				info.put(Status.infoKeys.SSID_CONNECTED.name(), getSSID());
				info.put(Status.infoKeys.OS_KERNEL_Version.name(), getRaspbianKernelVersion());
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
			//checkStatusTime();
		}catch(Throwable  t){
			logger.error("Exception in StatusRegister", t);
		}
	}
	
	private String getRaspbianKernelVersion() {
		String out = executeCommandLine("awk -F= '$1==\"PRETTY_NAME\" { print $2 ;}' /etc/os-release");
		out += " - kernel: " + executeCommandLine("uname -r");
		return out;
	}
	private String getSSID() {
		return executeCommandLine("iwgetid -r");
	}
	private String getFreeDiskSpace() {
		return executeCommandLine("df -P / | awk '/%/ {print 100 -$5 \"%\"}'");
	}
	private String getLastWeatherTime() {
		String out = "";
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
		Weather weather = WeatherService.getLastSaved();
		if(weather!=null){
			Date last = weather.getTime();
			if(last!=null) out = df.format(last);
		}
		return out;
	}
	private String getLastCPime() {
		String out = "";
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
		CounterPeople counterpeople = CounterPeopleService.getLastSaved();
		if(counterpeople!=null && counterpeople.getTime()!=null){
			out = df.format(counterpeople.getTime());
		}
		return out;
	}
	private String getPendingWeather() {
		return WeatherService.notUploadedTotal()+"";
	}
	private String getPendingCP() {
		return CounterPeopleService.notUploadedTotal()+"";
	}
	private String getAverageUptime() {
		return executeCommandLine("sudo tuptime | grep \"Average uptime:\" | awk '{print $3\" \"$4\" \"$5\" \"$6\" \"$7\" \"$8\" \"$9\" \"$10\" \"$11}'");
	}
	private String getFreeMemory() {
		return executeCommandLine("free -m | awk '/Mem:/ { total=$2 } /buffers\\/cache/ { free=$4 } END { print free/total*100}'");
	}

	
	private String getJavaMemory() {
		try {
			return  Runtime.getRuntime().totalMemory()+" - "+Runtime.getRuntime().freeMemory()+ " - "+Runtime.getRuntime().maxMemory();
		} catch (Exception e) {
			logger.error("Error getting memory from Runtime: "+e.getMessage());
		}
		return "error";
	}
	private String getMemoryProcess() {
		return executeCommandLine(" top -bn1 | grep \"java\" | awk '{print $10\"%\"}'");
	}
	private String getCPU() {
		return executeCommandLine("grep 'cpu ' /proc/stat | awk '{usage=($2+$4)*100/($2+$4+$5)} END {print usage \"%\"}'");
		
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
				lastStatusTime = StatusService.getLastStatusTime();
			}
			if(lastStatusTime!=null){
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
				Date now = new Date();
				long interval = now.getTime() - lastStatusTime.getTime();
				int buffer = 10 * 60 * 1000; //10 minutes
				int statusInterval = ClientRegister.getInstance().getSaveStatusInterval() * 60 * 1000;
				if(interval>statusInterval+buffer){
					String error_message = "Time ERROR - Time moved forward. Now: "+ df.format(now)+" - LastStatus: "+ df.format(lastStatusTime);
					logger.error(error_message);
					//generateAlert(error_message);
				}
				else if(interval<statusInterval-buffer){
					String error_message = "Time ERROR - Time moved backward. Now: "+ df.format(now)+" - LastStatus: "+ df.format(lastStatusTime);
					logger.error(error_message);
					//generateAlert(error_message);
				} else {
					//DisableAlertIfExists();
				}
				lastStatusTime = now;
			}
		}
	}

	private void generateAlert(String error_message) {
		Alert alert = new Alert();
		alert.setSeverity(Alert.AlertSeverity.HIGH);
		alert.setGenerator(Alert.AlertGenerator.StatusRegister);
		alert.setThreshold(10 * 60 * 1000L); //10 minutes
		alert.setName(error_message);
		alert.setReference(ClientRegister.getInstance().getReference());
		alert.setTime(new Date());
		alert.setType(Alert.AlertType.TIME);
		alert.setActive(true);
		alert.setUpload(0);
		alert.setReference(ClientRegister.getInstance().getReference());
		SaveAlert.save(alert);
	}
	private void changeDelay() {
		if(ClientRegister.getInstance().getSaveStatusInterval()!=this.interval){
			boolean res = future.cancel(false);
	        logger.info("Previous StatusRegister cancelled: " + res);
	        StatusRegister ipRegister = new StatusRegister(this.oSExecutor);
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
		String line =  executeCommandLine("uptime -s");
        //07:33:54 up 11 min,  1 user,  load average: 1.14, 0.96, 0.55
        //16:30:34 up  6:40,  1 user,  load average: 0.01, 0.01, 0.00
        if (line != null) {
        	//logger.debug(line);
            /*Pattern parse = Pattern.compile("((\\d+) days,)? (\\d+):(\\d+)");
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
            }*/
        	uptime=line;
        }
		return uptime;
	}
	
	private String getTimeSystemUp2() throws IOException {
		String uptime = null;
		String line =  executeCommandLine("command -v tuptime");
		if(line!=null && !"".equals(line)){
			uptime = executeCommandLine("sudo tuptime | grep \"System uptime:\" | awk '{print $3\"%\"}'");
		}
		else{
			uptime = getTimeSystemUp();
		}
		return  uptime;
	}

	private String getLastAnswerTime() {
		String out = "";
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
		Answer answer = AnswerService.getLastSaved();
		if(answer!=null && answer.getTime()!=null){
			out = df.format(answer.getTime());
		}
		return out;
	}

	private String getSoftwareVersion() {
		return StartFeerBoxClient.version;
	}
	
	private String getInternetStatusPing(){
		String line = executeCommandLine("ping -c 2 google.com | grep received | awk '{print $6}'");
		if(line!=null && "0%".equals(line)){
			return "true";
		}
		return "false";
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
		        if(ClientRegister.getInstance().getUSB3G()){
			        if((this.ip==null || "".equals(this.ip.trim())) && iface.getName().toUpperCase().contains("PPP0")){
			        	this.ip = addr.getHostAddress();
			        	out = "true";
			        }
			        if((this.ip==null || "".equals(this.ip.trim())) && iface.getName().toUpperCase().contains("ETH1")){
			        	if(addr.getHostAddress()!=null && addr.getHostAddress().matches("\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b"))
			        	this.ip = addr.getHostAddress();
			        	out = "true";
			        }
		        }
		    }
		}
		if("true".equals(out) && !ClientRegister.getInstance().getInternet()){
			logger.info("WLAN-GPRS detected, but no internet on ClientRegister");
		}
		return out;
	}
	
	protected JsonObject statusToJson(Status status) {
		JsonObject json = new JsonObject();
		json.addProperty("reference", status.getReference());
		//JsonElement element = new JsonParser().parse(new Gson().toJson(status.getInfo()));
		JsonArray array = new JsonArray();
		for(String key : status.getInfo().keySet()){
			if(status.getInfo().get(key)!=null){
				JsonObject element = new JsonObject();
				element.add(key, new JsonPrimitive(status.getInfo().get(key)));
				array.add(element);
			}
		}
		json.add("info", array);
		return json;
	}

	public void setFuture(ScheduledFuture<?> future) {
		this.future = future;
	}
	
}
