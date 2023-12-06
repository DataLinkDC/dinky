---
sidebar_position: 9
id: global_var_ext
title: 表达式变量扩展
---

:::tip

本扩展文档适用于 v1.0.0 及以上版本

:::

## 介绍

> 本扩展用于扩展表达式中的变量，以满足更多场景需求

## 前置环境

> 扩展前需要启动 Dinky 项目, 本地开发环境搭建请参考 [本地调试](../../developer_guide/local_debug)

## 扩展方法

找到 `dinky-core` 模块下的 `org.dinky.executor.VariableManager.ENGINE_CONTEXT` 变量, 并在其中添加


举例:

> 假设需要扩展 [`Hash 算法`](https://doc.hutool.cn/pages/HashUtil/) 相关的表达式变量

```java

import cn.hutool.core.util.HashUtil;

public final class VariableManager {

    public static final Dict ENGINE_CONTEXT = Dict.create()
            .set("random", RandomUtil.class)
            .set("date", DateUtil.class)
            // 只需要在此处添加即可
            // 需要注意的是, 此处的 key 必须与表达式中的变量名一致,随后便可以使用该变量调用其方法
            .set("hash", HashUtil.class) 
            .set("id", IdUtil.class);
    
    
    //... 其他代码无需关心,因此省略...
}
```

:::tip 说明
如您扩展完成需要生产中使用, 参考[编译](../../deploy_guide/compiler) , 打包完成 替换服务器部署的 `dinky/lib/dinky-core-1.0.0.jar` 文件, 重启 dinky 服务即可

考虑到多数用户通用情况下,当然也欢迎您将此次扩展贡献给社区, 请参考 [如何贡献](../../developer_guide/contribution/how_contribute) 进行 [PR](../../developer_guide/contribution/pull_request)

:::