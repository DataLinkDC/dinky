---
sidebar_position: 1
position: 1
id: openapi_overview
title: Open API 总览
---


:::info 介绍
Dinky 平台提供 OpenAPI 能力，通过调用 Dinky 的 OpenAPI 可以使用 Dinky 的功能，实现应用和 Dinky 的集成和交互。

Dinky 的 OpenAPI 提供多种 API 功能。通过调用 API，可以快速进行操作和系统集成对接等工作，提高数据开发效率，满足企业定制化需求。您还可以通过开放平台，轻松获取
OpenAPI 的使用情况。
:::
当前支持的 OpenAPI 包括如下：

| 序号 |      执行模式      |        类型名称        |             作用             |
|:--:|:--------------:|:------------------:|:--------------------------:|
| 1  |   explainSql   |       cancel       |      调用取消 FlinkSQL 作业      |
| 2  |   explainSql   |     statement      |      调用 statement 语句       |
| 3  |   executeJar   |  yarn-application  | 调用执行 Yarn-Application jar包 |
| 4  |   executeSql   | kubernetes-session |   调用执行作业在 K8ssession 上运行   |
| 5  |   executeSql   |       local        |     调用执行作业在 Local 上运行      |
| 6  |   executeSql   |    yarn-per-job    |  调用执行作业在 Yarn-per-job 上运行  |
| 7  |   executeSql   |     standalone     |   调用执行作业在 Standalone 上运行   |
| 8  |   executeSql   |    yarn-session    |  调用执行作业在 Yarn-session 上运行  |
| 9  |   getJobData   |                    |                            |
| 10 |   getJobPlan   |     statement      |          调用获取执行计划          |
| 11 | getStreamGraph |     statement      |      调用获取 Flink DAG 图      |
| 12 |   savepoint    |                    |     调用手动保存 Checkpoints     |
| 13 | savepointTask  |                    |      调用并触发 Savepoint       |
| 14 |   submitTask   |                    |            作业调度            |


## 示例

**explainSql**

explainSql 包括 FlinkSQL 作业取消和 statement 语句执行

FlinkSQL 作业 cancel：

**explainSql**

explainSql 包括 cancel

```
http://127.0.0.1:8888/openapi/explainSql
{
  /* required-start */
  "jobId":"195352b0a4518e16699983a13205f059",
  /* required-end */
  /* custom-start */
  "address":"127.0.0.1:8081",
  "gatewayConfig":{
    "clusterConfig":{
      "appId":"application_1637739262398_0032",
      "flinkConfigPath":"/opt/src/flink-1.13.3_conf/conf",
      "flinkLibPath":"hdfs:///flink13/lib/flinklib",
      "yarnConfigPath":"/usr/local/hadoop/hadoop-2.7.7/etc/hadoop"
    }
  }
  /* custom-start */
}
```

statement 语句执行：

```
 http://127.0.0.1:8888/openapi/explainSql
{
  /* required-start */
  "statement":"CREATE TABLE Orders (\r\n    order_number INT,\r\n    price        DECIMAL(32,2),\r\n    order_time   TIMESTAMP(3)\r\n) WITH (\r\n  'connector' = 'datagen',\r\n  'rows-per-second' = '1',\r\n  'fields.order_number.kind' = 'sequence',\r\n  'fields.order_number.start' = '1',\r\n  'fields.order_number.end' = '1000'\r\n);\r\nCREATE TABLE pt (\r\nordertotal INT,\r\nnumtotal INT\r\n) WITH (\r\n 'connector' = 'print'\r\n);\r\ninsert into pt select 1 as ordertotal ,sum(order_number)*2 as numtotal from Orders",
  /* required-end */
  /* default-start */
  "useStatementSet":false,
  "fragment":false,
  "parallelism":1,
  /* default-start */
  /* custom-start */
  "configuration":{
    "table.exec.resource.default-parallelism":2
  }
  /* custom-end */
}
```

**executeJar**

Yarn-application jar 包调用并提交

```
 http://127.0.0.1:8888/openapi/executeJar
{
  /* required-start */
  "type":"yarn-application",
  "gatewayConfig":{
    "clusterConfig":{
      "flinkConfigPath":"/opt/src/flink-1.13.3_conf/conf",
      "flinkLibPath":"hdfs:///flink13/lib/flinklib",
      "yarnConfigPath":"/usr/local/hadoop/hadoop-2.7.7/etc/hadoop"
    },
    "appConfig":{
      "userJarPath":"hdfs:///flink12/jar/currencyAppJar.jar",
      "userJarParas":["--id","2774,2775,2776"," --type","dwd"],
      "userJarMainAppClass":"com.app.MainApp"
    },
    "flinkConfig": {
      "configuration":{
        "parallelism.default": 1
      }
    }
  },
  /* required-end */
  /* custom-start */
  "jobName":"openapitest",
  "savePointPath":"hdfs://ns/flink/savepoints/savepoint-5f4b8c-4326844a6843"
  /* custom-end */
}
```

**executeSql**

executeSql 提交执行作业包括 Local，Kubernetes-session，Yarn-per-job，Standalone，Yarn-session

Local:

```
 http://127.0.0.1:8888/openapi/executeSql 
{
  /* required-start */
  "type":"local",
  "statement":"CREATE TABLE Orders (\r\n    order_number INT,\r\n    price        DECIMAL(32,2),\r\n    order_time   TIMESTAMP(3)\r\n) WITH (\r\n  'connector' = 'datagen',\r\n  'rows-per-second' = '1',\r\n  'fields.order_number.kind' = 'sequence',\r\n  'fields.order_number.start' = '1',\r\n  'fields.order_number.end' = '1000'\r\n);\r\nCREATE TABLE pt (\r\nordertotal INT,\r\nnumtotal INT\r\n) WITH (\r\n 'connector' = 'print'\r\n);\r\ninsert into pt select 1 as ordertotal ,sum(order_number)*2 as numtotal from Orders",
  /* required-end */
  /* default-start */
  "useResult":false,
  "useChangeLog":false,
  "useAutoCancel":false,
  "useStatementSet":false,
  "fragment":false,
  "maxRowNum":100,
  "checkPoint":0,
  "parallelism":1,
  /* default-start */
  /* custom-start */
  "jobName":"openapitest",
  "savePointPath":"hdfs://ns/flink/savepoints/savepoint-5f4b8c-4326844a6843",
  "configuration":{
    "table.exec.resource.default-parallelism":2
  }
  /* custom-end */
}
```

Kubernetes-session：

```
 http://127.0.0.1:8888/openapi/executeSql
{
  /* required-start */
  "type":"kubernetes-session",
  "address":"127.0.0.1:8081",
  "statement":"CREATE TABLE Orders (\r\n    order_number INT,\r\n    price        DECIMAL(32,2),\r\n    order_time   TIMESTAMP(3)\r\n) WITH (\r\n  'connector' = 'datagen',\r\n  'rows-per-second' = '1',\r\n  'fields.order_number.kind' = 'sequence',\r\n  'fields.order_number.start' = '1',\r\n  'fields.order_number.end' = '1000'\r\n);\r\nCREATE TABLE pt (\r\nordertotal INT,\r\nnumtotal INT\r\n) WITH (\r\n 'connector' = 'print'\r\n);\r\ninsert into pt select 1 as ordertotal ,sum(order_number)*2 as numtotal from Orders",
  /* required-end */
  /* default-start */
  "useResult":false,
  "useStatementSet":false,
  "useChangeLog":false,
  "useAutoCancel":false,
  "fragment":false,
  "maxRowNum":100,
  "checkPoint":0,
  "parallelism":1,
  /* default-start */
  /* custom-start */
  "jobName":"openapitest",
  "savePointPath":"hdfs://ns/flink/savepoints/savepoint-5f4b8c-4326844a6843",
  "configuration":{
    "table.exec.resource.default-parallelism":2
  }
  /* custom-end */
}
```

Yarn-per-job:

```
http://127.0.0.1:8888/openapi/executeSql 
{
  /* required-start */
  "type":"yarn-per-job",
  "statement":"CREATE TABLE Orders (\r\n    order_number INT,\r\n    price        DECIMAL(32,2),\r\n    order_time   TIMESTAMP(3)\r\n) WITH (\r\n  'connector' = 'datagen',\r\n  'rows-per-second' = '1',\r\n  'fields.order_number.kind' = 'sequence',\r\n  'fields.order_number.start' = '1',\r\n  'fields.order_number.end' = '1000'\r\n);\r\nCREATE TABLE pt (\r\nordertotal INT,\r\nnumtotal INT\r\n) WITH (\r\n 'connector' = 'print'\r\n);\r\ninsert into pt select 1 as ordertotal ,sum(order_number)*2 as numtotal from Orders",
  "gatewayConfig":{
    "clusterConfig":{
      "flinkConfigPath":"/opt/src/flink-1.13.3_conf/conf",
      "flinkLibPath":"hdfs:///flink13/lib/flinklib",
      "yarnConfigPath":"/usr/local/hadoop/hadoop-2.7.7/etc/hadoop"
    },
    "flinkConfig": {
      "configuration":{
        "parallelism.default": 1
      }
    }
  },
  /* required-end */
  /* default-start */
  "useResult":false,
  "useStatementSet":false,
  "fragment":false,
  "maxRowNum":100,
  "checkPoint":0,
  "parallelism":1,
  /* default-start */
  /* custom-start */
  "jobName":"openapitest",
  "savePointPath":"hdfs://ns/flink/savepoints/savepoint-5f4b8c-4326844a6843",
  "configuration":{
    "table.exec.resource.default-parallelism":2
  }
  /* custom-end */
}
```

Yarn-session:

```
 http://127.0.0.1:8888/openapi/executeSql 
{
  /* required-start */
  "type":"yarn-session",
  "address":"127.0.0.1:8081",
  "statement":"CREATE TABLE Orders (\r\n    order_number INT,\r\n    price        DECIMAL(32,2),\r\n    order_time   TIMESTAMP(3)\r\n) WITH (\r\n  'connector' = 'datagen',\r\n  'rows-per-second' = '1',\r\n  'fields.order_number.kind' = 'sequence',\r\n  'fields.order_number.start' = '1',\r\n  'fields.order_number.end' = '1000'\r\n);\r\nCREATE TABLE pt (\r\nordertotal INT,\r\nnumtotal INT\r\n) WITH (\r\n 'connector' = 'print'\r\n);\r\ninsert into pt select 1 as ordertotal ,sum(order_number)*2 as numtotal from Orders",
  /* required-end */
  /* default-start */
  "useResult":false,
  "useStatementSet":false,
  "useChangeLog":false,
  "useAutoCancel":false,
  "fragment":false,
  "maxRowNum":100,
  "checkPoint":0,
  "parallelism":1,
  /* default-start */
  /* custom-start */
  "jobName":"openapitest",
  "savePointPath":"hdfs://ns/flink/savepoints/savepoint-5f4b8c-4326844a6843",
  "configuration":{
    "table.exec.resource.default-parallelism":2
  }
  /* custom-end */
}
```

Standalone:

```
 http://127.0.0.1:8888/openapi/executeSql
{
  /* required-start */
  "type":"standalone",
  "address":"127.0.0.1:8081",
  "statement":"CREATE TABLE Orders (\r\n    order_number INT,\r\n    price        DECIMAL(32,2),\r\n    order_time   TIMESTAMP(3)\r\n) WITH (\r\n  'connector' = 'datagen',\r\n  'rows-per-second' = '1',\r\n  'fields.order_number.kind' = 'sequence',\r\n  'fields.order_number.start' = '1',\r\n  'fields.order_number.end' = '1000'\r\n);\r\nCREATE TABLE pt (\r\nordertotal INT,\r\nnumtotal INT\r\n) WITH (\r\n 'connector' = 'print'\r\n);\r\ninsert into pt select 1 as ordertotal ,sum(order_number)*2 as numtotal from Orders",
  /* required-end */
  /* default-start */
  "useResult":false,
  "useStatementSet":false,
  "useChangeLog":false,
  "useAutoCancel":false,
  "fragment":false,
  "maxRowNum":100,
  "checkPoint":0,
  "parallelism":1,
  /* default-start */
  /* custom-start */
  "jobName":"openapitest",
  "savePointPath":"hdfs://ns/flink/savepoints/savepoint-5f4b8c-4326844a6843",
  "configuration":{
    "table.exec.resource.default-parallelism":2
  }
  /* custom-end */
}
```

**getJobData**

```
 http://127.0.0.1:8888/openapi/getJobData?jobId=195352b0a4518e16699983a13205f059
```

**getJobPlan**

```
 http://127.0.0.1:8888/openapi/getJobPlan
{
  /* required-start */
  "statement":"CREATE TABLE Orders (\r\n    order_number INT,\r\n    price        DECIMAL(32,2),\r\n    order_time   TIMESTAMP(3)\r\n) WITH (\r\n  'connector' = 'datagen',\r\n  'rows-per-second' = '1',\r\n  'fields.order_number.kind' = 'sequence',\r\n  'fields.order_number.start' = '1',\r\n  'fields.order_number.end' = '1000'\r\n);\r\nCREATE TABLE pt (\r\nordertotal INT,\r\nnumtotal INT\r\n) WITH (\r\n 'connector' = 'print'\r\n);\r\ninsert into pt select 1 as ordertotal ,sum(order_number)*2 as numtotal from Orders",
  /* required-end */
  /* default-start */
  "useStatementSet":false,
  "fragment":false,
  "parallelism":1,
  /* default-start */
  /* custom-start */
  "configuration":{
    "table.exec.resource.default-parallelism":2
  }
  /* custom-end */
}
```

**getStreamGraph**

```
 http://127.0.0.1:8888/openapi/getStreamGraph 
{
  /* required-start */
  "statement":"CREATE TABLE Orders (\r\n    order_number INT,\r\n    price        DECIMAL(32,2),\r\n    order_time   TIMESTAMP(3)\r\n) WITH (\r\n  'connector' = 'datagen',\r\n  'rows-per-second' = '1',\r\n  'fields.order_number.kind' = 'sequence',\r\n  'fields.order_number.start' = '1',\r\n  'fields.order_number.end' = '1000'\r\n);\r\nCREATE TABLE pt (\r\nordertotal INT,\r\nnumtotal INT\r\n) WITH (\r\n 'connector' = 'print'\r\n);\r\ninsert into pt select 1 as ordertotal ,sum(order_number)*2 as numtotal from Orders",
  /* required-end */
  /* default-start */
  "useStatementSet":false,
  "fragment":false,
  "parallelism":1,
  /* default-start */
  /* custom-start */
  "configuration":{
    "table.exec.resource.default-parallelism":2
  }
  /* custom-end */
}
```

**savepoint**

```
 http://127.0.0.1:8888/openapi/savepoint
{
  /* required-start */
  "jobId":"195352b0a4518e16699983a13205f059",
  "savePointType":"trigger", // trigger | stop | cancel
  /* required-end */
  /* custom-start */
  "savePoint":"195352b0a4518e16699983a13205f059",
  "address":"127.0.0.1:8081",
  "gatewayConfig":{
    "clusterConfig":{
      "appId":"application_1637739262398_0032",
      "flinkConfigPath":"/opt/src/flink-1.13.3_conf/conf",
      "flinkLibPath":"hdfs:///flink13/lib/flinklib",
      "yarnConfigPath":"/usr/local/hadoop/hadoop-2.7.7/etc/hadoop"
    }
  }
  /* custom-start */
}
```

**savepointTask**

```
http://127.0.0.1:8888/openapi/savepointTask
{
  /* required-start */
  "taskId":"1",
  /* required-end */
  /* custom-start */
  "type":"trigger" // trigger | stop | cancel | canceljob
  /* custom-start */
}
```

**submitTask**

```
http://127.0.0.1:8888/openapi/submitTask?id=1 
```

:::tip 说明
OpenAPI 包括元数据、数据开发、数据集成、运维中心、数据质量、数据服务等。其中数据开发已经开发完成，其他根据版本发布会逐步实现。如果您在数据开发过程中需要使用
Dinky OpenAPI，使用方式请参见示例。
:::