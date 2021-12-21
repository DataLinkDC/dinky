## 简介

实时即未来，Dlink 为 Apache Flink 而生，让 Flink SQL 纵享丝滑。

Dlink 是一个交互式的 FlinkSQL Studio，可以在线开发、补全、校验 、执行、预览 FlinkSQL，支持 Flink 官方所有语法及其增强语法，并且可以同时对多 Flink 集群实例进行提交、停止、SavePoint 等运维操作，如同您的 IntelliJ IDEA For Flink SQL。

需要注意的是：Dlink 更专注于 FlinkSQL 的应用，而不是 DataStream。在开发过程中您不会看到任何一句 java、scala 或者 python。所以，它的目标是基于 100% FlinkSQL 来实现批流一体的实时计算平台。

值得惊喜的是：Dlink 的实现基于 Apache Flink 源码二次开发，支持其绝大多数特性与机制，而交互更加贴近 Flink 的功能与体验，并且紧随官方社区发展。即站在巨人肩膀上开发与创新，Dlink 在未来批流一体的发展趋势下潜力无限。

最后，Dlink 的发展皆归功于 Apache Flink 等其他优秀的开源项目的指导与成果。

## 原理

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/040/Dlink_principle.png)

### JobManager

JobManager 作为 Dlink 的作业管理的统一入口，负责 Flink 的各种作业执行方式及其他功能的调度。

### Executor

Executor 是 Dlink 定制的 FlinkSQL 执行器，来模拟真实的 Flink 执行环境，负责 FlinkSQL 的 Catalog 管理、UDF管理、片段管理、配置管理、语句集管理、语法校验、逻辑验证、计划优化、生成 JobGraph、本地执行、远程提交、SELECT 及 SHOW 预览等核心功能。

### Interceptor

Interceptor 是 Dlink 的 Flink 执行拦截器，负责对其进行片段解析、UDF注册、SET 和 AGGTABLE 等增强语法解析。

### Gateway

Gateway 并非是开源项目 flink-sql-gateway，而是 Dlink 自己定制的 Gateway，负责进行基于 Yarn 环境的任务提交与管理，主要有Yarn-Per-Job 和 Yarn-Application  的 FlinkSQL 提交、停止、SavePoint 以及配置测试，而 User Jar 目前只开放了 Yarn-Application 的提交。

### Flink SDK

Dlink 主要通过调用 flink-client 和 flink-table 模块进行二次开发。

### Yarn SDK

Dlink 通过调用 flink-yarn 模块进行二次开发。

### Flink API

Dlink 也支持通过调用 JobManager 的 RestAPI 对任务进行管理等操作，系统配置可以控制开启和停用。

### Yarn-Session

Dlink 通过已注册的 Flink Session 集群实例可以对 Standalone 和 Yarn-Session 两种集群进行 FlinkSQL 的提交、Catalog 的交互式管理以及对 SELECT 和 SHOW 等语句的执行结果预览。

### Yarn-Per-Job

Dlink 通过已注册的集群配置来获取对应的 YarnClient 实例，然后将本地解析生产的 JobGraph 与 Configuration 提交至 Yarn 来创建 Flink-Per-Job 应用。

### Yarn-Application

Dlink 通过已注册的集群配置来获取对应的 YarnClient 实例。对于 User Jar，将 Jar 相关配置与 Configuration 提交至 Yarn 来创建 Flink-Application 应用；对于 Flink SQL，Dlink 则将作业 ID 及数据库连接配置作为 Main 入参和 dlink-app.jar 以及 Configuration 提交至 Yarn 来创建 Flink-Application 应用。

## 功能

注意：以下功能均为对应版本已实现的功能，实测可用。

|   应用   |      方向      | 功能                                                         | 进展  |
| :------: | :------------: | ------------------------------------------------------------ | :---: |
| 开发中心 |    FlinkSQL    | 支持 sql-client 所有语法                                     | 0.4.0 |
|          |                | 支持 Flink 所有 Configuration                                | 0.4.0 |
|          |                | 支持 Flink 所有 Connector                                    | 0.4.0 |
|          |                | 支持 SELECT、SHOW 等查询实时预览                             | 0.4.0 |
|          |                | 支持 INSERT 语句集                                           | 0.4.0 |
|          |                | 新增 SQL 片段语法                                            | 0.4.0 |
|          |                | 新增 AGGTABLE 表值聚合语法及 UDATF 支持                      | 0.4.0 |
|          |                | 新增 Flink Catalog 交互查询                                  | 0.4.0 |
|          |                | 新增 执行环境的共享与私有会话机制                            | 0.4.0 |
|          |                | 新增 多种方言的作业目录管理（FlinkSQL、SQL、Java）           | 0.5.0 |
|          |                | 新增 作业配置与执行配置管理                                  | 0.4.0 |
|          |                | 新增 基于 Explain 的语法校验与逻辑解析                       | 0.4.0 |
|          |                | 新增 JobPlan 图预览                                          | 0.5.0 |
|          |                | 新增 基于 StreamGraph 的表级血缘分析                         | 0.4.0 |
|          |                | 新增 基于上下文元数据自动提示与补全                          | 0.4.0 |
|          |                | 新增 自定义规则的自动提示与补全                              | 0.4.0 |
|          |                | 新增 关键字高亮与代码缩略图                                  | 0.4.0 |
|          |                | 新增 选中片段执行                                            | 0.4.0 |
|          |                | 新增 布局拖拽                                                | 0.4.0 |
|          |                | 支持 local 模式下 FlinkSQL 提交                              | 0.4.0 |
|          |                | 支持 standalone 模式下 FlinkSQL 提交                         | 0.4.0 |
|          |                | 支持 yarn session 模式下 FlinkSQL 提交                       | 0.4.0 |
|          |                | 支持 yarn per-job 模式下 FlinkSQL 提交                       | 0.4.0 |
|          |                | 支持 yarn application 模式下 FlinkSQL 提交                   | 0.4.0 |
|          |   Flink 作业   | 支持 yarn application 模式下 Jar 提交                        | 0.4.0 |
|          |                | 支持 作业 Cancel                                             | 0.4.0 |
|          |                | 支持 作业 SavePoint 的 Cancel、Stop、Trigger                 | 0.4.0 |
|          |                | 新增 作业自动从 SavePoint 恢复机制（包含最近、最早、指定一次） | 0.4.0 |
|          |   Flink 集群   | 支持 查看已注册集群的作业列表与运维                          | 0.4.0 |
|          |                | 新增 自动注册 Yarn 创建的集群                                | 0.4.0 |
|          |      SQL       | 新增 外部数据源的 SQL 校验                                   | 0.5.0 |
|          |                | 新增 外部数据源的 SQL 执行与预览                             | 0.5.0 |
|          |     元数据     | 新增 查询外部数据源的元数据信息                              | 0.4.0 |
|          |      归档      | 新增 执行与提交历史                                          | 0.4.0 |
| 运维中心 |      暂无      | 暂无                                                         | 0.4.0 |
| 注册中心 | Flink 集群实例 | 新增 外部 Flink 集群实例注册                                 | 0.4.0 |
|          |                | 新增 外部 Flink 集群实例心态检测与版本获取                   | 0.4.0 |
|          |                | 新增 外部 Flink 集群手动一键回收                             | 0.4.0 |
|          | Flink 集群配置 | 新增 Flink On Yarn 集群配置注册及测试                        | 0.4.0 |
|          |    User Jar    | 新增 外部 User Jar 注册                                      | 0.4.0 |
|          |     数据源     | 新增 Mysql 数据源注册及测试                                  | 0.4.0 |
|          |                | 新增 Oracle 数据源注册及测试                                 | 0.4.0 |
|          |                | 新增 postgreSql 数据源注册及测试                             | 0.4.0 |
|          |                | 新增 ClickHouse 数据源注册及测试                             | 0.4.0 |
|   关于   |   关于 Dlink   | 版本更新记录                                                 | 0.4.0 |

## 近期计划

1.支持同时托管多版本的Flink实例

2.支持K8S多种运行模式

3.支持多种任务调度框架接口

4.支持UDF动态加载

5.完善Studio交互功能

## 致谢

[Apache Flink](https://github.com/apache/flink)

[Mybatis Plus](https://github.com/baomidou/mybatis-plus)

[ant-design-pro](https://github.com/ant-design/ant-design-pro)

[Monaco Editor](https://github.com/Microsoft/monaco-editor)

[SpringBoot]()

[docsify](https://github.com/docsifyjs/docsify/)

此外，感谢 [JetBrains](https://www.jetbrains.com/?from=dlink) 提供的免费开源 License 赞助

[![JetBrains](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/main/jetbrains.svg)](https://www.jetbrains.com/?from=dlink)

## 交流与贡献

欢迎您加入社区交流分享，也欢迎您为社区贡献自己的力量。

在此有意向参与代码及文档贡献或积极测试者可以私信我加入 Dlink Contributors 群聊进一步了解。

Dlink 将正式开始社区积极的发展阶段，社区的主旨是开放、交流、创新、共赢，Dlink 的核心理念是创新，即不受思想约束地勇于尝试。Dlink 本就是一个创新型的解决方案，而不是模仿已有产品的思路按部就班，一味模仿对于社区及所有人的发展意义并不大，积极创新才可能独树一帜，并为大家带来更大的利益。无论您是否已经建成了自己的 FlinkSQL 开发运维平台，相信它一定会在创新的方向上为您带来些许启发。

在此非常感谢大家的支持~

QQ社区群：**543709668**，申请备注 “ Dlink ”，不写不批

微信社区群（推荐，大佬云集）：添加微信号 wenmo_ai 邀请进群，申请备注 “ Dlink ”，不写不批

公众号（最新消息获取建议关注）：[DataLink数据中台](https://mmbiz.qpic.cn/mmbiz_jpg/dyicwnSlTFTp6w4PuJruFaLV6uShCJDkzqwtnbQJrQ90yKDuuIC8tyMU5DK69XZibibx7EPPBRQ3ic81se5UQYs21g/0?wx_fmt=jpeg)

163 邮箱：aiwenmo@163.com

QQ 邮箱：809097465@qq.com