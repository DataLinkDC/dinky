---
position: 3
id: maven_setting
sidebar_position: 3
title: Maven 配置
---

:::info 简介
Dinky1.0增加了从git拉取项目编译的功能（比如udf项目），当用户使用 **注册中心** > *
*[Git项目](../../register_center/git_project)**，构建项目需要下载jar包依赖，所以需要在 **配置中心** > **全局配置** > *
*Maven 配置** 页面进行相关设置。
:::

![maven_setting](http://pic.dinky.org.cn/dinky/docs/test/maven_setting.png)

**参数配置说明:**

| 参数名称             | 参数说明                                                                                                               |
|:-----------------|:-------------------------------------------------------------------------------------------------------------------|
| **Maven 配置文件路径** | setting文件地址,如果配置了环境变量 `MAVEN_HOME`，则优先使用环境变量配置的路径,但是需要注意 setting 文件的名称是默认的 `settings.xml`,如果名称不对或者没有配置环境变量，可以在此处填写 |
| **Maven 仓库地址**   | 默认为阿里云仓库地址                                                                                                         |
| **Maven 仓库用户名**  | Maven 私服地址的用户名,如无则不用填                                                                                              |
| **Maven 仓库密码**   | Maven 私服地址的密码,如无则不用填                                                                                               |
