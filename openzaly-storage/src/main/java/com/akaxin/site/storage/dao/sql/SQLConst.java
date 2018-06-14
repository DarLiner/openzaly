package com.akaxin.site.storage.dao.sql;

import java.util.HashMap;

/**
 * SQLite中的表以及建表的SQL语句
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017-12-13 17:35:52
 */
public interface SQLConst {

	int SITE_DB_VERSION_9 = 9;// 0.9.5

	int SITE_DB_VERSION_10 = 10;// 0.9.5

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
			+ "(id INTEGER PRIMARY KEY NOT NULL, config_key INTEGER UNIQUE NOT NULL, config_value TEXT);";

	String CREATE_SITE_USER_PROFILE_TABLE = "CREATE TABLE IF NOT EXISTS " + SITE_USER_PROFILE
			+ "(id INTEGER PRIMARY KEY NOT NULL,"// 主键，自增
			+ "site_user_id VARCHAR(50) UNIQUE NOT NULL,"// 站点用户ID
			+ "global_user_id VARCHAR(100) UNIQUE NOT NULL,"// 用户的全局ID
			+ "user_id_pubk TEXT UNIQUE NOT NULL,"// 用户ID公钥
			+ "site_login_id VARCHAR(50) UNIQUE,"// 用户登陆账号 login_id_lowercase
			+ "login_id_lowercase VARCHAR(50) UNIQUE,"// login_id_lowercase
			+ "user_password VARCHAR(50),"// 用户登陆站点的密码
			+ "user_name VARCHAR(50) NOT NULL,"// 用户昵称
			+ "user_name_in_latin VARCHAR(50),"// 用户昵称
			+ "user_photo TEXT,"// 用户头像
			+ "phone_id VARCHAR(20),"// 手机号码 +86_15271868205
			+ "self_introduce TEXT,"// 自我介绍
			+ "apply_info varchar(100), "// 申请注册站点的理由
			+ "user_status INTEGER,"// 用户的状态
			+ "mute BOOLEAN,"// 用户是否对站点静音
			+ "register_time LONG);";// 用户注册时间

	String CREATE_SITE_USER_SESSION_TABLE = "CREATE TABLE IF NOT EXISTS " + SITE_USER_SESSION
			+ "(id INTEGER PRIMARY KEY NOT NULL, site_user_id VARCHAR(50) not null, session_id VARCHAR(100), is_online boolean, device_id VARCHAR(50), login_time LONG);";

	String CREATE_SITE_USER_FRIEND_TABLE = "CREATE TABLE IF NOT EXISTS " + SITE_USER_FRIEND
			+ "(id INTEGER PRIMARY KEY NOT NULL,"// 主键
			+ "site_user_id VARCHAR(50) not null,"//
			+ "site_friend_id VARCHAR(50) not null,"// 好友id
			+ "alias_name VARCHAR(50),"// 好友的别名
			+ "alias_name_in_latin VARCHAR(50),"// 好友的别名拼音
			+ "relation INTEGER,"// 和好友之间的关系
			+ "mute BOOLEAN,"// 是否对好友消息免打扰
			+ "add_time LONG);";// 添加好友的时间

	String CREATE_SITE_FRIEND_APPLY_TABLE = "CREATE TABLE IF NOT EXISTS " + SITE_FRIEND_APPLY
			+ "(id INTEGER PRIMARY KEY NOT NULL, site_user_id VARCHAR(50) not null, site_friend_id VARCHAR(50) not null, apply_reason TEXT, apply_time LONG);";

	String CREATE_SITE_USER_MESSAGE_TABLE = "CREATE TABLE IF NOT EXISTS " + SITE_USER_MESSAGE
			+ "(id INTEGER PRIMARY KEY NOT NULL," // primary key
			+ "site_user_id VARCHAR(50) not null,"// 消息拥有者
			+ "msg_id VARCHAR(50)," // 消息ID
			+ "send_user_id VARCHAR(50),"// 消息发送者
			+ "receive_user_id VARCHAR(50),"// 消息接受者
			+ "msg_type INTEGER, " // 消息类型
			+ "content TEXT, "// 消息内容
			+ "device_id VARCHAR(50), "// 设备ID
			+ "ts_key VARCHAR(50),"// 加密key
			+ "msg_time LONG);";// 消息时间

	String CREATE_SITE_MESSAGE_POINTER_TABLE = "CREATE TABLE IF NOT EXISTS " + SITE_MESSAGE_POINTER
			+ "(id INTEGER PRIMARY KEY NOT NULL, site_user_id VARCHAR(50) not null, pointer INTEGER, device_id VARCHAR(50));";

	String CREATE_SITE_USER_GROUP_TABLE = "CREATE TABLE IF NOT EXISTS " + SITE_USER_GROUP
			+ "(id INTEGER PRIMARY KEY NOT NULL, site_user_id VARCHAR(50) not null, site_group_id VARCHAR(50) not null, user_role INTEGER, mute BOOLEAN, add_time LONG);";

	String CREATE_SITE_GROUP_PROFILE_TABLE = "CREATE TABLE IF NOT EXISTS " + SITE_GROUP_PROFILE
			+ "(id INTEGER PRIMARY KEY NOT NULL, site_group_id INTEGER UNIQUE NOT NULL, group_name VARCHAR(50), group_photo TEXT, group_notice TEXT, ts_status INTEGER, create_user_id VARCHAR(20), group_status INTEGER,close_invite_group_chat BOOLEAN, create_time LONG);";

	String CREATE_SITE_GROUP_MESSAGE_TABLE = "CREATE TABLE IF NOT EXISTS " + SITE_GROUP_MESSAGE
			+ "(id INTEGER PRIMARY KEY NOT NULL, site_group_id VARCHAR(50) not null, msg_id VARCHAR(50), send_user_id VARCHAR(50), send_device_id VARCHAR(50), msg_type INTEGER, content TEXT, msg_time LONG);";

	String CREATE_SITE_GROUP_MESSAGE_POINTER_TABLE = "CREATE TABLE IF NOT EXISTS " + SITE_GROUP_MESSAGE_POINTER
			+ "(id INTEGER PRIMARY KEY NOT NULL, site_user_id VARCHAR(50) not null, site_group_id VARCHAR(50) not null, pointer INTEGER, device_id VARCHAR(50));";

	String CREATE_SITE_USER_DEVICE_TABLE = "CREATE TABLE IF NOT EXISTS " + SITE_USER_DEVICE // 用户设备
			+ "(id INTEGER PRIMARY KEY NOT NULL," // 主键
			+ "site_user_id VARCHAR(50) NOT NULL," // 站点用户
			+ "device_id VARCHAR(50) UNIQUE NOT NULL," // 设备ID
			+ "user_device_pubk TEXT NOT NULL,"// 设备公钥
			+ "device_uuid VARCHAR(50) UNIQUE,"// 设备识别码,设备序列码
			+ "user_token VARCHAR(50),"// 用户的usertoken
			+ "device_name VARCHAR(60), " // 设备名称
			+ "device_ip VARCHAR(50), " // 设备ip
			+ "active_time LONG," // 活跃时间
			+ "add_time LONG);";// add时间

	String CREATE_SITE_PLUGIN_MANAGER_TABLE = "CREATE TABLE IF NOT EXISTS " + SITE_PLUGIN_MANAGER
			+ "(id INTEGER PRIMARY KEY NOT NULL,"// 主键
			+ "name VARCHAR(50) UNIQUE NOT NULL,"// 扩展名称
			+ "icon TEXT NOT NULL,"// 扩展logo
			+ "url_page TEXT,"// 扩展落地页
			+ "api_url TEXT,"// 扩展API地址
			+ "auth_key TEXT NOT NULL,"// 认证KEY
			+ "allowed_ip TEXT,"// 允许访问的IP
			+ "position INTEGER,"// 扩展在客户端的位置
			+ "sort INTEGER,"// 扩展的排序
			+ "display_mode INTEGER,"// 扩展展示的方式，新页，分屏等
			+ "permission_status INTEGER,"// 扩展是否可用的状态
			+ "add_time LONG);";// 扩展添加的时间

	String CREATE_SITE_USER_UIC_TABLE = "CREATE TABLE IF NOT EXISTS " + SITE_USER_UIC
			+ "(id INTEGER PRIMARY KEY NOT NULL,uic VARCHAR(10) UNIQUE NOT NULL,site_user_id VARCHAR(50),status INTEGER,create_time LONG,use_time LONG)";

	public HashMap<String, String> SITE_TABLES_MAP = new HashMap<String, String>() {
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
