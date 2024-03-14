---
sidebar_position: 82
title: 1.0.1 release
---

| Dinky 版本 | Flink 版本 | 二进制程序                                                                                                                           | Source                                                                                |
|----------|----------|---------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------|
| 1.0.1    | 1.14     | [dinky-release-1.14-1.0.1.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.0.1/dinky-release-1.14-1.0.1.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.0.1.zip) |
| 1.0.1    | 1.15     | [dinky-release-1.15-1.0.1.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.0.1/dinky-release-1.15-1.0.1.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.0.1.zip) |
| 1.0.1    | 1.16     | [dinky-release-1.16-1.0.1.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.0.1/dinky-release-1.16-1.0.1.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.0.1.zip) |
| 1.0.1    | 1.17     | [dinky-release-1.17-1.0.1.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.0.1/dinky-release-1.17-1.0.1.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.0.1.zip) |
| 1.0.1    | 1.18     | [dinky-release-1.18-1.0.1.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.0.1/dinky-release-1.18-1.0.1.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.0.1.zip) |

## Dinky-1.0.1 发行说明

### 升级说明

:::warning 提示
- 1.0.1 是一个 BUG 修复版本,无数据库升级变更,可以直接升级
- 关于 SCALA 版本: 发版使用 Scala-2.12 , 如你的环境必须使用 Scala-2.11, 请自行编译,请参考 [编译部署](../docs/next/deploy_guide/compile_deploy) , 将 profile 的 scala-2.12 改为 scala-2.11
:::


### 新功能
- 添加一些 Flink Options 类,用于触发快捷提示
- 实现数据开发中控制台日志自动滚动


### 修复
- 修复短信告警插件未打包的问题
- 修复创建 UDF 时 NPE 的异常和一些其他问题
- 修复创建任务时作业类型渲染异常
- 修复数据开发中查看 Catalog 时页面崩溃的问题
- 修复 `add jar` 和 s3 一起使用时的参数配置问题
- 修复一些 ``rs`` 资源协议的问题
- 修复数据开发中快捷导航中的路由错误跳转问题
- 修复当选择 UDF 任务类型时, 控制台没有关闭的问题
- 修复 `decimal` 数据类型超过 38 时位数的问题(超过 38 位将转为 string)
- 修复一些弹框无法关闭的问题
- 修复 application 模式下全局变量无法识别的问题
- 修复 application 模式下获取 container 时会存在数组越界的问题
- 修复 `add file` 无法解析的问题


### 优化
- 优化一些前端请求 URL 到同意常量中
- 优化启动脚本,移除 FLINK_HOME 环境变量加载
- 优化密码错误时的提示信息
- 优化数据开发任务的 tag 展示
- 关闭数据开发编辑器内的自动预览
- 优化表达式变量定义方式,由文件定义改为系统配置中定义
- 优化 application 模式下不支持查询语句的提示信息
- 优化`FlinkSQL 环境`列表的渲染效果
- 优化 GIT 项目构建时的环境检查异常提示
- 优化集群在心跳检测的时候可能出现 NPE 的问题

### 文档
- 添加整库同步的内置变量文档
- 优化文档版本
- 添加 `EXECUTE JAR` 任务 DEMO
- 优化创建集群配置时的一些文案提示
- 优化整库同步文档中的部分路径


