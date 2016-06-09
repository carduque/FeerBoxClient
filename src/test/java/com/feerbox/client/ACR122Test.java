package com.feerbox.client;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;

import javax.smartcardio.ATR;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;

public class ACR122Test {
	public static void main(String[] args) throws CardException {
		// show the list of available terminals
		TerminalFactory factory = TerminalFactory.getDefault();

		List<CardTerminal> terminals = factory.terminals().list();
		System.out.println("Terminals: " + terminals);

		// Use the first terminal
		CardTerminal terminal = terminals.get(0);

		// Connect with the card
		terminal.waitForCardPresent(10000);
		// establish a connection with the card
		System.out.println("Is Card Present " + terminal.isCardPresent());
		Card card = terminal.connect("*");
		System.out.println("card: " + card);
		ATR atr = card.getATR();
		System.out.println("ATR Length - " + atr.getBytes().length + "\nATR Bytes: ");
		System.out.println(byteToHex(atr.getBytes()));
		System.out.println("Historical Length - " + atr.getHistoricalBytes().length + "\nHistorican Bytes: ");
		System.out.println(byteToHex(atr.getHistoricalBytes()));
		CardChannel channel = card.getBasicChannel();
		System.out.println("Channel: " + channel);
		System.out.println("Protocol " + card.getProtocol());

		byte[] baReadUID = new byte[5];

		baReadUID = new byte[] { (byte) 0xFF, (byte) 0xCA, (byte) 0x00, (byte) 0x00, (byte) 0x00 };

		System.out.println("UID: " + send(baReadUID, channel));
		// If successfull, the output will end with 9000

		card.disconnect(true);
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

	private static String byteToHex(byte[] data) {
		StringBuilder localStringBuilder = new StringBuilder();
		for (int i = 0; i < data.length; i++) {
			String str;
			if ((str = Integer.toHexString(data[i] & 0xFF).toUpperCase()).length() == 1) {
				localStringBuilder.append(0);
			}
			localStringBuilder.append(str).append(" ");
		}
		return localStringBuilder.substring(0, localStringBuilder.length() - 1);
	}
}
