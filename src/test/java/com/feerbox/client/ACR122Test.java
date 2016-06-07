package com.feerbox.client;

import java.util.List;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;

public class ACR122Test {
	public static void main(String[] args) throws CardException {
		// show the list of available terminals
        TerminalFactory factory = TerminalFactory.getDefault();

        List<CardTerminal> terminals = factory.terminals().list();
        System.out.println("Terminals: " + terminals);
	}
}
