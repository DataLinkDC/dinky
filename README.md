# Dinky

[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=socialflat-square&)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Total Lines](https://img.shields.io/github/stars/DataLinkDC/dinky?style=socialflat-square&label=stars)](https://github.com/DataLinkDC/dinky/stargazers)
[![CN doc](https://img.shields.io/badge/文档-中文版-blue.svg?style=socialflat-square&)](README_zh_CN.md)
[![EN doc](https://img.shields.io/badge/document-English-blue.svg?style=socialflat-square&)](README.md)

[![Stargazers over time](https://starchart.cc/DataLinkDC/dinky.svg)](https://starchart.cc/DataLinkDC/dinky)

## Introduction

Dinky is an out of the box one-stop real-time computing platform dedicated to the construction and practice of Unified Streaming & Batch and Unified Data Lake & Data Warehouse. Based on Apache Flink, Dinky provides the ability to connect many big data frameworks including OLAP and Data Lake.

## Feature

Its main feature are as follows:

- Immersive Flink SQL Data Development: Automatic prompt completion, syntax highlighting, statement beautification, online debugging, syntax verification, execution plan, MetaStore, lineage, version comparison, etc.
- Support FlinkSQL multi-version development and execution modes: Local,Standalone,Yarn/Kubernetes Session,Yarn Per-Job,Yarn/Kubernetes Application.
- Support Apache Flink ecology: Connector,FlinkCDC,Table Store,etc.
- Support FlinkSQL syntax enhancement: Database synchronization, execution environments, global variables, statement merging, table-valued aggregate functions, load dependency, row-level permissions, etc.
- Supports real-time warehousing and lake entry of the entire FlinkCDC database, multi-database output, and automatic table creation.
- Support SQL job development: ClickHouse, Doris, Hive, Mysql, Oracle, Phoenix, PostgreSql, Presto, SqlServer, StarRocks, etc.
- Support real-time online debugging preview Table, ChangeLog, statistical chart and UDF.
- Support Flink Catalog, data source metadata online query and management.
- Support real-time task operation and maintenance: Online and offline, job information, cluster information, job snapshot, exception information, data map, data exploration, historical version, alarm record, etc.
- Support as multi-version FlinkSQL Server and OpenApi capability.
- Support real-time job alarm and alarm group: DingTalk, WeChat, Feishu, E-mail, etc.
- Support automatically managed SavePoint/CheckPoint recovery and triggering mechanisms: latest, earliest, specified, etc.
- Support resource management: Cluster instance, cluster configuration, jar, data source, alarm group, alarm instance, document, global variable, system configuration, etc.
- Support enterprise-level management: multi-tenant, user, role, project space.
- More hidden features are waiting for friends to explore.

## Principle

![dinky_principle](https://raw.githubusercontent.com/DataLinkDC/dinky/dev/images/main/dinky_principle.png)

## Run the Screenshot

> FlinkSQL Studio

![datastudio](https://raw.githubusercontent.com/DataLinkDC/dinky/dev/images/070/datastudio.png)

> Grammar Check

![checksql](https://raw.githubusercontent.com/DataLinkDC/dinky/dev/images/070/checksql.png)

> Version Management

![versiondiff](https://raw.githubusercontent.com/DataLinkDC/dinky/dev/images/070/versiondiff.png)

> lineage

![lineage](https://raw.githubusercontent.com/DataLinkDC/dinky/dev/images/070/lineage.png)

> BI Charts

![charts](https://raw.githubusercontent.com/DataLinkDC/dinky/dev/images/070/charts.png)

> Metadata Query

![metadata](https://raw.githubusercontent.com/DataLinkDC/dinky/dev/images/070/metadata.png)

> Task Monitoring

![monitor](https://raw.githubusercontent.com/DataLinkDC/dinky/dev/images/070/monitor.png)

> Job Information

![jobinfo](https://raw.githubusercontent.com/DataLinkDC/dinky/dev/images/070/jobinfo.png)

## Participate in Contributions
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](https://github.com/DataLinkDC/dinky/pulls)

Welcome to join the community, build a win-win situation, please refer to the contribution process： [How to contribute](https://github.com/DataLinkDC/dinky/blob/dev/docs/docs/developer_guide/contribution/how_contribute.md).

Thank you to all the people who already contributed to Dinky!

[![contrib graph](https://contrib.rocks/image?repo=DataLinkDC/dinky)](https://github.com/DataLinkDC/dinky/graphs/contributors)

## How to Deploy

- The dev branch is the 0.8 reconstructed version, which is still unstable.

- The 0.7 branch is the current stable version. Please use this branch for compilation and debugging.

See [source code compilation](https://github.com/DataLinkDC/dinky/blob/dev/docs/docs/deploy_guide/compile.mdx) and [installation and deployment](https://github.com/DataLinkDC/dinky/blob/dev/docs/docs/deploy_guide/deploy.mdx) for details.

## How to Upgrade

Due to many functions, there are many bugs and optimization points. It is strongly recommended to use or upgrade to the latest version.

Upgrade steps:

(1) Upgrade the app: Replace all dependencies of latest Dinky.

(2) Upgrade the DDL: Execute the relevant version upgrade statements in the sql/upgrade directory, and execute them in the order of the version numbers.

## Thanks

Standing on the shoulders of giants, Dinky was born. For this we express our heartfelt thanks to all the open source software used and its communities! We also hope that we are not only beneficiaries of open source, but also contributors to open source. We also hope that partners who have the same enthusiasm and belief in open source will join in and contribute to open source together.

A partial list of acknowledgements follows:

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

Thanks to [JetBrains](https://www.jetbrains.com/?from=dlink) for sponsoring a free open source license.

[![JetBrains](https://raw.githubusercontent.com/DataLinkDC/dinky/dev/images/main/jetbrains.svg)](https://www.jetbrains.com/?from=dlink)

## Get Help

1.Create an issue and describe it clearly.

2.Visit the [official website](http://www.dlink.top/#/) website to read the latest documentation manual.

3.It is recommended to scan the code to enter the DingTalk group.

<img src="https://raw.githubusercontent.com/DataLinkDC/dinky/dev/images/main/dinkydingding.jpg" alt="dinkydingding" style="zoom:30%;" />

4.Enter the WeChat user community group (recommended, add WeChat `wenmo_ai` to invite into the group) and QQ user community group (**543709668**) to communicate, and apply for the remarks "Dinky + company name + position".

5.Follow the WeChat public account to get the latest official articles: [Dinky Open Source](https://mmbiz.qpic.cn/mmbiz_jpg/dyicwnSlTFTp6w4PuJruFaLV6uShCJDkzqwtnbQJrQ90yKDuuIC8tyMU5DK69XZibibx7EPPBRQ3ic81se5UQYs21g/0?wx_fmt=jpeg).

6.Follow bilibili UP master (Shi Wen Mo A) to get the latest video teaching.

## Copyright

Please refer to the [LICENSE](https://github.com/DataLinkDC/dinky/blob/dev/LICENSE) document.

