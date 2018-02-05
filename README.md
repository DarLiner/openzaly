Copyright 2018-2028 Akaxin Group.
 
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
 
     http://www.apache.org/licenses/LICENSE-2.0
 
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


----------------------------------[openzaly]------------------------------------

项目名称：openzaly 
	説明：站点服务器程序 
	注意：目前处于内部原型开发，仅供测试使用。

站点服务器包括5个模块：

	模块一：站点启动模块（openzaly-boot）
		功能介绍：
			1.加载数据源初始项
			2.启动Netty服务器
			3.启动Http服务器
	
	模块二：站点连接管理模块（openzaly-connector）
	
	模块三：站点api业务处理模块（openzaly-business）
	
	模块四：站点IM消息模块（openzaly-message）
	
	模块五：存储模块（openzaly-storage）
	
	项目依赖jar：
		1.openzaly公共功能包（openzaly-common）
		2.openzaly公有protobuf包（openzaly-protobuf）

