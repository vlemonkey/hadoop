package com.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import com.boco.global.Constants;


public class JDBCUtils {

	private static Properties prop = ConfigUtils.getConfig(Constants.JDBC_PATH);

	private static String VAR = "#1.#2";
	private static String PRE;
	private static String DRIVER;
	private static String URL;
	private static String USERNAME;
	private static String PASSWORD;

	private JDBCUtils() {
	}

	/**
	 * 根据数据类型返回connections
	 * @param jdbcType
	 * @return
	 */
	public static Connection getInstance(String jdbcType) {
		PRE = jdbcType;
		DRIVER = getParam("driverClassName");
		URL = getParam("url");
		USERNAME = getParam("username");
		PASSWORD = getParam("password");
		
		Connection conn = null;

		try {
			Class.forName(DRIVER);
			conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	/**
	 * 获取配置文件中的JDBC连接
	 * 
	 * @param pre
	 * @param str
	 * @return
	 */
	private static String getParam(String str) {
		String varString = VAR.replace("#1", PRE).replace("#2", str);
		String s = prop.getProperty(varString, "");
		varString = null;
		return s;
	}
	
	
	/**
	 * 关闭PreparedStatement
	 * @param stmt
	 */
	public static void closeResultSet(ResultSet rs) {
		if (null != rs) {
			try {
				rs.close();
			} catch (Exception e) {
				System.err.println("close ResultSet error");
			}
		}
	}
	
	/**
	 * 关闭PreparedStatement
	 * @param stmt
	 */
	public static void closePreparedStatement(PreparedStatement stmt) {
		if (null != stmt) {
			try {
				stmt.close();
			} catch (Exception e) {
				System.err.println("close PreparedStatement error");
			}
		}
	}
	
	/**
	 * 关闭PreparedStatement
	 * @param stmt
	 */
	public static void closeConnection(Connection conn) {
		if (null != conn) {
			try {
				conn.close();
			} catch (Exception e) {
				System.err.println("close Connection error");
			}
		}
	}
	
	public static void main(String[] args) {
		Connection conn = getInstance("mysql");
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.prepareStatement("select count(*) from CLASSIFY");
			rs = stmt.executeQuery();

			while (rs.next()) {
				System.out.println(rs.getInt(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResultSet(rs);
			closePreparedStatement(null);
			closeConnection(conn);
		}
	}
	
}
