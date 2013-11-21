package com.fish.test.sequence.read;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class ModelMapper extends Mapper<Object, Text, Text, Text>{
	
	Text tk = new Text();
	Text tv = new Text();
	
	@Override
	public void map(Object key, Text value, Context context)
		throws IOException, InterruptedException{
		System.out.println("key:" + key.toString());
		String[] strs = value.toString().split("\t");
		System.out.println(strs[0]);
		System.out.println(StringUtils.equals(strs[0], "CMNET"));
		System.out.println(strs[1]);
		System.out.println(strs[2]);
		System.out.println();
		context.write(new Text("map") , value);
	}
	
	@Override
	public void setup(Context context) throws IOException, InterruptedException{
		
	}
	
	@Override
	public void cleanup(Context context) throws IOException, InterruptedException{
		
	}
}
