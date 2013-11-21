package com.fish.verify;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import com.fish.global.CounterUtils;

public class ModelDriver extends Configured implements Tool{
	
	@Override
	public int run(String[] args) throws Exception {
		if (args.length != 2) {
			System.out.printf(
					"Usage: %s [generic options] <input dir> <output dir>\n",
					getClass().getSimpleName());
			ToolRunner.printGenericCommandUsage(System.out);
			return -1;
		}
		
		FileSystem fs = FileSystem.get(getConf());
		fs.delete(new Path(args[1]), true);
		
		Job job = new Job(getConf());
		job.setJarByClass(ModelDriver.class);
		job.setJobName(getClass().getName());
		
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		
		job.setMapperClass(ModelMapper.class);
		job.setReducerClass(ModelReducer.class);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		

		boolean success = job.waitForCompletion(true);
		if (success) {
			CounterUtils.insert2Mysql(123, "ETL模块", job.getCounters());
			return 0;
		}else {
			return 1;
		}
	}
	
	public static void main(String[] args) throws Exception{
		int ret = ToolRunner.run(new ModelDriver(), args);
		System.exit(ret);
	}
	
}
