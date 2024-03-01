# Dinky

[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=socialflat-square&)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Total Lines](https://img.shields.io/github/stars/DataLinkDC/dinky?style=socialflat-square&label=stars)](https://github.com/DataLinkDC/dinky/stargazers)
[![CN doc](https://img.shields.io/badge/文档-中文版-blue.svg?style=socialflat-square&)](README_zh_CN.md)
[![EN doc](https://img.shields.io/badge/document-English-blue.svg?style=socialflat-square&)](README.md)

[![Stargazers over time](https://starchart.cc/DataLinkDC/dinky.svg)](https://starchart.cc/DataLinkDC/dinky)

## Introduction

Dinky is a real-time data development platform based on Apache Flink, enabling agile data development, deployment and operation.

## Feature

Its main features are as follows:

- Immersive Flink SQL Data Development: Dinky provides prompt completion, statement beautification, online debugging, syntax verification, logic plan, catalog, lineage, version comparison, and more.
- Support FlinkSQL multi-version development and execution modes: Dinky supports multiple development and execution modes for FlinkSQL, including Local, Standalone, Yarn/Kubernetes Session, Yarn Per-Job, and Yarn/Kubernetes Application.
- Support Flink ecosystem: Connector, FlinkCEP, FlinkCDC, Paimon, PyFlink
- Support FlinkSQL syntax enhancement: Dinky enhances FlinkSQL with features like database synchronization, execution environments, global variables, table-valued aggregate functions, load dependency, row-level permissions, and execute jar.
- Support real-time warehousing and lake entry of the entire FlinkCDC database and FlinkCDC Pipeline task.
- Support real-time online debugging: Preview Table, ChangeLog and UDF.
- Support Flink Catalog, data source metadata online query and management.
- Support real-time task operation and maintenance: Online and offline, job information, job log, version info, job snapshot, monitor, sql lineage, alarm record, etc.
- Support real-time job alarm and alarm group: DingTalk, WeChat, Feishu, E-mail, SMS, Http etc.
- Support automatically managed SavePoint/CheckPoint recovery and triggering mechanisms: latest, earliest, specified, etc.
- Support resource management: Cluster instance, cluster configuration, data source, alarm, document, global variable, git project, UDF, resource, system configuration, etc.
- Support enterprise-level management: multi-tenant, user, role, token.
- More hidden features await exploration by our users.

## Principle

![dinky_principle](https://raw.githubusercontent.com/DataLinkDC/dinky/dev/images/main/dinky_principle.png)

## Run the Screenshot

> FlinkSQL Studio

![datastudio](https://raw.githubusercontent.com/DataLinkDC/dinky/dev/images/v1/datastudio.png)

> Grammar Check

![checksql](https://raw.githubusercontent.com/DataLinkDC/dinky/dev/images/v1/checksql.png)

> Version Management

![versiondiff](https://raw.githubusercontent.com/DataLinkDC/dinky/dev/images/v1/versiondiff.png)

> lineage

![lineage](https://raw.githubusercontent.com/DataLinkDC/dinky/dev/images/v1/lineage.png)

> Task Monitoring

![monitor](https://raw.githubusercontent.com/DataLinkDC/dinky/dev/images/v1/monitor.png)

## Participate in Contributions
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](https://github.com/DataLinkDC/dinky/pulls)

Welcome to join the community, build a win-win situation, please refer to the contribution process： [How to contribute](https://github.com/DataLinkDC/dinky/blob/dev/docs/docs/developer_guide/contribution/how_contribute.md).

Thank you to all the people who already contributed to Dinky!

[![contrib graph](https://contrib.rocks/image?repo=DataLinkDC/dinky)](https://github.com/DataLinkDC/dinky/graphs/contributors)

## How to Deploy

See [source code compilation](https://github.com/DataLinkDC/dinky/blob/dev/docs/docs/deploy_guide/compile.mdx) and [installation and deployment](https://github.com/DataLinkDC/dinky/blob/dev/docs/docs/deploy_guide/deploy.mdx) for details.

## Thanks

Standing on the shoulders of giants, Dinky was born. For this we express our heartfelt thanks to all the open source software used and its communities! We also hope that we are not only beneficiaries of open source, but also contributors to open source. We also hope that partners who share our enthusiasm and belief in open source will join us in contributing to the open-source community.

Below is a partial list of acknowledgements:

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

Thanks to [JetBrains](https://www.jetbrains.com/?from=dlink) for providing a free open-source license.

[![JetBrains](https://raw.githubusercontent.com/DataLinkDC/dinky/dev/images/main/jetbrains.svg)](https://www.jetbrains.com/?from=dlink)

## Get Help

1.Create an issue and provide a clear description.

2.Visit the [official website](http://www.dinky.org.cn/#/) website to read the latest documentation manual.

3.It is recommended to scan the code to enter the DingTalk group.

<img src="https://raw.githubusercontent.com/DataLinkDC/dinky/dev/images/main/dinkydingding.jpg" alt="dinkydingding" style="zoom:30%;" />

4.Enter the WeChat user community group (recommended, add WeChat `wenmo_ai` to invite into the group) and QQ user community group (**543709668**) to communicate, and apply for the remarks "Dinky + company name + position".

5.Follow the WeChat public account to get the latest official articles: [Dinky Open Source](https://mmbiz.qpic.cn/mmbiz_jpg/dyicwnSlTFTp6w4PuJruFaLV6uShCJDkzqwtnbQJrQ90yKDuuIC8tyMU5DK69XZibibx7EPPBRQ3ic81se5UQYs21g/0?wx_fmt=jpeg).

## Copyright

Please refer to the [LICENSE](https://github.com/DataLinkDC/dinky/blob/dev/LICENSE) document.