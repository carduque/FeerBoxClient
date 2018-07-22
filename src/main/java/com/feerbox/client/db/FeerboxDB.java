package com.feerbox.client.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

public class FeerboxDB {
	private static Connection connection;
	protected final static Logger logger = Logger.getLogger(FeerboxDB.class);

	public static Connection getConnection() {
		try {
			if (connection == null || connection.isClosed()) {
				if (System.getenv("TEST") != null || System.getProperty("TEST")!=null) {
					createConnectionTEST();
				} else if(System.getProperty("SDI2Feerbox")!=null){
					createConnectionSDI2Feerbox();
				} else {
					createConnection();
				}
				createTables();
			}
		} catch (SQLException e) {
			logger.error("SQLException", e);
		}
		return connection;
	}

	private static void createConnectionSDI2Feerbox() {
		connection = null;
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection(
					"jdbc:sqlite:"+System.getProperty("SDI2Feerbox"));
			connection.setAutoCommit(true);
		} catch (SQLException e) {
			logger.error("SQLException", e);
		} catch (ClassNotFoundException e) {
			logger.error("ClassNotFoundException", e);
		}
	}

	private static void createTables() {
		try {
			createMACTableIfNotExists();
			//createAlertConfigurationTableIfNotExists();
			//createAlertTimeTablesTableIfNotExists();
			//createAlertThresholdsTableIfNotExists();
			createAlertsTableIfNotExists();
			createWeatherSensorTableIfNotExists();
			createCleanersTableIfNotExists();
			createCleaningServicesTableIfNotExists();
			createAnswersTableIfNotExists();
			createStatusTableIfNotExists();
			createCommandsTableIfNotExists();
			createCounterPeopleTableIfNotExists();
		} catch (SQLException e) {
			logger.error("SQLException", e);
		}

	}

	private static void createWeatherSensorTableIfNotExists() throws SQLException {
		Statement statement = connection.createStatement();
		statement.setQueryTimeout(30); // set timeout to 30 sec.
		// severity, generator, threshold, name, reference, time, type, weekday
		String sql = "create table if not exists WeatherSensor (id INTEGER PRIMARY KEY AUTOINCREMENT, time timestamp, temperature varchar, humidity varchar, reference varchar, upload integer)";
		// logger.debug(sql);
		statement.executeUpdate(sql);
	}

	private static void createConnection() {
		connection = null;
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager
					.getConnection("jdbc:sqlite:/opt/FeerBoxClient/FeerBoxClient/db/feerboxclient.db");
			// logger.info(connection.getClientInfo());
			connection.setAutoCommit(true);
		} catch (SQLException e) {
			logger.error("SQLException", e);
		} catch (ClassNotFoundException e) {
			logger.error("ClassNotFoundException", e);
		}
	}

	private static void createConnectionTEST() {
		connection = null;
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection(
					"jdbc:sqlite:C:\\Users\\cduquemarcos\\documentation\\Personal\\FeerBox\\clients\\mercat\\CP2017001\\feerboxclient.db");
			connection.setAutoCommit(true);
		} catch (SQLException e) {
			logger.error("SQLException", e);
		} catch (ClassNotFoundException e) {
			logger.error("ClassNotFoundException", e);
		}
	}

	private static void createAnswersTableIfNotExists() throws SQLException {
		Statement statement = connection.createStatement();
		statement.setQueryTimeout(30); // set timeout to 30 sec.
		// logger.debug("Creating table if not exists...");
		// table = ClientRegister.getInstance().getCustomer();
		String sql = "create table if not exists Answers (id INTEGER PRIMARY KEY AUTOINCREMENT, time timestamp, button integer, reference varchar, upload integer)";
		// logger.debug(sql);
		statement.executeUpdate(sql);
	}

	private static void createMACTableIfNotExists() throws SQLException {
		Statement statement = connection.createStatement();
		statement.setQueryTimeout(30); // set timeout to 30 sec.
		// logger.debug("Creating table if not exists...");
		// table = ClientRegister.getInstance().getCustomer();
		String sql = "create table if not exists MACS (id INTEGER PRIMARY KEY AUTOINCREMENT, time timestamp, mac varchar, request integer, reference varchar, upload integer)";
		// logger.debug(sql);
		statement.executeUpdate(sql);
	}

	private static void createCounterPeopleTableIfNotExists() throws SQLException {
		Statement statement = connection.createStatement();
		statement.setQueryTimeout(30); // set timeout to 30 sec.
		// logger.debug("Creating table if not exists...");
		// table = ClientRegister.getInstance().getCustomer();
		String sql = "create table if not exists CounterPeople (id INTEGER PRIMARY KEY AUTOINCREMENT, time timestamp, distance float, reference varchar, type varchar, upload integer)";
		// logger.debug(sql);
		statement.executeUpdate(sql);
	}

	private static void createCleaningServicesTableIfNotExists() throws SQLException {
		Statement statement = connection.createStatement();
		// logger.debug("Creating table if not exists...");
		// table = ClientRegister.getInstance().getCustomer();
		String sql = "create table if not exists CleaningServices (id INTEGER PRIMARY KEY AUTOINCREMENT, time timestamp, cleanerReference varchar, feerBoxReference varchar, upload integer)";
		// logger.debug(sql);
		statement.executeUpdate(sql);
	}

	private static void createCleanersTableIfNotExists() throws SQLException {
		Statement statement = connection.createStatement();
		// logger.debug("Creating table if not exists...");
		// table = ClientRegister.getInstance().getCustomer();
		String sql = "create table if not exists Cleaners (id INTEGER PRIMARY KEY AUTOINCREMENT, name varchar, surname varchar, reference varchar, serverlastupdate timestamp, company integer, servercreationdate timestamp, serverid integer)";
		// logger.debug(sql);
		statement.executeUpdate(sql);
	}

	private static void createStatusTableIfNotExists() throws SQLException {
		Statement statement = connection.createStatement();
		statement.setQueryTimeout(30); // set timeout to 30 sec.		
		// logger.debug("Creating table if not exists...");
		// table = ClientRegister.getInstance().getCustomer();
		String sql = "create table if not exists Status (id INTEGER PRIMARY KEY AUTOINCREMENT, time timestamp, reference varchar, internet varchar, upload integer)";
		// logger.debug(sql);
		statement.executeUpdate(sql);
	}

	private static void createCommandsTableIfNotExists() throws SQLException {
		Statement statement = connection.createStatement();
		statement.setQueryTimeout(30); // set timeout to 30 sec.		
		// logger.debug("Creating table if not exists...");
		// table = ClientRegister.getInstance().getCustomer();
		String sql = "create table if not exists Commands (id INTEGER PRIMARY KEY AUTOINCREMENT, time timestamp, command varchar, output varchar, startTime timestamp, finishTime timestamp, serverId integer, upload integer, serverCreationTime timestamp, restart integer, parameter varchar)";
		// logger.debug(sql);
		statement.executeUpdate(sql);
	}

	private static void createAlertsTableIfNotExists() throws SQLException {
		Statement statement = connection.createStatement();
		statement.setQueryTimeout(30); // set timeout to 30 sec.
		// severity, generator, threshold, name, reference, time, type, weekday
		String sql = "create table if not exists Alerts (id INTEGER PRIMARY KEY AUTOINCREMENT, severity varchar, generator varchar, threshold integer, name varchar, time timestamp, type varchar, active integer, reference varchar, upload integer)";
		// logger.debug(sql);
		statement.executeUpdate(sql);
	}

	private static void createAlertConfigurationTableIfNotExists() throws SQLException {
		Statement statement = connection.createStatement();
		statement.setQueryTimeout(30); // set timeout to 30 sec.
		// severity, generator, threshold, name, reference, time, type, weekday
		String sql = "create table if not exists AlertConfigurations (id INTEGER PRIMARY KEY AUTOINCREMENT, active integer, type varchar, name varchar)";
		// logger.debug(sql);
		statement.executeUpdate(sql);
	}

	private static void createAlertTimeTablesTableIfNotExists() throws SQLException {
		Statement statement = connection.createStatement();
		statement.setQueryTimeout(30); // set timeout to 30 sec.
		String sql = "create table if not exists alerttimetables (id_alertconfiguration INTEGER, startingtime timestamp, closingtime timestamp, weekday integer, threshold integer)";
		// logger.debug(sql);
		statement.executeUpdate(sql);
	}

	private static void createAlertThresholdsTableIfNotExists() throws SQLException {
		Statement statement = connection.createStatement();
		statement.setQueryTimeout(30); // set timeout to 30 sec.
		String sql = "create table if not exists alertthresholds (id_alertconfiguration INTEGER, threshold integer, type varchar, weekday integer)";
		// logger.debug(sql);
		statement.executeUpdate(sql);
	}

}
