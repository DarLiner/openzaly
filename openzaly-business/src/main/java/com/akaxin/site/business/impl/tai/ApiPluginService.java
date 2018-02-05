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
package com.akaxin.site.business.impl.tai;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.constant.ErrorCode2;
import com.akaxin.common.http.ZalyHttpClient;
import com.akaxin.proto.core.PluginProto;
import com.akaxin.proto.site.ApiPluginListProto;
import com.akaxin.proto.site.ApiPluginPageProto;
import com.akaxin.proto.site.ApiPluginProxyProto;
import com.akaxin.site.business.dao.SitePluginDao;
import com.akaxin.site.business.impl.AbstractRequest;
import com.akaxin.site.business.impl.site.SiteConfig;
import com.akaxin.site.storage.bean.PluginBean;
import com.google.protobuf.ByteString;

/**
 * 处理客户端与服务点扩展之间功能
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017-12-11 14:46:28
 */
public class ApiPluginService extends AbstractRequest {
	private static Logger logger = LoggerFactory.getLogger(ApiPluginService.class);
	private static final String HTTP_PREFFIX = "http://";

	public CommandResponse list(Command command) {
		CommandResponse commandResponse = new CommandResponse().setAction(CommandConst.ACTION_RES);
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			ApiPluginListProto.ApiPluginListRequest request = ApiPluginListProto.ApiPluginListRequest
					.parseFrom(command.getParams());
			String siteUserId = command.getSiteUserId();
			int pageNumber = request.getPageNumber();
			int pageSize = request.getPageSize();
			int status = request.getStatusValue();
			logger.info("api.plugin.list command={} ,request={}", command.toString(), request.toString());

			String siteAdmin = SiteConfig.getSiteAdmin();
			logger.info("api.plugin.list siteAdmin={}", siteAdmin);

			if (PluginProto.PluginStatus.AVAILABLE_HOME_PAGE == request.getStatus()
					|| PluginProto.PluginStatus.AVAILABLE_MSG_PAGE == request.getStatus()) {
				if (pageNumber == 0 && pageSize == 0) {
					pageNumber = 1;
					pageSize = 4;
				}
				logger.info("api.plugin.list pageNum={} pageSize={} status={}", pageNumber, pageSize, status);

				List<PluginBean> pluginList = null;
				if (StringUtils.isNotBlank(siteUserId) && siteUserId.equals(siteAdmin)) {
					pluginList = SitePluginDao.getInstance().getPluginPageList(pageNumber, pageSize, status,
							status + 1);
				} else {
					pluginList = SitePluginDao.getInstance().getPluginPageList(pageNumber, pageSize, status);
				}

				if (pluginList != null) {
					ApiPluginListProto.ApiPluginListResponse.Builder responseBuilder = ApiPluginListProto.ApiPluginListResponse
							.newBuilder();
					for (PluginBean bean : pluginList) {
						responseBuilder.addPlugin(getPluginProfile(bean));
					}
					commandResponse.setParams(responseBuilder.build().toByteArray());
					errCode = ErrorCode2.SUCCESS;
				}
			} else {
				errCode = ErrorCode2.ERROR2_PLUGIN_STATUS;
			}
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			logger.error("api plugin list error.", e);
		}
		logger.info("api.plugin.list result={}", errCode.toString());
		return commandResponse.setErrCode2(errCode);
	}

	/**
	 * <pre>
	 * 代理前台客户端中扩展的请求
	 * 		1.界面请求后台一般使用http请求
	 * 		2.使用tcp代理，代替http请求
	 * </pre>
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse proxy(Command command) {
		CommandResponse commandResponse = new CommandResponse().setAction(CommandConst.ACTION_RES);
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			ApiPluginProxyProto.ApiPluginProxyRequest request = ApiPluginProxyProto.ApiPluginProxyRequest
					.parseFrom(command.getParams());
			String siteUserId = command.getSiteUserId();
			String pluginId = request.getPluginId();
			String requestApi = request.getApi();
			String requestParams = request.getParams();
			logger.info("api.plugin.proxy cmd={} request={}", command.toString(), request.toString());

			if (!StringUtils.isAnyBlank(siteUserId, pluginId, requestApi)) {
				PluginBean bean = SitePluginDao.getInstance().getPluginProfile(Integer.valueOf(pluginId));
				if (bean != null) {
					if (!requestApi.startsWith("/")) {
						requestApi = "/" + requestApi;
					}
					String pluginUrl = HTTP_PREFFIX + bean.getUrlPage() + requestApi;
					logger.info("Api.Plugin.Proxy pluginId={} api={} url={} params={}", pluginId, requestApi, pluginUrl,
							requestParams);
					PluginProto.ProxyPackage proxyPackage = PluginProto.ProxyPackage.newBuilder()
							.putProxyContent(PluginProto.ProxyKey.CLIENT_SITE_USER_ID_VALUE, siteUserId)
							.setData(requestParams).build();
					byte[] httpResponse = ZalyHttpClient.getInstance().postBytes(pluginUrl, proxyPackage.toByteArray());
					ApiPluginProxyProto.ApiPluginProxyResponse response = ApiPluginProxyProto.ApiPluginProxyResponse
							.newBuilder().setData(ByteString.copyFrom(httpResponse)).build();
					commandResponse.setParams(response.toByteArray());// httpResposne,callback方法的回调方法参数
					errCode = ErrorCode2.SUCCESS;
				}
			} else {
				errCode = ErrorCode2.ERROR_PARAMETER;
			}
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			logger.error("api plugin proxy error.", e);
		}
		logger.info("api.plugin.proxy result={}", errCode.toString());
		return commandResponse.setErrCode2(errCode);
	}

	/**
	 * 获取插件扩展的展示页面
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse page(Command command) {
		CommandResponse commandResponse = new CommandResponse().setAction(CommandConst.ACTION_RES);
		ErrorCode2 errCode = ErrorCode2.ERROR;
		try {
			ApiPluginPageProto.ApiPluginPageRequest request = ApiPluginPageProto.ApiPluginPageRequest
					.parseFrom(command.getParams());
			String siteUserId = command.getSiteUserId();
			String pluginId = request.getPluginId();
			String pluginAPi = request.getApi();// /index
			logger.info("api.plugin.page cmd={} request={}", command.toString(), request.toString());

			if (StringUtils.isNoneEmpty(siteUserId, pluginId)) {
				PluginBean bean = SitePluginDao.getInstance().getPluginProfile(Integer.valueOf(pluginId));
				if (bean != null) {
					String url = HTTP_PREFFIX + bean.getUrlPage() + pluginAPi + "?siteUserId=" + siteUserId;
					logger.info("http request uri={}", url);
					byte[] httpResponse = ZalyHttpClient.getInstance().get(url);
					ApiPluginPageProto.ApiPluginPageResponse response = ApiPluginPageProto.ApiPluginPageResponse
							.newBuilder().setData(ByteString.copyFrom(httpResponse)).build();
					commandResponse.setParams(response.toByteArray());
					errCode = ErrorCode2.SUCCESS;
				}
			} else {
				errCode = ErrorCode2.ERROR_PARAMETER;
			}
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			logger.error("api plugin page error", e);
		}
		logger.info("api.plugin.page result={}", errCode.toString());
		return commandResponse.setErrCode2(errCode);
	}

	private PluginProto.PluginProfile getPluginProfile(PluginBean bean) {
		PluginProto.PluginProfile.Builder pluginBuilder = PluginProto.PluginProfile.newBuilder();
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
		if (StringUtils.isNotBlank(bean.getUrlApi())) {
			pluginBuilder.setUrlApi(bean.getUrlApi());
		}
		if (StringUtils.isNotBlank(bean.getAuthKey())) {
			pluginBuilder.setAuthKey(bean.getAuthKey());
		}
		if (StringUtils.isNotBlank(bean.getAllowedIp())) {
			pluginBuilder.setAllowedIp(bean.getAllowedIp());
		}
		pluginBuilder.setStatus(PluginProto.PluginStatus.forNumber(bean.getStatus()));
		return pluginBuilder.build();
	}

}
