package com.feerbox.client.db;

import com.feerbox.client.model.Answer;

public class ReadAnswer{

	public static Answer readAnswer(Integer id) {
		return FeerboxDB.readAnswer(id);
	}

}
