---
sidebar_position: 6
id: job_import_export
title: 作业导入导出
---



## 介绍

Dinky 目前支持作业的导入和导出，当前仅支持单个作业的导出和导入。导入和导出的作业为JSON文件。

导出作业，导出开发时的最新的已保存内容

导入作业，会覆盖开发时的内容

## 说明

Dinky 导出作业，导出的内容信息包括 statement语法、作业名称、作业配置信息等。具体的作业参数如下:

|           参数           |                           解释说明                           |
| :----------------------: | :----------------------------------------------------------: |
|           name           |                           作业名称                           |
|          alias           |                           作业别名                           |
|         dialect          |                数据库方言，如 FlinkSQL、Doris                |
|           type           | 如果dialect是FlinkSQL，则为执行模式，StandAlone等<br/>  如果是其他数据库，则为null |
|        statement         |                           SQL 语法                           |
|        checkPoint        |                           默认是0                            |
|    savePointStrategy     |                 FlinkSQL作业savePoint的策略                  |
|      savePointPath       |               FlinkSQL作业savePoint的存储路径                |
|       parallelism        |                          作业并行度                          |
|         fragment         |                    作业全局变量，默认禁用                    |
|       statementSet       |                    Insert语句集，默认禁用                    |
|        batchModel        |               如果FlinkSQL 批作业，默认是true                |
|       clusterName        |                        Flink 集群名称                        |
|        configJson        |                                                              |
|           note           |                                                              |
|           step           |                                                              |
|         enabled          |                                                              |
|           path           |                           作业路径                           |
|       databaseName       |              数据库schema名称，FlinkSQL显示null              |
| clusterConfigurationName |               FlinkSQL作业的Flink集群配置名称                |
|         envName          |                FlinkSQL作业的FlinkSQLEnv环境                 |
|      alertGroupName      |                   FlinkSQL作业的告警组名称                   |



