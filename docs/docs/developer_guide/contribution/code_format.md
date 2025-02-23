---
sidebar_position: 111
position: 111
id: code_format
title: 代码格式化
---

:::info 简介

如果你在 Dinky 基础上进行了二次开发/bugfix/实现了新功能,并打算贡献到社区,基于代码格式化要求,你必须执行格式化代码,满足规范后方可被合并.

但是在 Dinky 1.0.0 之后,格式化插件升级后要求 JDK 环境为 JDK 11,如果你的 JDK 环境为 JDK 8,那么则需要升级 JDK 环境/配置服务端格式化,否则无法使用格式化插件.你的 Pull Request 的代码检查结果将会失败,从而无法被合并

基于上述说明,下面介绍多种方式,如何执行格式化并满足格式化要求.
:::

## 升级 JDK 环境

> 此为单个 JDK 环境的升级

Dinky 1.0.0 之后,全面支持 JDK 11,如果你的 JDK 环境为 JDK 8,那么可以选择升级 JDK 环境,具体 JDK11 的安装方式自行搜索即可.

## JDK 环境并存

如果你需要有多个 JDK 环境,那么你可以通过以下方式来实现 JDK 环境并存:

1. 下载 JDK 11 并安装.不需要配置环境变量,只需要在 IDEA 中配置即可.前提是你所需的 JDK 主版本非 JDK11, 如果你想要设置 JDK 11 为默认,那么可以自行配置环境变量.
2. 在 IDEA 打开的 Dinky 工程中配置 JDK 11,并勾选 Maven Profile 中的 `jdk11`,并刷新 Maven Profile,使其重新加载. 
3. 找到 Maven => Dinky 根下的 插件 => 展开 spotless, 双击 spotless:apply ,等待格式化完成.

## 服务端格式化

如果你只需要单个 JDK 环境，并且本地的 JDK 环境版本为 JDK 8, 不想升级 JDK 环境,那么你可以通过以下方式来实现:

> 注意: 此操作需要在你提交本地代码到你的仓库前进行.

1. 登录你的 GitHub 账号,转到 https://github.com/settings/tokens
2. 点击下图路径, 创建 token,并指定名称为 `FORMAT`,请注意大小写,创建完成之后请牢记该 Token 值, 后续将会用到
![create_token](http://pic.dinky.org.cn/dinky/docs/zh-CN/developer_guide/contribution/code_format/create_token.png)
3. 打开你的 GitHub 中 Fork 的 Dinky 仓库 -> Settings -> Secrets and variables -> Actions
![secrets_url](http://pic.dinky.org.cn/dinky/docs/zh-CN/developer_guide/contribution/code_format/secrets_url.png)
4. 点击 Secrets Tag -> New repository secret -> 名称指定为 `TOKEN` , 粘贴你在第一步中创建的 token 值, 点击 Add secret
5. 点击 Variables -> New repository variable -> 名称指定为 `FORMAT` , 值为`true`, 点击 Add variable
![variables_url](http://pic.dinky.org.cn/dinky/docs/zh-CN/developer_guide/contribution/code_format/variables_url.png)

6. 基于此方式,可以一劳永逸,无需在 IDEA 中手动执行格式化,并无需安装 JDK 11 环境. 只需要正常修改/新增代码 -> 提交到你的 Dinky 仓库即可,服务端会自动执行格式化,并将结果反馈在 下图所示的 Actions 界面中.
![show_actions](http://pic.dinky.org.cn/dinky/docs/zh-CN/developer_guide/contribution/code_format/show_actions.png)
此方式使用 GITHUB 的 Actions 服务,每次提交代码都会触发 Actions 服务,并执行格式化,如果格式化失败,则会反馈失败信息,如果格式化成功,则会反馈成功信息.


## 前端格式化
> 由于在 Dinky 1.0.0 之后,前端代码也需要进行格式化,但是在 本地只能通过手动执行格式化的方式来实现,所以在此提供一种方案,进行前端代码格式化. 如果配置了服务端格式化,那么可以忽略此步骤.由 GITHUB Actions 服务来执行格式化.

步骤:
1. 进入 dinky-web 目录,执行 `pnpm install` 安装依赖
2. 执行 `pnpm run prettier` 进行格式化.


:::tip 提示

以上为几种如何实现格式化的方案, 请根据你的实际情况选择合适的方式.推荐使用 [服务端格式化](#服务端格式化) 的方式,因为它更加方便,而且不会影响你的本地环境.

如果你在提交没有进行代码格式化且没有配置服务端格式化, 那么你的 Pull Request 将 actions 失败,并提示你进行格式化.如果你的 JDK 环境为 JDK 8,那么你需要升级 JDK 环境或者配置服务端格式化,否则无法通过格式化检查.
如果 JDK 环境为 JDK 11,那么你可以在 IDEA 中配置 JDK 11,并勾选 Maven Profile 中的 `jdk11`,并刷新 Maven Profile,使其重新加载,然后再修改的类中再次回车触发修改,使该文件处于变更列表内,然后执行格式化即可.最后再进行提交.

:::