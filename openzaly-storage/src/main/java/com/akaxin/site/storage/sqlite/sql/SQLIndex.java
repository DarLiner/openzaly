package com.akaxin.site.storage.sqlite.sql;

import java.util.Arrays;
import java.util.List;

/**
 * 数据库创建索引字段以及语句
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-29 14:06:32
 */
public interface SQLIndex {
	public String INDEX_SITEUSERID = "site_user_id";
	public String INDEX_GLOBALUSERID = "global_user_id";
	public String INDEX_SITEFRIENDID = "site_friend_id";
	public String INDEX_SITEGROUPID = "site_group_id";
	public String INDEX_SESSIONID = "session_id";
	public String INDEX_DEVICEID = "device_id";

	public String USER_PROFILE_SITEUSERID_INDEXSQL = "CREATE UNIQUE INDEX IF NOT EXISTS index_user_profile_id ON "
			+ SQLConst.SITE_USER_PROFILE + "(" + INDEX_SITEUSERID + "," + INDEX_GLOBALUSERID + ")";
	public String USER_SESSION_INDEXSQL = "CREATE INDEX IF NOT EXISTS index_user_session ON "
			+ SQLConst.SITE_USER_SESSION + "(" + INDEX_SITEUSERID + "," + INDEX_DEVICEID + ")";
	public String USER_SESSIONID_INDEXSQL = "CREATE INDEX IF NOT EXISTS index_user_sessionid ON "
			+ SQLConst.SITE_USER_SESSION + "(" + INDEX_SESSIONID + ")";
	public String USER_FRIEND_INDEXSQL = "CREATE UNIQUE INDEX IF NOT EXISTS index_user_friend ON "
			+ SQLConst.SITE_USER_FRIEND + "(" + INDEX_SITEUSERID + "," + INDEX_SITEFRIENDID + ")";
	public String FRIEND_APPLY_INDEXSQL = "CREATE INDEX IF NOT EXISTS index_friend_apply ON "
			+ SQLConst.SITE_FRIEND_APPLY + "(" + INDEX_SITEUSERID + "," + INDEX_SITEFRIENDID + ")";
	public String U2_MESSAGE_INDEXSQL = "CREATE INDEX IF NOT EXISTS index_u2_message ON " + SQLConst.SITE_USER_MESSAGE
			+ "(" + INDEX_SITEUSERID + ")";
	public String U2_POINTER_INDEXSQL = "CREATE INDEX IF NOT EXISTS index_u2_pointer ON "
			+ SQLConst.SITE_MESSAGE_POINTER + "(" + INDEX_SITEUSERID + "," + INDEX_DEVICEID + ")";
	public String USER_GROUP_INDEXSQL = "CREATE UNIQUE INDEX IF NOT EXISTS index_user_group ON "
			+ SQLConst.SITE_USER_GROUP + "(" + INDEX_SITEUSERID + "," + INDEX_SITEGROUPID + ")";
	public String GROUP_PROFILE_INDEXSQL = "CREATE INDEX IF NOT EXISTS index_group_profile ON "
			+ SQLConst.SITE_GROUP_PROFILE + "(" + INDEX_SITEGROUPID + ")";
	public String GROUP_MESSAGE_INDEXSQL = "CREATE INDEX IF NOT EXISTS index_group_message ON "
			+ SQLConst.SITE_GROUP_MESSAGE + "(" + INDEX_SITEGROUPID + ")";
	public String GROUP_POINTER_INDEXSQL = "CREATE INDEX IF NOT EXISTS index_group_pointer ON "
			+ SQLConst.SITE_GROUP_MESSAGE_POINTER + "(" + INDEX_SITEUSERID + "," + INDEX_SITEGROUPID + ","
			+ INDEX_DEVICEID + ")";
	public String DEVICE_INDEXSQL = "CREATE UNIQUE INDEX IF NOT EXISTS index_user_device ON "
			+ SQLConst.SITE_USER_DEVICE + "(" + INDEX_SITEUSERID + "," + INDEX_DEVICEID + ")";

	public List<String> DB_INDEXS_SQL = Arrays.asList(//
			USER_PROFILE_SITEUSERID_INDEXSQL, //
			USER_SESSION_INDEXSQL, //
			USER_SESSIONID_INDEXSQL, //
			USER_FRIEND_INDEXSQL, //
			FRIEND_APPLY_INDEXSQL, //
			U2_MESSAGE_INDEXSQL, //
			U2_POINTER_INDEXSQL, //
			USER_GROUP_INDEXSQL, //
			GROUP_PROFILE_INDEXSQL, //
			GROUP_MESSAGE_INDEXSQL, //
			GROUP_POINTER_INDEXSQL, //
			DEVICE_INDEXSQL);
}
