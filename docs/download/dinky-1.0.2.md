---
sidebar_position: 81
title: 1.0.2 release
---

| Dinky 版本 | Flink 版本 | 二进制程序                                                                                                                           | Source                                                                                |
|----------|----------|---------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------|
| 1.0.2    | 1.14     | [dinky-release-1.14-1.0.2.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.0.2/dinky-release-1.14-1.0.2.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.0.2.zip) |
| 1.0.2    | 1.15     | [dinky-release-1.15-1.0.2.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.0.2/dinky-release-1.15-1.0.2.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.0.2.zip) |
| 1.0.2    | 1.16     | [dinky-release-1.16-1.0.2.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.0.2/dinky-release-1.16-1.0.2.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.0.2.zip) |
| 1.0.2    | 1.17     | [dinky-release-1.17-1.0.2.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.0.2/dinky-release-1.17-1.0.2.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.0.2.zip) |
| 1.0.2    | 1.18     | [dinky-release-1.18-1.0.2.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.0.2/dinky-release-1.18-1.0.2.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.0.2.zip) |
| 1.0.2    | 1.19     | [dinky-release-1.19-1.0.2.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.0.2/dinky-release-1.18-1.0.2.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.0.2.zip) |

## Dinky-1.0.2 发行说明

### 升级说明

:::warning 提示
- 1.0.2 是一个 BUG 修复版本,有表结构/数据变更,请执行 DINKY_HOME/sql/upgrade/1.0.2_schema/数据源类型/dinky_dml.sql
- 关于 SCALA 版本: 发版使用 Scala-2.12 , 如你的环境必须使用 Scala-2.11, 请自行编译,请参考 [编译部署](../docs/next/deploy_guide/compile_deploy) , 将 profile 的 scala-2.12 改为 scala-2.11
:::


### 新功能
- 适配 KubernetsApplicationOperator 模式下的多种 Rest SvcType 并修改 JobId 获取判断逻辑
- 新增 SSE 的心跳机制
- 新增自动检索最新高可用JobManager地址的功能(目前实现了 Yarn; K8s暂未实现)
- 新增数据开发中控制台清空日志功能
- 支持 Flink1.19
- 增加推送到 Apache DolphinScheduler 时的任务组相关配置
- 新增由用户指定提交YarnApplication 任务的用户
- 启动脚本增加 GC相关启动参数,并支持配置 DINKY_HOME 环境变量
- 实现集群配置中的 FlinkSQL 配置项支持 RS 协议(仅 Yarn 模式)


### 修复
- 修复全局变量在 YarnApplication 模式下未识别的问题,并重构YarnApplication提交方式
- 修复数据源心跳检测反馈错误问题
- 修复前端路由跳转可能存在 404 的问题
- 修复全局变量不存在时的错误提示不正确问题
- 修复前端数据开发中编辑器中光标移动闪烁的问题
- 修复 DockerfileDinkyFlink 的 docker 文件中路径错误问题
- 修复配置 Python 选项无法识别的问题
- 修复角色用户列表空指针异常
- 修复一些 K8s任务提交时的问题
- 修复整库同步时 Oracle 的Time 类型转换问题
- 修复 k8s pod 模板无法正确解析问题
- 修复 SPI 加载 CodeGeneratorImpl 失败的问题
- 修复了 UNSIGNED / ZEROFILL 关键字声明的数字列会导致解析不匹配的问题
- 修复批任务完成后状态仍是未知的问题
- 修复一些未经登录认证即可访问的不安全接口
- 修复Pre-Job 模式下状态未知的问题
- 修复Jid重复导致检索多个作业实例的问题
- 修复用户列表无法使用worknum搜索的问题
- 修复查询数据时结果Tag页右侧无法正确渲染查询数据按钮的问题
- 修复 print table 语法存在的问题
- 修复资源列表新增或者修改后无法刷新问题
- 修复数据开发的控制台滚动更新任务状态不正确的问题
- 修复打包偶发失败问题
- 修复 Git 项目构建时存在的问题


### 优化
- 优化启停脚本
- 优化全局配置页面部分溢出的问题
- 优化 UDF 管理的提示
- 优化运维中心列表页用户体验,支持按照时间排序
- 优化 Git 项目中默认数据的仓库地址
- 优化 Flink jar 任务提交支持批任务
- 优化右键菜单溢出可视区时无法点击的问题
- 优化运维中心列表组件主键 key
- 当修改任务时,由可修改模版优化为不可修改模版
- 优化集群配置的展示方式及其类型
- 优化K8s模式下删除集群的逻辑
- 修复Application模式下集群不自动释放的问题
- 移除使用 Paimon 进行数据源缓存的逻辑,改为默认内存缓存,可配置为 redis 缓存
- 移除切换任务时自动弹出Console
- 优化资源管理的渲染逻辑,未开启资源时无法使用资源管理功能
- 优化登录状态的检测逻辑
- 优化登录页面反馈提示
- 删除一些前端的无用代码
- 优化整库同步多次构建算子图时,算子图顺序不一致导致无法从 savepoint 恢复的问题
- 优化资源配置的提示文案
- 优化并完善资源中心复制功能,支持目前 Dinky 内的所有引用场景

### 安全
- 排除一些高危 jmx 暴露的 endpoints

### 文档
- 优化表达式变量扩展文档
- 优化整库同步部分实践文档
- 添加 JDBC 关于 tinyint 类型相关的 FAQ
- 新增文档官网首页的轮播图
- 修复文档全局配置中资源配置的描述问题
- 增加全局配置中环境配置相关文档
- 删除全局配置中Flink 配置的一些配置项
- 增加告警类型为 email 的文档配置描述



