package com.feerbox.client;

import static org.junit.Assert.*;

import org.junit.Test;

import com.feerbox.client.db.ReadAnswer;
import com.feerbox.client.db.SaveAnswer;
import com.feerbox.client.model.Answer;
import com.feerbox.client.registers.InternetAccess;

public class SaveLocalTest {

	@Test
	public void testLocal1() {
		InternetAccess.getInstance().setAccess(false);
		Answer answer = new Answer();
		answer.setButton(3);
		Integer id = SaveAnswer.save(answer);
		answer = ReadAnswer.readAnswer(id);
		assertTrue(answer.getButton() == 3);
	}
	
	@Test
	public void testLocal2() {
		InternetAccess.getInstance().setAccess(false);
		Answer answer = new Answer();
		answer.setButton(4);
		Integer id = SaveAnswer.save(answer);
		answer = ReadAnswer.readAnswer(id);
		assertTrue(answer.getButton() == 4);
	}
	

}
