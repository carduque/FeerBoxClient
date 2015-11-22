package com.feerbox.client;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.feerbox.client.db.FeerboxDB;
import com.feerbox.client.db.SaveAnswer;
import com.feerbox.client.model.Answer;
import com.feerbox.client.registers.InternetAccess;
import com.feerbox.client.registers.UploadAnswersRegister;

public class UploadAnswersTest {

	@Test
	public void testLocal1() {
		InternetAccess.getInstance().setAccess(false);
		Integer id1 = SaveAnswer.saveAnswer(3);
		Integer id2 = SaveAnswer.saveAnswer(1);
		Integer id3 = SaveAnswer.saveAnswer(2);
		InternetAccess.getInstance().setAccess(true);
		UploadAnswersRegister uploadAnswersRegister = new UploadAnswersRegister();
		uploadAnswersRegister.run();
		List<Answer> list = FeerboxDB.readAnswersNotUploaded();
		assertTrue(list.size()==0);
	}

}
