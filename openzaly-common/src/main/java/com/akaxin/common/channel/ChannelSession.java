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

import com.akaxin.common.constant.RequestAction;

import io.netty.channel.Channel;

/**
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-31 12:26:53
 */
public class ChannelSession {
	private String userId; // 用户在站点的siteUserId，每个站点id不同（区别globalUserId）
	private String deviceId;// 用户当前设备号
	private Channel channel;// 用户当前连接的channel
	private int ctype;// 1:IM 2:APi 3:WS
	private long psnTime = 0;// 发送给用户PSN时间戳
	private long synFinTime = 0;// 用户同步消息结束时间

	public ChannelSession(Channel channel) {
		this.channel = channel;
	}

	public ChannelSession(String userId, Channel channel) {
		this.userId = userId;
		this.channel = channel;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public int getCtype() {
		return ctype;
	}

	public void setCtype(int ctype) {
		this.ctype = ctype;
	}

	public long getPsnTime() {
		return psnTime;
	}

	public void setPsnTime(long psnTime) {
		this.psnTime = psnTime;
	}

	public long getSynFinTime() {
		return synFinTime;
	}

	public void setSynFinTime(long synFinTime) {
		this.synFinTime = synFinTime;
	}

	public boolean detectPsn() {
		return synFinTime <= psnTime;
	}

	public void setActionForPsn(String action) {
		if (RequestAction.IM_SYNC_FINISH.getName().equals(action)) {
			this.synFinTime = System.currentTimeMillis();
		} else if (RequestAction.IM_STC_PSN.getName().equals(action)) {
			this.psnTime = System.currentTimeMillis();
		}
	}
}
