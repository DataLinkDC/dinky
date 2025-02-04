---
sidebar_position: 78
title: 1.2.0 release
---

| Dinky 版本 | Flink 版本 | 二进制程序                                                                                                                                   | Source                                                                                |
|----------|----------|-----------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------|
| 1.2.0    | 1.14     | [dinky-release-1.14-1.2.0.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.2.0/dinky-release-1.14-1.2.0.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.2.0.zip) |
| 1.2.0    | 1.15     | [dinky-release-1.15-1.2.0.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.2.0/dinky-release-1.15-1.2.0.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.2.0.zip) |
| 1.2.0    | 1.16     | [dinky-release-1.16-1.2.0.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.2.0/dinky-release-1.16-1.2.0.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.2.0.zip) |
| 1.2.0    | 1.17     | [dinky-release-1.17-1.2.0.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.2.0/dinky-release-1.17-1.2.0.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.2.0.zip) |
| 1.2.0    | 1.18     | [dinky-release-1.18-1.2.0.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.2.0/dinky-release-1.18-1.2.0.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.2.0.zip) |
| 1.2.0    | 1.19     | [dinky-release-1.19-1.2.0.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.2.0/dinky-release-1.19-1.2.0.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.2.0.zip) |
| 1.2.0    | 1.20     | [dinky-release-1.20-1.2.0.tar.gz](https://github.com/DataLinkDC/dinky/releases/download/v1.2.0/dinky-release-1.20-1.2.0.tar.gz) | [Source code (zip)](https://github.com/DataLinkDC/dinky/archive/refs/tags/v1.2.0.zip) |

## Dinky-1.2.0 发行说明

### 升级说明

:::warning 重要
v1.2.0 替换所有 Dinky 相关 jar 后直接重启可自动升级。
:::

### 新功能
- 添加 npm 配置
- 添加错误模板版本号
- 添加内置的 Flink History Server 以减少未知状态，使 Flink 任务的最终信息更准确
- 添加了对 Flink 1.20 的支持，并更新了对其他 Flink 版本的依赖
- 支持作业导出
- 添加全局 token
- 支持物理删除资源
- 支持 paimon hdfs hive 数据源
- 使用 ingress 地址获取作业信息
- Flink SQL 任务支持插入结果预览
- 添加首次初始化页面
- FlinkSQL 数据开发支持实时更新作业状态
- Flink jar 添加表单
- 提供初始化工具
- 支持 pgsql 的 flink catalog
- 添加 E2E Test

### 修复
- 修复执行show语句的错误
- 修复 Json 序列化和反序列
- 修复 Flink cli 的问题
- 修复 "all ip port is not available" 的问题
- 修复 Git 项目表单中的启用按钮没有默认值的问题
- 修复 git 项目模块中 saveOrUpdate 方法的问题
- 修复保存点（SavePoint）路径逻辑，并调整 Flink 配置获取的配置方法
- 解决当打开过多作业标签时出现 “超出存储配额” 的问题
- 修复 git 构建过程中的问题
- 修复字段血缘不支持全局变量的问题
- 修复代码编辑器中的缩略图显示
- 修复 PG sql 自动初始化问题
- 修复 Oracle 字段类型转换错误
- 修复许多 Local 模式下的 Flink 问题
- 修复 flyway 不支持 mysql 5.7 的问题
- 修复 pg 查询中的异常数据
- 修复在优化工作台上点击一项任务时因无实例导致的异常
- 修复执行失败
- 修复查询 Oracle 主键列问题
- 修复程序启动
- 修复任务树无法排序的问题
- 修复 Git 项目页面无限刷新的问题
- 修复 dinky 配置钉钉告警时出现空指针异常
- 修复 minor 的问题
- 修复获取血缘信息时数组越界的问题
- 修复由于升级 Druid 版本导致的 SQL 注入错误
- 修复目录显示字段的错误
- 修复在 yarn 应用程序中执行 jar 提交
- 修复警报中出现的空指针异常
- 修复表名中有中线导致任务无法执行的问题
- 修复配置 key 的错误
- 修复作业告警 dinky 地址 url
- 修复菜单 mapper
- 修复查询模型中作业 ID 为空的异常
- 修复与 Kerberos 相关的错误修复、SQL SET 值不生效等问题
- 修复在查询模式下不保存作业实例的问题
- 修复 websocket 的问题
- 修复 web 打包
- 修复与 Flink 1.20 配合使用的 Dinky 后端 CI 工作流
- 修复主键生成策略的问题
- 修复模拟语句时对象未找到的问题
- 修复数据开发底部状态
- 修复文档模块中依赖不完整的问题
- 关闭数据开发页面的悬浮按钮
- 修复数据开发页面并启用系统配置
- 修复 k8s 表单 ingresss 的问题
- 修复欢迎页面上的路由重定向错误
- 修复 Flink session 作业提交失败的问题
- 修复 web npe
- 修复 web 清空的问题
- 修复了在资源中心使用复制按钮时的一个错误
- 修复创建同名子目录的新任务时出现的问题
- 修复在 Kubernetes 模式下运行时对任务名称的限制
- 修复 k8s 测试的问题
- 修复数据开发并引入 LESS 导致全局 CSS 样式混乱
- 修复数据开发，Flink jar 任务工具栏显示
- 修复 pg 的问题
- 修复 DolphinScheduler 调用 Dinky 任务和并发执行异常
- 修复在开启 Kerberos 认证后提交 Flink 任务时，Yarn webui 无法获取任务状态的问题
- 修复重命名作业时提交的作业名称保持不变的问题
- 修复告警序列化问题
- 修复登录问题
- 修复 Flink Jar 任务的提交
- 修复自动化脚本的路径问题
- 修复 Git code 构建失败
- 修复 yarn 并行提交
- 修复在 PG 表上执行查询语句时的空指针异常
- 修复 FlinkJar 无法使用全局变量的问题

### 优化
- 优化版本更新逻辑以解决因升级导致的缓存问题
- 优化工作台界面
- 重构指标请求
- 重构获取 user.dir 的方法
- SSE 切换到全局 WebSocket，Web 容器从 Tomcat 切换到 Undertow
- 添加 Schemas and getTables api
- 删除 dinky_cluster index
- 优化 mapper 查询
- 优化类属性类型问题
- 删除用户自定义函数（UDF）注册管理页面上的提示消息
- 优化一些网页布局，使其在小屏幕上显示时更具用户友好性
- 优化数据源详情列表的虚拟滚动问题
- 优化登录页面
- 优化文档 Action
- 升级文档依赖
- 改进获取表的结构信息
- 优化集群配置并为手动注册启动会话集群
- 优化配置中心中配置项的介绍和布局
- 优化角色权限的提示
- 添加获取血缘的加载效果
- 实现统一的 JSON（Jackson）序列化
- 添加提示：角色和租户是绑定的
- 修改并升级 SQL 文件版本号
- 优化运维中心中 Flink 操作符图的显示
- 优化 dinky flink web UI
- Oracle 中的 TIMESTAMP 列类型顺序被更改，使其排在 TIME 列之前
- 优化任务列表布局
- 优化许多代码
- 添加重复导入任务
- 通过 -XX:MaxRAMPercentage 限制 JVM 使用的容器内存的最大百分比
- 优化 K8S 日志打印
- 优化 flink application 模式状态刷新
- 重构一个新的数据开发页面以提升用户体验
- 去除作业名称中对下划线的限制
- 更改令牌密钥名称
- 在构建 FlinkSQL 时去除引号
- 升级 FlinkCDC 的版本到 3.2
- 重构获取版本号的方法
- 添加标签页右键方法
- 优化新 UI
- 优化调试作业来预览数据
- 优化 FlinkDDL 执行顺序
- 移除旧版本的数据开发页面并修复一些小细节
- 统一使用 '/' 作为文件分隔符
- 添加 package-lock.json
- 优化检查语句并添加测试
- 移动 DataStudioNew 为 DataStudio
- 重构结果查询
- 添加 websocket PING PONG
- 添加底部最近修改时间
- 优化 IDE 的风格
- 移除旧的血缘分析
- 优化数据开发的主题
- 优化 CDCSOURCE 并支持输出到 print 与 mock
- 优化下线按钮图标
- 优化数据开发的图标
- 改进打印表数据的显示方法
- 优化正在运行的任务的状态并美化用户界面
- 优化构建角色菜单的逻辑
- 优化没有 Flink 依赖的 Dinky，无法启动的问题
- 改进了资源中心上传文件时缺失异常消息的情况
- 优化提交任务打印错误日志
- 点击任务选项卡同步切换到对应的服务
- 重新提交任务时删除之前失败的集群
- 优化 flink jar 表单下拉选择
- 优化 app 打包大小
- 优化登录界面的响应式布局
- 变量提示优化
- 优化了 App 的包大小和 rs 协议
- 数据开发添加资源管理页面
- 添加部署状态监测
- 优化一些脚本
- 添加默认的 jobmanager.memory.process.size 参数
- 优化调度器请求错误断言方法
- 重构 udf 执行
- 优化血缘关系获取，添加保存点，优化 udf 类名显示
- 优化数据开发页面的 UI
- 修改 sqllite 数据存储位置
- 将中文注释更改为英文注释
- 添加欢迎页面自动宽度
- 添加推送作业到海豚调度

### 文档
- 添加 K8S 作业的提交文档
- 添加 Datasophon 集成
- 添加 Flink Cli 文档
- 更新文档的 ICP 备案信息
- 更新部署指南参考
- 修复部署文档
- 调试数据预览更新的文档
- 更新快速开始文档的图片
- 修改README.md和README_zh_CN.md关于如何部署中源码部署的错误链接。

