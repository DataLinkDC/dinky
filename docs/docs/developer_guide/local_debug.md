---
sidebar_position: 2
id: local_debug
title: 本地调试
---

# 前言

最近小伙伴们一直追问，如何在 IDEA 里去调试 Dlink。本文将指导大家可以成功地搭建调试环境并可以修改相关功能的代码，当然欢迎大家将相关问题修复及新功能的实现贡献到 dev 分支哦。那一起来看看吧！

# 开发者本地调试手册

## 前置条件

在搭建Dinky开发环境之前请确保你已经安装如下软件

- Git:版本控制软件
- JDK：后端开发
- Maven：Java包管理
- Node:前端开发;

### 环境要求

|  环境   |     版本     |
| :-----: | :----------: |
|   npm   |    7.19.0    |
| node.js |   14.17.0    |
|   jdk   |     1.8      |
|  maven  |    3.6.0+    |
| lombok  | IDEA插件安装 |
|  mysql  |     5.7+     |

### 代码克隆

请通过 git 管理工具从 GitHub 中拉取 Dinky 源码

```
mkdir workspace
cd workspace
git clone https://github.com/DataLinkDC/dlink.git
#或者
git clone git://github.com/DataLinkDC/dlink.git
```

## IntelliJ IDEA

该指南介绍了关于如何设置 IntelliJ IDEA 来进行 Dlink 前后端开发。Eclipse 不建议使用。

以下文档描述了 [IntelliJ IDEA 2021.3](https://www.jetbrains.com/idea/download/) 的设置步骤以及 Dlink 的导入步骤。

所以以下简称 IDEA 来表示 IntelliJ IDEA 。

### 安装 Lombok 插件

IDEA 提供了插件设置来安装 Lombok 插件。如果尚未安装，请在导入 Dlink 之前按照以下说明来进行操作以启用对 Lombok 注解的支持：

1. 转到 IDEA Settings → Plugins 并选择 Marketplace 。
2. 选择并安装 Lombok 插件。
3. 如果出现提示，请重启 IDEA 。

### 导入 Dinky

1. 启动 IDEA 并选择 Open。
2. 选择已克隆的 Dlink 存储库的根文件夹。
3. 等待项目加载完成。
4. 设置 JDK 1.8 和 Maven 3.6.0。

## 前端环境

### 安装 node.js

可用版本 14.17.0 +，安装步骤详情百度。

### 安装 npm

因 node.js 安装后 npm 版本较高，因此需要可用版本 7.19.0，升级npm命令如下：

```
npm install npm@7.19.0 -g
```

### 初始化依赖

```bash
npm install --force
```

## 源码编译

### 编译

IDEA 里 Build → Build Project

### 打包

```bash
mvn clean install -Dmaven.test.skip=true

# 如若修改版本，按以下指定即可。flink可支持多版本(1.11-1.16)
mvn clean install -Dmaven.test.skip=true -P pord,scala-2.11,flink-1.14,flink-1.15

# 如若不需要web编译,-P 后面加: `!web`
mvn clean install -Dmaven.test.skip=true -P !web,pord,scala-2.11,flink-1.14,flink-1.15
```

打包最终位于根目录 build 下，`dlink-release-x.x.x.tar.gz` 其大小约为 40 M。

### 问题

如果在打包 dlink-web 过程失败，请先单独打包前端进行问题排查。

```bash
npm build
```

## 开发者须知

Dinky开发环境配置有两种模式，分别是 provided 环境和 compile 环境

- provided：此环境适合已经存在安装包，可进行远程调试，此模式需要外部环境安装Flink；
- compile：此环境适合二次开发或者熟悉源码，此模式不需要额外安装Flink,通过Flink自带的local模式变可调试开发
> 开发时，在maven 配置文件 ,勾选即可。dev -> compiler 、 prod -> provided

### Maven Profile须知
![local_debug_maven_profile_intro.png](http://www.aiwenmo.com/dinky/docs/zh-CN/developer_guide/local_debug/local_debug_maven_profile_intro.png)
## Dinky本地开发环境

### 分支选择

开发不同的代码需要不同的分支

- 如果要基于二进制包进行开发，需要切换到对应的分支代码，如 0.5.1；
- 如果想要开发新代码，切换到dev分支即可；

下面说明在启动前如何修改相应的代码，为本地启动做准备。修改

### 修改pom文件

需要修改 dlink根目录下的pom文件，下面以本地开发为例，修改如下：

```
<properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.source>${target.java.version}</maven.compiler.source>
        <maven.compiler.target>${target.java.version}</maven.compiler.target>
        <!--  `provided` for product environment ,`compile` for dev environment  -->
        <scope.runtime>compile</scope.runtime>
    </properties>
```

#### 修改配置文件

修改dlink根目录下/dlink-admin/src/main/resources/application.ym文件

配置数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/dlink?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: dlink
    password: dlink
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### 初始化数据库

在MySQL数据库创建 dlink 用户并在 dlink 数据库中执行 dlink-doc/sql/dinky.sql 文件。此外 dlink-doc/sql/upgrade 目录下存放了了各版本的升级 sql 请依次按照版本号执行。

以上文件修改完成后，就可以启动Dinky。

### 启动后端服务

启动 dlink-admin 下的 Dlink 启动类，可见 8888 端口。

稍微等待一会，即可访问 127.0.0.1:8888 可见登录页。

登录用户/密码: admin/admin

**说明：** 在dinky 0.6版本后，不需要额外启动前端，启动后端后便可访问 127.0.0.1:8888

### 启动前端服务

如你需要对前端做修改 请参考以下:

```bash
npm start
```

稍微等待一会，即可访问 127.0.0.1:8000 可见登录页。

登录用户/密码: admin/admin

### 本地源码调试示例

在IDEA启动后，等待几分钟，即可看到登录页，如下：

![login](http://www.aiwenmo.com/dinky/docs/zh-CN/developer_guide/local_debug/login.png)

登录进去后，以配置数据源和查询数据源为例，观察IDEA的日志情况和dinky界面是否互通；

![test_database_is_success](http://www.aiwenmo.com/dinky/docs/zh-CN/developer_guide/local_debug/test_database_is_success.png)

![url_log](http://www.aiwenmo.com/dinky/docs/zh-CN/developer_guide/local_debug/url_log.png)

如上，配置的数据源已经成功，IDEA日志也正常，这个时候就可以基于本地做二次开发或者贡献代码了。

## 源码结构

```java
dlink--父项目
    |-dlink-admin--管理中心
    |-dlink-alert--告警中心
    |-dlink-app--Application Jar
    |-dlink-assembly--打包配置
    |-dlink-client--Client 中心
    | |-dlink-client-1.11--Client-1.11实现
    | |-dlink-client-1.12--Client-1.12实现
    | |-dlink-client-1.13--Client-1.13实现
    | |-dlink-client-1.14--Client-1.14实现
    |-dlink-common--通用中心
    |-dlink-connectors--Connectors 中心
    | |-dlink-connector-jdbc--Jdbc 扩展
    |-dlink-core--执行中心
    |-dlink-doc--文档
    | |-bin--启动脚本
    | |-bug--bug 反馈
    | |-config--配置文件
    | |-doc--使用文档
    | |-sql--sql脚本
    |-dlink-executor--执行中心
    |-dlink-extends--扩展中心
    |-dlink-function--函数中心
    |-dlink-gateway--Flink 网关中心
    |-dlink-metadata--元数据中心
    | |-dlink-metadata-base--元数据基础组件
    | |-dlink-metadata-clickhouse--元数据-clickhouse 实现
    | |-dlink-metadata-mysql--元数据-mysql 实现
    | |-dlink-metadata-oracle--元数据-oracle 实现
    | |-dlink-metadata-postgresql--元数据-postgresql 实现
    | |-dlink-metadata-doris--元数据-doris 实现
    | |-dlink-metadata-phoenix-元数据-phoenix 实现
    | |-dlink-metadata-sqlserver-元数据-sqlserver 实现
    |-dlink-web--React 前端
    |-docs--官网文档
```

### dlink-admin

Dlink 的管理中心，标准的 SpringBoot 应用，负责与前端 react 交互。

### dlink-alert

Dinky的告警中心，当前已完成: 钉钉 、企业微信 、飞书 、邮箱。

### dlink-app

Dlink 在 Yarn Application 模式所使用的简化解析包。

### dlink-assembly

项目打包配置，管理了最终 tar.gz 的打包内容。

### dlink-client

Dlink 定制的 Flink 运行环境的实现。用来桥接 Dlink 与不同版本的 Flink 运行环境。

### dlink-common

Dlink 的子项目的公用类及实现项目。

### dlink-connectors

Dlink 的 Connectors，目前实现了 Oracle、Clickhouse、SQLServer ...。此外 Dlink 可以直接使用 Flink 的所有连接器，在确保依赖不冲突的情况下。

### dlink-core

Dlink 的核心模块，内包含 Flink RestAPI 、集群、SQL解释器、Job统一调度器（JobManager）、会话管理等实现。

### dlink-doc

此模块为打包所需的资源模块，包含启动脚本、sql脚本、配置文件等。

### dlink-executor

Dlink 的执行模块，是从 dlink-core 中拆分出来，内含最核心的 Executor、Interceptor、Operation 等实现。

### dlink-extends

存放 Dlink 扩展其他生态的组件。

### dlink-function

Dlink 所额外提供的 Flink 各种自定义函数。

### dlink-gateway

Dlink 的任务网关，负责把实现不同执行模式的任务提交与管理，目前主要包含 Yarn PerJob 和 Application。

### dlink-metadata

Dlink 的元数据中心，用于实现各种外部数据源对接到 Dlink，以此使用其各种查询、执行等能力。未来用于 Flink Catalog 的预装载等。

### dlink-web

Dlink 的前端项目，基于 Ant Design Pro 5.0.0。Why Not Vue ? React Who Use Who Know。（中式英语 =。=）

Dlink 的前端架构与开发后续文章会详解，本文略。

## 任务执行路线

同步执行：三角号按钮。

### Local

同步执行/异步提交 ==> StudioService ==> JobManager ==> Executor ==> LocalStreamExecutor ==> CustomTableEnvironmentImpl ==> LocalEnvironment

### Standalone

注册集群实例 ==> 同步执行/异步提交 ==> StudioService ==> JobManager ==> Executor ==> RemoteStreamExecutor ==> CustomTableEnvironmentImpl ==> RemoteEnvironment ==> JobGraph ==> Flink Standalone Cluster

### Yarn Session

注册集群实例 ==> 同步执行/异步提交 ==> StudioService ==> JobManager ==> Executor ==> RemoteStreamExecutor ==> CustomTableEnvironmentImpl ==> RemoteEnvironment ==> JobGraph ==> Flink Yarn Session Cluster

### Yarn Per-Job

注册集群配置 ==> 异步提交 ==> StudioService ==> JobManager ==> Executor ==> JobGraph ==> Gateway ==> YarnPerJobGateway==> YarnClient ==> Flink Yarn Per-Job Cluster

### Yarn Application

注册集群配置 ==> 异步提交 ==> StudioService ==> JobManager ==> Executor ==> TaskId & JDBC ==> Gateway ==> YarnApplicationGateway==> YarnClient ==> dlink-app.jar ==> Executor ==> AppStreamExecutor ==> CustomTableEnvironmentImpl ==> LocalEnvironmentFlink Yarn Application Cluster

## 说明

以为内容是 Dinky在IDEA 部署调试步骤，并简单介绍了各模块的作用，也清晰的描述了各执行模式下 FlinkSQL 实现免 Jar 提交的代码思路。在了解以上内容后，可以动手改造 Dinky。

