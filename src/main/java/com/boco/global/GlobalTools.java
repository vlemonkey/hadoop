package com.boco.global;

import java.net.URI;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;

import com.utils.ConfigUtils;

public class GlobalTools {
	
	/**
	 * 初始化Job
	 * @param conf
	 * @param cls
	 * @return
	 * @throws Exception
	 */
	public static Job initJob(Configuration conf, Class<?> cls) 
		throws Exception{
		//建立一个软连接
		DistributedCache.createSymlink(conf);
		DistributedCache.addCacheArchive(new URI(Constants.DISTRIBUTEDCACHE_URI), conf);
		
		// 声明job
		Job job = new Job(conf);
		job.setJarByClass(cls);
		job.setJobName(cls.getName());

		return job;
	}
	
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
	 * 设置input， output
	 * @param job
	 * @param intputFormat
	 * @param outputFormat
	 */
	@SuppressWarnings("rawtypes")
	public static void setInputOutputFormat(Job job, Class<? extends FileInputFormat> intputFormat,
			Class<? extends FileOutputFormat> outputFormat) {
		if (intputFormat != null) {
			job.setInputFormatClass(intputFormat);
		}
		
		if (outputFormat != null) {
			if (outputFormat == SequenceFileOutputFormat.class) {
				setSequenceOutput(job);
			}else {
				job.setOutputFormatClass(outputFormat);
			}
			
		}
	}
	
	/**
	 * 初始化sequencefile job
	 * 只有output
	 * @param job
	 */
	@SuppressWarnings("unchecked")
	public static void setSequenceOutput(Job job) {
		Class<? extends CompressionCodec> compressClass = null;
		try {
			compressClass = (Class<? extends CompressionCodec>)Class.forName(Constants.COMPRESS_CLASS);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		job.setOutputFormatClass(SequenceFileOutputFormat.class);
		SequenceFileOutputFormat.setCompressOutput(job, true);
		SequenceFileOutputFormat.setOutputCompressorClass(job, compressClass);
		SequenceFileOutputFormat.setOutputCompressionType(job, CompressionType.BLOCK);
	}
	
	/**
	 * 是否开启调试模式
	 * @return
	 */
	public static boolean isDebug() {
		return StringUtils.equals("ON", ConfigUtils.getGlobalValue("DEBUG"));
	}
}
