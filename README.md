<p align="right">
  <a href="https://www.akaxin.com/">
    <img
      alt="Akaxin"
      src="https://avatars3.githubusercontent.com/u/32624098?s=200&v=4"
      width="128"
    />
  </a>
</p>


[Akaxin](https://www.akaxin.com)
====

[![License](https://img.shields.io/badge/license-apache2-blue.svg)](LICENSE)

**源码仓库**

> * **Github**: https://github.com/akaxincom/openzaly
> * `国内快` Gitee: https://gitee.com/akaxin/openzaly

_

**向我们提问**

> QQ群：`655249600`


简介
----

Akaxin 是一款开源免费的私有聊天软件，可以部署在任意服务器上，搭建自己的聊天服务器，供自己与朋友、用户使用。

特性：

* 单聊、群聊（含文字、图片、语音等）
* 端到端的加密消息（服务端不可解密，服务器可通过配置关闭此特性）
* 匿名注册、实名注册，以及注册邀请码机制（只允许特定用户注册）
* 扩展机制
* 等


<p align="center">
  <img align="center" src="https://is1-ssl.mzstatic.com/image/thumb/Purple118/v4/5f/56/82/5f56825f-5a1d-751a-76ee-e4af3337133c/pr_source.png/0x0ss.jpg" width="200"  /> &nbsp; <img align="center" src="https://is1-ssl.mzstatic.com/image/thumb/Purple128/v4/0a/13/7f/0a137f45-a89e-57d6-3135-5c72b219b28d/pr_source.png/0x0ss.jpg" width="200"  /> &nbsp; <img align="center" src="https://is1-ssl.mzstatic.com/image/thumb/Purple128/v4/45/ec/0a/45ec0a96-6683-049e-139b-f11aaea306c8/pr_source.png/0x0ss.jpg" width="200"  /> &nbsp;
</p>


一、快速体验
----

**1. 启动服务器**

  * **最新版本: openzaly 0.10.6 **
    * 国内镜像：https://cdn-akaxin-1255552447.cos.ap-beijing.myqcloud.com/openzaly/openzaly-server.jar
    * Github下载: https://github.com/akaxincom/openzaly/releases/

  * **Changelog**
    * openzaly发布team版本，同时支持个人版与team版。
    * team 版本支持使用mysql数据库，并且可配置主从库。
    * openzaly使用ssl与平台交互增加了信息传输的安全性。
    
    
openzlay 0.10.6开始支持Personal（个人版）与Team版，默认状态下使用Personal

支持的启动参数：`java -jar openzaly-server.jar -h`

openzaly Personal版本 命令：

    * 版本升级：`java -jar openzaly-server.jar -upgrade` ，此命令在服务与sqlite数据库版本不一致时执行，正常情况无需执行

    * 启动命令：`java -jar openzaly-server.jar`
    
openzaly Team版本 命令：
    
    * 启动Team版本命令：`java -jar openzaly-server.jar -team`
    
    * 修改配置文件: 上一步会生成 openzaly-server.config 与 openzaly-mysql.sql 两个文件
                如果使用mysql数据库需在openzaly-server.config配置文件中配置mysql参数：
                主库（数据库编码需要设置utf8mb4）：
                    openzaly.mysql.host=localhost //数据库的地址
                    openzaly.mysql.port=3306        //数据库端口
                    openzaly.mysql.database=openzaly    //数据库名称
                    openzaly.mysql.username=root        //mysql数据库访问用户
                    openzaly.mysql.password=1234567890  //mysql数据库密码
                
                从库（如果需要使用主从模式，配置这里，不需要从库则不需要配置）数据库编码需要设置utf8mb4：
                    openzaly.mysql.slave.host=localhost
                    openzaly.mysql.slave.port=3306
                    openzaly.mysql.slave.database=openzaly
                    openzaly.mysql.slave.username=root
                    openzaly.mysql.slave.password=1234567890
                
                其他mysql参数为使用mysql连接池的配置参数，如若涉及性能优化可开启配置项。
                
     * 迁移数据库命令：openzaly支持使用者把Personal版本的sqlite中的数据迁移到Team版本的mysql数据库
                     如果执行这一步需要在openzaly-server.config配置文件中配置：
                        `openzaly.sqlite.url=openzalyDB.sqlite3` 这里指定sqlite数据库文件的位置
                     
                     继续执行迁移命令：
                        `java -jar openzaly-server.jar -migrate`
        
     * 启动命令：`java -jar openzaly-server.jar`      
        

**2. 下载客户端**

> * [iOS](https://itunes.apple.com/cn/app/%E9%98%BF%E5%8D%A1%E4%BF%A1/id1346971087?mt=8)
> * [Android](https://www.akaxin.com)

**3. 访问站点**

> * 生成账号（手机账号与匿名均可）
> * 输入站点服务器
> * 首次登陆为管理员，邀请码：000000
> * 别的用户登陆后可以互加好友，开始聊天。

* 匿名账号，账号保存在设备本地，用户不会填写手机信息，任何地方都获取不到。

> **站点注册方式默认为匿名，进入站点后，请根据情况第一时间修改为 实名 或者 开启邀请码，防止恶意用户进入**


二、源码编译安装
----

需要本地有mvn，直接使用mvn编译即可。


三、扩展开发
----

Akaxin 具有灵活、强大的扩展机制 `(“管理平台” 就是一个扩展)`。通过嵌入WEB页面，与后端的扩展API进行交互， 可以很轻松的构建丰富的业务功能，如：

* 附近交友
* 店铺点评
* 在线游戏
* 等等等等

你的聊天服务器，将摇身一变，成为一个强大的社交软件平台。

> 扩展机制处于技术预览阶段，如果你希望在自己的业务中开发自己的扩展，可以联系我们（ mail: hi@akaxin.xyz ），我们将免费提供文档与技术答疑。

以下是我们开发的一个 “校园社交” 的扩展，截图如下：

<p align="center">
  <img align="center" src="https://raw.githubusercontent.com/akaxincom/faq/master/app_pic/plugin.1.jpeg" width="200"  /> &nbsp; <img align="center" src="https://raw.githubusercontent.com/akaxincom/faq/master/app_pic/plugin.2.jpeg" width="200"  /> &nbsp; <img align="center" src="https://raw.githubusercontent.com/akaxincom/faq/master/app_pic/plugin.3.jpeg" width="200"  /> &nbsp;
</p>

> 大家可以去 demo.akaxin.com 体验。


四、技术贡献者
----

> 以加入时间排序

* sisishiliu
* SAM2O2O
* childeYin
* yi.chao
* lei.yu
* cuikun
* alexfanchina
* Mino0885
* 505541778
