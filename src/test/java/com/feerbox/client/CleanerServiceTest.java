package com.feerbox.client;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.junit.Test;

import com.feerbox.client.model.Cleaner;
import com.feerbox.client.services.CleanerService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CleanerServiceTest {
	public void getPendingCleaners(){
		List<Cleaner> cleaners = CleanerService.getPendingUpdates("2016007");
		for(Cleaner cleaner:cleaners){
			System.out.println(cleaner.getName());
		}
		
	}
	
	@Test
	public void parseJson(){
		String in = "{\"cleaners\":[{\"id\":1,\"name\":\"NameCleaner1\",\"surname\":\"SurnameCleaner1\",\"reference\":\"nfc-reference\",\"creationDate\":\"25-Sep-2016 07:58:38.247\",\"lastUpdate\":\"25-Sep-2016 07:58:38.247\",\"Company\":{\"id\":1,\"name\":\"FEERBOX\"}}]}";
		JsonParser parser = new JsonParser();
		JsonObject json = parser.parse(in).getAsJsonObject();
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
		    System.out.println("Cleaner recived:" + cleaner);
		}
	}
}
