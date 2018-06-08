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
 * @since 2018-01-31 12:28:35
 */
public enum RequestAction {
	None(0, "none"), // none

	SITE(1, "site"), //

	IM(100, "im"), // im request
	IM_SITE(101, "im.site"), //
	IM_SITE_HELLO(102, "im.site.hello"), //
	IM_SITE_AUTH(103, "im.site.auth"), //

	IM_CTS_PING(104, "im.cts.ping"), // 客户端发送的ping
	IM_STC_PONG(105, "im.stc.pong"), // 服务端回复的pong
	IM_CTS_MESSAGE(106, "im.cts.message"), // im.messag
	IM_CTS_MESSAGE_U2(107, "im.cts.message.u2"), // 二人
	IM_CTS_MESSAGE_GROUP(108, "im.cts.message.group"), // 群组
	IM_STC_PSN(109, "im.stc.psn"), // 站点给客户端发送psn
	IM_SYNC_MESSAGE(110, "im.sync.message"), //
	IM_SYNC_FINISH(111, "im.sync.finish"), //
	IM_STC_NOTICE(112, "im.stc.notice"), // 站点给客户端发送通知命令
	IM_SYNC_MSGSTATUS(113, "im.sync.msgStatus"), //

	API(200, "api"), // api request
	API_SITE(201, "api.site"), //
	API_SITE_CONFIG(202, "api.site.config"), // register
	API_SITE_REGISTER(203, "api.site.register"), // register
	API_SITE_LOGIN(204, "api.site.login"), // login
	API_FRIEND(205, "friend"), //
	API_GROUP(206, "group"), //
	API_SECRETCHAT(207, "secretChat"), //
	API_USER(208, "user"), //
	API_DEVICE(209, "device"), // device
	API_FILE(210, "file"), // file
	API_PLUGIN(211, "plugin");

	private int index;
	private String name;

	RequestAction(int index, String name) {
		this.index = index;
		this.name = name;
	}

	public int getIndex() {
		return this.index;
	}

	public String getName() {
		return this.name;
	}

	public static RequestAction getAction(String serviceName) {
		for (RequestAction sName : RequestAction.values()) {
			if (sName.getName().equals(serviceName)) {
				return sName;
			}
		}
		return None;
	}

	public static RequestAction getService(int index) {
		for (RequestAction sName : RequestAction.values()) {
			if (sName.getIndex() == index) {
				return sName;
			}
		}
		return None;
	}
}
