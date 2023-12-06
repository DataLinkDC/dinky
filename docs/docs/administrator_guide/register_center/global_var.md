---
position: 5
id: global_var
title: 全局变量
---


:::info 简介

为了方便/统一/可复用/安全的使用变量，我们提供了全局变量的功能

全局变量可以在 `数据开发` 中使用 , 并且支持关键词触发提示, 在开发过程中, 输入全局变量名称 ,即可触发关键词提示, 选择后即可自动补全

另外: Dinky 提供了多种全局变量使用/声明方式, 请根据自己的需求选择 ,具体请看下面的介绍

:::



:::warning 注意

如需使用全局变量相关功能，需要在`数据开发`的`作业配置`中开启全局变量。

:::



## 方式 1: 通过 `注册中心` -> `全局变量` 中定义全局变量

> 可以在 `注册中心` -> `全局变量` 中定义/查看/修改/删除全局变量




## 方式 2: 通过 `数据开发` 中定义全局变量

```sql map=sql
# 格式
key:= value;

# 示例
var1:=student;
select * from ${var1};

# 查看所有变量 (该查询会包含所有声明的变量,包括注册中心中定义的全局变量)
SHOW FRAGMENTS;
# 查看单个变量
SHOW FRAGMENT var1;
```

## 方式 3: 表达式变量

> 由于部分场景需要使用到类似于 `当前时间戳` `当前日期` 等变量, 为了方便使用, Dinky 提供了表达式变量的功能.


:::warning 注意

在 `1.0.0` 之前, Dinky 提供了类似 `${_CURRENT_DATE_}` `${_CURRENT_TIMESTAMP_}` 的变量

但是此种方式比较受限,自由度不高,基于此 Dinky 重构了此功能,改为提供`表达式变量`功能,并且不与之前的 `${_CURRENT_DATE_}` `${_CURRENT_TIMESTAMP_}` 变量兼容, 请知悉!

如您升级到 `1.0.0` 及以上版本, 请修改引用 `${_CURRENT_DATE_}` `${_CURRENT_TIMESTAMP_}` 变量的相关作业

新的表达式变量无需声明, 已由程序启动时自动加载初始化 ,直接使用即可
:::

### 日期时间类表达式变量

> 日期相关: `date` 为实例名用来调用方法`(必须固定)`,使用 [DateUtil](https://doc.hutool.cn/pages/DateUtil/)工具类实现,且支持此工具类的所有方法调用


```sql
-- 获取当前秒
select '${date.currentSeconds()}';

# 获取日期 减去 10 天
select '${date.offsetDay(date.date(), -10)}';

# 获取当前日期 标准格式 yyyy-MM-dd HH:mm:ss
select '${date.now()}';

# etc .....

```




### 随机串相关表达式变量

> `random` 为实例名用来调用方法`(必须固定)`, 使用 [RandomUtil](https://doc.hutool.cn/pages/RandomUtil/)工具类实现,且支持此工具类的所有方法调用

```sql
# 产生一个[10, 100)的随机数
select '${random.randomInt(10, 100)}';

# 随机字符串(只包含数字和字符）
select '${random.randomString()}';

# 获得一个只包含数字的字符串
select '${random.randomNumbers()}';

```

### 唯一 ID 相关表达式变量

> id 为实例名用来调用方法`(必须固定)` , 使用 [IdUtil](https://doc.hutool.cn/pages/IdUtil/) 工具类实现

```sql
# 生成的UUID是带-的字符串，类似于：a5c8a5e8-df2b-4706-bea4-08d0939410e3
select '${id.randomUUID()}';

# 生成的是不带-的字符串，类似于：b17f24ff026d40949c85a24f4f375d42
select '${id.simpleUUID()}';
```

:::tip 扩展

如您需要扩展更多表达式变量, 请参考 [Dinky 表达式变量扩展](../../extend/function_expansion/global_var_ext)

:::