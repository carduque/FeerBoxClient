package com.feerbox.client.model;

import java.sql.Timestamp;

public class Cleaner {
	private int id;
	private int name;
	private String surname;
	private String reference;
	private Timestamp lastupdate;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getName() {
		return name;
	}
	public void setName(int name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public Timestamp getLastupdate() {
		return lastupdate;
	}
	public void setLastupdate(Timestamp lastupdate) {
		this.lastupdate = lastupdate;
	}
	
	
}
