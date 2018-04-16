package com.akaxin.admin.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.akaxin.admin.service.IUICService;
import com.akaxin.proto.core.UicProto.UicStatus;
import com.akaxin.site.business.dao.SiteUicDao;
import com.akaxin.site.storage.bean.UicBean;

@Service
public class UICManageService implements IUICService {
	private static final Logger logger = LoggerFactory.getLogger(UICManageService.class);

	@Override
	public boolean addUIC(int num) {
		try {
			UicBean bean = new UicBean();
			bean.setStatus(UicStatus.UNUSED_VALUE);
			bean.setCreateTime(System.currentTimeMillis());
			if (SiteUicDao.getInstance().batchAddUic(bean, num)) {
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
