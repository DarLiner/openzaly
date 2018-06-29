package com.akaxin.site.storage.dao.sql;

/**
 * SQLite中的表以及建表的SQL语句
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017-12-13 17:35:52
 */
public interface SQLConst {

	int SITE_DB_VERSION_9 = 9;// 0.9.5

	int SITE_DB_VERSION_10 = 10;// 0.10.6

	int SITE_DB_VERSION_11 = 11;// 1.0.7

	String SITE_CONFIG_INFO = "site_config_info";
	String SITE_USER_PROFILE = "site_user_profile";
	String SITE_USER_SESSION = "site_user_session";
	String SITE_USER_FRIEND = "site_user_friend";
	String SITE_FRIEND_APPLY = "site_friend_apply";
	String SITE_USER_MESSAGE = "site_user_message";
	String SITE_MESSAGE_POINTER = "site_message_pointer";
	String SITE_USER_GROUP = "site_user_group";
	String SITE_GROUP_PROFILE = "site_group_profile";
	String SITE_GROUP_MESSAGE = "site_group_message";
	String SITE_GROUP_MESSAGE_POINTER = "site_group_message_pointer";
	String SITE_USER_DEVICE = "site_user_device";
	String SITE_PLUGIN_MANAGER = "site_plugin_manager";
	String SITE_USER_UIC = "site_user_uic";// 用户邀请码存放信息表名称

	String SITE_EXPIRE_TOKEN = "site_expire_token";
}
