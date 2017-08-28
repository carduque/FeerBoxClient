package com.feerbox.client.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.feerbox.client.model.AlertConfiguration;
import com.feerbox.client.model.AlertThreshold;
import com.feerbox.client.model.AlertTimeTable;

public class SaveAlertConfiguration extends FeerboxDB {

	public static int save(AlertConfiguration alertConfiguration) {
		int id = 0;
		Statement statement = null;
		try {
			// create a database connection
			Connection con = getConnection();
			statement = con.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.
			
			statement.executeUpdate("insert into AlertConfigurations (active, type, name) values ("
					+(alertConfiguration.getActive()?1:0)+","
					+"'"+alertConfiguration.getType().name()+"',"
					+"'"+alertConfiguration.getName()+"'"
					+")");
			
			ResultSet rs = statement.executeQuery("SELECT last_insert_rowid() AS rowid FROM AlertConfigurations LIMIT 1");
			while (rs.next()) {
				id = rs.getInt("rowid");
			}
			for(AlertTimeTable alertTimeTable : alertConfiguration.getAlertTimeTables()){
				statement.executeUpdate(
						"insert into alerttimetables (id_alertconfiguration, startingtime, closingtime, weekday, threshold)"
						+"values("
						+id+","
						+"'"+ alertTimeTable.getStartingTimeDateFormatted()+"',"
						+"'"+ alertTimeTable.getClosingTimeDateFormatted()+"',"
						+alertTimeTable.getWeekDay()+","
						+alertTimeTable.getThreshold()
						+ ")");
				
			}
			for(AlertThreshold alertThreshold : alertConfiguration.getAlertThresholds()){
				statement.executeUpdate(
						"insert into alertthresholds (id_alertconfiguration, threshold, type, weekday)"
						+"values("
						+id+","
						+alertThreshold.getThreshold()+","
						+"'"+alertThreshold.getType().name()+"',"
						+alertThreshold.getWeekDay()
						+ ")");
				
			}
			logger.debug("Alert saved: "+id);
		} catch (SQLException e) {
			logger.error("SQLException", e);
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				logger.error("SQLException", e);
			}
		}
		return id;
	}

}
