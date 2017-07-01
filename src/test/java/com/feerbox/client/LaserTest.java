package com.feerbox.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LaserTest {
	public static void main(String[] args) throws IOException{
		System.out.println("starting");
		ProcessBuilder pb = new ProcessBuilder("/home/pi/test/laser/laser.sh");
		 Process p = pb.start();
		 BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		 String line = null;
		 while ((line = reader.readLine()) != null)
		 {
		    System.out.println(line);
		 }
		System.out.println("finishing");
	}
	
}
