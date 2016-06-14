package com.feerbox.client.services;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;

import com.feerbox.client.db.ReadCommand;
import com.feerbox.client.db.SaveCommand;
import com.feerbox.client.model.Command;
import com.feerbox.client.registers.ClientRegister;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class CommandService {
	final static Logger logger = Logger.getLogger(CommandService.class);

	public static List<Command> getServerCommands(String reference) {
		List<Command> commands = null;
		try {
			URL myURL = new URL(ClientRegister.getInstance().getEnvironment()+"/command/getpending");
			HttpURLConnection conn = (HttpURLConnection) myURL.openConnection();
			conn.setRequestProperty("Content-Length", "1000");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			//String json = "{\"button\":\""+answer.getButton()+"\",\"reference\":\""+answer.getReference()+"\", \"time\":\""+answer.getTimeText()+"\"}";
			JsonObject json = new JsonObject();
			json.addProperty("feerboxReference", reference);
			
			OutputStream os = conn.getOutputStream();
			os.write(json.toString().getBytes());
			os.flush();

			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				Reader reader = new InputStreamReader(conn.getInputStream());
				Gson gson = new Gson();
				List response = gson.fromJson(reader, List.class);
				logger.debug("Response size: "+response.size());

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
		return commands;
	}

	public static Command startNextExecution() {
		Command command = ReadCommand.startNextExecution();
		SaveCommand.startExecution(command);
		return command;
	}

	public static void startExecution(Command command) {
		// TODO Auto-generated method stub
		
	}

	public static void finishExecution(Command command) {
		// TODO Auto-generated method stub
		
	}

	public static void saveOutput(Command command, String output) {
		// TODO Auto-generated method stub
		
	}

	public static void save(Command command) {
		SaveCommand.save(command);
	}

	public static List<Command> getCommandsToUpload() {
		return ReadCommand.readCommandsNotUploaded();
	}

	public static boolean saveServer(Command command) {
		try {
			URL myURL = new URL(ClientRegister.getInstance().getEnvironment()+"/command/update");
			HttpURLConnection conn = (HttpURLConnection) myURL.openConnection();
			conn.setRequestProperty("Content-Length", "1000");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			//String json = "{\"button\":\""+answer.getButton()+"\",\"reference\":\""+answer.getReference()+"\", \"time\":\""+answer.getTimeText()+"\"}";
			JsonObject json = new JsonObject();
			json.addProperty("commandId", command.getServerId());
			json.addProperty("startTime", command.getStartTimeFormatted());
			json.addProperty("finishTime", command.getFinishTimeFormatted());
			json.addProperty("output", command.getOutput());
			
			OutputStream os = conn.getOutputStream();
			os.write(json.toString().getBytes());
			os.flush();

			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				logger.debug("Command "+command.getServerId()+" updated on server side");
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
		return true;
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

	public static void restart() {
		// TODO Auto-generated method stub
		
	}

	public static void upload(Command command) {
		SaveCommand.update(command);
	}

}
