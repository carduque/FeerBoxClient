package com.feerbox.client.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;

import com.feerbox.client.db.ReadCounterPeople;
import com.feerbox.client.db.RemoveCounterPeople;
import com.feerbox.client.db.SaveCounterPeople;
import com.feerbox.client.model.CounterPeople;
import com.feerbox.client.registers.ClientRegister;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;


public class CounterPeopleService {
	public static final int MAX_BULKY = 400;
	public static final int MAX_SAFE_BULKY = 100;
	private static int TIMEOUT_VALUE = 600000;
	protected final static Logger logger = Logger.getLogger(CounterPeopleService.class);

	public static List<CounterPeople> notUploaded() {
		return ReadCounterPeople.notUpload(100, 1);
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
	
	public static String saveServerBulky(List<CounterPeople> counterPeoples) {
		String ok = null;
		OutputStream os = null;
		HttpURLConnection conn = null;
		try {
			URL myURL = new URL(ClientRegister.getInstance().getEnvironment()+"/counterpeople/addbulky");
			ok = sendServer(myURL, conn, os, counterPeoples);
			
		} catch (MalformedURLException e) {
			logger.error("MalformedURLException", e);
		} catch (SocketTimeoutException e){
			logger.error("Error Timeout", e);
		} catch (IOException e) {
			logger.error("IOException", e);
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

	private static String sendServer(URL myURL, HttpURLConnection conn, OutputStream os, List<CounterPeople> counterPeoples) throws IOException {
		String ok = null;
		conn = (HttpURLConnection) myURL.openConnection();
		
		conn.setConnectTimeout(TIMEOUT_VALUE);
		conn.setReadTimeout(TIMEOUT_VALUE);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();
		Type listType = new TypeToken<List<CounterPeople>>() {}.getType();
		String json = gson.toJson(counterPeoples, listType);
		conn.setRequestProperty("Content-Length", json.length()+"");
		//conn.connect();
		os = conn.getOutputStream();
		os.write(json.toString().getBytes());
		os.flush();
		if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
			logger.warn("Failed CounterPeopleService/add : HTTP error code : "+ conn.getResponseCode());
			logger.warn("Error stream: "+conn.getErrorStream());
			ok = null;
		}
		else{
			ok = "";
			//[{"serverId":406,"save":"OK"},{"serverId":407,"save":"OK"}]
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
			StringBuilder responseStrBuilder = new StringBuilder();
			String inputStr;
			while ((inputStr = br.readLine()) != null){
			    responseStrBuilder.append(inputStr);
			}
			Type listPost= new TypeToken<List<Post>>() {}.getType();
			List<Post> posts = gson.fromJson(responseStrBuilder.toString(), listPost);
			for(Post post : posts){
				if(post.save!=null && "OK".equals(post.save)){
					ok+=post.clientId+",";
				}
			}
		}
		return ok;
	}

	public static List<CounterPeople> notUploadedBulky() {
		return ReadCounterPeople.notUploadSafe(MAX_BULKY);
	}

	public static void uploadList(String ids) {
		SaveCounterPeople.uploadList(ids);
	}
	
	protected  class Post{
		@SerializedName("serverId")
		public String serverId;
		@SerializedName("clientId")
		public String clientId;
		@SerializedName("save")
		public String save;
	}

	public static int notUploadedTotal() {
		return ReadCounterPeople.notUpload();
	}

	public static List<CounterPeople> failedUploadedBulky() {
		return ReadCounterPeople.notUpload(MAX_SAFE_BULKY, 2);
	}

	public static String saveFailedServerBulky(List<CounterPeople> counterPeoples) {
		String ok = null;
		OutputStream os = null;
		HttpURLConnection conn = null;
		try {
			URL myURL = new URL(ClientRegister.getInstance().getEnvironment()+"/counterpeople/addSafeBulky");
			ok = sendServer(myURL, conn, os, counterPeoples);
			
		} catch (MalformedURLException e) {
			logger.error("MalformedURLException", e);
		} catch (SocketTimeoutException e){
			logger.error("Error Timeout", e);
		} catch (IOException e) {
			logger.error("IOException", e);
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

	public static CounterPeople getLastSaved() {
		return ReadCounterPeople.getLastSaved();
	}

	public static long save(CounterPeople counterpeople) {
		return SaveCounterPeople.save(counterpeople);
	}

	public static int delete(CounterPeople counterpeople) {
		return RemoveCounterPeople.delete(counterpeople);
	}

	public static long saveDelayed(CounterPeople counterpeople) {
		return SaveCounterPeople.saveDelayed(counterpeople);
	}

	public static CounterPeople getLastSaved(String feerBoxReference) {
		return ReadCounterPeople.getLastSaved(feerBoxReference);
	}

}
