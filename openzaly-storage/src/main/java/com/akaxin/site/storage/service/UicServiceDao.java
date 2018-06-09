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
package com.akaxin.site.storage.service;

import java.sql.SQLException;
import java.util.List;

import com.akaxin.site.storage.api.IUicDao;
import com.akaxin.site.storage.bean.UicBean;
import com.akaxin.site.storage.dao.SiteUICDao;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-31 12:11:17
 */
public class UicServiceDao implements IUicDao {

	@Override
	public boolean addUic(UicBean bean) throws SQLException {
		return SiteUICDao.getInstance().addUIC(bean);
	}

	@Override
	public boolean batchAddUic(UicBean bean, int num, int length) throws SQLException {
		return SiteUICDao.getInstance().batchAddUIC(bean, num, length);
	}

	@Override
	public boolean updateUic(UicBean bean) throws SQLException {
		return SiteUICDao.getInstance().updateUIC(bean);
	}

	@Override
	public UicBean getUicInfo(String uic) throws SQLException {
		return SiteUICDao.getInstance().queryUIC(uic);
	}

	@Override
	public List<UicBean> getUicPageList(int pageNum, int pageSize, int status) throws SQLException {
		return SiteUICDao.getInstance().queryUicList(pageNum, pageSize, status);
	}

	@Override
	public List<UicBean> getAllUicPageList(int pageNum, int pageSize) throws SQLException {
		return SiteUICDao.getInstance().queryAllUicList(pageNum, pageSize);
	}

}
