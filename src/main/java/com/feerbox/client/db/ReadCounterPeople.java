package com.feerbox.client.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.feerbox.client.model.CounterPeople;

public class ReadCounterPeople extends FeerboxDB {
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

}
