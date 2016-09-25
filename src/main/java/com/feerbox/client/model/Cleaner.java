package com.feerbox.client.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Cleaner {
	private int id;
	private String name;
	private String surname;
	private String reference;
	private Date serverLastUpdate;
	private int serverId;
	private int Company;
	private Date serverCreationDate;
	
	
	public Cleaner() {
	}
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
	public Date getServerLastUpdate() {
		return serverLastUpdate;
	}
	public void setServerLastUpdate(Date lastupdate) {
		this.serverLastUpdate = lastupdate;
	}
	public int getServerId() {
		return serverId;
	}
	public void setServerId(int serverId) {
		this.serverId = serverId;
	}
	public int getCompany() {
		return Company;
	}
	public void setCompany(int company) {
		Company = company;
	}
	public Date getServerCreationDate() {
		return serverCreationDate;
	}
	public void setServerCreationDate(Date serverCreationDate) {
		this.serverCreationDate = serverCreationDate;
	}
	public String getServerCreationDateFormatted() {
		//dd-MMM-yyyy HH:mm:ss.SSS
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		return df.format(this.serverCreationDate);
	}
	public String getServerLastUpdateDateFormatted() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		return df.format(this.serverLastUpdate);
	}
	
	@Override
	public String toString() {
		return "Cleaner [id=" + id + ", name=" + name + ", surname=" + surname + ", reference=" + reference
				+ ", serverLastUpdate=" + serverLastUpdate + ", serverId=" + serverId + ", Company=" + Company
				+ ", serverCreationDate=" + serverCreationDate + "]";
	}
	
	
	
}
