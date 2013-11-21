package com.fish.verify;

import java.io.IOException;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

import com.fish.global.COUNTER;

public class ModelReducer extends Reducer<Text, Text, Text, Text>{
	
	private static MultipleOutputs<NullWritable, Text> mos;

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setup(Context context) throws IOException, InterruptedException{
		mos = new MultipleOutputs(context);
	}
	
	@Override
	public void reduce(Text key, Iterable<Text> values, Context context) 
			throws IOException, InterruptedException{
		context.getCounter(COUNTER.ReducerOutput).increment(1);
		for (Text text : values) {
			mos.write(NullWritable.get(), text, key.toString() + "/");
		}
	}
	
	@Override
	public void cleanup(Context context) throws IOException, InterruptedException{
		mos.close();
	}
}
