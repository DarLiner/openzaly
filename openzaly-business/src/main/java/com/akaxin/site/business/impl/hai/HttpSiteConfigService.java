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
package com.akaxin.site.business.impl.hai;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.ErrorCode2;
import com.akaxin.proto.core.ConfigProto;
import com.akaxin.proto.plugin.HaiSiteGetConfigProto;
import com.akaxin.proto.plugin.HaiSiteUpdateConfigProto;
import com.akaxin.site.business.dao.SiteConfigDao;
import com.akaxin.site.business.impl.AbstractRequest;
import com.akaxin.site.business.impl.site.SiteConfig;
import com.akaxin.site.message.utils.SiteConfigHelper;

/**
 * hai接口，提供对站点配置相关操作
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-08 14:22:52
 */
public class HttpSiteConfigService extends AbstractRequest {
	private static final Logger logger = LoggerFactory.getLogger(HttpSiteConfigService.class);

	/**
	 * 获取站点配置信息
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse getConfig(Command command) {
		logger.info("/hai/site/getConfig");
		CommandResponse commandResponse = new CommandResponse();
		ErrorCode2 errorCode = ErrorCode2.ERROR;
		try {
			logger.info("/hai/site/getConfig command={}", command.toString());

			Map<Integer, String> configMap = SiteConfig.getConfigMap();
			ConfigProto.SiteBackConfig config = ConfigProto.SiteBackConfig.newBuilder().putAllSiteConfig(configMap)
					.build();
			HaiSiteGetConfigProto.HaiSiteGetConfigResponse response = HaiSiteGetConfigProto.HaiSiteGetConfigResponse
					.newBuilder().setSiteConfig(config).build();
			commandResponse.setParams(response.toByteArray());
			errorCode = ErrorCode2.SUCCESS;
		} catch (Exception e) {
			errorCode = ErrorCode2.ERROR_SYSTEMERROR;
			logger.error("hai get config error", e);
		}
		logger.info("/hai/site/getConfig result={}", errorCode.toString());
		return commandResponse.setErrCode2(errorCode);
	}

	/**
	 * 更新站点配置相关操作
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse updateConfig(Command command) {
		logger.info("/hai/site/updateConfig");
		CommandResponse commandResponse = new CommandResponse();
		ErrorCode2 errorCode = ErrorCode2.ERROR;
		try {
			HaiSiteUpdateConfigProto.HaiSiteUpdateConfigRequest request = HaiSiteUpdateConfigProto.HaiSiteUpdateConfigRequest
					.parseFrom(command.getParams());
			Map<Integer, String> configMap = request.getSiteConfig().getSiteConfigMap();
			logger.info("/hai/site/updateConfig command={} request={}", command.toString(), request.toString());

			if (configMap != null) {
				if (SiteConfigDao.getInstance().updateSiteConfig(configMap)) {
					errorCode = ErrorCode2.SUCCESS;
					SiteConfig.updateConfig();
					SiteConfigHelper.updateConfig();
				}
			} else {
				errorCode = ErrorCode2.ERROR_PARAMETER;
			}

		} catch (Exception e) {
			errorCode = ErrorCode2.ERROR_SYSTEMERROR;
			logger.error("hai update config proto error.", e);
		}
		logger.info("/hai/site/updateConfig result={}", errorCode.toString());
		return commandResponse.setErrCode2(errorCode);
	}
}
