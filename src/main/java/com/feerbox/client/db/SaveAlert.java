package com.feerbox.client.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.feerbox.client.model.Alert;

public class SaveAlert extends FeerboxDB {

	public static int save(Alert alert) {
		int id = 0;
		Statement statement = null;
		try {
			// create a database connection
			Connection con = getConnection();
			statement = con.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.
			
			statement.executeUpdate(
					"insert into Alerts (severity, generator, threshold, name, reference, time, type, weekday) values("
					+ "'"+alert.getSeverity().name()+"',"
					+ "'"+alert.getGenerator().name()+"',"
					+ alert.getThreshold()+","
					+ "'"+alert.getName()+"',"
					+ "'"+alert.getReference()+"',"
					+ "STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW', 'localtime')," 
					+ "'"+alert.getType().name()+"',"
					+alert.getWeekday()
					+")");
			ResultSet rs = statement.executeQuery("SELECT last_insert_rowid() AS rowid FROM Alerts LIMIT 1");
			while (rs.next()) {
				id = rs.getInt("rowid");
			}
			logger.debug("Alert saved: "+id);
		} catch (SQLException e) {
			logger.error("SQLException", e);
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				logger.error("SQLException", e);
			}
		}
		return id;
	}

}
