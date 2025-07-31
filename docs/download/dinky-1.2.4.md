---
sidebar_position: 74
title: 1.2.4 release
---

| Dinky 版本 | Flink 版本 | 二进制程序                                                                                                                                   | Source                                                                                |
|----------|----------|-----------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------|
| 1.2.4    | 1.14     | [dinky-release-1.14-1.2.4.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.2.4/dinky-release-1.14-1.2.4.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.2.4.zip) |
| 1.2.4    | 1.15     | [dinky-release-1.15-1.2.4.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.2.4/dinky-release-1.15-1.2.4.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.2.4.zip) |
| 1.2.4    | 1.16     | [dinky-release-1.16-1.2.4.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.2.4/dinky-release-1.16-1.2.4.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.2.4.zip) |
| 1.2.4    | 1.17     | [dinky-release-1.17-1.2.4.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.2.4/dinky-release-1.17-1.2.4.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.2.4.zip) |
| 1.2.4    | 1.18     | [dinky-release-1.18-1.2.4.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.2.4/dinky-release-1.18-1.2.4.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.2.4.zip) |
| 1.2.4    | 1.19     | [dinky-release-1.19-1.2.4.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.2.4/dinky-release-1.19-1.2.4.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.2.4.zip) |
| 1.2.4    | 1.20     | [dinky-release-1.20-1.2.4.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.2.4/dinky-release-1.20-1.2.4.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.2.4.zip) |

## Dinky-1.2.4 发行说明

### 升级说明

:::warning 重要
v1.2.4 替换所有 Dinky 相关 jar 后直接重启可自动升级。
:::

### 新功能
- 支持 Flink table planner loader

### 修复
- 修复文档模块中子类型菜单不会随文档类型刷新的问题
- 修复 CDCSOURCE 中启用自动建表时 sink.db 不能为空的问题
- 修改 K8s 部署命令
- 修复 JAR 包中 SQL 语句拆分问题
- 修复缺少 dinky-cdc-plus.jar 的问题
- 修复任务定位错误问题
- 修复从其他发送方式切换到企业微信时出现的 bug
- 修复 CI/CD 的 k3d 相关 bug
- 修复 K8s operator 提交 bug
- 修复令牌无法正常刷新的问题

### 优化
- 目录服务（CatalogueService）新增 findByTaskId 方法
