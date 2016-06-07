package com.feerbox.client.registers;

import java.util.List;

import org.apache.log4j.Logger;

import com.feerbox.client.db.ReadAnswer;
import com.feerbox.client.db.SaveAnswer;
import com.feerbox.client.model.Answer;
import com.feerbox.client.services.SaveAnswerService;

public class UploadAnswersRegister extends Thread {
	final static Logger logger = Logger.getLogger(UploadAnswersRegister.class);
	public void run(){
		if(InternetAccess.getInstance().getAccess()){
			logger.debug("Going to update answers");
			List<Answer> list = ReadAnswer.readAnswersNotUploaded();
			if(list!=null){
				for(Answer answer: list){
					ClientRegister.getInstance().setAnswersUploaded(false);
					boolean ok = SaveAnswerService.saveAnswerInternet(answer);
					logger.debug("Upload to Internet? "+ok);
					if(ok){
						SaveAnswer.upload(answer);
					}
				}
			}
		}
		//Nothing to update
		ClientRegister.getInstance().setAnswersUploaded(true);
	}

}
