package com.feerbox.client;

import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.feerbox.client.model.CounterPeople;
import com.feerbox.client.services.CounterPeopleService;


public class CounterPeopleTest {
	private static final int NUM = 1;

	@Test 
	public void saveServerBulkyTest(){
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
		Date startingDate = null;
		Date closingDate = null;
		try {
			startingDate = format.parse("08-02-2016 00:00:00");
			closingDate = format.parse("21-02-2016 23:59:00");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String feerBoxReference = "2015001";
		List<CounterPeople> counterPeoples = createCounterPeoples(startingDate, closingDate, feerBoxReference,NUM);
		String ids = CounterPeopleService.saveServerBulky(counterPeoples);
		assertNotNull(ids);
		assertTrue((ids.split(",", -1).length - 1)==NUM-1);
		System.out.println(ids);
	}
	
	private static List<CounterPeople> createCounterPeoples(Date startingDate, Date closingDate, String feerBoxReference, int max) {
		List<CounterPeople> out = new ArrayList<CounterPeople>();
		for(int i=0;i<max;i++){
			CounterPeople counterPeople = new CounterPeople();
			counterPeople.setType(CounterPeople.Type.LASER);
			int count=(int) (Math.random()*100)+1;
			if(count%2==0) counterPeople.setDistance(1.00);
			else counterPeople.setDistance(-1.00);
			counterPeople.setFeerBoxReference(feerBoxReference);
			counterPeople.setTime(new Timestamp(getRandomTimeBetweenTwoDates(startingDate.getTime(), closingDate.getTime())));
			out.add(counterPeople);
		}
		return out;
	}
	
	protected static long getRandomTimeBetweenTwoDates(long beginTime, long endTime) {
		long diff = endTime - beginTime + 1;
		return beginTime + (long) (Math.random() * diff);
	}
}
