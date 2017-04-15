package com.feerbox.client;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class SchedulerTest {
	private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	public static int executions = 0;
	
	@Test
	public void threadTest(){
		ThreadTest threadTest1 = new ThreadTest("A", 3);
		ThreadTest threadTest2 = new ThreadTest("B", 5);
		ThreadTest threadTest3 = new ThreadTest("C", 7);
		ThreadTest threadTest4 = new ThreadTest("D", 9);
		ThreadTest threadTest5 = new ThreadTest("E", 10);
		scheduler.scheduleAtFixedRate(threadTest1, 0, 1, TimeUnit.SECONDS);
		scheduler.scheduleAtFixedRate(threadTest2, 0, 1, TimeUnit.SECONDS);
		scheduler.scheduleAtFixedRate(threadTest3, 0, 1, TimeUnit.SECONDS);
		scheduler.scheduleAtFixedRate(threadTest4, 0, 1, TimeUnit.SECONDS);
		scheduler.scheduleAtFixedRate(threadTest5, 0, 1, TimeUnit.SECONDS);
		
		/*scheduler.scheduleAtFixedRate(threadTest2, 1, 3, TimeUnit.SECONDS);
		ThreadTest threadTest3 = new ThreadTest();
		ThreadTest2 threadTest4 = new ThreadTest2();
		scheduler.scheduleAtFixedRate(threadTest3, 0, 3, TimeUnit.SECONDS);
		scheduler.scheduleAtFixedRate(threadTest4, 1, 3, TimeUnit.SECONDS);*/
		for(;;);
	}
}
