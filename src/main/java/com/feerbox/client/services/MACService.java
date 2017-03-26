package com.feerbox.client.services;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;

import com.feerbox.client.db.ReadMAC;
import com.feerbox.client.db.SaveMAC;
import com.feerbox.client.model.MAC;
import com.feerbox.client.registers.ClientRegister;
import com.google.gson.JsonObject;


public class MACService {
	protected final static Logger logger = Logger.getLogger(MACService.class);

	public static List<MAC> notUploaded() {
		return ReadMAC.notUpload();
	}

	public static boolean saveServer(MAC mac) {
		boolean ok = true;
		OutputStream os = null;
		HttpURLConnection conn = null;
		try {
			URL myURL = new URL(ClientRegister.getInstance().getEnvironment()+"/mac/add");
			conn = (HttpURLConnection) myURL.openConnection();
			conn.setRequestProperty("Content-Length", "1000");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			JsonObject json = MACToJson(mac);
			
			os = conn.getOutputStream();
			os.write(json.toString().getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
				logger.info("Failed MACService/add : HTTP error code : "+ conn.getResponseCode());
				ok = false;
			}
			
		} catch (MalformedURLException e) {
			logger.debug("MalformedURLException", e);
			ok = false;
		} catch (IOException e) {
			logger.debug("IOException", e);
			ok = false;
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
		return ok;
	}

	private static JsonObject MACToJson(MAC mac) {
		JsonObject json = new JsonObject();
		json.addProperty("clientId", mac.getId());
		json.addProperty("mac", mac.getMac());
		json.addProperty("request", mac.getRequest());
		json.addProperty("time", mac.getTimeFormatted());
		json.addProperty("feerboxReference", mac.getFeerBoxReference());
		return json;
	}

	public static void upload(MAC mac) {
		SaveMAC.upload(mac);
	}

}
