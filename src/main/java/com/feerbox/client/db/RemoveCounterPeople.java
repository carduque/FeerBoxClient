package com.feerbox.client.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.feerbox.client.model.CounterPeople;

public class RemoveCounterPeople extends FeerboxDB {

	public static int delete(CounterPeople counterpeople) {
		PreparedStatement preparedStatement = null;
		int out = 0;
		//Cleaner cleaner = new Cleaner();
		try {
			// create a database connection
			Connection con = getConnection();
			//createCleanersTableIfNotExists(statement);
			preparedStatement = con.prepareStatement("delete from CounterPeople where id = ?");
			preparedStatement.setQueryTimeout(30); // set timeout to 30 sec.
			 preparedStatement.setLong(1, counterpeople.getId());
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
