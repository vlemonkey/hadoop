package com.boco.global;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.Job;

import com.utils.ConfigUtils;
import com.utils.JDBCUtils;

public class CounterUtils {
	
	/**
	 * 将counter信息保存到数据库中 
	 * @param moduleName 自定义模块名称
	 * @param job
	 */
	public static void insert2Mysql(String moduleName, Job job) {
		Properties prop = ConfigUtils.getConfig(Constants.SQL_PATH);
		String sql = prop.getProperty("INSERT_CHECK_DETAIL");
		Counters counters = null;
		try {
			counters = job.getCounters();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		if (null == counters) {
			return;
		}
		Configuration conf = job.getConfiguration();
		
		PreparedStatement stmt = null;
		Connection conn = null;
		long l = System.currentTimeMillis();
		long costTime = l - conf.getLong(Constants.COST_TIME, 0);
		try {
			conn = JDBCUtils.getInstance("mysql");
			stmt = conn.prepareStatement(sql);
			stmt.setLong(1, conf.getLong(Constants.FKID, 0));
			stmt.setString(2, moduleName);
			stmt.setTimestamp(3, new Timestamp(l));
			int i = 4;
			for (Enum<COUNTER> c : COUNTER.values()) {
				long value = counters.findCounter(c).getValue();
				stmt.setLong(i++, value);
			}
			stmt.setLong(i, costTime);
			
			stmt.executeUpdate();
			
		} catch (Exception e) {
			System.err.println("connection error");
			e.printStackTrace();
		}finally {
			JDBCUtils.closePreparedStatement(stmt);
			JDBCUtils.closeConnection(conn);
			sql = null; conf = null; counters = null;
		}
	}
	
	/**
	 * 返回错误文件夹名字+枚举名
	 * @param en
	 * @return
	 */
	public static String getErrorDirectory(COUNTER c) {
		return "error-data/".concat(c.name());
	}
	
	public static void main(String[] args) {
		
	}
}
