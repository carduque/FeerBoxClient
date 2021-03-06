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

import com.feerbox.client.model.Answer;

public class ReadAnswer extends FeerboxDB{
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	public static List<Answer> readAnswersNotUploaded() {
		Statement statement = null;
		List<Answer> answers = new ArrayList<Answer>();
		try {
			// create a database connection
			Connection con = getConnection();
			statement = con.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.

			// statement.executeUpdate("drop table if exists person");
			ResultSet rs = statement.executeQuery("select id, time, button, reference, upload from Answers where upload=0 order by id asc limit 100");
			while (rs.next()) {
				Answer answer = new Answer();
				answer.setId(rs.getInt("id"));
				String time = rs.getString("time");
				answer.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(time));
				answer.setButton(rs.getInt("button"));
				answer.setUpload(rs.getInt("upload")==1); //1: true - 0: false
				answer.setReference(rs.getString("reference"));
				answers.add(answer);
			}
		} catch (SQLException e) {
			logger.error("SQLException", e);
			answers = null;
		} catch (ParseException e) {
			logger.error("ParseException", e);
			answers = null;
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				logger.error("SQLException", e);
				answers = null;
			}
		}
		return answers;
	}
	
	public static int countAnswersNotUploaded() {
		Statement statement = null;
		int total = 0;
		try {
			// create a database connection
			Connection con = getConnection();
			statement = con.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.

			// statement.executeUpdate("drop table if exists person");
			ResultSet rs = statement.executeQuery("select count(*) as total from Answers where upload=0");
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
	
	public static Answer readAnswer(Integer id) {
		Statement statement = null;
		Answer answer = new Answer();
		try {
			// create a database connection
			Connection con = getConnection();
			statement = con.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.

			// statement.executeUpdate("drop table if exists person");
			ResultSet rs = statement.executeQuery("select id, time, button, reference from Answers where id=" + id);
			while (rs.next()) {
				answer.setId(rs.getInt("id"));
				String time = rs.getString("time");
				answer.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time));
				answer.setButton(rs.getInt("button"));
				answer.setReference("reference");
			}
		} catch (SQLException e) {
			logger.debug("SQLException", e);
		} catch (ParseException e) {
			logger.debug("ParseException", e);
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				logger.debug("SQLException", e);
			}
		}
		return answer;
	}

	public static long readAnswersDayBefore() {
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
			ResultSet rs = statement.executeQuery("select count(*) as total from Answers where time>=date('"+sdf.format(calendar.getTime())+" 00:00:00') and time<=date('"+sdf.format(calendar.getTime())+" 23:59:59')");
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

	public static long readAnswersDayBefore(LocalTime startingTime, LocalTime closingTime) {
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
			ResultSet rs = statement.executeQuery("select count(*) as total from Answers where time>=date('"+sdf.format(calendar.getTime())+" "+startingTime.getHour()+":"+startingTime.getMinute()+":"+startingTime.getSecond()+
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

	public static Answer getLastSaved() {
		Statement statement = null;
		Answer answer = new Answer();
		try {
			// create a database connection
			Connection con = getConnection();
			statement = con.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.

			// statement.executeUpdate("drop table if exists person");
			ResultSet rs = statement.executeQuery("select id, time, button, reference from Answers order by time desc limit 1");
			while (rs.next()) {
				answer.setId(rs.getInt("id"));
				String time = rs.getString("time");
				answer.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time));
				answer.setButton(rs.getInt("button"));
				answer.setReference("reference");
			}
		} catch (SQLException e) {
			logger.debug("SQLException", e);
		} catch (ParseException e) {
			logger.debug("ParseException", e);
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				logger.debug("SQLException", e);
			}
		}
		return answer;
	}
	
	


}
