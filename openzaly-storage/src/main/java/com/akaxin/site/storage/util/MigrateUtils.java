package com.akaxin.site.storage.util;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import com.akaxin.common.utils.PrintUtils;
import com.akaxin.site.storage.connection.DatabaseConnection;
import com.akaxin.site.storage.dao.config.JdbcConst;
import com.akaxin.site.storage.dao.mysql.manager.InitDatabaseConnection;
import com.akaxin.site.storage.dao.mysql.manager.MysqlManager;
import com.akaxin.site.storage.dao.sqlite.manager.SQLiteJDBCManager;
import com.akaxin.site.storage.exception.MigrateDatabaseException;
import com.akaxin.site.storage.exception.NeedInitMysqlException;

/**
 * 数据库迁移工具，将sqlite中数据迁移到mysql中
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-06-14 19:01:42
 */
public class MigrateUtils {
	private static final Logger logger = LoggerFactory.getLogger(MigrateUtils.class);

	private static final String OPENZALY_MYSQL_SQL = "openzaly-mysql.sql";
	private static Connection sqliteConnection;
	private static Connection mysqlConnection;

	public static void sqlite2Mysql(Properties prop) throws MigrateDatabaseException, NeedInitMysqlException {

		if (prop == null || prop.size() == 0) {
			throw new MigrateDatabaseException("properties config is null");
		}

		String sqliteUrl = prop.getProperty(JdbcConst.SQLITE_URL);

		if (StringUtils.isEmpty(sqliteUrl)) {
			throw new MigrateDatabaseException("sqlite url is null");
		}

		try {
			// Step1.加载sqlite
			try {
				SQLiteJDBCManager.loadDriver(sqliteUrl);
				sqliteConnection = SQLiteJDBCManager.getConnection();
			} catch (Exception e) {
				throw new MigrateDatabaseException("load sqlite url={} error", sqliteUrl);
			}

			// Setp2.检测mysql database and tables
			try {
				mysqlConnection = InitDatabaseConnection.getInitConnection(prop);
				// 初始化数据库表
				File file = new File(OPENZALY_MYSQL_SQL);
				if (!file.exists()) {
					throw new NeedInitMysqlException("check mysql with sql script file is not exists");
				}

				FileSystemResource rc = new FileSystemResource(file);
				EncodedResource encodeRes = new EncodedResource(rc, "GBK");
				ScriptUtils.executeSqlScript(mysqlConnection, encodeRes);
			} catch (Exception e) {
				throw new MigrateDatabaseException("check mysql database tables error,msg={}", e.getMessage());
			}

			// Step3.加载mysql
			try {
				mysqlConnection = InitDatabaseConnection.getConnection(prop);
			} catch (Exception e) {
				throw new MigrateDatabaseException("load mysql connection error,msg={}", e.getMessage());
			}

			if (sqliteConnection != null && mysqlConnection != null) {
				doSiteConfigInfoTable();
				doSiteUserProfileTable();
				doSiteUserSessionTable();
				doSiteUserFriendTable();
				doSiteFriendAapplyTable();
				doSiteUserMessageTable();
				doSiteMessagePointerTable();
				doSiteUserGroupTable();
				doSiteGroupProfileTable();
				doSiteGroupMessageTable();
				doSiteGroupMessagePointerTable();
				doSiteUserDeviceTable();
				doSitePluginManagerTable();
				doSiteUserUicTable();
			} else {
				throw new MigrateDatabaseException("sqlite or mysql connection is null");
			}

		} finally {
			// close sqliteConnection
			MysqlManager.returnConnection(sqliteConnection);
			// close mysqlConnection
			MysqlManager.returnConnection(mysqlConnection);
		}

	}

	// table.1 : site_config_info
	private static boolean doSiteConfigInfoTable() throws MigrateDatabaseException {
		// 迁移表1.
		String table = "site_config_info";
		String sql = "id,config_key,config_value";
		String fromSql = "SELECT " + sql + " from site_config_info;";
		String toSql = "INSERT INTO site_config_info(" + sql + ") VALUES(?,?,?);";

		return doMigrateWork(table, fromSql, toSql, "siteConfigInfoRS");
	}

	private static void siteConfigInfoRS(PreparedStatement ps, ResultSet rs) throws SQLException {
		ps.setInt(1, rs.getInt(1));
		ps.setInt(2, rs.getInt(2));
		ps.setString(3, rs.getString(3));
		ps.executeUpdate();
	}

	// table.2 site_user_profile
	private static boolean doSiteUserProfileTable() throws MigrateDatabaseException {
		String table = "site_user_profile";
		String sql = "id,site_user_id,global_user_id,site_login_id,login_id_lowercase,user_password,user_id_pubk,user_name,user_name_in_latin,user_photo,phone_id,self_introduce,apply_info,user_status,mute,register_time";
		String fromSql = "SELECT " + sql + " FROM site_user_profile;";
		String toSql = "INSERT INTO site_user_profile(" + sql + ") VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";

		return doMigrateWork(table, fromSql, toSql, "siteUserProfileRS");
	}

	private static void siteUserProfileRS(PreparedStatement ps, ResultSet rs) throws SQLException {
		ps.setInt(1, rs.getInt(1));
		ps.setString(2, rs.getString(2));
		ps.setString(3, rs.getString(3));
		ps.setString(4, rs.getString(4));
		ps.setString(5, rs.getString(5));
		ps.setString(6, rs.getString(6));
		ps.setString(7, rs.getString(7));
		ps.setString(8, rs.getString(8));
		ps.setString(9, rs.getString(9));
		ps.setString(10, rs.getString(10));
		ps.setString(11, rs.getString(11));
		ps.setString(12, rs.getString(12));
		ps.setString(13, rs.getString(13));
		ps.setInt(14, rs.getInt(14));
		ps.setBoolean(15, rs.getBoolean(15));
		ps.setLong(16, rs.getLong(16));
		ps.executeUpdate();
	}

	// table 3:site_user_session
	private static boolean doSiteUserSessionTable() throws MigrateDatabaseException {
		String table = "site_user_session";
		String sql = "id,session_id,site_user_id,is_online,device_id,login_time";
		String fromSql = "SELECT " + sql + " FROM site_user_session;";
		String toSql = "INSERT INTO site_user_session(" + sql + ") VALUES(?,?,?,?,?,?);";

		return doMigrateWork(table, fromSql, toSql, "siteUserSessionRS");
	}

	private static void siteUserSessionRS(PreparedStatement ps, ResultSet rs) throws SQLException {
		ps.setInt(1, rs.getInt(1));
		ps.setString(2, rs.getString(2));
		ps.setString(3, rs.getString(3));
		ps.setBoolean(4, rs.getBoolean(4));
		ps.setString(5, rs.getString(5));
		ps.setLong(6, rs.getLong(6));
		ps.executeUpdate();
	}

	// table 4:site_user_friend
	private static boolean doSiteUserFriendTable() throws MigrateDatabaseException {
		String table = "site_user_friend";
		String sql = "id,site_user_id,site_friend_id,alias_name,alias_name_in_latin,relation,mute,add_time";
		String fromSql = "SELECT " + sql + " FROM site_user_friend;";
		String toSql = "INSERT INTO site_user_friend(" + sql + ") VALUES(?,?,?,?,?,?,?,?);";

		return doMigrateWork(table, fromSql, toSql, "siteUserFriendRS");
	}

	private static void siteUserFriendRS(PreparedStatement ps, ResultSet rs) throws SQLException {
		ps.setInt(1, rs.getInt(1));
		ps.setString(2, rs.getString(2));
		ps.setString(3, rs.getString(3));
		ps.setString(4, rs.getString(4));
		ps.setString(5, rs.getString(5));
		ps.setInt(6, rs.getInt(6));
		ps.setBoolean(7, rs.getBoolean(7));
		ps.setLong(8, rs.getLong(8));
		ps.executeUpdate();
	}

	// table 5:site_friend_apply
	private static boolean doSiteFriendAapplyTable() throws MigrateDatabaseException {
		String table = "site_friend_apply";
		String sql = "id,site_user_id,site_friend_id,apply_reason,apply_time";
		String fromSql = "SELECT " + sql + " FROM site_friend_apply;";
		String toSql = "INSERT INTO site_friend_apply(" + sql + ") VALUES(?,?,?,?,?);";

		return doMigrateWork(table, fromSql, toSql, "siteFriendApplyRS");
	}

	private static void siteFriendApplyRS(PreparedStatement ps, ResultSet rs) throws SQLException {
		ps.setInt(1, rs.getInt(1));
		ps.setString(2, rs.getString(2));
		ps.setString(3, rs.getString(3));
		ps.setString(4, rs.getString(4));
		ps.setLong(5, rs.getLong(5));
		ps.executeUpdate();
	}

	// table 6:site_user_message
	private static boolean doSiteUserMessageTable() throws MigrateDatabaseException {
		String table = "site_user_message";
		String sql = "id,site_user_id,msg_id,send_user_id,receive_user_id,msg_type,content,device_id,ts_key,msg_time";
		String fromSql = "select " + sql + " from site_user_message;";
		String toSql = "INSERT INTO site_user_message(" + sql + ") VALUES(?,?,?,?,?,?,?,?,?,?);";

		return doMigrateWork(table, fromSql, toSql, "siteUserMessageRS");
	}

	private static void siteUserMessageRS(PreparedStatement ps, ResultSet rs) throws SQLException {
		try {
			ps.setInt(1, rs.getInt(1));
			ps.setString(2, rs.getString(2));
			ps.setString(3, rs.getString(3));
			ps.setString(4, rs.getString(4));
			ps.setString(5, rs.getString(5));
			ps.setInt(6, rs.getInt(6));
			ps.setString(7, rs.getString(7));
			ps.setString(8, rs.getString(8));
			ps.setString(9, rs.getString(9));
			ps.setLong(10, rs.getLong(10));
			ps.executeUpdate();
		} catch (SQLException e) {
			logger.error("migrate table site_user_message error", e);
		}
	}

	// table 7:site_message_pointer
	private static boolean doSiteMessagePointerTable() throws MigrateDatabaseException {
		String table = "site_message_pointer";
		String sql = "id,site_user_id,device_id,pointer";
		String fromSql = "SELECT " + sql + " FROM site_message_pointer;";
		String toSql = "INSERT INTO site_message_pointer(" + sql + ") values(?,?,?,?);";

		return doMigrateWork(table, fromSql, toSql, "siteMessagePointerRS");
	}

	private static void siteMessagePointerRS(PreparedStatement ps, ResultSet rs) throws SQLException {
		ps.setInt(1, rs.getInt(1));
		ps.setString(2, rs.getString(2));
		ps.setString(3, rs.getString(3));
		ps.setInt(4, rs.getInt(4));
		ps.executeUpdate();
	}

	// table 8:site_user_group
	private static boolean doSiteUserGroupTable() throws MigrateDatabaseException {
		String table = "site_user_group";
		String sql = "id,site_user_id,site_group_id,user_role,mute,add_time";
		String fromSql = "SELECT " + sql + " FROM site_user_group;";
		String toSql = "INSERT INTO site_user_group(" + sql + ") VALUES(?,?,?,?,?,?);";

		return doMigrateWork(table, fromSql, toSql, "siteUserGroupRS");
	}

	private static void siteUserGroupRS(PreparedStatement ps, ResultSet rs) throws SQLException {
		ps.setInt(1, rs.getInt(1));
		ps.setString(2, rs.getString(2));
		ps.setString(3, rs.getString(3));
		ps.setInt(4, rs.getInt(4));
		ps.setBoolean(5, rs.getBoolean(5));
		ps.setLong(6, rs.getLong(6));
		ps.executeUpdate();
	}

	// table 9:site_group_profile
	private static boolean doSiteGroupProfileTable() throws MigrateDatabaseException {
		String table = "site_group_profile";
		String sql = "id,site_group_id,create_user_id,group_name,group_photo,group_notice,ts_status,group_status,close_invite_group_chat,create_time";
		String fromSql = "SELECT " + sql + " FROM site_group_profile;";
		String toSql = "INSERT INTO site_group_profile(" + sql + ") VALUES(?,?,?,?,?,?,?,?,?,?);";

		return doMigrateWork(table, fromSql, toSql, "siteGroupProfileRS");
	}

	private static void siteGroupProfileRS(PreparedStatement ps, ResultSet rs) throws SQLException {
		ps.setInt(1, rs.getInt(1));
		ps.setString(2, rs.getString(2));
		ps.setString(3, rs.getString(3));
		ps.setString(4, rs.getString(4));
		ps.setString(5, rs.getString(5));
		ps.setString(6, rs.getString(6));
		ps.setInt(7, rs.getInt(7));
		ps.setInt(8, rs.getInt(8));
		ps.setBoolean(9, rs.getBoolean(9));
		ps.setLong(10, rs.getLong(10));
		ps.executeUpdate();
	}

	// table 10:site_group_message
	private static boolean doSiteGroupMessageTable() throws MigrateDatabaseException {
		String table = "site_group_message";
		String sql = "id,site_group_id,msg_id,send_user_id,send_device_id,msg_type,content,msg_time";
		String fromSql = "SELECT " + sql + " FROM site_group_message;";
		String toSql = "INSERT INTO site_group_message(" + sql + ") VALUES(?,?,?,?,?,?,?,?);";

		return doMigrateWork(table, fromSql, toSql, "siteSiteGroupMessageRS");
	}

	private static void siteSiteGroupMessageRS(PreparedStatement ps, ResultSet rs) throws SQLException {
		try {
			ps.setInt(1, rs.getInt(1));
			ps.setString(2, rs.getString(2));
			ps.setString(3, rs.getString(3));
			ps.setString(4, rs.getString(4));
			ps.setString(5, rs.getString(5));
			ps.setInt(6, rs.getInt(6));
			ps.setString(7, rs.getString(7));
			ps.setLong(8, rs.getLong(8));
			ps.executeUpdate();
		} catch (Exception e) {
			logger.error("migrate table site_group_message error.", e);
		}
	}

	// table 11:site_group_message_pointer
	private static boolean doSiteGroupMessagePointerTable() throws MigrateDatabaseException {
		String table = "site_group_message_pointer";
		String sql = "id,site_group_id,site_user_id,device_id,pointer";
		String fromSql = "SELECT " + sql + " FROM site_group_message_pointer;";
		String toSql = "INSERT INTO site_group_message_pointer(" + sql + ") VALUES(?,?,?,?,?);";

		return doMigrateWork(table, fromSql, toSql, "siteGroupMessagePointerRS");
	}

	private static void siteGroupMessagePointerRS(PreparedStatement ps, ResultSet rs) throws SQLException {
		ps.setInt(1, rs.getInt(1));
		ps.setString(2, rs.getString(2));
		ps.setString(3, rs.getString(3));
		ps.setString(4, rs.getString(4));
		ps.setInt(5, rs.getInt(5));
		ps.executeUpdate();
	}

	// table 12:site_user_device
	private static boolean doSiteUserDeviceTable() throws MigrateDatabaseException {
		String table = "site_user_device";
		String sql = "id,site_user_id,device_id,user_device_pubk,device_uuid,user_token,device_name,device_ip,active_time,add_time";
		String fromSql = "SELECT " + sql + " FROM site_user_device;";
		String toSql = "INSERT INTO site_user_device(" + sql + ") VALUES(?,?,?,?,?,?,?,?,?,?);";

		return doMigrateWork(table, fromSql, toSql, "siteUserDeviceRS");
	}

	private static void siteUserDeviceRS(PreparedStatement ps, ResultSet rs) throws SQLException {
		ps.setInt(1, rs.getInt(1));
		ps.setString(2, rs.getString(2));
		ps.setString(3, rs.getString(3));
		ps.setString(4, rs.getString(4));
		ps.setString(5, rs.getString(5));
		ps.setString(6, rs.getString(6));
		ps.setString(7, rs.getString(7));
		ps.setString(8, rs.getString(8));
		ps.setLong(9, rs.getLong(9));
		ps.setLong(10, rs.getLong(10));
		ps.executeUpdate();
	}

	// table 13:site_plugin_manager
	private static boolean doSitePluginManagerTable() throws MigrateDatabaseException {
		String table = "site_plugin_manager";
		String sql = "id,name,icon,api_url,url_page,auth_key,allowed_ip,position,sort,display_mode,permission_status,add_time";
		String fromSql = "SELECT " + sql + " FROM site_plugin_manager;";
		String toSql = "INSERT INTO site_plugin_manager(" + sql + ") values(?,?,?,?,?,?,?,?,?,?,?,?);";

		return doMigrateWork(table, fromSql, toSql, "sitePluginManagerRS");
	}

	private static void sitePluginManagerRS(PreparedStatement ps, ResultSet rs) throws SQLException {
		ps.setInt(1, rs.getInt(1));
		ps.setString(2, rs.getString(2));
		ps.setString(3, rs.getString(3));
		ps.setString(4, rs.getString(4));
		ps.setString(5, rs.getString(5));
		ps.setString(6, rs.getString(6));
		ps.setString(7, rs.getString(7));
		ps.setInt(8, rs.getInt(8));
		ps.setInt(9, rs.getInt(9));
		ps.setInt(10, rs.getInt(10));
		ps.setInt(11, rs.getInt(11));
		ps.setLong(12, rs.getLong(12));
		ps.executeUpdate();
	}

	// table 14:site_user_uic
	private static boolean doSiteUserUicTable() throws MigrateDatabaseException {
		String table = "site_user_uic";
		String sql = "id,uic,site_user_id,status,create_time,use_time";
		String fromSql = "SELECT " + sql + " FROM site_user_uic;";
		String toSql = "INSERT INTO site_user_uic(" + sql + ") VALUES(?,?,?,?,?,?)";
		return doMigrateWork(table, fromSql, toSql, "siteUserUicRS");
	}

	private static void siteUserUicRS(PreparedStatement ps, ResultSet rs) throws SQLException {
		ps.setInt(1, rs.getInt(1));
		ps.setString(2, rs.getString(2));
		ps.setString(3, rs.getString(3));
		ps.setInt(4, rs.getInt(4));
		ps.setLong(5, rs.getLong(5));
		ps.setLong(6, rs.getLong(6));
	}

	private static boolean doMigrateWork(String tableName, String fromSql, String toSql, final String rsMethodName)
			throws MigrateDatabaseException {

		PrintUtils.print("migrating database table : {}", tableName);
		PrintUtils.flush();

		boolean result = false;
		PreparedStatement fromPst = null;
		ResultSet rs = null;
		PreparedStatement toPst = null;
		try {
			fromPst = sqliteConnection.prepareStatement(fromSql);
			rs = fromPst.executeQuery();
			mysqlConnection.setAutoCommit(false);
			try {
				toPst = mysqlConnection.prepareStatement(toSql);
				while (rs.next()) {
					reflectRsMethod(rsMethodName, toPst, rs);
				}
				mysqlConnection.commit();
				result = true;
			} catch (Exception e) {
				mysqlConnection.rollback();
				throw e;
			} finally {
				mysqlConnection.setAutoCommit(true);
			}
		} catch (Exception e) {
			logger.error("migrate databse sqlite to mysql error", e);
			throw new MigrateDatabaseException(e);
		} finally {
			DatabaseConnection.closePreparedStatement(fromPst);
			DatabaseConnection.closeResultSet(rs);
			DatabaseConnection.closePreparedStatement(toPst);
		}

		PrintUtils.print("migrate database table : {} finish,result={}", tableName, result ? "OK" : "ERROR");
		PrintUtils.flush();
		return result;
	}

	private static void reflectRsMethod(String rsMethodName, PreparedStatement ps, ResultSet rs)
			throws SQLException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		Method method = MigrateUtils.class.getDeclaredMethod(rsMethodName, PreparedStatement.class, ResultSet.class);
		method.invoke(null, ps, rs);
	}
}
