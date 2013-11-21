package com.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisUtils {
	private static final String CONFIG_PATH = "/config/redis.properties";
	private static String env;
	private static String host;
	private static int port;
	private static JedisPoolConfig conf = null;
	private static Properties prop;
	private static JedisPool pool;
	
	static {
		prop = ConfigUtils.getConfig(CONFIG_PATH);
		env = prop.getProperty("ENV", "");
	}

	private static void initPool() {
		conf = new JedisPoolConfig();
		conf.setMaxActive(1000);
		conf.setMaxIdle(500);
		conf.setMaxWait(10000);
		conf.setTestOnBorrow(true);
		host = prop.getProperty(env.concat(".REDIS.HOST"), "");
		port = Integer.parseInt(prop.getProperty(env.concat(".REDIS.PORT"), ""));
		pool = new JedisPool(conf, host, port, 60 * 1000);
	}
	
	private static Jedis getJedis() {
		if (null == pool) {
			initPool();
		}
		return pool.getResource();
	}

	private static void returnJedis(Jedis jedis) {
		if (jedis != null) {
			pool.returnResource(jedis);
		}
	}
   
	public static void closeRedis(Jedis jedis) {
		if (jedis != null) {
			jedis.disconnect();
			pool.destroy();
		}
		conf = null;
	}

	public static void putOneHash2Redis(String tableName,
			String key, String value) {
		Jedis j = getJedis();
		j.hset(tableName, key, value);
		returnJedis(j);
	}

	/**
	 * 批量入 redis
	 * 
	 * @param tableName
	 * @param totalMap
	 */
	public static void putData2Redis(String tableName, Map<String, String> totalMap) {
		Jedis j = getJedis();
		j.hmset(tableName, totalMap);
		returnJedis(j);
	}
	/**
	 * 根据tableName返回对应的map
	 * 
	 * @param tableName
	 * @return
	 */
	public static Map<String, String> getResltMap(String tableName) {
		Jedis j = getJedis();
		Map<String, String> rsMap = j.hgetAll(tableName);
		returnJedis(j);
		return null != rsMap ? rsMap : new HashMap<String, String>();
	}
	
	/**
	 * 根据tableName返回list
	 * @param tableName
	 * @return
	 */
	public static List<String> findList(String tableName){
		Jedis j = getJedis();
		List<String> list = j.lrange(tableName, 0, -1);
		returnJedis(j);
		return null != list ? list : new ArrayList<String>();
	}
	
	public static void delTable(String tableName) {
		Jedis j = getJedis();
		if (j.exists(tableName)) {
			j.del(tableName);
		}
		returnJedis(j);
	}
	
	public static void main(String[] args) {
		
	}
}
