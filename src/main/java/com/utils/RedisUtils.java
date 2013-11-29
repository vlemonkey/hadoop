package com.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.boco.global.Constants;

public final class RedisUtils {
	private static final String VALUE = ".VALUE";

	private static Jedis j = null;
	private static Properties prop = null;
	private static JedisPool pool = null; // Jedis客户端池
	private static Pattern p;
	private static Pattern valueSplit;

	static {
		pool = RedisPool.getJedisPool();
		prop = ConfigUtils.getConfig(Constants.REDIS_PATH);
		p = Constants.configSplit;
		valueSplit = Pattern.compile(prop.getProperty("DILIMITER"));
	}

	/**
	 * 根据tableName返回对应的map
	 * 
	 * @param tableName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> findTableMap(String tableName) {
		j = getJedis();
		Map<String, String> rsMap = j.hgetAll(tableName);
		returnJedis(j);
		return null != rsMap ? rsMap : Collections.EMPTY_MAP;
	}
	
	/**
	 * 自定义返回的value字段
	 * @param tableName
	 * @param dataMap
	 * @param columns
	 * @return
	 */
	public static Map<String, String> findTableMapByCustValue(String tableName, String... columns){
		Map<String,	String> dataMap = findTableMap(tableName);
		Map<String, Integer> indexMap = getIndexMap(tableName); // value字段的indexMap
		int[] valueIndex = getCustIndex(indexMap, columns);
		Map<String, String> resultMap = new HashMap<String, String>();
		String[] strTemps = null;
		for(Entry<String, String> entry : dataMap.entrySet()) {
			strTemps = valueSplit.split(entry.getValue(), -1);
			resultMap.put(entry.getKey(), getDatas(strTemps, valueIndex, prop.getProperty("DILIMITER")));
		}
		
		return resultMap;
	}
	
	/**
	 * 自定义返回的value字段
	 * @param tableName
	 * @param dataMap
	 * @param columns
	 * @return
	 */
	public static Map<String, String> findTableMapByCustValue(String tableName, Map<String, String> dataMap, String... columns){
		Map<String, Integer> indexMap = getIndexMap(tableName); // value字段的indexMap
		int[] valueIndex = getCustIndex(indexMap, columns);
		Map<String, String> resultMap = new HashMap<String, String>();
		String[] strTemps = null;
		for(Entry<String, String> entry : dataMap.entrySet()) {
			strTemps = valueSplit.split(entry.getValue(), -1);
			resultMap.put(entry.getKey(), getDatas(strTemps, valueIndex, prop.getProperty("DILIMITER")));
		}
		
		return resultMap;
	}
	
	/**
	 * 插入key value
	 * @param key
	 * @param value
	 */
	public static void addData(String key, String value) {
		j = getJedis();
		j.set(key, value);
		returnJedis(j);
	}
	
	/**
	 * 返回value
	 * @param key
	 * @return
	 */
	public static String getData(String key) {
		j = getJedis();
		String ret = j.get(key);
		returnJedis(j);
		return ret;
	}
	
	/**
	 * 插入hash类型数据
	 * @param tableName
	 * @param key
	 * @param value
	 */
	public static void addHashData(String tableName, String key, String value) {
		j = getJedis();
		j.hset(tableName, key, value);
		returnJedis(j);
	}
	
	/**
	 * 插入hash类型多条数据
	 * @param tableName
	 * @param keyValue
	 */
	public static void addHashData(String tableName, Map<String, String> keyValue) {
		j = getJedis();
		j.hmset(tableName, keyValue);
		returnJedis(j);
	}

	
	/* *******************************自定义函数************************************ */
	
	// 获取该tablename中所有的列索引
	private static Map<String, Integer> getIndexMap(String tableName){
		Map<String, Integer> indexMap = new HashMap<String, Integer>();
		String[] cols = p.split(getValue(tableName), -1);
		for (int i=0, n=cols.length; i<n; i++) {
			indexMap.put(cols[i], i);
		}
		return indexMap;
	}
	
	// 返回索引对应的数据
	private static String getDatas(String[] all, int[] index, String... delimiter) {
		String d = delimiter.length == 0 ? "," : delimiter[0];
		String[] datas = new String[index.length];
		for (int i=0, n=index.length; i<n; i++) {
			if (-1 != index[i]) {
				datas[i] = all[index[i]];
			}else {
				datas[i] = StringUtils.EMPTY;
			}
			
		}
		
		return StringUtils.join(datas, d);
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
			indexes[i] = indexMap.get(columns[i]) != null ? indexMap.get(columns[i]) : -1;
		}
		return indexes;
	}

	/**
	 * 根据key删除表
	 * 
	 * @param key
	 */
	public static void del(String... key) {
		j = getJedis();
		System.out.printf("删除redis：%s\n", Arrays.toString(key));
		j.del(key);
		returnJedis(j);
	}
	
	// 从pool中获取实例
	private static Jedis getJedis() {
		return pool.getResource();
	}
	
	// 还回实例
	private static void returnJedis(Jedis j) {
		pool.returnResource(j);
	}
	
	// 返回tableName.KEY_VALUE
	private static String getValue(String tableName) {
		return prop.getProperty(StringTools.append(tableName, VALUE));
	}
	
	/**
	 * 关闭rediss连接
	 */
	public static void closeRedis() {
		if (j != null) {
			j.disconnect();
		}
		if (pool != null) {
			pool.destroy();
		}
	}
	
	public static void main(String[] args) {
		String tableName = "IMEI";
		
		Map<String, String> map = findTableMap(tableName);
		map = findTableMapByCustValue(tableName, "PRICE", null);
		printMapTopN(map, 3);
		
		closeRedis();
	}
	
	private static void printMapTopN(Map<String, String> map, int count) {
		for (Entry<String, String> entry : map.entrySet()) {
			if (count-- > 0) {
				System.out.println(entry);
			}else {
				break;
			}
		}
	}
}
