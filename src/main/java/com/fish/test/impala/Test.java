package com.fish.test.impala;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Test {

	public static void main(String[] args) {
		testInsertCount();
	}
	
	public static void testInsertCount() {
		String sql = "insert into wq2 select * from wq2";
		
		String sql2 = "select count(*) from wq2";
		
		Statement stmt = null;
		ResultSet rs = null;
		
		Connection conn = new GetImpalaConnection().getImpalaConn();
		try {
			stmt = conn.createStatement();
			
			stmt.execute(sql);
			Thread.sleep(10000);
			
			
			int count = stmt.getUpdateCount();
			System.out.printf("count: %d\n", count);
			
			rs = stmt.executeQuery(sql2);
			while(rs.next()) {
				System.out.println(rs.getInt(1));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
