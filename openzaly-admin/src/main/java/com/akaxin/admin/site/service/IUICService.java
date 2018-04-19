package com.akaxin.admin.site.service;

import java.util.List;

import com.akaxin.site.storage.bean.UicBean;

public interface IUICService {

	boolean addUIC(int num);

	List<UicBean> getUsedUicList(int pageNum, int pageSize, int status);

}
