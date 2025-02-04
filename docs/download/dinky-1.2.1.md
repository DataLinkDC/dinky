---
sidebar_position: 77
title: 1.2.1 release
---

| Dinky 版本 | Flink 版本 | 二进制程序                                                                                                                                   | Source                                                                                |
|----------|----------|-----------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------|
| 1.2.1    | 1.14     | [dinky-release-1.14-1.2.1.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.2.1/dinky-release-1.14-1.2.1.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.2.1.zip) |
| 1.2.1    | 1.15     | [dinky-release-1.15-1.2.1.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.2.1/dinky-release-1.15-1.2.1.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.2.1.zip) |
| 1.2.1    | 1.16     | [dinky-release-1.16-1.2.1.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.2.1/dinky-release-1.16-1.2.1.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.2.1.zip) |
| 1.2.1    | 1.17     | [dinky-release-1.17-1.2.1.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.2.1/dinky-release-1.17-1.2.1.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.2.1.zip) |
| 1.2.1    | 1.18     | [dinky-release-1.18-1.2.1.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.2.1/dinky-release-1.18-1.2.1.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.2.1.zip) |
| 1.2.1    | 1.19     | [dinky-release-1.19-1.2.1.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.2.1/dinky-release-1.19-1.2.1.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.2.1.zip) |
| 1.2.1    | 1.20     | [dinky-release-1.20-1.2.1.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.2.1/dinky-release-1.20-1.2.1.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.2.1.zip) |

## Dinky-1.2.1 发行说明

### 升级说明

:::warning 重要
v1.2.1 替换所有 Dinky 相关 jar 后直接重启可自动升级。
:::

### 新功能
- 支持 CALL 语句
- Flink Kubernetes opoerator 支持 ingress

### 修复
- 修复 Flink Jar 提交
- 修复由于枚举字段中的不正确的 get 方法导致的反序列化异常
- 更改 k8s StringUtils 的引入包
- 修复 FlinkJar 任务的参数无法解析全局变量
- 修复变量解析不能抛出异常
- 修复当目标表包含 '.' 时调试作业失败的问题
- 修复带有全局变量的作业的血缘解析失败的问题
- 修复 postgresql 使用 concat 导致失败的问题
- 修复作业锁策略条件
- 修复 FlinkJar作业丢失页面信息
- 修复点击触发 savepoint 的错误
- 修复读取 'root-exception' 的错误
- 修复查询 Paimon 的数值和日期类型数据时的错误
- 修复 CALL 语句不能在 standalone 集群执行
- 修复 set 语句在 application 模式不生效
- 修复 webocket 不能正确关闭

### 优化
- 优化脚本的执行逻辑
- 优化推送海豚调度的英文信息
- 优化 docker 镜像构建
- 优化主页图表和作业详情的血缘的暗夜主题
- 优化血缘关系图的展示
- 优化端到端测试
- 添加 CDCSOURCE 的 source 的 url 参数
- 优化用户定义的 Flink 配置路径重写参数

### 文档
- 修复常规部署文档的错误
- 快速体验文档更新

