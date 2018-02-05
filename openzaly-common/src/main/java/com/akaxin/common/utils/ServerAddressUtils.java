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

/**
 * <pre>
 * 	提供Netty启动的服务地址以及端口号
 * 
 * 	Address and Port of Netty server
 * </pre>
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-31 12:35:32
 */
public class ServerAddressUtils {

	private static String address = "0.0.0.0";
	private static int port = 2021; // default port

	public static String getAddress() {
		return address;
	}

	public static void setAddress(String address) {
		ServerAddressUtils.address = address;
	}

	public static void setPort(int currPort) {
		ServerAddressUtils.port = currPort;
	}

	public static int getPort() {
		return port;
	}

	public static String getAddressPort() {
		return address + ":" + port;
	}

}
