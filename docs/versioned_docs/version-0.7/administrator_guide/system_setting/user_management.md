---
position: 1
id: user_management
title: 用户管理
---




系统设置中的用户管理功能，包含添加或删除用户、修改密码等。此用户管理中的用户仅限于登录 Dinky 界面。

**默认用户名/密码:** admin/admin

## 添加用户

当用户使用非admin用户登录时，可以添加一个新用户，通过新用户登录Dinky。
- **普通用户**创建好后，**默认密码**是 **123456**，

进入 **系统设置 > 用户管理 > 新建**

![create_user](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator_guide/system_setting/user_management/create_user.png)


**参数配置：**

- **用户名:** 自定义;
- **昵称:** 自定义;
- **工号:** 可定义一个唯一值;
- **手机号:** 用户使用的手机号;
- **是否启用:** 默认禁用，需要开启;

## 密码修改

- 普通用户创建好后，**默认密码**是 **123456**，
- **admin 用户**密码是 **admin** 。为避免信息泄露，**在生产环境建议用户修改密码**。

首先进入 **系统设置**，选择 **用户管理**，点击对应用户的密码修改即可。

![update_passwd](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator_guide/system_setting/user_management/update_passwd.png)

![update_passwd_ok](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator_guide/system_setting/user_management/update_passwd_ok.png)

当密码修改成功后，用户可以选择界面**右上角**的**退出登录**,既可通过新密码登录 Dinky。



## 用户管理

当您添加用户后，可以对用户做修改、删除和修改密码等操作。

![user_manager](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator_guide/system_setting/user_management/user_manager.png)

用户管理相关字段含义如下：

|     字段     |                 说明                 |
| :----------: | :----------------------------------: |
|    用户名    |                自定义                |
|     昵称     |                自定义                |
|     工号     |                唯一值                |
|    手机号    |           用户使用的手机号           |
|   是否启用   |          已启用<br/> 已禁用          |
| 最近更新时间 |          用户信息的修改时间          |
|     操作     | 对用户信息做编辑、删除、修改密码操作 |

:::warning 注意事项

​    admin 用户在 Dinky 是最高权限用户，无法删除，只能修改密码

:::
