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

import com.feerbox.client.StartFeerBoxClient;

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

	static
	    {
	        try {
	            configuration = new PropertiesConfiguration("config.properties");
	            configuration.setReloadingStrategy(new FileChangedReloadingStrategy());
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

}
