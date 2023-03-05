---

sidebar_position: 88
title: 0.7.1 release
--------------------

| 版本    | 二进制程序                                                                                                                 | Source                                                                                |
|-------|-----------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------|
| 0.7.1 | [dlink-release-0.7.1.tar.gz](https://github.com/DataLinkDC/dlink/releases/download/v0.7.1/dlink-release-0.7.1.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dlink/archive/refs/tags/v0.7.1.zip) |

## Dinky发行说明

Dinky 0.7.1 是一个 bug 修复版本。

### 修复

- 修复 tinyint(1) 类型转换错误
- 修复数据表缺少列 tenant_id
- 修复 i18n 国际化
- 修复 UDF 不忽略大小写
- 修复数据源 Schema 名包含中划线导致的问题
- 修复作业提交成功却报错失败
- 修复 yarn application 模式提交作业报空指针异常
- 修复查询数据时的空指针异常
- 修复新增 K8S 集群配置发生空指针异常
- 修复 postgres 数据源 bigint 类型转换错误

### 优化

- 优化数据源注册时校验不结果不影响保存
- 优化清空控制台及新增控制台右键清空
- 升级 org.apache.httpcomponents:httpclient 4.4.1 到 4.5.13

### 贡献者

- @aiwenmo
- @leechor
- @zackyoungh
- @zhoumengyks
- @zhu-mingye

