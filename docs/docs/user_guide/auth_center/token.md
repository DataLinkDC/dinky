---
position: 6
sidebar_position: 6
id: token
title: 令牌
---

:::info 简介
为了实现 OpenApi 的安全访问，Dinky 提供了令牌管理功能，可以实现对 Dinky OpenApi 的访问控制。并提供细粒度的权限控制，可以对用户、角色、租户进行授权。
:::

### 新建
点击新建按钮，点击生成token，选择用户、角色、租户，选择过期类型，完成令牌的新建。

> 注意: 选择时必须依次选择 会触发级联查询操作, 选择用户后, 角色会自动刷新, 选择角色后, 租户会自动刷新。

**认证中心 > 令牌 > 新建**

![token_add](http://pic.dinky.org.cn/dinky/docs/test/token_add.png)

如需要对令牌进行修改，可点击编辑按钮进行操作。

