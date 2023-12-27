---
position: 5
sidebar_position: 5
id: tenant
title: 租户
---


:::info 简介

租户模块提供了租户的新增、编辑及用户的分配功能。

系统初始化时,默认创建了一个超级管理员租户,租户编码为: `DefaultTenant`, 租户名称为: `DefaultTenant`, 该租户拥有系统所有权限, 无法删除。

此功能建议由系统管理员操作,不建议将此功能开放给普通用户.
:::

### 新建
点击新建按钮，输入租户编码、备注等相关信息，完成租户的新建。

**认证中心 > 租户 > 新建**

![tenant_add](http://pic.dinky.org.cn/dinky/docs/test/tenant_add.png)

### 分配用户
点击分配用户按钮，选择需要分配的用户，完成用户的分配。

**认证中心 > 租户 > 新建**

![tenant_allocation](http://pic.dinky.org.cn/dinky/docs/test/tenant_allocation.png)

:::tip 提示
1. 分配完成后，需要用户重新登录才能生效。
2. 点击`租户编码`可以查看租户下的用户。
::