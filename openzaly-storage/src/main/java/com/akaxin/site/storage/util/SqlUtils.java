package com.akaxin.site.storage.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-05-26 11:42:47
 */
public class SqlUtils {

	public static SqlBean buildUpdateSql(String sql, Map<String, String> sqlMap) {
		SqlUtils.SqlBean bean = new SqlBean();
		bean.setSql(sql);
		Map<Integer, String> res = new HashMap<Integer, String>();
		StringBuilder sqlTag = new StringBuilder("SET ");
		if (sqlMap != null) {
			int index = 0;
			for (String key : sqlMap.keySet()) {
				if (sqlMap.get(key) != null) {
					if (index++ > 0) {
						sqlTag.append(",");
					}
					sqlTag.append(key + "=?");
					res.put(index, sqlMap.get(key));
				}

			}
		}
		bean.setSqlSupple(sqlTag.toString());
		bean.setParams(res);
		return bean;
	}

	public static class SqlBean {
		private String sql;
		private String sqlSupple;
		private Map<Integer, String> params;

		public String getSql() {
			if (sqlSupple != null) {
				return sql.replace("{}", sqlSupple);
			}
			return sql;
		}

		public void setSql(String sql) {
			this.sql = sql;
		}

		public String getSqlSupple() {
			return sqlSupple;
		}

		public void setSqlSupple(String sqlSupple) {
			this.sqlSupple = sqlSupple;
		}

		public Map<Integer, String> getParams() {
			return params;
		}

		public void setParams(Map<Integer, String> params) {
			this.params = params;
		}

	}

	public static void main(String[] args) {
		Map<String, String> hello = new HashMap<String, String>();
		hello.put("vk_na", null);
		hello.put("vk_he", "nihao");
		hello.put("vk_he2", "nihao2");

		String sql = "update site_user_profile {} where site_user_id=?;";
		SqlBean bean = buildUpdateSql(sql, hello);

		System.out.println(bean.getSql());
		System.out.println(bean.getParams());
	}
}
