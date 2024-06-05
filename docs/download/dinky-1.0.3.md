---
sidebar_position: 80
title: 1.0.3 release
---

| Dinky 版本 | Flink 版本 | 二进制程序                                                                                                                           | Source                                                                                |
|----------|----------|---------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------|
| 1.0.3    | 1.14     | [dinky-release-1.14-1.0.3.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.0.3/dinky-release-1.14-1.0.3.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.0.3.zip) |
| 1.0.3    | 1.15     | [dinky-release-1.15-1.0.3.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.0.3/dinky-release-1.15-1.0.3.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.0.3.zip) |
| 1.0.3    | 1.16     | [dinky-release-1.16-1.0.3.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.0.3/dinky-release-1.16-1.0.3.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.0.3.zip) |
| 1.0.3    | 1.17     | [dinky-release-1.17-1.0.3.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.0.3/dinky-release-1.17-1.0.3.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.0.3.zip) |
| 1.0.3    | 1.18     | [dinky-release-1.18-1.0.3.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.0.3/dinky-release-1.18-1.0.3.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.0.3.zip) |
| 1.0.3    | 1.19     | [dinky-release-1.19-1.0.3.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.0.3/dinky-release-1.19-1.0.3.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.0.3.zip) |

## Dinky-1.0.3 发行说明

### 升级说明

:::warning 提示

- 1.0.3 是一个 BUG 修复版本,无表结构变更,升级时无需执行额外的 SQL 脚本, 直接覆盖安装即可,注意配置文件的修改和依赖的放置
- 关于 SCALA 版本: 发版使用 Scala-2.12 , 如你的环境必须使用 Scala-2.11, 请自行编译,请参考 [编译部署](../docs/next/deploy_guide/compile_deploy) , 将 profile 的 scala-2.12 改为 scala-2.11

:::

### 新功能
- 新增在任务运行中卡死后可以手动杀死该进程的功能

### 修复
- 修复 Flink 1.19 的支持中 Yarn Application 模式无法执行任务的问题
- 修复启停脚本存在的问题,适配 jdk 11 的 GC 参数
- 修复 UDF 发布后无法找到类的问题
- 修复 在Application 任务  SQL 内,set 函数无法覆盖的优先级问题

### 优化
- 优化 Dinky 服务在监控时导致 CPU 负载过高,线程不释放的问题
- 优化 Dinky 监控配置,根据 `配置中心->全局配置->Metrics 配置->**Dinky JVM Monitor 开关**` 开关来控制是否开启 Flink 任务的监控
- 优化 Oracle 整库同步数据类型转换逻辑
- 优化监控数据前端渲染性能及展示效果





