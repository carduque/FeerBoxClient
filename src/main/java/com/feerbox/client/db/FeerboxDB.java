package com.feerbox.client.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.feerbox.client.model.Answer;
import com.feerbox.client.registers.ClientRegister;

public class FeerboxDB {
	private static boolean tableCreated = false;
	private static String table;
	private static Connection connection;

	protected static int saveAnswer(int buttonNumber) {
		System.out.println("going to save answer locally...");
		int id = 0;
		Statement statement = null;
		try {
			// create a database connection
			statement = createConnection();

			// statement.executeUpdate("drop table if exists person");
			createTableIfNotExists(statement);
			statement.executeUpdate(
					"insert into " + table + " (time, button, upload) values(CURRENT_TIMESTAMP," + buttonNumber + ", 0)");
			ResultSet rs = statement.executeQuery("SELECT last_insert_rowid() AS rowid FROM " + table + " LIMIT 1");
			while (rs.next()) {
				id = rs.getInt("rowid");
			}
			System.out.println("Answered registered offline: "+id);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				statement.close();
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return id;
	}

	private static Statement createConnection() {
		Statement statement = null;
		connection = null;
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:/opt/pi4j/examples/feerbox.db");
			statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return statement;
	}

	private static void createTableIfNotExists(Statement statement) throws SQLException {
		if (!tableCreated) {
			System.out.println("Creating table...");
			table = ClientRegister.getInstance().getCustomer();
			String sql = "create table if not exists " + table
					+ " (id INTEGER PRIMARY KEY AUTOINCREMENT, time timestamp, button integer, upload integer)";
			//System.out.println(sql);
			statement.executeUpdate(sql);
			tableCreated = true;
		}
	}

	public static Answer readAnswer(Integer id) {
		Statement statement = null;
		Answer answer = new Answer();
		try {
			// create a database connection
			statement = createConnection();

			// statement.executeUpdate("drop table if exists person");
			createTableIfNotExists(statement);
			ResultSet rs = statement.executeQuery("select id, time, button from " + table + " where id=" + id);
			while (rs.next()) {
				answer.setId(rs.getInt("id"));
				String time = rs.getString("time");
				answer.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time));
				answer.setButton(rs.getInt("button"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				statement.close();
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return answer;
	}

	public static List<Answer> readAnswersNotUploaded() {
		Statement statement = null;
		List<Answer> answers = new ArrayList<Answer>();
		try {
			// create a database connection
			statement = createConnection();

			// statement.executeUpdate("drop table if exists person");
			createTableIfNotExists(statement);
			ResultSet rs = statement.executeQuery("select id, time, button, upload from " + table + " where upload=0");
			while (rs.next()) {
				Answer answer = new Answer();
				answer.setId(rs.getInt("id"));
				String time = rs.getString("time");
				answer.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time));
				answer.setButton(rs.getInt("button"));
				answer.setUpload(rs.getInt("upload")==1); //1: true - 0: false
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
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
				answers = null;
			}
		}
		return answers;
	}

	public static void saveAnswer(Answer answer) {
		Statement statement = null;
		try {
			// create a database connection
			statement = createConnection();

			// statement.executeUpdate("drop table if exists person");
			createTableIfNotExists(statement);
			int upload = 0;
			if(answer.isUpload()) upload=1;
			statement.executeUpdate(
					"update " + table + " set upload="+upload+" where id="+answer.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				statement.close();
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
