package com.feerbox.client;

import java.util.concurrent.TimeUnit;

public class ThreadTest extends Thread {
	private double random = Math.random();
	public void run(){
		try {
			System.out.println("Starting...");
			TimeUnit.SECONDS.sleep(3);
			System.out.println(Math.ceil(random*1000)+" Thread Test: "+SchedulerTest.executions++);
			if(SchedulerTest.executions==3){
				System.out.println(random+" Exception!");
				throw new NullPointerException();
			}
			for(long i=0;i==10000000L;i++);
		} catch (Throwable e) {
			System.out.println(random+" Exception catch!");
		}
	}
}
