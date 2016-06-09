package com.feerbox.client;

import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.TerminalFactory;

import java.awt.Toolkit;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.ListIterator;

import javax.smartcardio.Card;

public class NFCReader2 extends Thread {
	 private static CardTerminal terminal;
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
	
	static void init() throws Exception {
        TerminalFactory tf = TerminalFactory.getDefault();
       CardTerminals ct = tf.terminals();
       List<CardTerminal> l = null;
       Card card = null;

       try {
           l = ct.list();
       } catch (Exception e) {
           System.out.println("Error listing Terminals: " + e.toString());
           throw e;
       }

       System.out.println("List of PC/SC Readers connected:");
       ListIterator i = l.listIterator();
       while (i.hasNext()) {
           System.out.println("Reader: " + ((CardTerminal) i.next()).getName());
       }
       // Pick up the first one
       String terminalName = l.get(0).getName();
       terminal = ct.getTerminal(terminalName);
       System.out.println("Terminal fetched: " + terminal.getName());
   }
	
}
