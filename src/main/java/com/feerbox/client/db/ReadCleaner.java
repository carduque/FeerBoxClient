package com.feerbox.client.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.feerbox.client.model.Answer;
import com.feerbox.client.model.Cleaner;
import com.feerbox.client.registers.ClientRegister;

public class ReadCleaner extends FeerboxDB{

	public static Cleaner read(Cleaner cleaner) {
		Statement statement = null;
		//Cleaner cleaner = new Cleaner();
		try {
			// create a database connection
			Connection con = getConnection();
			statement = con.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.

			// statement.executeUpdate("drop table if exists person");
			createCleanersTableIfNotExists(statement);
			ResultSet rs = statement.executeQuery("select id, name, surname, serverlastupdate from Cleaners where reference='" + cleaner.getReference()+"'");
			while (rs.next()) {
				cleaner.setId(rs.getInt("id"));
				cleaner.setName(rs.getString("name"));
				cleaner.setSurname(rs.getString("surname"));
				cleaner.setServerLastUpdate(rs.getTimestamp("serverlastupdate"));
			}
		} catch (SQLException e) {
			logger.debug("SQLException", e);
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				logger.debug("SQLException", e);
			}
		}
		return cleaner;
	}

	public static Date getLastUpdate() {
		Statement statement = null;
		Date out = null;
		//Cleaner cleaner = new Cleaner();
		try {
			// create a database connection
			Connection con = getConnection();
			statement = con.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.

			// statement.executeUpdate("drop table if exists person");
			createCleanersTableIfNotExists(statement);
			ResultSet rs = statement.executeQuery("select max(serverlastupdate) from Cleaners");
			while (rs.next()) {
				out = rs.getTimestamp("serverlastupdate");
			}
		} catch (SQLException e) {
			logger.debug("SQLException", e);
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				logger.debug("SQLException", e);
			}
		}
		return out;
	}

}
