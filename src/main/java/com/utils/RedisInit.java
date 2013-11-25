package com.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import com.boco.global.Constants;

public class RedisInit {
	
	private static String ALIAS = ".REDIS_ALIAS";
	private static String COLUMNS = ".COLUMNS";
	private static String KEY = ".KEY";
	private static String VALUE = ".VALUE";
	
	private static String CONFIG_PATH = "/config/redis_meta_data.properties";
	private static JedisPool pool = null; // Jedis客户端池
	private static Properties prop;
	private static Pattern splitPattern;
	private static String delimiter;

	private static Jedis j = null;
	private static String[] INIT_TABLES; // 初始化tables
	private static String filesPath; // 数据文件路径
	private static String filesTailName; // 数据文件类型

	static {
		pool = RedisPool.getJedisPool();
		prop = ConfigUtils.getConfig(CONFIG_PATH);
		splitPattern = Constants.configSplit;
		delimiter = prop.getProperty("DILIMITER", ",");

		filesPath = prop.getProperty("INIT_DATA_PATH");
		filesTailName = prop.getProperty("INIT_FILE_TAIL_NAME");
		INIT_TABLES = splitPattern.split(prop.getProperty("REDIS_INIT_TABLES"));
	}

	// 初始化一个表的数据
	private static void initTable(String tableName) {
		File file = new File(getFilePath(tableName));
		if (file.exists()) {
			String redisTableName = getAlias(tableName);
			System.out.printf("import tableName:%s\n", redisTableName);

			int count = 0; // 计数
			j = pool.getResource();
			// 如果表已经存在 则删除，重新导入
			if (j.exists(redisTableName)) {
				j.del(redisTableName);
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
					CSV_DATAS = splitPattern.split(strTemp);
					pipe.hset(redisTableName, getDatas(CSV_DATAS, keyIndex),
							getDatas(CSV_DATAS, valueIndex, delimiter));
					count++;
				}
				pipe.sync();
			} catch (Exception e) {
				System.err.printf("read file error:%s\n", getFilePath(tableName));
				e.printStackTrace();
			} finally {
				pool.returnResource(j);
				System.out.printf("total:%d\ncost:%d\n\n", count, System.currentTimeMillis() - l);
			}
		}else {
			System.err.printf("error path:%s\n", getFilePath(tableName));
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
		String[] keys = splitPattern.split(getKey(tableName));
		int[] keyIndexes = new int[keys.length];
		for (int i=0, n=keys.length; i<n; i++) {
			keyIndexes[i] = indexMap.get(keys[i]);
		}
		keys = null;
		return keyIndexes;
	}
	
	/**
	 * 获取value对应的索引
	 * @param indexMap
	 * @param tableName
	 * @return
	 */
	private static int[] getValueIndex(Map<String, Integer> indexMap, String tableName) {
		String[] values = splitPattern.split(getValue(tableName));
		int[] valueIndexes = new int[values.length];
		for (int i=0, n=values.length; i<n; i++) {
			valueIndexes[i] = indexMap.get(values[i]);
		}
		values = null;
		return valueIndexes;
	}

	// 获取该tablename中所有的列索引
	private static Map<String, Integer> getIndexMap(String tableName){
		Map<String, Integer> indexMap = new HashMap<String, Integer>();
		String[] cols = splitPattern.split(getColumns(tableName));
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
		return StringTools.append(filesPath, "/", tableName, ".",filesTailName);
	}

	// 初始化数据
	public static void main(String[] args) {
		initAll();
	}
}
