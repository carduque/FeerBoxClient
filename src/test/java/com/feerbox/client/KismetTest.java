package com.feerbox.client;

import java.net.ConnectException;

import com.feerbox.client.registers.KismetClient;

public class KismetTest {
	public static void main(String[] args) {
		KismetClient conn = new KismetClient();
		if (conn.connectToServer()) {
			System.out.println("Success!");
		}
	}
}
