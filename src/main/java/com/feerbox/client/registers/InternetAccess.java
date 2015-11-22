package com.feerbox.client.registers;

public class InternetAccess {
	private static InternetAccess instance;
	private boolean access;
	
	public static InternetAccess getInstance(){
		if(instance==null){
			instance = new InternetAccess();
		}
		return instance;
	}

	public void setAccess(boolean access) {
		this.access = access;
	}

	public boolean getAccess() {
		return this.access;
	}

}
