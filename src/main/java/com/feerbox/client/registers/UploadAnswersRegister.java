package com.feerbox.client.registers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.feerbox.client.db.ReadAnswer;
import com.feerbox.client.db.SaveAnswer;
import com.feerbox.client.model.Answer;
import com.feerbox.client.services.SaveAnswerService;

public class UploadAnswersRegister extends Thread {
	public void run(){
		if(InternetAccess.getInstance().getAccess()){
			System.out.println("Going to update answers "+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			List<Answer> list = ReadAnswer.readAnswersNotUploaded();
			for(Answer answer: list){
				boolean ok = SaveAnswerService.saveAnswerInternet(answer);
				System.out.println("Upload to Internet? "+ok);
				if(ok){
					SaveAnswer.upload(answer);
				}
			}
		}
	}

}
