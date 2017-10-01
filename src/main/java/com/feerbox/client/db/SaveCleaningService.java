package com.feerbox.client.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.feerbox.client.model.CleaningService;

public class SaveCleaningService extends FeerboxDB{

	public static Integer save(CleaningService cleaningService) {
		logger.debug("going to save cleaningService locally: "+cleaningService.getCleanerReference());
		int id = 0;
		Statement statement = null;
		try {
			// create a database connection
			Connection con = getConnection();
			statement = con.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.


			// statement.executeUpdate("drop table if exists person");
			statement.executeUpdate(
					"insert into CleaningServices (time, cleanerReference, feerBoxReference, upload) values(STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW', 'localtime'),\"" + cleaningService.getCleanerReference() + "\", \""+cleaningService.getFeerboxReference()+"\", 0)");
			ResultSet rs = statement.executeQuery("SELECT last_insert_rowid() AS rowid FROM CleaningServices LIMIT 1");
			while (rs.next()) {
				id = rs.getInt("rowid");
			}
			logger.debug("CleaningService registered offline: "+id);
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

	public static void upload(CleaningService cleaningService) {
		logger.debug("Upload cleaningService"+cleaningService.getId());
		Statement statement = null;
		try {
			// create a database connection
			Connection con = getConnection();
			statement = con.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.

			// statement.executeUpdate("drop table if exists person");
			statement.executeUpdate("update CleaningServices set upload=1 where id="+cleaningService.getId());
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

}
