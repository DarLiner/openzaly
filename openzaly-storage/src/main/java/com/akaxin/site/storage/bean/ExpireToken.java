package com.akaxin.site.storage.bean;

import com.akaxin.common.utils.GsonUtils;

/**
 * 站点token
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-12 15:03:42
 */
public class ExpireToken {
	private int id;
	private String token;
	private String bid;
	private int btype;
	private int status;
	// private String content;
	private long createTime;
	private long expireTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token
	 *            the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * @return the bid
	 */
	public String getBid() {
		return bid;
	}

	/**
	 * @param bid
	 *            the bid to set
	 */
	public void setBid(String bid) {
		this.bid = bid;
	}

	/**
	 * @return the btype
	 */
	public int getBtype() {
		return btype;
	}

	/**
	 * @param btype
	 *            the btype to set
	 */
	public void setBtype(int btype) {
		this.btype = btype;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return the createTime
	 */
	public long getCreateTime() {
		return createTime;
	}

	/**
	 * @param createTime
	 *            the createTime to set
	 */
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	/**
	 * @return the expireTime
	 */
	public long getExpireTime() {
		return expireTime;
	}

	/**
	 * @param expireTime
	 *            the expireTime to set
	 */
	public void setExpireTime(long expireTime) {
		this.expireTime = expireTime;
	}

	public String toString() {
		return GsonUtils.toJson(this);
	}
}
