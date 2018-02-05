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
package com.akaxin.common.constant;

public interface ErrorCode {
	String SUCCESS = "success";
	String ERROR = "error.alter";

	String REGISTER_ALTER = "error.register.alert"; // 注册失败，展示注册失败信息

	String LOGIN_NEED_REGISTER = "error.login.need_register"; // 登陆失败，需要注册

}
