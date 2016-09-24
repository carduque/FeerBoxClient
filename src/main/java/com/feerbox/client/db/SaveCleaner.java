package com.feerbox.client.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.feerbox.client.model.Cleaner;

public class SaveCleaner extends FeerboxDB {

	public static int save(Cleaner cleaner) {
		//logger.debug("going to save cleaner locally");
		int id = 0;
		Statement statement = null;
		try {
			// create a database connection
			Connection con = getConnection();
			statement = con.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.

			createCleanersTableIfNotExists(statement);
			
			String sql = "insert or replace into Cleaners (id, name, surname, reference, serverid, company, servercreationdate, serverlastupdate) "
					+ " values("
					+ " (select id from Cleaners where serverid="+cleaner.getServerId()+"),"
					+ " '"+cleaner.getName()+"', '"+cleaner.getSurname()+"',"+"'"+cleaner.getReference()+"',"
					+" "+cleaner.getServerId()+", "+cleaner.getCompany()+", STRFTIME('%Y-%m-%d %H:%M:%f', '"+ cleaner.getServerCreationDateFormatted()+"'), "
					+ " STRFTIME('%Y-%m-%d %H:%M:%f', '"+ cleaner.getServerLastUpdateDateFormatted()+"'))";
			//logger.debug(sql);
			statement.executeUpdate(sql);
			ResultSet rs = statement.executeQuery("SELECT last_insert_rowid() AS rowid FROM Cleaners LIMIT 1");
			while (rs.next()) {
				id = rs.getInt("rowid");
			}
			logger.debug("Cleaner saved or updated: "+id);
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

}
