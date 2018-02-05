package com.akaxin.site.storage.bean;

import com.akaxin.common.utils.GsonUtils;

public class PluginBean {
	private int id;
	private String name;
	private String urlPage;
	private String urlApi;
	private String icon;
	private String authKey;
	private String allowedIp;
	private int status;// 0:可用状态，1:禁用状态
	private int sort;
	private long addTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrlPage() {
		return urlPage;
	}

	public void setUrlPage(String urlPage) {
		this.urlPage = urlPage;
	}

	public String getUrlApi() {
		return urlApi;
	}

	public void setUrlApi(String urlApi) {
		this.urlApi = urlApi;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getAuthKey() {
		return authKey;
	}

	public void setAuthKey(String authKey) {
		this.authKey = authKey;
	}

	public String getAllowedIp() {
		return allowedIp;
	}

	public void setAllowedIp(String allowedIp) {
		this.allowedIp = allowedIp;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public long getAddTime() {
		return addTime;
	}

	public void setAddTime(long addTime) {
		this.addTime = addTime;
	}

	public String toString() {
		return GsonUtils.toJson(this);
	}
}
