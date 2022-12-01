---
sidebar_position: 1
id: integrate
title: 集成
---



**声明:** Dinky并没有维护下面列出的库,其中有一部分是生产实践,一部分还未做过相关生产实践。欢迎使用Dinky已经集成的第三方库。

## 关系型数据库
  - MySQL
  - PostGreSQL
  - Oracle
  - SQLServer

## OLAP引擎
  - Doris
  - ClickHouse
  - Phoenix
  - Hive
  
  

## 消息队列

   - Kafka
   - Pulsar
   

以上均采用FlinkSQL Connector与Dinky集成使用，下面链接提供了FlinkSQL相关Connector包，请在[maven仓库](https://repo.maven.apache.org/maven2/org/apache/flink/)自行下载。


## CDC工具

  - [Flink CDC](https://github.com/ververica/flink-cdc-connectors/releases/)
  

## 调度平台

  - **Oozie**
  - **dolphinscheduler:** [Apache dolphinscheduler](https://dolphinscheduler.apache.org/zh-cn/)大数据调度平台
  

对于调度平台,需支持HTTP服务即可

## 数据湖

   - Hudi
   - Iceberg
  

[Hudi](https://github.com/apache/hudi) 及 [Iceberg](https://github.com/apache/iceberg) 请移步到github自行编译或者下载

## HADOOP
  - [flink-shaded-hadoop-3-uber](https://repository.cloudera.com/artifactory/cloudera-repos/org/apache/flink/flink-shaded-hadoop-3-uber)

## 其他
  
  - **dataspherestudio:** 微众银行开源[数据中台](https://github.com/WeBankFinTech/DataSphereStudio)
  

与Dinky的集成实践请移步最佳实践中的集成指南
  

  
  
   