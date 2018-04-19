package com.akaxin.site.business.dao;

import com.akaxin.site.storage.api.IMessageDao;
import com.akaxin.site.storage.service.MessageDaoService;
import org.slf4j.LoggerFactory;

public class UserMessageDao {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UserProfileDao.class);
    private static UserMessageDao instance = new UserMessageDao();
    private IMessageDao messageDao = new MessageDaoService();
    public static UserMessageDao getInstance() {
        return instance;
    }

}
