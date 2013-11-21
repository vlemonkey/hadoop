package com.boco.sort;

import java.io.IOException;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class OrderModelReducer extends Reducer<OrderObject, Text, Text, NullWritable>{

	Text tv = new Text();

	@Override
	public void setup(Context context) throws IOException, InterruptedException{
		
	}
	
	@Override
	public void reduce(OrderObject key, Iterable<Text> values, Context context) 
			throws IOException, InterruptedException{
		int count = 1;
		for (Text text : values) {
			tv.set(key.toString().concat(",").concat(text.toString())
					.concat(",").concat(String.valueOf(count++)));
			context.write(tv, NullWritable.get());
		}
	}
	
	@Override
	public void cleanup(Context context) throws IOException, InterruptedException{
		
	}
}
