/** 
 * Copyright 2018-2028 Akaxin Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package com.akaxin.common.constant;

/**
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-31 12:28:44
 */
public enum HttpUriAction {
	HTTP_ACTION("http_request_action", "hai"), //

	HAI_USER_LIST("/hai/user/list", "list"), //
	HAI_USER_RELATIONLIST("/hai/user/relationList", "relationList"), // 附带是否互为好友的关系列表
	HAI_USER_SEARCH("/hai/user/search", "search"), //
	HAI_USER_UPDATE("/hai/user/update", "update"), //
	HAI_USER_SEALUP("/hai/user/sealUp", "sealUp"), //
	HAI_USER_PROFILE("/hai/user/profile", "profile"), //

	HAI_GROUP_LIST("/hai/group/list", "list"), //
	HAI_GROUP_PROFILE("/hai/group/profile", "profile"), //
	HAI_GROUP_MEMBERS("/hai/group/members", "members"), //
	HAI_GROUP_NONMEMBERS("/hai/group/nonmembers", "nonmembers"), //
	HAI_GROUP_ADDMEMBER("/hai/group/addMember", "addMember"), //
	HAI_GROUP_DELETE("/hai/group/delete", "delete"), //
	HAI_GROUP_REMOVEMEMBER("/hai/group/removeMember", "removeMember"), //
	HAI_GROUP_SETADMIN("/hai/group/setAdmin", "setAdmin"), //
	HAI_GROUP_UPDATEPROFILE("/hai/group/updateProfile", "updateProfile"), //

	HAI_FRIEND_APPLY("/hai/friend/apply", "apply"), //
	HAI_FRIEND_DELETE("/hai/friend/delete", "delete"), //

	HAI_SITE_GETCONFIG("/hai/site/getConfig", "getConfig"), //
	HAI_SITE_UPDATECONFIG("/hai/site/updateConfig", "updateConfig"), //

	HAI_UIC_CREATE("/hai/uic/create", "create"), //
	HAI_UIC_INFO("/hai/uic/info", "info"), //
	HAI_UIC_LIST("/hai/uic/list", "list"), //

	HAI_PLUGIN_ADD("/hai/plugin/add", "add"), //
	HAI_PLUGIN_DELETE("/hai/plugin/delete", "delete"), //
	HAI_PLUGIN_DISABLE("/hai/plugin/disable", "disable"), //
	HAI_PLUGIN_LIST("/hai/plugin/list", "list"), //
	HAI_PLUGIN_PROFILE("/hai/plugin/profile", "profile"), //
	HAI_PLUGIN_UPDATE("/hai/plugin/update", "update"), //
	HAI_PLUGIN_UPDATESTATUS("/hai/plugin/updateStatus", "updateStatus"); //

	private String uri;
	private String method;

	HttpUriAction(String uri, String method) {
		this.uri = uri;
		this.method = method;
	}

	public String getUri() {
		return this.uri;
	}

	public String getMethod() {
		return this.method;
	}

	public static HttpUriAction getUriActionEnum(String uri) {
		for (HttpUriAction ua : HttpUriAction.values()) {
			if (ua.getUri().equals(uri)) {
				return ua;
			}
		}
		return HttpUriAction.HTTP_ACTION;
	}
}
