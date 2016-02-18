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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.feerbox.client.StartFeerBoxClient;
import com.feerbox.client.model.Status;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class StatusRegister implements Runnable {
	private static final String FEERBOX_SERVER_URL = ClientRegister.getInstance().getEnvironment();
	private String ip = "";

	public void run() {
		try {
			System.out.println("Going to update status for "+ClientRegister.getInstance().getReference());
			Status status = new Status();
			status.setReference(ClientRegister.getInstance().getReference());
			HashMap<String, String> info = new HashMap<String, String>();
			info.put(Status.infoKeys.INTERNET.name(), getInternetStatus());
			info.put(Status.infoKeys.IP.name(), getIp());
			info.put(Status.infoKeys.SW_VERSION.name(), getSoftwareVersion());
			info.put(Status.infoKeys.LAST_ANSWER.name(), getLastAnswerTime());
			info.put(Status.infoKeys.TIME_UP.name(), getTimeSystemUp());
			info.put(Status.infoKeys.SYSTEM_TIME.name(), getSystemTime());
			status.setInfo(info);
			
			URL myURL = new URL(FEERBOX_SERVER_URL+"/status/add");
			HttpURLConnection conn = (HttpURLConnection) myURL.openConnection();
			conn.setRequestProperty("Content-Length", "1000");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			//String json = "{\"button\":\""+answer.getButton()+"\",\"reference\":\""+answer.getReference()+"\", \"time\":\""+answer.getTimeText()+"\"}";
			JsonObject json = statusToJson(status);
			
			OutputStream os = conn.getOutputStream();
			os.write(json.toString().getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
				System.out.println("Failed to send status: HTTP error code : "+ conn.getResponseCode());
			}

			conn.disconnect();
			
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		return df.format(new Date());
	}

	private String getTimeSystemUp() throws IOException {
		String uptime = "";
		Process uptimeProc = Runtime.getRuntime().exec("uptime");
        BufferedReader in = new BufferedReader(new InputStreamReader(uptimeProc.getInputStream()));
        String line = in.readLine();
        if (line != null) {
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
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
		return df.format(ClientRegister.getInstance().getLastAnswerSaved());
	}

	private String getSoftwareVersion() {
		return StartFeerBoxClient.version;
	}

	private String getInternetStatus() throws SocketException, InterruptedException {
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
		        if(iface.getName().toUpperCase().contains("WIFI")){
		        	this.ip = addr.getHostAddress();
		        	out = "true";
		        }
		    }
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

}
