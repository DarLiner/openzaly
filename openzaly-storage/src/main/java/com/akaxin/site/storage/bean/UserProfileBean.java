package com.akaxin.site.storage.bean;

import java.util.zip.CRC32;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import com.akaxin.common.utils.GsonUtils;

public class UserProfileBean {
    private String siteUserId;
    private String globalUserId;// 全局的用户ID
    private String userIdPubk;
    private String userName;
    private String userPhoto;
    private String selfIntroduce;
    private String applyInfo;
    private String phoneId;
    private int userStatus;
    private long registerTime;
    private int defaultState;

    public int getDefaultState() {
        return defaultState;
    }

    public void setDefaultState(int defaultState) {
        this.defaultState = defaultState;
    }

    public String getGlobalUserId() {
        if (StringUtils.isEmpty(this.globalUserId)) {
            String body = this.userIdPubk;
            String SHA1UserPubKey = new String(Hex.encodeHex(DigestUtils.sha1(body)));

            CRC32 c32 = new CRC32();
            c32.update(body.getBytes(), 0, body.getBytes().length);
            String CRC32UserPubKey = String.valueOf(c32.getValue());

            return SHA1UserPubKey + "-" + CRC32UserPubKey;
        }
        return this.globalUserId;
    }

    public void setGlobalUserId(String globalUserId) {
        this.globalUserId = globalUserId;
    }

    public String getSiteUserId() {
        return siteUserId;
    }

    public void setSiteUserId(String siteUserId) {
        this.siteUserId = siteUserId;
    }

    public String getUserIdPubk() {
        return userIdPubk;
    }

    public void setUserIdPubk(String userIdPubk) {
        this.userIdPubk = userIdPubk;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getSelfIntroduce() {
        return selfIntroduce;
    }

    public void setSelfIntroduce(String selfIntroduce) {
        this.selfIntroduce = selfIntroduce;
    }

    public String getApplyInfo() {
        return applyInfo;
    }

    public void setApplyInfo(String applyInfo) {
        this.applyInfo = applyInfo;
    }

    public String getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(String phoneId) {
        this.phoneId = phoneId;
    }

    public long getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(long registerTime) {
        this.registerTime = registerTime;
    }

    public int getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(int userStatus) {
        this.userStatus = userStatus;
    }

    public String toString() {
        return GsonUtils.toJson(this);
    }
}
