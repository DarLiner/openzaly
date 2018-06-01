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

public interface CommandConst {
	public String PROTOCOL_VERSION = "1.0";// 网络协议版本
	public String SITE_VERSION = "0.3.2";// 站点版本（特指proto版本）

	public String SITE_FRIEND_ID = "site_friend_id";
	public String SITE_GROUP_ID = "site_group_id";
	public String CHAHHEL_SESSION = "channel_session";
	public String CHANNEL_CONTEXT = "channel_context";
	public String CLIENT_IP = "client_ip";
	public String MSG_TYPE = "msg_type";
	public String START_TIME = "start_time";
	public String END_TIME = "end_time";

	// plugin
	public String PLUGIN_AUTH_KEY = "plugin_auth_key";
	public String PLUGIN_ID = "plugin_id";

	public String IM_MSG_TOCLIENT = "im.stc.message";
	public String IM_MSG_TOSITE = "im.cts.message";

	public String IM_MSG_FINISH = "im.msg.finish";

	public String IM_STC_PSN = "im.stc.psn";

	public String IM_NOTICE = "im.stc.notice";

	public String API_PUSH_NOTIFICATION = "api.push.notification";
	public String API_PUSH_NOTIFICATIONS = "api.push.notifications";

	public String API_PHONE_CONFIRETOKEN = "api.phone.confirmToken";

	public String ACTION_RES = "_";
}
