# Dlink 概念原理与源码扩展介绍

## Dlink 是什么

​ Dlink 是一个基于 Apache Flink 二次开发的网页版的 FlinkSQL Studio，可以连接多个 Flink 集群实例，并在线开发、执行、提交 FlinkSQL 语句以及预览其运行结果，支持 Flink 官方所有语法并进行了些许增强。

​ Dlink 在 DataLink（https://github.com/DataLinkDC/DataLink） 中的定位是一个实时计算平台，将提供相关功能如开发调试、元数据、血缘分析、任务调度等功能。

## 与 Flink 的不同

​ Dlink 基于 Flink 源码二次开发，主要应用于 SQL 任务的管理与执行。以下将介绍 Dlink-0.2.3 与 Flink 的不同。

### Dlink  的原理

![](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTo5cwuZy7GSLibw5J7Lx6cicx0vXaDHqn5VrrDJ9d3hcEicbEVO77NcP6bOylC9bOpuibM08JJ8bh8XQQ/0?wx_fmt=png)

### Dlink 的 FlinkSQL 执行原理

![](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTqVImq5JvQzZ7oMqpnQ2NVHdmM6Pfib63atzoWNIqK7Ue6p9KfdibV889sOcZJ1Olw3kLHLmHZiab3Tg/0?wx_fmt=png)

### Connector 的扩展

​ Dlink 的 Connector 的使用与扩展同 Flink 的完全一致，即当使用 Flink 集成 Dlink 时，只需要将 Flink 扩展的依赖加入 Dlink 的 lib 下即可。

​ 当然，Dlink 自身源码也提供了一些 Connector ，它们遵循 Flink 的扩展要求，可以直接被加入到 Flink 的 lib 下进行使用。

### 多版本支持

​ Dlink 的单机版只能同时稳定连接同一大版本号下的不同版本的 Flink 集群实例，连接其他大版本号的集群实例在提交任务时可能存在问题；而 DataLink 中的 Dlink 微服务版可以同时稳定连接所有版本号的 Flink 集群实例。

​ Dlink 提供了多版本的 `dlink-client.jar`，根据需求选择对应版本的依赖加入到 lib 下即可稳定连接该版本的 Flink 集群实例。

### Catalog 共享

​ Dlink 提供了共享会话对 Flink 的 Catalog、环境配置等进行了长期管理，可以实现团队开发共享 Catalog 的效果。

### Sql 语法增强

​ Dlink 对 FlinkSQL 的语法进行增强，主要表现为 Sql 片段与表值聚合 Sql 化。

#### Sql 片段

```sql
sf:=
select *
from;
tb:=student;
${sf}
${tb}
## 效果等同于
select *
from student
```

#### 表值聚合

```sql
CREATE
AGGTABLE aggdemo AS
SELECT myField, value, rank
FROM MyTable
GROUP BY myField AGG BY TOP2(value) as (value,rank);
```

### 同步执行结果预览

​ Dlink 可以对同步执行的 FlinkSQL 进行运行完成的结果预览，同 `sql-client`。

## 概念原理

​ 在 Dlink 中具有六个概念，当熟悉他们的原理时，可以搭配出更强大的使用效果。

### 本地环境

​ 本地环境即为`LocalEnvironment`，是在本地模式运行 Flink 程序的句柄，在本地的 JVM （standalone 或嵌入其他程序）里运行程序，通过调用`ExecutionEnvironment.createLocalEnvironment()`方法来实现。

​ Dlink 通过本地环境来实现隔离调试，本地环境执行时所需要的 `connector` 等资源在 `lib` 目录下引入。本地环境执行过程包含完整的 sql 执行过程。

### 远程环境

​ 远程环境即为`RemoteEnvironment`，是在远程模式中向指定集群提交 Flink 程序的句柄，在目标集群的环境里运行程序，通过调用`ExecutionEnvironment.createRemoteEnvironment(host,port)`方法来实现，其中 host 为 `rest.address` ，port 为 `rest.port` 。

​ Dlink 可以对任意 standalone、on yarn等运行模式的远程集群进行 sql 提交。远程环境执行过程只包含 sql 任务的准备工作，即解析、优化、转化物理执行计划、生成算子、提交作业执行图。所以远程环境执行时所需要的 connector 等资源也需要在 lib 目录下引入。

### 共享会话

​ 共享会话为用户与执行环境的操作会话，主要包含 Catalog、片段、执行环境配置等内容。可以认为官方的 `sql-client` 是一个会话，保留了本次命令窗口的操作结果，当退出 `sql-client` 后，会话结束。

​ Dlink 的共享会话相当于可以启动多个 `sql-client` 来进行会话操作，并且其他用户可以使用您的会话 key ，在对应环境中共享您的会话的所有信息。例如，通过执行环境 + 共享会话可以确定唯一的 Catalog。

### 临时会话

​ 临时会话指不启用共享会话，您每次交互执行操作时，都会创建临时的独立的会话，操作解释后立即释放，适合作业解耦处理。

Dlink 的临时会话相当于只启动一个 `sql-client` ，执行完语句后立即关闭再启动。

### 同步执行

​ 同步执行指通过 Studio 进行操作时为同步等待，当语句运行完成后返回运行结果。

​ Dlink 的语句与官方语句一致，并做了些许增强。Dlink 将所有语句划分为三种类型，即 `DDL`、`DQL` 和 `DML` 。对于同步执行来说， `DDL` 和 `DQL` 均为等待语句执行完成后返回运行结果，而 `DML` 语句则立即返回异步提交操作的执行结果。

### 异步提交

​ 异步提交指通过 Studio 进行操作时为异步操作，当语句被执行后立马返回操作执行结果。

​ 对于三种语句类型，Dlink 的异步提交均立即返回异步操作的执行结果。当前版本的 Dlink 的异步提交不进行历史记录。

### 搭配使用

| 执行环境 | 会话     | 运行方式 | 适用场景                                                     |
| -------- | -------- | -------- | ------------------------------------------------------------ |
| 本地环境 | 临时会话 | 同步执行 | 无集群或集群不可用的情况下单独开发FlinkSQL作业，需要查看运行结果 |
| 本地环境 | 共享会话 | 同步执行 | 无集群或集群不可用的情况下复用Catalog或让同事排查bug，需要查看运行结果 |
| 本地环境 | 临时会话 | 异步提交 | 无集群或集群不可用的情况下快速启动一个作业，不需要查看运行结果 |
| 本地环境 | 共享会话 | 异步提交 | 共享会话效果无效                                             |
| 远程环境 | 临时会话 | 同步执行 | 依靠集群单独开发FlinkSQL作业，需要查看运行结果               |
| 远程环境 | 共享会话 | 同步执行 | 依靠集群复用Catalog或让同事排查bug，需要查看运行结果         |
| 远程环境 | 临时会话 | 异步提交 | 快速向集群提交任务，不需要查看运行结果                       |
| 远程环境 | 共享会话 | 异步提交 | 共享会话效果无效                                             |

## 源码扩展

​ Dlink 的源码是非常简单的， Spring Boot 项目轻松上手。

### 项目结构

```java
dlink--父项目
    |-dlink-admin--管理中心
    |-dlink-client--Client 中心
    | |-dlink-client-1.12--Client-1.12实现
    | |-dlink-client-1.13--Client-1.13实现
    |-dlink-connectors--Connectors 中心
    | |-dlink-connector-jdbc--Jdbc 扩展
    |-dlink-core--执行中心
    |-dlink-doc--文档
    | |-bin--启动脚本
    | |-bug--bug 反馈
    | |-config--配置文件
    | |-doc--使用文档
    | |-sql--sql脚本
    |-dlink-function--函数中心
    |-dlink-web--React 前端
```

### 模块介绍

#### dlink-admin

​ 该模块为管理模块，基于 `Spring Boot + MybatisPlus` 框架开发，目前版本对作业、目录、文档、集群、语句等功能模块进行管理。

#### dlink-client

​ 该模块为 Client 的封装模块，依赖了 `flink-client`，并自定义了新功能的实现如 `CustomTableEnvironmentImpl`、`SqlManager ` 等。

​ 通过该模块完成对不同版本的 Flink 集群的适配工作。

#### dlink-connectors

​ 该模块为 Connector 的封装模块，用于扩展 Flink 的 `Connector`。

#### dlink-core

​ 该模块为 Dlink 的核心处理模块，里面涉及了共享会话、拦截器、执行器等任务执行过程使用到的功能。

#### dlink-doc

​ 该模块为 Dlink 的文档模块，部署相关资源以及使用文档等资料都在该模块下。

#### dlink-function

​ 该模块为 UDF 的封装模块，用于扩展 Flink 的 `UDF` 。

#### dlink-web

​ 该模块为 Dlink 的前端工程，基于 `Ant Design Pro` 开发，属于 `React` 技术栈，其中的 Sql 在线编辑器是基于 `Monaco Editor` 开发。

### IDE 调试

#### 前端调试

​ 安装相关依赖

```shell
npm install
```

​ 启动前端项目

```shell
npm start
```

#### 后端调试

​ 编译项目后，运行 `dlink-admin` 下的 `Dlink`启动类。

​ 当前版本的 Dlink 在 IDE 进行调试时，需要将 `dlink-core` 下 `pom` 中的 `dlink-client-1.12`、`dlink-connector-jdbc`、`dlink-function` 的 `scope` 注释掉后才能正常加载运行环境。

### IDE 打包

#### 前端打包

```shell
npm run build
```

​ 打包成功后将 `dlink-web/dist` 目录下的内容放到  `dlink-admin` 的 `static` 下。

#### 后端打包

```shell
maven clean install -Dmaven.test.skip=true
```

## 部署-单机

### 下载地址

​ 链接：https://pan.baidu.com/s/16egitElK1p8QcyDEavzMRQ
​ 提取码：0203

### 应用结构

```java
config/--配置文件
    |-application.yml
    lib/--外部依赖及Connector
    |-dlink-client-1.12.jar--必需,二选一
    |-dlink-client-1.13.jar--必需，二选一
    |-dlink-connector-jdbc.jar
    |-dlink-function-0.2.3.jar
    |-flink-connector-jdbc_2.11-1.12.4.jar
    |-flink-csv-1.12.4.jar
    |-flink-json-1.12.4.jar
    |-mysql-connector-java-8.0.21.jar
    sql/
    |-dinky.sql--Mysql初始化脚本
    |-upgrade/--各个版本升级SQL脚本
    auto.sh--启动停止脚本
    dlink-admin.jar--程序包
```

### 修改配置文件

```yaml
spring:
datasource:
  url: jdbc:mysql://127.0.0.1:3306/dlink?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
  username: dlink
  password: dlink
  driver-class-name: com.mysql.cj.jdbc.Driver
```

### 执行与停止

```shell
# 启动
sh auto.sh start
# 停止
sh auto.sh stop
# 重启
sh auto.sh restart
# 状态
sh auto.sh status
```

## 未来

Dlink 从零开源三周了，目前正在积极地通过小伙伴们反馈的问题进行优化改进，后续将随版本号开放更多实用的功能。

近期，Dlink 将开放集群任务的管理与血缘分析等功能。

未来，Dlink 将紧跟 Flink 官方社区发展，为推广及发展 Flink 的应用而奋斗。

Dlink 将后续不断完善 Studio，打造 FlinkSQL 的最佳搭档的形象。 当然元数据、时间与依赖调度、血缘分析、影响分析、数据地图、监控预警等核心功能将逐步上线。

与此同时，DataLink 数据中台将同步发展，未来将提供开源的企业级数据中台解决方案。

