package com.feerbox.client.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.feerbox.client.model.Status;

public class SaveStatus extends FeerboxDB{

	public static int save(Status status) {
		//System.out.println("going to save status locally");
		int id = 0;
		Statement statement = null;
		try {
			// create a database connection
			Connection con = getConnection();
			statement = con.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.


			// statement.executeUpdate("drop table if exists person");
			createStatusTableIfNotExists(statement);
			statement.executeUpdate(
					"insert into Status (time, reference, internet) values(datetime('now', 'localtime'),\"" + status.getReference() + "\",  \""+status.getInfo().get(Status.infoKeys.INTERNET)+" - "+status.getInfo().get(Status.infoKeys.IP)+"\")");
			ResultSet rs = statement.executeQuery("SELECT last_insert_rowid() AS rowid FROM Answers LIMIT 1");
			while (rs.next()) {
				id = rs.getInt("rowid");
			}
			//System.out.println("Status registered offline: "+id);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return id;
	}

}
