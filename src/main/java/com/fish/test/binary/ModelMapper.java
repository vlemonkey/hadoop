package com.fish.test.binary;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class ModelMapper extends Mapper<Object, Text, Text, Text>{
	
	Text tk = new Text();
	Text tv = new Text();
	
	@Override
	public void setup(Context context) throws IOException, InterruptedException{
		
	}
	
	@Override
	public void map(Object key, Text value, Context context)
		throws IOException, InterruptedException{
		tk.set(key.toString());
		byte[] bytes = value.toString().getBytes();
		for (byte b : bytes) {
			System.out.print(b + " ");
		}
		System.out.println();
		System.out.println("-----------------------------");
//		System.out.println(bytes[0]);
//		System.out.println(bytes[1]);
		context.write(tk, value);
	}
	
	@Override
	public void cleanup(Context context) throws IOException, InterruptedException{
		
	}
}
