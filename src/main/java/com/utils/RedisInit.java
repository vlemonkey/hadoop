package com.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import com.boco.global.Constants;

public class RedisInit {
	
	private static String ALIAS = ".ALIAS";
	private static String COLUMNS = ".COLUMNS";
	private static String KEY = ".KEY";
	private static String VALUE = ".VALUE";
	
	private static String CONFIG_PATH = "/config/redis_meta_data.properties";
	private static Properties prop;
	private static Pattern splitPattern;
	private static String delimiter;

	private static Jedis j = null;
	private static String[] INIT_TABLES; // 初始化tables
	private static String filesPath; // 数据文件路径
	private static String filesTailName; // 数据文件类型

	
	// 初始化redis
	private static void init(String... args) {
		prop = ConfigUtils.getConfig(CONFIG_PATH);
		splitPattern = Constants.configSplit;
		delimiter = prop.getProperty("DILIMITER", ",");

		filesPath = prop.getProperty("INIT_DATA_PATH");
		filesPath = prop.getProperty("INIT_DATA_PATH");
		filesTailName = prop.getProperty("INIT_FILE_TAIL_NAME");
		INIT_TABLES = splitPattern.split(prop.getProperty("REDIS_INIT_TABLES"), -1);
		
		Properties redisProp = ConfigUtils.getConfig("/config/jdbc.properties");
		String host = redisProp.getProperty("REDIS.HOST");
		int port = Integer.parseInt(redisProp.getProperty("REDIS.PORT")),
			timeout = Integer.parseInt(redisProp.getProperty("REDIS.TIMEOUT"));
		switch(args.length) {
			case 4:
				timeout = Integer.parseInt(args[3]);
			case 3:
				port = Integer.parseInt(args[2]);
			case 2:
				host = args[1];
			case 1:
				filesPath = args[0];
			default:
				break;
		}
		j = new Jedis(host, port, timeout);
	}

	// 初始化一个表的数据
	private static void initTable(String tableName) {
		File file = new File(getFilePath(tableName));
		if (file.exists()) {
			System.out.printf("import tableName:%s\n", tableName);
			System.out.println(file.getAbsolutePath());
			int count = 0; // 计数
			// 如果表已经存在 则删除，重新导入
			if (j.exists(tableName)) {
				j.del(tableName);
			}

			BufferedReader reader = null;
			String strTemp = null;
			
			Map<String, Integer> indexMap = getIndexMap(tableName);
			int[] keyIndex = getKeyIndex(indexMap, tableName);
			int[] valueIndex = getValueIndex(indexMap, tableName);
			String[] CSV_DATAS;
			
			long l = System.currentTimeMillis();
			try {
				Pipeline pipe = j.pipelined();
				reader = new BufferedReader(new FileReader(file));
				while (null != (strTemp = reader.readLine())) {
					CSV_DATAS = splitPattern.split(strTemp, -1);
					pipe.hset(tableName, getDatas(CSV_DATAS, keyIndex),
							getDatas(CSV_DATAS, valueIndex, delimiter));
					count++;
				}
				pipe.sync();
			} catch (Exception e) {
				System.err.printf("read file error:%s\n", getFilePath(tableName));
				printGenericCommandUsage(System.out);
				e.printStackTrace();
			} finally {
				System.out.printf("total:%d\ncost:%d\n\n", count, System.currentTimeMillis() - l);
			}
		}else {
			System.err.printf("error path:%s\n", getFilePath(tableName));
			printGenericCommandUsage(System.out);
		}
	}
	
	/*
	 * 初始化装载选定的表文件
	 */
	public static void initAll() {
		for (String table : INIT_TABLES) {
			initTable(table);
		}
	}
	
	// 返回索引对应的数据
	private static String getDatas(String[] all, int[] index, String... delimiter) {
		String d = delimiter.length == 0 ? "," : delimiter[0];
		String[] datas = new String[index.length];
		for (int i=0, n=index.length; i<n; i++) {
			datas[i] = all[index[i]];
		}
		
		return StringUtils.join(datas, d);
	}
	
	/**
	 * 获取key对应的索引
	 * @param indexMap
	 * @param tableName
	 * @return
	 */
	private static int[] getKeyIndex(Map<String, Integer> indexMap, String tableName) {
		String[] keys = splitPattern.split(getKey(tableName), -1);
		return getCustIndex(indexMap, keys);
	}
	
	/**
	 * 获取value对应的索引
	 * @param indexMap
	 * @param tableName
	 * @return
	 */
	private static int[] getValueIndex(Map<String, Integer> indexMap, String tableName) {
		String[] values = splitPattern.split(getValue(tableName), -1);
		return getCustIndex(indexMap, values);
	}
	
	/**
	 * 获取自定义字段的索引
	 * @param indexMap
	 * @param columns
	 * @return
	 */
	private static int[] getCustIndex(Map<String, Integer> indexMap, String... columns) {
		if (columns.length == 0) {
			return null;
		}
		
		int[] indexes = new int[columns.length];
		for (int i=0, n=columns.length; i<n; i++) {
			indexes[i] = indexMap.get(columns[i]);
		}
		return indexes;
	}

	// 获取该tablename中所有的列索引
	private static Map<String, Integer> getIndexMap(String tableName){
		Map<String, Integer> indexMap = new HashMap<String, Integer>();
		String[] cols = splitPattern.split(getColumns(tableName), -1);
		for (int i=0, n=cols.length; i<n; i++) {
			indexMap.put(cols[i], i);
		}
		return indexMap;
	}

	// 返回tableName.ALIAS
	private static String getAlias(String tableName) {
		return prop.getProperty(StringTools.append(tableName, ALIAS));
	}

	// 返回tableName.COLUMNS
	private static String getColumns(String tableName) {
		return prop.getProperty(StringTools.append(tableName, COLUMNS));
	}

	// 返回tableName.KEY_VALUE
	private static String getKey(String tableName) {
		return prop.getProperty(StringTools.append(tableName, KEY));
	}
	
	// 返回tableName.KEY_VALUE
	private static String getValue(String tableName) {
		return prop.getProperty(StringTools.append(tableName, VALUE));
	}

	// 根据tableName返回数据文件全路径
	private static String getFilePath(String tableName) {
		return StringTools.append(filesPath, "/", getAlias(tableName), ".",filesTailName);
	}
	
	// 输出提示信息
	private static void printGenericCommandUsage(PrintStream out) {
		out.println("\n--------------------------------------------");
	    out.println("Generic command supported are\n");
	    out.println("hadoop jar xxx.jar com.utils.RedisInit");
	    out.println("hadoop jar xxx.jar com.utils.RedisInit /home/boco/INI");
	    out.println("hadoop jar xxx.jar com.utils.RedisInit /home/boco/INI 10.0.7.215");
	    out.println("hadoop jar xxx.jar com.utils.RedisInit /home/boco/INI 10.0.7.215 6379");
	    out.println("hadoop jar xxx.jar com.utils.RedisInit /home/boco/INI 10.0.7.215 6379 60000\n");
	    out.println("args[0]	cvs file folder");
	    out.println("args[1]	redis host IP");
	    out.println("args[2]	redis port");
	    out.println("args[3]	redis timeout");
	    out.println("--------------------------------------------\n");
	    IOUtils.closeQuietly(out);
	  }
	
	// 初始化数据
	// 
	public static void main(String[] args) {
		init(args);
		initAll();
		j = null;
	}
}
