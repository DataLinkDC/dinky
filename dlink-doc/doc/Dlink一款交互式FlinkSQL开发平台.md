# Dlink ？一款交互式FlinkSQL开发平台

## 背景

目前 Flink 社区正如火如荼的发展，但苦于没有一款适合 Flink SQL 界面化开发的工具，于是增加了 Flink 的门槛与成本。虽然官方提供了 SQL Client，但仍有很多局限与不方便。

对于开源的 Flink 平台主要有 DTStack 的 FlinkStreamSQL及Flinkx、zhp8341 的 flink-streaming-platform-web、streamxhub 的 streamx 以及 apache zeppelin 等项目，其中每个平台的各有所长，若做比较的话，还请关注后续文章。

本文将为您带来一款全新的创新型的交互式 FlinkSQL 开发平台—— Dlink。

## 简介

Dlink 为 Apache Flink 而生，让 Flink SQL 更加丝滑。它是一个 C/S 架构的 FlinkSQL Studio，可以交互式开发、补全、校验 、执行、预览 FlinkSQL，支持 Flink 官方所有语法及其增强语法，并且可以同时对多 Flink 集群实例进行提交、停止、SavePoint 等运维操作，如同您的 IntelliJ IDEA For Flink SQL。

需要注意的是，Dlink 它更专注于 FlinkSQL 的应用，而不是 DataStream。在开发过程中您不会看到任何一句 java、scala 或者 python。所以，它的目标是基于 100% FlinkSQL 来实现批流一体的实时计算平台。

站在巨人肩膀上开发，Dlink 在未来批流一体的发展趋势下潜力无限。

## 原理

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/040/Dlink_principle.png)

其原理并非本文的核心内容，一图带过。

## 功能

以下功能为 dlink-0.4.0 的功能展示。

|        概要         |                    细节                    |
| :-----------------: | :----------------------------------------: |
|      基本管理       |                  作业管理                  |
|                     |         FlinkSQL 及 Savepoint 管理         |
|                     |          Flink 集群实例及配置管理          |
|                     |               用户 Jar 管理                |
|                     |               外部数据源管理               |
|                     |                  文档管理                  |
|                     |                  系统配置                  |
|                     |                  用户管理                  |
|  FlinkSQL 语法增强  |                SQL 片段语法                |
|                     |               AGGTABLE 语法                |
|                     |                   语句集                   |
|                     |          支持 sql-client 所有语法          |
| FlinkSQL 交互式开发 |          会话 Catalog 查询及管理           |
|                     |                SQL 语法检查                |
|                     |               SQL 执行图校验               |
|                     |         上下文元数据自动提示与补全         |
|                     |           支持自定义代码补全规则           |
|                     |                 关键字高亮                 |
|                     |              结构折叠与缩略图              |
|                     |                支持选中提交                |
|                     |                  布局拖拽                  |
|                     |         SELECT、SHOW等语法数据预览         |
|                     |        血缘分析 及 StreamGraph 预览        |
|   Flink 任务运维    |             standalone SQL提交             |
|                     |            yarn session SQL提交            |
|                     |            yarn per-job SQL提交            |
|                     |          yarn application SQL提交          |
|                     |          yarn application Jar提交          |
|                     |                作业 Cancel                 |
|                     |     作业 SavePoint Cancel,Stop,Trigger     |
|                     |       作业从 SavePoint 恢复多种机制        |
|     元数据功能      |       Flink Catelog 浏览（connector)       |
|                     |            外部数据源元数据浏览            |
|      共享会话       |    支持 Session 集群 Catelog 持久与浏览    |
|                     |             支持共享与私有会话             |
|   Flink 集群中心    |           手动注册 Session 集群            |
|                     | 自动注册及回收 per-job 和 application 集群 |

## 优势

### 支持 Local、Standalone、Yarn-Session、Yarn-Per-Job、Yarn-Application 五种执行模式的 FlinkSQL 提交

Dlink 内置 Flink 的 Local 环境，可以在环境隔离下进行语法校验、计算血缘关系、预览StreamGraph、生成 JobGraph 以及提交FlinkSQL 任务。Dlink 对 Flink 进行的语法增强以及其他处理在所有模式下都是生效的，所以您可以轻松的把 FlinkSQL 切换到其他的执行模式下，常用于生产与测试集群隔离下的开发及调试辅助。

后续版本将开发 K8S 的相关支持。

### 支持用户 Jar 管理与提交

Dlink 也支持用户编译的可执行 Jar 的管理与提交。当前版本下您需要把需要提交的 Jar 注册到 dlink 中，dlink便可以提交相关配置到   Yarn 进行任务提交。

### 支持仿 IDEA 的 FlinkSQL 开发控制台

Dlink 的最大亮点就是支持 FlinkSQL 的交互式开发，您可以通过网页进行 FlinkSQL 的开发与调试，主要包含布局拖拽、关键字高亮、自动提示与补全、语法校验、StreamGraph 预览、血缘分析、作业及执行配置、SELECT及SHOW预览、SQL 提交、任务管理等其他功能。

### 支持作业 SavePoint 触发、恢复及其管理

Dlink 支持对 FlinkSQL 作业 和 Jar 作业进行 SavePoint 的 trigger、stop、cancel 操作，以及最近一次、最早一次、指定一次的自动恢复机制，当然也记录并管理了所产生的所有 SavePoint 信息。

### 支持 Flink 社区所有连接器及插件

由于 Dlink 是基于 Flink 源码二次开发的交互式开发工具，所以理论上它可以支持 Flink 的所有特性及插件，甚至您可以将您修改编译后的Flink源码轻易地接入 Dlink。

### 支持 Flink 多版本的切换

Dlink 支持 Flink 1.11、1.12、1.13、1.14 间版本的运行环境切换。

### 支持 sql-client 的所有语法及增强语法

Dlink 可以看作是一个 sql-client 的 web 版，不过它的功能远超 sql-client 所开放的功能。Dlink 提供语句片段、 AGGTABLE 表值聚合语法以及语句集提交。

语句片段：

```sql
sf:=select * from;tb:=student;
${sf} ${tb}
##效果等同于
select * from student
```

AGGTABLE 表值聚合:

```sql
CREATE AGGTABLE aggdemo AS
SELECT myField,value,rank
FROM MyTable
GROUP BY myField
AGG BY TOP2(value) as (value,rank);
```

### 支持批流的 SELECT 及 SHOW 结果预览

Dlink 如同 sql-client 一样，可以实时查看 Select 和 Show 语句的执行结果，并且支持表格查询、历史回溯。

### 支持 Session 执行模式的会话管理

Dlink 可以基于 Session 集群来创建共享与私有会话，可以在团队开发中共享及管理 Catalog 环境，便于协作排查问题。

### 支持外部 Flink 集群的任务运维

Dlink 可以对外部 Flink 集群实例进行托管，统一进行任务运维。

### 支持语法及逻辑校验

Dlink 可以对 FlinkSQL 进行执行环境中的语法及逻辑校验。

### 支持血缘分析

Dlink 支持基于 StreamGraph 的血缘分析计算及展示。

### 支持执行历史

Dlink 支持对所有通过 Dlink 提交的任务进行历史归档及管理。

### 支持异常反馈

Dlink 可以将 Flink 语句在执行过程中的异常完整的反馈到前端页面。

### 支持文档管理

Dlink 提供文档管理，可以用于使用查询、自动补全等功能。不再需要担心字段有没有敲错、函数用法有没有记错等。

### 支持集群管理

Dlink 支持对外部的 Flink 集群实例进行注册、管理等操作，也可以对 perjob 与 application 任务创建的集群同步自动注册及回收。

### 支持外部数据源管理

Dlink 支持对外部数据源的管理，以便用于查询其元数据、生成 FlinkSQL 或者自动加载 Catalog。

### 支持用户验证及管理

Dlink 提供了简易的用户登录授权及管理。

### 部署简单门槛低

Dlink 部署极为简单，支持依赖 Mysql 和 Nginx ，使用门槛底。

### 对接或改造成本低

Dlink 后台基于 SpringBoot 框架与 Flink 源码编写，代码逻辑简单，对接或改造成本低。前端基于 react 的 Ant Design Pro 开发，修改简单且易扩展。 

### 代码及设计紧随时代发展

Dlink 的代码依赖与设计思路紧随各大社区发展，不会出现Flink源码版本或功能落后的限制局面。

### 项目目标定位专业

相比于其他开源项目，Dlink 的目标更加专一且专业。

### 潜力无限

站在巨人肩膀上开发，Dlink 在未来批流一体的发展趋势下潜力无限。

## 未来计划

### 支持 K8S 的任务执行方式

目前 Dlink 不支持 K8S 的 Flink 集群托管，后续将支持。

### 支持 UDF 的管理

目前 Dlink 的 UDF 没有进行管理且加载机制需要重启实例，后续将改进。

### 支持 RPC 架构部署

目前 Dlink 存在单点故障及依赖冲突问题，后续将通过 rpc 来改进。

### 支持 Flink 多版本集群实例同时托管

目前 Dlink 无法同时加载多版本的集群环境，后续将通过 rpc 来改进。

### 支持多种调度平台接口

目前 Dlink 不支持定时任务等功能，后续将开发调度接口与简易的定时任务管理。

### 完善功能细节

目前 Dlink 很多功能细节没有开发或者存在问题，后续将逐步完善。

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

> 元数据查询

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/040/metadata.png)

