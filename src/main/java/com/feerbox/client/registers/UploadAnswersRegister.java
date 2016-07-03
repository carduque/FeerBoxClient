package com.feerbox.client.registers;

import java.util.List;

import org.apache.log4j.Logger;

import com.feerbox.client.db.ReadAnswer;
import com.feerbox.client.db.ReadCleaningService;
import com.feerbox.client.db.SaveAnswer;
import com.feerbox.client.db.SaveCleaningService;
import com.feerbox.client.model.Answer;
import com.feerbox.client.model.CleaningService;
import com.feerbox.client.services.CleaningServiceService;
import com.feerbox.client.services.SaveAnswerService;

public class UploadAnswersRegister extends Thread {
	final static Logger logger = Logger.getLogger(UploadAnswersRegister.class);
	public void run(){
		if(InternetAccess.getInstance().getAccess()){
			List<Answer> list = ReadAnswer.readAnswersNotUploaded();
			if(list!=null && list.size()!=0){
				logger.debug("Going to update answers");
				for(Answer answer: list){
					ClientRegister.getInstance().setAnswersUploaded(false);
					boolean ok = SaveAnswerService.saveAnswerInternet(answer);
					logger.debug("Upload to Internet? "+ok);
					if(ok){
						SaveAnswer.upload(answer);
					}
				}
			}
			if(ClientRegister.getInstance().getCleaningServiceEnable()){
				List<CleaningService> list2 = ReadCleaningService.notUploaded();
				if(list2!=null && list2.size()!=0){
					logger.debug("Going to update cleaningServices");
					for(CleaningService cleaningService: list2){
						boolean ok = CleaningServiceService.saveServer(cleaningService);
						logger.debug("Upload to Internet? "+ok);
						if(ok){
							SaveCleaningService.upload(cleaningService);
						}
					}
				}
			}
		}
		//Nothing to update
		ClientRegister.getInstance().setAnswersUploaded(true);
	}

}
