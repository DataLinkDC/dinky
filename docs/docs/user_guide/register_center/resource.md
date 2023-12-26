---
position: 8
sidebar_position: 8
id: resource
title: 资源中心
---

:::info 简介
Dinky 在 v1.0.0 版本开始,提供了资源中心的功能，可以在 Dinky 中很方便的进行资源的管理,包括上传资源,删除资源(逻辑删除),预览资源等

需要注意的是,资源中心中的资源,并不是指 Dinky 服务的资源,而是指 Dinky 服务所管理的资源,比如上传的文件,或者是 Git 项目的产物等,都可以在资源中心中进行管理

在 Dinky v1.0.0 版本中,扩展了 rs 协议,可以通过 rs 协议访问资源中心中的资源,方便的进行资源的访问.
:::


![resource_overview](http://pic.dinky.org.cn/dinky/docs/zh-CN/user_guide/register_center/resource/resource_overview.png)

## 支持功能
1. 支持托管 Git 构建任务的产物,包含 Jar Zip 等
2. 支持上传资源
3. 支持预览部分文件类型内容,如: 配置文件,文本文件等
4. 支持 rs 协议,不管原本的文件系统是什么,只要是由`资源中心`管理的,都可以通过 rs 协议访问
5. 支持 Jar 任务提交时,通过 rs 协议访问资源中心中的 Jar 资源,并适用于 Flink 全模式任务的提交



:::warning 注意

1. 该功能依赖于资源配置, 请确保已经配置了文件系统,配置详见: [文件系统配置](../system_setting/global_settings/resource_setting) ,请在配置完成后,再使用该功能,否则会出现异常
2. 请注意,虽然提供了删除功能,为了避免误操作,在 Dinky 中删除资源并不会删除文件系统中的文件,仅会删除 Dinky 的数据库中的记录, 如需彻底删除, 请自行手动删除文件系统中的文件
3. 如果上传 同名文件,会覆盖原有文件,请谨慎操作
4. 资源中心默认有一层逻辑根节点 Root, 该节点目录不可删除, 所有上传文件/新建目录,都归于该节点下, 如果使用 rs 协议访问, 请注意路径,不要带上 Root 节点

:::



## 使用方式

> eg: 假设在 `配置中心` -> `Resource 配置` 中使用 `LOCAL` 协议 ,并设置了 `上传根路径`为 /tmp/dinky/resource
> 那么可以通过以下方式访问资源中心中的资源, 在使用过程中,可以直接忽视 `上传根路径` 的配置.

```bash
# 假设基于以上配置, 在资源中心 Root 根目录下上传了一个 app.jar 文件,那么可以通过以下方式访问
rs:/app.jar

# 假设基于以上配置, 在资源中心 Root 根目录 新建了一个目录 test,并在 test 目录下上传了一个 app.jar 文件,那么可以通过以下方式访问
rs:/test/app.jar

```
