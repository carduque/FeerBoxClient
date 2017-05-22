package com.feerbox.client;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

public class SchedulerTest {
	private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
	
	public static int executions = 0;
	
	@Test
	public void threadTest(){
		ThreadTest threadTest1 = new ThreadTest("A", 3);
		ThreadTest threadTest2 = new ThreadTest("B", 5);
		ThreadTest threadTest3 = new ThreadTest("C", 7);
		ThreadTest threadTest4 = new ThreadTest("D", 9);
		ThreadTest threadTest5 = new ThreadTest("E", 10);
		ScheduledFuture<?>  tmp1 = scheduler.scheduleAtFixedRate(threadTest1, 0, 1, TimeUnit.SECONDS);
		scheduler.scheduleAtFixedRate(threadTest2, 0, 1, TimeUnit.SECONDS);
		scheduler.scheduleAtFixedRate(threadTest3, 0, 1, TimeUnit.SECONDS);
		scheduler.scheduleAtFixedRate(threadTest4, 0, 1, TimeUnit.SECONDS);
		scheduler.scheduleAtFixedRate(threadTest5, 0, 1, TimeUnit.SECONDS);
		
		/*scheduler.scheduleAtFixedRate(threadTest2, 1, 3, TimeUnit.SECONDS);
		ThreadTest threadTest3 = new ThreadTest();
		ThreadTest2 threadTest4 = new ThreadTest2();
		scheduler.scheduleAtFixedRate(threadTest3, 0, 3, TimeUnit.SECONDS);
		scheduler.scheduleAtFixedRate(threadTest4, 1, 3, TimeUnit.SECONDS);*/
		for(;;){
			//if(tmp1.getDelay(TimeUnit.SECONDS)!=0) System.out.println("A:"+tmp1.getDelay(TimeUnit.SECONDS));
			/*try {
				if(tmp1.isDone()) System.out.println("is done");
				tmp1.get(2, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				System.out.println("InterruptedException");
				return;
			} catch (ExecutionException e) {
				System.out.println("ExecutionException");
				return;
			} catch (TimeoutException e) {
				System.out.println("TimeoutException");
				scheduler.scheduleAtFixedRate(threadTest1, 0, 1, TimeUnit.SECONDS);
			}*/
		}
	}
}
