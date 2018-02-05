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
package com.akaxin.common.channel;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-31 12:26:34
 */
public class ChannelManager {

	private static Map<String, ChannelSession> clientChannelSessions = new ConcurrentHashMap<String, ChannelSession>();

	private ChannelManager() {

	}

	public static Map<String, ChannelSession> addChannelSession(String deviceId, ChannelSession channelSession) {
		clientChannelSessions.put(deviceId, channelSession);
		return clientChannelSessions;
	}

	public static Map<String, ChannelSession> delChannelSession(String userId) {
		clientChannelSessions.remove(userId);
		return clientChannelSessions;
	}

	public static ChannelSession getChannelSession(String deviceId) {
		return clientChannelSessions.get(deviceId);
	}

	public static Set<String> getChannelSessionKeySet() {
		return clientChannelSessions.keySet();
	}

	public static Map<String, ChannelSession> getChannelSessions() {
		return clientChannelSessions;
	}

	public static long getChannelSessionSize() {
		return clientChannelSessions.size();
	}
}