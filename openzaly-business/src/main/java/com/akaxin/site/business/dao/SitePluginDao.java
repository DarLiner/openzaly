/**
 * Copyright 2018-2028 Akaxin Group
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
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

import com.akaxin.proto.core.PluginProto;
import com.akaxin.site.storage.api.IPluginDao;
import com.akaxin.site.storage.bean.PluginBean;
import com.akaxin.site.storage.service.PluginServiceDao;

public class SitePluginDao {
    private static final Logger logger = LoggerFactory.getLogger(SitePluginDao.class);
    private IPluginDao pluginDao = new PluginServiceDao();
    private static SitePluginDao instance = new SitePluginDao();

    public static SitePluginDao getInstance() {
        return instance;
    }

    /**
     * 新增扩展
     *
     * @param bean
     * @return
     */
    public boolean addPlugin(PluginBean bean) {
        try {
            return pluginDao.addPlugin(bean);
        } catch (SQLException e) {
            logger.error("add plugin error.", e);
        }
        return false;
    }

    /**
     * 删除扩展
     *
     * @param pluginId
     * @return
     */
    public boolean deletePlugin(int pluginId) {
        try {
            return pluginDao.deletePlugin(pluginId);
        } catch (SQLException e) {
            logger.error("delete plugin error.", e);
        }
        return false;
    }

    public boolean updatePlugin(PluginBean bean) {
        try {
            return pluginDao.updatePlugin(bean);
        } catch (SQLException e) {
            logger.error("update plugin error.", e);
        }
        return false;
    }

    /**
     * 获取管理员的扩展列表
     *
     * @param pageNum
     * @param pageSize
     * @param position
     * @return
     */
    public List<PluginBean> getAdminPluginPageList(int pageNum, int pageSize, int position) {
        List<PluginBean> pluginList = null;
        try {
            pluginList = pluginDao.getPluginPageList(pageNum, pageSize, position);
        } catch (SQLException e) {
            logger.error("get plugin list error.", e);
        }
        return pluginList;
    }

    /**
     * 获取普通用户的扩展列表
     *
     * @param pageNum
     * @param pageSize
     * @param position
     * @return
     */
    public List<PluginBean> getOrdinaryPluginPageList(int pageNum, int pageSize, int position) {
        List<PluginBean> pluginList = null;
        try {
            pluginList = pluginDao.getPluginPageList(pageNum, pageSize, position,
                    PluginProto.PermissionStatus.AVAILABLE_VALUE);
        } catch (SQLException e) {
            logger.error("get plugin list error.", e);
        }
        return pluginList;
    }

    /**
     * 获取所有扩展列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public List<PluginBean> getAllPluginList(int pageNum, int pageSize) {
        List<PluginBean> pluginList = null;
        try {
            pluginList = pluginDao.getAllPluginList(pageNum, pageSize);
        } catch (SQLException e) {
            logger.error("get plugin list error.", e);
        }
        return pluginList;
    }

    public PluginBean getPluginProfile(int pluginId) {
        try {
            return pluginDao.getPluginProfile(pluginId);
        } catch (SQLException e) {
            logger.error("get plugin by id error.", e);
        }
        return null;
    }

    public String reSetAuthKey(int pluginId) {
        try {
            return pluginDao.reSetAuthKey(pluginId);
        } catch (SQLException e) {
            logger.error("reset AuthKey error.", e);
        }
        return "false";
    }
}
