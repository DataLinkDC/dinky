---
sidebar_position: 105
position: 105
id: auto_sync
title: 自动同步主仓库
---



:::info 简介
  自动同步主仓库 (upstream) 的代码到你的仓库 (origin)，保持你的仓库代码与主仓库代码同步。
:::

## 步骤

1. Fork 主仓库到你的 GitHub 账号下.
2. 点击 Fork 之后的仓库, 点击 `Actions` 标签.
3. 会提示一些介绍, 点击 `I understand my workflows, go ahead and enable them` 按钮.
4. 完成之后,会在每天的 00:00:00 执行同步操作. 后续该 workflow 文件不改变的情况下,会一直按照定时配置自动执行同步操作.
