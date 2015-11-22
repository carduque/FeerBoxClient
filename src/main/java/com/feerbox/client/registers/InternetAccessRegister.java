package com.feerbox.client.registers;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InternetAccessRegister implements Runnable {

	public void run() {
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
    			System.out.println("YES Internet connection "+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    			InternetAccess.getInstance().setAccess(true);
    		}
    		else{
    			System.out.println("FORCED No Internet connection "+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    			InternetAccess.getInstance().setAccess(false);
    		}

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            System.out.println("No Internet connection "+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            InternetAccess.getInstance().setAccess(false);
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("No Internet connection "+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            InternetAccess.getInstance().setAccess(false);
        }
	}

}
