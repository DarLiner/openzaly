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
package com.akaxin.site.web.admin.service.impl;

import java.util.List;
import java.util.Map;

import com.akaxin.site.storage.bean.SimpleUserBean;
import com.akaxin.site.storage.bean.UserProfileBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.akaxin.site.business.dao.SiteConfigDao;
import com.akaxin.site.business.impl.site.SiteConfig;
import com.akaxin.site.web.admin.service.IBasicService;

@Service
public class BasicManageService implements IBasicService {
    private static final Logger logger = LoggerFactory.getLogger(BasicManageService.class);

    @Override
    public Map<Integer, String> getSiteConfig() {
        Map<Integer, String> configMap = SiteConfig.getConfigMap();
        return configMap;
    }

    @Override
    public boolean updateSiteConfig(String siteUserId, Map<Integer, String> config) {
        boolean isAdmin = SiteConfig.isSiteSuperAdmin(siteUserId);
        boolean result = SiteConfigDao.getInstance().updateSiteConfig(config, isAdmin);
        SiteConfig.updateConfig();
        logger.info("siteUserId={} isAdmin={} update configMap={} result={}", siteUserId, isAdmin, config, result);
        return result;
    }

    @Override
    public boolean setUserDefault(String site_user_id) {
        List<String> defaultList = SiteConfigDao.getInstance().getUserDefault();
        if (defaultList != null && defaultList.size() >= 5) {
            return false;
        }
        boolean b = SiteConfigDao.getInstance().updateUserDefault(site_user_id);
        if (b) {
            return b;
        }
        return SiteConfigDao.getInstance().setUserDefault(site_user_id);
    }

    @Override
    public boolean delUserDefault(String site_user_id) {
        List<String> userDefault = SiteConfigDao.getInstance().getUserDefault();
        StringBuffer stringBuffer = new StringBuffer();
        if (userDefault.contains(site_user_id)) {
            userDefault.remove(site_user_id);
            for (String s : userDefault) {
                stringBuffer.append(s);
                stringBuffer.append(",");
            }
            if (userDefault.size() == 0) {
                String del = null;
                return SiteConfigDao.getInstance().delUserDefault(del);
            }
            int i = stringBuffer.lastIndexOf(",");
            stringBuffer.delete(i, i + 1);
            String s = stringBuffer.toString();
            return SiteConfigDao.getInstance().delUserDefault(s);
        }

        return false;
    }

    @Override
    public List<String> getUserDefault() {
        return SiteConfigDao.getInstance().getUserDefault();
    }

    @Override
    public List<String> getGroupDefault() {
        return SiteConfigDao.getInstance().getGroupDefault();
    }

}
