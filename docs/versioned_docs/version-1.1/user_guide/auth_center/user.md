---
position: 1
sidebar_position: 1
id: user
title: 用户
---

:::info 简介

用户模块提供了用户的新增、删除、编辑、角色分配、密码修改及重置等功能。

系统初始化 sql 时 会自动创建 admin 用户.

此功能建议由系统管理员操作,不建议将此功能开放给普通用户.
:::

:::warning 注意事项

**系统初始化默认用户名/密码:** admin/dinky123!@#

admin 用户在 Dinky 是最高权限用户，无法删除

自 Dinky 1.0.0 版本开始，admin 用户的默认密码是 dinky123!@#

:::


### 新建

点击新建按钮，输入用户名等相关信息，完成用户的新建。

> **注意：** 新建用户的默认密码为: 123456 , 为避免信息泄露，**在生产环境建议用户修改密码**。

**认证中心 > 用户 > 新建**

![user_add](http://pic.dinky.org.cn/dinky/docs/test/user_add.png)

### 编辑

点击编辑按钮，进行用户信息的修改操作。

**认证中心 > 用户 > 编辑**

![user_edit](http://pic.dinky.org.cn/dinky/docs/test/user_edit.png)

### 分配角色

点击分配角色按钮，进行用户的角色分配操作。

**认证中心 > 用户 > 分配角色**

![role_assignment](http://pic.dinky.org.cn/dinky/docs/test/role_assignment.png)

### 修改密码

点击修改密码按钮，进行用户密码的修改操作。

- 普通用户创建好后，**默认密码**是 **123456**，
- **admin 用户**密码是 **dinky123!@#** 。为避免信息泄露，**在生产环境建议用户修改密码**。

**认证中心 > 用户 > 修改密码**

![password_modify](http://pic.dinky.org.cn/dinky/docs/test/password_modify.png)

当密码修改成功后，用户可以选择界面**右上角**的**退出登录**,既可通过新密码登录 Dinky。

### 用户删除

点击用户删除按钮，进行用户的删除操作。

**认证中心 > 用户 > 删除**

![user_delete](http://pic.dinky.org.cn/dinky/docs/test/user_delete.png)

### 密码重置

点击密码重置按钮，进行密码的重置操作。重置后的密码为随机密码，页面会有弹框消息提示，请注意查看。

**认证中心 > 用户 > 重置**

![password_reset](http://pic.dinky.org.cn/dinky/docs/test/password_reset.png)

用户管理相关字段含义如下：

|  字段   |               说明                |
|:-----:|:-------------------------------:|
|  用户名  |               自定义               |
|  昵称   |               自定义               |
|  工号   |               唯一值               |
|  手机号  |            用户使用的手机号             |
| 注册类型  | 有两种类型，本地注册用户为LOCAL, LDAP用户为LDAP |
| 是否删除  |            用户状态是否删除             |
| 超级管理员 |       admin默认是，其他自定义用户为否        |
| 是否启用  |          已启用<br/> 已禁用           |
|  操作   |       对用户信息做编辑、删除、修改密码操作        |

