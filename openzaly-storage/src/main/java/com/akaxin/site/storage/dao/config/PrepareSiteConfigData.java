package com.akaxin.site.storage.dao.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.proto.core.ConfigProto;
import com.akaxin.proto.core.PluginProto;
import com.akaxin.proto.core.UicProto;
import com.akaxin.site.storage.bean.UicBean;
import com.akaxin.site.storage.connection.DatabaseConnection;
import com.akaxin.site.storage.dao.SQLiteSiteConfigDao;
import com.akaxin.site.storage.dao.SQLiteUICDao;
import com.akaxin.site.storage.dao.sqlite.manager.PluginArgs;

/**
 * 初始化数据库以后，完成初始化站点配置工作
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-06-07 17:53:20
 */
public class PrepareSiteConfigData {
	private static final Logger logger = LoggerFactory.getLogger(PrepareSiteConfigData.class);

	public static void init(DBConfig config) {
		initSiteConfig(config.getConfigMap());
		initSitePlugin(1, PluginArgs.SITE_ADMIN_NAME, config.getAdminApi(), config.getSiteServer(),
				config.getAdminIcon());
		initSitePlugin(2, PluginArgs.FRIEND_SQUARE_NAME, PluginArgs.FRIEND_SQUARE_API, config.getSiteServer(),
				config.getParam(PluginArgs.FRIEND_SQUARE, String.class));
		initAdminUic(config.getAdminUic());
	}

	private static void initSiteConfig(Map<Integer, String> configMap) {
		try {
			Map<Integer, String> oldMap = SQLiteSiteConfigDao.getInstance().querySiteConfig();
			if (oldMap != null) {
				if (oldMap.get(ConfigProto.ConfigKey.SITE_ADMIN_VALUE) != null) {
					configMap.remove(ConfigProto.ConfigKey.SITE_ADMIN_VALUE);
				}
				if (oldMap.get(ConfigProto.ConfigKey.SITE_ADDRESS_VALUE) != null) {
					configMap.remove(ConfigProto.ConfigKey.SITE_ADDRESS_VALUE);
				}
				if (oldMap.get(ConfigProto.ConfigKey.PIC_PATH_VALUE) != null) {
					configMap.remove(ConfigProto.ConfigKey.PIC_PATH_VALUE);
				}
				if (oldMap.get(ConfigProto.ConfigKey.GROUP_MEMBERS_COUNT_VALUE) != null) {
					configMap.remove(ConfigProto.ConfigKey.GROUP_MEMBERS_COUNT_VALUE);
				}
				if (oldMap.get(ConfigProto.ConfigKey.REALNAME_STATUS_VALUE) != null) {
					configMap.remove(ConfigProto.ConfigKey.REALNAME_STATUS_VALUE);
				}
				if (oldMap.get(ConfigProto.ConfigKey.INVITE_CODE_STATUS_VALUE) != null) {
					configMap.remove(ConfigProto.ConfigKey.INVITE_CODE_STATUS_VALUE);
				}
				if (oldMap.get(ConfigProto.ConfigKey.PUSH_CLIENT_STATUS_VALUE) != null) {
					configMap.remove(ConfigProto.ConfigKey.PUSH_CLIENT_STATUS_VALUE);
				}
			}
			SQLiteSiteConfigDao.getInstance().updateSiteConfig(configMap, true);
		} catch (SQLException e) {
			logger.error("init site config error.");
		}
	}

	private static void initSitePlugin(int id, String siteName, String urlPage, String apiUrl, String siteIcon) {
		boolean result = false;
		String updateSql = "UPDATE site_plugin_manager SET "//
				+ "name=?,"//
				+ "url_page=?,"//
				+ "api_url=?,"//
				+ "auth_key=?"//
				+ " WHERE id=?;";//
		Connection updateConn = null;
		try {
			updateConn = DatabaseConnection.getConnection();
			PreparedStatement pst = updateConn.prepareStatement(updateSql);
			pst.setString(1, siteName);
			pst.setString(2, urlPage);
			pst.setString(3, apiUrl);
			pst.setString(4, "");
			pst.setInt(5, id);
			result = (pst.executeUpdate() > 0);
			logger.info("update site plugin result={} SQL={} name={} url_page={} url_api={}", result, updateSql,
					siteName, urlPage, apiUrl);
		} catch (SQLException e) {
			logger.error("update site plugin error", e);
		} finally {
			DatabaseConnection.returnConnection(updateConn);
		}

		Connection insertConn = null;
		try {
			if (!result) {
				String insertSql = "INSERT INTO site_plugin_manager("//
						+ "id,"//
						+ "name,"//
						+ "icon,"//
						+ "url_page,"//
						+ "api_url,"//
						+ "auth_key,"//
						+ "allowed_ip,"//
						+ "position,"//
						+ "sort,"//
						+ "display_mode,"//
						+ "permission_status,"//
						+ "add_time) VALUES(?,?,?,?,?,?,?,?,?,?,?,?);";
				insertConn = DatabaseConnection.getConnection();
				PreparedStatement pst = insertConn.prepareStatement(insertSql);
				pst.setInt(1, id);
				pst.setString(2, siteName);
				pst.setString(3, siteIcon);
				pst.setString(4, urlPage);
				pst.setString(5, apiUrl);
				if (id == 1) {// 默认为后台管理
					pst.setString(6, "");// authkey
					pst.setString(7, "127.0.0.1");// allowed_ip
					pst.setInt(8, PluginProto.PluginPosition.HOME_PAGE_VALUE);// position
					pst.setInt(9, 0);// sort
					pst.setInt(10, PluginProto.PluginDisplayMode.NEW_PAGE_VALUE); // display_mode
					pst.setInt(11, PluginProto.PermissionStatus.DISABLED_VALUE); // permission_status
					pst.setLong(12, System.currentTimeMillis()); // add_time
				} else {
					pst.setString(6, "");// authkey
					pst.setString(7, "127.0.0.1");// allowed_ip
					pst.setInt(8, PluginProto.PluginPosition.HOME_PAGE_VALUE);// position
					pst.setInt(9, 1);// sort
					pst.setInt(10, PluginProto.PluginDisplayMode.NEW_PAGE_VALUE); // display_mode
					pst.setInt(11, PluginProto.PermissionStatus.AVAILABLE_VALUE); // permission_status
					pst.setLong(12, System.currentTimeMillis()); // add_time
				}
				result = (pst.executeUpdate() > 0);

				logger.info("insert site plugin result={} SQL={} name={} url_page={} url_api={}", result, insertSql,
						siteName, urlPage, apiUrl);
			}
		} catch (SQLException e) {
			logger.error("insert site plugin error", e);
		} finally {
			DatabaseConnection.returnConnection(insertConn);
		}
	}

	private static void initAdminUic(String uic) {
		boolean result = false;
		try {
			UicBean bean = new UicBean();
			bean.setUic(uic);
			bean.setStatus(UicProto.UicStatus.UNUSED_VALUE);
			bean.setCreateTime(System.currentTimeMillis());
			result = SQLiteUICDao.getInstance().addUIC(bean);
		} catch (SQLException e) {
			logger.warn("add new uic to db error,you can ignore it");
		}
		if (result) {
			logger.info("init addmin uic success");
		} else {
			logger.warn("init admin uic fail");
		}
	}
}
