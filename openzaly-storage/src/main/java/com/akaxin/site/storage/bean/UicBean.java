package com.akaxin.site.storage.bean;

import com.akaxin.common.utils.GsonUtils;

/**
 * 用户注册/登陆站点，需要使用的邀请码
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-12 15:03:42
 */
public class UicBean {
	private int id;
	private String uic;
	private String siteUserId;
	private String userName;
	private int status;
	private long createTime;
	private long useTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUic() {
		return uic;
	}

	public void setUic(String uic) {
		this.uic = uic;
	}

	public String getSiteUserId() {
		return siteUserId;
	}

	public void setSiteUserId(String siteUserId) {
		this.siteUserId = siteUserId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long getUseTime() {
		return useTime;
	}

	public void setUseTime(long useTime) {
		this.useTime = useTime;
	}

	public String toString() {
		return GsonUtils.toJson(this);
	}
}
