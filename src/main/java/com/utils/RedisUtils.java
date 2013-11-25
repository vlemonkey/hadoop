package com.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.Text;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

public final class RedisUtils {
	private static final String COLUMNS = ".COLUMNS";
	private static final String KEY_VALUE = ".KEY_VALUE";

	private static Jedis j = null;
	private static Properties prop = null;
	private static JedisPool pool = null; // Jedis客户端池
	private final static Pattern p = Pattern.compile(","); // 逗号分割

	static {
		pool = RedisPool.getJedisPool();
		prop = ConfigUtils.getConfig("/config/redis_meta_data.properties");
	}

	/**
	 * 根据tableName返回对应的map
	 * 
	 * @param tableName
	 * @return
	 */
	public static Map<String, String> findCode2CodeIDMap(String tableName) {
		j = getJedis();
		Map<String, String> rsMap = j.hgetAll(tableName);
		returnJedis(j);
		return null != rsMap ? rsMap : new HashMap<String, String>();
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

	/**
	 * 根据配置文件设置的key_value 返回默认的新组建map
	 * 
	 * @param tableName
	 * @return
	 */
	public static Map<String, String> findKeyValueMapByTable(String tableName) {
		int[] keyValueIndex = getDefaultKeyValueIndex(tableName);
		return findKeyValueMapByTable(tableName, keyValueIndex[0],
				keyValueIndex[1]);
	}

	/**
	 * 适用于某一个表的不同字段重用
	 * 
	 * @param tabMap
	 * @param keyColumnName
	 * @param valueColumnName
	 * @param tableName
	 * @return
	 */
	public static Map<String, String> findMapRepeat(Map<String, String> tabMap,
			String tableName, String keyColumnName, String valueColumnName) {

		// Map<String, String> map = new HashMap<String, String>();
		Map<String, String> rsMap = new HashMap<String, String>();
		// map = tabMap;

		String[] datas; // 保存临时分组数据
		String column = prop.getProperty(getColumns(tableName));
		List<String> list = Arrays.asList(p.split(column));
		int keyIndex = list.indexOf(keyColumnName);
		int valueIndex = list.indexOf(valueColumnName);
		for (Entry<String, String> entry : tabMap.entrySet()) {
			datas = p.split(entry.getValue() + ", ");
			rsMap.put(datas[keyIndex], datas[valueIndex]);
		}

		return rsMap;

	}

	/**
	 * 根据tableName和key索引、value索引 返回新组建的Map
	 * 
	 * @param tableName
	 * @param keyIndex
	 * @param valueIndex
	 * @return
	 */
	public static Map<String, String> findKeyValueMapByTable(String tableName,
			int keyIndex, int valueIndex) {
		Map<String, String> tempMap = new HashMap<String, String>();
		tempMap = findCode2CodeIDMap(tableName);
		Map<String, String> rsMap = new HashMap<String, String>();
		if (!tempMap.isEmpty()) {
			String[] datas; // 保存临时分组数据
			for (Entry<String, String> entry : tempMap.entrySet()) {
				datas = p.split(entry.getValue() + ", ");
				rsMap.put(datas[keyIndex], datas[valueIndex]);
			}

			tempMap = null;
			datas = null;
		}
		return rsMap;
	}

	/**
	 * 根据tableName和key字段名和value字段名 返回新组建的Map
	 * 
	 * @param tableName
	 * @param keyColumnName
	 * @param valueColumnName
	 * @return
	 */
	public static Map<String, String> findKeyValueMapByTable(String tableName,
			String keyColumnName, String valueColumnName) {
		String column = prop.getProperty(getColumns(tableName));
		List<String> list = Arrays.asList(p.split(column));
		int keyIndex = list.indexOf(keyColumnName);
		int valueIndex = list.indexOf(valueColumnName);
		if (keyIndex > -1 && valueIndex > -1) {
			return findKeyValueMapByTable(tableName, keyIndex, valueIndex);
		}

		return new HashMap<String, String>();
	}
	/**
	 * 根据tableName value的索引和key的索引数组 返回新组建的map
	 * 
	 * @param tableName
	 * @param valueIndex
	 * @param keyIndexes
	 * @return
	 */
	public static Map<String, String> findKeyValuesMapByTable(String tableName,
			int keyIndex, String delimiter, int... valueIndexes) {
		Map<String, String> tempMap = new HashMap<String, String>();
		tempMap = findCode2CodeIDMap(tableName);
		Map<String, String> rsMap = new HashMap<String, String>();
		if (!tempMap.isEmpty()) {
			String[] datas; // 保存临时分组数据
			List<String> tempValueList;
			for (Entry<String, String> entry : tempMap.entrySet()) {
				datas = p.split(entry.getValue() + ", ");
				tempValueList = new ArrayList<String>();
				for (int index : valueIndexes) {
					if (-1 != index) {
						tempValueList.add(datas[index]);
					}else {
						tempValueList.add(StringUtils.EMPTY);
					}
				}
				rsMap.put(datas[keyIndex], StringUtils.join(tempValueList, delimiter));
			}
			tempMap = null;
		}
		return rsMap;
	}

	/**
	 * 根据tableName value的字段名和key的字段名数组 返回新组建的map
	 * 
	 * @param tableName
	 * @param valueIndex
	 * @param keyIndexes
	 * @return
	 */
	public static Map<String, String> findKeyValuesMapByTable(String tableName,
			String keyColumnName, String delimiter, String... valueColumnName) {
		String column = prop.getProperty(getColumns(tableName));
		List<String> list = Arrays.asList(p.split(column));
		int keyIndex = list.indexOf(keyColumnName);
		int[] valueIndexes = new int[valueColumnName.length];
		for (int i = 0, n = valueColumnName.length; i < n; i++) {
			int index = list.indexOf(valueColumnName[i]);
			valueIndexes[i] = index;
		}
		if (valueIndexes.length > 0) {
			return findKeyValuesMapByTable(tableName, keyIndex, delimiter, valueIndexes);
		}

		return new HashMap<String, String>();
	}

	/**
	 * 根据tableName value的索引和key的索引数组 返回新组建的map
	 * 
	 * @param tableName
	 * @param valueIndex
	 * @param keyIndexes
	 * @return
	 */
	public static Map<String, String> findKeyValueMapByTable(String tableName,
			int valueIndex, int... keyIndexes) {
		Map<String, String> tempMap = new HashMap<String, String>();
		tempMap = findCode2CodeIDMap(tableName);
		Map<String, String> rsMap = new HashMap<String, String>();
		if (!tempMap.isEmpty()) {
			String[] datas; // 保存临时分组数据
			StringBuilder tempKey;
			for (Entry<String, String> entry : tempMap.entrySet()) {
				datas = p.split(entry.getValue() + ", ");
				tempKey = new StringBuilder();
				for (int index : keyIndexes) {
					tempKey.append(datas[index]);
				}
				rsMap.put(tempKey.toString(), datas[valueIndex]);
			}
			tempMap = null;
		}
		return rsMap;
	}

	/**
	 * 根据tableName value的字段名和key的字段名数组 返回新组建的map
	 * 
	 * @param tableName
	 * @param valueIndex
	 * @param keyIndexes
	 * @return
	 */
	public static Map<String, String> findKeyValueMapByTable(String tableName,
			String valueColumnName, String... keyColumnName) {
		String column = prop.getProperty(getColumns(tableName));
		List<String> list = Arrays.asList(p.split(column));
		int valueIndex = list.indexOf(valueColumnName);
		int[] keyIndexes = new int[keyColumnName.length];
		for (int i = 0, n = keyColumnName.length; i < n; i++) {
			int index = list.indexOf(keyColumnName[i]);
			if (index > -1) {
				keyIndexes[i] = index;
			} else {
				break;
			}
		}
		if (keyIndexes.length > 0) {
			return findKeyValueMapByTable(tableName, valueIndex, keyIndexes);
		}

		return new HashMap<String, String>();
	}

	/**
	 * 根据传入值更新redis
	 * 
	 * @param groupId
	 * @param valuesMap
	 */
	public static void update2Redis(String tableName, Iterable<Text> values) {
		String tempValue;
		int[] keyValueIndex = getDefaultKeyValueIndex(tableName);
		String[] columns = p.split(prop.getProperty(getColumns(tableName)));
		String[] updateDatas;
		Set<String> keySet = new HashSet<String>();
		for (Text value : values) {
			tempValue = value.toString(); // 输入数据肯定是 isnotblank
			keySet.add(tempValue); // 去除重复
		}

		j = getJedis();
		tempValue = j.get(StringTools.append(tableName, "_MAXCODE"));
		int maxNum = StringUtils.isNotBlank(tempValue) ? Integer
				.parseInt(tempValue) : 0;
		Pipeline pipe = j.pipelined();
		for (String key : keySet) {
			updateDatas = new String[columns.length];
			updateDatas[keyValueIndex[0]] = String.valueOf(++maxNum);
			updateDatas[keyValueIndex[1]] = key;
			pipe.hset(tableName, key, StringUtils.join(updateDatas, ","));
		}
		pipe.sync();
		returnJedis(j);
	}

	/**
	 * 将数据放入redis
	 * 
	 * @param tableName
	 * @param totalMap
	 */
	public static void putData2Redis(String tableName,
			Map<String, String> totalMap) {
		j = getJedis();
		j.del(tableName); // 删除原始数据
		Pipeline pipe = j.pipelined();
		j.hmset(tableName, totalMap);
		pipe.sync();
		returnJedis(j);
	}

	/**
	 * 将一条数据放入redis
	 * 
	 * @param tableName
	 * @param totalMap
	 */
	public static void putOneData2Redis(String tableName,
			String key, String value) {
		j = getJedis();
		j.hset(tableName, key, value);
		returnJedis(j);
	}
	
	public static void putOneData2RedisLb(String key, String value) {
		j = getJedis();
		Pipeline pipe = j.pipelined();
		j.set(key, value);
		pipe.sync();
		returnJedis(j);
	}
	
	/**
	 * 根据key删除表
	 * 
	 * @param key
	 */
	public static void del(String... key) {
		j = getJedis();
		System.out.println("删除redis："+key);
		j.del(key);
		returnJedis(j);
	}

	/**
	 * 根据tableName获取配置文件中的默认key_value索引 如果么有，默认索引都为0
	 * 
	 * @param tableName
	 * @return
	 */
	private static int[] getDefaultKeyValueIndex(String tableName) {
		String[] columnDatas = p.split(prop.getProperty(getColumns(tableName)));
		List<String> columnList = Arrays.asList(columnDatas);

		int[] keyValueIndex = { 0, 0 }; // 初始化key和value的索引 默认取第一个元素
		String keyValue = prop.getProperty(getKeyValue(tableName));
		if (keyValue != null) {
			keyValueIndex[0] = columnList.indexOf(StringUtils.substringBefore(
					keyValue, ","));
			keyValueIndex[1] = columnList.indexOf(StringUtils.substringAfter(
					keyValue, ","));
		}
		return keyValueIndex;
	}

	/**
	 * 将需要自编码的表的最大编码缓存到redis中 key--tableName + "_MAXCODE" value--最大编码
	 * 
	 * @param groupMap
	 */
	public static void setMaxAutoCode2Redis(
			Map<String, Map<String, String>> groupMap) {
		String autoCodeTable = prop.getProperty("REDIS_SELF_UPDATE_TABLES", "");
		if (StringUtils.isNotBlank(autoCodeTable)) {
			String[] autoCodeTables = p.split(autoCodeTable);
			j = getJedis();
			Pipeline pipe = j.pipelined();
			for (String string : autoCodeTables) {
				int maxCode = getMaxCodeByMap(groupMap.get(string));
				j.set(StringTools.append(string, "_MAXCODE"),
						String.valueOf(maxCode));
			}
			pipe.sync();
			returnJedis(j);
		}

	}
	
	private static Jedis getJedis() {
		return pool.getResource();
	}
	
	private static void returnJedis(Jedis j) {
		pool.returnResource(j);
	}

	/**
	 * 获取所有需要更新的table
	 * 
	 * @return
	 */
	public static List<String> getUpdateTables() {
		String[] updateTables = p.split(prop.getProperty(
				"REDIS_SELF_UPDATE_TABLES", ""));
		return Arrays.asList(updateTables);
	}

	/**
	 * 根据tableName取得其对应的tableMap
	 * 
	 * @param tableName
	 * @return 返回value中的最大值
	 */
	public static int getMaxCodeByMap(Map<String, String> tableMap) {
		if (null != tableMap && tableMap.values().size() > 0) {
			String strMaxNum = Collections.max(tableMap.values()).toString();
			if (StringUtils.isNotEmpty(strMaxNum)
					&& StringUtils.isNumeric(strMaxNum)) {
				return Integer.parseInt(strMaxNum);
			}
			return 0;
		} else {
			return 0;
		}
	}

	// 返回tableName.COLUMNS
	private static String getColumns(String tableName) {
		return StringTools.append(tableName, COLUMNS);
	}

	// 返回tableName.KEY_VALUE
	private static String getKeyValue(String tableName) {
		return StringTools.append(tableName, KEY_VALUE);
	}
	
	public static void main(String[] args) {
//		Map<String,String> m = RedisUtils.findKeyValuesMapByTable("BUSINESS_DETAIL",0,"\t",0,1,2);
		Map<String,String> m = RedisUtils.findKeyValuesMapByTable("URLHOTACTION",0,",",1);
		for(String s:m.keySet()){
//			if(s.startsWith("1"))
			System.out.println(s+"-->"+m.get(s));
		}
		
		m = RedisUtils.findKeyValuesMapByTable("BUSINESS_DETAIL",0,"\t",0,1,2);
		for(String s:m.keySet()){
//			if(s.startsWith("1"))
			System.out.println(s+"-->"+m.get(s));
		}
	}
}
