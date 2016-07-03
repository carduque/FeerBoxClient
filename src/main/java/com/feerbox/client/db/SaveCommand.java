package com.feerbox.client.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.feerbox.client.model.Command;

public class SaveCommand extends FeerboxDB {
	public static int save(Command command) {
		//logger.debug("going to save command locally");
		int id = 0;
		Statement statement = null;
		try {
			// create a database connection
			Connection con = getConnection();
			statement = con.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.

			createCommandsTableIfNotExists(statement);
			int restart = 0;
			if(command.getRestart()){
				restart=1;
			}
			int upload = 0;
			if(command.getUpload()){
				upload=1;
			}
			String parameter = null;
			if(command.getParameter()!=null){
				parameter = command.getParameter().replaceAll("'","''");
			}
			String sql = "insert into Commands (time, command, serverId, upload, serverCreationTime, restart, parameter) "
					+ "values(STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW', 'localtime'),'" + command.getCommand() + "',  "+command.getServerId()+", "+upload
							+", STRFTIME('%Y-%m-%d %H:%M:%f', '"+ command.getsetServerCreationTimeFormatted()+"'), "
									+restart+",'"+parameter+"')";
			//logger.debug(sql);
			statement.executeUpdate(sql);
			ResultSet rs = statement.executeQuery("SELECT last_insert_rowid() AS rowid FROM Commands LIMIT 1");
			while (rs.next()) {
				id = rs.getInt("rowid");
			}
			logger.debug("Command registered offline: "+id);
		} catch (SQLException e) {
			logger.debug("SQLException", e);
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				logger.debug("SQLException", e);
			}
		}
		return id;
	}

	public static void update(Command command) {
		Statement statement = null;
		try {
			// create a database connection
			Connection con = getConnection();
			statement = con.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.

			// statement.executeUpdate("drop table if exists person");
			createCommandsTableIfNotExists(statement);
			statement.executeUpdate("update Commands set upload=1 where id="+command.getId());
		} catch (SQLException e) {
			logger.error("SQLException", e);
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				logger.error("SQLException", e);
			}
		}
	}

	public static void startExecution(Command command) {
		Statement statement = null;
		try {
			// create a database connection
			Connection con = getConnection();
			statement = con.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.


			// statement.executeUpdate("drop table if exists person");
			createCommandsTableIfNotExists(statement);
			statement.executeUpdate("update Commands set startTime=STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW', 'localtime') where id="+command.getId());
		} catch (SQLException e) {
			logger.error("SQLException", e);
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				logger.error("SQLException", e);
			}
		}
	}

	public static void saveFinishExecution(Command command) {
		Statement statement = null;
		try {
			// create a database connection
			Connection con = getConnection();
			statement = con.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.
			String output = null;
			if(command.getOutput()!=null){
				output = command.getOutput().replaceAll("'","''");
			}

			// statement.executeUpdate("drop table if exists person");
			createCommandsTableIfNotExists(statement);
			statement.executeUpdate("update Commands set output='"+output+"',finishTime=STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW', 'localtime') where id="+command.getId());
		} catch (SQLException e) {
			logger.error("SQLException", e);
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				logger.error("SQLException", e);
			}
		}
		logger.debug("Execution saved in DB for: "+command.getCommand());
	}
}
