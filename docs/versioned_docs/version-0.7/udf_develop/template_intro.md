---
sidebar_position: 2
id: udf_template_intro
title: UDF Template 介绍
---

## 开发目的

1. 为了减少代码复制，实现快速开发

## 使用方式

模板引擎框架采用 [`Freemarker`](https://freemarker.apache.org/)

1. 在 `配置中心 -> udf模板设置` 可调节对应模板。Dinky官方目前提供这几个模板，后续会不断迭代以及追加
   ![template_intro.png](http://www.aiwenmo.com/dinky/docs/zh-CN/udf_develop/template_intro.png)
2. 在编辑模板中，java、scala 提供 package、className参数，由类名解析而来；如：`com.dinky.SubStringFunction`,
   对应解析成

```
${package}=com.dinky
${className}=SubStringFunction
```

![java_udf_template.png](http://www.aiwenmo.com/dinky/docs/zh-CN/udf_develop/java_udf_template.png)
![java_udf.png](http://www.aiwenmo.com/dinky/docs/zh-CN/udf_develop/java_udf.png)

3. python 这边较为特殊，目前只有2个参数，如：`SubStringFunction.f` ，对应为

```
${className}=SubStringFunction,
${attr}=f
```

![python_udf_template.png](http://www.aiwenmo.com/dinky/docs/zh-CN/udf_develop/python_udf_template.png)
![python_udf.png](http://www.aiwenmo.com/dinky/docs/zh-CN/udf_develop/python_udf.png)