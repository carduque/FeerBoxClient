package com.feerbox.client.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.feerbox.client.model.CounterPeople;
import com.feerbox.client.registers.ClientRegister;

public class SaveCounterPeople extends FeerboxDB {

	public static long save(CounterPeople counterPeople) {
		//logger.debug("going to save counterPeople locally");
		long id = 0;
		Statement statement = null;
		try {
			// create a database connection
			Connection con = getConnection();
			statement = con.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.

			int upload = 0;
			if(counterPeople.getUpload()){
				upload=1;
			}
			String sql = "insert into CounterPeople (time, distance, reference, type, upload) "
					+ "values(STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW', 'localtime')," + counterPeople.getDistance() + ",  '"+counterPeople.getFeerBoxReference()+"', '"+counterPeople.getType().name()+"',"+upload+")";
			//logger.debug(sql);
			statement.executeUpdate(sql);
			ResultSet rs = statement.executeQuery("SELECT last_insert_rowid() AS rowid FROM CounterPeople LIMIT 1");
			while (rs.next()) {
				id = rs.getLong("rowid");
			}
			//logger.debug("CounterPeople registered offline: "+id);
		} catch (SQLException e) {
			logger.error("SQLException", e);
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				logger.error("SQLException", e);
			}
		}
		ClientRegister.getInstance().setLastCPSaved(counterPeople.getTime());
		return id;
	}
	
	public static void upload(CounterPeople counterPeople) {
		upload(counterPeople, 1);
	}
	
	public static void upload(CounterPeople counterPeople, int upload) {
		logger.debug("Upload CounterPeople "+counterPeople.getId());
		Statement statement = null;
		try {
			// create a database connection
			Connection con = getConnection();
			statement = con.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.

			// statement.executeUpdate("drop table if exists person");
			statement.executeUpdate("update CounterPeople set upload="+upload+" where id="+counterPeople.getId());
		} catch (SQLException e) {
			logger.error("SQLException", e);
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				logger.error("SQLException", e);
			}
		}
	}

	public static void uploadList(String ids) {
		//logger.debug("Upload CounterPeopleBulky");
		Statement statement = null;
		try {
			// create a database connection
			Connection con = getConnection();
			statement = con.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.

			// statement.executeUpdate("drop table if exists person");
			statement.executeUpdate("update CounterPeople set upload=1 where id in ("+ids+")");
		} catch (SQLException e) {
			logger.error("SQLException", e);
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				logger.error("SQLException", e);
			}
		}
		
	}

	/*
	 * To be used only for SDI boxes
	 */
	public static long saveDelayed(CounterPeople counterPeople) {
		//logger.debug("going to save counterPeople locally");
		long id = 0;
		Statement statement = null;
		try {
			// create a database connection
			Connection con = getConnection();
			statement = con.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.

			int upload = 0;
			if(counterPeople.getUpload()){
				upload=1;
			}
			String sql = "insert into CounterPeople (time, distance, reference, type, upload) "
					+ "values(STRFTIME('%Y-%m-%d %H:%M:%f', '"+ counterPeople.getTimeFormatted()+"')," + counterPeople.getDistance() + ",  '"+counterPeople.getFeerBoxReference()+"', '"+counterPeople.getType().name()+"',"+upload+")";
			//logger.debug(sql);
			statement.executeUpdate(sql);
			ResultSet rs = statement.executeQuery("SELECT last_insert_rowid() AS rowid FROM CounterPeople LIMIT 1");
			while (rs.next()) {
				id = rs.getLong("rowid");
			}
			//logger.debug("CounterPeople registered offline: "+id);
		} catch (SQLException e) {
			logger.error("SQLException", e);
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				logger.error("SQLException", e);
			}
		}
		ClientRegister.getInstance().setLastCPSaved(counterPeople.getTime());
		return id;
	}

}
