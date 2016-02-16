package com.feerbox.client.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.feerbox.client.model.Answer;

public class ReadAnswer extends FeerboxDB{

	public static List<Answer> readAnswersNotUploaded() {
		Statement statement = null;
		List<Answer> answers = new ArrayList<Answer>();
		try {
			// create a database connection
			Connection con = getConnection();
			statement = con.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.

			// statement.executeUpdate("drop table if exists person");
			createTableIfNotExists(statement, "Answers");
			ResultSet rs = statement.executeQuery("select id, time, button, reference, upload from Answers where upload=0");
			while (rs.next()) {
				Answer answer = new Answer();
				answer.setId(rs.getInt("id"));
				String time = rs.getString("time");
				answer.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time));
				answer.setButton(rs.getInt("button"));
				answer.setUpload(rs.getInt("upload")==1); //1: true - 0: false
				answer.setReference(rs.getString("reference"));
				answers.add(answer);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			answers = null;
		} catch (ParseException e) {
			e.printStackTrace();
			answers = null;
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
				answers = null;
			}
		}
		return answers;
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
			createTableIfNotExists(statement, "Answers");
			ResultSet rs = statement.executeQuery("select id, time, button, reference from Answers where id=" + id);
			while (rs.next()) {
				answer.setId(rs.getInt("id"));
				String time = rs.getString("time");
				answer.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time));
				answer.setButton(rs.getInt("button"));
				answer.setReference("reference");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return answer;
	}
	
	


}
