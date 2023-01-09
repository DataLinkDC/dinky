---

sidebar_position: 89
title: 0.7.0 release
--------------------

|  版本   |                                                         二进制程序                                                         |                                        Source                                         |
|-------|-----------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------|
| 0.7.0 | [dlink-release-0.7.0.tar.gz](https://github.com/DataLinkDC/dlink/releases/download/v0.7.0/dlink-release-0.7.0.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dlink/archive/refs/tags/v0.7.0.zip) |

## Dinky发行说明

Dinky 0.7.0 是一个新功能版本。

:::warning 注意
该版本多租户功能已开放 <br/>
此版本有表结构变更 <br/>
需要按需执行 **sql/upgrade/0.7.0_schema/mysql/dinky_ddl.sql** 和 **sql/upgrade/0.7.0_schema/mysql/dinky_dml.sql**
:::

### 新功能

- 支持 Apache Flink 1.16.0
- 新增 Java udf 打包
- 支持 Flink Session 模式自动加载 udf
- 支持 Flink Per-Job 模式自动加载 udf
- 支持 Flink Application 模式自动加载 udf
- 支持 Python udf 在线开发
- 支持 Scala udf 在线开发
- 支持自定义的 K8S Application 提交
- 新增 FlinkJar 文件上传
- 从逻辑计划分析字段血缘支持 Flink 所有版本
- Flink JDBC 支持数据过滤
- Flink JDBC 分区查询支持 datetime
- 新增多租户管理
- 新增在登录时选择租户
- 新增海豚调度自动创建任务
- 新增系统日志控制台
- 新增执行进度控制台
- 新增 Flink udf 模板
- 新增 Presto 数据源
- 新增作业树目录级删除功能
- 新增 CDCSOURCE 的 datastream-doris-ext 支持元数据写入
- 数据开发元数据添加刷新按钮
- 新增数据源复制
- 新增前端国际化
- 新增后端国际化
- 新增 kerberos 验证
- 新增 K8S 自动部署应用
- 新增 Local 模式的 FlinkWebUI

### 修复

- 修复从指定的 savepoint 恢复任务时未设置 savepint 文件路径导致的问题
- 修复 StarRocks 数据源不可见
- 修复数据源查询数据后切换数据源导致报错
- 修复数据源查询视图元数据报错
- 修复 Oracle 验证查询的错误
- 修复 PG 数据库元数据获取 schema 失败
- 修复 kafka properties 没有启用
- 修复 jobConfig useAutoCancel 参数传递错误
- 修复由于多租户导致的作业监控报错
- 修复集群实例删除导致已存在的任务无法停止
- 修复 Application 模式缺失数据源变量
- 修复连续单击任务项将打开多个选项卡问题
- 修复 cdcsource KafkaSink 不支持添加 transactionalIdPrefix 导致 kafka product 发送消息失败
- 修复当集群别名为空时集群无法展示
- 修复作用版本查询错误
- 修复作业完成时 Per-Job 和 Application 的状态始终未知
- 修复引导页的连接错误
- 修复 Open API 没有租户

### 优化

- 增加 MysqlCDC 的参数配置
- 优化 datastream kafka-json 和 datastream starrocks
- 支持元数据缓存到 Redis
- 自动在 doris label prefix 后追加 uuid
- 优化租户切换
- 修改资源中心为认证中心
- 添加 spotless 插件
- 优化不同版本的 SQL 文件
- 改进 MySQL 表的自动创建
- 优化 postgres 元数据信息
- 优化 postgre 建表语句
- 优化 Flink Oracle Connector
- 优化 maven assembly 和 profile
- 兼容 Java 11
- 删除数据源的重复初始化
- 升级 mysql 驱动版本至 8.0.28
- 升级 Flink 1.14.5 到 1.14.6
- 升级 Guava 和 Lombok 版本
- 升级jackson 和 sa-token 版本

### 文档

- 优化 ReadMe
- 更新官网
- 优化部署文档
- 添加 Flink Metrics 监控和优化的文档

### 贡献者

- @aiwenmo
- @billy-xing
- @boolean-dev
- @chengchuen
- @Forus0322
- @hxp0618
- @ikiler
- @leechor
- @lewnn
- @jinyanhui2008
- @ren-jq101
- @rookiegao
- @siriume
- @tgluon
- @wmtbnbo
- @wuzhenhua01
- @zackyoungh
- @zhongjingq
- @zhu-mingye
- @ziqiang-wang
- @Zzih
- @zzzzzzzs

