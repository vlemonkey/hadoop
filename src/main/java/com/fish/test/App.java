package com.fish.test;

import java.util.Date;

import org.apache.commons.lang.time.FastDateFormat;



/**
 * Hello world!
 *
 */
public class App{
	
    public static void main( String[] args ){
        FastDateFormat FAST_SECOND_FORMATETR = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        String isoDT = FAST_SECOND_FORMATETR.format(now);
        System.out.println(isoDT);
    }
}
