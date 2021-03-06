package com.feerbox.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandExecutorTest {
	public static void main(String[] args) {
		System.out.println("going to execute...");
		ProcessBuilder pb = new ProcessBuilder("/bin/bash", "grep-logs.sh", "\"^$(date -d -1hour +'%Y-%m-%d %H')\"");
		pb.directory(new File("/opt/FeerBoxClient/FeerBoxClient/scripts"));
		try {
			Process process = pb.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			StringBuilder builder = new StringBuilder();
			String line = null;
			while ( (line = reader.readLine()) != null) {
			   builder.append(line);
			   builder.append(System.getProperty("line.separator"));
			}
			System.out.println(builder.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
