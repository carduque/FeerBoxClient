package com.feerbox.client;

import com.diozero.HD44780Lcd;
import com.diozero.HD44780Lcd.LcdConnection;
import com.diozero.api.I2CConstants;
import com.diozero.util.RuntimeIOException;
import com.diozero.util.SleepUtil;

public class LCDTest3 {
	private static LcdConnection lcd_connection = new HD44780Lcd.PCF8574LcdConnection(I2CConstants.BUS_1, HD44780Lcd.PCF8574LcdConnection.DEFAULT_DEVICE_ADDRESS);
	private static HD44780Lcd  lcd = new HD44780Lcd(lcd_connection, 16, 2);
	public static void main(String[] args) {
		// Initialise display
		try{
			
			lcd.setCursorPosition(0, 0);
			lcd.setText(0, "Hello World");
			SleepUtil.sleepSeconds(10);
			lcd.clear();

		} catch (RuntimeIOException e) {
			System.out.println("Error: {}"+ e.getMessage());
		}
	}
}
