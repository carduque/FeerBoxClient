package com.feerbox.client.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.feerbox.client.model.CounterPeople;

public class ReadCounterPeople extends FeerboxDB {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public static List<CounterPeople> notUpload(int limit) {
		Statement statement = null;
		List<CounterPeople> counterPeoples = new ArrayList<CounterPeople>();
		try {
			// create a database connection
			Connection con = getConnection();
			statement = con.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.

			// statement.executeUpdate("drop table if exists person");
			createCounterPeopleTableIfNotExists();
			ResultSet rs = statement.executeQuery("select id, time, distance, type, reference, upload from counterPeople where upload=0 order by id asc limit "+limit);
			while (rs.next()) {
				CounterPeople counterPeople = new CounterPeople();
				counterPeople.setId(rs.getLong("id"));
				String time = rs.getString("time");
				counterPeople.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(time));
				counterPeople.setDistance(rs.getDouble("distance"));
				counterPeople.setType(CounterPeople.Type.valueOf(rs.getString("type")));
				counterPeople.setFeerBoxReference(rs.getString("reference"));
				counterPeople.setUpload(rs.getInt("upload")==1); //1: true - 0: false
				
				counterPeoples.add(counterPeople);
			}
		} catch (SQLException e) {
			logger.error("SQLException", e);
			counterPeoples = null;
		} catch (ParseException e) {
			logger.error("ParseException", e);
			counterPeoples = null;
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				logger.error("SQLException", e);
				counterPeoples = null;
			}
		}
		return counterPeoples;
	}

	public static int notUpload() {
		Statement statement = null;
		int total = 0;
		try {
			// create a database connection
			Connection con = getConnection();
			statement = con.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.

			// statement.executeUpdate("drop table if exists person");
			createCounterPeopleTableIfNotExists();
			ResultSet rs = statement.executeQuery("select count(*) as total from counterPeople where upload=0");
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

	public static long readCounterPeopleDayBefore() {
		Statement statement = null;
		long total = 0;
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -1);
		try {
			// create a database connection
			Connection con = getConnection();
			statement = con.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.

			// statement.executeUpdate("drop table if exists person");
			createCounterPeopleTableIfNotExists();
			ResultSet rs = statement.executeQuery("select count(*) as total from counterpeople where time>=date('"+sdf.format(calendar.getTime())+" 00:00:00') and time<=date('"+sdf.format(calendar.getTime())+" 23:59:59')");
			while (rs.next()) {
				total = rs.getLong("total");
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

	public static long readCounterPeopleDayBefore(LocalTime startingTime, LocalTime closingTime) {
		Statement statement = null;
		long total = 0;
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -1);
		try {
			// create a database connection
			Connection con = getConnection();
			statement = con.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.

			// statement.executeUpdate("drop table if exists person");
			createAnswersTableIfNotExists(statement);
			ResultSet rs = statement.executeQuery("select count(*) as total from counterpeople where time>=date('"+sdf.format(calendar.getTime())+" "+startingTime.getHour()+":"+startingTime.getMinute()+":"+startingTime.getSecond()+
					"') and time<=date('"+sdf.format(calendar.getTime())+" "+closingTime.getHour()+":"+closingTime.getMinute()+":"+closingTime.getSecond()+"')");
			while (rs.next()) {
				total = rs.getLong("total");
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

}
