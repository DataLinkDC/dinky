## 概述

Dinky做为FlinkSQL的实时计算平台目前可以被部署在本地。充分利用了Flink在SQL上计算的能力，为您提供极致的Dinky使用体验。

- **开源：** 产品提供多个的Flink版本，与开源Flink接口完全兼容，满足您的平滑迁移业务的需求。

- **免费：** 平台各组件免费使用，您只需部署Dinky，Flink及相关上下游存储系统即可使用Dinky产品。
- **易用：** 提供开箱即用的作业开发平台，以及Metric采集、展示、监控和报警的能力。满足分钟级别搭建平台的需求，节省时间。

[Apache Flink](https://github.com/apache/flink)是Apache社区的先进开源项目，主攻流计算领域，具备高吞吐，低延时的特点，已经成为了目前流式处理领域的热门引擎。主要应用功能包括流批一体化及湖仓一体化的建设。

Dinky做为实时数据中台，当前也支持各种数据源连接。

## 支持上下游系统

### FlinkSQL上下游系统

FlinkSQL支持丰富的上下游存储，实时计算平台支持支持Flink1.11、Flink1.12、Flink1.13和Flink1.14四个版本，对应的版本支持所有开源的上下游存储详见具体Connector信息，请参见Flink开源社区：

- [Flink1.11](https://nightlies.apache.org/flink/flink-docs-release-1.11/dev/table/connectors/)

- [Flink1.12](https://nightlies.apache.org/flink/flink-docs-release-1.12/dev/table/connectors/)
- [Flink1.13](https://nightlies.apache.org/flink/flink-docs-release-1.13/docs/connectors/table/overview/)
- [Flink1.14](https://nightlies.apache.org/flink/flink-docs-release-1.14/docs/connectors/table/overview/)

另外非Flink官网支持的上下游系统详见github：

- [Flink-CDC](https://github.com/ververica/flink-cdc-connectors/releases/)

- [Hudi](https://github.com/apache/hudi/releases)
- [Iceberg](https://github.com/apache/iceberg/releases)
- [Doris:](https://github.com/apache/incubator-doris-flink-connector/tags) 目前只支持sink
- [Starrocks](https://github.com/StarRocks/flink-connector-starrocks/releases)
- [ClickHouse]()
- [Pulsar](https://github.com/streamnative/pulsar-flink/releases)

### 其他数据源

具体数据源请参考[扩展数据源](/zh-CN/extend/datasource.md)

## 管理控制台介绍

Dinky实时数据中台数据开发模块包括数据开发、运维中心、注册中心和系统设置三大模块。

1.在浏览器输入Dinky地址；

2.点击登录进入Dinky管理界面控制台；

**点击系统设置**

系统设置包括用户管理和Flink设置

![image-20220327001743513](http://www.aiwenmo.com/dinky/dev/docs/image-20220327001743513.png)



**点击注册中心**

注册中心包括集群管理、Jar管理、数据源管理、报警管理和文档管理

![image-20220327001833051](http://www.aiwenmo.com/dinky/dev/docs/image-20220327001833051.png)



**点击运维中心**

![image-20220327002124659](http://www.aiwenmo.com/dinky/dev/docs/image-20220327002124659.png)



**点击数据开发**

数据开发包括作业管理、作业配置和运维管理等

![image-20220327002318869](http://www.aiwenmo.com/dinky/dev/docs/image-20220327002318869.png)

