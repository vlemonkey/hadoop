package com.utils;

import java.util.Properties;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPool {
	
	private static final String CONFIG_PATH = "/config/redis_meta_data.properties";

	public static JedisPool pool = null; // Jedis客户端池
	public static Properties prop = ConfigUtils.getConfig(CONFIG_PATH);

	static {
		System.out.println("1");
		String host = prop.getProperty("REDIS.HOST");
		int port = Integer.parseInt(prop.getProperty("REDIS.PORT"));
		int maxActive = Integer.parseInt(prop.getProperty("REDIS.MAXACTIVE", "20"));
		int maxIdle = Integer.parseInt(prop.getProperty("REDIS.MAXIDLE", "5"));
		int maxWait = Integer.parseInt(prop.getProperty("REDIS.MAXWAIT", "1000"));
		int timeout = Integer.parseInt(prop.getProperty("REDIS.TIMEOUT", "60000"));

		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxActive(maxActive);
		config.setMaxIdle(maxIdle);
		config.setMaxWait(maxWait);
		config.setTestOnBorrow(true);
		pool = new JedisPool(config, host, port, timeout);
	}
	
	public static JedisPool getJedisPool() {
		return pool;
	}
	
	public static Properties getProp() {
		return prop;
	}
	
	public static void main(String[] args) {

	}
}
