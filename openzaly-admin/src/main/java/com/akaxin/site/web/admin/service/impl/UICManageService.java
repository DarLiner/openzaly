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
package com.akaxin.site.web.admin.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.akaxin.proto.core.UicProto.UicStatus;
import com.akaxin.site.business.dao.SiteUicDao;
import com.akaxin.site.storage.bean.UicBean;
import com.akaxin.site.web.admin.service.IUICService;

@Service
public class UICManageService implements IUICService {
	private static final Logger logger = LoggerFactory.getLogger(UICManageService.class);

	@Override
	public boolean addUIC(int num, int length) {
		try {
			UicBean bean = new UicBean();
			bean.setStatus(UicStatus.UNUSED_VALUE);
			bean.setCreateTime(System.currentTimeMillis());
			if (SiteUicDao.getInstance().batchAddUic(bean, num, length)) {
				return true;
			}
		} catch (Exception e) {
			logger.error("add uic error", e);
		}
		return false;
	}

	@Override
	public List<UicBean> getUsedUicList(int pageNum, int pageSize, int status) {
		List<UicBean> uicList = SiteUicDao.getInstance().getUicList(pageNum, pageSize, status);
		return uicList;
	}

}
