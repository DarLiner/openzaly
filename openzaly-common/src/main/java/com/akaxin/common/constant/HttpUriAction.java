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
	HTTP_ACTION("hai", ""), //

	HAI_SITE_SERVICE("hai", "site"), // 站点配置服务

	HAI_USER_SERVICE("hai", "list"), // 用户服务

	HAI_GROUP_SERVICE("hai", "list"), // 群组服务

	HAI_FRIEND_SERVICE("hai", "apply"), // 好友服务

	HAI_MESSAGE_SERVICE("hai", "message"), // 好友服务

	HAI_PUSH_SERVICE("hai", "push"), // push通知推送

	HAI_UIC_SERVICE("hai", "uic"), // 邀请码服务

	HAI_PLUGIN_SERVICE("hai", "plugin");// 扩展服务

	private String rety;
	private String service;

	HttpUriAction(String rety, String service) {
		this.rety = rety;
		this.service = service;
	}

	public String getRety() {
		return this.rety;
	}

	public String getService() {
		return this.service;
	}

	public static HttpUriAction getUriActionEnum(String rety, String service) {
		for (HttpUriAction hua : HttpUriAction.values()) {
			if (hua.getService().equals(service)) {
				return hua;
			}
		}
		return null;
	}
}
