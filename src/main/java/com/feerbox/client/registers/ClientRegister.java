package com.feerbox.client.registers;

import java.io.IOException;
import java.util.Properties;

public class ClientRegister {
	private static ClientRegister instance= null;
	private String customer = null;
	private boolean internet;
	
	

	public ClientRegister() {
		readConfiguration();
	}

	private void readConfiguration() {
		Properties prop = new Properties();
		try {
		    //load a properties file from class path, inside static method
		    prop.load(this.getClass().getClassLoader().getResourceAsStream("config.properties"));

		    //get the property value and print it out
		    System.out.println(prop.getProperty("customer"));
		    this.customer = prop.getProperty("customer");
		    this.internet = Boolean.parseBoolean(prop.getProperty("internet"));

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

	public String getCustomer() {
		return this.customer;
	}

	public boolean getInternet() {
		return this.internet;
	}

}
