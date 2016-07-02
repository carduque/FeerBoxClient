package com.feerbox.client;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.feerbox.client.db.ReadCleaningService;
import com.feerbox.client.db.SaveCleaningService;
import com.feerbox.client.model.CleaningService;
import com.feerbox.client.registers.InternetAccess;
import com.feerbox.client.registers.UploadAnswersRegister;
import com.google.gson.JsonObject;

public class CleaningServiceTest {

	
	public void testUpload() {
		InternetAccess.getInstance().setAccess(true);
		CleaningService cleaningService = new CleaningService();
		cleaningService.setCleanerReference("123456");
		cleaningService.setFeerboxReference("2015001");
		
		SaveCleaningService.save(cleaningService);
		
		UploadAnswersRegister uploadAnswersRegister = new UploadAnswersRegister();
		uploadAnswersRegister.run();
		List<CleaningService> list = ReadCleaningService.notUploaded();
		assertTrue(list.size()==0);
	}
	@Test
	public void testSaveServer(){
		CleaningService cleaningService = new CleaningService();
		cleaningService.setCleanerReference("123456");
		cleaningService.setFeerboxReference("2015001");
		cleaningService.setId(1);
		cleaningService.setUpload(false);
		cleaningService.setTime(new Date());
		try {
			URL myURL = new URL("http://feerbox-dev.herokuapp.com/cleaningService/add");
			HttpURLConnection conn = (HttpURLConnection) myURL.openConnection();
			conn.setRequestProperty("Content-Length", "1000");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			//String json = "{\"button\":\""+answer.getButton()+"\",\"reference\":\""+answer.getReference()+"\", \"time\":\""+answer.getTimeText()+"\"}";
			JsonObject json = answerToJson(cleaningService);
			
			OutputStream os = conn.getOutputStream();
			os.write(json.toString().getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
				System.out.println("Failed cleaningService/add : HTTP error code : "+ conn.getResponseCode());
				fail();
			}
			os.close();
			conn.disconnect();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
			fail();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
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
