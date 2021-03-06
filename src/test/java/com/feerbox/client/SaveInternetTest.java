package com.feerbox.client;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.feerbox.client.db.ReadAnswer;
import com.feerbox.client.db.SaveAnswer;
import com.feerbox.client.model.Answer;
import com.feerbox.client.registers.InternetAccess;
import com.feerbox.client.registers.InformationServerRegister;

public class SaveInternetTest {
	@Test
	public void testInternet1() throws InterruptedException {
		InternetAccess.getInstance().setAccess(false);
		Answer answer = new Answer();
		answer.setButton(3);
		answer.setReference("2015001");
		Integer id = SaveAnswer.save(answer);
		//Thread.sleep(60000);
		InternetAccess.getInstance().setAccess(true);
		InformationServerRegister uploadAnswersRegister = new InformationServerRegister();
		uploadAnswersRegister.run();
		List<Answer> list = ReadAnswer.readAnswersNotUploaded();
		assertTrue(list.size()==0);
		answer = ReadAnswer.readAnswer(id);
		System.out.println(id+" Local:"+answer.getTimeText());
		
	}
}
