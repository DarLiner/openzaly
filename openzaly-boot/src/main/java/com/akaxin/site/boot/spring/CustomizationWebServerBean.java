package com.akaxin.site.boot.spring;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.stereotype.Component;

import com.akaxin.site.boot.config.ConfigHelper;
import com.akaxin.site.boot.config.ConfigKey;

@Component
public class CustomizationWebServerBean implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {
	private static Logger logger = LoggerFactory.getLogger(CustomizationWebServerBean.class);

	@Override
	public void customize(ConfigurableServletWebServerFactory server) {
		String adminAddress = ConfigHelper.getStringConfig(ConfigKey.SITE_ADMIN_ADDRESS);
		String adminPort = ConfigHelper.getStringConfig(ConfigKey.SITE_ADMIN_PORT);

		// set admin port
		if (StringUtils.isNumeric(adminPort)) {
			server.setPort(Integer.valueOf(adminPort));
		} else {
			server.setPort(8288);
		}

		// set admin address
		if (StringUtils.isNotEmpty(adminAddress)) {
			try {
				InetAddress address = InetAddress.getByName(adminAddress);
				server.setAddress(address);
			} catch (UnknownHostException e) {
			}
		}
		server.setContextPath("/akaxin");
	}

}
