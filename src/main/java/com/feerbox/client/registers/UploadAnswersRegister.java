package com.feerbox.client.registers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.feerbox.client.db.FeerboxDB;
import com.feerbox.client.db.SaveAnswer;
import com.feerbox.client.model.Answer;

public class UploadAnswersRegister extends Thread {
	public void run(){
		if(InternetAccess.getInstance().getAccess()){
			System.out.println("Going to update answers "+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			List<Answer> list = FeerboxDB.readAnswersNotUploaded();
			for(Answer answer: list){
				boolean ok = SaveAnswer.saveOnServer(answer);
				if(ok){
					SaveAnswer.markAsUploaded(answer);
				}
			}
		}
	}

}
