package com.akaxin.site.admin.service;

public interface IMonitorService {
    int queryNumRegisterPerDay(long now, int day);

    int queryNumMessagePerDay(long now, int day);

    int queryGroupMessagePerDay(long now, int day);

    int queryU2MessagePerDay(long now, int day);

    int getSiteUserNum(long now,int day);

    int getGroupNum(long now,int day);

    int friendNum(long now,int day);
}
