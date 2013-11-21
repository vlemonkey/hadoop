package com.fish.test.outputtest;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class ModelMapper extends Mapper<Object, Text, Text, Text>{
	
	@Override
	public void setup(Context context) throws IOException, InterruptedException{
		
	}
	
	@Override
	public void map(Object key, Text value, Context context)
		throws IOException, InterruptedException{
		
		context.write(value, value);
	}
	
	@Override
	public void cleanup(Context context) throws IOException, InterruptedException{
		
	}
}
