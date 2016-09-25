package com.feerbox.client.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import com.feerbox.client.db.ReadCleaner;
import com.feerbox.client.db.SaveCleaner;
import com.feerbox.client.db.SaveCommand;
import com.feerbox.client.model.Cleaner;
import com.feerbox.client.registers.ClientRegister;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CleanerService {
	final static Logger logger = Logger.getLogger(CleanerService.class);

	public static List<Cleaner> getPendingUpdates(String reference) {
		List<Cleaner> out = new ArrayList<Cleaner>();
		try {
			URL myURL = new URL(ClientRegister.getInstance().getEnvironment()+"/cleaner/getpendingupdates");
			HttpURLConnection conn = (HttpURLConnection) myURL.openConnection();
			conn.setRequestProperty("Content-Length", "1000");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			//String json = "{\"button\":\""+answer.getButton()+"\",\"reference\":\""+answer.getReference()+"\", \"time\":\""+answer.getTimeText()+"\"}";
			Date lastUpdate = ReadCleaner.getLastUpdate();
			if(lastUpdate==null){
				lastUpdate = new Date(0);
			}
			JsonObject json_out = new JsonObject();
			json_out.addProperty("feerboxReference", reference);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			df.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
			json_out.addProperty("lastupdate", df.format(lastUpdate));
			
			OutputStream os = conn.getOutputStream();
			os.write(json_out.toString().getBytes());
			os.flush();

			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				ClientRegister.getInstance().setLastGetCleaners(new Date());
				//{"commands":[{"id":5,"command":"deploy.sh","reference":"2015001","creationDate":"16-Jun-2016 23:09:33.813","active":true,"restart":true}]}
				InputStream in = conn.getInputStream();
				BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8")); 
				StringBuilder responseStrBuilder = new StringBuilder();
				String inputStr;
				while ((inputStr = streamReader.readLine()) != null){
				    responseStrBuilder.append(inputStr);
				}
				//logger.debug("UpdateCleaners "+responseStrBuilder.toString());
				JsonParser parser = new JsonParser();
				JsonObject json = parser.parse(responseStrBuilder.toString()).getAsJsonObject();
				JsonArray  cleaners = json.getAsJsonArray("cleaners");
				for(final JsonElement element : cleaners) {
					JsonObject jsonObject = element.getAsJsonObject();
					Cleaner cleaner = new Cleaner();
					cleaner.setServerId(jsonObject.get("id").getAsInt());
				    cleaner.setName(jsonObject.get("name").getAsString());
				    cleaner.setSurname(jsonObject.get("surname").getAsString());
				    JsonObject jsonCompany = jsonObject.get("Company").getAsJsonObject();
				    cleaner.setCompany(jsonCompany.get("id").getAsInt());
				    String creationDate = jsonObject.get("creationDate").getAsString();
				    String lastUpdateDate = jsonObject.get("lastUpdate").getAsString();
				    SimpleDateFormat df2 = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss.SSS");
				    try {
						cleaner.setServerCreationDate(df2.parse(creationDate));
						cleaner.setServerLastUpdate(df2.parse(lastUpdateDate));
					} catch (ParseException e) {
						e.printStackTrace();
					}
				    logger.debug("Cleaner recived:" + cleaner);
				    out.add(cleaner);
				}
			}
			else{
				logger.info("Failed : HTTP error code : "+ conn.getResponseCode());
			}
			os.close();
			conn.disconnect();
			
		} catch (MalformedURLException e) {
			logger.debug("MalformedURLException", e);
		} catch (IOException e) {
			logger.debug("IOException", e);
		}
		return out;
	}

	public static void saveOrUpdate(Cleaner cleaner) {
		SaveCleaner.save(cleaner);
	}
}
