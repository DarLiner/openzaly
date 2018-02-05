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
package com.akaxin.common.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * address = {host}:{port}
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-30 11:07:21
 */
public class ServerAddress {
	private String host = "0.0.0.0";
	private int port = 2021; // default port

	public ServerAddress(String address) {
		this.setAddress(address);
	}

	public ServerAddress setAddress(String address) {
		if (StringUtils.isNoneBlank(address)) {
			// InetAddress netAdd =InetAddress.getByName(address);
			String addrs[] = address.split(":");
			if (addrs.length == 1) {
				this.host = address;
			} else if (addrs.length == 2) {
				this.host = addrs[0];
				this.port = Integer.parseInt(addrs[1]);
			}
		}
		return this;
	}

	public String getAddress() {
		if (this.port == 2021) {
			return this.host;
		}
		return this.host + ":" + this.port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isRightAddress() {
		return !"0.0.0.0".equals(this.host);
	}
}
