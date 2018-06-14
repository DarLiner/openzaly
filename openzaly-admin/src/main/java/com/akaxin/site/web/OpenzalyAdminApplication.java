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
package com.akaxin.site.web;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * springboot的支持，放在openzaly-boot中
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-06-05 19:31:16
 */
@Deprecated
@SpringBootApplication
public class OpenzalyAdminApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(OpenzalyAdminApplication.class);
		application.setBannerMode(Banner.Mode.OFF);
		application.run(args);
	}
}
