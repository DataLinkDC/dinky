---
sidebar_position: 76
title: 1.2.2 release
---

| Dinky 版本 | Flink 版本 | 二进制程序                                                                                                                                   | Source                                                                                |
|----------|----------|-----------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------|
| 1.2.2    | 1.14     | [dinky-release-1.14-1.2.2.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.2.2/dinky-release-1.14-1.2.2.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.2.2.zip) |
| 1.2.2    | 1.15     | [dinky-release-1.15-1.2.2.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.2.2/dinky-release-1.15-1.2.2.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.2.2.zip) |
| 1.2.2    | 1.16     | [dinky-release-1.16-1.2.2.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.2.2/dinky-release-1.16-1.2.2.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.2.2.zip) |
| 1.2.2    | 1.17     | [dinky-release-1.17-1.2.2.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.2.2/dinky-release-1.17-1.2.2.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.2.2.zip) |
| 1.2.2    | 1.18     | [dinky-release-1.18-1.2.2.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.2.2/dinky-release-1.18-1.2.2.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.2.2.zip) |
| 1.2.2    | 1.19     | [dinky-release-1.19-1.2.2.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.2.2/dinky-release-1.19-1.2.2.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.2.2.zip) |
| 1.2.2    | 1.20     | [dinky-release-1.20-1.2.2.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.2.2/dinky-release-1.20-1.2.2.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.2.2.zip) |

## Dinky-1.2.2 发行说明

### 升级说明

:::warning 重要
v1.2.2 替换所有 Dinky 相关 jar 后直接重启可自动升级。
:::

### 新功能
- 支持数据开发页面目录树列表的滚动交互与搜索

### 修复
- 修复 Flink 中挂载日志配置文件的漏洞
- 修复无法执行如创建数据库等语句的问题
- 修复任务提交期间 pipeline.jars 的配置
- 修复在详情页切换标签时重复提交作业的问题
- 修复无法创建名称相同但父级不同的项目的问题
- 修复解析全局变量时出现的 “不存在” 错误报告问题
- 修复由于配置为空导致任务启动失败
- 修复了任务推送后历史版本未刷新的问题

### 优化
- 启用英文时注册中心文档模态框显示优化
- 替换 NPM，切换到 PNPM
- 优化 UDF 已保存占位符
- 优化 WebSocket 的架构，使其能与 Spring 事件协同工作
- 优化目录表信息
- 在数据预览时，将时间戳类型字段显示为字符串值
- 与 kubernetes.container.image 和 kubernetes.container.image.ref 兼容
- 优化 web 项目的 package.json 内容
- 优化提示消息的国际化
- 优化作业执行前对集群配置可用性的验证
