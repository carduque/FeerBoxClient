package com.feerbox.client.registers;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.ListIterator;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.TerminalFactory;

import org.apache.log4j.Logger;

import com.feerbox.client.db.ReadCleaner;
import com.feerbox.client.model.Cleaner;

public class NFCReader extends Thread {
	private CardTerminal terminal;
	private Card card;
	final static Logger logger = Logger.getLogger(NFCReader.class);

	public void run() {
		// logger.debug("My thread is in running state.");
		try {
			while (terminal.waitForCardPresent(0)) {
				try {
					card = terminal.connect("T=0");
					// System.out.println("Terminal connected");
					byte[] baReadUID = new byte[5];

					baReadUID = new byte[] { (byte) 0xFF, (byte) 0xCA, (byte) 0x00, (byte) 0x00, (byte) 0x00 };
					CardChannel channel = card.getBasicChannel();
					String uid = send(baReadUID, channel);
					//logger.info("NFC UID: " + uid);
					// Toolkit.getDefaultToolkit().beep();
					Cleaner cleaner = ReadCleaner.read(new Cleaner(uid));
					logger.info("NFC UID: " + uid + " - "+cleaner.getName()+" "+cleaner.getSurname());
					
					// terminal.waitForCardAbsent(0);
					// logger.debug("Card removed");
				} catch (Exception e) {
					logger.error("Terminal NOT connected: " + e.toString());
				}
			}
		} catch (CardException e) {
			logger.error("CardException", e);
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

	public boolean init() {
		TerminalFactory tf = TerminalFactory.getDefault();
		CardTerminals ct = tf.terminals();
		List<CardTerminal> l = null;
		Card card = null;

		try {
			l = ct.list();
		} catch (Exception e) {
			logger.error("Error listing Terminals: " + e.toString());
			return false;
		}

		logger.debug("List of PC/SC Readers connected:");
		ListIterator i = l.listIterator();
		while (i.hasNext()) {
			logger.debug("Reader: " + ((CardTerminal) i.next()).getName());
		}
		// Pick up the first one
		String terminalName = l.get(0).getName();
		terminal = ct.getTerminal(terminalName);
		logger.debug("Terminal fetched: " + terminal.getName());
		return true;
	}
}
