package com.fish.test.test;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.jobcontrol.ControlledJob;
import org.apache.hadoop.mapreduce.lib.jobcontrol.JobControl;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class ModelDriver extends Configured implements Tool{
	
	@Override
	public int run(String[] args) throws Exception {
		
		FileSystem fs = FileSystem.get(getConf());
		fs.delete(new Path(args[1]), true);
		
		Configuration conf = new Configuration();
		Job job = new Job(conf, "1");
		job.setJarByClass(ModelDriver.class);
		job.setJobName("1");
		
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		job.setInputFormatClass(SplitInputFormat.class);
		
		job.setMapperClass(ModelMapper.class);
		job.setCombinerClass(ModelReducer.class);
		job.setReducerClass(ModelReducer.class);
		job.setNumReduceTasks(6);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(LongWritable.class);
		
		/* *********************************************/
		
		Configuration conf2 = new Configuration();
		Job job2 = new Job(conf2, "2");
		job2.setJarByClass(ModelDriver.class);
		job2.setJobName("2");
		
		FileInputFormat.setInputPaths(job2, new Path(args[1]));
		FileOutputFormat.setOutputPath(job2, new Path(args[2]));
		
		job2.setInputFormatClass(SplitInputFormat.class);
		
		job2.setMapperClass(TestMapper.class);
		job2.setCombinerClass(TestReducer.class);
		job2.setReducerClass(TestReducer.class);
		job2.setNumReduceTasks(6);
		
		job2.setMapOutputKeyClass(Text.class);
		job2.setMapOutputValueClass(LongWritable.class);
		
		JobControl jobControl = new JobControl("FindTopKIP");  
        ControlledJob cJob1 = new ControlledJob(conf);  
        cJob1.setJob(job);  
        ControlledJob cJob2 = new ControlledJob(conf2);  
        cJob2.setJob(job2);  
        jobControl.addJob(cJob1);  
        jobControl.addJob(cJob2);  
        cJob2.addDependingJob(cJob1);  
        jobControl.run();  
        
		System.out.println("fuck finally!");
		
		
		return 0;
	}
	
	public static void main(String[] args) throws Exception{
		int ret = ToolRunner.run(new ModelDriver(), args);
		System.exit(ret);
	}
	
}
