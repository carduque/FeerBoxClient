package com.feerbox.client.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.feerbox.client.model.Weather;

public class SaveWeather extends FeerboxDB {

	public static void uploaded(Weather weather) {
		Statement statement = null;
		try {
			// create a database connection
			Connection con = getConnection();
			statement = con.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.
			statement.executeUpdate("update WeatherSensor set upload=1 where id="+weather.getId());
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
