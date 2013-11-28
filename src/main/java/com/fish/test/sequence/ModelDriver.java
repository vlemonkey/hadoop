package com.fish.test.sequence;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import com.boco.global.GlobalTools;

public class ModelDriver extends Configured implements Tool{
	
	@Override
	public int run(String[] args) throws Exception {
		FileSystem fs = FileSystem.get(getConf());
		fs.delete(new Path(args[1]), true);
		
		// 初始化job 不包括inputformat
		// getclass 当前类，用来设置job.setJarByClass()、job.setJobName()
		// outputformat sequencefile
		Job job = GlobalTools.initJob(getConf(), getClass());
		GlobalTools.setSequenceOutput(job);
		
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		job.setMapperClass(ModelMapper.class);
//		job.setReducerClass(IdentityReducer.class);
		job.setReducerClass(ModelReducer.class);
		job.setNumReduceTasks(10);
		
		job.setOutputKeyClass(NullWritable.class);
		job.setOutputValueClass(Text.class);
		
		boolean success = job.waitForCompletion(true);
//		boolean success = false;
		return success ? 0 : 1;
	}
	
	public static void main(String[] args) throws Exception{
		int ret = ToolRunner.run(new ModelDriver(), args);
		System.exit(ret);
	}
	
}
