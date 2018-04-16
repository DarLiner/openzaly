package com.akaxin.admin.service.impl;

import com.akaxin.admin.service.ManageService;
import com.akaxin.site.business.impl.site.SiteConfig;

import java.util.Map;

public class ManageServiceImpl implements ManageService {

    @Override
    public Map<Integer, String> getSiteConfig() {
        Map<Integer, String> configMap = SiteConfig.getConfigMap();
        return configMap;
    }
}
