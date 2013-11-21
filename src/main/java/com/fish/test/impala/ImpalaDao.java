package com.fish.test.impala;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ImpalaDao {

	/**
	 * sql DAO
	 * 
	 * @param sql 查询sql
	 * @param type 是否需要截断数据 0截断
	 * @return
	 */
	public  List<String> findImpalaList(String sql, int tagnum, int type) {
		GetImpalaConnection getconn = new GetImpalaConnection();
		Statement stm = null;
		ResultSet rs = null;
		List<String> list = new ArrayList<String>();
		try {
			//stm = conn.prepareStatement(sql);
			stm = getconn.getImpalaConn().createStatement();
			rs = stm.executeQuery(sql);
			System.out.println("rsrsrsrrsrsrsrsrsrsrsrsrsrsr");
			int i = 1;
			while (rs.next()) {
				StringBuffer sb = new StringBuffer();
				int j = 1;
				try {
					while (j <= tagnum) {
						sb.append(rs.getString(j)).append(",");
						j++;
					}
				} catch (Exception e) {
					list.add(e.getMessage());
					//e.printStackTrace();
				}
				list.add(sb.toString().substring(0, sb.toString().length() - 1));

				// 判断是否需要截断数据
				if (type == 0) {
					// 最大返回条数
					i++;
					if (i > GetImpalaConnection.impala_index_num) {
						break;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			list.add("-1");
			list.add("SQL ErrorCode : " + String.valueOf(e.getErrorCode()));
			list.add(e.getSQLState());
			list.add(e.getMessage());
		} finally {
			 try {
			     closeImpala(stm, rs);
			 } catch (SQLException e) {
			     e.printStackTrace();
			 }
		}
		return list;
	}
	
	public static void closeImpala( Statement stm,
			ResultSet rs) throws SQLException {
		if (rs != null)
			rs.close();
		if (stm != null)
			stm.close();
		//if (conn != null)
		//	conn.close();
	}
	
	public static void closeImpala(Connection conn, Statement stm) throws SQLException {
		if (stm != null)
			stm.close();
		if (conn != null)
			conn.close();
	}

	
}