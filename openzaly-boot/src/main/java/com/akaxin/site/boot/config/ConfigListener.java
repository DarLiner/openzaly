package com.akaxin.site.boot.config;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.akaxin.common.logs.LogCreater;
import com.akaxin.common.logs.LogUtils;
import com.akaxin.site.business.impl.site.SiteConfig;
import com.akaxin.site.message.utils.SiteConfigHelper;

/**
 * 站点配置监听器，定时更新缓存中数据
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-02-01 14:52:50
 */
public class ConfigListener {
	private static final Logger logger = LogCreater.createTimeLogger("config");

	static {
		Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {
				doListenning();
			}

		}, 20, 20, TimeUnit.SECONDS);
	}

	public static void startListenning() {
		LogUtils.info(logger, "start config listenning");
	}

	public static void doListenning() {
		Map<Integer, String> apiConfigMap = SiteConfigHelper.updateConfig();
		LogUtils.info(logger, "update api site config={}", apiConfigMap);
		Map<Integer, String> imConfigMap = SiteConfig.updateConfig();
		LogUtils.info(logger, "update im site config={}", imConfigMap);
	}
}
