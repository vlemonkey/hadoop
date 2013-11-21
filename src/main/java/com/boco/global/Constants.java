package com.boco.global;

import java.util.regex.Pattern;

public class Constants {

	public static final String DEFAULT_DELIMITER = ","; // 默认分割符
	public static Pattern configSplit = Pattern.compile(DEFAULT_DELIMITER); // 默认切分
	
	public static final String FKID = "FKID";  // 数据稽查外键
	public static final String COST_TIME = "COST_TIME"; // 耗时
	
	// 容量调度器名称
	public static final String QUEUE_DEFAULT = "default";
	public static final String QUEUE_MIA = "mia";
	public static final String QUEUE_WY = "wy";
}
