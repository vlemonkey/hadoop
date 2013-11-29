package com.utils;

import java.util.Properties;

import com.boco.global.Constants;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPool {
	
	public static JedisPool pool = null; // Jedis客户端池
	public static Properties prop = ConfigUtils.getConfig(Constants.JDBC_PATH);

	static {
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
	
	private RedisPool() {
	}
	
	public static JedisPool getJedisPool() {
		return pool;
	}
	
	public static Properties getProp() {
		return prop;
	}
	
	public static void main(String[] args) {
		JedisPool pool = getJedisPool();
		Jedis j = pool.getResource();
		j.set("a", "c");
		System.out.println(j.get("a"));
		j.del("a");
		System.out.println(j.get("a"));
		pool.returnResource(j);
	}
}
