---
sidebar_position: 83
title: 1.0.0 release
---

| Dinky 版本 | Flink 版本 | 二进制程序                                                                                                                           | Source                                                                                |
|----------|----------|---------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------|
| 1.0.0    | 1.14     | [dinky-release-1.14-1.0.0.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.0.0/dinky-release-1.14-1.0.0.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.0.0.zip) |
| 1.0.0    | 1.15     | [dinky-release-1.15-1.0.0.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.0.0/dinky-release-1.15-1.0.0.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.0.0.zip) |
| 1.0.0    | 1.16     | [dinky-release-1.16-1.0.0.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.0.0/dinky-release-1.16-1.0.0.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.0.0.zip) |
| 1.0.0    | 1.17     | [dinky-release-1.17-1.0.0.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.0.0/dinky-release-1.17-1.0.0.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.0.0.zip) |
| 1.0.0    | 1.18     | [dinky-release-1.18-1.0.0.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.0.0/dinky-release-1.18-1.0.0.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.0.0.zip) |

## Dinky-1.0.0 发行说明

### 升级说明

- Dinky 1.0 是一个重构版本，对已有的功能进行重构，并新增了若干企业级功能，修复了 0.7 的一些局限性问题。 目前无法直接从 0.7 升级到 1.0。建议重新部署 1.0 版本。
- 自 Dinky 1.0 版本起，Dinky 社区将不再维护 1.0 之前的所有版本。
- 自 Dinky 1.0 版本起，Dinky 社区将对 Flink 1.14.x 及以上版本提供支持，不再维护 Flink 1.14 以下的版本。同时 Flink 新增一些特性，Dinky 也会逐步支持。
- Dinky 1.0 及以后版本, Flink 每新增一个大版本，Dinky 也会新增一个大版本，同时会视情况剔除一个 Dinky-Client 版本。剔除版本可能会进行投票，投票结果决定剔除的版本。
- 在重构过程中陆续发布了 4 个 RC 版本,RC 版本可以进行升级,但是仍建议重新部署 1.0-RELEASE 版本。避免出现一些位置问题。
- Dinky 0.7 版本的用户可以继续使用 0.7 版本，但是不再提供任何维护和支持。建议尽快安装 1.0 版本。

:::danger 注意
- 0.7 版本至 1.0 版本的变更较大,存在一些不兼容变更,使用 0.7 版本的用户无法直接升级到 1.0 版本，建议重新部署 1.0 版本。
:::

### 不兼容变更
- CDCSOURCE 动态变量定义由`${}`改为`#{}`
- `_CURRENT_DATE_` 等全局变量移除，使用表达式变量代替
- Flink Jar 任务定义由表单改为 EXECUTE JAR 语法
- Application 模式的 dinky-app-xxxx.jar 的定义移到集群配置中
- 数据库DDL 部分不兼容升级
- Dinky内置Catalog的类型属性由 `dlink_catalog` 变更为 `dinky_catalog`

### 重构
- 重构数据开发
- 重构运维中心
- 重构注册中心
- 重构 Flink 任务提交流程
- 重构 Flink Jar 任务提交方式
- 重构 CDCSOURCE 整库同步代码架构
- 重构 Flink 任务监控与告警
- 重构权限管理
- 重构系统配置为在线配置
- 重构推送DolphinScheduler
- 重构打包方式


### 新功能
- 数据开发支持代码片段提示
- 支持实时打印 Flink 表数据
- 控制台实时打印任务提交 log
- 支持 Flink CDC 3.0 整库同步
- 支持自定义告警规则和自定义告警模板
- 支持Flink k8s operator 提交
- 支持代理 Flink webui 访问
- 新增 Flink 任务 Metrics监控自定义图表
- 支持 Dinky jvm 监控
- 新增资源中心功能（local,hdfs,oss）并扩展 rs 协议
- 新增 Git UDF/JAR 项目托管及整体构建流程
- 支持全模式Flink jar 任务提交
- 新增 ADD CUSTOMJAR 语法动态加载依赖
- 新增 ADD FILE 语法动态加载文件
- openapi 支持自定义参数提交
- 权限系统升级，支持租户，角色，token，菜单权限
- 支持LDAP
- 数据开发页面新增小工具功能
- 支持推送依赖任务至 DolphinScheduler
- 实现 Flink 实例停止功能
- 实现 CDCSOURCE 整库同步多并行度下数据有序
- 实现可配置方式的告警防重发功能
- 实现普通 SQL 可被 DolphinScheduler 调度执行
- 新增获取系统内加载的依赖 JAR，并进行分组， 便于排查 JAR 相关问题
- 实现集群配置测试连接功能
- 支持 H2、Mysql、Postgre 部署,默认为 H2

### 新语法
- CREATE TEMPORAL FUNCTION  用于定义临时表函数
- ADD FILE 用于动态加载类/配置等文件
- ADD CUSTOMJAR 用于动态加载JAR依赖
- PRINT TABLE 用于实时预览表数据
- EXECUTE JAR 用于定义 Flink Jar 任务
- EXECUTE PIPELINE 用于定义 Flink CDC 3.x 整库同步任务

### 修复
- 修复 auto.sh 的 CLASS_PATH 中缺少 extends 路径的问题
- 修复发布/下线后作业列表生命周期状态值没有重新渲染的问题
- 修复 Flink 1.18 set 语法不工作并产生 null 错误
- 修复提交历史的保存点机制问题
- 修复 Dinky Catalog 中创建视图的问题
- 修复 Flink application 不会抛出异常
- 修复告警选项渲染不正确
- 修复作业生命周期的问题
- 修复集群配置中 k8s 的 YAML 无法显示
- 修复运维中心作业列表耗时格式化错误
- 修复 Flink dag 提示框问题
- 修复 checkpoint 的路径未找到
- 修复向海豚调度推送作业时节点位置错误
- 修复当 set 配置中包含单引号时作业参数未生效的问题
- 升级 jmx_prometheus_javaagent 到 0.20.0 来解决一些 CVE
- 修复 checkpoint 展示问题
- 修复作业实例始终处于运行中状态
- 修复 Yarn Application 提交任务失败后缺少日志打印
- 修复作业配置不能渲染 yarn prejob 集群
- 修复 URL 拼错导致请求失败
- 修复多用户登录时出现相同令牌值插入错误的问题
- 修复告警实例表单渲染问题
- 修复FlinkSQLEnv 无法检查的问题
- 修复 set 语句无法生效的问题
- 修复yarn集群配置自定义Flink，hadoop配置无效问题
- 修复运维中心检查点信息获取不到问题
- 修复Yarn Application作业结束后状态无法检测问题
- 修复yarn作业提交失败控制台日志无打印问题
- 修复从集群配置启动的Flink 实例无法在作业配置中选择的问题
- 修复RECONNECT状态作业状态识别错误
- 修复 FlinkJar 任务在提交到 PreJob 模式的问题
- 修复Dinky启动检测pid问题
- 修复内置 Paimon 与用户集成版本不一致时,导致冲突的问题(使用 shade 实现)
- 修复FlinkJar 任务在 Application 模式下CheckPoint 参数不生效问题
- 修复修改 Task 作业时名称和备注信息更新错误的问题
- 修复注册数据源时密码为必填的问题
- 修复集群实例心跳检测不正确的问题
- 修复 Jar 任务提交不能使用 set 语法的问题
- 修复数据开发-> 作业列表部分情况下无法折叠的问题
- 修复多线程下告警信息重复发送的问题
- 修复数据开发-> 打开作业的 tag 高度问题
- 修复运维中心作业详情的 jobmanager 日志部分情况下无法正常展示的问题
- 修复 Catalog NPE 的问题
- 修复prejob 任务状态错误的问题
- 修复 add customjar 语法问题
- 修复 jar 任务无法监控的问题
- 修复 Token 无效异常
- 修复语句分隔符导致的一系列问题, 并移除系统内配置
- 修复运维中心任务状态渲染问题
- 修复作业实例不存在时删除任务失败的问题
- 修复重复异常告警
- 修复 PythonFlink 提交的一些问题
- 修复 Application Mode 无法使用全局变量的问题
- 修复 K8s 任务由于未初始化资源类型导致无法启动问题
- 修复 Jar 任务的 pipeline 获取错误导致前端无法正常使用
- 修复 SqlServer 时间戳转换为字符串
- 修复带有 UDF的任务发布时 NPE问题
- 修复 Jar 任务无法获取执行历史问题
- 修复 Doris 数据源获取 DDL 以及查询时 NPE 导致前端崩溃问题

### 优化
- 添加作业配置项的键宽度
- 优化查询作业目录树
- 优化 Flink on yarn 的 app 提交
- 优化 Explainer 类使用建造者模式来构建结果
- 优化文档管理
- 通过 SPI 来实现 operator
- 优化文档表单弹出层
- 优化 Flink 实例的类型渲染
- 优化数据源详情搜索框
- 获取版本方式优化为后端接口返回
- 优化CANCEL作业逻辑，对于失联作业可强制停止
- 优化注册中心部分删除时的检测引用逻辑
- 优化作业创建时可指定作业模版
- 优化Task 删除逻辑
- 优化部分前端国际化
- 优化执行预览时控制台与结果 Tag 自动切换
- 优化 K8S的 UDF 下载逻辑
- 优化整库同步之分库分表
- 优化注册中心->数据源列表跳转到详情页的逻辑
- 作业配置逻辑优化(作业已发布状态下作业配置不可编辑)
- 优化数据开发中作业配置的集群实例渲染逻辑
- 优化Flink集群的心跳检测
- 优化数据源获取数据异常未反馈前端问题
- 优化程序停机策略为优雅停机
- CDCSOURCE 支持最早偏移和时间戳 scanStartupMode
- 取消任务表保存点路径唯一性限制
- 优化CDCSOURCE从Mysql到Doris的light_schema_change
- 优化启动脚本类路径添加 FLINK_HOME
- 优化一些前端的绝对路径为相对路径
- 修改默认的 admin 账户的密码为强密码

### 文档
- 完善注册中心的集群实例列表文档
- 完善注册中心的告警文档
- 完善注册中心的Git项目文档
- 修改域名
- 完善注册中心和认证中心的文档
- 完善贡献者开发文档
- 在 CDCSOURCE 添加参数描述 debezium.*
- 修改官网文档结构
- 添加部分数据开发相关文档
- 删除一些弃用/错误的文档
- 添加快速开始文档
- 添加部署文档
- 优化分库分表的文档
- 优化常规部署文档
- 添加告警防重发相关文档
- 优化 openapi 文档
- 添加 HDFS HA 配置文档
- 优化 LDAP 相关文档
- 修复一些文档单词错误问题
- 修复集成DolphinScheduler文档中版本错误问题


### 安全
- CVE-2023-2976
- CVE-2020-8908

### 其他
- 增加一些自动化 Action
