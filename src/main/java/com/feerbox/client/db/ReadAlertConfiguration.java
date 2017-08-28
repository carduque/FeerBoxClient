package com.feerbox.client.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.feerbox.client.model.AlertConfiguration;
import com.feerbox.client.model.AlertThreshold;
import com.feerbox.client.model.AlertTimeTable;

public class ReadAlertConfiguration extends FeerboxDB {

	public static Map<String, AlertConfiguration> readAll() {
		Statement statement = null;
		Map<String, AlertConfiguration> alerts = new HashMap<String, AlertConfiguration>();
		try {
			// create a database connection
			Connection con = getConnection();
			statement = con.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.
			
			ResultSet rs = statement.executeQuery("select id, name, active, type from AlertConfigurations where active=1");
			while (rs.next()) {
				AlertConfiguration config = new AlertConfiguration();
				config.setId(rs.getInt("id"));
				config.setName(rs.getString("name"));
				config.setActive(rs.getInt("active")==1);
				String type = rs.getString("type");
				ResultSet rs2 = statement.executeQuery("select startingtime, closingtime, weekday, threshold from AlertConfigurations ac, alerttimetables at where ac.id=at.id_alertconfiguration and ac.id="+config.getId());
				List<AlertTimeTable> timetables = new ArrayList<AlertTimeTable>();
				while (rs2.next()) {
					AlertTimeTable timeTable = new AlertTimeTable();
					String[] output = rs.getString("startingtime").split(":");
					LocalTime startingTime = LocalTime.of(Integer.parseInt(output[0]), Integer.parseInt(output[1]), Integer.parseInt(output[2]));
					timeTable.setStartingTime(startingTime);
					output = rs.getString("closingtime").split(":");
					LocalTime closingTime = LocalTime.of(Integer.parseInt(output[0]), Integer.parseInt(output[1]), Integer.parseInt(output[2]));
					timeTable.setClosingTime(closingTime);
					timeTable.setThreshold(rs.getLong("threshold"));
					timeTable.setWeekDay(rs.getInt("weekday"));
					timetables.add(timeTable);
				}
				rs2.close();
				config.setAlertTimeTables(timetables);
				ResultSet rs3 = statement.executeQuery("select threshold, at.type, weekday from AlertConfigurations ac, alertthresholds at where ac.id=at.id_alertconfiguration and ac.active=1 and ac.id="+config.getId());
				List<AlertThreshold> alertThresholds = new ArrayList<AlertThreshold>();
				while (rs3.next()) {
					AlertThreshold alertThreshold = new AlertThreshold();
					alertThreshold.setThreshold(rs.getLong("threshold"));
					alertThreshold.setWeekDay(rs.getInt("weekday"));
					alertThreshold.setType(AlertThreshold.TypeAlertThreshold.valueOf(rs.getString("type")));
					alertThresholds.add(alertThreshold);
				}
				config.setAlertThresholds(alertThresholds);
				rs3.close();
				
				AlertConfiguration.TypeAlertConfiguration typeAlertConfiguration = AlertConfiguration.TypeAlertConfiguration.valueOf(type);
				switch(typeAlertConfiguration){
					case ANSWERS: alerts.put(AlertConfiguration.TypeAlertConfiguration.ANSWERS.name(), config);
						break;
					case CLIENT: alerts.put(AlertConfiguration.TypeAlertConfiguration.CLIENT.name(), config);
						break;
					case COUNTERPEOPLE: alerts.put(AlertConfiguration.TypeAlertConfiguration.COUNTERPEOPLE.name(), config);
						break;
					case RASPBIAN: alerts.put(AlertConfiguration.TypeAlertConfiguration.RASPBIAN.name(), config);
						break;
					default:
						break;
				}
			}
			rs.close();
			
		} catch (SQLException e) {
			logger.debug("SQLException", e);
		} catch (NumberFormatException e) {
			logger.debug("NumberFormatException", e);
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				logger.debug("SQLException", e);
			}
		}
		return alerts;
	}

}
