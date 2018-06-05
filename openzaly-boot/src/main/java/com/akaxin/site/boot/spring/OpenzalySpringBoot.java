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
package com.akaxin.site.boot.spring;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <pre>
 * 	openzaly支持springboot框架，在启动main中会同样启动springboot
 * 
 * 	在maven modules中，springboot会存在启动main中扫描不到其他modules中的package，两种方法解决：
 * 		其一：@SpringBootApplication(scanBasePackages={"com.akaxin.site.*"})
 * 		其二：SpringApplication.run(Class<?>...clazzs ,args),clazzs 把需要加载的主类添加上
 * </pre>
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-06-05 19:25:55
 */
@SpringBootApplication(scanBasePackages = { "com.akaxin.site.*" })
public class OpenzalySpringBoot {

	public static void main(String[] args) {

		SpringApplication application = new SpringApplication(OpenzalySpringBoot.class);
		application.setBannerMode(Banner.Mode.OFF);
		// application.setDefaultProperties(properties);
		application.run(args);
	}

}
