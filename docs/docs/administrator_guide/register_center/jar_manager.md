---
position: 4
id: jar_manager
title: Jar 管理
---

当您使用 jar 包提交 Flink 应用时，可以在 **jar管理** 中对所需 jar 进行管理。并可以对已经注册的 Jar 包做编辑、删除等操作。

## Jar包配置

**注册中心 > jar管理 > 新建 > 创建Jar配置**。

![create_jar](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator_guide/register_center/jar_manager/create_jar.png)

![create_jar_config](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator_guide/register_center/jar_manager/create_jar_config.png)

**参数配置说明：**

- **Jar 配置：**
  - **默认：** User App
  - **文件路径：** 指定 HDFS 上的文件路径，即 Flink 提交的 jar 包
  - **启动类：** 指定可执行 Jar 的启动类
  - **执行参数：** 指定可执行 Jar 的启动类入参
- **基本配置：**
  - **标识：** 英文唯一标识(必选)
  - **名称：** 自定义
  - **注释：** 自定义
- **是否启用：** 默认启用

Jar 包配置完成后，创建 **FlinkJar** 任务，详见。

## 查询 Jar 包管理信息

您可以对所添加的可执行 Jar 包做编辑、删除等操作。

Jar 包管理信息相关字段含义如下：

|     字段     |                      说明                      |
| :----------: | :--------------------------------------------: |
|     名称     |                    名称唯一                    |
|     别名     |                     自定义                     |
|     类型     |                 默认 User App                  |
|   文件路径   | 指定 HDFS 上的文件路径，即 Flink 提交的 jar 包 |
|    启动类    |            指定可执行 Jar 的启动类             |
|   执行参数   |          指定可执行 Jar 的启动类入参           |
|   是否启用   |               已启用<br/> 已禁用               |
| 最近更新时间 |                Jar 包的修改时间                |
|     操作     |              对 Jar 包修改、删除               |

:::warning 注意事项

   目前 jar 包提交的方式只支持 Yarn Application 模式

:::