package com.boco.global;

import java.util.regex.Pattern;

public class Constants {

	public static final String DEFAULT_DELIMITER = ","; // 默认分割符
	public static Pattern configSplit = Pattern.compile(DEFAULT_DELIMITER); // 默认切分
	
	public static final String FKID = "FKID";  // 数据稽查外键
	public static final String COST_TIME = "COST_TIME"; // 耗时
	
	// config配置文件在distributedcache上hdfs路径
	public static final String DISTRIBUTEDCACHE_PATH = "hadoop/distributedcache/config";
}
