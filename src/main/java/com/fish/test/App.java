package com.fish.test;

import org.apache.hadoop.util.StringUtils;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        String str = "a,d,a,df,,,";
        String[] strs = StringUtils.split(str, ',');
        System.out.println(strs.length);
        for (String string : strs) {
			System.out.println(string);
		}
    }
}
