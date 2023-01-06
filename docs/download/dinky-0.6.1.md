---
sidebar_position: 96
title: 0.6.1 release
---



| 版本     | 二进制程序                                                                                                                   |   Source   |
| ---------- |----------------------------------------------------------------------------------------------------------------------| ---- |
| 0.6.1 | [dinky-release-0.6.1.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/0.6.1/dinky-release-0.6.1.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/0.6.1.zip) |


## Dinky发行说明

Dinky 0.6.1 是一个 Bug 修复版本。

### 新功能

- 新增 issue 模板
- 新增 Phoenix 的 Flink Connector
- 新增 savepointTask 的 Open API
- 新增 WeChat WebHook 报警方式
- 新增 数据开发全屏的退出按钮
- 新增 飞书 WebHook 报警方式
- 新增 邮箱 报警方式

### 修复和优化

- 修复 Jar 任务存在空配置时提交失败的 bug
- 修复 Mysql 字段类型转换的 bug
- 修复 Hive 多语句查询失败的 bug
- 修复 Jar 任务无法被运维中心监控的 bug
- 升级 mybatis-plus-boot-starter 至最新版本 3.5.1
- 修复 报警实例表单联动的 bug
- 修复 数据源元数据表信息切换无效的 bug
- 修复 用户信息修改导致密码被二次加密的 bug
- 修复 启动文件的编码格式为 LF
- 修复 FlinkSQL 美化时出现空格的 bug
- 修复 停止 per-job 任务无法销毁集群实例的 bug
- 优化 文档管理表单
- 修复 字段级血缘的无法解析语句集的 bug
- 修复 字段级血缘的无法解析语句集的 bug
- 修复 钉钉报警表单无法正确展示的 bug
- 修复 FlinkSQL 执行或提交由于空配置导致的 bug