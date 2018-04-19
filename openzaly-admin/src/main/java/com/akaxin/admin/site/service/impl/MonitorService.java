package com.akaxin.admin.site.service.impl;

import com.akaxin.admin.site.service.IMonitorService;
import com.akaxin.site.business.dao.MonitorDao;
import org.springframework.stereotype.Service;

@Service
public class MonitorService implements IMonitorService {

    private MonitorDao monitorDao = MonitorDao.getInstance();

    @Override
    public int queryNumRegisterPerDay(long now) {
        return monitorDao.queryNumRegisterPerDay(now);
    }

    @Override
    public int queryNumMessagePerDay(long now) {
        return monitorDao.queryNumMessagePerDay(now);
    }

    @Override
    public int queryGroupMessagePerDay(long now) {
        return monitorDao.queryGroupMessagePerDay(now);
    }

    @Override
    public int queryU2MessagePerDay(long now) {
        return monitorDao.queryU2MessagePerDay(now);
    }

    @Override
    public int getSiteUserNum() {
        return monitorDao.getSiteUserNum();
    }

    @Override
    public int getGroupNum() {
        return monitorDao.getGroupNum();
    }
}
