package com.boco.sort;
/**
 * @author yuhaibao
 * @date ${date}
 * ${tags}
 */
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import com.boco.global.Constants;
import com.boco.global.CounterUtils;

public class OrderModelDriver extends Configured implements Tool{
	
	@Override
	public int run(String[] args) throws Exception {
		
		FileSystem fs = FileSystem.get(getConf());
		fs.delete(new Path(args[2]), true);
		
		Configuration conf = getConf();
		
		Job job = new Job(conf);
		job.setJarByClass(OrderModelDriver.class);
		job.setJobName(getClass().getName());

		FileInputFormat.setInputPaths(job, new Path(args[1]));
		FileOutputFormat.setOutputPath(job, new Path(args[2]));
		
		job.setMapperClass(OrderModelMapper.class);
		job.setReducerClass(OrderModelReducer.class);
		job.setNumReduceTasks(3);
		
		
		job.setMapOutputKeyClass(OrderObject.class);
		job.setMapOutputValueClass(Text.class);
		
		// 分区 key
		job.setPartitionerClass(OrderObject.KeyPartitioner.class);
		
		// 分组 values
		job.setGroupingComparatorClass(OrderObject.FirstComparator.class);
		
		// 设置数据稽查FKID以及执行开始时间
		job.getConfiguration().setLong(Constants.FKID, Long.parseLong(args[0]));
		job.getConfiguration().setLong(Constants.COST_TIME, System.currentTimeMillis());
		boolean success = job.waitForCompletion(true);
		if (success) {
			CounterUtils.insert2Mysql("排序模版 ", job);
		}
		return success ? 0 : 1;
	}
	
	public static void main(String[] args) throws Exception{
		int ret = ToolRunner.run(new OrderModelDriver(), args);
		System.exit(ret);
	}
	
}
