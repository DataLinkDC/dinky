---
position: 6
sidebar_position: 6
id: git_project
title: Git 项目
---

:::info 简介
Dinky 在 v1.0.0 版本开始,提供了 Git 项目管理的功能，可以在 Dinky 中管理 Git 项目，方便的进行項目编译打包操作,并提供了全构建流程的实时化日志查看功能.
:::

## 支持功能列表
1. 可以托管 Jar任务的项目, 并将打包产物自动推送至 [`资源中心`](resource) 以便数据开发中使用 jar 任务提交方式
2. 可以托管 UDF 项目, 并将打包产物自动推送至 [`资源中心`](resource) 以便数据开发中使用该 UDF
3. 支持自动解析 UDF 项目打包后的 UDF function
4. 支持查看 clone 后的代码
5. 支持查看全流程构建日志
6. 支持配置 GitHub 项目/GitLab 项目
7. 支持配置 Maven 私服仓库,配置详见: [Maven 配置](../system_setting/global_settings/maven_setting)
8. 支持 Python/Java 项目
9. 支持 https/ssh 协议 clone 方式

## 列表
![git_project](http://pic.dinky.org.cn/dinky/docs/test/git_project_list.png)

## 构建

![git_build](http://pic.dinky.org.cn/dinky/docs/test/git.png)


**注意:** 构建完成后,会将构建产物推送至 [`资源中心`](resource_center) ,进行集中管理


## 查看代码

![git_show_code](http://pic.dinky.org.cn/dinky/docs/test/git_show_code.png)

**注意:** 目前仅支持查看 clone 后的代码,不支持编辑代码