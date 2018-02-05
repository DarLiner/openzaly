package com.akaxin.site.storage.bean;

/**
 * 当前用户siteUserId，与继承的用户userId，之间的用户关系
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017-12-27 15:18:45
 */
public class SimpleUserRelationBean extends SimpleUserBean {

	private String siteUserId;
	private int relation;

	public String getSiteUserId() {
		return siteUserId;
	}

	public void setSiteUserId(String siteUserId) {
		this.siteUserId = siteUserId;
	}

	public int getRelation() {
		return relation;
	}

	public void setRelation(int relation) {
		this.relation = relation;
	}

}
