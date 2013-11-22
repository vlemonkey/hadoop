package com.boco.global;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileAsTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.LazyOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;

public class GlobalTools {
	
	/**
	 * 设置数据稽查参数
	 * @param job
	 * @param FKID
	 */
	public static void setDataExaminer(Job job, String FKID) {
		job.getConfiguration().setLong(Constants.FKID, Long.parseLong(FKID));
		job.getConfiguration().setLong(Constants.COST_TIME, System.currentTimeMillis());
	}
	
	/**
	 * 初始化Job
	 * @param conf
	 * @param cls
	 * @return
	 * @throws Exception
	 */
	public static Job initJob(Configuration conf, Class<?> cls) 
		throws Exception{
		Job job = new Job(conf);
		job.setJarByClass(cls);
		job.setJobName(cls.getName());
		initSequenceJob(job);

		return job;
	}
	
	/**
	 * 初始化Job
	 * @param conf
	 * @param cls
	 * @return
	 * @throws Exception
	 */
	public static Job initJobWithoutInput(Configuration conf, Class<?> cls) 
		throws Exception{
		Job job = new Job(conf);
		job.setJarByClass(cls);
		job.setJobName(cls.getName());
		initSequenceJobWithoutInput(job);

		return job;
	}
	
	/**
	 * 初始化Job
	 * @param conf
	 * @param cls
	 * @return
	 * @throws Exception
	 */
	public static Job initJob(Configuration conf, Class<?> cls,
			Class<? extends CompressionCodec> codecClass) 
		throws Exception{
		Job job = new Job(conf);
		job.setJarByClass(cls);
		job.setJobName(cls.getName());
		initSequenceJob(job, codecClass);

		return job;
	}
	
	/**
	 * 初始化Job
	 * @param conf
	 * @param cls
	 * @return
	 * @throws Exception
	 */
	public static Job initJobWithoutInput(Configuration conf, Class<?> cls,
			Class<? extends CompressionCodec> codecClass) 
		throws Exception{
		Job job = new Job(conf);
		job.setJarByClass(cls);
		job.setJobName(cls.getName());
		initSequenceJobWithoutInput(job, codecClass);

		return job;
	}

	/**
	 * 初始化sequencefile job
	 * 带input和output
	 * @param job
	 */
	private static void initSequenceJob(Job job){
		job.setInputFormatClass(SequenceFileAsTextInputFormat.class);
		setSequenceOutput(job);
	}
	
	/**
	 * 初始化sequencefile job
	 * 带input和output
	 * 自定义压缩方式
	 * @param job
	 * @param codecClass
	 */
	private static void initSequenceJob(Job job, Class<? extends CompressionCodec> codecClass){
		job.setInputFormatClass(SequenceFileAsTextInputFormat.class);
		setSequenceOutput(job, codecClass);
	}
	
	/**
	 * 初始化sequencefile job
	 * 只有output
	 * @param job
	 */
	private static void initSequenceJobWithoutInput(Job job){
		setSequenceOutput(job);
	}
	
	/**
	 * 初始化sequencefile job
	 * 只有output
	 * 自定义压缩方式
	 * @param job
	 */
	private static void initSequenceJobWithoutInput(Job job, Class<? extends CompressionCodec> codecClass){
		setSequenceOutput(job, codecClass);
	}
	
	/**
	 * 初始化sequencefile job
	 * 只有output
	 * @param job
	 */
	private static void setSequenceOutput(Job job) {
		job.setOutputFormatClass(SequenceFileOutputFormat.class);
		SequenceFileOutputFormat.setCompressOutput(job, true);
		SequenceFileOutputFormat.setOutputCompressorClass(job, GzipCodec.class);
		SequenceFileOutputFormat.setOutputCompressionType(job, CompressionType.BLOCK);
		LazyOutputFormat.setOutputFormatClass(job, SequenceFileOutputFormat.class);
	}
	
	/**
	 * 初始化sequencefile job
	 * 只有output
	 * 自定义压缩方式
	 * @param job
	 */
	private static void setSequenceOutput(Job job, Class<? extends CompressionCodec> codecClass) {
		job.setOutputFormatClass(SequenceFileOutputFormat.class);
		SequenceFileOutputFormat.setCompressOutput(job, true);
		SequenceFileOutputFormat.setOutputCompressorClass(job, codecClass);
		SequenceFileOutputFormat.setOutputCompressionType(job, CompressionType.BLOCK);
		LazyOutputFormat.setOutputFormatClass(job, SequenceFileOutputFormat.class);
	}
}
