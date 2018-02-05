package com.akaxin.site.storage.bean;

/**
 * 群成员bean
 * 
 * @author Sam
 * @since 2017.10.25
 *
 */
public class GroupMemberBean extends SimpleUserBean {
	private int userRole;

	public int getUserRole() {
		return userRole;
	}

	public void setUserRole(int userRole) {
		this.userRole = userRole;
	}

}
