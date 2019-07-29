package com.akaxin.site.storage.dao.sqlite.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.site.storage.connection.DatabaseConnection;
import com.akaxin.site.storage.dao.sql.SQLConst;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-07-05 10:55:27
 */
public class SQLiteUnique {
	private static final Logger logger = LoggerFactory.getLogger(SQLiteUnique.class);

	private static String SITE_USER_PROFILE = SQLConst.SITE_USER_PROFILE;
	private static String SITE_U2_MESSAGE = SQLConst.SITE_USER_MESSAGE;
	private static String SITE_GROUP_MESSAGE = SQLConst.SITE_GROUP_MESSAGE;

	private SQLiteUnique() {
	}

	/**
	 * 清理老版本中不是unique的字段，新版本需要设置为unique的问题
	 * 
	 * @throws SQLException
	 */
	public static void clearUnique(Connection conn) throws SQLException {
		cleanUserPhoneId(conn);
		cleanU2MsgId(conn);
		cleanGroupMsgId(conn);
	}

	private static void cleanUserPhoneId(Connection conn) throws SQLException {
		String sql = "delete from site_user_profile where id in (select id from site_user_profile where phone_id in (select phone_id from site_user_profile group by phone_id having count(phone_id)>1)) AND id not in (select max(id) from site_user_profile group by phone_id having count(phone_id)>1);";
		int result = clearWork(conn, sql);
		logger.info("clean unique phone_id from site_user_profile result={}", result);
	}

	private static void cleanU2MsgId(Connection conn) throws SQLException {
		String sql = "delete from site_user_message where id in (select id from site_user_message where msg_id in (select msg_id from site_user_message group by msg_id having count(msg_id)>1)) and id not in (select min(id) from site_user_message group by msg_id having count(msg_id)>1);";
		int result = clearWork(conn, sql);
		logger.info("clean unique msg_id from site_user_message result={}", result);
	}

	private static void cleanGroupMsgId(Connection conn) throws SQLException {
		String sql = "delete from site_group_message where id in (select id from site_group_message where msg_id in (select msg_id from site_group_message group by msg_id having count(msg_id)>1)) and id not in (select min(id) from site_group_message group by msg_id having count(msg_id)>1);";
		int result = clearWork(conn, sql);
		logger.info("clean unique msg_id from site_group_message result={}", result);
	}

	private static int clearWork(Connection conn, String sql) throws SQLException {
		PreparedStatement pst = null;
		int result = 0;
		try {
			pst = conn.prepareStatement(sql);
			result = pst.executeUpdate();
		} finally {
			DatabaseConnection.closePreparedStatement(pst);
		}
		return result;
	}

}
