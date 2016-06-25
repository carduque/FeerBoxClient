package com.feerbox.client;

import com.diozero.I2CLcd;
import com.diozero.util.RuntimeIOException;
import com.diozero.util.SleepUtil;

public class LCDTest2 {
	public static void main(String[] args) {
		// Initialise display
		try (I2CLcd lcd = new I2CLcd(16, 2)) {
			
			byte[] space_invader = new byte[] { 0x00, 0x0e, 0x15, 0x1f, 0x0a, 0x04, 0x0a, 0x11 };
			byte[] smilie = new byte[] { 0x00, 0x00, 0x0a, 0x00, 0x00, 0x11, 0x0e, 0x00 };
			byte[] frownie = new byte[] { 0x00, 0x00, 0x0a, 0x00, 0x00, 0x00, 0x0e, 0x11 };
			lcd.createChar(0, space_invader);
			lcd.createChar(1, smilie);
			lcd.createChar(2, frownie);
		
			lcd.setCursorPosition(0, 0);
			lcd.addText((byte) 'H');
			lcd.addText((byte) 'e');
			lcd.addText((byte) 'l');
			lcd.addText((byte) 'l');
			lcd.addText((byte) 'o');
			lcd.addText((byte) ' ');
			lcd.addText((byte) ' ');
			lcd.addText((byte) 0);
			lcd.addText((byte) 1);
			lcd.addText((byte) 2);
			lcd.setCursorPosition(0, 1);
			lcd.addText((byte) 'W');
			lcd.addText((byte) 'o');
			lcd.addText((byte) 'r');
			lcd.addText((byte) 'l');
			lcd.addText((byte) 'd');
			lcd.addText((byte) '!');
			lcd.addText((byte) ' ');
			lcd.addText((byte) 0);
			lcd.addText((byte) 1);
			lcd.addText((byte) 2);
			SleepUtil.sleepSeconds(5);
			lcd.clear();
			
			for (int i=0; i<1; i++) {
				// Send some text
				lcd.setText(0, "Hello -");
				lcd.setText(1, "World! " + i);
				SleepUtil.sleepSeconds(1);
				
				lcd.clear();
				SleepUtil.sleepSeconds(1);
			  
				// Send some more text
				lcd.setText(0, ">         RPiSpy");
				lcd.setText(1, ">        I2C LCD");
				SleepUtil.sleepSeconds(1);
			}
			
			SleepUtil.sleepSeconds(1);
			lcd.clear();
			
			for (byte b : "Hello Matt!".getBytes()) {
				lcd.addText(b);
				SleepUtil.sleepSeconds(.2);
			}
			
			SleepUtil.sleepSeconds(1);
			lcd.clear();

			int x=0;
			for (int i=0; i<3; i++) {
				for (byte b : "Hello World! ".getBytes()) {
					if (x++ == lcd.getColumnCount()) {
						lcd.entryModeControl(true, true);
					}
					lcd.addText(b);
					SleepUtil.sleepSeconds(.2);
				}
			}
			SleepUtil.sleepSeconds(1);
			lcd.clear();
			lcd.entryModeControl(true, false);
			
			lcd.setCursorPosition(0, 0);
			lcd.addText((byte) 'H');
			lcd.addText((byte) 'e');
			lcd.addText((byte) 'l');
			lcd.addText((byte) 'l');
			lcd.addText((byte) 'o');
			lcd.addText((byte) ' ');
			lcd.addText((byte) ' ');
			lcd.addText((byte) 0);
			lcd.addText((byte) 1);
			lcd.addText((byte) 2);
			lcd.setCursorPosition(0, 1);
			lcd.addText((byte) 'W');
			lcd.addText((byte) 'o');
			lcd.addText((byte) 'r');
			lcd.addText((byte) 'l');
			lcd.addText((byte) 'd');
			lcd.addText((byte) '!');
			lcd.addText((byte) ' ');
			lcd.addText((byte) 0);
			lcd.addText((byte) 1);
			lcd.addText((byte) 2);
			System.out.println("Sleeping for 60 seconds...");
			SleepUtil.sleepSeconds(60);
			
			lcd.clear();
		} catch (RuntimeIOException e) {
			System.out.println("Error: {}"+ e.getMessage());
		}
	}
}
