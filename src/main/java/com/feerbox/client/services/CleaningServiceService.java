package com.feerbox.client.services;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.log4j.Logger;

import com.feerbox.client.db.SaveCleaningService;
import com.feerbox.client.model.CleaningService;
import com.feerbox.client.registers.ClientRegister;
import com.google.gson.JsonObject;

public class CleaningServiceService {
	protected final static Logger logger = Logger.getLogger(CleaningServiceService.class);
	
	public static Integer save(String cleanerReference) {
		Integer id = null;
		CleaningService cleaningService = new CleaningService();
		cleaningService.setCleanerReference(cleanerReference);
		cleaningService.setFeerboxReference(ClientRegister.getInstance().getReference());
		cleaningService.setTime(new Timestamp(new Date().getTime()));
		id = SaveCleaningService.save(cleaningService);
		if(id==0) return null;		
		return id;
	}
	
	

	public static boolean saveServer(CleaningService cleaningService) {
		boolean ok = true;
		OutputStream os = null;
		HttpURLConnection conn = null;
		try {
			URL myURL = new URL(ClientRegister.getInstance().getEnvironment()+"/cleaningService/add");
			conn = (HttpURLConnection) myURL.openConnection();
			conn.setRequestProperty("Content-Length", "1000");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			//String json = "{\"button\":\""+answer.getButton()+"\",\"reference\":\""+answer.getReference()+"\", \"time\":\""+answer.getTimeText()+"\"}";
			JsonObject json = answerToJson(cleaningService);
			
			os = conn.getOutputStream();
			os.write(json.toString().getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
				logger.info("Failed cleaningService/add : HTTP error code : "+ conn.getResponseCode());
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


	private static JsonObject answerToJson(CleaningService cleaningService) {
		JsonObject json = new JsonObject();
		json.addProperty("clientId", cleaningService.getId());
		json.addProperty("cleanerReference", cleaningService.getCleanerReference());
		json.addProperty("time", cleaningService.getTimeFormatted());
		json.addProperty("feerboxReference", cleaningService.getFeerboxReference());
		return json;
	}
}
