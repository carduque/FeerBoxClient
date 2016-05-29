package com.feerbox.client.services;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import com.feerbox.client.db.FeerboxDB;
import com.feerbox.client.db.SaveAnswer;
import com.feerbox.client.db.SaveAnswerError;
import com.feerbox.client.model.Answer;
import com.feerbox.client.registers.ClientRegister;
import com.feerbox.client.registers.InternetAccess;
import com.google.gson.JsonObject;

public class SaveAnswerService extends FeerboxDB{
	private static final String FEERBOX_SERVER_URL = ClientRegister.getInstance().getEnvironment();
	

	public static Integer saveAnswer(int buttonNumber) {
		int id = 0;
		Answer answer = new Answer();
		answer.setButton(buttonNumber);
		long now = System.currentTimeMillis();
		Date date = new Date(now); //To be check if this is the best alternative to get current time
		answer.setTime(date);
		answer.setReference(ClientRegister.getInstance().getReference());
		/*
		if(InternetAccess.getInstance().getAccess()){
			logger.debug("Answer internet: "+buttonNumber);
			boolean ok = saveAnswerInternet(answer);
			if(!ok){
				id = SaveAnswer.save(answer);
			}
		}
		*/
		id = SaveAnswer.save(answer);
		if(id==0) return null;
		ClientRegister.getInstance().setLastAnswerSaved(date);
		return id;
	}

	public static boolean saveAnswerInternet(Answer answer) {
		boolean ok = true;
		try {
			URL myURL = new URL(FEERBOX_SERVER_URL+"/answer/add");
			HttpURLConnection conn = (HttpURLConnection) myURL.openConnection();
			conn.setRequestProperty("Content-Length", "1000");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			//String json = "{\"button\":\""+answer.getButton()+"\",\"reference\":\""+answer.getReference()+"\", \"time\":\""+answer.getTimeText()+"\"}";
			JsonObject json = answerToJson(answer);
			
			OutputStream os = conn.getOutputStream();
			os.write(json.toString().getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
				logger.info("Failed : HTTP error code : "+ conn.getResponseCode());
				ok = false;
				//SaveAnswer.save(answer);
			}

			conn.disconnect();
			
		} catch (MalformedURLException e) {
			logger.debug("MalformedURLException", e);
			ok = false;
		} catch (IOException e) {
			logger.debug("IOException", e);
			ok = false;
		}
		return ok;
	}
	
	protected static JsonObject answerToJson(Answer answer) {
		JsonObject json = new JsonObject();
		json.addProperty("button", answer.getButton());
		json.addProperty("time", answer.getTimeFormatted());
		json.addProperty("feerBoxReference", answer.getReference());
		return json;
	}
	
	public static void tryConnection() throws SaveAnswerError {
		try {
			URL myURL = new URL(FEERBOX_SERVER_URL);
			URLConnection myURLConnection = myURL.openConnection();
			myURLConnection.setConnectTimeout(5000);
			myURLConnection.setReadTimeout(5000);
			myURLConnection.setRequestProperty("Content-Length", "1000");
			myURLConnection.getInputStream();
		} catch (MalformedURLException e) {
			throw new SaveAnswerError(e);
		} catch (IOException e) {
			throw new SaveAnswerError(e);
		}
		
	}

	public static void saveIP(String iface, String ip) {
		try {
			//logger.debug(FEERBOX_SERVER_URL+"iface/"+iface+"/ip/"+ip);
			URL myURL = new URL(FEERBOX_SERVER_URL+"iface/"+iface+"/ip/"+ip);
			URLConnection myURLConnection = myURL.openConnection();
			myURLConnection.setRequestProperty("Content-Length", "1000");
			myURLConnection.getInputStream();
		} catch (MalformedURLException e) {
			logger.debug("MalformedURLException", e);
		} catch (IOException e) {
			logger.debug("IOException", e);
		}
		
	}

}
