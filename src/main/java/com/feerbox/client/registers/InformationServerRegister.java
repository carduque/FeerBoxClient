package com.feerbox.client.registers;

import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.feerbox.client.db.ReadAnswer;
import com.feerbox.client.db.ReadCleaningService;
import com.feerbox.client.db.SaveAnswer;
import com.feerbox.client.db.SaveCleaningService;
import com.feerbox.client.model.Alert;
import com.feerbox.client.model.Answer;
import com.feerbox.client.model.CleaningService;
import com.feerbox.client.model.Command;
import com.feerbox.client.model.CounterPeople;
import com.feerbox.client.model.MAC;
import com.feerbox.client.model.Weather;
import com.feerbox.client.services.AlertService;
import com.feerbox.client.services.CleaningServiceService;
import com.feerbox.client.services.CommandService;
import com.feerbox.client.services.CounterPeopleService;
import com.feerbox.client.services.MACService;
import com.feerbox.client.services.SaveAnswerService;
import com.feerbox.client.services.WeatherService;

public class InformationServerRegister extends Thread {
	private static final int INTERVAL_FAST_UPDATE = 10;
	final static Logger logger = Logger.getLogger(InformationServerRegister.class);
	private ScheduledFuture<?> future;
	private boolean normalScheduler = true;
	public void run(){
		try {
			if(InternetAccess.getInstance().getAccess()){
				uploadAnswers();
				uploadCleaningService();
				uploadMACs();
				uploadCounterPeople();
				uploadCommandsOutput();
				uploadWeather();
				uploadAlerts();
			}
			//Nothing to update
			ClientRegister.getInstance().setAnswersUploaded(true); //Indicate register finished
		} catch (Throwable  t) {
			logger.error("Error in InformationServerRegister");
		}
	}
	private void uploadAlerts() {
		if(ClientRegister.getInstance().getAlertsEnabled()){
			List<Alert> list = AlertService.notUploaded();
			if(list!=null && list.size()!=0){
				logger.debug("Going to update alert data "+list.size());
				int i=1;
				for(Alert alert: list){
					boolean ok = AlertService.saveServer(alert);
					if(ok){
						AlertService.uploaded(alert);
					} else{
						logger.info("Not able to upload alert data on server");
					}
					i++;
				}
			}
		}
	}
	private void uploadWeather() {
		if(ClientRegister.getInstance().getWeatherSensor()){
			List<Weather> list = WeatherService.notUploaded();
			if(list!=null && list.size()!=0){
				logger.debug("Going to update weather data "+list.size());
				int i=1;
				for(Weather weather: list){
					boolean ok = WeatherService.saveServer(weather);
					if(ok){
						WeatherService.uploaded(weather);
					} else{
						logger.info("Not able to upload weather data on server");
					}
					i++;
				}
			}
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
				saveCounterPeopleList(list);
			}
		}
	}
	private void uploadBulkyCounterPeople() {
		if(ClientRegister.getInstance().getCounterPeopleEnabled()){
			List<CounterPeople> list = CounterPeopleService.notUploadedBulky();
			if(list!=null && list.size()!=0){
				int size = list.size();
				int total = CounterPeopleService.notUploadedTotal();
				activeFastUpdate(total);
				logger.debug("Going to update CounterPeople "+size+"/"+total);
				String ok = CounterPeopleService.saveServerBulky(list);
				if(ok!=null && "TIMEOUT".equals(ok)){
					logger.error("Error timeout on bulky, back to normal update");
					saveCounterPeopleList(list);
				}
				else if(ok!=null && "NOT_CREATED".equals(ok)){
					logger.error("Error not created on bulky, back to normal update");
					saveCounterPeopleList(list);
				}
				else
				if(ok!=null && !"".equals(ok) && ok.length()>0){
					int length = ok.length();
					ok = ok.substring(0, length-1); //last comma has to be out
					logger.debug("Upload to Internet "+size+"? "+(StringUtils.countMatches(ok, ",")+1));
					CounterPeopleService.uploadList(ok);
				} else{
					logger.debug("Error uploading to server: "+ok+" , back to normal update");
					saveCounterPeopleList(list);
				}
			}
			else{
				activeFastUpdate(0);
			}
		}
	}
	private void saveCounterPeopleList(List<CounterPeople> list) {
		for(CounterPeople counterPeople: list){
			boolean ok2 = CounterPeopleService.saveServer(counterPeople);
			logger.debug("Upload to Internet? "+ok2);
			if(ok2){
				CounterPeopleService.upload(counterPeople);
			}
		}
	}

	private void activeFastUpdate(int total) {
		if (total > CounterPeopleService.MAX_BULKY && normalScheduler) {
			if (future != null) {
				future.cancel(true);
			}
			logger.info("Activating fast update");
			normalScheduler = false;
			future = ClientRegister.getInstance().getScheduler().scheduleAtFixedRate(this, INTERVAL_FAST_UPDATE, INTERVAL_FAST_UPDATE, TimeUnit.SECONDS);
		} else {
			if(!normalScheduler && total < CounterPeopleService.MAX_BULKY){
				if (future != null) {
					future.cancel(true);
				}
				logger.info("Back to normal update time rate");
				normalScheduler = true;
				future = ClientRegister.getInstance().getScheduler().scheduleAtFixedRate(this, 1, 1, TimeUnit.MINUTES);
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
			logger.debug("Going to update answers "+list.size());
			int i=1;
			for(Answer answer: list){
				ClientRegister.getInstance().setAnswersUploaded(false);
				boolean ok = SaveAnswerService.saveAnswerInternet(answer);
				logger.debug("Upload to Internet ("+i+"/"+list.size()+")? "+ok);
				if(ok){
					SaveAnswer.upload(answer);
				}
				i++;
			}
		}
	}
	public void setFuture(ScheduledFuture<?> future) {
		this.future = future;
	}

}
