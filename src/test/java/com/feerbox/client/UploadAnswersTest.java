package com.feerbox.client;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.feerbox.client.db.ReadAnswer;
import com.feerbox.client.model.Answer;
import com.feerbox.client.registers.InternetAccess;
import com.feerbox.client.registers.InformationServerRegister;
import com.feerbox.client.services.SaveAnswerService;

public class UploadAnswersTest {

	@Test
	public void testLocal1() {
		InternetAccess.getInstance().setAccess(false);
		Answer answer1 = new Answer();
		answer1.setButton(3);
		Integer id1 = SaveAnswerService.saveAnswer(3);
		Integer id2 = SaveAnswerService.saveAnswer(1);
		Integer id3 = SaveAnswerService.saveAnswer(2);
		InternetAccess.getInstance().setAccess(true);
		InformationServerRegister uploadAnswersRegister = new InformationServerRegister();
		uploadAnswersRegister.run();
		List<Answer> list = ReadAnswer.readAnswersNotUploaded();
		assertTrue(list.size()==0);
	}

}
