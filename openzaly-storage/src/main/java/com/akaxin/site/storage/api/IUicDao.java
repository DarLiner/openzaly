package com.akaxin.site.storage.api;

import java.sql.SQLException;
import java.util.List;

import com.akaxin.site.storage.bean.UicBean;

public interface IUicDao {

	public boolean addUic(UicBean bean) throws SQLException;

	public boolean updateUic(UicBean bean) throws SQLException;

	public UicBean getUicInfo(String uic) throws SQLException;

	public List<UicBean> getUicPageList(int pageNum, int pageSize, int status) throws SQLException;

	List<UicBean> getAllUicPageList(int pageNum, int pageSize) throws SQLException;

}
