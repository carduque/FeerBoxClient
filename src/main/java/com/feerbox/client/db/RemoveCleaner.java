package com.feerbox.client.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RemoveCleaner extends FeerboxDB {

	public static int removeOthersCompanies(int companyId) {
		PreparedStatement preparedStatement = null;
		int out = 0;
		//Cleaner cleaner = new Cleaner();
		try {
			// create a database connection
			Connection con = getConnection();
			//createCleanersTableIfNotExists(statement);
			preparedStatement = con.prepareStatement("delete from Cleaners where company = ?");
			preparedStatement.setQueryTimeout(30); // set timeout to 30 sec.
			 preparedStatement.setInt(1, companyId);
			 out = preparedStatement.executeUpdate();
			
		} catch (SQLException e) {
			logger.error("SQLException", e);
		} finally {
			try {
				preparedStatement.close();
			} catch (SQLException e) {
				logger.error("SQLException", e);
			}
		}
		return out;
	}

}
