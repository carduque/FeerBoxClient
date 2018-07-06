package com.feerbox.client.alerts;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.feerbox.client.db.ReadAlertConfiguration;
import com.feerbox.client.db.SaveAlert;
import com.feerbox.client.db.SaveAlertConfiguration;
import com.feerbox.client.model.Alert;
import com.feerbox.client.model.AlertConfiguration;
import com.feerbox.client.model.AlertConfiguration.TypeAlertConfiguration;
import com.feerbox.client.model.AlertThreshold;
import com.feerbox.client.model.AlertTimeTable;
import com.feerbox.client.registers.DataAlertsRegister;

public class AlertTest {
	@Test
	public void saveAlert() {
		Alert alert = createAlert();
		int id=SaveAlert.save(alert);
		assertNotEquals(id, 0);
	}

	private Alert createAlert() {
		Alert alert = new Alert();
		alert.setName("name");
		alert.setGenerator(Alert.AlertGenerator.LOG);
		alert.setReference("2015001");
		alert.setSeverity(Alert.AlertSeverity.MEDIUM);
		alert.setThreshold(1000L);
		alert.setTime(new Date());
		alert.setType(Alert.AlertType.NotEnoughDataBeenCollected);
		//alert.setWeekday(1);
		return alert;
	}

	@Test
	public void readAlertConfiguration() {
		AlertConfiguration alertConfiguration = createAlertConfiguration();
		long id = SaveAlertConfiguration.save(alertConfiguration);
		assertNotEquals(id, 0);
		//assertNotEquals(id1, id2);
		Map<String, AlertConfiguration> configurations = ReadAlertConfiguration.readAll();
		assertTrue(configurations.keySet().size()>0);
		assertTrue(configurations.get(TypeAlertConfiguration.ANSWERS.name())!=null);
	}

	private AlertConfiguration createAlertConfiguration() {
		AlertConfiguration alertConfiguration = new AlertConfiguration();
		alertConfiguration.setName("AlertConfiguration");
		alertConfiguration.setActive(true);
		List<AlertThreshold> alertThresholds = new ArrayList<AlertThreshold>();
		AlertThreshold alertThreshold = new AlertThreshold();
		alertThreshold.setThreshold(1000L);
		alertThreshold.setWeekDay(4);
		alertThreshold.setType(AlertThreshold.TypeAlertThreshold.UPPER);
		alertThresholds.add(alertThreshold);
		alertConfiguration.setAlertThresholds(alertThresholds);
		List<AlertTimeTable> alertTimeTables = new ArrayList<AlertTimeTable>();
		AlertTimeTable alertTimeTable = new AlertTimeTable();
		alertTimeTable.setWeekDay(4);
		alertTimeTable.setThreshold(10L);
		alertTimeTable.setStartingTime(LocalTime.of(0, 0, 0));
		alertTimeTable.setClosingTime(LocalTime.of(8, 0, 0));
		alertTimeTables.add(alertTimeTable);
		alertConfiguration.setAlertTimeTables(alertTimeTables);
		alertConfiguration.setType(TypeAlertConfiguration.ANSWERS);
		return alertConfiguration;
	}
	
	@Test
	public void DataAlertsRegister() {
		DataAlertsRegister register = new DataAlertsRegister();
		register.run();
	}
}
