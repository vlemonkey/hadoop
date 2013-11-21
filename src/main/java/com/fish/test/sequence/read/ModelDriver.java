package com.fish.test.sequence.read;

import org.apache.commons.io.output.NullWriter;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileAsTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class ModelDriver extends Configured implements Tool{
	
	@Override
	public int run(String[] args) throws Exception {
		FileSystem fs = FileSystem.get(getConf());
		fs.delete(new Path(args[1]), true);
		
		getConf().set("mapred.max.split.size", args[3]);
		
		Job job = new Job(getConf());
		job.setJarByClass(ModelDriver.class);
		job.setJobName(getClass().getName());
		
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		job.setMapperClass(ModelMapper.class);
		job.setReducerClass(ModelReducer.class);
//		job.setNumReduceTasks(0);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		
		job.setOutputKeyClass(NullWriter.class);
		job.setOutputValueClass(Text.class);
	
//		job.setOutputFormatClass(SequenceFileOutputFormat.class);
		job.setInputFormatClass(SequenceFileAsTextInputFormat.class);
		
		MultipleOutputs.addNamedOutput(job, "text", TextOutputFormat.class, NullWritable.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "sequence", SequenceFileOutputFormat.class, NullWritable.class, Text.class);
		
		boolean success = job.waitForCompletion(true);
		return success ? 0 : 1;
	}
	
	public static void main(String[] args) throws Exception{
		int ret = ToolRunner.run(new ModelDriver(), args);
		System.exit(ret);
	}
	
}
