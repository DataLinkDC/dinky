系统设置中的用户管理功能，包含添加或删除用户、修改密码等。此用户管理中的用户仅限于登录Dinky界面。

**默认用户名/密码:** admin/admin

## 添加用户

当用户使用非admin用户登录时，可以添加一个新用户，通过新用户登录Dinky。

首先要进入<span style="">系统设置</span>，选择<span>用户管理</span>，新建。

![用户新建](http://www.aiwenmo.com/dinky/dev/docs/%E7%94%A8%E6%88%B7%E6%96%B0%E5%BB%BA.png)

进入后，会出现用户创建界面

![](http://www.aiwenmo.com/dinky/dev/docs/%E5%88%9B%E5%BB%BA%E7%94%A8%E6%88%B7.png)

**参数配置：**

- **用户名:** 自定义;
- **昵称:** 自定义;
- **工号:** 可定义一个唯一值;
- **手机号:** 用户使用的手机号;
- **是否启用:** 默认禁用，需要开启;

## 删除用户

但用户不使用某个创建的普通用户时，可以进行删除。首先进入<span style="">系统设置</span>，选择<span>用户管理</span>，点击对应用户的删除即可。

![用户删除](http://www.aiwenmo.com/dinky/dev/docs/%E7%94%A8%E6%88%B7%E5%88%A0%E9%99%A4.png)

![用户删除1](http://www.aiwenmo.com/dinky/dev/docs/%E7%94%A8%E6%88%B7%E5%88%A0%E9%99%A41.png)



**说明:** admin用户在dinky是最高权限用户，无法删除，只能修改密码

## 用户配置及编辑

用户配置或者编辑是为了用户修改用户的基本信息，首先进入<span style="">系统设置</span>，选择<span>用户管理</span>，点击对应用户的配置或者编辑。接下来就可以维护用户信息。

![用户编辑及配置](http://www.aiwenmo.com/dinky/dev/docs/%E7%94%A8%E6%88%B7%E7%BC%96%E8%BE%91%E5%8F%8A%E9%85%8D%E7%BD%AE.png)

![维护用户](http://www.aiwenmo.com/dinky/dev/docs/%E7%BB%B4%E6%8A%A4%E7%94%A8%E6%88%B7.png)



## 密码修改

普通用户创建好后，默认密码是123456，admin用户密码是admin。为避免信息泄露，在生产环境建议用户修改密码。

首先进入<span style="">系统设置</span>，选择<span>用户管理</span>，点击对应用户的密码修改即可。

![image-20220314235010502](http://www.aiwenmo.com/dinky/dev/docs/image-20220314235010502.png)

![修改密码1](http://www.aiwenmo.com/dinky/dev/docs/%E4%BF%AE%E6%94%B9%E5%AF%86%E7%A0%811.png)



当密码修改成功后，用户可以选择界面右上角的退出登录,既可通过新密码登录Dinky。

![image-20220314224207098](http://www.aiwenmo.com/dinky/dev/docs/image-20220314224207098.png)



![image-20220314224454102](http://www.aiwenmo.com/dinky/dev/docs/image-20220314224454102.png)







