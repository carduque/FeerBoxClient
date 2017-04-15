package com.feerbox.client;

import java.util.concurrent.TimeUnit;

public class ThreadTest extends Thread {
	private String name = "";
	private int executions = 0;
	private int whenException = 0;
	private double random = Math.random();
	public ThreadTest(String name, int i) {
		this.name = name;
		this.whenException = i;
	}
	public void run(){
			System.out.println("Thread "+this.name);
			if(this.executions==this.whenException){
				System.out.println(random+" Exception on "+this.name+"!");
				throw new NullPointerException();
			}
			this.executions++;
			/*
			TimeUnit.SECONDS.sleep(3);
			System.out.println(Math.ceil(random*1000)+" Thread "+this.name+" Test: "+SchedulerTest.executions++);
			if(SchedulerTest.executions==3){
				System.out.println(random+" Exception!");
				throw new NullPointerException();
			}
			for(long i=0;i==10000000L;i++);
			*/
			
	}
}
