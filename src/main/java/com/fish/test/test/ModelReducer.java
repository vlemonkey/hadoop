package com.fish.test.test;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class ModelReducer extends Reducer<Text, LongWritable, Text, LongWritable>{
	
	LongWritable tv = new LongWritable();
	
	@Override
	public void setup(Context context) throws IOException, InterruptedException{
	}
	
	@Override
	public void reduce(Text key, Iterable<LongWritable> values, Context context) 
			throws IOException, InterruptedException{
		long count = 0;
		for (LongWritable l : values) {
			count += Long.parseLong(l.toString());
		}
		
		tv.set(count);
		context.write(key, tv);
	}
	
	@Override
	public void cleanup(Context context) throws IOException, InterruptedException{
	}
}
