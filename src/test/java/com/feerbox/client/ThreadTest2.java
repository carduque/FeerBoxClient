package com.feerbox.client;

public class ThreadTest2 extends Thread {
	private double random = Math.random();
	public void run(){
		try {
			System.out.println(Math.ceil(random*1000)+" Thread Test2: "+SchedulerTest.executions++);
			/*if(SchedulerTest.executions==3){
				System.out.println(random+" Exception2!");
				throw new NullPointerException();
			}*/
		} catch (Throwable e) {
			System.out.println(random+" Exception2 catch!"+e.getMessage());
		}
	}
}
