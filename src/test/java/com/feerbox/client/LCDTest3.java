package com.feerbox.client;

import com.diozero.I2CLcd;
import com.diozero.util.RuntimeIOException;
import com.diozero.util.SleepUtil;

public class LCDTest3 {
	public static void main(String[] args) {
		// Initialise display
		try (I2CLcd lcd = new I2CLcd(16, 2)) {
			
			lcd.setCursorPosition(0, 0);
			lcd.setText(0, "Hello World");
			SleepUtil.sleepSeconds(10);
			lcd.clear();

		} catch (RuntimeIOException e) {
			System.out.println("Error: {}"+ e.getMessage());
		}
	}
}
