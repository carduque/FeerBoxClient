package com.feerbox.client.registers;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

public class ClientRegister {
	private static ClientRegister instance= null;
	private String email = null;
	private String reference = null;
	private String environment = null;
	private boolean internet = true;
	private Date lastAnswerSaved;
	private boolean aliveLights = false;
	
	

	public ClientRegister() {
		readConfiguration();
	}

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
		return this.internet;
	}

	public String getEmail() {
		return email;
	}

	public String getReference() {
		return reference;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setLastAnswerSaved(Date date) {
		this.lastAnswerSaved = date;
	}

	public Date getLastAnswerSaved() {
		return lastAnswerSaved;
	}

	public boolean isAliveLights() {
		return aliveLights;
	}
	
	public boolean getAliveLights() {
		return aliveLights;
	}

	public void setAliveLights(boolean aliveLights) {
		this.aliveLights = aliveLights;
	}

}
