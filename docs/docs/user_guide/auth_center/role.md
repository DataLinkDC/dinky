---
position: 2
sidebar_position: 2
id: role
title: 角色
---

:::info 简介
角色模块提供了角色的新增、编辑及角色菜单的分配等功能。

系统初始化时,默认创建了一个超级管理员角色,角色编码为: `SuperAdmin`, 角色名称为: `SuperAdmin`, 该角色拥有系统所有权限, 无法删除。

此功能建议由系统管理员操作,不建议将此功能开放给普通用户.

注意:
角色是和租户绑定的，新建租户和用户之后，需要用`超级管理员切换到新租户`，创建新角色并分配菜单权限，并给新用户分配角色, 新用户才能正常使用。
目前分配完成菜单权限后, 需要该角色下的用户重新登录, 才能生效。
:::



### 新建
点击新建按钮，输入角色编码、角色名称等相关信息，完成角色的新建。

**认证中心 > 角色 > 新建**

![role_add](http://pic.dinky.org.cn/dinky/docs/test/role_add.png)

### 编辑
点击编辑按钮，进行角色信息的修改操作。

**认证中心 > 角色 > 编辑**

![role_edit](http://pic.dinky.org.cn/dinky/docs/test/role_edit.png)

### 分配菜单
点击分配菜单按钮，可以对角色的菜单权限进行分配。

**认证中心 > 角色 > 分配角色**

![role_distribute](http://pic.dinky.org.cn/dinky/docs/test/role_distribute.png)

:::tip 提示
1. 分配完成后，需要用户重新登录才能生效。
2. 点击`角色编码`可以查看角色下的用户。
:::