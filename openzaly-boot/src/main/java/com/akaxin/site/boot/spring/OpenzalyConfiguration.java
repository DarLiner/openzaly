package com.akaxin.site.boot.spring;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
public class OpenzalyConfiguration {

//	@Bean
//	public ConfigurableServletWebServerFactory webServerFactory() {
//		TomcatServletWebServerFactory webserver = new TomcatServletWebServerFactory();
////		webserver.setPort(80822);
////		webserver.setContextPath("/akaxin");
//		return webserver;
//	}
}
