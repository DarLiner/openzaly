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
package com.akaxin.site.connector.session;

import com.akaxin.site.storage.bean.SimpleAuthBean;

/**
 * 管理用户session，设置用户在线／离线状态。
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.11.04 11:24:35
 */
public class SessionManager {

	public static SessionManager getInstance() {
		return SingletonHolder.instance;
	}

	static class SingletonHolder {
		private static SessionManager instance = new SessionManager();
	}

	public SimpleAuthBean getAuthSession(String sessionId) {
		return UserSession.getInstance().getUserSession(sessionId);
	}

	public boolean setUserOnline(String siteUserId, String deviceId) {
		return UserSession.getInstance().updateSessionOnline(siteUserId, deviceId);
	}

	public boolean setUserOffline(String siteUserId, String deviceId) {
		return UserSession.getInstance().updateSessionOffline(siteUserId, deviceId);
	}

	public boolean updateActiveTime(String siteUserId, String deviceId) {
		return UserSession.getInstance().updateActiveTime(siteUserId, deviceId);
	}
}
