package com.feerbox.client.registers;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.log4j.Logger;

public class ClientRegister {
	private static ClientRegister instance= null;
	private static PropertiesConfiguration configuration = null;
	private String email = null;
	private String reference = null;
	private String environment = null;
	private boolean internet = true;
	private Date lastAnswerSaved;
	private boolean aliveLights = false;
	private boolean saveStatusLocally = false;
	private boolean answersUploaded = true;
	final static Logger logger = Logger.getLogger(ClientRegister.class);
	private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(6);
	private Date lastGetCommands;
	private Date lastGetCleaners;
	private Date lastExecuteCommand;
	private Date lastCPSaved;
	private Date lastWeatherSaved;
	private boolean voiceAnswer;

	static
	    {
	        try {
	            configuration = new PropertiesConfiguration("config.properties");
	            configuration.setReloadingStrategy(new FileChangedReloadingStrategy());
	            addPropertiesAtRunTimeOutsideJava();
	        } catch (ConfigurationException e) {
	        	logger.debug("ConfigurationException", e);
	        }
	    }
	 
	    public static synchronized String getProperty(final String key)
	    {
	    	 String property = (String)configuration.getProperty(key);
	    	 if(property==null){
	    		 logger.error("Property "+key+" not found on configuration file");
	    		 property = addPropertiesAtRunTime(key);
	    	 }
	        return property;
	    }

	private static void addPropertiesAtRunTimeOutsideJava() {
		String property = (String)configuration.getProperty("laser_sensor_threshold");
   	 	if(property==null){
			configuration.setProperty("laser_sensor_threshold", "0.2"); //seconds
			try {
				configuration.save();
			} catch (ConfigurationException e) {
				logger.error("Storing config file: "+e.getMessage());
			}
			logger.info("Property laser_sensor_threshold added with value 0.2");
		}
	}

	private static String addPropertiesAtRunTime(String key) {
		String value = null;
		if(key.equals("show_internet_connection_error")){
			value = "false";
			configuration.setProperty(key, value);
			try {
				configuration.save();
			} catch (ConfigurationException e) {
				logger.error("Storing config file: "+e.getMessage());
			}
			logger.info("Property "+key+" added with value "+value);
		}
		if(key.equals("led_power_on")){
			value = "false";
			configuration.setProperty(key, value);
			try {
				configuration.save();
			} catch (ConfigurationException e) {
				logger.error("Storing config file: "+e.getMessage());
			}
			logger.info("Property "+key+" added with value "+value);
		}
		if(key.equals("mac_upload_enabled")){
			value = "false";
			configuration.setProperty(key, value);
			try {
				configuration.save();
			} catch (ConfigurationException e) {
				logger.error("Storing config file: "+e.getMessage());
			}
			logger.info("Property "+key+" added with value "+value);
		}
		if(key.equals("counter_people_enabled")){
			value = "false";
			configuration.setProperty(key, value);
			try {
				configuration.save();
			} catch (ConfigurationException e) {
				logger.error("Storing config file: "+e.getMessage());
			}
			logger.info("Property "+key+" added with value "+value);
		}
		if(key.equals("led_power_on")){
			value = "false";
			configuration.setProperty(key, value);
			try {
				configuration.save();
			} catch (ConfigurationException e) {
				logger.error("Storing config file: "+e.getMessage());
			}
			logger.info("Property "+key+" added with value "+value);
		}
		if(key.equals("mac_upload_enabled")){
			value = "false";
			configuration.setProperty(key, value);
			try {
				configuration.save();
			} catch (ConfigurationException e) {
				logger.error("Storing config file: "+e.getMessage());
			}
			logger.info("Property "+key+" added with value "+value);
		}
		if(key.equals("counter_people_min_threshold")){
			value = "150"; //cm
			configuration.setProperty(key, value);
			try {
				configuration.save();
			} catch (ConfigurationException e) {
				logger.error("Storing config file: "+e.getMessage());
			}
			logger.info("Property "+key+" added with value "+value);
		}
		if(key.equals("counter_people_max_threshold")){
			value = "150"; //cm
			configuration.setProperty(key, value);
			try {
				configuration.save();
			} catch (ConfigurationException e) {
				logger.error("Storing config file: "+e.getMessage());
			}
			logger.info("Property "+key+" added with value "+value);
		}
		if(key.equals("counter_people_lcd_enabled")){
			value = "false";
			configuration.setProperty(key, value);
			try {
				configuration.save();
			} catch (ConfigurationException e) {
				logger.error("Storing config file: "+e.getMessage());
			}
			logger.info("Property "+key+" added with value "+value);
		}
		if(key.equals("counter_people_debug_mode")){
			value = "false";
			configuration.setProperty(key, value);
			try {
				configuration.save();
			} catch (ConfigurationException e) {
				logger.error("Storing config file: "+e.getMessage());
			}
			logger.info("Property "+key+" added with value "+value);
		}
		if(key.equals("counter_people_laser")){
			value = "false";
			configuration.setProperty(key, value);
			try {
				configuration.save();
			} catch (ConfigurationException e) {
				logger.error("Storing config file: "+e.getMessage());
			}
			logger.info("Property "+key+" added with value "+value);
		}
		if(key.equals("counter_people_distancesensor")){
			value = "false";
			configuration.setProperty(key, value);
			try {
				configuration.save();
			} catch (ConfigurationException e) {
				logger.error("Storing config file: "+e.getMessage());
			}
			logger.info("Property "+key+" added with value "+value);
		}
		if(key.equals("weathersensor")){
			value = "false";
			configuration.setProperty(key, value);
			try {
				configuration.save();
			} catch (ConfigurationException e) {
				logger.error("Storing config file: "+e.getMessage());
			}
			logger.info("Property "+key+" added with value "+value);
		}
		if(key.equals("monitoring_alerts")){
			value = "false";
			configuration.setProperty(key, value);
			try {
				configuration.save();
			} catch (ConfigurationException e) {
				logger.error("Storing config file: "+e.getMessage());
			}
			logger.info("Property "+key+" added with value "+value);
		}
		if(key.equals("monitoring_internet")){
			value = "false";
			configuration.setProperty(key, value);
			try {
				configuration.save();
			} catch (ConfigurationException e) {
				logger.error("Storing config file: "+e.getMessage());
			}
			logger.info("Property "+key+" added with value "+value);
		}
		if(key.equals("usb_3g")){
			value = "false";
			configuration.setProperty(key, value);
			try {
				configuration.save();
			} catch (ConfigurationException e) {
				logger.error("Storing config file: "+e.getMessage());
			}
			logger.info("Property "+key+" added with value "+value);
		}
		if(key.equals("windows")){
			value = "false";
			configuration.setProperty(key, value);
			try {
				configuration.save();
			} catch (ConfigurationException e) {
				logger.error("Storing config file: "+e.getMessage());
			}
			logger.info("Property "+key+" added with value "+value);
		}
		if(key.equals("voice_answer")){
			value = "false";
			configuration.setProperty(key, value);
			try {
				configuration.save();
			} catch (ConfigurationException e) {
				logger.error("Storing config file: "+e.getMessage());
			}
			logger.info("Property "+key+" added with value "+value);
		}
		if(key.equals("presence_detector_enabled")){
			value = "false";
			configuration.setProperty(key, value);
			try {
				configuration.save();
			} catch (ConfigurationException e) {
				logger.error("Storing config file: "+e.getMessage());
			}
			logger.info("Property "+key+" added with value "+value);
		}
		if(key.equals("presence_detector_pause_between_mesurements")){
			value = "3000"; //ms
			configuration.setProperty(key, value);
			try {
				configuration.save();
			} catch (ConfigurationException e) {
				logger.error("Storing config file: "+e.getMessage());
			}
			logger.info("Property "+key+" added with value "+value);
		}
		if(key.equals("button_sound_enabled")){
			value = "false"; //ms
			configuration.setProperty(key, value);
			try {
				configuration.save();
			} catch (ConfigurationException e) {
				logger.error("Storing config file: "+e.getMessage());
			}
			logger.info("Property "+key+" added with value "+value);
		}
		if(key.equals("button_pause_between_mesurements")){
			value = "0"; //ms
			configuration.setProperty(key, value);
			try {
				configuration.save();
			} catch (ConfigurationException e) {
				logger.error("Storing config file: "+e.getMessage());
			}
			logger.info("Property "+key+" added with value "+value);
		}
		return value;
	}

	public ClientRegister() {
		readLiveConfiguration();
	}
	
	private void readLiveConfiguration() {
	    //get the property value and print it out
	    logger.debug("FeerBox configured as :"+getProperty("reference"));
	    /*this.reference = getProperty("reference");
	    this.environment = getProperty("environment");
	    this.email = getProperty("email");
	    this.internet = Boolean.parseBoolean(getProperty("internet"));
	    this.aliveLights = Boolean.parseBoolean(getProperty("alive_lights"));*/
	}

	@Deprecated
	private void readConfiguration() {
		Properties prop = new Properties();
		try {
		    //load a properties file from class path, inside static method
		    prop.load(this.getClass().getClassLoader().getResourceAsStream("config.properties"));

		    //get the property value and print it out
		    logger.debug(prop.getProperty("reference"));
		    this.reference = prop.getProperty("reference");
		    this.environment = prop.getProperty("environment");
		    this.email = prop.getProperty("email");
		    this.internet = Boolean.parseBoolean(prop.getProperty("internet"));
		    this.setAliveLights(Boolean.parseBoolean(prop.getProperty("alive_lights")));

		} 
		catch (IOException e) {
			logger.debug("IOException", e);
		}
	}

	public static ClientRegister getInstance() {
		if(instance==null){
			instance = new ClientRegister();
		}
		return instance;
	}

	

	public boolean getInternet() {
		this.internet = Boolean.parseBoolean(getProperty("internet"));
		return this.internet;
	}

	public String getEmail() {
		this.email = getProperty("email");
		return email;
	}

	public String getReference() {
		 this.reference = getProperty("reference");
		return reference;
	}

	public String getEnvironment() {
		this.environment = getProperty("environment");
		return environment;
	}

	public void setLastAnswerSaved(Date date) {
		this.lastAnswerSaved = date;
	}

	public Date getLastAnswerSaved() {
		return lastAnswerSaved;
	}

	public boolean isAliveLights() {
		this.aliveLights = Boolean.parseBoolean(getProperty("alive_lights"));
		return aliveLights;
	}
	
	public boolean getAliveLights() {
		this.aliveLights = Boolean.parseBoolean(getProperty("alive_lights"));
		return aliveLights;
	}
	
	public boolean isSaveStatusLocally() {
		this.saveStatusLocally = Boolean.parseBoolean(getProperty("save_status_locally"));
		return saveStatusLocally;
	}
	
	public boolean getSaveStatusLocally() {
		this.saveStatusLocally = Boolean.parseBoolean(getProperty("save_status_locally"));
		return saveStatusLocally;
	}

	public void setAliveLights(boolean aliveLights) {
		this.aliveLights = aliveLights;
	}
	
	public boolean getWifiDetection(){
		return Boolean.parseBoolean(getProperty("wifi_detection"));
	}
	
	public String getWifiInterface(){
		return getProperty("wifi_interface");
	}

	public void setAnswersUploaded(boolean upload) {
		this.answersUploaded = upload;
	}
	
	public boolean getAnswersUploaded() {
		return this.answersUploaded;
	}
	
	public int getSaveStatusInterval(){
		int interval = 60;
		try {
			interval = Integer.parseInt(getProperty("send_status_interval"));
		} catch (NumberFormatException e) {
			logger.error("NumberFormatException", e);
		}
		return interval;
	}
	
	public int getCommandExecutorInterval(){
		int interval = 1440;
		try {
			interval = Integer.parseInt(getProperty("command_executor_interval"));
		} catch (NumberFormatException e) {
			logger.error("NumberFormatException", e);
		}
		return interval;
	}
	
	public int getCommandQueueRegisterInterval(){
		int interval = 720;
		try {
			interval = Integer.parseInt(getProperty("commandqueue_register_interval"));
		} catch (NumberFormatException e) {
			logger.error("NumberFormatException", e);
		}
		return interval;
	}

	public boolean getNFCReaderEnabled() {
		return Boolean.parseBoolean(getProperty("nfc_reader_enabled"));
	}
	
	public boolean getTetheringLightsEnabled() {
		return Boolean.parseBoolean(getProperty("tethering_lights"));
	}

	public ScheduledExecutorService getScheduler() {
		return scheduler;
	}

	public boolean getVoiceAnswer() {
		return Boolean.parseBoolean(getProperty("voice_answer"));
	}

	public void setVoiceAnswer(boolean voiceAnswer) {
		this.voiceAnswer = voiceAnswer;
	}

	public void setScheduler(ScheduledExecutorService scheduler) {
		this.scheduler = scheduler;
	}
	
	public boolean getCommandExecutorEnabled() {
		return Boolean.parseBoolean(getProperty("enable_command_executor"));
	}
	
	public boolean getLCDActive() {
		return Boolean.parseBoolean(getProperty("lcd_active"));
	}

	public boolean getCleaningServiceEnable() {
		return Boolean.parseBoolean(getProperty("cleaning_service_enable"));
	}

	public void setLastGetCommands(Date date) {
		this.lastGetCommands= date;
	}
	
	public Date getLastGetCommands() {
		return lastGetCommands;
	}

	public void setLastGetCleaners(Date date) {
		this.lastGetCleaners = date;
	}

	public boolean getCleanerExecutorEnabled() {
		return Boolean.parseBoolean(getProperty("enable_cleaner_register"));
	}

	public long getCleanerRegisterInterval() {
		int interval = 1440;
		try {
			interval = Integer.parseInt(getProperty("cleaner_register_interval"));
		} catch (NumberFormatException e) {
			logger.error("NumberFormatException", e);
		}
		return interval;
	}

	public boolean getShowUnknownNFCs() {
		return Boolean.parseBoolean(getProperty("show_unkown_nfcs"));
	}

	public boolean getShowInternetConnectionError() {
		boolean out = false;
		try {
			out = Boolean.parseBoolean(getProperty("show_internet_connection_error"));
		} catch (Exception e) {
			logger.error("ShowInternetConnectionError: "+e.getMessage());
		}
		return out;
	}

	public Date getLastExecuteCommand() {
		return this.lastExecuteCommand;
	}
	
	public void setLastExecuteCommand(Date lastExecuteCommand) {
		this.lastExecuteCommand = lastExecuteCommand;
	}

	public boolean getLedPowerOn() {
		boolean out = false;
		try {
			out = Boolean.parseBoolean(getProperty("led_power_on"));
		} catch (Exception e) {
			logger.error("LedPowerOn: "+e.getMessage());
		}
		return out;
	}

	public boolean getMACUplodEnable() {
		boolean out = false;
		try {
			out = Boolean.parseBoolean(getProperty("mac_upload_enabled"));
		} catch (Exception e) {
			logger.error("ShowInternetConnectionError: "+e.getMessage());
		}
		return out;
	}

	public boolean getCounterPeopleEnabled() {
		boolean out = false;
		try {
			out = Boolean.parseBoolean(getProperty("counter_people_enabled"));
		} catch (Exception e) {
			logger.error("ShowInternetConnectionError: "+e.getMessage());
		}
		return out;
	}

	public double getCounterPeopleMinThreshold() {
		int threshold = 0;
		try {
			threshold = Integer.parseInt(getProperty("counter_people_min_threshold"));
		} catch (NumberFormatException e) {
			logger.error("NumberFormatException", e);
		}
		return threshold;
	}

	public double getCounterPeopleMaxThreshold() {
		int threshold = 0;
		try {
			threshold = Integer.parseInt(getProperty("counter_people_max_threshold"));
		} catch (NumberFormatException e) {
			logger.error("NumberFormatException", e);
		}
		return threshold;
	}

	public long getCounterPeoplePauseBetweenMesurements() {
		int millis = 0;
		try {
			millis = Integer.parseInt(getProperty("counter_people_pause_between_mesurements"));
		} catch (NumberFormatException e) {
			logger.error("NumberFormatException", e);
		}
		return millis;
	}

	public boolean getCounterPeopleLCD() {
		boolean out = false;
		try {
			out = Boolean.parseBoolean(getProperty("counter_people_lcd_enabled"));
		} catch (Exception e) {
			logger.error("ShowInternetConnectionError: "+e.getMessage());
		}
		return out;
	}

	public boolean getCounterPeopleDebugMode() {
		boolean out = false;
		try {
			out = Boolean.parseBoolean(getProperty("counter_people_debug_mode"));
		} catch (Exception e) {
			logger.error("ShowInternetConnectionError: "+e.getMessage());
		}
		return out;
	}

	public boolean getCounterPeopleLaser() {
		boolean out = false;
		try {
			out = Boolean.parseBoolean(getProperty("counter_people_laser"));
		} catch (Exception e) {
			logger.error("ShowInternetConnectionError: "+e.getMessage());
		}
		return out;
	}

	public boolean getCounterPeopleDistanceSensor() {
		boolean out = false;
		try {
			out = Boolean.parseBoolean(getProperty("counter_people_distancesensor"));
		} catch (Exception e) {
			logger.error("ShowInternetConnectionError: "+e.getMessage());
		}
		return out;
	}

	public boolean getWeatherSensor() {
		boolean out = false;
		try {
			out = Boolean.parseBoolean(getProperty("weathersensor"));
		} catch (Exception e) {
			logger.error("Error: "+e.getMessage());
		}
		return out;
	}

	public boolean getAlertsEnabled() {
		boolean out = false;
		try {
			out = Boolean.parseBoolean(getProperty("monitoring_alerts"));
		} catch (Exception e) {
			logger.error("Error: "+e.getMessage());
		}
		return out;
	}

	public boolean getMonitorInternetConnection() {
		boolean out = false;
		try {
			out = Boolean.parseBoolean(getProperty("monitoring_internet"));
		} catch (Exception e) {
			logger.error("Error: "+e.getMessage());
		}
		return out;
	}

	public boolean getUSB3G() {
		boolean out = false;
		try {
			out = Boolean.parseBoolean(getProperty("usb_3g"));
		} catch (Exception e) {
			logger.error("Error: "+e.getMessage());
		}
		return out;
	}

	public void setLastCPSaved(Date time) {
		this.lastCPSaved = time;
	}
	
	public Date getLastCPSaved() {
		return lastCPSaved;
	}

	public Date getLastWeatherSaved() {
		return lastWeatherSaved;
	}

	public void setLastWeatherSaved(Date lastWeatherSaved) {
		this.lastWeatherSaved = lastWeatherSaved;
	}
	
	public boolean getWindows() {
		boolean out = false;
		try {
			out = Boolean.parseBoolean(getProperty("windows"));
		} catch (Exception e) {
			logger.error("windows: "+e.getMessage());
		}
		return out;
	}

	public boolean getTempSensor() {
		boolean out = false;
		try {
			out = Boolean.parseBoolean(getProperty("tempsensor"));
		} catch (Exception e) {
			logger.error("tempsensor: "+e.getMessage());
		}
		return out;
	}

	public boolean getPresenceDetectorEnabled() {
		boolean out = false;
		try {
			out = Boolean.parseBoolean(getProperty("presence_detector_enabled"));
		} catch (Exception e) {
			logger.error("presence_detector_enabled: "+e.getMessage());
		}
		return out;
	}

	public long getPresenceDetectorPauseBetweenMesurements() {
		int millis = 0;
		try {
			millis = Integer.parseInt(getProperty("presence_detector_pause_between_mesurements"));
		} catch (NumberFormatException e) {
			logger.error("NumberFormatException", e);
		}
		return millis;
	}

	public boolean getButtonSoundEnabled() {
		boolean out = false;
		try {
			out = Boolean.parseBoolean(getProperty("button_sound_enabled"));
		} catch (Exception e) {
			logger.error("presence_detector_enabled: "+e.getMessage());
		}
		return out;
	}

	public long getButtonPauseBetweenMesurements() {
		int millis = 0;
		try {
			millis = Integer.parseInt(getProperty("button_pause_between_mesurements"));
		} catch (NumberFormatException e) {
			logger.error("NumberFormatException", e);
		}
		return millis;
	}

}
