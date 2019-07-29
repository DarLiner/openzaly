/** 
 * Copyright 2018-2028 Akaxin Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package com.akaxin.site.business.dao;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.site.storage.api.IUicDao;
import com.akaxin.site.storage.bean.UicBean;
import com.akaxin.site.storage.service.UicDaoService;

public class SiteUicDao {
	private static final Logger logger = LoggerFactory.getLogger(SiteUicDao.class);
	private IUicDao uicDao = new UicDaoService();
	private static SiteUicDao instance = new SiteUicDao();

	public static SiteUicDao getInstance() {
		return instance;
	}

	/**
	 * 新增扩展
	 * 
	 * @param bean
	 * @return
	 */
	public boolean addUic(UicBean bean) {
		try {
			return uicDao.addUic(bean);
		} catch (SQLException e) {
			logger.error("add uic error.", e);
		}
		return false;
	}

	/**
	 * 批量增加UIC
	 * 
	 * @param bean
	 * @param num
	 * @return
	 */
	public boolean batchAddUic(UicBean bean, int num, int length) {
		try {
			return uicDao.batchAddUic(bean, num, length);
		} catch (SQLException e) {
			logger.error("add uic error.", e);
		}
		return false;
	}

	public UicBean getUicInfo(String uic) {
		try {
			return uicDao.getUicInfo(uic);
		} catch (SQLException e) {
			logger.error("add uic error.", e);
		}
		return null;
	}

	public boolean updateUic(UicBean bean) {
		try {
			return uicDao.updateUic(bean);
		} catch (SQLException e) {
			logger.error("update plugin error.", e);
		}
		return false;
	}

	public List<UicBean> getUicList(int pageNum, int pageSize, int status) {
		List<UicBean> pluginList = null;
		try {
			pluginList = uicDao.getUicPageList(pageNum, pageSize, status);
		} catch (SQLException e) {
			logger.error("get plugin list error.", e);
		}
		return pluginList;
	}

	public List<UicBean> getAllUicList(int pageNum, int pageSize) {
		List<UicBean> uicList = null;
		try {
			uicList = uicDao.getAllUicPageList(pageNum, pageSize);
		} catch (SQLException e) {
			logger.error("get plugin list error.", e);
		}
		return uicList;
	}

}
