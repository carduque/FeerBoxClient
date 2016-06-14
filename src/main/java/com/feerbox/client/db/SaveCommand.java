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
			statement.executeUpdate(
					"insert into Commands (time, command, serverId, upload) values(datetime('now', 'localtime'),\"" + command.getCommand() + "\",  "+command.getServerId()+", "+command.getUpload()+")");
			ResultSet rs = statement.executeQuery("SELECT last_insert_rowid() AS rowid FROM Commands LIMIT 1");
			while (rs.next()) {
				id = rs.getInt("rowid");
			}
			//logger.debug("Command registered offline: "+id);
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
		// TODO Auto-generated method stub
		
	}
}
