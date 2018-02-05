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
package com.akaxin.site.boot.main;

import java.util.Arrays;
import java.util.Base64;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.constant.HttpUriAction;
import com.akaxin.common.constant.RequestAction;
import com.akaxin.common.executor.AbstracteExecutor;
import com.akaxin.proto.core.FileProto.FileType;
import com.akaxin.site.boot.config.ConfigHelper;
import com.akaxin.site.boot.config.ConfigKey;
import com.akaxin.site.boot.config.ConfigListener;
import com.akaxin.site.boot.config.SiteDefaultIcon;
import com.akaxin.site.business.utils.FilePathUtils;
import com.akaxin.site.business.utils.FileServerUtils;
import com.akaxin.site.connector.handler.ApiRequestHandler;
import com.akaxin.site.connector.handler.HttpRequestHandler;
import com.akaxin.site.connector.handler.ImMessageHandler;
import com.akaxin.site.connector.handler.ImSiteAuthHandler;
import com.akaxin.site.connector.http.HttpServer;
import com.akaxin.site.connector.netty.NettyServer;
import com.akaxin.site.storage.DataSourceManager;
import com.akaxin.site.storage.sqlite.manager.DBConfigBean;

/**
 * <pre>
 * 启动akaxin项目.
 * Begin from here,start the netty server for clients
 * </pre>
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018.01.01 11:23:42
 */
public class Bootstrap {
	private static Logger logger = LoggerFactory.getLogger(Bootstrap.class);

	public static void main(String[] args) {
		logger.info("start openzaly site server...");
		try {
			String siteAddress = ConfigHelper.getStringConfig(ConfigKey.SITE_ADDRESS);
			int sitePort = ConfigHelper.getIntConfig(ConfigKey.SITE_PORT);
			String httpAddress = ConfigHelper.getStringConfig(ConfigKey.HTTP_ADDRESS);
			int httpPort = ConfigHelper.getIntConfig(ConfigKey.HTTP_PORT);
			String adminAddress = ConfigHelper.getStringConfig(ConfigKey.SITE_ADMIN_ADDRESS);
			int adminPort = ConfigHelper.getIntConfig(ConfigKey.SITE_ADMIN_PORT);
			String dbDir = ConfigHelper.getStringConfig(ConfigKey.SITE_BASE_DIR);
			String adminUic = ConfigHelper.getStringConfig(ConfigKey.SITE_ADMIN_UIC);
			Map<Integer, String> siteConfigMap = ConfigHelper.getConfigMap();

			DBConfigBean bean = new DBConfigBean();
			bean.setDbDir(dbDir);
			bean.setAdminAddress(adminAddress);
			bean.setAdminPort(adminPort);
			bean.setAdminUic(adminUic);
			bean.setAdminAddress(adminAddress);
			bean.setAdminServerName("管理后台");
			bean.setConfigMap(siteConfigMap);
			// 设置后台管理默认图片
			bean.setAdminIcon(getDefaultSiteAdminIcon());

			initDataSource(bean);
			startHttpServer(httpAddress, httpPort);
			startNettyServer(siteAddress, sitePort);
			addConfigListener();
		} catch (Exception e) {
			logger.error("start Bootstrap args exception.args:{}", Arrays.toString(args));
		}
	}

	/**
	 * 初始化数据源
	 */
	private static void initDataSource(DBConfigBean bean) {
		logger.info("start init datasource bean={}", bean.toString());
		DataSourceManager.init(bean);
	}

	/**
	 * 启动Http服务，提供与扩展服务之间的hai（http application interface）接口功能
	 */
	private static void startHttpServer(String address, int port) {
		new HttpServer() {

			@Override
			public void loadExecutor(AbstracteExecutor<Command> executor) {
				executor.addChain(HttpUriAction.HTTP_ACTION.getUri(), new HttpRequestHandler());
			}

		}.start(address, port);
		logger.info("start openzaly http server {}:{} ok.", address, port);
	}

	/**
	 * 启动Netty服务器，提供用户与站点服务之间的长链接功能
	 * 
	 * @param address
	 * @param port
	 */
	private static void startNettyServer(String address, int port) {
		new NettyServer() {

			@Override
			public void loadExecutor(AbstracteExecutor<Command> executor) {
				executor.addChain(RequestAction.IM_SITE.getName(), new ImSiteAuthHandler());
				executor.addChain(RequestAction.IM.getName(), new ImMessageHandler());
				executor.addChain(RequestAction.API.getName(), new ApiRequestHandler());
			}

		}.start(address, port);
		logger.info("start openzaly netty server {}:{} ok.", address, port);
	}

	/**
	 * <pre>
	 * 启动两个监听线程，定时更新缓存中的站点信息
	 * 		1.为什用两个？防止后期api请求/im请求，针对站点配置信息处理逻辑不同，早起直接使用两个线程
	 * </pre>
	 */
	private static void addConfigListener() {
		logger.info("-------add config listener--------");
		ConfigListener.startListenning();
	}

	private static String getDefaultSiteAdminIcon() {
		try {
			byte[] iconBytes = Base64.getDecoder().decode(SiteDefaultIcon.DEFAULT_SITE_ADMIN_ICON);
			String fileId = FileServerUtils.saveFile(iconBytes, FilePathUtils.getPicPath(""),
					FileType.SITE_PLUGIN_VALUE);
			return fileId;
		} catch (Exception e) {
			logger.error("get default site admin icon error", e);
		}
		return "";
	}
}
