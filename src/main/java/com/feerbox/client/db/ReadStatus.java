package com.feerbox.client.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.feerbox.client.model.Status;


public class ReadStatus extends FeerboxDB {

	public static Date getLastStatusTime() {
		Status out = new Status();
		Statement statement = null;
		try {
			Connection con = getConnection();
			statement = con.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.
			ResultSet rs = statement.executeQuery("select id, time from Status order by time desc limit 1");
			while (rs.next()) {
				String time = rs.getString("time");
				out.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time));
			}
		} catch (SQLException e) {
			logger.error("SQLException", e);
		} catch (ParseException e) {
			logger.error("ParseException", e);
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				logger.error("SQLException", e);
			}
		}
		return out.getTime();
	}

}
