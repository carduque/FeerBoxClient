package com.feerbox.client.registers;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.feerbox.client.db.ReadAlertConfiguration;
import com.feerbox.client.db.ReadAnswer;
import com.feerbox.client.db.ReadCounterPeople;
import com.feerbox.client.db.SaveAlert;
import com.feerbox.client.model.Alert;
import com.feerbox.client.model.AlertConfiguration;
import com.feerbox.client.model.AlertOS;
import com.feerbox.client.model.AlertThreshold;
import com.feerbox.client.model.AlertTimeTable;

public class DataAlertsRegister extends TimerTask {
	final static Logger logger = Logger.getLogger(DataAlertsRegister.class);
	private Timer timer;
	Map<String, AlertConfiguration> alerts = null;
	
	
	@Override
	public void run() {
		try {
			loadAlerts();
			//ANSWERS
			long total_answers = ReadAnswer.readAnswersDayBefore();
			NotEnoughAnswersBeenCollected(total_answers);
			TooMuchAnswersBeenCollected(total_answers);
			AnswersOutTime();
			
			//COUNTERPEOPLE
			long total_counterpeople = ReadCounterPeople.readCounterPeopleDayBefore();
			NotEnoughCounterPeopleBeenCollected(total_counterpeople);
			TooMuchCounterPeopleBeenCollected(total_counterpeople);
			CounterPeopleOutOfTime();
			
			//RASPBIAN
			NotEnoughUpTime();
			RunningOutOfDisk();
			
			//CLIENT
			ChangeOfFeerBoxReference();
			
		} catch (Throwable  t) {
			logger.error("Error in DataAlertsRegister");
		}
	}


	private void ChangeOfFeerBoxReference() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -1);
		/*AbstractMap<String, Long> answers = ReadAnswer.GroupByReferenceByDay(calendar);
		AbstractMap<String, Long> counterpeople = ReadCounterPeople.GroupByReferenceByDay(calendar);
		AbstractMap<String, Long> macs = ReadMAC.GroupByReferenceByDay(calendar);
		
		if(answers.size()>1 || counterpeople.size()>1 || macs.size()>1){
			Alert alert = createMoreThanOneReferenceAlert();
			SaveAlert.save(alert);
		}*/
	}


	private Alert createMoreThanOneReferenceAlert() {
		// TODO Auto-generated method stub
		return null;
	}


	private void RunningOutOfDisk() {
		double occupancy = getDiskOccupancy();
		AlertConfiguration config = alerts.get(AlertConfiguration.TypeAlertConfiguration.RASPBIAN.name());
		if(config!=null){
			AlertOS alertOS = config.getAlertOS().get(AlertOS.AlertOSType.MAXIMUMDISKOCCUPANCY);
			if(alertOS.generateAlert(occupancy)){
				Alert alert = createMaximumDiskOccupancyAlert(occupancy);
				SaveAlert.save(alert);
			}
		}
	}


	private Alert createMaximumDiskOccupancyAlert(double occupancy) {
		Alert alert = new Alert();
		alert.setSeverity(Alert.AlertSeverity.HIGH);
		alert.setGenerator(Alert.AlertGenerator.RASPBIAN);
		alert.setThreshold((long)(occupancy*100));
		alert.setName("Disk Occupancy at midnight was more than threshold");
		alert.setReference(ClientRegister.getInstance().getReference());
		alert.setTime(new Date());
		alert.setType(Alert.AlertType.PoorUpTime);
		return alert;
	}


	private double getDiskOccupancy() {
		double occupancy = 0.0;
		StatusRegister register = new StatusRegister();
		String txt = register.executeCommandLine("df -h | grep \"/dev/sda1\" | awk '{print $5}'");
		occupancy = Double.parseDouble(txt);
		return occupancy;
	}


	private void NotEnoughUpTime() {
		double uptime = getUptime();
		AlertConfiguration config = alerts.get(AlertConfiguration.TypeAlertConfiguration.RASPBIAN.name());
		if(config!=null){
			AlertOS alertOS = config.getAlertOS().get(AlertOS.AlertOSType.NOTENGOUGHUPTIME);
			if(alertOS.generateAlert(uptime)){
				Alert alert = createPoorUptimeAlert(uptime);
				SaveAlert.save(alert);
			}
		}
	}


	private Alert createPoorUptimeAlert(double uptime) {
		Alert alert = new Alert();
		alert.setSeverity(Alert.AlertSeverity.HIGH);
		alert.setGenerator(Alert.AlertGenerator.RASPBIAN);
		alert.setThreshold((long)(uptime*100));
		alert.setName("Uptime at midnight was below threshold");
		alert.setReference(ClientRegister.getInstance().getReference());
		alert.setTime(new Date());
		alert.setType(Alert.AlertType.PoorUpTime);
		return alert;
	}


	private double getUptime() {
		double uptime = 0.0;
		StatusRegister register = new StatusRegister();
		String line =  register.executeCommandLine("command -v tuptime");
		if(line!=null && !"".equals(line)){
			String txt = register.executeCommandLine("sudo tuptime | grep \"System uptime:\" | awk '{print $3}'");
			uptime = Double.parseDouble(txt);
		}
		else{
			Alert alert = createTuptimeNotInstalledAlert();
			SaveAlert.save(alert);
		}
		return uptime;
	}


	private Alert createTuptimeNotInstalledAlert() {
		Alert alert = new Alert();
		alert.setSeverity(Alert.AlertSeverity.LOW);
		alert.setGenerator(Alert.AlertGenerator.RASPBIAN);
		alert.setName("Tuptime");
		alert.setReference(ClientRegister.getInstance().getReference());
		alert.setTime(new Date());
		alert.setType(Alert.AlertType.TuptimeNotInstalled);
		return alert;
	}


	private void CounterPeopleOutOfTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -1);
		int day_of_week = calendar.get(Calendar.DAY_OF_WEEK);
		AlertConfiguration config = alerts.get(AlertConfiguration.TypeAlertConfiguration.COUNTERPEOPLE.name());
		if(config!=null){
			for(AlertTimeTable timeTable: config.getAlertTimeTables()){
				if(timeTable.getWeekDay()==day_of_week){
					long total_answers = ReadCounterPeople.readCounterPeopleDayBefore(timeTable.getStartingTime(), timeTable.getClosingTime());
					if(total_answers>timeTable.getThreshold()){
						Alert alert = createAnswerOutOfTimeAlert(timeTable.getThreshold(), day_of_week,timeTable.getStartingTime(), timeTable.getClosingTime());
						SaveAlert.save(alert);
					}
				}
			}
		}
	}


	private void TooMuchCounterPeopleBeenCollected(long total_counterpeople) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -1);
		int day_of_week = calendar.get(Calendar.DAY_OF_WEEK);
		AlertConfiguration config = alerts.get(AlertConfiguration.TypeAlertConfiguration.COUNTERPEOPLE.name());
		if(config!=null){
			List<AlertThreshold> thresholds = config.getAlertThresholds();
			long yesterday_threshold = 0;
			for(AlertThreshold threshold : thresholds){
				if(threshold.getWeekDay()==day_of_week){
					yesterday_threshold = threshold.getThreshold();
				}
			}
			if(total_counterpeople>yesterday_threshold){
				Alert alert = createTooMuchCounterPeopleAlert(yesterday_threshold, day_of_week);
				SaveAlert.save(alert);
			}
		}
	}


	private Alert createTooMuchCounterPeopleAlert(long threshold, int day_of_week) {
		Alert alert = new Alert();
		alert.setSeverity(Alert.AlertSeverity.HIGH);
		alert.setGenerator(Alert.AlertGenerator.DB);
		alert.setThreshold(threshold);
		alert.setName("CounterPeople on day before was upper threshold");
		alert.setReference(ClientRegister.getInstance().getReference());
		alert.setTime(new Date());
		alert.setType(Alert.AlertType.TooMuchDataBeenCollected);
		alert.setWeekday(day_of_week);
		return alert;
	}


	private void NotEnoughCounterPeopleBeenCollected(long total_counterpeople) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -1);
		int day_of_week = calendar.get(Calendar.DAY_OF_WEEK);
		AlertConfiguration config = alerts.get(AlertConfiguration.TypeAlertConfiguration.COUNTERPEOPLE.name());
		if(config!=null){
			List<AlertThreshold> thresholds = config.getAlertThresholds();
			long yesterday_threshold = 0;
			for(AlertThreshold threshold : thresholds){
				if(threshold.getWeekDay()==day_of_week){
					yesterday_threshold = threshold.getThreshold();
				}
			}
			if(total_counterpeople<yesterday_threshold){
				Alert alert = createNotEnoughCounterPeopleAlert(yesterday_threshold, day_of_week);
				SaveAlert.save(alert);
			}
		}
	}


	private Alert createNotEnoughCounterPeopleAlert(long threshold, int day_of_week) {
		Alert alert = new Alert();
		alert.setSeverity(Alert.AlertSeverity.HIGH);
		alert.setGenerator(Alert.AlertGenerator.DB);
		alert.setThreshold(threshold);
		alert.setName("CounterPeople on day before was below threshold");
		alert.setReference(ClientRegister.getInstance().getReference());
		alert.setTime(new Date());
		alert.setType(Alert.AlertType.NotEnoughDataBeenCollected);
		alert.setWeekday(day_of_week);
		return alert;
	}


	private void AnswersOutTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -1);
		int day_of_week = calendar.get(Calendar.DAY_OF_WEEK);
		AlertConfiguration config = alerts.get(AlertConfiguration.TypeAlertConfiguration.ANSWERS.name());
		if(config!=null){
			for(AlertTimeTable timeTable: config.getAlertTimeTables()){
				if(timeTable.getWeekDay()==day_of_week){
					long total_answers = ReadAnswer.readAnswersDayBefore(timeTable.getStartingTime(), timeTable.getClosingTime());
					if(total_answers>timeTable.getThreshold()){
						Alert alert = createAnswerOutOfTimeAlert(timeTable.getThreshold(), day_of_week,timeTable.getStartingTime(), timeTable.getClosingTime());
						SaveAlert.save(alert);
					}
				}
			}
		}
	}


	private Alert createAnswerOutOfTimeAlert(long threshold, int day_of_week, LocalTime startingTime, LocalTime closingTime) {
		Alert alert = new Alert();
		alert.setSeverity(Alert.AlertSeverity.HIGH);
		alert.setGenerator(Alert.AlertGenerator.DB);
		alert.setThreshold(threshold);
		alert.setName("Answers on day before had data out of time");
		alert.setReference(ClientRegister.getInstance().getReference());
		alert.setTime(new Date());
		alert.setType(Alert.AlertType.OutOfTimeDataCollected);
		alert.setWeekday(day_of_week);
		return alert;
	}


	private void TooMuchAnswersBeenCollected(long total_answers) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -1);
		int day_of_week = calendar.get(Calendar.DAY_OF_WEEK);
		AlertConfiguration config = alerts.get(AlertConfiguration.TypeAlertConfiguration.ANSWERS.name());
		if(config!=null){
			List<AlertThreshold> thresholds = config.getAlertThresholds();
			long yesterday_threshold = 0;
			for(AlertThreshold threshold : thresholds){
				if(threshold.getWeekDay()==day_of_week){
					yesterday_threshold = threshold.getThreshold();
				}
			}
			if(total_answers>yesterday_threshold){
				Alert alert = createTooMuchAnswersAlert(yesterday_threshold, day_of_week);
				SaveAlert.save(alert);
			}
		}
	}


	private Alert createTooMuchAnswersAlert(long threshold, int day_of_week) {
		Alert alert = new Alert();
		alert.setSeverity(Alert.AlertSeverity.HIGH);
		alert.setGenerator(Alert.AlertGenerator.DB);
		alert.setThreshold(threshold);
		alert.setName("Answers on day before was upper threshold");
		alert.setReference(ClientRegister.getInstance().getReference());
		alert.setTime(new Date());
		alert.setType(Alert.AlertType.TooMuchDataBeenCollected);
		alert.setWeekday(day_of_week);
		return alert;
	}


	private void NotEnoughAnswersBeenCollected(long total_answers) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -1);
		int day_of_week = calendar.get(Calendar.DAY_OF_WEEK);
		AlertConfiguration config = alerts.get(AlertConfiguration.TypeAlertConfiguration.ANSWERS.name());
		if(config!=null){
			List<AlertThreshold> thresholds = config.getAlertThresholds();
			long yesterday_threshold = 0;
			for(AlertThreshold threshold : thresholds){
				if(threshold.getWeekDay()==day_of_week){
					yesterday_threshold = threshold.getThreshold();
				}
			}
			if(total_answers<yesterday_threshold){
				Alert alert = createNotEnoughAnswersAlert(yesterday_threshold, day_of_week);
				SaveAlert.save(alert);
			}
		}
	}


	private void loadAlerts() {
		alerts = ReadAlertConfiguration.readAll();
	}


	private Alert createNotEnoughAnswersAlert(long threshold, int day_of_week) {
		Alert alert = new Alert();
		alert.setSeverity(Alert.AlertSeverity.HIGH);
		alert.setGenerator(Alert.AlertGenerator.DB);
		alert.setThreshold(threshold);
		alert.setName("Answers on day before was below threshold");
		alert.setReference(ClientRegister.getInstance().getReference());
		alert.setTime(new Date());
		alert.setType(Alert.AlertType.NotEnoughDataBeenCollected);
		alert.setWeekday(day_of_week);
		return alert;
	}


	public Timer getTimer() {
		return timer;
	}
	public void setTimer(Timer timer) {
		this.timer = timer;
	}

}
