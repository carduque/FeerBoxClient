package com.feerbox.client.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.feerbox.client.db.ReadCommand;
import com.feerbox.client.db.SaveCommand;
import com.feerbox.client.model.Command;
import com.feerbox.client.registers.ClientRegister;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CommandService {
	final static Logger logger = Logger.getLogger(CommandService.class);

	public static List<Command> getServerCommands(String reference) {
		List<Command> out = new ArrayList<Command>();
		OutputStream os = null;
		HttpURLConnection conn = null;
		try {
			URL myURL = new URL(ClientRegister.getInstance().getEnvironment()+"/command/getpending");
			conn = (HttpURLConnection) myURL.openConnection();
			conn.setRequestProperty("Content-Length", "1000");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			//String json = "{\"button\":\""+answer.getButton()+"\",\"reference\":\""+answer.getReference()+"\", \"time\":\""+answer.getTimeText()+"\"}";
			JsonObject json_out = new JsonObject();
			json_out.addProperty("feerboxReference", reference);
			
			os = conn.getOutputStream();
			os.write(json_out.toString().getBytes());
			os.flush();
			ClientRegister.getInstance().setLastGetCommands(new Date());
			
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				ClientRegister.getInstance().setLastGetCommands(new Date());
				//{"commands":[{"id":5,"command":"deploy.sh","reference":"2015001","creationDate":"16-Jun-2016 23:09:33.813","active":true,"restart":true}]}
				InputStream in = conn.getInputStream();
				BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8")); 
				StringBuilder responseStrBuilder = new StringBuilder();
				String inputStr;
				while ((inputStr = streamReader.readLine()) != null){
				    responseStrBuilder.append(inputStr);
				}
				JsonParser parser = new JsonParser();
				JsonObject json = parser.parse(responseStrBuilder.toString()).getAsJsonObject();
				JsonArray  commands = json.getAsJsonArray("commands");
				for(final JsonElement element : commands) {
					JsonObject jsonObject = element.getAsJsonObject();
					Command command = new Command();
				    command.setServerId(jsonObject.get("id").getAsInt());
				    command.setCommand(jsonObject.get("command").getAsString());
				    command.setParameter(jsonObject.get("parameter").getAsString());
				    command.setRestart(jsonObject.get("id").getAsBoolean());
				    command.setRestart(jsonObject.get("restart").getAsBoolean());
				    String creationDate = jsonObject.get("creationDate").getAsString();
				    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss.SSS");
				    try {
						command.setServerCreationTime(new Timestamp(df.parse(creationDate).getTime()));
					} catch (ParseException e) {
						e.printStackTrace();
					}
				    logger.debug("Command recived:" + command);
				    out.add(command);
				}
			}
			else{
				logger.info("Failed : HTTP error code : "+ conn.getResponseCode());
			}
			
		} catch (UnknownHostException e) {
			if(ClientRegister.getInstance().getShowInternetConnectionError()){
				logger.error("UnknownHostException - No Internet connection: " + e.getMessage());
			}
		}
		catch (MalformedURLException e) {
			logger.debug("MalformedURLException", e);
		} catch (IOException e) {
			logger.debug("IOException", e);
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
		return out;
	}

	public static Command startNextExecution() {
		return ReadCommand.startNextExecution();
	}


	public static void save(Command command) {
		SaveCommand.save(command);
	}

	public static List<Command> getCommandsToUpload() {
		return ReadCommand.readCommandsNotUploaded();
	}

	public static boolean saveServer(Command command) {
		boolean out = true;
		HttpURLConnection conn = null;
		OutputStream os = null;
		try {
			URL myURL = new URL(ClientRegister.getInstance().getEnvironment()+"/command/updateExecutions");
			conn = (HttpURLConnection) myURL.openConnection();
			//conn.setRequestProperty("Content-Length", "1000");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			JsonObject json = new JsonObject();
			json.addProperty("id", command.getServerId());
			json.addProperty("reference", ClientRegister.getInstance().getReference());
			json.addProperty("output", command.getOutput());
			json.addProperty("startTime", command.getStartTimeFormatted());
			json.addProperty("finishTime", command.getFinishTimeFormatted());
			
			os = conn.getOutputStream();
			os.write(json.toString().getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
				logger.error("Failed saving command to server: HTTP error code : "+ conn.getResponseCode());
				out = false;
			}
			
		} catch (MalformedURLException e) {
			logger.error("MalformedURLException", e);
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
		return out;
	}

	public static boolean isCommandInExecution() {
		return ReadCommand.IsAnyCommandInExecution();
	}

	public static boolean forceCleanQueue() {
		// TODO Auto-generated method stub
		return false;
	}

	public static void cleanQueue() {
		// TODO Auto-generated method stub
		
	}

	public static boolean forceRestart() {
		// TODO Auto-generated method stub
		return false;
	}

	public static void upload(Command command) {
		SaveCommand.update(command);
	}

}
