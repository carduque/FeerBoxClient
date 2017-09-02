package com.feerbox.client.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.feerbox.client.model.Weather;

public class ReadWeather extends FeerboxDB {

	public static List<Weather> notUploaded() {
		Statement statement = null;
		List<Weather> weathers = new ArrayList<Weather>();
		try {
			// create a database connection
			Connection con = getConnection();
			statement = con.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.

			ResultSet rs = statement.executeQuery("select id, time, temperature, humidity, reference, upload from WeatherSensor where upload=0 order by id asc limit 100");
			while (rs.next()) {
				Weather weather = new Weather();
				weather.setId(rs.getInt("id"));
				String time = rs.getString("time");
				weather.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(time));
				weather.setTemperature(rs.getString("temperature"));
				weather.setHumidity(rs.getString("humidity"));
				weather.setUpload(rs.getInt("upload")==1); //1: true - 0: false
				weather.setFeerBoxReference(rs.getString("reference"));
				weathers.add(weather);
			}
		} catch (SQLException e) {
			logger.error("SQLException", e);
			weathers = null;
		} catch (ParseException e) {
			logger.error("ParseException", e);
			weathers = null;
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				logger.error("SQLException", e);
				weathers = null;
			}
		}
		return weathers;
	}

}
