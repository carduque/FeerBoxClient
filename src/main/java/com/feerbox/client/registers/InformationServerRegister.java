package com.feerbox.client.registers;

import java.util.List;
import java.util.concurrent.ScheduledFuture;

import org.apache.log4j.Logger;

import com.feerbox.client.db.ReadAnswer;
import com.feerbox.client.db.ReadCleaningService;
import com.feerbox.client.db.SaveAnswer;
import com.feerbox.client.db.SaveCleaningService;
import com.feerbox.client.model.Answer;
import com.feerbox.client.model.CleaningService;
import com.feerbox.client.model.Command;
import com.feerbox.client.model.CounterPeople;
import com.feerbox.client.model.MAC;
import com.feerbox.client.services.CleaningServiceService;
import com.feerbox.client.services.CommandService;
import com.feerbox.client.services.CounterPeopleService;
import com.feerbox.client.services.MACService;
import com.feerbox.client.services.SaveAnswerService;

public class InformationServerRegister extends Thread {
	final static Logger logger = Logger.getLogger(InformationServerRegister.class);
	private ScheduledFuture<?> future;
	public void run(){
		try {
			if(InternetAccess.getInstance().getAccess()){
				uploadAnswers();
				uploadCleaningService();
				uploadMACs();
				uploadCounterPeople();
				uploadCommandsOutput();
			}
			//Nothing to update
			ClientRegister.getInstance().setAnswersUploaded(true); //Indicate register finished
		} catch (Throwable  t) {
			logger.error("Error in InformationServerRegister");
		}
	}
	private void uploadCommandsOutput() {
		//Check if there are commands finished to send output
		List<Command> commands = CommandService.getCommandsToUpload();
		if(commands!=null && commands.size()!=0) {
			logger.debug("Going to upload commands output");
			for(Command command:commands){
				boolean ok = CommandService.saveServer(command);
				if(ok){
					CommandService.upload(command);
				}
			}
		}
	}
	private void uploadCounterPeople() {
		if(ClientRegister.getInstance().getCounterPeopleEnabled()){
			List<CounterPeople> list = CounterPeopleService.notUploaded();
			if(list!=null && list.size()!=0){
				logger.debug("Going to update CounterPeople");
				for(CounterPeople counterPeople: list){
					boolean ok = CounterPeopleService.saveServer(counterPeople);
					logger.debug("Upload to Internet? "+ok);
					if(ok){
						CounterPeopleService.upload(counterPeople);
					}
				}
			}
		}
	}
	private void uploadMACs() {
		if(ClientRegister.getInstance().getMACUplodEnable()){
			List<MAC> list = MACService.notUploaded();
			if(list!=null && list.size()!=0){
				logger.debug("Going to update MACs");
				for(MAC mac: list){
					boolean ok = MACService.saveServer(mac);
					logger.debug("Upload to Internet? "+ok);
					if(ok){
						MACService.upload(mac);
					}
				}
			}
		}
	}
	private void uploadCleaningService() {
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
	private void uploadAnswers() {
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
	}
	public void setFuture(ScheduledFuture<?> future) {
		this.future = future;
	}

}
