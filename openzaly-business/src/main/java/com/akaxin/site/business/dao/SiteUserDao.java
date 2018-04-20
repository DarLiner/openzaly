package com.akaxin.site.business.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.site.storage.api.ISiteUsersDao;
import com.akaxin.site.storage.service.SiteUsersDaoService;

/**
 * 获取站点数据库信息，例如所有用户
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-04-20 18:27:46
 */
public class SiteUserDao {
	private static final Logger logger = LoggerFactory.getLogger(SiteUserDao.class);
	private ISiteUsersDao siteUsersDao = new SiteUsersDaoService();

	private static class SingletonHolder {
		private static SiteUserDao instance = new SiteUserDao();
	}

	public static SiteUserDao getInstance() {
		return SingletonHolder.instance;
	}

	public List<String> getSiteUsersByPage(int pageNum, int pageSize) {
		try {
			return siteUsersDao.getSiteUserByPage(pageNum, pageSize);
		} catch (Exception e) {
			logger.error("get site user by page error", e);
		}
		return null;
	}

}
