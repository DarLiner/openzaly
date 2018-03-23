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

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.ErrorCode2;
import com.akaxin.common.utils.StringHelper;
import com.akaxin.proto.core.PluginProto;
import com.akaxin.proto.plugin.HaiPluginAddProto;
import com.akaxin.proto.plugin.HaiPluginDeleteProto;
import com.akaxin.proto.plugin.HaiPluginListProto;
import com.akaxin.proto.plugin.HaiPluginProfileProto;
import com.akaxin.proto.plugin.HaiPluginUpdateProto;
import com.akaxin.site.business.dao.SitePluginDao;
import com.akaxin.site.business.impl.AbstractRequest;
import com.akaxin.site.business.utils.StringRandomUtils;
import com.akaxin.site.storage.bean.PluginBean;

/**
 * hai接口，扩展管理服务
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-10 18:59:10
 */
public class HttpPluginService extends AbstractRequest {
	private static final Logger logger = LoggerFactory.getLogger(HttpPluginService.class);

	/**
	 * 申请添加好友
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse add(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		ErrorCode2 errorCode = ErrorCode2.ERROR;
		try {
			HaiPluginAddProto.HaiPluginAddRequest request = HaiPluginAddProto.HaiPluginAddRequest
					.parseFrom(command.getParams());
			logger.info("/hai/plugin/add command={},request={}", command.toString(), request.toString());

			PluginBean bean = new PluginBean();
			bean.setName(request.getPlugin().getName());
			bean.setIcon(request.getPlugin().getIcon());
			bean.setUrlPage(request.getPlugin().getUrlPage());
			bean.setApiUrl(request.getPlugin().getApiUrl());
			bean.setAllowedIp(request.getPlugin().getAllowedIp());
			bean.setPosition(request.getPlugin().getPositionValue());
			bean.setDisplayMode(PluginProto.PluginDisplayMode.NEW_PAGE_VALUE);
			bean.setPermissionStatus(request.getPlugin().getPermissionStatusValue());
			bean.setAddTime(System.currentTimeMillis());
			// 随机生成64位的字符串
			bean.setAuthKey(StringHelper.generateRandomString(16));
			
			if (SitePluginDao.getInstance().addPlugin(bean)) {
				errorCode = ErrorCode2.SUCCESS;
			}
		} catch (Exception e) {
			errorCode = ErrorCode2.ERROR_SYSTEMERROR;
			logger.error("add plugin error.", e);
		}
		logger.info("/hai/plugin/add result={}", errorCode.toString());
		return commandResponse.setErrCode2(errorCode);
	}

	/**
	 * 删除扩展，直接从数据库中删除数据
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse delete(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		ErrorCode2 errorCode = ErrorCode2.ERROR;
		try {
			HaiPluginDeleteProto.HaiPluginDeleteRequest request = HaiPluginDeleteProto.HaiPluginDeleteRequest
					.parseFrom(command.getParams());
			String pluginId = request.getPluginId();
			logger.info("/hai/plugin/delete command={} request={}", command.toString(), request.toString());

			if (StringUtils.isNotBlank(pluginId)) {
				if (SitePluginDao.getInstance().deletePlugin(Integer.valueOf(pluginId))) {
					errorCode = ErrorCode2.SUCCESS;
				}
			} else {
				errorCode = ErrorCode2.ERROR_PARAMETER;
			}
		} catch (Exception e) {
			errorCode = ErrorCode2.ERROR_SYSTEMERROR;
			logger.error("delete plugin error.", e);
		}

		logger.info("/hai/plugin/delete result={}", errorCode.toString());
		return commandResponse.setErrCode2(errorCode);
	}

	/**
	 * 分页获取扩展的列表
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse list(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		ErrorCode2 errorCode = ErrorCode2.ERROR;
		try {
			HaiPluginListProto.HaiPluginListRequest request = HaiPluginListProto.HaiPluginListRequest
					.parseFrom(command.getParams());
			int pageNum = request.getPageNumber();
			int pageSize = request.getPageSize();
			logger.info("/hai/plugin/list command={} request={}", command.toString(), request.toString());

			List<PluginBean> pluginList = SitePluginDao.getInstance().getAllPluginList(pageNum, pageSize);

			if (pluginList != null) {
				HaiPluginListProto.HaiPluginListResponse.Builder responseBuilder = HaiPluginListProto.HaiPluginListResponse
						.newBuilder();
				for (PluginBean bean : pluginList) {
					responseBuilder.addPlugin(getPluginProfile(bean));
				}
				commandResponse.setParams(responseBuilder.build().toByteArray());
				errorCode = ErrorCode2.SUCCESS;
			}
		} catch (Exception e) {
			errorCode = ErrorCode2.ERROR_SYSTEMERROR;
			logger.error("/hai/plugin/list error.", e);
		}
		logger.info("/hai/plugin/list result={}", errorCode.toString());
		return commandResponse.setErrCode2(errorCode);
	}

	/**
	 * 获取扩展的信息
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse profile(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		ErrorCode2 errorCode = ErrorCode2.ERROR;
		try {
			HaiPluginProfileProto.HaiPluginProfileRequest request = HaiPluginProfileProto.HaiPluginProfileRequest
					.parseFrom(command.getParams());
			String pluginId = request.getPluginId();
			logger.info("/hai/plugin/profile command={},request={}", command.toString(), request.toString());

			PluginBean bean = SitePluginDao.getInstance().getPluginProfile(Integer.valueOf(pluginId));
			if (bean != null) {
				HaiPluginProfileProto.HaiPluginProfileResponse response = HaiPluginProfileProto.HaiPluginProfileResponse
						.newBuilder().setPlugin(getPluginProfile(bean)).build();
				commandResponse.setParams(response.toByteArray());
				errorCode = ErrorCode2.SUCCESS;
			}
		} catch (Exception e) {
			errorCode = ErrorCode2.ERROR_SYSTEMERROR;
			logger.error("hai apply friend error.", e);
		}
		logger.info("/hai/plugin/profile result={}", errorCode.toString());
		return commandResponse.setErrCode2(errorCode);
	}

	/**
	 * 更新扩展的信息
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse update(Command command) {
		CommandResponse commandResponse = new CommandResponse();
		ErrorCode2 errorCode = ErrorCode2.ERROR;
		try {
			HaiPluginUpdateProto.HaiPluginUpdateRequest request = HaiPluginUpdateProto.HaiPluginUpdateRequest
					.parseFrom(command.getParams());
			logger.info("/hai/plugin/update command={},request={}", command.toString(), request.toString());
			PluginBean bean = new PluginBean();
			bean.setId(Integer.valueOf(request.getPlugin().getId()));
			bean.setName(request.getPlugin().getName());
			bean.setIcon(request.getPlugin().getIcon());
			bean.setUrlPage(request.getPlugin().getUrlPage());
			bean.setApiUrl(request.getPlugin().getApiUrl());
			bean.setAuthKey(request.getPlugin().getAuthKey());
			bean.setAllowedIp(request.getPlugin().getAllowedIp());
			bean.setSort(request.getPlugin().getOrder());
			bean.setPosition(request.getPlugin().getPositionValue());
			bean.setPermissionStatus(request.getPlugin().getPermissionStatusValue());

			if (SitePluginDao.getInstance().updatePlugin(bean)) {
				errorCode = ErrorCode2.SUCCESS;
			}
		} catch (Exception e) {
			errorCode = ErrorCode2.ERROR_SYSTEMERROR;
			logger.error("/hai/plugin/update error.", e);
		}
		logger.info("/hai/plugin/update result={}", errorCode.toString());
		return commandResponse.setErrCode2(errorCode);
	}

	private PluginProto.Plugin getPluginProfile(PluginBean bean) {
		PluginProto.Plugin.Builder pluginBuilder = PluginProto.Plugin.newBuilder();
		pluginBuilder.setId(String.valueOf(bean.getId()));
		if (StringUtils.isNotBlank(bean.getName())) {
			pluginBuilder.setName(bean.getName());
		}
		if (StringUtils.isNotBlank(bean.getIcon())) {
			pluginBuilder.setIcon(bean.getIcon());
		}
		if (StringUtils.isNotBlank(bean.getUrlPage())) {
			pluginBuilder.setUrlPage(bean.getUrlPage());
		}
		if (StringUtils.isNotBlank(bean.getApiUrl())) {
			pluginBuilder.setApiUrl(bean.getApiUrl());
		}
		if (StringUtils.isNotBlank(bean.getAuthKey())) {
			pluginBuilder.setAuthKey(bean.getAuthKey());
		}
		if (StringUtils.isNotBlank(bean.getAllowedIp())) {
			pluginBuilder.setAllowedIp(bean.getAllowedIp());
		}
		pluginBuilder.setOrder(bean.getSort());
		pluginBuilder.setPositionValue(bean.getPosition());
		pluginBuilder.setPermissionStatusValue(bean.getPermissionStatus());

		return pluginBuilder.build();
	}
}