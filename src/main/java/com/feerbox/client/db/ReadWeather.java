package com.feerbox.client.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.feerbox.client.model.Command;
import com.feerbox.client.model.CounterPeople;
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
	
	public static int notUploadTotal() {
		Statement statement = null;
		int total = 0;
		try {
			// create a database connection
			Connection con = getConnection();
			statement = con.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.

			ResultSet rs = statement.executeQuery("select count(*) as total from WeatherSensor where upload!=1");
			while (rs.next()) {
				total = rs.getInt("total");
			}
		} catch (SQLException e) {
			logger.error("SQLException", e);
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				logger.error("SQLException", e);
			}
		}
		return total;
	}

	public static Weather getLastSaved() {
		Weather out = new Weather();
		Statement statement = null;
		try {
			Connection con = getConnection();
			statement = con.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.
			ResultSet rs = statement.executeQuery("select id, time, temperature, humidity, reference, upload from WeatherSensor order by time desc limit 1");
			while (rs.next()) {
				out.setId(rs.getInt("id"));
				String time = rs.getString("time");
				out.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(time));
				out.setTemperature(rs.getString("temperature"));
				out.setHumidity(rs.getString("humidity"));
				out.setUpload(rs.getInt("upload")==1); //1: true - 0: false
				out.setFeerBoxReference(rs.getString("reference"));
			}
		} catch (SQLException e) {
			logger.error("SQLException", e);
		} catch (ParseException e) {
			logger.error("ParseException", e);
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				logger.error("SQLException", e);
			}
		}
		return out;
	}

}
