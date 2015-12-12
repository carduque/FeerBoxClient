package com.feerbox.client.services;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import com.feerbox.client.db.FeerboxDB;
import com.feerbox.client.db.SaveAnswer;
import com.feerbox.client.db.SaveAnswerError;
import com.feerbox.client.model.Answer;
import com.feerbox.client.registers.ClientRegister;
import com.feerbox.client.registers.InternetAccess;

public class SaveAnswerService extends FeerboxDB{
	private static final String FEERBOX_SERVER_URL = "http://feerbox.herokuapp.com/";
	

	public static Integer saveAnswer(int buttonNumber) {
		int id = 0;
		Answer answer = new Answer();
		answer.setButton(buttonNumber);
		answer.setTime(new Date());
		answer.setCustomer(ClientRegister.getInstance().getCustomer());
		
		if(InternetAccess.getInstance().getAccess()){
			System.out.println("Answer internet: "+buttonNumber);
			boolean ok = saveAnswerInternet(answer);
			if(!ok){
				id = SaveAnswer.save(answer);
			}
		}
		else{
			id = SaveAnswer.save(answer);
		}
		if(id==0) return null;
		return id;
	}

	public static boolean saveAnswerInternet(Answer answer) {
		boolean ok = true;
		try {
			URL myURL = new URL(FEERBOX_SERVER_URL+"answers/add");
			HttpURLConnection conn = (HttpURLConnection) myURL.openConnection();
			conn.setRequestProperty("Content-Length", "1000");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			String json = "{\"button\":\""+answer.getButton()+"\",\"customer\":\""+answer.getCustomer()+"\", \"time\":\""+answer.getTimeText()+"\"}";

			OutputStream os = conn.getOutputStream();
			os.write(json.getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
				System.out.println("Failed : HTTP error code : "+ conn.getResponseCode());
				ok = false;
				//SaveAnswer.save(answer);
			}

			//BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			/*
			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}
			*/
			conn.disconnect();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
			ok = false;
		} catch (IOException e) {
			e.printStackTrace();
			ok = false;
		}
		return ok;
	}
	
	private static boolean saveAnswerInternetOld(int buttonNumber) {
		boolean ok = true;
		try {
			String customer = ClientRegister.getInstance().getCustomer();
			URL myURL = new URL(FEERBOX_SERVER_URL+"db/"+customer+"/button/"+buttonNumber);
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
			//System.out.println(FEERBOX_SERVER_URL+"iface/"+iface+"/ip/"+ip);
			URL myURL = new URL(FEERBOX_SERVER_URL+"iface/"+iface+"/ip/"+ip);
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

}
