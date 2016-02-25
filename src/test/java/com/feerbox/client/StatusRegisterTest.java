package com.feerbox.client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class StatusRegisterTest {
	@Test
	public void testUpTime() {
		String input = "07:33:54 up 11 min,  1 user,  load average: 1.14, 0.96, 0.55";
        Pattern parse = Pattern.compile("((\\d+) days,)? (\\d+):(\\d+)");
        Matcher matcher = parse.matcher(input);
        if (matcher.find()) {
            String _days = matcher.group(2);
            String _hours = matcher.group(3);
            String _minutes = matcher.group(4);
            int days = _days != null ? Integer.parseInt(_days) : 0;
            int hours = _hours != null ? Integer.parseInt(_hours) : 0;
            int minutes = _minutes != null ? Integer.parseInt(_minutes) : 0;
            //uptime = (minutes * 60000) + (hours * 60000 * 60) + (days * 6000 * 60 * 24);
            System.out.println(days + "d - "+hours+"h - "+minutes+"m");
        }
	}

}
