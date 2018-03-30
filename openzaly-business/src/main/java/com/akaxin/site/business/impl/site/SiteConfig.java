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

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.constant.ConfigConst;
import com.akaxin.proto.core.ConfigProto;
import com.akaxin.site.business.constant.GroupConfig;
import com.akaxin.site.business.dao.SiteConfigDao;

/**
 * 管理站点配置相关信息
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-14 21:18:49
 */
public class SiteConfig {
	private static final Logger logger = LoggerFactory.getLogger(SiteConfig.class);

	private static volatile Map<Integer, String> configMap;

	private SiteConfig() {
	}

	public static Map<Integer, String> getConfigMap() {
		if (configMap == null) {
			configMap = SiteConfigDao.getInstance().getSiteConfig();
		}
		return configMap;
	}

	public static Map<Integer, String> updateConfig() {
		try {
			configMap = SiteConfigDao.getInstance().getSiteConfig();
		} catch (Exception e) {
			logger.error("update site config error.", e);
		}
		return configMap;
	}

	public static String getConfig(int key) {
		try {
			return getConfigMap().get(key);
		} catch (Exception e) {
			logger.error("get config value error", e);
		}
		return null;
	}

	/**
	 * <pre>
	 * 站点是否关闭，关闭的站点，用户不能注册以及登陆功能
	 * 		1.siteStatus=0,站点关闭
	 * 		2.siteStatus=1,站点开启
	 * </pre>
	 * 
	 * @return true/false
	 */
	public static boolean isOpen() {
		if (getConfigMap() != null) {
			String value = getConfigMap().get(ConfigProto.ConfigKey.SITE_STATUS_VALUE);

			if (StringUtils.isNumeric(value)) {
				return ConfigProto.SiteStatus.OPEN_VALUE == Integer.valueOf(value);
			}
		}
		return false;
	}

	/**
	 * 判断站点是否关闭状态
	 * 
	 * @return
	 */
	public static boolean isClose() {
		return !isOpen();
	}

	/**
	 * 获取用户的注册方式，通过后台管理进行修改用户注册方式
	 * 
	 * @return
	 */
	public static ConfigProto.RegisterWay getRegisterWay() {
		ConfigProto.RegisterWay regway = null;
		if (getConfigMap() != null) {
			String value = getConfigMap().get(ConfigProto.ConfigKey.REGISTER_WAY_VALUE);
			if (StringUtils.isNumeric(value)) {
				regway = ConfigProto.RegisterWay.forNumber(Integer.valueOf(value));
			}
		}
		return regway == null ? ConfigProto.RegisterWay.ANONYMOUS : regway;
	}

	public static String getSiteAdmin() {
		if (getConfigMap() != null) {
			return getConfigMap().get(ConfigProto.ConfigKey.SITE_ADMIN_VALUE);
		}
		return null;
	}

	public static boolean hasNoAdminUser() {
		return !hasAdminUser();
	}

	public static boolean hasAdminUser() {
		String adminUser = SiteConfig.getSiteAdmin();
		logger.info("======= site admin user adminUser={} ", adminUser);
		if (StringUtils.isNotEmpty(adminUser) && !ConfigConst.DEFAULT_SITE_ADMIN.equals(adminUser)) {
			return true;
		}
		return false;
	}

	public static int getMaxGroupMemberSize() {
		try {
			Map<Integer, String> map = getConfigMap();
			if (map != null) {
				String memberCount = map.get(ConfigProto.ConfigKey.GROUP_MEMBERS_COUNT_VALUE);
				if (StringUtils.isNumeric(memberCount)) {
					return Integer.valueOf(memberCount);
				}
			}
		} catch (Exception e) {
			logger.error("get max group member size error.", e);
		}
		return GroupConfig.GROUP_MAX_MEMBER_COUNT;
	}

	public static ConfigProto.U2EncryptionStatus getU2EncryStatus() {
		try {
			Map<Integer, String> map = getConfigMap();
			if (map != null) {
				String statusNum = map.get(ConfigProto.ConfigKey.U2_ENCRYPTION_STATUS_VALUE);
				if (StringUtils.isNumeric(statusNum)) {
					return ConfigProto.U2EncryptionStatus.forNumber(Integer.valueOf(statusNum));
				}
			}
		} catch (Exception e) {
			logger.error("get u2 encry status error", e);
		}
		return ConfigProto.U2EncryptionStatus.U2_CLOSE;
	}
}