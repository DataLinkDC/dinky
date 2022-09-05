# Dinky

[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Total Lines](https://tokei.rs/b1/github/DataLinkDC/dlink?category=lines)](https://github.com/DataLinkDC/dlink)
[![CN doc](https://img.shields.io/badge/文档-中文版-blue.svg)](README_zh_CN.md)
[![EN doc](https://img.shields.io/badge/document-English-blue.svg)](README.md)

[![Stargazers over time](https://starchart.cc/DataLinkDC/dlink.svg)](https://starchart.cc/DataLinkDC/dlink)

## Introduction

Real-time is the future. Dlink is born for Apache Flink, allowing Flink SQL to enjoy silky smoothness, and is committed to the construction of a real-time computing platform.

Dinky implements Dlink based on Apache Flink, enhances the application and experience of Flink, and explores streaming data warehouses. That is to stand on the shoulders of giants to innovate and practice, Dinky has unlimited potential under the development trend of batch and flow integration in the future.

In the end, Dinky's development is due to the guidance and results of other excellent open source projects such as Apache Flink.

## Features

A `out-of-the-box`, `easy to extend`, based on `Apache Flink`, a `one-stop` real-time computing platform connecting with many frameworks such as `OLAP` and `data lake`, dedicated to `stream-batch integration` The construction and practice of `Lake and Warehouse Integration`.

Its main objectives are as follows:

- Visual interactive FlinkSQL and SQL data development platform: automatic prompt completion, syntax highlighting, debugging execution, syntax verification, statement beautification, global variables, etc.

- Supports comprehensive multi-version FlinkSQL job submission methods: Local, Standalone, Yarn Session, Yarn Per-Job, Yarn Application, Kubernetes Session, Kubernetes Application

- Support all Connectors, UDFs, CDCs, etc. of Apache Flink

- Support FlinkSQL syntax enhancement: compatible with Apache Flink SQL, table-valued aggregate functions, global variables, CDC multi-source merge, execution environment, statement merge, shared session, etc.

- Supports easily extensible SQL job submission methods: ClickHouse, Doris, Hive, Mysql, Oracle, Phoenix, PostgreSql, SqlServer, etc.

- Support FlinkCDC (Source merge) real-time warehousing into the lake

- Support real-time debugging preview Table and ChangeLog data and graphics display

- Support syntax logic check, job execution plan, field-level blood relationship analysis, etc.

- Support Flink metadata, data source metadata query and management

- Support real-time task operation and maintenance: job online and offline, job information, cluster information, job snapshot, exception information, job log, data map, ad hoc query, historical version, alarm record, etc.

- Support as multi-version FlinkSQL Server capability as well as OpenApi

- Support easy-to-expand real-time job alarms and alarm groups: DingTalk, WeChat Enterprise Account, etc.

- Support for fully managed SavePoint launch mechanisms: most recent, earliest, once specified, etc.

- Support multiple resource management: cluster instance, cluster configuration, Jar, data source, alarm group, alarm instance, document, user, system configuration, etc.

- More hidden functions are waiting for friends to explore

## Principle

![dinky_principle](https://raw.githubusercontent.com/DataLinkDC/dlink/main/dlink-doc/images/main/dinky_principle.png)

## Wonderful Moment

> FlinkSQL Studio

![flinksqlstudio](https://raw.githubusercontent.com/DataLinkDC/dlink/main/dlink-doc/images/060/flinksqlstudio.png)

> Live debug preview

![selectpreview](https://raw.githubusercontent.com/DataLinkDC/dlink/main/dlink-doc/images/060/selectpreview.png)

> Grammar and logic checking

![checksql](https://raw.githubusercontent.com/DataLinkDC/dlink/main/dlink-doc/images/060/checksql.png)

> JobPlan

![jobplan](https://raw.githubusercontent.com/DataLinkDC/dlink/main/dlink-doc/images/060/jobplan.png)

> Field-level bloodline analysis

![lineage](https://raw.githubusercontent.com/DataLinkDC/dlink/main/dlink-doc/images/060/lineage.png)

> BI showcase

![charts](https://raw.githubusercontent.com/DataLinkDC/dlink/main/dlink-doc/images/060/charts.png)

> Metadata query

![metadata](https://raw.githubusercontent.com/DataLinkDC/dlink/main/dlink-doc/images/060/metadata.png)

> Real-time task monitoring

![monitor](https://raw.githubusercontent.com/DataLinkDC/dlink/main/dlink-doc/images/060/monitor.png)

> Real-time job information

![jobinfo](https://raw.githubusercontent.com/DataLinkDC/dlink/main/dlink-doc/images/060/jobinfo.png)

> Data Map

![datamap](https://raw.githubusercontent.com/DataLinkDC/dlink/main/dlink-doc/images/060/datamap.png)

> Data source registration

![datasource](https://raw.githubusercontent.com/DataLinkDC/dlink/main/dlink-doc/images/060/datasource.png)

## Function

See [Function](https://github.com/DataLinkDC/dlink/blob/dev/docs/zh-CN/feature.md)

## Near-Term Plans

- Multi-tenancy and namespaces

- Global bloodline and influence analysis

- Unified metadata management

- Flink metadata persistence

- Multi-version Flink-Client Server

- Synchronization of thousands of watches in the whole library

## How to Contribute

You are welcome to contribute your strength to the community and build a win-win situation. Please refer to the contribution process: [[How to Contribute](https://github.com/DataLinkDC/dlink/blob/dev/docs/zh-CN/developer_guide/how_contribute.md)]

## How to Deploy

See [Compile](https://github.com/DataLinkDC/dlink/blob/dev/docs/zh-CN/quick_start/build.md) And [Install](https://github.com/DataLinkDC/dlink/blob/dev/docs/zh-CN/quick_start/deploy.md) 。

## How to Upgrade to the latest

Due to more functions, there are more bugs and optimization points. It is strongly recommended that you use or upgrade to the latest version.

Replace all dependent packages of the latest Dinky, and execute some upgrade statements in dlink_history.sql in the sql directory. It is based on the version number and date to determine where to start the execution. Please do not directly execute all sql.

## Thanks

Standing on the shoulders of giants, Dinky was born. For this we express our heartfelt thanks to all the open source software used and its communities! We also hope that we are not only beneficiaries of open source, but also contributors to open source. We also hope that partners who have the same enthusiasm and belief in open source will join in and contribute to open source together! Acknowledgments are listed below:

[Apache Flink](https://github.com/apache/flink)

[Apache Dolphinscheduler](https://github.com/apache/dolphinscheduler)

[Ant-Design-Pro](https://github.com/ant-design/ant-design-pro)

[Mybatis Plus](https://github.com/baomidou/mybatis-plus)

[Monaco Editor](https://github.com/Microsoft/monaco-editor)

[SpringBoot]()

Thanks to [JetBrains](https://www.jetbrains.com/?from=dlink) for sponsoring a free open source license.

[![JetBrains](https://raw.githubusercontent.com/DataLinkDC/dlink/main/dlink-doc/images/main/jetbrains.svg)](https://www.jetbrains.com/?from=dlink)

## Get Help

1. Submit an issue

2. Enter the WeChat user community group (recommended, add WeChat `wenmo_ai` to invite into the group) and QQ user community group (**543709668**) to communicate, apply for the remark "Dinky + company name + position", do not write or approve

3. Follow the WeChat public account to get relevant articles (recommended to follow the latest news): [DataLink Data Center](https://mmbiz.qpic.cn/mmbiz_jpg/dyicwnSlTFTp6w4PuJruFaLV6uShCJDkzqwtnbQJrQ90yKDuuIC8tyMU5DK69XZibibx7EPPBRQ3ic81se5UQYs21g/0?wx_fmt=jpeg)

4. Follow the bilibili UP master (at the end of the article) to get the latest video teaching

5. Visit [GithubPages](https://datalinkdc.github.io/dlink/#/) or [Official Website](http://www.dlink.top/#/) to read the latest documentation manual

## LICENSE

Please refer to the [LICENSE](https://github.com/DataLinkDC/dlink/blob/main/LICENSE) document.
