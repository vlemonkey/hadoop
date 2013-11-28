package com.fish.test.sequence;

import java.io.IOException;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

public class ModelReducer extends Reducer<NullWritable, Text, NullWritable, Text>{
	
	private MultipleOutputs<NullWritable, Text> mos;

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setup(Context context) throws IOException, InterruptedException{
		mos = new MultipleOutputs(context);
	}
	
	@Override
	public void reduce(NullWritable key, Iterable<Text> values, Context context) 
			throws IOException, InterruptedException{
		for (Text text : values) {
//			mos.write(key, text, "abc/");
			context.write(key, text);
			mos.write(NullWritable.get(), text, "a/");
			mos.write(NullWritable.get(), text, "b/");
			mos.write(NullWritable.get(), text, "c/");
		}
	}
	
	@Override
	public void cleanup(Context context) throws IOException, InterruptedException{
		mos.close();
	}
}
