package com.feerbox.client.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.feerbox.client.model.Command;

public class ReadCommand extends FeerboxDB {
	public static List<Command> readCommandsNotUploaded() {
		Statement statement = null;
		List<Command> commands = new ArrayList<Command>();
		try {
			// create a database connection
			Connection con = getConnection();
			statement = con.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.

			// statement.executeUpdate("drop table if exists person");
			createCommandsTableIfNotExists(statement);
			ResultSet rs = statement.executeQuery("select id, time, command, startTime, finishTime, upload, serverId, output, parameter from Commands where upload=0 and finishTime is not null");
			while (rs.next()) {
				Command command = new Command();
				command.setId(rs.getInt("id"));
				String time = rs.getString("time");
				command.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(time));
				command.setCommand(rs.getString("command"));
				String startTime = rs.getString("startTime");
				command.setStartTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(startTime));
				String finishTime = rs.getString("finishTime");
				command.setFinishTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(finishTime));
				command.setUpload(rs.getInt("upload")==1); //1: true - 0: false
				command.setServerId(rs.getInt("serverId"));
				command.setOutput(rs.getString("output"));
				command.setParameter(rs.getString("parameter"));
				commands.add(command);
			}
		} catch (SQLException e) {
			logger.debug("SQLException", e);
			commands = null;
		} catch (ParseException e) {
			logger.debug("ParseException", e);
			commands = null;
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				logger.debug("SQLException", e);
				commands = null;
			}
		}
		return commands;
	}

	public static boolean IsAnyCommandInExecution() {
		boolean out = false;
		Statement statement = null;
		try {
			// create a database connection
			Connection con = getConnection();
			statement = con.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.

			// statement.executeUpdate("drop table if exists person");
			createCommandsTableIfNotExists(statement);
			ResultSet rs = statement.executeQuery("select id, time, command, startTime, finishTime, upload from Commands where finishTime is null and startTime is not null");
			while (rs.next()) {
				out = true;
			}
		} catch (SQLException e) {
			logger.debug("SQLException", e);
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				logger.debug("SQLException", e);
			}
		}
		return out;
	}

	public static Command startNextExecution() {
		//logger.debug("startNextExecution");
		Command command = null;
		Statement statement = null;
		try {
			// create a database connection
			Connection con = getConnection();
			statement = con.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.
			// statement.executeUpdate("drop table if exists person");
			//createCommandsTableIfNotExists(statement);
			ResultSet rs = statement.executeQuery("select id, time, command, startTime, finishTime, upload, restart, parameter from Commands where upload=0 and startTime is null and finishTime is null order by time asc limit 1");
			while (rs.next()) {
				command = new Command();
				command.setId(rs.getInt("id"));
				String time = rs.getString("time");
				command.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(time));
				command.setCommand(rs.getString("command"));
				command.setParameter(rs.getString("parameter"));
				String startTime = rs.getString("startTime");
				if(startTime!=null){
					command.setStartTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(startTime));
				}
				String finishTime = rs.getString("finishTime");
				if(finishTime!=null){
					command.setFinishTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(finishTime));
				}
				command.setUpload(rs.getInt("upload")==1); //1: true - 0: false
				command.setRestart(rs.getInt("restart")==1); //1: true - 0: false
			}
		} catch (SQLException e) {
			logger.error("SQLException", e);
		} catch (ParseException e) {
			logger.error("ParseException", e);
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				logger.error("SQLException", e);
			}
		}
		return command;
	}
}
