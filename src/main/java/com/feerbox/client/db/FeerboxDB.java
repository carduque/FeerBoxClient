package com.feerbox.client.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class FeerboxDB {
	private static boolean answersTableCreated = false;
	private static boolean statusTableCreated = false;
	private static Connection connection;

	
	public static Connection getConnection() {
		try {
			if(connection==null || connection.isClosed()){
				createConnection();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connection;
	}

	private static void createConnection() {
		connection = null;
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:/opt/pi4j/examples/feerbox2.db");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected static void createAnswersTableIfNotExists(Statement statement) throws SQLException {
		if (!answersTableCreated) {
			//System.out.println("Creating table if not exists...");
			//table = ClientRegister.getInstance().getCustomer();
			String sql = "create table if not exists Answers (id INTEGER PRIMARY KEY AUTOINCREMENT, time timestamp, button integer, reference varchar, upload integer)";
			//System.out.println(sql);
			statement.executeUpdate(sql);
			answersTableCreated = true;
		}
	}
	
	protected static void createStatusTableIfNotExists(Statement statement) throws SQLException {
		if (!statusTableCreated) {
			//System.out.println("Creating table if not exists...");
			//table = ClientRegister.getInstance().getCustomer();
			String sql = "create table if not exists Status (id INTEGER PRIMARY KEY AUTOINCREMENT, time timestamp, reference varchar, internet varchar)";
			//System.out.println(sql);
			statement.executeUpdate(sql);
			statusTableCreated = true;
		}
	}


}
