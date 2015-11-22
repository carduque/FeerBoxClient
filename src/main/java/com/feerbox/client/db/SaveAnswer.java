package com.feerbox.client.db;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.feerbox.client.model.Answer;
import com.feerbox.client.registers.ClientRegister;
import com.feerbox.client.registers.InternetAccess;

public class SaveAnswer{
	private static final String FEERBOX_SERVER_URL = "http://feerbox.herokuapp.com/";
	

	public static Integer saveAnswer(int buttonNumber) {
		int id = 0;
		if(InternetAccess.getInstance().getAccess()){
			System.out.println("Answer internet");
			saveAnswerInternet(buttonNumber);
		}
		else{
			id = FeerboxDB.saveAnswer(buttonNumber);
		}
		if(id==0) return null;
		return id;
	}

	private static boolean saveAnswerInternet(int buttonNumber) {
		boolean ok = true;
		try {
			String customer = ClientRegister.getInstance().getCustomer();
			URL myURL = new URL(FEERBOX_SERVER_URL+"/db/"+customer+"/button/"+buttonNumber);
			URLConnection myURLConnection = myURL.openConnection();
			myURLConnection.setRequestProperty("Content-Length", "1000");
			//myURLConnection.setRequestProperty("Content-Type", "application\\json");
			myURLConnection.getInputStream();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			ok = false;
		} catch (IOException e) {
			e.printStackTrace();
			ok = false;
		}
		return ok;
	}

	public static void tryConnection() throws SaveAnswerError {
		try {
			URL myURL = new URL(FEERBOX_SERVER_URL);
			URLConnection myURLConnection = myURL.openConnection();
			myURLConnection.setRequestProperty("Content-Length", "1000");
			myURLConnection.getInputStream();
		} catch (MalformedURLException e) {
			throw new SaveAnswerError(e);
		} catch (IOException e) {
			throw new SaveAnswerError(e);
		}
		
	}

	public static void saveIP(String iface, String ip) {
		try {
			URL myURL = new URL(FEERBOX_SERVER_URL+"/iface/"+iface+"/ip/"+ip);
			URLConnection myURLConnection = myURL.openConnection();
			myURLConnection.setRequestProperty("Content-Length", "1000");
			myURLConnection.getInputStream();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public static boolean saveOnServer(Answer answer) {
		return saveAnswerInternet(answer.getButton());
	}

	public static void markAsUploaded(Answer answer) {
		answer.setUpload(true);
		FeerboxDB.saveAnswer(answer);
	}

}
