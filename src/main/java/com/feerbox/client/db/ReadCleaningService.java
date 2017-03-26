package com.feerbox.client.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.feerbox.client.model.CleaningService;

public class ReadCleaningService extends FeerboxDB {

	public static List<CleaningService> notUploaded() {
		Statement statement = null;
		List<CleaningService> cleaningServices = new ArrayList<CleaningService>();
		try {
			// create a database connection
			Connection con = getConnection();
			statement = con.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.

			// statement.executeUpdate("drop table if exists person");
			createCleaningServicesTableIfNotExists(statement);
			ResultSet rs = statement.executeQuery("select id, time, cleanerReference, feerBoxReference, upload from CleaningServices where upload=0 order by id asc limit 100");
			while (rs.next()) {
				CleaningService cleaningService = new CleaningService();
				cleaningService.setId(rs.getInt("id"));
				String time = rs.getString("time");
				cleaningService.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(time));
				cleaningService.setCleanerReference(rs.getString("cleanerReference"));
				cleaningService.setFeerboxReference(rs.getString("feerBoxReference"));
				cleaningService.setUpload(rs.getInt("upload")==1); //1: true - 0: false
				
				cleaningServices.add(cleaningService);
			}
		} catch (SQLException e) {
			logger.error("SQLException", e);
			cleaningServices = null;
		} catch (ParseException e) {
			logger.error("ParseException", e);
			cleaningServices = null;
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				logger.error("SQLException", e);
				cleaningServices = null;
			}
		}
		return cleaningServices;
	}

}
