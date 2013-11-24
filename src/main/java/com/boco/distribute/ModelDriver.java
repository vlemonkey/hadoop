package com.boco.distribute;

import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import com.boco.global.CounterUtils;
import com.boco.global.GlobalTools;

public class ModelDriver extends Configured implements Tool{
	
	@Override
	public int run(String[] args) throws Exception {
		
		FileSystem fs = FileSystem.get(getConf());
		fs.delete(new Path(args[2]), true);
		
		Configuration conf = getConf();
		
		//建立一个软连接
		DistributedCache.createSymlink(conf);
		DistributedCache.addCacheArchive(new URI("hadoop/distributedcache/config#config"), conf);
		
		Job job = GlobalTools.initJob(conf, getClass());
		GlobalTools.setSequenceOutput(job);
		GlobalTools.setDataExaminer(job, args[0]);
		
		FileInputFormat.setInputPaths(job, new Path(args[1]));
		FileOutputFormat.setOutputPath(job, new Path(args[2]));
		
		job.setMapperClass(ModelMapper.class);
//		job.setReducerClass(ModelReducer.class);
		job.setNumReduceTasks(0);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		
		boolean success = job.waitForCompletion(true);
		if (success) {
			CounterUtils.insert2Mysql("模块名称", job);
		}
		return success ? 0 : 1;
	}
	
	public static void main(String[] args) throws Exception{
		int ret = ToolRunner.run(new ModelDriver(), args);
		System.exit(ret);
	}
	
}
