package com.akaxin.site.storage.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MysqlManager {

	public static void main(String[] args) {
		try {
			C3P0PoolManager.initPool();

			for (int i = 0; i < 10; i++) {

				System.out.println("times = " + i);
				Connection conn = C3P0PoolManager.getConnection();

				PreparedStatement ps = conn.prepareStatement("select * from hello;");
				ResultSet rs = ps.executeQuery();

				while (rs.next()) {
					System.out.println(rs.getInt(1) + " " + rs.getString(2));
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
