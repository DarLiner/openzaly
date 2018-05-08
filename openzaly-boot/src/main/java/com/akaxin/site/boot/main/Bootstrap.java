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
package com.akaxin.site.boot.main;

import java.util.Base64;
import java.util.Map;

import org.apache.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.HttpUriAction;
import com.akaxin.common.constant.RequestAction;
import com.akaxin.common.executor.AbstracteExecutor;
import com.akaxin.common.logs.AkxLog4jManager;
import com.akaxin.common.utils.StringHelper;
import com.akaxin.proto.core.FileProto.FileType;
import com.akaxin.site.boot.config.AkxProject;
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
import com.akaxin.site.connector.websocket.WebSocketServer;
import com.akaxin.site.storage.DataSourceManager;
import com.akaxin.site.storage.sqlite.manager.DBConfig;
import com.akaxin.site.storage.sqlite.manager.PluginArgs;
import com.akaxin.site.web.OpenzalyAdminApplication;

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
	private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);
	private static final String DEBUG_ENV = "DEBUG";

	public static void main(String[] args) {
		logger.info("{} start site server...", AkxProject.PLN);
		try {
			// init log level
			setSystemLogLevel();

			String siteAddress = ConfigHelper.getStringConfig(ConfigKey.SITE_ADDRESS);
			int sitePort = ConfigHelper.getIntConfig(ConfigKey.SITE_PORT);
			String httpAddress = ConfigHelper.getStringConfig(ConfigKey.HTTP_ADDRESS);
			int httpPort = ConfigHelper.getIntConfig(ConfigKey.HTTP_PORT);
			String adminAddress = ConfigHelper.getStringConfig(ConfigKey.SITE_ADMIN_ADDRESS);
			int adminPort = ConfigHelper.getIntConfig(ConfigKey.SITE_ADMIN_PORT);
			String dbDir = ConfigHelper.getStringConfig(ConfigKey.SITE_BASE_DIR);
			String adminUic = ConfigHelper.getStringConfig(ConfigKey.SITE_ADMIN_UIC);
			Map<Integer, String> siteConfigMap = ConfigHelper.getConfigMap();

			DBConfig config = new DBConfig();
			config.setDbDir(dbDir);
			config.setAdminAddress(adminAddress);
			config.setAdminPort(adminPort);
			config.setAdminUic(adminUic);
			config.setAdminServerName(PluginArgs.SITE_ADMIN_NAME);
			config.setConfigMap(siteConfigMap);
			// 设置后台管理默认图片
			config.setAdminIcon(getDefaultIcon(SiteDefaultIcon.DEFAULT_SITE_ADMIN_ICON));
			// 设置用户广场默认图片
			config.setParam(PluginArgs.FRIEND_SQUARE, getDefaultIcon(SiteDefaultIcon.DEFAULT_FRIEND_SQUARE_ICON));

			// add config
			initDataSource(config);
			addConfigListener();

			// start server
			startHttpServer(httpAddress, httpPort);// 0.0.0.0:2021
			startNettyServer(siteAddress, sitePort);// 0.0.0.0:8080
			startWebSocketServer("0.0.0.0", 9090);// 0.0.0.0:9090

			// start spring
			initSpringBoot(args);
		} catch (Exception e) {
			logger.error(StringHelper.format("{} start Bootstrap error", AkxProject.PLN), e);
			logger.error("openzaly-boot exit!!!");
			System.exit(-1);// 直接退出程序
		}
	}

	private static void setSystemLogLevel() {
		// 先获取站点的项目环境 site.project.env
		String projectEvn = ConfigHelper.getStringConfig(ConfigKey.SITE_PROJECT_ENV);
		Level level = Level.INFO;
		if (DEBUG_ENV.equalsIgnoreCase(projectEvn)) {
			level = Level.DEBUG;
		}
		// 更新日志级别
		AkxLog4jManager.setLogLevel(level);
		logger.info("{} set system log level={}", AkxProject.PLN, level);
	}

	/**
	 * 初始化数据源
	 */
	private static void initDataSource(DBConfig config) {
		logger.info("{} init datasource config={}", AkxProject.PLN, config.toString());
		DataSourceManager.init(config);
	}

	/**
	 * 启动Http服务，提供与扩展服务之间的hai（http application interface）接口功能
	 *
	 * @throws Exception
	 */
	private static void startHttpServer(String address, int port) throws Exception {
		new HttpServer() {

			@Override
			public void loadExecutor(AbstracteExecutor<Command, CommandResponse> executor) {
				executor.addChain(HttpUriAction.HTTP_ACTION.getRety(), new HttpRequestHandler());
			}

		}.start(address, port);
		logger.info("{} start http server {}:{} ok.", AkxProject.PLN, address, port);
	}

	/**
	 * 启动Netty服务器，提供用户与站点服务之间的长链接功能
	 *
	 * @param address
	 * @param port
	 * @throws Exception
	 */
	private static void startNettyServer(String address, int port) throws Exception {
		new NettyServer() {

			@Override
			public void loadExecutor(AbstracteExecutor<Command, CommandResponse> executor) {
				executor.addChain(RequestAction.IM_SITE.getName(), new ImSiteAuthHandler());
				executor.addChain(RequestAction.IM.getName(), new ImMessageHandler());
				executor.addChain(RequestAction.API.getName(), new ApiRequestHandler());
			}

		}.start(address, port);
		logger.info("{} start netty server {}:{} ok.", AkxProject.PLN, address, port);
	}

	private static void startWebSocketServer(String address, int port) throws Exception {
		new WebSocketServer() {
		}.start(address, port);
	}

	private static void initSpringBoot(String[] args) {
		OpenzalyAdminApplication.main(args);
	}

	/**
	 * <pre>
	 * 启动两个监听线程，定时更新缓存中的站点信息
	 * 		1.为什用两个？防止后期api请求/im请求，针对站点配置信息处理逻辑不同，早起直接使用两个线程
	 * </pre>
	 */
	private static void addConfigListener() {
		ConfigListener.startListenning();
		logger.info("{} add config listener to site-config", AkxProject.PLN);
	}

	private static String getDefaultIcon(String base64Str) {
		try {
			byte[] iconBytes = Base64.getDecoder().decode(base64Str);
			String fileId = FileServerUtils.saveFile(iconBytes, FilePathUtils.getPicPath(), FileType.SITE_PLUGIN, null);
			return fileId;
		} catch (Exception e) {
			logger.error(StringHelper.format("{} set openzaly-admin default icon error", AkxProject.PLN), e);
		}
		return "";
	}

}
