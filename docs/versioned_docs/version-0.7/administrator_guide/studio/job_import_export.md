---
sidebar_position: 6
id: job_import_export
title: 作业导入导出
---



## 介绍

Dinky 目前支持作业的导入和导出。导入和导出的作业为JSON文件。

导出作业时需要确保任务已保存,以保证导出的为最新的内容。

### 单任务导出

- **方式1:** 选中某一个作业 点击左上角导出按钮 导出为 json 文件
- **方式2:** 选中某一个作业 右键单击 `导出 json`

### 多任务导出

- 选中多个任务 点击左上角导出按钮 导出为json文件 , 注意:多个任务导出的仍为一个文件,文件内以JSON数组存储

## 说明

Dinky 导出作业，导出的内容信息包括 statement 语法、作业名称、作业配置信息等。具体的作业参数如下:

|           参数           |                            解释说明                             |
| :----------------------: |:-----------------------------------------------------------:|
|           name           |                            作业名称                             |
|          alias           |                            作业别名                             |
|         dialect          |                   数据库方言，如 FlinkSQL、Doris                    |
|           type           | 如果dialect是FlinkSQL，则为执行模式，StandAlone等<br/>  如果是其他数据库，则为null |
|        statement         |                           SQL 内容                            |
|        checkPoint        |                     checkPoint时间 ; 默认是0                     |
|    savePointStrategy     |                   FlinkSQL作业savePoint的策略                    |
|      savePointPath       |                  FlinkSQL作业savePoint的存储路径                   |
|       parallelism        |                            作业并行度                            |
|         fragment         |                         作业全局变量，默认禁用                         |
|       statementSet       |                       Insert语句集，默认禁用                        |
|        batchModel        |                   如果FlinkSQL 批作业，默认是true                    |
|       clusterName        |                         Flink 集群名称                          |
|        configJson        |                             配置项                             |
|           note           |                           作业备注/描述                           |
|           step           |                          作业当前的生命周期                          |
|         enabled          |                            是否启用                             |
|           path           |                            作业路径                             |
|       databaseName       |                 数据库schema名称，FlinkSQL显示null                  |
| clusterConfigurationName |                   FlinkSQL作业的Flink集群配置名称                    |
|         envName          |                  FlinkSQL作业的FlinkSQLEnv环境                   |
|      alertGroupName      |                      FlinkSQL作业的告警组名称                       |



