package com.feerbox.client;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.List;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;


public class NFCReaderTest2 {
	public static void main(String[] args) throws Exception {
		TerminalFactory factory = TerminalFactory.getDefault();
		List<CardTerminal> terminals = factory.terminals().list();
		System.out.println("Terminals: " + terminals);

		// Get the first terminal in the list
		CardTerminal terminal = terminals.get(0);
		int index = 1;
		while (terminal.waitForCardPresent(0)) {
			if(index==1) terminalRead1(terminal);
			if(index==2) terminalRead2(terminal);
			if(index==3) terminalRead3(terminal);
			index++;
			if(index>3) index = 1;
			//terminal.waitForCardAbsent(0);
		}
	}

	private static void terminalRead3(CardTerminal terminal) {
		try {
			Card card = terminal.connect("T=0");
			CardChannel channel = card.getBasicChannel();
			CommandAPDU getAts = new CommandAPDU(0xFF, 0xCA, 0x00, 0x00, 0x04);
			ResponseAPDU response = channel.transmit(getAts);

			System.out.println(response.getSW1() + " - "+response.getSW2());
			card.disconnect(false);
		} catch (CardException e) {
			e.printStackTrace();
		}
	}

	private static void terminalRead2(CardTerminal terminal) {
		try {
			Card card = terminal.connect("T=0");
			byte[] baReadUID = new byte[5];

			baReadUID = new byte[] { (byte) 0xFF, (byte) 0xCA, (byte) 0x00, (byte) 0x00, (byte) 0x00 };
			CardChannel channel = card.getBasicChannel();
			String res = "";

			byte[] baResp = new byte[258];
			ByteBuffer bufCmd = ByteBuffer.wrap(baReadUID);
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
			System.out.println(res);
			card.disconnect(false);
		} catch (CardException e) {
			e.printStackTrace();
		}
	}

	private static void terminalRead1(CardTerminal terminal) {
		try {
			Card card = terminal.connect("*");
			byte[] baReadUID = new byte[5];

			baReadUID = new byte[] { (byte) 0xFF, (byte) 0xCA, (byte) 0x00, (byte) 0x00, (byte) 0x00 };
			CardChannel channel = card.getBasicChannel();
			ResponseAPDU response = channel.transmit(new CommandAPDU(baReadUID));
			System.out.print(bytesToHex(response.getBytes()) + " - ");
			System.out.println(bytesToHex(response.getData()));
			card.disconnect(false);
		} catch (CardException e) {
			e.printStackTrace();
		}
	}
	
	static String bin2hex(byte[] data) {
	    return String.format("%0" + (data.length * 2) + "X", new BigInteger(1,data));
	}
	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
}
