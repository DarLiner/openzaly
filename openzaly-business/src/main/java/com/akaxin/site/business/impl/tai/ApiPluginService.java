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
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.CharsetCoding;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.constant.ErrorCode2;
import com.akaxin.common.constant.IErrorCode;
import com.akaxin.common.crypto.AESCrypto;
import com.akaxin.common.exceptions.ZalyException2;
import com.akaxin.common.http.ZalyHttpClient;
import com.akaxin.common.logs.LogUtils;
import com.akaxin.proto.core.CoreProto;
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
	private static final String HTTPS_PREFFIX = "https://";

	/**
	 * 分页获取扩展列表
	 * 
	 * @param command
	 * @return
	 */
	public CommandResponse list(Command command) {
		CommandResponse commandResponse = new CommandResponse().setAction(CommandConst.ACTION_RES);
		IErrorCode errCode = ErrorCode2.ERROR;
		try {
			ApiPluginListProto.ApiPluginListRequest request = ApiPluginListProto.ApiPluginListRequest
					.parseFrom(command.getParams());
			String siteUserId = command.getSiteUserId();
			String sessionId = command.getHeader().get(CoreProto.HeaderKey.CLIENT_SOCKET_PLATFORM_SESSION_ID_VALUE);
			int pageNumber = request.getPageNumber();
			int pageSize = request.getPageSize();
			PluginProto.PluginPosition position = request.getPosition();
			LogUtils.requestDebugLog(logger, command, request.toString());

			pageNumber = Math.max(pageNumber, 1);// 从第一页开始

			// 支持首页以及消息聊天界面扩展
			if (PluginProto.PluginPosition.HOME_PAGE != position && PluginProto.PluginPosition.MSG_PAGE != position) {
				throw new ZalyException2(ErrorCode2.ERROR2_PLUGIN_STATUS);
			}

			List<PluginBean> pluginList = null;
			if (StringUtils.isNotBlank(siteUserId) && SiteConfig.isSiteManager(siteUserId)) {
				pluginList = SitePluginDao.getInstance().getAdminPluginPageList(pageNumber, pageSize,
						position.getNumber());
			} else {
				pluginList = SitePluginDao.getInstance().getOrdinaryPluginPageList(pageNumber, pageSize,
						position.getNumber());
			}

			if (pluginList != null) {
				ApiPluginListProto.ApiPluginListResponse.Builder responseBuilder = ApiPluginListProto.ApiPluginListResponse
						.newBuilder();
				for (PluginBean bean : pluginList) {
					PluginProto.Plugin.Builder pluginBuilder = getPluginProtoBuilder(bean);

					String authKey = bean.getAuthKey();
					if (StringUtils.isNotEmpty(authKey)) {
						byte[] tsk = bean.getAuthKey().getBytes(CharsetCoding.ISO_8859_1);
						byte[] encryptedSessionId = AESCrypto.encrypt(tsk, sessionId.getBytes());
						pluginBuilder.setEncryptedSessionIdBase64(Base64.encodeBase64String(encryptedSessionId));
					}
					responseBuilder.addPlugin(pluginBuilder.build());
				}
				commandResponse.setParams(responseBuilder.build().toByteArray());
				errCode = ErrorCode2.SUCCESS;
			}

		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		} catch (ZalyException2 e) {
			errCode = e.getErrCode();
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode(errCode);
	}

	/**
	 * <pre>
	 * 获取插件扩展的展示页面,支持两种方式
	 * 	1.非加密方式，此时扩展authkey不存在
	 *  2.加密方式，此时扩展authkey存在
	 * </pre>
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
			String requestPage = request.getPage();// /index || index.php || index.html
			String requestParams = request.getParams();
			LogUtils.requestDebugLog(logger, command, request.toString());

			Map<Integer, String> header = command.getHeader();
			String siteSessionId = header.get(CoreProto.HeaderKey.CLIENT_SOCKET_SITE_SESSION_ID_VALUE);
			String pluginRefere = header.get(CoreProto.HeaderKey.PLUGIN_CLIENT_REFERER_VALUE);
			if (StringUtils.isNoneEmpty(siteUserId, pluginId)) {
				PluginBean bean = SitePluginDao.getInstance().getPluginProfile(Integer.valueOf(pluginId));
				if (bean != null && bean.getApiUrl() != null) {
					String pageUrl = buildUrl(bean.getApiUrl(), requestPage, bean.getUrlPage());
					logger.debug("http request uri={}", pageUrl);

					PluginProto.ProxyPluginPackage.Builder packageBuilder = PluginProto.ProxyPluginPackage.newBuilder();
					packageBuilder.putPluginHeader(PluginProto.PluginHeaderKey.CLIENT_SITE_USER_ID_VALUE, siteUserId);
					packageBuilder.putPluginHeader(PluginProto.PluginHeaderKey.CLIENT_SITE_SESSION_ID_VALUE,
							siteSessionId);
					packageBuilder.putPluginHeader(PluginProto.PluginHeaderKey.PLUGIN_ID_VALUE, pluginId);
					packageBuilder.putPluginHeader(PluginProto.PluginHeaderKey.PLUGIN_TIMESTAMP_VALUE,
							String.valueOf(System.currentTimeMillis()));
					if (StringUtils.isNotEmpty(pluginRefere)) {
						packageBuilder.putPluginHeader(PluginProto.PluginHeaderKey.PLUGIN_REFERER_VALUE, pluginRefere);
					}
					if (StringUtils.isNotEmpty(requestParams)) {
						packageBuilder.setData(requestParams);
					}

					byte[] httpContent = packageBuilder.build().toByteArray();
					String authKey = bean.getAuthKey();
					if (StringUtils.isNotEmpty(authKey)) {
						// AES 加密整个proto，通过http传输给plugin
						byte[] tsk = bean.getAuthKey().getBytes(CharsetCoding.ISO_8859_1);
						byte[] enPostContent = AESCrypto.encrypt(tsk, httpContent);
						httpContent = enPostContent;
					}

					byte[] httpResponse = ZalyHttpClient.getInstance().postBytes(pageUrl, httpContent);
					ApiPluginProxyProto.ApiPluginProxyResponse response = ApiPluginProxyProto.ApiPluginProxyResponse
							.newBuilder().setData(ByteString.copyFrom(httpResponse)).build();

					commandResponse.setParams(response.toByteArray());
					errCode = ErrorCode2.SUCCESS;
				}
			} else {
				errCode = ErrorCode2.ERROR_PARAMETER;
			}
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode2(errCode);
	}

	/**
	 * <pre>
	 * 	代理前台客户端中扩展的请求
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
			LogUtils.requestDebugLog(logger, command, request.toString());

			Map<Integer, String> header = command.getHeader();
			String siteSessionId = header.get(CoreProto.HeaderKey.CLIENT_SOCKET_SITE_SESSION_ID_VALUE);
			String pluginRefere = header.get(CoreProto.HeaderKey.PLUGIN_CLIENT_REFERER_VALUE);

			if (!StringUtils.isAnyBlank(siteUserId, pluginId, requestApi)) {
				PluginBean bean = SitePluginDao.getInstance().getPluginProfile(Integer.valueOf(pluginId));
				if (bean != null && bean.getApiUrl() != null) {
					String pluginUrl = this.buildUrl(bean.getApiUrl(), requestApi, null);
					logger.debug("action={} pluginId={} api={} url={} params={}", command.getAction(), pluginId,
							requestApi, pluginUrl, requestParams);

					PluginProto.ProxyPluginPackage.Builder packageBuilder = PluginProto.ProxyPluginPackage.newBuilder();
					packageBuilder.putPluginHeader(PluginProto.PluginHeaderKey.CLIENT_SITE_USER_ID_VALUE, siteUserId);
					packageBuilder.putPluginHeader(PluginProto.PluginHeaderKey.CLIENT_SITE_SESSION_ID_VALUE,
							siteSessionId);
					packageBuilder.putPluginHeader(PluginProto.PluginHeaderKey.PLUGIN_ID_VALUE, pluginId);
					packageBuilder.putPluginHeader(PluginProto.PluginHeaderKey.PLUGIN_TIMESTAMP_VALUE,
							String.valueOf(System.currentTimeMillis()));
					if (StringUtils.isNotEmpty(pluginRefere)) {
						packageBuilder.putPluginHeader(PluginProto.PluginHeaderKey.PLUGIN_REFERER_VALUE, pluginRefere);
					}
					if (StringUtils.isNotEmpty(requestParams)) {
						packageBuilder.setData(requestParams);
					}

					byte[] httpContent = packageBuilder.build().toByteArray();
					String authKey = bean.getAuthKey();
					if (StringUtils.isNotEmpty(authKey)) {
						// AES 加密整个proto，通过http传输给plugin
						// byte[] tsk = AESCrypto.generateTSKey(bean.getAuthKey());
						byte[] tsk = bean.getAuthKey().getBytes(CharsetCoding.ISO_8859_1);
						byte[] enPostContent = AESCrypto.encrypt(tsk, httpContent);
						httpContent = enPostContent;
					}

					byte[] httpResponse = ZalyHttpClient.getInstance().postBytes(pluginUrl, httpContent);
					if (httpResponse != null) {
						ApiPluginProxyProto.ApiPluginProxyResponse response = ApiPluginProxyProto.ApiPluginProxyResponse
								.newBuilder().setData(ByteString.copyFrom(httpResponse)).build();
						commandResponse.setParams(response.toByteArray());// httpResposne,callback方法的回调方法参数
					}
					errCode = ErrorCode2.SUCCESS;
				}
			} else {
				errCode = ErrorCode2.ERROR_PARAMETER;
			}
		} catch (Exception e) {
			errCode = ErrorCode2.ERROR_SYSTEMERROR;
			LogUtils.requestErrorLog(logger, command, e);
		}
		return commandResponse.setErrCode2(errCode);
	}

	private PluginProto.Plugin.Builder getPluginProtoBuilder(PluginBean bean) {
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
		pluginBuilder.setDisplayModeValue(bean.getDisplayMode());
		// pluginBuilder.setEncryptedSessionIdBase64(value)
		return pluginBuilder;
	}

	private String buildUrl(String apiUrl, String apiName, String defaultPage) {
		String pageUrl = HTTP_PREFFIX;
		if (apiUrl.startsWith(HTTP_PREFFIX) || apiUrl.startsWith(HTTPS_PREFFIX)) {
			pageUrl = apiUrl;
		} else {
			pageUrl += apiUrl;
		}
		if (StringUtils.isNotEmpty(apiName)) {
			if (apiName.startsWith("/")) {
				pageUrl += apiName;
			} else {
				pageUrl += "/" + apiName;
			}
		} else {
			if (StringUtils.isNotEmpty(defaultPage)) {
				if (defaultPage.startsWith("/")) {
					pageUrl += defaultPage;
				} else {
					pageUrl += "/" + defaultPage;
				}
			}
		}
		return pageUrl;
	}

}
