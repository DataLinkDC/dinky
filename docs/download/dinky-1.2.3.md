---
sidebar_position: 75
title: 1.2.3 release
---

| Dinky 版本 | Flink 版本 | 二进制程序                                                                                                                                   | Source                                                                                |
|----------|----------|-----------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------|
| 1.2.3    | 1.14     | [dinky-release-1.14-1.2.3.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.2.3/dinky-release-1.14-1.2.3.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.2.3.zip) |
| 1.2.3    | 1.15     | [dinky-release-1.15-1.2.3.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.2.3/dinky-release-1.15-1.2.3.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.2.3.zip) |
| 1.2.3    | 1.16     | [dinky-release-1.16-1.2.3.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.2.3/dinky-release-1.16-1.2.3.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.2.3.zip) |
| 1.2.3    | 1.17     | [dinky-release-1.17-1.2.3.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.2.3/dinky-release-1.17-1.2.3.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.2.3.zip) |
| 1.2.3    | 1.18     | [dinky-release-1.18-1.2.3.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.2.3/dinky-release-1.18-1.2.3.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.2.3.zip) |
| 1.2.3    | 1.19     | [dinky-release-1.19-1.2.3.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.2.3/dinky-release-1.19-1.2.3.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.2.3.zip) |
| 1.2.3    | 1.20     | [dinky-release-1.20-1.2.3.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.2.3/dinky-release-1.20-1.2.3.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.2.3.zip) |

## Dinky-1.2.3 发行说明

### 升级说明

:::warning 重要
v1.2.3 替换所有 Dinky 相关 jar 后直接重启可自动升级。
:::

### 新功能
- 支持从最新完成的 checkpoint 恢复

### 修复
- 修复 Flink SQL 历史版本切换按钮不起作用的问题
- 修复页面不断刷新的问题
- 修复两个具有相同前缀的 Flink 配置项导致 Flink 配置解析出错的问题
- 修复创建新任务时前端表单被清空的问题
- 将helm的配置与 src/main/resources 目录进行同步
- 修复两个前缀相同的 Flink 配置项导致 Flink 配置解析出错的问题
- 修复 Paimon 查询中数组类型未显示值的问题
- 修复 metadata-hive MissingFormatArgumentException
- 修复实用工具类中的导入错误并解决与前端显示相关的配置问题
- 修复数据源中心 Paimon 的 tinyint 数据不能预览
- 修复flinkjar任务历史缓存对比错误
- 修复 websocket 重连导致页面闪烁
- 修复登录页初始化请求加载多次

### 优化
- 在启动 Flink 会话之前检查集群是否已禁用
- 优化 WebSocket 以实现异步发送
