package com.feerbox.client;

import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;

import java.awt.Toolkit;
import java.nio.ByteBuffer;

import javax.smartcardio.Card;

public class NFCReader extends Thread {
	 private CardTerminal terminal;
	 private Card card;

	public void run(){  
		    System.out.println("My thread is in running state.");  
		    try {
				while (terminal.waitForCardPresent(0)) {
					try {
				        card = terminal.connect("T=0");
				        //System.out.println("Terminal connected");
				        byte[] baReadUID = new byte[5];

						baReadUID = new byte[] { (byte) 0xFF, (byte) 0xCA, (byte) 0x00, (byte) 0x00, (byte) 0x00 };
						CardChannel channel = card.getBasicChannel();
						System.out.println("UID: " + send(baReadUID, channel));
						//Toolkit.getDefaultToolkit().beep();
			            terminal.waitForCardAbsent(0);
			            System.out.println("Card removed");
				    } catch (Exception e) {
				        System.out.println("Terminal NOT connected: " + e.toString());
				    }
				}
			} catch (CardException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public void setTerminal(CardTerminal terminal) {
		this.terminal = terminal;
	}
	public static String send(byte[] cmd, CardChannel channel) {

		String res = "";

		byte[] baResp = new byte[258];
		ByteBuffer bufCmd = ByteBuffer.wrap(cmd);
		ByteBuffer bufResp = ByteBuffer.wrap(baResp);

		// output = The length of the received response APDU
		int output = 0;

		try {

			output = channel.transmit(bufCmd, bufResp);
		} catch (CardException ex) {
			ex.printStackTrace();
		}

		for (int i = 0; i < output; i++) {
			res += String.format("%02X", baResp[i]);
			// The result is formatted as a hexadecimal integer
		}

		return res;
	}
}
