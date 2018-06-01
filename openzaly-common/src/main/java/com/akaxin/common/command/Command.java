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
package com.akaxin.common.command;

import java.util.HashMap;
import java.util.Map;

import com.akaxin.common.channel.ChannelSession;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.utils.StringHelper;

import io.netty.channel.ChannelHandlerContext;

/**
 * 处理完成后的消息载体
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017.09.30
 */
public class Command {
	private String siteUserId;
	private String deviceId;
	private String globalUserId;
	private String rety; // request type，im/api/hai
	private String service;
	private String method;
	private Map<Integer, String> header;
	private byte[] params;
	private CommandResponse response; // response

	private Map<String, Object> fields = new HashMap<String, Object>();

	public void setAction(String splitStrs) {
		String[] splitStr = splitStrs.split("\\.");
		this.rety = splitStr[0];
		this.service = splitStr[1];
		if (splitStr.length == 3) {
			this.method = splitStr[2];
		}
	}

	public String getAction() {
		if ("im".equals(this.rety) || "api".equals(this.rety)) {
			return this.rety + "." + this.service + "." + this.method;
		}
		return null;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getSiteUserId() {
		return siteUserId;
	}

	public void setSiteUserId(String siteUserId) {
		this.siteUserId = siteUserId;
	}

	public String getGlobalUserId() {
		return globalUserId;
	}

	public void setGlobalUserId(String globalUserId) {
		this.globalUserId = globalUserId;
	}

	public String getRety() {
		return rety;
	}

	public void setRety(String rety) {
		this.rety = rety;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getUri() {
		if ("hai".equals(this.rety)) {
			return "/" + this.rety + "/" + this.service + "/" + this.method;
		}
		return null;
	}

	public void setUri(String uri) {
		uri = StringHelper.clearRepeated(uri, "/");
		String[] splitStr = uri.split("/");
		int index = splitStr.length - 3;
		this.rety = splitStr[index];
		this.service = splitStr[index + 1];
		this.method = splitStr[index + 2];
	}

	public Map<Integer, String> getHeader() {
		return header;
	}

	public void setHeader(Map<Integer, String> header) {
		this.header = header;
	}

	public byte[] getParams() {
		return params;
	}

	public Command setParams(byte[] params) {
		this.params = params;
		return this;
	}

	public Map<String, Object> getFields() {
		return fields;
	}

	@SuppressWarnings("unchecked")
	public <T> T getField(String k, Class<T> t) {
		Object obj = fields.get(k);
		return obj == null ? null : (T) obj;
	}

	public Command setFields(Map<String, Object> map) {
		this.fields.putAll(map);
		return this;
	}

	public Command setField(String k, Object v) {
		this.fields.put(k, v);
		return this;
	}

	public Command setSiteFriendId(String siteFriendId) {
		this.fields.put(CommandConst.SITE_FRIEND_ID, siteFriendId);
		return this;
	}

	public String getSiteFriendId() {
		return this.getField(CommandConst.SITE_FRIEND_ID, String.class);
	}

	public Command setSiteGroupId(String siteGroupId) {
		this.fields.put(CommandConst.SITE_GROUP_ID, siteGroupId);
		return this;
	}

	public String getSiteGroupId() {
		return this.getField(CommandConst.SITE_GROUP_ID, String.class);
	}

	public Command setClientIp(String clientIp) {
		this.fields.put(CommandConst.CLIENT_IP, clientIp);
		return this;
	}

	public String getClientIp() {
		return this.getField(CommandConst.CLIENT_IP, String.class);
	}

	public Command setPluginId(String pluginId) {
		this.fields.put(CommandConst.PLUGIN_ID, pluginId);
		return this;
	}

	public String getPluginId() {
		return this.getField(CommandConst.PLUGIN_ID, String.class);
	}

	public Command setPluginAuthKey(String authKey) {
		this.fields.put(CommandConst.PLUGIN_AUTH_KEY, authKey);
		return this;
	}

	public String getPluginAuthKey() {
		return this.getField(CommandConst.PLUGIN_AUTH_KEY, String.class);
	}

	public Command setMsgType(int msgType) {
		this.fields.put(CommandConst.MSG_TYPE, msgType);
		return this;
	}

	public int getMsgType() {
		return this.getField(CommandConst.MSG_TYPE, Integer.class);
	}

	public Command setStartTime(long time) {
		this.fields.put(CommandConst.START_TIME, time);
		return this;
	}

	public long getStartTime() {
		Long time = this.getField(CommandConst.START_TIME, Long.class);
		return time == null ? 0 : time;
	}

	public Command setEndTime(long time) {
		this.fields.put(CommandConst.END_TIME, time);
		return this;
	}

	public long getEndTime() {
		Long time = this.getField(CommandConst.END_TIME, Long.class);
		return time == null ? 0 : time;
	}

	public Command setChannelSession(ChannelSession channelSession) {
		this.fields.put(CommandConst.CHAHHEL_SESSION, channelSession);
		return this;
	}

	public ChannelSession getChannelSession() {
		return this.getField(CommandConst.CHAHHEL_SESSION, ChannelSession.class);
	}

	public Command setChannelContext(ChannelHandlerContext channelContext) {
		this.fields.put(CommandConst.CHANNEL_CONTEXT, channelContext);
		return this;
	}

	public ChannelHandlerContext getChannelContext() {
		return this.getField(CommandConst.CHANNEL_CONTEXT, ChannelHandlerContext.class);
	}

	public CommandResponse getResponse() {
		return response;
	}

	public void setResponse(CommandResponse response) {
		this.response = response;
	}

	public String toString() {
		return StringHelper.format("cmd: siteUserId={} deviceId={} rety={} service={} method={} header={}",
				this.siteUserId, this.deviceId, this.rety, this.service, this.method, this.header);
	}

}
