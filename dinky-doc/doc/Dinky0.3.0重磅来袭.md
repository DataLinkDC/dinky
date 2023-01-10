# Dlink0.3.0重磅来袭，让 FlinkSQL 更丝滑

## 前言

Apache Flink 1.14 即将来袭，与此同时 Dlink 也带来了最新的进展，试图使 FlinkSQL 更加丝滑。

## 简介

Dlink 为 Apache Flink 而生。它基于 Flink 源码进行二次开发，增强特性的同时兼具解耦，最终提供了一个 FlinkSQL Studio 的能力。

值得注意的是，Dlink 更专注于 FlinkSQL 的应用，试图通过 100% SQL 化来胜任企业中常见的业务情景，降低 Flink 的使用门槛，减少运维开发成本，加快 Flink 的应用推广。

![](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTo5cwuZy7GSLibw5J7Lx6cicx0vXaDHqn5VrrDJ9d3hcEicbEVO77NcP6bOylC9bOpuibM08JJ8bh8XQQ/0?wx_fmt=png)

## 功能

Dlink 提供了仿 IDE 的 SQL 开发界面，支持语法高亮、自动补全、参数配置、语法逻辑检查、SQL 远程提交、批流 SELECT 预览、血缘分析、执行历史、任务启停等功能；Dlink 还提供了 Flink 集群实例管理、数据源管理、元数据、函数文档等功能。

注意：0.3.0 为架构版本，只包含基础架构，相应实现与扩展将在其修订版本更新。

### 首页

![](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTrh5UYychtscpfXuuKt16CvTKz0dcibiavX4ZHA1wSHUvXLlsRvcghHKOhLmIMicJWlnp61L2gxyJEwg/0?wx_fmt=png)

Dlink 的首页主要展示了其开源进展。

未来将改为数据地图。

### Flink 集群注册

![](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTrh5UYychtscpfXuuKt16Cv2010EVibf5rvht2nQVu7dAIoUMGgwh7PUWk9HWUjgyy2emSLGCqkdeQ/0?wx_fmt=png)

Dlink 可以对多版本的任意部署方式的 Flink 集群多实例进行接管，只需要将 Flink 的 JobManager 注册到 Dlink 中即可完成对接工作，此外 Dlink 无缝兼容 Flink 的所有拓展组件，即可以将 Flink 集群下的依赖加入 Dlink lib 下进行使用。

对于 HA 部署的 Flink 集群，只需要将 JobManager 可能出现的所有地址以英文逗号连接配置即可，如 "192.168.123.156:8081,192.168.123.157:8081,192.168.123.158:8081"，Dlink 会在执行任务的间隙自动检测 JobManager 的地址，确保当发生事故时任务仍可以正常提交与管理。

注意：目前 Dlink 主要支持 session 模式的任务执行方式。

未来将支持 application 和 perjob 。

### Flink SQL 开发

![](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTrh5UYychtscpfXuuKt16Cv1oWyfwgHbcYQGEyS0xg8SVVArEmPXVWVSQk2AGWO0cnh9C3ZtyXeJg/0?wx_fmt=png)

Dlink 提供了仿 IDE 的前端开发界面，支持语法高亮、自动补全等特性，左侧目录维护了 SQL 任务的管理，右侧配置维护了 SQL 任务的执行参数，上方提供了快捷功能按钮与当前状态信息，底部则提供了一系列的展示功能。

需要注意的是，0.3.0 版本的 Dlink 每个任务只能提交一个 Insert 或 Select 语句。点击右上方的执行按钮将当前页面的 SQL 与配置信息提交到后台进行执行。

未来将支持语句集的提交管理。

### Flink SQL  语法与逻辑检查

![](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTrh5UYychtscpfXuuKt16Cvhpib8mBVribEEEUacvddKxL28xwjWicwIoJ78YTGLgtqZ2dKWfAFOckTw/0?wx_fmt=png)

Dlink 提供了语句片段、AGGTABLE等增强特性，并提供了包含新特性的语法校验与执行图的生成校验，可帮助开发者在提交快速定位语句问题，再也不需要盲写后提交至 SQL-Client 等其他入口。

语句片段：

```sql
sf:=select * from;tb:=student;
${sf} ${tb}
##效果等同于
select * from student
```

AGGTABLE 标值聚合函数：

```sql
CREATE AGGTABLE aggdemo AS
SELECT myField,value,rank
FROM MyTable
GROUP BY myField
AGG BY TOP2(value) as (value,rank);
```

### Flink SQL 批流预览

![](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTrh5UYychtscpfXuuKt16CvJ1roopzpX0zSyC2gP1p3a7fZykXqn90k38wOjARrR9DHiajbQAldEQA/0?wx_fmt=png)

Dlink 提供了比 SQL-Client 更强大的数据预览功能，即 Dlink 可以实时预览流任务与批任务 Select 的执行结果，可帮助开发者快速获取语句的执行效果以及数据质量问题排查等工作，如同数据库连接工具一样流畅。

### Flink SQL 执行历史

![](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTrh5UYychtscpfXuuKt16CvDuLY9K4xalvoytxAlNjoR6Upf1v167rGicaPaAfIhibCGcEhOvzI7V1A/0?wx_fmt=png)

Dlink 提供了 SQL 的执行历史管理，记录了任务细节与执行信息，方便开发者快速追溯执行记录。

### Flink SQL 数据回放

![](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTrh5UYychtscpfXuuKt16CvLlyAyRYXPPkDrSYuKN6DB13wcHbLZ2qibbewQgXibeaWH8zOLq0lQyAQ/0?wx_fmt=png)

Dlink 通过执行历史的载体提供了历史任务的预览数据回放功能，无需再次执行即可还原当时数据状态。

### Flink SQL 异常排查

![](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTrh5UYychtscpfXuuKt16Cv7Ut058hld87PWgamiayRMz0X4eF8SkROnXGquVq5wc3OzkPf8tlWmGw/0?wx_fmt=png)

Dlink 在提交任务时记录了 SQL 在翻译、校验、生成、执行等过程的异常信息，帮助开发者快速定位问题根源。

未来将支持异常信息解决思路的管理。

### Flink SQL 进程监控

![](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTrh5UYychtscpfXuuKt16CvQKRDlwya3rEaJzzhVohZRTponJXnf4iaZ85Q8Vic8iaLLvcTIQJrJOZOQ/0?wx_fmt=png)

Dlink 对已注册的 Flink 集群进行了托管，可以进行实现监控、任务停止等操作。

未来将支持 Flink 集群所有功能的管理。

### Flink SQL 血缘分析

![](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTrh5UYychtscpfXuuKt16CvmrA22mmuFJ6QzAG5fEAMSbmMHXFsODzzrzOrFz3eGd7pEicN6fQupwg/0?wx_fmt=png)

Dlink 提供了表字段的血缘分析，通过 JobGraph 获取最真实的血缘结果，不受冗余语句的影响。

未来将支持全局血缘分析、影响分析等。

### Flink SQL 共享会话

![](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTrh5UYychtscpfXuuKt16Cvp0AcrZPNAuoe9yeHJLl1ztj7NUoMJx0I4GQwTYaQYI8Ldp6PWzhShQ/0?wx_fmt=png)

Dlink 提供了共享会话来进行 Catalog 的共享，使开发者可以在团队开发中灵活共享环境以及协助排查问题。

### Flink SQL 函数文档

![](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTrh5UYychtscpfXuuKt16Cvo3bmeQGbM3qMibOkbNGl6Uj8OibyR5CkOWp86YYlD6LDhZCX3VZLz9cA/0?wx_fmt=png)

Dlink 提供了 SQL 函数文档的管理，可以协助开发者快速查找相关功能的函数文档。

### 数据源注册

![](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTrh5UYychtscpfXuuKt16CvsFzqzCuMlFDrKYyBcuUQbEeicfJfHiaJk1e9Znfv2WvcAduSUsW02nsQ/0?wx_fmt=png)

Dlink 提供了外部数据源的管理与注册。

未来将扩展其他数据源。

### 元数据查询

![](https://mmbiz.qpic.cn/mmbiz_png/dyicwnSlTFTrh5UYychtscpfXuuKt16Cvwh7ES41AWxmujZtg0icuvzZc2WGRROgLv77devjaJ4p18X2Yv1ibklTA/0?wx_fmt=png)

Dlink 提供了数据源的元数据查询功能。

未来将支持 SQL 语句生成、Catalog 加载等。

## 部署

### 下载地址

链接：https://pan.baidu.com/s/1-2qIE01gj7v51GdqWw5rhw

提取码：0300

### 从源码编译

```java
dlink -- 父项目
|-dlink-admin -- 管理中心
|-dlink-client -- Client 中心
| |-dlink-client-1.12 -- Client-1.12 实现
| |-dlink-client-1.13 -- Client-1.13 实现
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
|-dlink-function -- 函数中心
|-dlink-metadata -- 元数据中心
| |-dlink-metadata-base -- 元数据基础组件
| |-dlink-metadata-clickhouse -- 元数据- clickhouse 实现
| |-dlink-metadata-mysql -- 元数据- mysql 实现
| |-dlink-metadata-oracle -- 元数据- oracle 实现
| |-dlink-metadata-postgresql -- 元数据- postgresql 实现
|-dlink-web -- React 前端
```

一键打包至根本目录 build 文件夹：

```shell
mvn clean install -Dmaven.test.skip=true
```

### 应用结构

```java
config/ -- 配置文件
|- application.yml
lib/ -- 外部依赖及Connector
|- dlink-client-1.12.jar -- 必需,二选一
|- dlink-client-1.13.jar -- 必需，二选一
|- dlink-metadata-jdbc.jar
|- dlink-connector-jdbc.jar
|- dlink-function-0.2.3.jar
|- flink-connector-jdbc_2.11-1.12.4.jar
|- flink-csv-1.12.4.jar
|- flink-json-1.12.4.jar
|- mysql-connector-java-8.0.21.jar
sql/
|- dinky.sql --Mysql初始化脚本
|- upgrade/ -- 各个版本升级SQL脚本
auto.sh -- 启动停止脚本
dlink-admin.jar -- 程序包
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

### 初始化数据库

在 Mysql 数据库中执行 dinky.sql 脚本。

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

## 扩展

### Connector

遵循 Flink Connector 扩展原则，Flink 集群上的 Connector 可以直接加入 lib 目录下进行扩展，重启生效。

### Function

遵循 Flink Function 扩展原则，无需 main 方法，直接加入 lib 目录下进行扩展，重启生效。

### MetaData

基于 SPI 扩展，可见源码示例，加入 lib 目录下进行扩展，重启生效。

## 未来

Dlink 0.4.0 将于 0.3.0 功能完善后提上日程，主要包含企业级应用功能如时间调度、依赖调度、数据地图等。

Dlink 将紧跟 Flink 官方社区发展，为推广及发展 Flink 的应用而奋斗，打造 FlinkSQL 的最佳搭档的形象。

与此同时，DataLink 数据中台将同步发展，未来将提供开源的企业级数据中台解决方案。

### 交流

欢迎您加入社区交流分享与批评，也欢迎您为社区贡献自己的力量。

QQ社区群：**543709668**，申请备注 “ Dlink ”，不写不批