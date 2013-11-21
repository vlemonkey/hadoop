package com.fish.test.wordcount;

import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class ModelMapper extends Mapper<Object, Text, Text, LongWritable>{
	
	private static final Pattern pattern = Pattern.compile(" "); 
	
	Text tk = new Text();
	LongWritable tv = new LongWritable();
	
	@Override
	public void setup(Context context) throws IOException, InterruptedException{
		
	}
	
	@Override
	public void map(Object key, Text value, Context context)
		throws IOException, InterruptedException{
		String[]  datas = pattern.split(value.toString());
		for (String str : datas) {
			tk.set(str);
			tv.set(1);
			context.write(tk, tv);
		}
//		tk.set("");
//		tv.set(1);
//		context.write(tk, tv);
	}
	
	@Override
	public void cleanup(Context context) throws IOException, InterruptedException{
		
	}
}
