package com.feerbox.client;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.feerbox.client.registers.InformationServerRegister;
import com.feerbox.client.registers.InternetAccess;

public class SafeBulkyCounterPeopleTest {
	@Before
	public void initObjects() {
		InternetAccess.getInstance().setAccess(true);
		
    }
	
	@Test
	public void NoCounterPeopleEntries(){
		InformationServerRegister register = new InformationServerRegister();
		register.run();
		fail();
	}
	
	@Test
	public void JustOneCounterPeopleEntry(){
		fail();
	}
	
	@Test
	public void TimeoutError(){
		fail();
	}
	
	@Test
	public void ServerError(){
		fail();
	}
	
	@Test
	public void LotOfEntriesNoError(){
		fail();
	}
}
