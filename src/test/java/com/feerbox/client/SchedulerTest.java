package com.feerbox.client;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class SchedulerTest {
	private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
	public static int executions = 0;
	
	@Test
	public void threadTest(){
		ThreadTest threadTest = new ThreadTest();
		//ThreadTest2 threadTest2 = new ThreadTest2();
		scheduler.scheduleAtFixedRate(threadTest, 0, 1, TimeUnit.SECONDS);
		/*scheduler.scheduleAtFixedRate(threadTest2, 1, 3, TimeUnit.SECONDS);
		ThreadTest threadTest3 = new ThreadTest();
		ThreadTest2 threadTest4 = new ThreadTest2();
		scheduler.scheduleAtFixedRate(threadTest3, 0, 3, TimeUnit.SECONDS);
		scheduler.scheduleAtFixedRate(threadTest4, 1, 3, TimeUnit.SECONDS);*/
		for(;;);
	}
}
