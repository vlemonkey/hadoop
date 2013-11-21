package com.fish.global;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Properties;

import org.apache.hadoop.mapreduce.Counters;

import com.utils.ConfigUtils;
import com.utils.JDBCUtils;

public class CounterUtils {
	private static Properties prop = ConfigUtils.getConfig("/config/sql.properties");
	
	/**
	 * 返回错误文件夹名字+枚举名
	 * @param en
	 * @return
	 */
	public static String getErrorDirectory(COUNTER c) {
		return "error-data/".concat(c.name());
	}
	
	public static void insert2Mysql(int fkId, String moduleName, Counters counters) {
		String sql = prop.getProperty("INSERT_CHECK_DETAIL");
		PreparedStatement stmt = null;
		Connection conn = null;
		try {
			conn = JDBCUtils.getInstance("mysql");
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, fkId);
			stmt.setString(2, moduleName);
			stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
			int i = 4;
			for (Enum<COUNTER> c : COUNTER.values()) {
				long value = counters.findCounter(c).getValue();
				stmt.setLong(i++, value);
			}
			
			stmt.executeUpdate();
			
		} catch (Exception e) {
			System.err.println("connection error");
			e.printStackTrace();
		}finally {
			JDBCUtils.closePreparedStatement(stmt);
			JDBCUtils.closeConnection(conn);
		}
	}
	
	public static void main(String[] args) {
		String sql = prop.getProperty("INSERT_CHECK_DETAIL");
		PreparedStatement stmt = null;
		Connection conn = null;
		try {
			conn = JDBCUtils.getInstance("mysql");
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, 123);
			stmt.setString(2, "ETL");
			stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
			
			long value = 16l;
			stmt.setLong(4, value);
			stmt.setLong(5, value);
			stmt.setLong(6, value);
			stmt.setLong(7, value);
			stmt.setLong(8, value);
			stmt.setLong(9, value);
			stmt.setLong(10, value);
			
			stmt.executeUpdate();
			
		} catch (Exception e) {
			System.err.println("connection error");
			e.printStackTrace();
		}finally {
			JDBCUtils.closePreparedStatement(stmt);
			JDBCUtils.closeConnection(conn);
		}
	}
}
