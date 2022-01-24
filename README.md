# Dinky 

## 简介

实时即未来，Dinky 为 Apache Flink 而生，让 Flink SQL 纵享丝滑，并致力于实时计算平台建设。

Dinky 架构于 Apache Flink，增强 Flink 的应用与体验，探索流式数仓。即站在巨人肩膀上创新与实践，Dinky 在未来批流一体的发展趋势下潜力无限。

最后，Dinky 的发展皆归功于 Apache Flink 等其他优秀的开源项目的指导与成果。

## 由来

Dinky（原 Dlink）：

1.Dinky 英译为 “ 小巧而精致的 ” ，最直观的表明了它的特征：轻量级但又具备复杂的大数据开发能力。

2.为 “ Data Integrate No Knotty ” 的首字母组合，英译 “ 数据整合不难 ”，寓意 “ 易于建设批流一体平台及应用 ”。

3.从 Dlink 改名为 Dinky 过渡平滑，更加形象的阐明了开源项目的目标，始终指引参与者们 “不忘初心，方得始终 ”。

## 原理

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/main/dinky_principle.png)

## 功能

注意：以下功能均为对应版本已实现的功能，实测可用。

|   应用    |     方向     | 功能                                        |  进展   |
|:-------:|:----------:|-------------------------------------------|:-----:|
|  开发中心   |  FlinkSQL  | 支持 sql-client 所有语法                        | 0.4.0 |
|         |            | 支持 Flink 所有 Configuration                 | 0.4.0 |
|         |            | 支持 Flink 所有 Connector                     | 0.4.0 |
|         |            | 支持 SELECT、SHOW、DESC 等查询实时预览               | 0.4.0 |
|         |            | 支持 INSERT 语句集                             | 0.4.0 |
|         |            | 新增 SQL 片段语法                               | 0.4.0 |
|         |            | 新增 AGGTABLE 表值聚合语法及 UDATF 支持              | 0.4.0 |
|         |            | 新增 FlinkSQLEnv 执行环境复用                     | 0.5.0 |
|         |            | 新增 Flink Catalog 交互查询                     | 0.4.0 |
|         |            | 新增 执行环境的共享与私有会话机制                         | 0.4.0 |
|         |            | 新增 多种方言的作业目录管理（FlinkSQL、SQL、Java）         | 0.5.0 |
|         |            | 新增 作业配置与执行配置管理                            | 0.4.0 |
|         |            | 新增 基于 Explain 的语法校验与逻辑解析                  | 0.4.0 |
|         |            | 新增 JobPlan 图预览                            | 0.5.0 |
|         |            | 新增 基于 StreamGraph 的表级血缘分析                 | 0.4.0 |
|         |            | 新增 基于上下文元数据自动提示与补全                        | 0.4.0 |
|         |            | 新增 自定义规则的自动提示与补全                          | 0.4.0 |
|         |            | 新增 关键字高亮与代码缩略图                            | 0.4.0 |
|         |            | 新增 选中片段执行                                 | 0.4.0 |
|         |            | 新增 布局拖拽                                   | 0.4.0 |
|         |            | 新增 SQL导出                                  | 0.5.0 |
|         |            | 新增 快捷键保存、校验、美化                            | 0.5.0 |
|         |            | 支持 local 模式下 FlinkSQL 提交                  | 0.4.0 |
|         |            | 支持 standalone 模式下 FlinkSQL 提交             | 0.4.0 |
|         |            | 支持 yarn session 模式下 FlinkSQL 提交           | 0.4.0 |
|         |            | 支持 yarn per-job 模式下 FlinkSQL 提交           | 0.4.0 |
|         |            | 支持 yarn application 模式下 FlinkSQL 提交       | 0.4.0 |
|         |            | 支持 kubernetes session 模式下 FlinkSQL 提交     | 0.5.0 |
|         |            | 支持 kubernetes application 模式下 FlinkSQL 提交 | 0.5.0 |
|         |            | 支持 UDF Java 方言Local模式在线编写、调试、动态加载         | 0.5.0 |
|         |  Flink 作业  | 支持 yarn application 模式下 Jar 提交            | 0.4.0 |
|         |            | 支持 k8s application 模式下 Jar 提交             | 0.5.0 |
|         |            | 支持 作业 Cancel                              | 0.4.0 |
|         |            | 支持 作业 SavePoint 的 Cancel、Stop、Trigger     | 0.4.0 |
|         |            | 新增 作业自动从 SavePoint 恢复机制（包含最近、最早、指定一次）     | 0.4.0 |
|         |  Flink 集群  | 支持 查看已注册集群的作业列表与运维                        | 0.4.0 |
|         |            | 新增 自动注册 Yarn 创建的集群                        | 0.4.0 |
|         |    SQL     | 新增 外部数据源的 SQL 校验                          | 0.5.0 |
|         |            | 新增 外部数据源的 SQL 执行与预览                       | 0.5.0 |
|         |     BI     | 新增 折线图的渲染                                 | 0.5.0 |
|         |            | 新增 条形图图的渲染                                | 0.5.0 |
|         |            | 新增 饼图的渲染                                  | 0.5.0 |
|         |    元数据     | 新增 查询外部数据源的元数据信息                          | 0.4.0 |
|         |     归档     | 新增 执行与提交历史                                | 0.4.0 |
|  运维中心   |     暂无     | 暂无                                        | 0.4.0 |
|  注册中心   | Flink 集群实例 | 新增 外部 Flink 集群实例注册                        | 0.4.0 |
|         |            | 新增 外部 Flink 集群实例心态检测与版本获取                 | 0.4.0 |
|         |            | 新增 外部 Flink 集群手动一键回收                      | 0.4.0 |
|         | Flink 集群配置 | 新增 Flink On Yarn 集群配置注册及测试                | 0.4.0 |
|         |  User Jar  | 新增 外部 User Jar 注册                         | 0.4.0 |
|         |    数据源     | 新增 Mysql 数据源注册及测试                         | 0.4.0 |
|         |            | 新增 Oracle 数据源注册及测试                        | 0.4.0 |
|         |            | 新增 postgreSql 数据源注册及测试                    | 0.4.0 |
|         |            | 新增 ClickHouse 数据源注册及测试                    | 0.4.0 |
| OpenApi |     调度     | 新增 submitTask 调度接口                        | 0.5.0 |
|         |  FlinkSQL  | 新增 executeSql 提交接口                        | 0.5.0 |
|         |            | 新增 explainSql 验证接口                        | 0.5.0 |
|         |            | 新增 getJobPlan 计划接口                        | 0.5.0 |
|         |            | 新增 getStreamGraph 计划接口                    | 0.5.0 |
|         |            | 新增 getJobData 数据接口                        | 0.5.0 |
|         |   Flink    | 新增 executeJar 提交接口                        | 0.5.0 |
|         |            | 新增 cancel 停止接口                            | 0.5.0 |
|         |            | 新增 savepoint 触发接口                         | 0.5.0 |
|   关于    |  关于 Dlink  | 版本更新记录                                    | 0.4.0 |

## 部署

### 版本

抢先体验( main 主支)：dlink-0.6.0-SNAPSHOT

稳定版本( 0.5.1 分支)：dlink-0.5.1

### 从安装包开始

```
config/ -- 配置文件
|- application.yml
extends/ -- 扩展
|- dlink-client-1.11.jar
|- dlink-client-1.12.jar
|- dlink-client-1.14.jar
html/ -- 前端编译产物，用于Nginx
jar/ -- dlink application模式提交sql用到的jar
lib/ -- 内部组件
|- dlink-client-1.13.jar -- 必需
|- dlink-connector-jdbc.jar
|- dlink-function.jar
|- dlink-metadata-clickhouse.jar
|- dlink-metadata-mysql.jar
|- dlink-metadata-oracle.jar
|- dlink-metadata-postgresql.jar
plugins/
|- flink-connector-jdbc_2.11-1.13.3.jar
|- flink-csv-1.13.3.jar
|- flink-dist_2.11-1.13.3.jar
|- flink-json-1.13.3.jar
|- flink-shaded-hadoop-3-uber-3.1.1.7.2.1.0-327-9.0.jar
|- flink-shaded-zookeeper-3.4.14.jar
|- flink-table-blink_2.11-1.13.3.jar
|- flink-table_2.11-1.13.3.jar
|- mysql-connector-java-8.0.21.jar
sql/ 
|- dlink.sql -- Mysql初始化脚本（首次部署执行这个）
|- dlink_history.sql -- Mysql各版本及时间点升级脚本
auto.sh --启动停止脚本
dlink-admin.jar --程序包
```

解压后结构如上所示，修改配置文件内容。lib 文件夹下存放 dlink 自身的扩展文件，plugins 文件夹下存放 flink 及 hadoop 的官方扩展文件（ 如果plugins下引入了flink-shaded-hadoop-3-uber 或者其他可能冲突的jar，请手动删除内部的 javax.servlet 等冲突内容）。其中 plugins 中的所有 jar 需要根据版本号自行下载并添加，才能体验完整功能，当然也可以放自己修改的 Flink 源码编译包。extends 文件夹只作为扩展插件的备份管理，不会被 dlink 加载。

请检查 plugins 下是否添加了 flink 对应版本的 flink-dist,flink-table,flink-shaded-hadoop-3-uber 等如上所示的依赖！！！
请检查 plugins 下是否添加了 flink 对应版本的 flink-dist,flink-table,flink-shaded-hadoop-3-uber 等如上所示的依赖！！！
请检查 plugins 下是否添加了 flink 对应版本的 flink-dist,flink-table,flink-shaded-hadoop-3-uber 等如上所示的依赖！！！

在Mysql数据库中创建 dlink 数据库并执行初始化脚本 dlink.sql。

执行以下命令管理应用。

```shell
sh auto.sh start
sh auto.sh stop
sh auto.sh restart
sh auto.sh status
```
前端快捷访问：
如果plugins下引入了flink-shaded-hadoop-3-uber 的jar，请手动删除内部的 javax.servlet 后既可以访问默认 8888 端口号（如127.0.0.1:8888），正常打开前端页面。

前后端分离部署—— Nginx 部署（推荐）：
Nginx 如何部署请见百度或谷歌。
将 html 文件夹上传至 nginx 的 html 文件夹下或者指定 nginx 配置文件的静态资源绝对路径，修改 nginx 配置文件并重启。

```shell
    server {
        listen       9999;
        server_name  localhost;

		gzip on;
		gzip_min_length 1k;
		gzip_comp_level 9;
		gzip_types text/plain application/javascript application/x-javascript text/css application/xml text/javascript application/x-httpd-php image/jpeg image/gif image/png;
		gzip_vary on;
		gzip_disable "MSIE [1-6]\.";

        location / {
            root   html;
            index  index.html index.htm;
			try_files $uri $uri/ /index.html;
        }

        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }

        location ^~ /api {
            proxy_pass http://127.0.0.1:8888;
            proxy_set_header   X-Forwarded-Proto $scheme;
            proxy_set_header   X-Real-IP         $remote_addr;
        }
    }
```

1.  server.listen 填写前端访问端口
2.  proxy_pass 填写后端地址如 http://127.0.0.1:8888
3.  将 html 文件夹下打包好的前端资源上传到 nginx 的 html 文件夹中，如果 nginx 已经启动，则执行 nginx -s reload 重载配置，访问即可。

### 从源码编译

#### 项目目录

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
| |-config -- 配置文件
| |-doc -- 使用文档
| |-extends -- Docker K8S模板
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
```

#### 编译打包

以下环境版本实测编译成功：

|  环境   |   版本    |
| :-----: | :-------: |
|   npm   |  7.19.0   |
| node.js |  14.17.0  |
|   jdk   | 1.8.0_201 |
|  maven  |   3.6.0   |
| lombok  |  1.18.16  |
|  mysql  |   5.7+    |

```shell
mvn clean install -Dmaven.test.skip=true
```

#### 扩展Connector及UDF

将 Flink 集群上已扩展好的 Connector 和 UDF 直接放入 Dlink 的 plugins 下，然后重启即可。定制 Connector 过程同 Flink 官方一样。

#### 扩展Metadata

遵循SPI。请参考 dlink-meta-mysql 的实现。

#### 扩展其他版本的Flink

Flink 的版本取决于 lib 下的 dlink-client-1.13.jar。当前版本默认为 Flink 1.13.5 API。向其他版本的集群提交任务可能存在问题，已实现 1.11、1.12、1.13, 1.14，切换版本时只需要将对应依赖在lib下进行替换，然后重启即可。

切换版本时需要同时更新 plugins 下的 Flink 依赖。

## 使用手册

1.[Flink AggTable 在 Dlink 的实践](https://github.com/DataLinkDC/dlink/blob/main/dlink-doc/doc/FlinkAggTable%E5%9C%A8Dlink%E7%9A%84%E5%BA%94%E7%94%A8.md)

2.[Dlink 概念原理与源码扩展介绍](https://github.com/DataLinkDC/dlink/blob/main/dlink-doc/doc/Dlink%E6%A0%B8%E5%BF%83%E6%A6%82%E5%BF%B5%E4%B8%8E%E5%AE%9E%E7%8E%B0%E5%8E%9F%E7%90%86%E8%AF%A6%E8%A7%A3.md)

3.[Dlink-0.3.0重磅来袭](https://github.com/DataLinkDC/dlink/blob/main/dlink-doc/doc/Dlink0.3.0%E9%87%8D%E7%A3%85%E6%9D%A5%E8%A2%AD.md)

4.[Dlink 实时计算平台——部署篇](https://github.com/DataLinkDC/dlink/blob/dev/dlink-doc/doc/Dlink%E5%AE%9E%E6%97%B6%E8%AE%A1%E7%AE%97%E5%B9%B3%E5%8F%B0%E2%80%94%E2%80%94%E9%83%A8%E7%BD%B2%E7%AF%87.md)

5.[Dlink-0.3.2更新说明](https://github.com/DataLinkDC/dlink/blob/main/dlink-doc/doc/Dlink-0.3.2%E6%9B%B4%E6%96%B0%E8%AF%B4%E6%98%8E.md)

6.[Dlink 读写 Hive 的实践](https://github.com/DataLinkDC/dlink/blob/dev/dlink-doc/doc/Dlink%E8%AF%BB%E5%86%99Hive%E7%9A%84%E5%AE%9E%E8%B7%B5.md)

7.[Dlink On Yarn 三种 Flink 执行方式的实践](https://github.com/DataLinkDC/dlink/blob/main/dlink-doc/doc/DlinkOnYarn%E4%B8%89%E7%A7%8DFlink%E6%89%A7%E8%A1%8C%E6%96%B9%E5%BC%8F%E7%9A%84%E5%AE%9E%E8%B7%B5.md)

8.[Dlink 在 Flink-mysql-cdc 到 Doris 的实践](https://github.com/DataLinkDC/dlink/blob/main/dlink-doc/doc/Dlink%E5%9C%A8Flink-mysql-cdc%E5%88%B0Doris%E7%9A%84%E5%AE%9E%E8%B7%B5.md)

## 技术栈

[Apache Flink](https://github.com/apache/flink)

[Mybatis Plus](https://github.com/baomidou/mybatis-plus)

[ant-design-pro](https://github.com/ant-design/ant-design-pro)

[Monaco Editor](https://github.com/Microsoft/monaco-editor)

[SpringBoot]()

## 致谢

感谢 [JetBrains](https://www.jetbrains.com/?from=dlink) 提供的免费开源 License 赞助

[![JetBrains](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/main/jetbrains.svg)](https://www.jetbrains.com/?from=dlink)

## 近期计划

1.任务生命周期管理

2.作业监控及运维

3.流作业自动恢复

4.作业日志查看

5.钉钉报警和推送

## 交流与贡献

欢迎您加入社区交流分享，也欢迎您为社区贡献自己的力量。

在此非常感谢大家的支持~

QQ社区群：**543709668**，申请备注 “ Dinky ”，不写不批

微信社区群（推荐）：添加微信号 wenmo_ai 邀请进群，申请备注 “ Dinky + 企业名 + 职位”，不写不批

公众号（最新消息获取建议关注）：[DataLink数据中台](https://mmbiz.qpic.cn/mmbiz_jpg/dyicwnSlTFTp6w4PuJruFaLV6uShCJDkzqwtnbQJrQ90yKDuuIC8tyMU5DK69XZibibx7EPPBRQ3ic81se5UQYs21g/0?wx_fmt=jpeg)

## 运行截图

> 登录页

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/050/050login.png)

> 首页

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/050/050home.png)

> FlinkSQL Studio

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/050/050flinksqlstudio.png)

> 自动补全

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/050/050autocomplete.png)

> ChangeLog 预览

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/050/050changelog.png)

> BI 折线图

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/050/050line.png)

> Table 预览

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/050/050table.png)

> 语法校验和逻辑检查

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/050/050check.png)

> JobPlan 预览

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/050/050jobplan.png)

> FlinkSQL 导出

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/050/050export.png)

> 血缘分析

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/050/050ca.png)

> Savepoint 管理

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/050/050savepoint.png)

> 共享会话

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/050/050session.png)

> 元数据

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/050/050metadata.png)

> 集群实例

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/050/050cluster.png)


> 集群配置

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/050/050clusterconf.png)
