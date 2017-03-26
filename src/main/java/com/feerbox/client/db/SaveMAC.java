package com.feerbox.client.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.feerbox.client.model.MAC;

public class SaveMAC extends FeerboxDB {

	public static void upload(MAC mac) {
		logger.debug("Upload MACs"+mac.getId());
		Statement statement = null;
		try {
			// create a database connection
			Connection con = getConnection();
			statement = con.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.

			// statement.executeUpdate("drop table if exists person");
			createMACTableIfNotExists();
			statement.executeUpdate("update MACS set upload=1 where id="+mac.getId());
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
