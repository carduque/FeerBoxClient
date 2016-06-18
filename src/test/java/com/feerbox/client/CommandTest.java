package com.feerbox.client;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.feerbox.client.model.Command;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CommandTest {
	public void getPending(){
		try {
			URL myURL = new URL("http://feerbox-dev.herokuapp.com/command/getpending");
			HttpURLConnection conn = (HttpURLConnection) myURL.openConnection();
			conn.setRequestProperty("Content-Length", "1000");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			JsonObject json = new JsonObject();
			json.addProperty("feerboxReference", "2015001");
			
			OutputStream os = conn.getOutputStream();
			os.write(json.toString().getBytes());
			os.flush();

			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				//{"commands":[{"id":5,"command":"deploy.sh","reference":"2015001","creationDate":"Jun 16, 2016 11:09:33 PM","active":true, "restart":true}]}
				//parse1(conn);
				InputStream in = conn.getInputStream();
				BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8")); 
				StringBuilder responseStrBuilder = new StringBuilder();
				String inputStr;
				while ((inputStr = streamReader.readLine()) != null){
				    responseStrBuilder.append(inputStr);
				}
				JsonParser parser = new JsonParser();
				JsonObject json2 = parser.parse(responseStrBuilder.toString()).getAsJsonObject();
				System.out.println(json2);
				JsonArray  commands = json2.getAsJsonArray("commands");
				for(final JsonElement command : commands) {
					JsonObject jsonObject = command.getAsJsonObject();
					Command localCommand = new Command();
				    localCommand.setServerId(jsonObject.get("id").getAsInt());
				    localCommand.setCommand(jsonObject.get("command").getAsString());
				    String creationDate = jsonObject.get("creationDate").getAsString();
				    System.out.println(creationDate);
				    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss.SSS");
				    try {
						localCommand.setTime(new Timestamp(df.parse(creationDate).getTime()));
					} catch (ParseException e) {
						e.printStackTrace();
					}
				    System.out.println(localCommand);
				}
				
			}
			else{
				fail("Failed : HTTP error code : "+ conn.getResponseCode());
			}
			os.close();
			conn.disconnect();
			
		} catch (MalformedURLException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	private void parse1(HttpURLConnection conn) throws IOException {
		Reader reader = new InputStreamReader(conn.getInputStream());
		Gson gson = new Gson();
		Out out = gson.fromJson(reader, Out.class);
		System.out.println("Response size: "+out.getCommands().size());
		if(out.getCommands().size()>0){
			Command command = out.getCommands().get(0);
			System.out.println(command.getCommand());
		}
	}
	
	@Test
	public void saveServer() throws IOException{
		URL myURL = new URL("http://feerbox-dev.herokuapp.com/command/updateExecutions");
		HttpURLConnection conn = (HttpURLConnection) myURL.openConnection();
		//conn.setRequestProperty("Content-Length", "1000");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		JsonObject json = new JsonObject();
		json.addProperty("id", "6");
		json.addProperty("reference", "2015001");
		json.addProperty("output", "output");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		json.addProperty("startTime", df.format(new Date()));
		json.addProperty("finishTime", df.format(new Date()));
		
		OutputStream os = conn.getOutputStream();
		os.write(json.toString().getBytes());
		os.flush();

		if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
			System.out.println("Failed : HTTP error code : "+ conn.getResponseCode());
			fail();
		}
		os.close();
		conn.disconnect();
	}

}

class Out{
	private ArrayList<Command> commands;

	public ArrayList<Command> getCommands() {
		return commands;
	}

	public void setCommands(ArrayList<Command> commands) {
		this.commands = commands;
	}
}
