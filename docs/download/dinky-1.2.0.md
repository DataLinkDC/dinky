---
sidebar_position: 78
title: 1.2.0 release
---

| Dinky 版本 | Flink 版本 | 二进制程序                                                                                                                           | Source                                                                                |
|----------|----------|---------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------|
| 1.2.0    | 1.14     | [dinky-release-1.14-1.2.0.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.2.0/dinky-release-1.14-1.2.0.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.2.0.zip) |
| 1.2.0    | 1.15     | [dinky-release-1.15-1.2.0.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.2.0/dinky-release-1.15-1.2.0.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.2.0.zip) |
| 1.2.0    | 1.16     | [dinky-release-1.16-1.2.0.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.2.0/dinky-release-1.16-1.2.0.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.2.0.zip) |
| 1.2.0    | 1.17     | [dinky-release-1.17-1.2.0.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.2.0/dinky-release-1.17-1.2.0.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.2.0.zip) |
| 1.2.0    | 1.18     | [dinky-release-1.18-1.2.0.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.2.0/dinky-release-1.18-1.2.0.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.2.0.zip) |
| 1.2.0    | 1.19     | [dinky-release-1.19-1.2.0.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.2.0/dinky-release-1.19-1.2.0.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.2.0.zip) |
| 1.2.0    | 1.20     | [dinky-release-1.20-1.2.0.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.2.0/dinky-release-1.19-1.2.0.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.2.0.zip) |

## Dinky-1.2.0 发行说明

### 升级说明

:::warning 重要
v1.2.0 替换所有 Dinky 相关 jar 后直接重启可自动升级。
:::

### 新功能
- 添加了 国内 npm 的镜像源(使用 profile 方式支持)
- 支持 Flink-1.20
- 支持 FlinkCDC 3.2.0
- 新增内置的 flink history server，从而极大降低了任务状态获取不正确的问题
- 增加角色和权限的一些页面提示
- 增加血缘展示获取时的 loading 效果
- 实现作业导入导出
- 支持对 FlinkSQL 及 CDCSOURCE 作业进行 Mock 预览测试
- 数据开发支持实时更新作业状态
- 增加首次部署时的引导初始化页面

### 修复
- 修复show databases 无法正常执行的问题
- 修复部分 json 序列化问题
- 修复 Flink1.19 下的 SQL Cli 存在的问题
- 修复k8s模式下 任务端口不可用的问题
- 修复 git 项目功能中存在的一些问题(默认值,拖拽排序)
- 修复kubernetes-operator模式下的 SavePoint 路径逻辑并调整 Flink 配置获取的配置方法
- 修复当数据开发页面打开过多任务时导致页面长时间无响应问题
- 修复 git 项目在构建过程中存在的问题
- 修复代码编辑器中的缩略图显示问题
- 修复 pg 的 sql 自动初始化问题
- 修复local 模式提交任务的问题
- 修复 Oracle 整库同步时的数据类型转换问题
- 修复工作台点击作业时,无实例产生的异常
- 修复 postgres 查询数据时语法报错问题
- 修复 flyway 无法支持 mysql5.7 的问题
- 修复 Oracle 数据源类型中获取主键列不正确的问题
- 修复任务列表排序不生效问题
- 修复 Git 项目页面重复刷新问题
- 修复钉钉告警可能存在的空指针异常
- 修复代理 Flink 地址存在的问题
- 修复查看catalog中的表结构无法正常显示问题

### 优化
- 优化获取任务详情时,支持https协议
- 优化工作台页面布局
- 删除dinky_cluster表的唯一索引,解决在 yarn/k8s 开启高可用时造成唯一索引冲突问题
- 优化部分 Mapper 的查询
- 优化 git 项目后端类声明属性的类型
- 删除UDF 注册管理中的提示文案
- 优化部分页面布局，使其在小屏幕上显示更加友好
- 优化版本更新逻辑，解决升级带来的缓存问题(自动对比版本实现)
- 优化数据源详情列表树节点过多时虚拟滚动不生效导致页面渲染过慢问题
- 优化登录页,解决资源过大卡顿问题
- 优化应用启动速度
- 优化将从集群配置启动的集群变更为手动注册
- 优化系统配置描述过长的展示布局
- 优化运维中心 flink 任务算子图的整体布局及渲染效率

### 重构
- 重构 user.dir 获取方式,避免不同部署环境下获取项目根路径错误的问题
- 将 SSE 重构替换为 websocket
- 重构获取任务监控数据的请求方式
- 移除 hutool-json 提升数据转换效率


### 文档
- 添加 Dinky 集成到 Datasophon 的文档
- 添加 SQL Cli 使用文档
- 优化文档页脚
- 增加角色绑定租户相关提示文档

### CI/CD
- 添加 github bug 模版的可选择版本项
- 移除部署文档时的国内仓库代理配置


