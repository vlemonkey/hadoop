package com.boco.global;

import java.util.regex.Pattern;

public class Constants {
	
	
	
	
	/* ********************************************************************* */

	public static final String DEFAULT_DELIMITER = ","; // 默认分割符
	public static final Pattern configSplit = Pattern.compile(DEFAULT_DELIMITER); // 默认切分
	
	public static final String FKID = "FKID";  // 数据稽查外键
	public static final String COST_TIME = "COST_TIME"; // 耗时
	
	// config配置文件在distributedcache上hdfs路径
	public static final String DISTRIBUTEDCAHCHE_CONFIG = "config";
	public static final String DISTRIBUTEDCACHE_URI = 
			"hadoop/distributedcache/config#".concat(DISTRIBUTEDCAHCHE_CONFIG);
	
	// sequencefile 压缩方式
	public static final String COMPRESS_CLASS = "org.apache.hadoop.io.compress.SnappyCodec";
	
	// 全局配置文件路径
	public static final String GLOBAL_PROP = "/config/global.properties";
	public static final String JDBC_PATH = "/config/jdbc.properties";
	public static final String REDIS_PATH = "/config/redis_meta_data.properties";
	public static final String SQL_PATH = "/config/sql.properties";
	
}
