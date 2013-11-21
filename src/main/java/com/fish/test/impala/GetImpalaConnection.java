package com.fish.test.impala;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.utils.ConfigUtils;

public class GetImpalaConnection {

	private static Connection conn = null;
	// 配置文件路径
	private static  String PROPERTIES_PATH = "/config/impala.properties";
	private static  String impala_driver;
	private static  String impala_url;
	private static  String impala_username;
	private static  String impala_password;
	// impala 最大返回条数
	public static  int impala_index_num;

	// 创建impala connection
	public Connection getImpalaConn() {
		// 根据配置文件返回数据
		Properties prop = ConfigUtils.getConfig(PROPERTIES_PATH);
		impala_driver = prop.getProperty("impala.driver", "");
		impala_url = prop.getProperty("impala.url", "");
		impala_username = prop.getProperty("impala.username", "");
		impala_password = prop.getProperty("impala.password", "");
		impala_index_num = Integer.valueOf(prop.getProperty("impala.index.num", ""));
		prop = null;
		if (conn == null) {
			try {
				Class.forName(impala_driver);
				System.out.println(impala_driver);
				System.out.println("impala_url:" + impala_url);
				conn = DriverManager.getConnection(impala_url, impala_username,
						impala_password);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				System.exit(1);
			} catch (SQLException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		return conn;
	}

	
}
