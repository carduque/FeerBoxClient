package com.feerbox.client.registers;

import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.TerminalFactory;

import org.apache.log4j.Logger;

import com.feerbox.client.LCDWrapper;
import com.feerbox.client.db.ReadCleaner;
import com.feerbox.client.db.SaveCleaningService;
import com.feerbox.client.model.Cleaner;
import com.feerbox.client.model.CleaningService;

public class NFCReader extends Thread {
	private CardTerminal terminal;
	private Card card;
	final static Logger logger = Logger.getLogger(NFCReader.class);
	private static Timestamp lastNFC = null;

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
					long now = System.currentTimeMillis();
					if(lastNFC==null || (now - lastNFC.getTime())>60*1000){ //Skip card less than a minute
						//logger.info("NFC UID: " + uid);
						// Toolkit.getDefaultToolkit().beep();
						Cleaner cleaner = ReadCleaner.read(new Cleaner(uid));
						CleaningService cleaningService = new CleaningService();
						if(cleaner!=null || cleaner.getName()!=null){
							logger.info("NFC UID: " + uid + " - "+cleaner.getName()+" "+cleaner.getSurname());
							if(ClientRegister.getInstance().getLCDActive()){
								LCDWrapper.clear();
								LCDWrapper.setTextRow0(cleaner.getName()+" "+cleaner.getSurname());
								LCDWrapper.setCurrentTimeRow1();
							}
							//cleaningService.setCleanerReference(cleaner.getName()+" "+cleaner.getSurname()); //TO DO change by real identifier
							cleaningService.setCleanerReference(cleaner.getReference());
							terminal.waitForCardAbsent(0);
							cleaningService.setFeerboxReference(ClientRegister.getInstance().getReference());
							
							Date date = new Date(now);
							cleaningService.setTime(date);
							SaveCleaningService.save(cleaningService);
							lastNFC = new Timestamp(now);
						}
						else{
							logger.info("NFC UID: " + uid + " not found on DB ");
							if(ClientRegister.getInstance().getShowUnknownNFCs()){
								if(ClientRegister.getInstance().getLCDActive()){
									LCDWrapper.clear();
									LCDWrapper.setTextRow0("UID:"+uid);
									LCDWrapper.setCurrentTimeRow1();
								}
							}
							//If NFC is not in DB, it won't send that cleaningService to server
						}
						// logger.debug("Card removed");
					}
				} catch (Exception e) {
					logger.error("Terminal NOT connected: " + e.toString());
				}
			}
		} catch (CardException e) {
			logger.error("CardException", e);
		} catch (Exception e){
			logger.error(e.getMessage());
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
