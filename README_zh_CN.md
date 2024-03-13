# Dinky

[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=socialflat-square&)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Total Lines](https://img.shields.io/github/stars/DataLinkDC/dinky?style=socialflat-square&label=stars)](https://github.com/DataLinkDC/dinky/stargazers)
[![CN doc](https://img.shields.io/badge/文档-中文版-blue.svg?style=socialflat-square&)](README_zh_CN.md)
[![EN doc](https://img.shields.io/badge/document-English-blue.svg?style=socialflat-square&)](README.md)

[![Stargazers over time](https://starchart.cc/DataLinkDC/dinky.svg)](https://starchart.cc/DataLinkDC/dinky)



## 简介

Dinky 是一个基于 `Apache Flink` 的实时数据开发平台，实现了敏捷的数据开发、部署和运维。

## 功能

其主要功能如下：

- FlinkSQL 数据开发：提示补全、语句美化、在线调试、逻辑校验、执行计划、Catalog、血缘分析、版本对比等
- 支持 FlinkSQL 多版本开发及多种执行模式：Local、Standalone、Yarn/Kubernetes Session、Yarn Per-Job、Yarn/Kubernetes Application
- 支持 Apache Flink 生态：CDC、Connector、FlinkCEP、FlinkCDC、Paimon、PyFlink 等
- 支持 FlinkSQL 语法增强：整库同步、执行环境、全局变量、表值聚合、加载依赖、行级权限、执行Jar任务等
- 支持 FlinkCDC 整库实时入仓入湖与 FlinkCDCPipeline 整库同步
- 支持实时在线调试预览 Table、ChangeLog 和 UDF
- 支持 Flink Catalog、数据源元数据在线查询及管理
- 支持实时任务运维：上线下线、作业信息、作业日志、版本信息、作业快照、监控、SQL 血缘、告警记录等
- 支持实时作业报警及报警组：钉钉、企业微信、飞书、邮箱、短信、http 等
- 支持自动托管的 SavePoint/CheckPoint 恢复及触发机制：最近一次、最早一次、指定一次等
- 支持多种资源管理：集群实例、集群配置、数据源、告警、文档、全局变量、Git 项目、UDF、资源、系统配置等
- 支持企业级管理：多租户、用户、角色、菜单、令牌
- 更多隐藏功能等待小伙伴们探索

## 原理

![dinky_principle](https://raw.githubusercontent.com/DataLinkDC/dinky/dev/images/main/dinky_principle.png)

## 运行效果

> FlinkSQL Studio

![datastudio](https://raw.githubusercontent.com/DataLinkDC/dinky/dev/images/v1/datastudio.png)

> 语法检查

![checksql](https://raw.githubusercontent.com/DataLinkDC/dinky/dev/images/v1/checksql.png)

> 版本管理

![versiondiff](https://raw.githubusercontent.com/DataLinkDC/dinky/dev/images/v1/versiondiff.png)

> 血缘分析

![lineage](https://raw.githubusercontent.com/DataLinkDC/dinky/dev/images/v1/lineage.png)

> 任务监控

![monitor](https://raw.githubusercontent.com/DataLinkDC/dinky/dev/images/v1/monitor.png)

## 参与贡献

[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](https://github.com/DataLinkDC/dinky/pulls)

欢迎加入社区，共建共赢，贡献流程请参考： [参与贡献](https://github.com/DataLinkDC/dinky/blob/dev/docs/docs/developer_guide/contribution/how_contribute.md)

感谢所有已经为 Dinky 做出贡献的人！

[![contrib graph](https://contrib.rocks/image?repo=DataLinkDC/dinky)](https://github.com/DataLinkDC/dinky/graphs/contributors)

## 如何部署

详见 [源码编译](https://github.com/DataLinkDC/dinky/blob/dev/docs/docs/deploy_guide/compile.mdx) 和 [安装部署](https://github.com/DataLinkDC/dinky/blob/dev/docs/docs/deploy_guide/deploy.mdx) 。

## 感谢

站在巨人的肩膀上，Dinky 才得以诞生。对此我们对使用的所有开源软件及其社区表示衷心的感谢！我们也希望自己不仅是开源的受益者，也能成为开源的贡献者，也希望对开源有同样热情和信念的伙伴加入进来，一起为开源献出一份力！

部分致谢列表如下：

[Apache Flink](https://github.com/apache/flink)

[FlinkCDC](https://github.com/ververica/flink-cdc-connectors)

[Apache Paimon](https://github.com/apache/incubator-paimon)

[Apache Dolphinscheduler](https://github.com/apache/dolphinscheduler)

[Apache Doris](https://github.com/apache/doris)

[Druid](https://github.com/alibaba/druid)

[Ant-Design-Pro](https://github.com/ant-design/ant-design-pro)

[Mybatis Plus](https://github.com/baomidou/mybatis-plus)

[Monaco Editor](https://github.com/Microsoft/monaco-editor)

[Sa Token](https://github.com/dromara/Sa-Token)

[SpringBoot]()

感谢 [JetBrains](https://www.jetbrains.com/?from=dlink) 提供的免费开源 License 赞助。

[![JetBrains](https://raw.githubusercontent.com/DataLinkDC/dinky/main/images/main/jetbrains.svg)](https://www.jetbrains.com/?from=dlink)

## 获得帮助

1.创建 issue，并描述清晰

2.访问 [官网](http://www.dinky.org.cn/#/) 网址，阅读最新文档手册

3.推荐扫码进入钉钉群

<img src="https://raw.githubusercontent.com/DataLinkDC/dinky/dev/images/main/dinkydingding.jpg" alt="dinkydingding" style="zoom:30%;" />

4.进入微信用户社区群（推荐，添加微信号 `wenmo_ai` 邀请进群）和 QQ 用户社区群（**543709668**）交流，必须申请备注 “Dinky + 企业名 + 职位”，

5.关注微信公众号获取官方最新文章：[Dinky 开源](https://mmbiz.qpic.cn/mmbiz_jpg/dyicwnSlTFTp6w4PuJruFaLV6uShCJDkzqwtnbQJrQ90yKDuuIC8tyMU5DK69XZibibx7EPPBRQ3ic81se5UQYs21g/0?wx_fmt=jpeg)

## 版权

请参考 [LICENSE](https://github.com/DataLinkDC/dinky/blob/dev/LICENSE) 文件。
