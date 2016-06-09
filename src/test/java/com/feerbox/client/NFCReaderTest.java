package com.feerbox.client;

import java.util.List;
import java.util.ListIterator;

import javax.smartcardio.Card;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.TerminalFactory;

public class NFCReaderTest {
	public static void main(String[] args) throws Exception {
		NFCReader reader = new NFCReader();
		reader.setTerminal(init());
		reader.start();
	}
	
	private static CardTerminal init() throws Exception {
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
       CardTerminal terminal = ct.getTerminal(terminalName);
       System.out.println("Terminal fetched: " + terminal.getName());
       return terminal;
   }
}
