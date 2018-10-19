package com.feerbox.client.synchronization;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CPSynchronizationTest {
	
	public static void main(String[] args) throws NoSuchAlgorithmException {
		/*
		 * CLIENT SIDE
		 */
		//Read CP entries where upload!=1 order by time desc limit MAX
		//Update CP entries set upload=2
		//Generate client id for each CP entry and sent it too: MD5 hash
		//Sent them
		//Wait for response or timeout
		//If response ok, update CP entries set upload=1 where ids included in response
		//If timeout, do nothing
		
		/*
		 * SERVER SIDE
		 */
		//Recieve CP entries to upload
		//Generate MD5 Hash if entries don't have that information (old software)
		//Insert them on conflict do nothing
		//Returns Ids just inserted
		
		String str="test string1";
	    MessageDigest messageDigest=MessageDigest.getInstance("MD5");
	    messageDigest.update(str.getBytes(),0,str.length());
	    System.out.println("String1: "+new BigInteger(1,messageDigest.digest()).toString(16));
	    
	    String str2="test string";
	    messageDigest.update(str2.getBytes(),0,str2.length());
	    System.out.println("String2: "+new BigInteger(1,messageDigest.digest()).toString(16));
	    
	    String str3="test string2";
	    MessageDigest messageDigest2=MessageDigest.getInstance("MD5");
	    messageDigest2.update(str3.getBytes(),0,str3.length());
	    System.out.println("String3: "+new BigInteger(1,messageDigest2.digest()).toString(16));
		
	}
}
