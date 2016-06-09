package com.feerbox.client;

import java.awt.Toolkit;
import java.util.List;
import java.util.ListIterator;

import javax.smartcardio.ATR;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

 
public class ReaderApplication {
 
     // a0 00 00 00 62 03 01 0c 01 01
     private static final byte[] SELECT = {(byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x00, (byte) 0x0b, 
                         /* AID */       (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04,
                                          (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08,
                                          (byte) 0x09, (byte) 0x00, (byte) 0x00,
                         /*  Le */       (byte) 0x00};
     
    private CommandAPDU SELECT_APDU = new CommandAPDU(SELECT);
    
    private CardTerminal terminal = null;
    private Card card = null;
    private String terminalName;
    private String terminalType;
    private String terminalProtocol;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
              ReaderApplication readerApp = new ReaderApplication();
              readerApp.go();
        } catch(Exception e) {
             System.out.println("Error: " + e.getMessage());
        }
    }
    
    private ReaderApplication() throws Exception {
         terminalProtocol = "T=0";
         init();
    }
    
    private void init() throws Exception {
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
        terminalName = l.get(0).getName();
        terminal = ct.getTerminal(terminalName);
        System.out.println("Terminal fetched: " + terminal.getName());
    }
    
    public void go() {
         try {
            while (terminal.waitForCardPresent(0)) {
 
                try {
                    card = terminal.connect(terminalProtocol);
                    System.out.println("Terminal connected");
                } catch (Exception e) {
                    System.out.println("Terminal NOT connected: " + e.toString());
                }
                
                ATR atr = card.getATR();
                System.out.println("ATR: " + byteToHex(atr.getBytes()));
 
                CardChannel ch = card.getBasicChannel();
 
                if (check9000(ch.transmit(SELECT_APDU))) {
                    System.out.println("SELECT OKAY");
                } else {
                    System.out.println("SELECT NOT OKAY");
                    return;
                }
                
                byte[] x = null;
                ResponseAPDU ra = null;

                // Put here code for sending/receiving APDUs
                
               Toolkit.getDefaultToolkit().beep();
                terminal.waitForCardAbsent(0);
                System.out.println("Card removed");
            }// while
        }// try
        catch (CardException e) {
            System.out.println("Error isCardPresent()" + e.toString());
        }
    }
 
    public static boolean check9000(ResponseAPDU ra) {
        byte[] response = ra.getBytes();
        return (response[response.length - 2] == (byte) 0x90 && response[response.length - 1] == (byte) 0x00);
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