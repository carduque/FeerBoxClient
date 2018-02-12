package com.feerbox.client.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.feerbox.client.model.MAC;

public class ReadMAC extends FeerboxDB {

	public static List<MAC> notUpload() {
		Statement statement = null;
		List<MAC> macs = new ArrayList<MAC>();
		try {
			// create a database connection
			Connection con = getConnection();
			statement = con.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.

			// statement.executeUpdate("drop table if exists person");
			ResultSet rs = statement.executeQuery("select id, time, mac, request, reference, upload from MACS where upload=0 order by id asc limit 100");
			while (rs.next()) {
				MAC mac = new MAC();
				mac.setId(rs.getLong("id"));
				String time = rs.getString("time");
				mac.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(time));
				mac.setMac(rs.getString("mac"));
				mac.setRequest(rs.getInt("request"));
				mac.setFeerBoxReference(rs.getString("reference"));
				mac.setUpload(rs.getInt("upload")==1); //1: true - 0: false
				
				macs.add(mac);
			}
		} catch (SQLException e) {
			logger.error("SQLException", e);
			macs = null;
		} catch (ParseException e) {
			logger.error("ParseException", e);
			macs = null;
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				logger.error("SQLException", e);
				macs = null;
			}
		}
		return macs;
	}

}
