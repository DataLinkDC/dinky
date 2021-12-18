## 简介

Dlink 为 Apache Flink 而生，让 Flink SQL 更加丝滑。它是一个交互式的 FlinkSQL Studio，可以在线开发、补全、校验 、执行、预览 FlinkSQL，支持 Flink 官方所有语法及其增强语法，并且可以同时对多 Flink 集群实例进行提交、停止、SavePoint 等运维操作，如同您的 IntelliJ IDEA For Flink SQL。

需要注意的是，Dlink 它更专注于 FlinkSQL 的应用，而不是 DataStream。在开发过程中您不会看到任何一句 java、scala 或者 python。所以，它的目标是基于 100% FlinkSQL 来实现批流一体的实时计算平台。

值得惊喜的是，Dlink 的实现基于最新 Flink 源码二次开发，而交互更加贴近 Flink 的功能与体验，并且紧随官方社区发展。即站在巨人肩膀上开发与创新，Dlink 在未来批流一体的发展趋势下潜力无限。

## 原理

![](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/040/Dlink_principle.png)

## 功能

注意：只表明核心功能，不包括细节。

|         域          |                 概要                 |  进展  |
| :-----------------: | :----------------------------------: | :----: |
|      基本管理       |        作业及 Savepoint 管理         | 已实现 |
|                     |        FlinkSQL 及执行配置管理       | 已实现 |
|                     |         Flink 集群及配置管理         | 已实现 |
|                     |              数据源管理              | 已实现 |
|                     |               文档管理               | 已实现 |
|                     |               系统配置               | 已实现 |
|                     |               用户管理               | 已实现 |
|  FlinkSQL 语法增强  |             SQL 片段语法             | 已实现 |
|                     |            AGGTABLE 语法             | 已实现 |
|                     |                语句集                | 已实现 |
|                     |       支持 sql-client 所有语法       | 已实现 |
|                     |     支持 Flink 所有 Configuration    | 已实现 |
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
|                     |           JobPlanGraph 预览            | 已实现 |
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

## 近期计划

1.支持同时托管多版本的Flink实例

2.支持K8S多种运行模式

3.支持多种任务调度框架接口

4.支持UDF动态加载

5.完善Studio交互功能

## 技术栈

[Apache Flink](https://github.com/apache/flink)

[Mybatis Plus](https://github.com/baomidou/mybatis-plus)

[ant-design-pro](https://github.com/ant-design/ant-design-pro)

[Monaco Editor](https://github.com/Microsoft/monaco-editor)

[SpringBoot]()

## 致谢

感谢 [JetBrains](https://www.jetbrains.com/?from=dlink) 提供的免费开源 License 赞助

[![JetBrains](https://gitee.com/DataLinkDC/dlink/raw/main/dlink-doc/images/main/jetbrains.svg)](https://www.jetbrains.com/?from=dlink)

## 交流与贡献

欢迎您加入社区交流分享，也欢迎您为社区贡献自己的力量。

在此有意向参与代码及文档贡献或积极测试者可以私信我加入 Dlink Contributors 群聊进一步了解。

dlink将正式开始社区积极的发展阶段，社区的主旨是开放、交流、创新、共赢，dlink的核心理念是创新，即不受思想约束地勇于尝试。dlink本就是一个创新型的解决方案，而不是模仿已有产品的思路按部就班，一味模仿对于社区及所有人的发展意义并不大，积极创新才可能独树一帜，并为大家带来更大的利益。无论您是否已经建成了自己的FlinkSQL平台，相信它一定会在创新的方向上为您带来些许启发。

在此非常感谢大家的支持~

QQ社区群：**543709668**，申请备注 “ Dlink ”，不写不批

微信社区群（推荐，大佬云集）：添加微信号 wenmo_ai 邀请进群，申请备注 “ Dlink ”，不写不批

公众号（最新消息获取建议关注）：[DataLink数据中台](https://mmbiz.qpic.cn/mmbiz_jpg/dyicwnSlTFTp6w4PuJruFaLV6uShCJDkzqwtnbQJrQ90yKDuuIC8tyMU5DK69XZibibx7EPPBRQ3ic81se5UQYs21g/0?wx_fmt=jpeg)

163 邮箱：aiwenmo@163.com

QQ 邮箱：809097465@qq.com