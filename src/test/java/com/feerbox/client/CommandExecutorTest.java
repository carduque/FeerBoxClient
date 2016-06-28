package com.feerbox.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandExecutorTest {
	public static void main(String[] args) {
		System.out.println("going to execute...");
		ProcessBuilder pb = new ProcessBuilder("/bin/bash", "test.sh");
		pb.directory(new File("/home/vagrant/commandExecutor"));
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
