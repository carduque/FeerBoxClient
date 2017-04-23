package com.feerbox.client.services;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;

import com.feerbox.client.db.ReadCounterPeople;
import com.feerbox.client.db.SaveCounterPeople;
import com.feerbox.client.model.CounterPeople;
import com.feerbox.client.registers.ClientRegister;
import com.google.gson.JsonObject;


public class CounterPeopleService {
	protected final static Logger logger = Logger.getLogger(CounterPeopleService.class);

	public static List<CounterPeople> notUploaded() {
		return ReadCounterPeople.notUpload();
	}

	public static boolean saveServer(CounterPeople counterPeople) {
		boolean ok = true;
		OutputStream os = null;
		HttpURLConnection conn = null;
		try {
			URL myURL = new URL(ClientRegister.getInstance().getEnvironment()+"/counterpeople/add");
			conn = (HttpURLConnection) myURL.openConnection();
			conn.setRequestProperty("Content-Length", "1000");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			JsonObject json = CounterPeopleToJson(counterPeople);
			
			os = conn.getOutputStream();
			os.write(json.toString().getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
				logger.info("Failed CounterPeopleService/add : HTTP error code : "+ conn.getResponseCode());
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

	private static JsonObject CounterPeopleToJson(CounterPeople counterPeople) {
		JsonObject json = new JsonObject();
		json.addProperty("clientId", counterPeople.getId());
		json.addProperty("distance", counterPeople.getDistance());
		json.addProperty("time", counterPeople.getTimeFormatted());
		json.addProperty("feerBoxReference", counterPeople.getFeerBoxReference());
		json.addProperty("type", counterPeople.getType().name());
		return json;
	}

	public static void upload(CounterPeople counterPeople) {
		SaveCounterPeople.upload(counterPeople);
	}

}
