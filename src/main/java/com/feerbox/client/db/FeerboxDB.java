package com.feerbox.client.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class FeerboxDB {
	private static boolean tableCreated = false;
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

	protected static void createTableIfNotExists(Statement statement, String tableName) throws SQLException {
		if (!tableCreated) {
			System.out.println("Creating table if not exists...");
			//table = ClientRegister.getInstance().getCustomer();
			String sql = "create table if not exists " + tableName
					+ " (id INTEGER PRIMARY KEY AUTOINCREMENT, time timestamp, button integer, reference varchar, upload integer)";
			//System.out.println(sql);
			statement.executeUpdate(sql);
			tableCreated = true;
		}
	}


}
