package com.akaxin.admin.site.service;

public interface IMonitorService {
    int queryNumRegisterPerDay(long now);

    int queryNumMessagePerDay(long now);

    int queryGroupMessagePerDay(long now);

    int queryU2MessagePerDay(long now);

    int getSiteUserNum();

    int getGroupNum();
}
