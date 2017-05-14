package com.feerbox.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class CommandExecutionTest {
	
	public static void main(String[] args) {
		System.out.println(executeCommandLine("date"));
	}

	
	private static String executeCommandLine(String command) {
		String out = "";
		BufferedReader in = null;
		Process proc = null;
		try {
			proc = Runtime.getRuntime().exec(command);
			in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			String line = in.readLine();
			if (line != null) {
				out = line;
			}
		} catch (IOException e) {
			System.out.println("Error executing command: "+e.getMessage());
		}
		finally{
			if(in!=null){
				try {
					in.close();
				} catch (IOException e) {
					System.out.println("Error executing command: "+e.getMessage());
				}
			}
			try {
				if(!proc.waitFor(1, TimeUnit.MINUTES)) {
				    //timeout - kill the process. 
				    proc.destroyForcibly();
				}
			} catch (InterruptedException e) {
				System.out.println("Error executing command: "+e.getMessage());
			}
			
		}
        return out;
	}
}
