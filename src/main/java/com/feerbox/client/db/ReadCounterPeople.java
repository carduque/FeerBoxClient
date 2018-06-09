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
import com.feerbox.client.model.Weather;

public class ReadCounterPeople extends FeerboxDB {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public static List<CounterPeople> notUpload(int limit, int status) {
		Statement statement = null;
		List<CounterPeople> counterPeoples = new ArrayList<CounterPeople>();
		try {
			// create a database connection
			Connection con = getConnection();
			statement = con.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.

			// statement.executeUpdate("drop table if exists person");
			ResultSet rs = statement.executeQuery("select id, time, distance, type, reference, upload from counterPeople where upload="+status+" order by id asc limit "+limit);
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
	
	public static List<CounterPeople> notUploadSafe(int limit) {
		Statement statement = null;
		List<CounterPeople> counterPeoples = new ArrayList<CounterPeople>();
		// create a database connection
		Connection con = getConnection();
		try {
			con.setAutoCommit(false);
			statement = con.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.

			// statement.executeUpdate("drop table if exists person");
			String ids = "";
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
				
				ids+=counterPeople.getId()+",";
				counterPeoples.add(counterPeople);
			}
			if(counterPeoples.size()>0){
				ids = ids.substring(0, ids.length() - 1); //Remove last comma
				statement.executeUpdate("update CounterPeople set upload=2 where id in ("+ids+")");
				con.commit();
			}
		} catch (SQLException e) {
			logger.error("SQLException", e);
			counterPeoples = null;
		} catch (ParseException e) {
			logger.error("ParseException", e);
			counterPeoples = null;
		} finally {
			try {
				con.setAutoCommit(true);
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
			ResultSet rs = statement.executeQuery("select count(*) as total from counterPeople where upload!=1");
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

	public static CounterPeople getLastSaved() {
		CounterPeople counterPeople = new CounterPeople();
		Statement statement = null;
		try {
			Connection con = getConnection();
			statement = con.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.
			ResultSet rs = statement.executeQuery("select id, time, distance, type, reference, upload from counterPeople where upload=0 order by time desc limit 1");
			while (rs.next()) {
				counterPeople.setId(rs.getLong("id"));
				String time = rs.getString("time");
				counterPeople.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(time));
				counterPeople.setDistance(rs.getDouble("distance"));
				counterPeople.setType(CounterPeople.Type.valueOf(rs.getString("type")));
				counterPeople.setFeerBoxReference(rs.getString("reference"));
				counterPeople.setUpload(rs.getInt("upload")==1); //1: true - 0: false
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
		return counterPeople;
	}

}
