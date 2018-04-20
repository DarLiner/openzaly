package com.akaxin.site.storage.api;

import java.sql.SQLException;
import java.util.List;

public interface ISiteUsersDao {

	public List<String> getSiteUserByPage(int pageNum, int pageSize) throws SQLException;

}
