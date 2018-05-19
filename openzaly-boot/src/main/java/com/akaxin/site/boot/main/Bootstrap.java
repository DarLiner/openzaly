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

import java.io.PrintWriter;
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
import com.akaxin.site.boot.utils.Helper;
import com.akaxin.site.business.utils.FilePathUtils;
import com.akaxin.site.business.utils.FileServerUtils;
import com.akaxin.site.connector.exception.HttpServerException;
import com.akaxin.site.connector.exception.TcpServerException;
import com.akaxin.site.connector.handler.ApiRequestHandler;
import com.akaxin.site.connector.handler.HttpRequestHandler;
import com.akaxin.site.connector.handler.ImMessageHandler;
import com.akaxin.site.connector.handler.ImSiteAuthHandler;
import com.akaxin.site.connector.handler.WSRequestHandler;
import com.akaxin.site.connector.http.HttpServer;
import com.akaxin.site.connector.netty.NettyServer;
import com.akaxin.site.connector.ws.WsServer;
import com.akaxin.site.storage.DataSourceManager;
import com.akaxin.site.storage.sqlite.manager.DBConfig;
import com.akaxin.site.storage.sqlite.manager.PluginArgs;
import com.akaxin.site.web.OpenzalyAdminApplication;

/**
 * <pre>
 * Openzaly是Akaxin聊天软件的服务端开源项目，当你第一次从github上下载源码至本地后，可以通过
 * Bootstrap中的main方法启动Openzaly服务器代码。Openzaly-server配合Akaxin客户端协同使用，
 * Akaxin客户端可以在苹果的Appstore以及<a href='www.akaxin.com'>Akaxin官方下载</a>
 * 
 * Openzaly-boot是Openzaly项目中的启动模块，主要负责项目的初始化，事件监听，日志等级变更，帮
 * 助文档，标准化输出，服务启动：
 * 
 * 1.帮助文档
 * 		Openzaly启动支持自定义参数，这些参数通过用户启动命令中增加[-h|-help]获取，具体执行如下：
 * 		java -jar openzaly-server.jar -h
 * 		java -jar openzaly-server.jar -help
 * 
 * 2.初始化工作
 * 		项目启动前期，需要初始化服务端数据，当前需要初始化的数据包括：
 * 		a.初始化数据库，自动创建SQLite中需要的table
 * 		b.站点服务的默认配置或者用户自定义的配置信息
 * 		c.默认后台管理与用户广场的ICON设置
 * 
 * 3.日志等级变更
 * 		Openzaly项目中使用的日志框架为Log4j+SLF4J，默认的日志等级为INFO级别，在后台管理中，支持
 * 		用户通过配置信息修改，来实时变更项目中的日志级别，从而达到在不停止服务情况下，修改日志级别。
 * 
 * 4.服务启动
 * 		Openzaly项目启动的主要部分，包含三个服务的启动分别如下：
 * 		a.提供扩展使用的Netty-Http服务
 * 			使用Netty框架启动Http服务，当开发者开发站点的扩展功能，可以调用此Http接口实现与站点之
 * 			间的交互。
 * 
 * 		b.提供客户端访问Netty-Tcp服务
 * 			Akaxin的客户端【Andorid与IOS】通过tcp连接保持与站点之间的长连接，实现用户与Openzaly
 * 			之间的IM功能以及部分API访问请求。
 * 
 * 		c.提供WEBIM使用的WebSocket服务
 * 			暂时此功能未上线
 * 
 * 5.标准化输出
 * 		在标准输出界面输出Openzaly的启动情况【log日志信息中支持更详细的启动记录】
 * 
 * 
 * Begin from here,start the Openzaly server for clients
 * ......
 * 
 * </pre>
 *
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018.01.01 11:23:42
 */
public class Bootstrap {
	private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

	public static void main(String[] args) {

		// 增加 -h|-help 启动参数 输出帮助文档
		// use java -jar -h|-help ,get more help message
		if (Helper.startHelper(args)) {
			return;
		}

		PrintWriter pwriter = new PrintWriter(System.out);
		Helper.showAkaxinBanner(pwriter);
		Helper.buildEnvToSystemOut(pwriter);

		String nettyTcpHost = "0.0.0.0";
		int nettyTcpPort = 2021;

		String nettyHttpHost = "0.0.0.0";
		int nettyHttpPort = 2021;

		try {
			// init and set default log level by openzaly.properties
			setSystemLogLevel();

			nettyTcpHost = ConfigHelper.getStringConfig(ConfigKey.SITE_ADDRESS);
			nettyTcpPort = ConfigHelper.getIntConfig(ConfigKey.SITE_PORT);
			nettyHttpHost = ConfigHelper.getStringConfig(ConfigKey.HTTP_ADDRESS);
			nettyHttpPort = ConfigHelper.getIntConfig(ConfigKey.HTTP_PORT);

			String adminHost = ConfigHelper.getStringConfig(ConfigKey.SITE_ADMIN_ADDRESS);
			int adminPort = ConfigHelper.getIntConfig(ConfigKey.SITE_ADMIN_PORT);

			String dbDir = ConfigHelper.getStringConfig(ConfigKey.SITE_BASE_DIR);
			String adminUic = ConfigHelper.getStringConfig(ConfigKey.SITE_ADMIN_UIC);
			Map<Integer, String> siteConfigMap = ConfigHelper.getConfigMap();

			DBConfig config = new DBConfig();
			config.setDbDir(dbDir);
			config.setAdminAddress(adminHost);
			config.setAdminPort(adminPort);
			config.setAdminUic(adminUic);
			config.setAdminServerName(PluginArgs.SITE_ADMIN_NAME);
			config.setConfigMap(siteConfigMap);
			// 设置后台管理默认图片
			config.setAdminIcon(getDefaultIcon(SiteDefaultIcon.DEFAULT_SITE_ADMIN_ICON));
			// 设置用户广场默认图片
			config.setParam(PluginArgs.FRIEND_SQUARE, getDefaultIcon(SiteDefaultIcon.DEFAULT_FRIEND_SQUARE_ICON));

			// add site config to database
			initDataSource(config);
			// use thread to update site-config cached in memory
			addConfigListener();

			// start server
			startNettyHttpServer(nettyHttpHost, nettyHttpPort);// 0.0.0.0:8280
			startNettyTcpServer(nettyTcpHost, nettyTcpPort);// 0.0.0.0:2021

			// disable websocket server
			// startWebSocketServer("0.0.0.0", 9090);// 0.0.0.0:9090

			// start spring boot for openzaly-admin
			initSpringBoot(args);

			Helper.startSuccess(pwriter);
			logger.info("start openzaly-server successfully");
		} catch (Exception e) {
			Helper.startFail(pwriter);
			logger.error("start Openzaly-server error", e);
			logger.error("Openzaly-server exit!!!");
			System.exit(-1);// 直接退出程序
		} catch (TcpServerException e) {
			String errMessage = StringHelper.format("openzaly tcp-server {}:{} {}", nettyTcpHost, nettyTcpPort,
					e.getCause().getMessage());
			Helper.startFailWithError(pwriter, errMessage);
			logger.error("start Openzaly with tcp server error", e);
			logger.error("Openzaly-server exit!!!");
			System.exit(-2);// 直接退出程序
		} catch (HttpServerException e) {
			String errMessage = StringHelper.format("openzaly http-server {}:{} {}", nettyHttpHost, nettyHttpPort,
					e.getCause().getMessage());
			Helper.startFailWithError(pwriter, errMessage);
			logger.error("start Openzaly with http server error", e);
			logger.error("Openzaly-server exit!!!");
			System.exit(-3);// 直接退出程序
		} finally {
			if (pwriter != null) {
				pwriter.close();
			}
		}
	}

	private static void setSystemLogLevel() {
		// 先获取站点的项目环境 site.project.env
		String projectEvn = ConfigHelper.getStringConfig(ConfigKey.SITE_PROJECT_ENV);
		Level level = Level.INFO;
		if ("DEBUG".equalsIgnoreCase(projectEvn)) {
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
	 * @throws HttpServerException
	 */
	private static void startNettyHttpServer(String address, int port) throws HttpServerException {
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
	 * @throws TcpServerException
	 */
	private static void startNettyTcpServer(String address, int port) throws TcpServerException {
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
		new WsServer() {

			@Override
			public void loadExecutor(AbstracteExecutor<Command, CommandResponse> executor) {
				executor.addChain("WS-ACTION", new WSRequestHandler());
			}

		}.start(address, port);
	}

	private static void initSpringBoot(String[] args) {
		OpenzalyAdminApplication.main(args);
	}

	private static void addConfigListener() {
		ConfigListener.startListenning();
		logger.info("{} add config listener to site-config", AkxProject.PLN);
	}

	private static String getDefaultIcon(String base64Str) {
		try {
			String fileBasePath = ConfigHelper.getStringConfig(ConfigKey.SITE_BASE_DIR);
			byte[] iconBytes = Base64.getDecoder().decode(base64Str);
			String fileId = FileServerUtils.saveFile(iconBytes, FilePathUtils.getPicPath(fileBasePath),
					FileType.SITE_PLUGIN, null);
			return fileId;
		} catch (Exception e) {
			logger.error(StringHelper.format("{} set openzaly-admin default icon error", AkxProject.PLN), e);
		}
		return "";
	}

}
