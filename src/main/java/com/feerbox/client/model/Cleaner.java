package com.feerbox.client.model;

import java.sql.Timestamp;

public class Cleaner {
	private int id;
	private String name;
	private String surname;
	private String reference;
	private Timestamp lastupdate;
	
	public Cleaner(String cleanerReference) {
		this.reference = cleanerReference;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
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
