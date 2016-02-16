package com.feerbox.client.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.feerbox.client.model.Answer;

public class SaveAnswer extends FeerboxDB{
	
	public static int save(Answer answer) {
		System.out.println("going to save answer locally: "+answer.getButton());
		int id = 0;
		Statement statement = null;
		try {
			// create a database connection
			Connection con = getConnection();
			statement = con.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.


			// statement.executeUpdate("drop table if exists person");
			createTableIfNotExists(statement, "Answers");
			statement.executeUpdate(
					"insert into Answers (time, button, upload, reference) values(datetime('now', 'localtime')," + answer.getButton() + ", 0, \""+answer.getReference()+"\")");
			ResultSet rs = statement.executeQuery("SELECT last_insert_rowid() AS rowid FROM Answers LIMIT 1");
			while (rs.next()) {
				id = rs.getInt("rowid");
			}
			System.out.println("Answered registered offline: "+id);
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

	public static void upload(Answer answer) {
		System.out.println("Upload "+answer.getId());
		Statement statement = null;
		try {
			// create a database connection
			Connection con = getConnection();
			statement = con.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.


			// statement.executeUpdate("drop table if exists person");
			createTableIfNotExists(statement, "Answers");
			statement.executeUpdate(
					"update Answers set upload=1 where id="+answer.getId());
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
	}

}
