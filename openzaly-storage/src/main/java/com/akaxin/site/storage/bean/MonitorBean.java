package com.akaxin.site.storage.bean;

public class MonitorBean {
    private int registerNum;
    private int messageNum;
    private int groupMsgNum;
    private int u2MsgNum;
    private int userNum;
    private int groupNum;
    private int friendNum;

    public MonitorBean(int registerNum, int messageNum, int groupMsgNum, int u2MsgNum, int userNum, int groupNum, int friendNum) {
        this.registerNum = registerNum;
        this.messageNum = messageNum;
        this.groupMsgNum = groupMsgNum;
        this.u2MsgNum = u2MsgNum;
        this.userNum = userNum;
        this.groupNum = groupNum;
        this.friendNum = friendNum;
    }
    public MonitorBean() {}

    public int getRegisterNum() {
        return registerNum;
    }

    public void setRegisterNum(int registerNum) {
        this.registerNum = registerNum;
    }

    public int getMessageNum() {
        return messageNum;
    }

    public void setMessageNum(int messageNum) {
        this.messageNum = messageNum;
    }

    public int getGroupMsgNum() {
        return groupMsgNum;
    }

    public void setGroupMsgNum(int groupMsgNum) {
        this.groupMsgNum = groupMsgNum;
    }

    public int getU2MsgNum() {
        return u2MsgNum;
    }

    public void setU2MsgNum(int u2MsgNum) {
        this.u2MsgNum = u2MsgNum;
    }

    public int getUserNum() {
        return userNum;
    }

    public void setUserNum(int userNum) {
        this.userNum = userNum;
    }

    public int getGroupNum() {
        return groupNum;
    }

    public void setGroupNum(int groupNum) {
        this.groupNum = groupNum;
    }

    public int getFriendNum() {
        return friendNum;
    }

    public void setFriendNum(int friendNum) {
        this.friendNum = friendNum;
    }

    public MonitorBean(int registerNum) {

        this.registerNum = registerNum;
    }
}
