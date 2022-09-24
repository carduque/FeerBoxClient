package com.feerbox.client.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import org.apache.log4j.Logger;

import com.feerbox.client.db.ReadAnswer;
import com.feerbox.client.db.SaveAnswer;
import com.feerbox.client.db.SaveAnswerError;
import com.feerbox.client.model.Answer;
import com.feerbox.client.registers.ClientRegister;
import com.feerbox.client.services.voice.AskQuestion;
import com.feerbox.client.services.voice.AskRecordAndTranscribe;
import com.google.gson.JsonObject;

public class AnswerService{
	protected final static Logger logger = Logger.getLogger(AnswerService.class);
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
		/*if(ClientRegister.getInstance().getVoiceAnswer()) {
			//if(answer.getButton()==1 || answer.getButton()==2) {
				logger.debug("Voice active and bad answer, going to reproduce a sound");
				AskRecordAndTranscribe.main(null);
			//}
		}*/
		return id;
	}

	public static boolean saveAnswerInternet(Answer answer) {
		boolean ok = true;
		HttpURLConnection conn = null;
		OutputStream os = null;
		try {
			//URL myURL = new URL(ClientRegister.getInstance().getEnvironment() + "/answer/add");

			URL myURL = new URL(ClientRegister.getInstance().getEnvironmentQrs() + "/api/v1/answer");

			conn = (HttpURLConnection) myURL.openConnection();
			conn.setRequestProperty("Content-Length", "1000");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");

			JsonObject json = answerToJson(answer);
			
			os = conn.getOutputStream();
			os.write(json.toString().getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED && conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				logger.info("Failed : HTTP error code : "+ conn.getResponseCode());
				ok = false;
				//SaveAnswer.save(answer);
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
	
	protected static JsonObject answerToJson(Answer answer) {
		JsonObject json = new JsonObject();
		/*json.addProperty("button", answer.getButton());
		json.addProperty("time", answer.getTimeFormatted());
		json.addProperty("feerBoxReference", answer.getReference());*/

		json.addProperty("datetime", answer.getTimeFormatted());
		json.addProperty("deviceId", answer.getReference());
		json.addProperty("value", answer.getButton());

		return json;
	}
	
	public static void tryConnection() throws SaveAnswerError {
		InputStream stream = null;
		HttpURLConnection myURLConnection = null;
		try {
			URL myURL = new URL(ClientRegister.getInstance().getEnvironment());
			myURLConnection = (HttpURLConnection) myURL.openConnection();
			myURLConnection.setConnectTimeout(5000);
			myURLConnection.setReadTimeout(5000);
			myURLConnection.setRequestProperty("Content-Length", "1000");
			stream = myURLConnection.getInputStream();
		} catch (MalformedURLException e) {
			throw new SaveAnswerError(e);
		} catch (IOException e) {
			throw new SaveAnswerError(e);
		}
		finally{
			try {
				if(stream!=null) stream.close();
			} catch (IOException e) {
				throw new SaveAnswerError(e);
			}
			if(myURLConnection!=null) myURLConnection.disconnect();
		}
	}

	public static void saveIP(String iface, String ip) {
		try {
			//logger.debug(FEERBOX_SERVER_URL+"iface/"+iface+"/ip/"+ip);
			URL myURL = new URL(ClientRegister.getInstance().getEnvironment()+"/iface/"+iface+"/ip/"+ip);
			URLConnection myURLConnection = myURL.openConnection();
			myURLConnection.setRequestProperty("Content-Length", "1000");
			myURLConnection.getInputStream();
		} catch (MalformedURLException e) {
			logger.debug("MalformedURLException", e);
		} catch (IOException e) {
			logger.debug("IOException", e);
		}
		
	}

	public static Answer getLastSaved() {
		return ReadAnswer.getLastSaved();
	}

}
