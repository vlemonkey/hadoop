package com.fish.test.sequence;

import java.io.IOException;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import com.utils.StringTools;

public class ModelMapper extends Mapper<Object, Text, NullWritable, Text>{
	
	Text tk = new Text();
	Text tv = new Text();
	
	public static String TEST = StringTools.append("mapper test");
	
	@Override
	public void map(Object key, Text value, Context context)
		throws IOException, InterruptedException{
//		tk.set("a");
		context.write(NullWritable.get(), value);
//		tk.set("b");
//		context.write(tk, value);
//		tk.set("c");
//		context.write(tk, value);
//		tk.set("d");
//		context.write(tk, value);
	}
	
	@Override
	public void setup(Context context) throws IOException, InterruptedException{

	}
	
	@Override
	public void cleanup(Context context) throws IOException, InterruptedException{
	
	}
}
