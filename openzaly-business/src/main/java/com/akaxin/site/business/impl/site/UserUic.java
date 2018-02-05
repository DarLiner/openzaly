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
package com.akaxin.site.business.impl.site;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.proto.core.UicProto;
import com.akaxin.site.business.dao.SiteUicDao;
import com.akaxin.site.storage.bean.UicBean;

/**
 * 用户邀请码管理实现
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-14 22:09:36
 */
public class UserUic {
	private static Logger logger = LoggerFactory.getLogger(UserUic.class);

	private UserUic() {
	}

	static class SingletonHolder {
		private static UserUic instance = new UserUic();
	}

	public static UserUic getInstance() {
		return SingletonHolder.instance;
	}

	public boolean updateUicUsed(String uic, String siteUserId) {
		UicBean bean = new UicBean();
		bean.setUic(uic);
		bean.setSiteUserId(siteUserId);
		bean.setStatus(UicProto.UicStatus.USED_VALUE);
		bean.setUseTime(System.currentTimeMillis());
		return SiteUicDao.getInstance().updateUic(bean);
	}

	/**
	 * <pre>
	 * 判断uic是否可用判断条件：
	 * 		1.查询的uic不为空
	 * 		2.uic未被用户使用
	 * 		3.uic的状态为UNUSED状态
	 * 
	 * </pre>
	 * 
	 * @param uic
	 *            用户邀请码
	 * @return
	 */
	public boolean checkUic(String uic, String siteUserId) {
		UicBean bean = SiteUicDao.getInstance().getUicInfo(uic);
		if (bean != null && StringUtils.isNotBlank(bean.getUic()) && StringUtils.isEmpty(bean.getSiteUserId())) {
			if (UicProto.UicStatus.UNUSED_VALUE == bean.getStatus()) {
				return updateUicUsed(uic, siteUserId);
			}
		}
		return false;
	}
}
