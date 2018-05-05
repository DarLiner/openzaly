
Akaxin
====

[![License](https://img.shields.io/badge/license-apache2-blue.svg)](LICENSE)

Akaxin 是一款开源免费的私有聊天软件，可以部署在任意服务器上，搭建自己的聊天服务器，供自己与朋友、用户使用。


快速体验
----

**启动服务器**

下载最新的jar包：https://github.com/

```
java -jar openzaly-server.jar
```

**下载客户端**

> * [iOS](https://itunes.apple.com/cn/app/%E9%98%BF%E5%8D%A1%E4%BF%A1/id1346971087?mt=8)
> * [Android](https://www.akaxin.com)

**访问站点**

> * 生成账号（手机账号与匿名均可）
> * 输入站点服务器
> * 首次登陆为管理员，邀请码：000000
> * 别的用户登陆后可以互加好友，开始聊天。

* 实名账号，用户会把自己的手机号传递给站点。
* 匿名账号，账号信息保存在用户本地，用户不会填写手机信息，任何地方都获取不到。
    * 本地身份一旦删除，如无备份或其他设备授权，便再也不发找回。

> **站点注册方式默认为匿名，进入站点后，请根据情况第一时间修改为 实名 或者 开启邀请码，防止恶意用户进入**



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
