package com.fish.urlfilter;

import java.io.IOException;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

public class ModelReducer extends Reducer<Text, Text, Text, Text>{
	
	Text tv = new Text();
	private static MultipleOutputs<NullWritable, Text> mos;

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setup(Context context) throws IOException, InterruptedException{
		mos = new MultipleOutputs(context);
	}
	
	@Override
	public void reduce(Text key, Iterable<Text> values, Context context) 
			throws IOException, InterruptedException{
		String strKey = key.toString();
		if ("error".equals(strKey)) {
			for (Text text : values) {
				mos.write(NullWritable.get(), text, key.toString() + "/");
			}
		}else {
			context.write(key, tv);
		}
	}
	
	@Override
	public void cleanup(Context context) throws IOException, InterruptedException{
		mos.close();
	}
}
