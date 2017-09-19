package com.feerbox.client;

import org.junit.Test;

import com.feerbox.client.model.Weather;
import com.google.gson.JsonObject;

public class NumberTest {
	@Test
	public void NumberFormat() {
		Weather weather = new Weather();
		weather.setHumidity("251.98374");
		weather.setTemperature("323.534");
		JsonObject json = convert(weather);
		System.out.println(json);
	}

	private JsonObject convert(Weather weather) {
		JsonObject json = new JsonObject();
		json.addProperty("clientId", weather.getId());
		try {
			String temperature = weather.getTemperature();
			temperature = temperature.replaceAll(",", ".");
			String humidity = weather.getHumidity();
			humidity = humidity.replaceAll(",", ".");
			json.addProperty("temperature", Double.parseDouble(temperature));
			json.addProperty("humidity", Double.parseDouble(humidity));
		} catch (NumberFormatException e) {
			System.out.println("Error converting temperature and humidity");
			json.addProperty("temperature", weather.getTemperature());
			json.addProperty("humidity", weather.getHumidity());
		}
		//json.addProperty("time", weather.getTimeFormatted());
		json.addProperty("feerboxReference", weather.getFeerBoxReference());
		
		return json;
	}
}
