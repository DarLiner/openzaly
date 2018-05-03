package com.akaxin.site.admin.service.impl;

import com.akaxin.site.admin.service.IMonitorService;
import com.akaxin.site.business.dao.MonitorDao;
import org.springframework.stereotype.Service;

@Service
public class MonitorService implements IMonitorService {

    private MonitorDao monitorDao = MonitorDao.getInstance();

    @Override
    public int queryNumRegisterPerDay(long now, int day) {
        return monitorDao.queryNumRegisterPerDay(now, day);
    }

    @Override
    public int queryNumMessagePerDay(long now, int day) {
        return monitorDao.queryNumMessagePerDay(now, day);
    }

    @Override
    public int queryGroupMessagePerDay(long now, int day) {
        return monitorDao.queryGroupMessagePerDay(now, day);
    }

    @Override
    public int queryU2MessagePerDay(long now, int day) {
        return monitorDao.queryU2MessagePerDay(now, day);
    }

    @Override
    public int getSiteUserNum(long now, int day) {
        return monitorDao.getSiteUserNum(now, day);
    }

    @Override
    public int getGroupNum(long now, int day) {
        return monitorDao.getGroupNum(now, day);
    }

    @Override
    public int friendNum(long now, int day) {
        return monitorDao.friendNum(now, day);
    }
}
