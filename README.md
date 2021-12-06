# Dlink

## 简介

Dlink 为 Apache Flink 而生，让 Flink SQL 更加丝滑。它是一个交互式的 FlinkSQL Studio，可以在线开发、补全、校验 、执行、预览 FlinkSQL，支持 Flink 官方所有语法及其增强语法，并且可以同时对多 Flink 集群实例进行提交、停止、SavePoint 等运维操作，如同您的 IntelliJ IDEA For Flink SQL。

需要注意的是，Dlink 它更专注于 FlinkSQL 的应用，而不是 DataStream。在开发过程中您不会看到任何一句 java、scala 或者 python。所以，它的目标是基于 100% FlinkSQL 来实现批流一体的实时计算平台。

站在巨人肩膀上开发与创新，Dlink 在未来批流一体的发展趋势下潜力无限。


## 原理

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/040/Dlink_principle.png)

## 功能

注意：只表明核心功能，不包括细节。

|         域          |                 概要                 |  进展  |
| :-----------------: | :----------------------------------: | :----: |
|      基本管理       |         作业及Savepoint管理          | 已实现 |
|                     |             FlinkSQL管理             | 已实现 |
|                     |            Flink 集群管理            | 已实现 |
|                     |          Flink 集群配置管理          | 已实现 |
|                     |              数据源管理              | 已实现 |
|                     |               文档管理               | 已实现 |
|                     |               系统配置               | 已实现 |
|                     |               用户管理               | 已实现 |
|  FlinkSQL 语法增强  |             SQL 片段语法             | 已实现 |
|                     |            AGGTABLE 语法             | 已实现 |
|                     |                语句集                | 已实现 |
|                     |       支持 sql-client 所有语法       | 已实现 |
| FlinkSQL 交互式开发 |        会话的 connector 查询         | 已实现 |
|                     |               语法检查               | 已实现 |
|                     |              执行图校验              | 已实现 |
|                     |      上下文元数据自动提示与补全      | 已实现 |
|                     |            自定义代码补全            | 已实现 |
|                     |              关键字高亮              | 已实现 |
|                     |           结构折叠与缩略图           | 已实现 |
|                     |             支持选中提交             | 已实现 |
|                     |               布局拖拽               | 已实现 |
|                     |      SELECT、SHOW等语法数据预览      | 已实现 |
|                     |           JobGraph 图预览            | 已实现 |
|   Flink 任务运维    |          standalone SQL提交          | 已实现 |
|                     |         yarn session SQL提交         | 已实现 |
|                     |         yarn per-job SQL提交         | 已实现 |
|                     |       yarn application SQL提交       | 已实现 |
|                     |       yarn application Jar提交       | 已实现 |
|                     |             作业 Cancel              | 已实现 |
|                     |  作业 SavePoint Cancel,Stop,Trigger  | 已实现 |
|                     |        作业从 SavePoint 恢复         | 已实现 |
|     元数据功能      |    Flink Catelog 浏览（connector)    | 已实现 |
|                     |         外部数据源元数据浏览         | 已实现 |
|      共享会话       | 支持 Session 集群 Catelog 持久与浏览 | 已实现 |
|                     |          支持共享与私有会话          | 已实现 |
|   Flink 集群中心    |        手动注册 Session 集群         | 已实现 |
|                     | 自动注册 per-job 和 application 集群 | 已实现 |

## 部署

### 版本

抢先体验( main 主支)：dlink-0.5.0-SNAPSHOT

稳定版本( 0.4.0 分支)：dlink-0.4.0

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
|- dlink.sql --Mysql初始化脚本
auto.sh --启动停止脚本
dlink-admin.jar --程序包
```

解压后结构如上所示，修改配置文件内容。lib 文件夹下存放 dlink 自身的扩展文件，plugins 文件夹下存放 flink 及 hadoop 的官方扩展文件。其中 plugins 中的所有 jar 需要根据版本号自行下载并添加，才能体验完整功能，当然也可以放自己修改的 Flink 源码编译包。extends 文件夹只作为扩展插件的备份管理，不会被 dlink 加载。

在Mysql数据库中创建数据库并执行初始化脚本。

执行以下命令管理应用。

```shell
sh auto.sh start
sh auto.sh stop
sh auto.sh restart
sh auto.sh status
```

前端 Nginx 部署：
    	将 html 文件夹上传至 nginx 的 html 文件夹下，修改 nginx 配置文件并重启。

```shell
    server {
        listen       9999;
        server_name  localhost;

		# gzip config
		gzip on;
		gzip_min_length 1k;
		gzip_comp_level 9;
		gzip_types text/plain application/javascript application/x-javascript text/css application/xml text/javascript application/x-httpd-php image/jpeg image/gif image/png;
		gzip_vary on;
		gzip_disable "MSIE [1-6]\.";

        #charset koi8-r;

        #access_log  logs/host.access.log  main;

        location / {
            root   html;
            index  index.html index.htm;
			try_files $uri $uri/ /index.html;
        }

        #error_page  404              /404.html;

        # redirect server error pages to the static page /50x.html
        #
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
3.  将 html 文件夹下打包好的前端资源上传到 nginx 的 html 文件夹中，重启 nginx，访问即可。

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

将 Flink 集群上已扩展好的 Connector 和 UDF 直接放入 Dlink 的 lib 或者 plugins 下，然后重启即可。定制 Connector 过程同 Flink 官方一样。

#### 扩展Metadata

遵循SPI。

#### 扩展其他版本的Flink

Flink 的版本取决于 lib 下的 dlink-client-1.13.jar。当前版本默认为 Flink 1.13.3 API。向其他版本的集群提交任务可能存在问题，已实现 1.11、1.12、1.13, 1.14，切换版本时只需要将对应依赖在lib下进行替换，然后重启即可。

切换版本时需要同时更新 plugins 下的 Flink 依赖。

## 使用手册

### 基础使用

#### 登录

超级管理员：admin/admin；

新增用户：默认密码 123456。

#### 集群中心

注册 Flink 集群地址时，格式为 host:port ，用英文逗号分隔。即添加 Flink 集群的 JobManager 的 RestApi 地址。当 HA 模式时，地址间用英文逗号分隔，例如：192.168.123.101:8081,192.168.123.102:8081,192.168.123.103:8081。新增和修改的等待时间较长，是因为需要检测最新的 JobManager 地址。心跳检测为手动触发，会更新集群状态与 JobManager 地址。

#### Studio

1. 在左侧目录区域创建文件夹或任务。
2. 在中间编辑区编写 FlinkSQL 。
3. 在右侧配置作业配置和执行参数。
4. Fragment 开启后，可以使用增强的 sql 片段语法：

```sql
sf:=select * from;tb:=student;
${sf} ${tb}
##效果等同于
select * from student
```

5. 内置 sql 增强语法-表值聚合：

```sql
CREATE AGGTABLE aggdemo AS
SELECT myField,value,rank
FROM MyTable
GROUP BY myField
AGG BY TOP2(value) as (value,rank);
```

6. MaxRowNum 为批流（Session模式下）执行Select时预览查询结果的最大集合长度，默认 100，最大 9999。
7. SavePoint策略支持最近一次、最早一次、指定一次三种策略。
8. Flink 共享会话共享 Catalog ，会话的使用需要在左侧会话选项卡手动创建并维护。
9. 连接器为 Catalog 里的表信息，清空按钮会销毁当前会话。
10. Local 模式主要用于语法校验、血缘分析、执行图预览等功能，当然也可执行任务，但目前版本建议请使用远程集群来执行任务。
11. 执行 SQL 时，如果您选中了部分 SQL，则会执行选中的内容，否则执行全部内容。
12. 小火箭的提交功能是异步提交当前任务已保存的 FlinkSQL 及配置到集群。由于适用于快速提交稳定的任务，所以无法提交草稿，且无法预览数据。
13. 执行信息或者历史中那个很长很长的就是集群上的 JobId 或者 APPID，任务历史可以查看执行过的任务的数据回放。
14. 草稿是无法被异步远程提交的，只能同步执行，且无法保存。
15. Studio 的布局可以随意拖动，但由于是实时计算，联动较多，请温柔些。
16. 同步执行时可以自由指定任务名，异步提交默认为作业名。
17. 支持 set 语法设置 Flink 的执行配置，其优先级大于右侧的配置。
18. 支持远程集群查看、SavePoint 及停止任务。
19. 支持自定义及上下文的 sql 函数或片段的自动补全，通过函数文档维护。
20. 支持 Flink 所有官方的连接器及插件的扩展，但需注意版本号适配。
21. 使用 IDEA 进行源码调试时，需要在 admin 及 core 下修改相应 pom 依赖的引入来完成功能的加载。
22. 支持基于 StreamGraph 的可执行 FlinkSql （Insert into）的血缘分析，无论你的 sql 有多复杂或者多 view。
23. Dlink 目前提交方式支持 Standalone 、Yarn Session、Yarn PerJob、Yarn Application，K8S 后续支持。
24. Dlink 目前对于 Flink 多版本的支持只能一个 Dlink 实例支持一个 Flink 版本，未来将开源同时支持多版本的能力。
25. 使用 Yarn PerJob、Yarn Application 需要配置集群配置，且其自动注册的集群实例需要手动点击回收。
26. 其他内容后续更新。。。

#### 使用技巧

1.[Flink AggTable 在 Dlink 的实践](https://github.com/DataLinkDC/dlink/blob/main/dlink-doc/doc/FlinkAggTable%E5%9C%A8Dlink%E7%9A%84%E5%BA%94%E7%94%A8.md)

2.[Dlink 概念原理与源码扩展介绍](https://github.com/DataLinkDC/dlink/blob/main/dlink-doc/doc/Dlink%E6%A0%B8%E5%BF%83%E6%A6%82%E5%BF%B5%E4%B8%8E%E5%AE%9E%E7%8E%B0%E5%8E%9F%E7%90%86%E8%AF%A6%E8%A7%A3.md)

3.[Dlink-0.3.0重磅来袭](https://github.com/DataLinkDC/dlink/blob/main/dlink-doc/doc/Dlink0.3.0%E9%87%8D%E7%A3%85%E6%9D%A5%E8%A2%AD.md)

4.[Dlink 实时计算平台——部署篇](https://github.com/DataLinkDC/dlink/blob/dev/dlink-doc/doc/Dlink%E5%AE%9E%E6%97%B6%E8%AE%A1%E7%AE%97%E5%B9%B3%E5%8F%B0%E2%80%94%E2%80%94%E9%83%A8%E7%BD%B2%E7%AF%87.md)

5.[Dlink-0.3.2更新说明](https://github.com/DataLinkDC/dlink/blob/main/dlink-doc/doc/Dlink-0.3.2%E6%9B%B4%E6%96%B0%E8%AF%B4%E6%98%8E.md)

6.[Dlink 读写 Hive 的实践](https://github.com/DataLinkDC/dlink/blob/dev/dlink-doc/doc/Dlink%E8%AF%BB%E5%86%99Hive%E7%9A%84%E5%AE%9E%E8%B7%B5.md)

7.[Dlink On Yarn 三种 Flink 执行方式的实践](https://github.com/DataLinkDC/dlink/blob/main/dlink-doc/doc/DlinkOnYarn%E4%B8%89%E7%A7%8DFlink%E6%89%A7%E8%A1%8C%E6%96%B9%E5%BC%8F%E7%9A%84%E5%AE%9E%E8%B7%B5.md)

#### 常见问题及解决

期待您提出问题。

## 技术栈

[Apache Flink](https://github.com/apache/flink)

[Mybatis Plus](https://github.com/baomidou/mybatis-plus)

[ant-design-pro](https://github.com/ant-design/ant-design-pro)

[Monaco Editor](https://github.com/Microsoft/monaco-editor)

[SpringBoot]()

## 近期计划

1.支持同时托管多版本的Flink实例

2.支持K8S多种运行模式

3.支持多种任务调度框架接口

4.支持UDF动态加载

5.完善Studio交互功能

## 交流与贡献

欢迎您加入社区交流分享，也欢迎您为社区贡献自己的力量。

在此有意向参与代码及文档贡献或积极测试者可以私信我加入 Dlink Contributors 群聊进一步了解。

dlink将正式开始社区积极的发展阶段，社区的主旨是开放、交流、创新、共赢，dlink的核心理念是创新，即不受思想约束地勇于尝试。dlink本就是一个创新型的解决方案，而不是模仿已有产品的思路按部就班，一味模仿对于社区及所有人的发展意义并不大，积极创新才可能独树一帜，并为大家带来更大的利益。无论您是否已经建成了自己的FlinkSQL平台，相信它一定会在创新的方向上为您带来些许启发。

在此非常感谢大家的支持~

QQ社区群：**543709668**，申请备注 “ Dlink ”，不写不批

微信社区群（推荐，大佬云集）：添加微信号 wenmo_ai 邀请进群，申请备注 “ Dlink ”，不写不批

公众号（最新消息获取建议关注）：[DataLink数据中台](https://mmbiz.qpic.cn/mmbiz_jpg/dyicwnSlTFTp6w4PuJruFaLV6uShCJDkzqwtnbQJrQ90yKDuuIC8tyMU5DK69XZibibx7EPPBRQ3ic81se5UQYs21g/0?wx_fmt=jpeg)

邮箱：aiwenmo@163.com

## 运行截图

> 登录页

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/040/login.png)

> 首页

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/040/welcome.png)

> Studio SQL 开发提示与补全

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/040/sqldev.png)

> Studio 语法和逻辑检查

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/040/sqlcheck.png)

> Studio 批流SELECT预览

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/040/selectpreview.png)

> Studio 异常反馈

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/040/sqlerror.png)

> Studio 进程监控

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/040/process.png)

> Studio 执行历史

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/040/history.png)

> Studio 数据回放

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/040/datashow.png)

> Studio SavePoint 管理

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/040/savepoint.png)

> Studio 血缘分析

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/040/ca.png)

> Studio 函数浏览

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/040/function.png)

> Studio 共享会话

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/040/session.png)

> 集群管理

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/040/cluster.png)


> 集群配置管理

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/040/clusterconfiguration.png)

> 数据源管理

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/040/db.png)
