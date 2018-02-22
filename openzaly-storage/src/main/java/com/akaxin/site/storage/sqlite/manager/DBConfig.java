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
package com.akaxin.site.storage.sqlite.manager;

import java.util.HashMap;
import java.util.Map;

import com.akaxin.common.utils.GsonUtils;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-31 12:13:35
 */
public class DBConfig {
	private String dbDir;
	private String adminServerName;
	private String adminAddress;
	private int adminPort;
	private String adminIcon;
	private String adminUic;
	private String adminApi = "/manage/index";
	private Map<Integer, String> configMap;
	// 扩展参数
	private Map<String, Object> params = new HashMap<String, Object>();

	public String getDbDir() {
		return dbDir;
	}

	public void setDbDir(String dbDir) {
		this.dbDir = dbDir;
	}

	public String getAdminServerName() {
		return adminServerName;
	}

	public void setAdminServerName(String adminServerName) {
		this.adminServerName = adminServerName;
	}

	public String getAdminAddress() {
		return adminAddress;
	}

	public void setAdminAddress(String adminAddress) {
		this.adminAddress = adminAddress;
	}

	public int getAdminPort() {
		return adminPort;
	}

	public void setAdminPort(int adminPort) {
		this.adminPort = adminPort;
	}

	public String getAdminIcon() {
		return adminIcon;
	}

	public void setAdminIcon(String adminIcon) {
		this.adminIcon = adminIcon;
	}

	public String getSiteServer() {
		return adminAddress + ":" + adminPort;
	}

	public String getAdminUic() {
		return adminUic;
	}

	public void setAdminUic(String adminUic) {
		this.adminUic = adminUic;
	}

	public String getAdminApi() {
		return adminApi;
	}

	public void setAdminApi(String adminApi) {
		this.adminApi = adminApi;
	}

	public Map<Integer, String> getConfigMap() {
		return configMap;
	}

	public void setConfigMap(Map<Integer, String> configMap) {
		this.configMap = configMap;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public DBConfig setParams(Map<String, Object> map) {
		this.params.putAll(map);
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T> T getParam(String k, Class<T> t) {
		Object obj = params.get(k);
		return obj == null ? null : (T) obj;
	}

	public DBConfig setParam(String k, Object v) {
		this.params.put(k, v);
		return this;
	}

	public String toString() {
		return GsonUtils.toJson(this);
	}
}
