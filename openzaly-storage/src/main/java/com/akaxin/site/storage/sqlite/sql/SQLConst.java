package com.akaxin.site.storage.sqlite.sql;

import java.util.HashMap;

/**
 * SQLite中的表以及建表的SQL语句
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017-12-13 17:35:52
 */
public interface SQLConst {

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

	String CREATE_SITE_CONFIG_INFO_TABLE = "CREATE TABLE IF NOT EXISTS " + SITE_CONFIG_INFO
			+ "(id INTEGER primary key not null, config_key INTEGER UNIQUE NOT NULL, config_value TEXT);";

	String CREATE_SITE_USER_PROFILE_TABLE = "create table IF NOT EXISTS " + SITE_USER_PROFILE
			+ "(id INTEGER primary key not null, site_user_id VARCHAR(50) UNIQUE NOT NULL, global_user_id VARCHAR(100) UNIQUE NOT NULL, user_id_pubk TEXT UNIQUE, user_name VARCHAR(50), user_photo TEXT, phone_id VARCHAR(20), self_introduce TEXT, apply_info varchar(100), user_status INTEGER, register_time DATETIME);";

	String CREATE_SITE_USER_SESSION_TABLE = "CREATE TABLE IF NOT EXISTS " + SITE_USER_SESSION
			+ "(id INTEGER primary key not null, site_user_id VARCHAR(50) not null, session_id VARCHAR(100), is_online boolean, device_id VARCHAR(50), login_time DATETIME);";

	String CREATE_SITE_USER_FRIEND_TABLE = "CREATE TABLE IF NOT EXISTS " + SITE_USER_FRIEND
			+ "(id INTEGER primary key not null, site_user_id VARCHAR(50) not null, site_friend_id VARCHAR(50) not null, relation INTEGER, add_time DATETIME);";

	String CREATE_SITE_FRIEND_APPLY_TABLE = "CREATE TABLE IF NOT EXISTS " + SITE_FRIEND_APPLY
			+ "(id INTEGER primary key not null, site_user_id VARCHAR(50) not null, site_friend_id VARCHAR(50) not null, apply_reason TEXT, apply_time DATETIME);";

	String CREATE_SITE_USER_MESSAGE_TABLE = "CREATE TABLE IF NOT EXISTS " + SITE_USER_MESSAGE
			+ "(id INTEGER primary key not null, site_user_id VARCHAR(50) not null, msg_id VARCHAR(50), send_user_id VARCHAR(50), msg_type INTEGER, content TEXT, device_id VARCHAR(50), ts_key VARCHAR(50), msg_time DATETIME);";

	String CREATE_SITE_MESSAGE_POINTER_TABLE = "CREATE TABLE IF NOT EXISTS " + SITE_MESSAGE_POINTER
			+ "(id INTEGER primary key not null, site_user_id VARCHAR(50) not null, pointer INTEGER, device_id VARCHAR(50));";

	String CREATE_SITE_USER_GROUP_TABLE = "CREATE TABLE IF NOT EXISTS " + SITE_USER_GROUP
			+ "(id INTEGER primary key not null, site_user_id VARCHAR(50) not null, site_group_id VARCHAR(50) not null, user_role INTEGER, add_time DATETIME);";

	String CREATE_SITE_GROUP_PROFILE_TABLE = "CREATE TABLE IF NOT EXISTS " + SITE_GROUP_PROFILE
			+ "(id INTEGER primary key not null, site_group_id INTEGER UNIQUE NOT NULL, group_name VARCHAR(50), group_photo TEXT, group_notice TEXT, ts_status INTEGER, create_user_id VARCHAR(20), group_status INTEGER, create_time DATETIME);";

	String CREATE_SITE_GROUP_MESSAGE_TABLE = "CREATE TABLE IF NOT EXISTS " + SITE_GROUP_MESSAGE
			+ "(id INTEGER primary key not null, site_group_id VARCHAR(50) not null, msg_id VARCHAR(50), send_user_id VARCHAR(50), send_device_id VARCHAR(50), msg_type INTEGER, content TEXT, msg_time DATETIME);";

	String CREATE_SITE_GROUP_MESSAGE_POINTER_TABLE = "CREATE TABLE IF NOT EXISTS " + SITE_GROUP_MESSAGE_POINTER
			+ "(id INTEGER primary key not null, site_user_id VARCHAR(50) not null, site_group_id VARCHAR(50) not null, pointer INTEGER, device_id VARCHAR(50));";

	String CREATE_SITE_USER_DEVICE_TABLE = "CREATE TABLE IF NOT EXISTS " + SITE_USER_DEVICE
			+ "(id INTEGER primary key not null, site_user_id VARCHAR(50) not null, device_id VARCHAR(50), user_device_pubk TEXT, device_name TEXT, device_ip VARCHAR(50), user_token VARCHAR(50),active_time DATATIME, add_time DATATIME);";

	String CREATE_SITE_PLUGIN_MANAGER_TABLE = "CREATE TABLE IF NOT EXISTS " + SITE_PLUGIN_MANAGER
			+ "(id INTEGER PRIMARY KEY NOT NULL, name VARCHAR(50) UNIQUE NOT NULL, icon TEXT NOT NULL, url_page TEXT, url_api TEXT,auth_key TEXT,allowed_ip VARCHAR(50),status INTEGER,sort INTEGER,add_time DATATIME);";

	String CREATE_SITE_USER_UIC_TABLE = "CREATE TABLE IF NOT EXISTS " + SITE_USER_UIC
			+ "(id INTEGER PRIMARY KEY NOT NULL,uic VARCHAR(10) UNIQUE NOT NULL,site_user_id VARCHAR(50),status INTEGER,create_time LONG,use_time LONG)";

	public HashMap<String, String> SITE_TABLES_MAP = new HashMap<String, String>() {
		//
		private static final long serialVersionUID = 1L;

		{
			put(SITE_CONFIG_INFO, CREATE_SITE_CONFIG_INFO_TABLE);
			put(SITE_USER_PROFILE, CREATE_SITE_USER_PROFILE_TABLE);
			put(SITE_USER_SESSION, CREATE_SITE_USER_SESSION_TABLE);
			put(SITE_USER_FRIEND, CREATE_SITE_USER_FRIEND_TABLE);
			put(SITE_FRIEND_APPLY, CREATE_SITE_FRIEND_APPLY_TABLE);
			put(SITE_USER_MESSAGE, CREATE_SITE_USER_MESSAGE_TABLE);
			put(SITE_MESSAGE_POINTER, CREATE_SITE_MESSAGE_POINTER_TABLE);
			put(SITE_USER_GROUP, CREATE_SITE_USER_GROUP_TABLE);
			put(SITE_GROUP_PROFILE, CREATE_SITE_GROUP_PROFILE_TABLE);
			put(SITE_GROUP_MESSAGE, CREATE_SITE_GROUP_MESSAGE_TABLE);
			put(SITE_GROUP_MESSAGE_POINTER, CREATE_SITE_GROUP_MESSAGE_POINTER_TABLE);
			put(SITE_USER_DEVICE, CREATE_SITE_USER_DEVICE_TABLE);
			put(SITE_PLUGIN_MANAGER, CREATE_SITE_PLUGIN_MANAGER_TABLE);
			put(SITE_USER_UIC, CREATE_SITE_USER_UIC_TABLE);
		}
	};
}
