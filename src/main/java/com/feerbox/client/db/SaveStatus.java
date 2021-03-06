package com.feerbox.client.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.feerbox.client.model.Status;
import com.google.gson.Gson;

public class SaveStatus extends FeerboxDB{

	public static int save(Status status) {
		//logger.debug("going to save status locally");
		int id = 0;
		Statement statement = null;
		try {
			// create a database connection
			Connection con = getConnection();
			statement = con.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.

			Gson objGson= new Gson();
			String json = objGson.toJson(status.getInfo());
			logger.debug(json);
			// statement.executeUpdate("drop table if exists person");
			statement.executeUpdate(
					"insert into Status (time, reference, internet, upload) values(datetime('now', 'localtime'),\"" + status.getReference()
					+ "\",  '"+json+"\', "
					+status.getUpload()+")");
			ResultSet rs = statement.executeQuery("SELECT last_insert_rowid() AS rowid FROM Status LIMIT 1");
			while (rs.next()) {
				id = rs.getInt("rowid");
			}
			//logger.debug("Status registered offline: "+id);
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
