package com.feerbox.client.services;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;

import com.feerbox.client.db.ReadWeather;
import com.feerbox.client.db.SaveWeather;
import com.feerbox.client.model.Weather;
import com.feerbox.client.registers.ClientRegister;
import com.google.gson.JsonObject;

public class WeatherService {
	final static Logger logger = Logger.getLogger(WeatherService.class);

	public static List<Weather> notUploaded() {
		return ReadWeather.notUploaded();
	}

	public static boolean saveServer(Weather weather) {
		boolean ok = true;
		HttpURLConnection conn = null;
		OutputStream os = null;
		try {
			URL myURL = new URL(ClientRegister.getInstance().getEnvironment()+"/weather/add");
			conn = (HttpURLConnection) myURL.openConnection();
			conn.setRequestProperty("Content-Length", "1000");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			//String json = "{\"button\":\""+answer.getButton()+"\",\"reference\":\""+answer.getReference()+"\", \"time\":\""+answer.getTimeText()+"\"}";
			JsonObject json = weatherToJson(weather);
			
			os = conn.getOutputStream();
			os.write(json.toString().getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
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

	private static JsonObject weatherToJson(Weather weather) {
		JsonObject json = new JsonObject();
		json.addProperty("clientId", weather.getId());
		try {
			String temperature = weather.getTemperature();
			temperature = temperature.replaceAll("\\.", ",");
			String humidity = weather.getHumidity();
			humidity = humidity.replaceAll("\\.", ",");
			json.addProperty("temperature", Double.parseDouble(temperature));
			json.addProperty("humidity", Double.parseDouble(humidity));
		} catch (NumberFormatException e) {
			logger.error("Error converting temperature and humidity: "+e.getMessage());
			json.addProperty("temperature", weather.getTemperature());
			json.addProperty("humidity", weather.getHumidity());
		}
		json.addProperty("time", weather.getTimeFormatted());
		json.addProperty("feerboxReference", weather.getFeerBoxReference());
		return json;
	}

	public static void uploaded(Weather weather) {
		SaveWeather.uploaded(weather);
	}

}
