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

import com.akaxin.common.constant.ErrorCode2;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-31 12:27:26
 */
public class CommandResponse {
	private String version;
	private String action;
	private byte[] params;

	private Map<Integer, String> header = new HashMap<Integer, String>();
	private String errCode;
	private String errInfo = "";

	public String getVersion() {
		return version;
	}

	public CommandResponse setVersion(String version) {
		this.version = version;
		return this;
	}

	public String getAction() {
		return action;
	}

	public CommandResponse setAction(String action) {
		this.action = action;
		return this;
	}

	public byte[] getParams() {
		return params;
	}

	public CommandResponse setParams(byte[] params) {
		this.params = params;
		return this;
	}

	public Map<Integer, String> getHeader() {
		return header;
	}

	public CommandResponse setHeader(Map<Integer, String> header) {
		this.header = header;
		return this;
	}

	public String getErrCode() {
		return errCode;
	}

	public CommandResponse setErrCode(String errCode) {
		this.errCode = errCode;
		return this;
	}

	public CommandResponse setErrCode2(ErrorCode2 errCode2) {
		this.errCode = errCode2.getCode();
		this.errInfo = errCode2.getInfo();
		return this;
	}

	public String getErrInfo() {
		return errInfo;
	}

	public CommandResponse setErrInfo(String errInfo) {
		this.errInfo = errInfo;
		return this;
	}

	public String toString() {
		return "[version=" + this.version + ",action=" + this.action + ",data=" + new String(this.params) + "]";
	}

}
