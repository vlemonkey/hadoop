package com.boco.model;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class ModelMapper extends Mapper<Object, Text, Text, Text>{
	
	Text tk = new Text();
	Text tv = new Text();
	
	@Override
	public void map(Object key, Text value, Context context)
		throws IOException, InterruptedException{
		
		// TODO: code
	}
	
	@Override
	public void setup(Context context) throws IOException, InterruptedException{
		
	}
	
	@Override
	public void cleanup(Context context) throws IOException, InterruptedException{
		
	}
}
