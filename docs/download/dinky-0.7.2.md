---

sidebar_position: 87
title: 0.7.2 release
--------------------

| 版本    | 二进制程序                                                                                                                 | Source                                                                                |
|-------|-----------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------|
| 0.7.2 | [dlink-release-0.7.2.tar.gz](https://github.com/DataLinkDC/dlink/releases/download/v0.7.2/dlink-release-0.7.2.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dlink/archive/refs/tags/v0.7.2.zip) |

## Dinky发行说明

Dinky 0.7.2 是一个新功能和 bug 修复版本。

:::warning 注意
此版本有表结构变更 <br/>
需要按需执行 **sql/upgrade/0.7.2_schema/mysql/dinky_ddl.sql**
:::

### 新功能

- CDCSOURCE 整库同步支持写入 Catalog
- 支持通过 API 获取作业的字段血缘
- 新增 Flink 行级权限
- Yarn session 支持 applicationId 和 resource manager 配置
- Flink Jar 任务支持报警

### 修复

- 修复向海豚调度提交作业失败却返回成功的问题
- 修复数据开发的血缘接口异常时只返回空结果
- 修复使用 FlinkSQLEnv 的任务无法获取字段血缘
- 修复 API 未登录的异常
- 修复错误的 Python 数据类型
- 修复由于 jdbc 连接超时导致 CDCSOURCE 无法获取元数据
- 修复数据预览表格无法显示 Boolean 类型的数据
- Fix K8S native application 模式提交任务报错

### 优化

- 优化启动脚本和移除无用文件
- 添加作业连接中的状态和优化作业监控
- 优化配置项来控制 mysql 元数据中的 tinyint(1) 转换为 Boolean

### 贡献者

- @aiwenmo
- @liaotian1005

