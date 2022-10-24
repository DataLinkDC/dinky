# Dinky

[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Total Lines](https://tokei.rs/b1/github/DataLinkDC/dlink?category=lines)](https://github.com/DataLinkDC/dlink)
[![CN doc](https://img.shields.io/badge/文档-中文版-blue.svg)](README_zh_CN.md)
[![EN doc](https://img.shields.io/badge/document-English-blue.svg)](README.md)

[![Stargazers over time](https://starchart.cc/DataLinkDC/dlink.svg)](https://starchart.cc/DataLinkDC/dlink)

## 简介

Dinky 是一个 `开箱即用` 、`易扩展` ，以 `Apache Flink` 为基础，连接 `OLAP` 和 `数据湖` 等众多框架的 `一站式` 实时计算平台，致力于 `流批一体` 和 `湖仓一体` 的探索与实践。

## 功能

其主要功能如下：

- 沉浸式 FlinkSQL 数据开发：自动提示补全、语法高亮、语句美化、在线调试、语法校验、执行计划、MetaStore、血缘分析、版本对比等
- 支持 FlinkSQL 多版本开发及多种执行模式：Local、Standalone、Yarn/Kubernetes Session、Yarn Per-Job、Yarn/Kubernetes Application
- 支持 Apache Flink 生态：Connector、FlinkCDC、Table Store 等
- 支持 FlinkSQL 语法增强：表值聚合函数、全局变量、执行环境、语句合并、整库同步、共享会话等
- 支持 FlinkCDC 整库实时入仓入湖、多库输出、自动建表
- 支持 SQL 作业开发：ClickHouse、Doris、Hive、Mysql、Oracle、Phoenix、PostgreSql、SqlServer、StarRocks 等
- 支持实时在线调试预览 Table、 ChangeLog、统计图和 UDF
- 支持 Flink Catalog、数据源元数据在线查询及管理
- 支持实时任务运维：上线下线、作业信息、集群信息、作业快照、异常信息、数据地图、数据探查、历史版本、报警记录等
- 支持作为多版本 FlinkSQL Server 以及 OpenApi 的能力
- 支持实时作业报警及报警组：钉钉、微信企业号、飞书、邮箱等
- 支持自动托管的 SavePoint/CheckPoint 恢复及触发机制：最近一次、最早一次、指定一次等
- 支持多种资源管理：集群实例、集群配置、Jar、数据源、报警组、报警实例、文档、用户、系统配置等
- 更多隐藏功能等待小伙伴们探索

## 原理

![dinky_principle](https://raw.githubusercontent.com/DataLinkDC/dlink/main/dlink-doc/images/main/dinky_principle.png)

## 运行效果

> FlinkSQL Studio

![datastudio](https://raw.githubusercontent.com/DataLinkDC/dlink/dev/dlink-doc/images/070/datastudio.png)

> 语法检查

![checksql](https://raw.githubusercontent.com/DataLinkDC/dlink/dev/dlink-doc/images/070/checksql.png)

> 版本管理

![versiondiff](https://raw.githubusercontent.com/DataLinkDC/dlink/dev/dlink-doc/images/070/versiondiff.png)

> 血缘分析

![lineage](https://raw.githubusercontent.com/DataLinkDC/dlink/dev/dlink-doc/images/070/lineage.png)

> BI 图表

![charts](https://raw.githubusercontent.com/DataLinkDC/dlink/dev/dlink-doc/images/070/charts.png)

> 元数据查询

![metadata](https://raw.githubusercontent.com/DataLinkDC/dlink/dev/dlink-doc/images/070/metadata.png)

> 任务监控

![monitor](https://raw.githubusercontent.com/DataLinkDC/dlink/dev/dlink-doc/images/070/monitor.png)

> 作业信息

![jobinfo](https://raw.githubusercontent.com/DataLinkDC/dlink/dev/dlink-doc/images/070/jobinfo.png)

## 近期计划

- [ ] 多租户及命名空间
- [ ] 全局血缘与影响分析
- [ ] 统一元数据管理
- [x] Flink 元数据持久化
- [x] 整库实时入仓入湖

## 参与贡献

欢迎加入社区，共建共赢，贡献流程请参考： [参与贡献](https://github.com/DataLinkDC/dlink/blob/dev/docs/docs/developer_guide/contribution/how_contribute.md)

## 如何部署

详见 [源码编译](https://github.com/DataLinkDC/dlink/blob/dev/docs/docs/build_deploy/build.md) 和 [安装部署](https://github.com/DataLinkDC/dlink/blob/dev/docs/docs/build_deploy/deploy.md) 。

## 如何升级

由于功能较多，所以 bug 及优化点较多，强烈建议使用或升级到最新版本。

升级步骤：

（1）升级应用：替换最新 Dinky 所有依赖包；

（2）升级 DDL：执行 sql/upgrade 目录下的相关版本升级语句，依次按照版本号顺序执行。

## 感谢

站在巨人的肩膀上，Dinky 才得以诞生。对此我们对使用的所有开源软件及其社区表示衷心的感谢！我们也希望自己不仅是开源的受益者，也能成为开源的贡献者，也希望对开源有同样热情和信念的伙伴加入进来，一起为开源献出一份力！

部分致谢列表如下：

[Apache Flink](https://github.com/apache/flink)

[FlinkCDC](https://github.com/ververica/flink-cdc-connectors)

[Apache Flink Table Store](https://github.com/apache/flink-table-store)

[Apache Dolphinscheduler](https://github.com/apache/dolphinscheduler)

[Apache Doris](https://github.com/apache/doris)

[Druid](https://github.com/alibaba/druid)

[Ant-Design-Pro](https://github.com/ant-design/ant-design-pro)

[Mybatis Plus](https://github.com/baomidou/mybatis-plus)

[Monaco Editor](https://github.com/Microsoft/monaco-editor)

[Sa Token](https://github.com/dromara/Sa-Token)

[SpringBoot]()

感谢 [JetBrains](https://www.jetbrains.com/?from=dlink) 提供的免费开源 License 赞助。

[![JetBrains](https://raw.githubusercontent.com/DataLinkDC/dlink/main/dlink-doc/images/main/jetbrains.svg)](https://www.jetbrains.com/?from=dlink)

## 获得帮助

1.提交 issue

2.访问 [官网](http://www.dlink.top/#/) 网址，阅读最新文档手册

3.推荐扫码进入钉钉群

<img src="https://raw.githubusercontent.com/DataLinkDC/dlink/dev/dlink-doc/images/main/dinkydingding.jpg" alt="dinkydingding" style="zoom:30%;" />

4.进入微信用户社区群（推荐，添加微信号 `wenmo_ai` 邀请进群）和QQ用户社区群（**543709668**）交流，申请备注 “ Dinky + 企业名 + 职位”

5.关注微信公众号获取官方最新文章：[Dinky 开源](https://mmbiz.qpic.cn/mmbiz_jpg/dyicwnSlTFTp6w4PuJruFaLV6uShCJDkzqwtnbQJrQ90yKDuuIC8tyMU5DK69XZibibx7EPPBRQ3ic81se5UQYs21g/0?wx_fmt=jpeg)

6.关注 bilibili UP 主（是文末呀）获取最新视频教学

## 版权

请参考 [LICENSE](https://github.com/DataLinkDC/dlink/blob/main/LICENSE) 文件。