## 前言

最近小伙伴们一直追问，如何在 IDEA 里去调试 Dlink。本文将指导大家可以成功地搭建调试环境并可以修改相关功能的代码，当然欢迎大家将相关问题修复及新功能的实现贡献到 dev 分支哦。那一起来看看吧！

## 准备

首先，请从 GitHub 中拉取 Dlink 源码，例如：

```bash
git clone https://github.com/DataLinkDC/dlink.git
```

##  IntelliJ IDEA

该指南介绍了关于如何设置 IntelliJ IDEA 来进行 Dlink 前后端开发。Eclipse 不建议使用。

以下文档描述了 IntelliJ IDEA 2021.3 (https://www.jetbrains.com/idea/download/) 的设置步骤以及 Dlink 的导入步骤。

所以以下简称 IDEA 来表示 IntelliJ IDEA 。

### 安装 Lombok 插件

IDEA 提供了插件设置来安装 Lombok 插件。如果尚未安装，请在导入 Dlink 之前按照以下说明来进行操作以启用对 Lombok 注解的支持：

1. 转到 IDEA Settings → Plugins 并选择 Marketplace 。
2. 选择并安装 Lombok 插件。
3. 如果出现提示，请重启 IDEA 。

### 导入 Dlink

1. 启动 IDEA 并选择 Open。
2. 选择已克隆的 Dlink 存储库的根文件夹。
3. 等待项目加载完成。
4. 设置 JDK 1.8 和 Maven 3.6.0。

## 前端环境

### 安装 npm

可用版本 7.19.0，安装步骤详情百度。

### 安装 node.js

可用版本 14.17.0，安装步骤详情百度。

### 初始化依赖

```bash
npm install --force
```

## 编译项目

### 编译

IDEA 里 Build → Build  Project 。

### 打包

```bash
mvn clean install -Dmaven.test.skip=true
```

打包最终位于根目录 build 下，`dlink-release-0.5.0-SNAPSHOT.tar.gz` 其大小约为 40 M。

### 问题

如果在打包 dlink-web 过程失败，请先单独打包前端进行问题排查。

```bash
npm build
```

## 修改 pom

### dlink-core.pom

```xml
		<dependency>
            <groupId>com.dlink</groupId>
            <artifactId>dlink-client-1.13</artifactId>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>com.dlink</groupId>
            <artifactId>dlink-executor</artifactId>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>com.dlink</groupId>
            <artifactId>dlink-connector-jdbc-1.13</artifactId>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>com.dlink</groupId>
            <artifactId>dlink-function</artifactId>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>com.dlink</groupId>
            <artifactId>dlink-gateway</artifactId>
            <scope>provided</scope>
        </dependency>
```

把上述依赖的 scope 注释掉。

### dlink-admin.pom

可在该 pom 下按功能添加其他 Dlink 子组件依赖以及 Flink 和 Hadoop 的第三方依赖。

如使用 ClickHouse 数据源及元数据功能，则添加以下内容：

```xml
		<dependency>
            <groupId>com.dlink</groupId>
            <artifactId>dlink-metadata-clickhouse</artifactId>
            <version>0.5.0-SNAPSHOT</version>
        </dependency>
```

如使用 Flink Hive 等其他连接器功能，则需要添加相关依赖。

## 修改配置文件

### application.yml

配置数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/dlink?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: dlink
    password: dlink
    driver-class-name: com.mysql.cj.jdbc.Driver
```

## 初始化数据库

创建 dlink 用户并在 dlink 数据库中执行 dlink-doc/sql/dlink.sql 文件。

此外 dlink-doc/sql/dlink_history.sql 标识了各版本的升级 sql。

## 启动后端服务

启动 dlink-admin 下的 Dlink 启动类，可见 8888 端口。

## 启动前端服务

```bash
npm start
```

等待几分钟，访问 127.0.0.1:8000  可见登录页。

输入 admin/admin 登录。

## 源码结构

```java
dlink -- 父项目
|-dlink-admin -- 管理中心
|-dlink-app -- Application Jar
|-dlink-assembly -- 打包配置
|-dlink-client -- Client 中心
| |-dlink-client-1.11 -- Client-1.11 实现
| |-dlink-client-1.12 -- Client-1.12 实现
| |-dlink-client-1.13 -- Client-1.13 实现
| |-dlink-client-1.14 -- Client-1.14 实现
|-dlink-common -- 通用中心
|-dlink-connectors -- Connectors 中心
| |-dlink-connector-jdbc -- Jdbc 扩展
|-dlink-core -- 执行中心
|-dlink-doc -- 文档
| |-bin -- 启动脚本
| |-bug -- bug 反馈
| |-config -- 配置文件
| |-doc -- 使用文档
| |-sql -- sql脚本
|-dlink-executor -- 执行中心
|-dlink-extends -- 扩展中心
|-dlink-function -- 函数中心
|-dlink-gateway -- Flink 网关中心
|-dlink-metadata -- 元数据中心
| |-dlink-metadata-base -- 元数据基础组件
| |-dlink-metadata-clickhouse -- 元数据- clickhouse 实现
| |-dlink-metadata-mysql -- 元数据- mysql 实现
| |-dlink-metadata-oracle -- 元数据- oracle 实现
| |-dlink-metadata-postgresql -- 元数据- postgresql 实现
|-dlink-web -- React 前端
|-docs -- 官网文档
```

### dlink-admin

Dlink 的管理中心，标准的 SpringBoot 应用，负责与前端 react 交互。

### dlink-app

Dlink 在 Yarn Application 模式所使用的简化解析包。

### dlink-assembly

项目打包配置，管理了最终 tar.gz 的打包内容。

### dlink-client

Dlink 定制的 Flink 运行环境的实现。用来桥接 Dlink 与不同版本的 Flink 运行环境。

### dlink-common

Dlink 的子项目的公用类及实现项目。

### dlink-connectors

Dlink 的 Connectors，目前实现了 Oracle、Clickhouse、SQLServer。此外 Dlink 可以直接使用 Flink 的所有连接器，在确保依赖不冲突的情况下。

### dlink-core

Dlink  的核心模块，内包含 Flink RestAPI 、集群、SQL解释器、Job统一调度器（JobManager）、会话管理等实现。

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

### docs

Dlink 的官网实现，大佬们可以修改或贡献 markdown。多多分享，让社区发展更快，十分感谢。

## 任务执行路线

同步执行：三角号按钮。

异步提交：小火箭按钮。

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

## 总结

以为内容为大家带来了 Dlink 的基本功能 IDEA 部署调试步骤，并简单介绍了各模块的作用，也清晰的描述了各执行模式下 FlinkSQL 实现免 Jar 提交的代码思路。在了解以上内容后，相信大家已经可以动手改造 Dlink 了，欢迎大家及时加入 Dlink 社区成为核心贡献者，共建共赢。

后续文章将指引大家如何快速拓展 Dlink 的功能组件，敬请期待。

