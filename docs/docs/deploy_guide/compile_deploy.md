---
sidebar_position: 3
position: 3
id: compile_deploy
title: 编译部署
---

:::tip 提示
1. 如果你只想部署 Dinky，可以跳过本章节，直接查看[常规部署](./normal_deploy)章节

2. 如果你想对 Dinky 做二次开发，参考[本地调试](../developer_guide/local_debug)章节,从而搭建开发环境。开发完成之后，再参考本章节，进行编译。

3. 自 Dinky v1.0.0 版本开始，Dinky 在打包时仅支持单个版本的打包方式,即: 你只能选择一个 Flink 版本进行打包,而不能同时打包多个版本。
:::


## 环境准备

环境要求请参考 [本地调试-环境要求](../developer_guide/local_debug#环境要求) 章节! 各个环境安装请自行搜索


---


:::warning 注意
当你看到这里的时候，说明你已经完成了所有的环境准备，接下来就是编译 Dinky 了。

接下来将从 IDEA 和 Linux Shell 两个方面介绍如何编译 Dinky。请注意环境要求是一致的,否则可能会出现编译失败的情况。
:::

### IDEA 编译

> 推荐使用 IDEA 进行编译，因为 IDEA 在打开项目时会自动下载依赖，而且编译速度快，方便调试.

#### Clone 项目
> 注意: 本次直接 clone 的是 Dinky 主仓库，如果你想要二次开发/基于自己的仓库进行二次开发，请先 fork 项目，然后再 clone 你自己的仓库

![compile_clone_1](http://pic.dinky.org.cn/dinky/docs/zh-CN/complie/complie_clone_1.png)

Clone 完成后, 等待 IDEA 自动下载依赖(前提 IDEA 已经正确配置了 Maven) ,下载完成后, 请按照下一步中的步骤(Profile)进行编译


#### 编译打包


:::danger 注意
请注意: 有关于各个 Profile 的详细介绍,请查看 [Profile 说明](../developer_guide/local_debug#Profile-说明)
:::


- 打包 Profile

> 注意: 需要勾选 web,否则无法打包前端资源

![locadenug_single_package_profile](http://pic.dinky.org.cn/dinky/docs/zh-CN/developer_guide/local_debug/locadenug_single_package_profile.png)




:::danger 注意
1. 请严格按照上述 Profile 进行编译打包，否则可能会出现编译失败的情况。如出现编译失败，请检查环境是否正确，Profile 是否正确。请务必仔细核对。
2. 如果出现编译失败，请查看 IDEA 控制台输出的错误信息，根据错误信息自行进行排查。如果无法解决，请百度/谷歌/Stackoverflow 解决。最后如果还是无法解决，请提交 [Issue](https://github.com/DataLinkDC/dinky/issues/new/choose)
3. 编译完成后，请查看编译后的目录，如果编译成功，会在 `dinky/build` 目录下生成对应的版本的 jar 包。
:::

#### 编译结果

编译完成后，请查看编译后的目录，如果编译成功，会在 `dinky/build` 目录下生成对应的版本的 tar.gz 包。


以上就是 IDEA 编译 Dinky 的详细步骤，接下来将介绍如何使用 Linux Shell 进行编译。

---

### Linux Shell 编译

> 注意: 本次直接 clone 的是 Dinky 主仓库，如果你想要二次开发/基于自己的仓库进行二次开发，请先 fork 项目，然后再 clone 你自己的仓库

#### Clone 项目

```bash
# 创建某一路径用来存放 Dinky 源码
mkdir -p /opt/dinky-source-code
cd /opt/dinky-source-code
# Clone 项目
git clone https://github.com/DataLinkDC/dinky.git
```

#### 编译打包

> 如果你看到此处,那么默认你是对 Maven 有一定了解并熟练使用的,如果你不了解 Maven/不会使用 Maven,请自行百度/谷歌/Stackoverflow...解决

```bash
cd /opt/dinky-source-code

# 编译打包 Profile, 注意 scala 支持 2.11 和 2.12, 请根据实际情况进行选择,jdk 支持 8/11,请根据实际情况进行选择,不选jdk11默认使用系统内的jdk8
mvn clean package -DskipTests=true -Pprod,jdk11,flink-single-version,scala-2.12,aliyun,flink-1.16,web

```

:::danger 注意
请注意: 有关于各个 Profile 的详细介绍,请查看 [Profile 说明](../developer_guide/local_debug#Profile-说明)
:::

:::danger 注意
1. 请严格按照上述 Profile 进行编译打包，否则可能会出现编译失败的情况。如出现编译失败，请检查环境是否正确，Profile 是否正确。请务必仔细核对。
2. 如果出现编译失败，请查看 IDEA 控制台输出的错误信息，根据错误信息自行进行排查。如果无法解决，请百度/谷歌/Stackoverflow 解决。最后如果还是无法解决，请提交 [Issue](https://github.com/DataLinkDC/dinky/issues)
:::

#### 编译结果

编译完成后，请查看编译后的目录，如果编译成功，会在 `dinky/build` 目录下生成对应的版本的 tar.gz 包。

以上就是 Linux Shell 编译 Dinky 的详细步骤，

---

:::info
上述两种方式，任选其一即可。如果已经正常编译成功，接下来将你可以按照 [部署文档](./normal_deploy) 进行部署了。
:::







