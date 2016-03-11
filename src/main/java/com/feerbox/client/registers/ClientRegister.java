package com.feerbox.client.registers;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

public class ClientRegister {
	private static ClientRegister instance= null;
	private static PropertiesConfiguration configuration = null;
	private String email = null;
	private String reference = null;
	private String environment = null;
	private boolean internet = true;
	private Date lastAnswerSaved;
	private boolean aliveLights = false;
	
	 static
	    {
	        try {
	            configuration = new PropertiesConfiguration("config.properties");
	            configuration.setReloadingStrategy(new FileChangedReloadingStrategy());
	        } catch (ConfigurationException e) {
	            e.printStackTrace();
	        }
	    }
	 
	    public static synchronized String getProperty(final String key)
	    {
	        return (String)configuration.getProperty(key);
	    }

	public ClientRegister() {
		readLiveConfiguration();
	}
	
	private void readLiveConfiguration() {
	    //get the property value and print it out
	    System.out.println("FeerBox configured as :"+getProperty("reference"));
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
		    System.out.println(prop.getProperty("reference"));
		    this.reference = prop.getProperty("reference");
		    this.environment = prop.getProperty("environment");
		    this.email = prop.getProperty("email");
		    this.internet = Boolean.parseBoolean(prop.getProperty("internet"));
		    this.setAliveLights(Boolean.parseBoolean(prop.getProperty("alive_lights")));

		} 
		catch (IOException ex) {
		    ex.printStackTrace();
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

	public void setAliveLights(boolean aliveLights) {
		this.aliveLights = aliveLights;
	}
	
	public boolean getWifiDetection(){
		return Boolean.parseBoolean(getProperty("wifi_detection"));
	}

}
