package com.fish.test.sequence.read;

import java.io.IOException;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

public class ModelReducer extends Reducer<Text, Text, NullWritable, Text>{
	
	private MultipleOutputs<NullWritable, Text> mos;

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setup(Context context) throws IOException, InterruptedException{
		mos = new MultipleOutputs(context);
	}
	
	@Override
	public void reduce(Text key, Iterable<Text> values, Context context) 
			throws IOException, InterruptedException{
		for (Text text : values) {
			mos.write("text", NullWritable.get(), text,  "reduce/");
			mos.write("sequence", NullWritable.get(), text, "reducesequence/");
		}
	}
	
	@Override
	public void cleanup(Context context) throws IOException, InterruptedException{
		mos.close();
	}
}
