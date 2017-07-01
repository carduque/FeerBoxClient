package com.feerbox.client;

import java.io.IOException;

public class LaserTest {
	public static void main(String[] args){
		System.out.println("starting");
		ProcessBuilder pb = new ProcessBuilder("/usr/bin/python", "/opt/FeerBoxClient/FeerBoxClient/scripts/countpeople/laser_count.py");
		System.out.println("finishing");
		try {
			Process process = pb.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
