---
sidebar_position: 90
title: 0.6.7 release
---



| 版本    | 二进制程序                                                                                                                 | Source                                                                                |
|-------|-----------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------|
| 0.6.7 | [dlink-release-0.6.7.tar.gz](https://github.com/DataLinkDC/dlink/releases/download/v0.6.7/dlink-release-0.6.7.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dlink/archive/refs/tags/v0.6.7.zip) |


## Dinky发行说明

Dinky 0.6.7 是一个修复的版本。

:::warning 注意
该版本多租户功能暂未开放
此版本有表结构变更 <br/>
需要按需执行 **sql/dlink_history.sql** 文件的增量 DDL
:::


### 新功能

- 添加多租户的实现
- 一键上线和下线作业
- 添加全局变量管理
- 添加命名空间与密码的表单
- 登录时选择多租户
- 多租户前端业务管理实现
- 添加 github 工作流来检查代码风格、测试类和打包
- 添加 druid 连接池来解决jdbc多连接问题
- 新增用户授权角色的功能
- 修改 Flink 默认版本为 1.14
- 新增全局变量管理实现
- 新增 SqlServer 整库同步
- 新增全局变量在 Flinksql 中生效
- 新增字段血缘分析从 Flink 逻辑计划获取
- 新增 postgresql 整库同步
- 修改 checkstyle 为必须的工作
- 新增 swagger api 文档
- cdcsource 增加多目标库同步功能
- 新增文件上传
- Jar 和集群配置管理新增文件上传
- 新增 StarRocks 数据源
- 新增任务监控失败重复的容错时间
- 新增数据开发任务信息日志详情按钮

### 修复

- 修改任务监控代码重复判断的问题
- 修复邮件报警参数问题
- 修复获取作业实例信息可能获取到错误的结果问题
- 修复 doris 连接器批量写入时发生异常导致写入失败
- 修复 SQLSinkBuilder.buildRow 的错误
- 修复 Flink1.14 执行缺失依赖的问题
- 修复 savepoint 接口获取前端集群表单的 taskId 为空的问题
- 修复 yarn per-job 无法自动释放资源的问题
- 修复多租户新增角色和删除角色的问题
- 修复 dlink-conector-pulsar-1.14 找不到 SubscriptionType 的报错
- 修复 flink1.14 savepoint 时的 jackjson 问题
- 修复元数据字段类型转换的问题
- 修复整库同步 KafkaSinkBuilder 未序列化导致报错
- 修复注册中心文档管理的查询条件错误
- 修复 yarn perjob/application 和 k8s application 集群配置未生效
- 修复 k8s application 模式提交失败，优化增加获取JobId等待时间
- 修复日志 banner 的错误
- 修复 UDF 和 UDTAF 在 Flink 1.14 的错误
- 修复 yarn-application 任务分隔符错误
- 修复重命名作业后保存作业失败
- 修复提交历史的第二次弹框时无内容

### 优化

- 优化前端和文档
- 优化作业被删除后作业版本未被删除
- 优化作业树在导入作业后溢出的问题
- 优化数据开发的进程列表
- 优化整库同步分流逻辑
- 优化git提交忽略的文件类型
- 优化中文和英文 Readme
- 移除一些接口的敏感信息
- 优化多租户
- 添加 Maven Wrapper
- 优化整库同步的时区问题
- 优化sql默认分隔符统一为 ;\n
- 优化代码风格的错误
- 添加 .DS_Store 到 git 的忽略文件类型
- 优化多租户和表单渲染
- 优化多租户角色穿梭框和前端回显
- 优化用户关联角色渲染
- 优化 dlink-admin 的代码风格
- 优化 dlink-alert 的代码风格
- 优化 dlink-common 的代码风格
- 优化 dlink-catalog 的代码风格
- 优化 dlink-client 的代码风格
- 优化 dlink-app 的代码风格
- 优化数据源连接池和链接创建
- 优化 dlink-connectors 的代码风格
- 优化 dlink-core 的代码风格
- 优化 dlink-daemon 的代码风格
- 优化 dlink-executor 的代码风格
- 优化 dlink-function 和 dlink-gateway 的代码风格
- 优化 dlink-metadata 的代码风格
- 添加协议头到 pom 文件
- 优化项目打包和启动文件
- dlink-client-hadoop 打包增加 ServicesResourceTransformer
- 优化配置文件和静态资源目录打包
- 配置全局 checkstyle 验证
- 添加 sqlserver 的 date 类型转换
- 优化PG数据库 schema_name 查询 sql
- Doris 支持更多语法
- 优化整库同步 DorisSink
- 优化前端的展示与提示
- 优化数据开发作业目录默认折叠
- 优化 DorisSink 和升级 Flink 1.15.2
- 升级 Flink 1.15 版本为 1.15.2
- 优化 SqlServer 字段类型查询

### 文档

- 合并官网文档仓库源码至主仓库的 docs 目录下
- 添加 Flink 1.15 文档
- 整库同步文档修复
- 添加导入导出作业的文档
- 优化多个文档
- 更新主页和基础信息的文档
- 新增 flink 扩展 redis 的实践分享
- 优化部署文档
