package com.feerbox.client.registers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.feerbox.client.StartFeerBoxClient;
import com.feerbox.client.services.LedService;

public class AliveRegister implements Runnable {
	final static Logger logger = Logger.getLogger(AliveRegister.class);
	private static boolean firstTimeTethering = false;

	public void run() {
		boolean before = InternetAccess.getInstance().getAccess();
		checkInternetAccess();
		boolean after = InternetAccess.getInstance().getAccess();
		if(before!=after && after == true){
			// 
			new StatusRegister().run();
		}
		aliveLights();
		checkWifiDetection();
		checkTetheringDetection();
	}

	private void checkTetheringDetection() {
		try {
			String ssid = "";
			Process uptimeProc = Runtime.getRuntime().exec("iwgetid -r");
			BufferedReader in = new BufferedReader(new InputStreamReader(uptimeProc.getInputStream()));
			String line = in.readLine();
			//07:33:54 up 11 min,  1 user,  load average: 1.14, 0.96, 0.55
			//16:30:34 up  6:40,  1 user,  load average: 0.01, 0.01, 0.00
			if (line != null) {
				if((line.trim().equals("feerbox-wifi") || line.trim().equals("feerbox.com")) && firstTimeTethering==false){
					//Tethering activated
					LedService.animation();
					firstTimeTethering = true;
				}
				else{
					
				}
			}
		} catch (IOException e) {
			logger.error("IOException", e);
		}
	}

	private void checkWifiDetection() {
		if(!ClientRegister.getInstance().getWifiDetection() && StartFeerBoxClient.kismet!=null){
			StartFeerBoxClient.kismet.disconnectFromServer();
		}
	}

	private void checkInternetAccess() {
		try {
            //make a URL to a known source
            URL url = new URL("http://www.google.com");

            //open a connection to that source
            HttpURLConnection urlConnect = (HttpURLConnection)url.openConnection();

            //trying to retrieve data from the source. If there
            //is no connection, this line will fail
            urlConnect.setConnectTimeout(5000);
            urlConnect.setReadTimeout(5000);
            Object objData = urlConnect.getContent();
            if(ClientRegister.getInstance().getInternet()){
    			//logger.debug("YES Internet connection "+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    			InternetAccess.getInstance().setAccess(true);
    		}
    		else{
    			logger.debug("FORCED No Internet connection ");
    			InternetAccess.getInstance().setAccess(false);
    		}

        } catch (UnknownHostException e) {
            logger.error("No Internet connection ");
            InternetAccess.getInstance().setAccess(false);
        }
        catch (IOException e) {
            logger.error("No Internet connection ");
            InternetAccess.getInstance().setAccess(false);
        }
	}

	private void aliveLights() {
		if(ClientRegister.getInstance().getAliveLights()){
			Date lastAnswer = ClientRegister.getInstance().getLastAnswerSaved();
			long seconds = (new Date().getTime()-lastAnswer.getTime())/1000;
			if(seconds>120){
				LedService.animation();
			}
		}
	}

}
