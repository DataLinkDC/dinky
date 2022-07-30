---
sidebar_position: 6
id: commit_code
title: 提交代码须知
---



# 提交代码

* 首先从远端仓库 *https://github.com/DataLinkDC/dlink.git*  fork 一份代码到自己的仓库中

* 远端仓库中目前有三个分支：

    * master 正常交付分支
      发布稳定版本以后，将稳定版本分支的代码合并到 master 上。

    * dev 日常开发分支
      日常 dev 开发分支，新提交的代码都可以 pull request 到这个分支上。

    * 0.6.0 发布版本分支
      发布版本分支，后续会有 1.0... 等版本分支。

* 把自己仓库 clone 到本地

  ```sh
  git clone https://github.com/DataLinkDC/dlink.git
  ```

* 添加远端仓库地址，命名为 upstream

  ```sh
  git remote add upstream https://github.com/DataLinkDC/dlink.git
  ```



* 查看仓库

  ```sh
  git remote -v
  ```

> 此时会有两个仓库：origin（自己的仓库）和 upstream（远端仓库）

* 获取/更新远端仓库代码（已经是最新代码，就跳过）

  ```sh
  git fetch upstream
  ```


* 同步远端仓库代码到本地仓库

  ```sh
   git checkout origin/dev
   git merge --no-ff upstream/dev
  ```

如果远端分支有新加的分支比如`dev-1.0`,需要同步这个分支到本地仓库

  ```sh
  git checkout -b dev-1.0 upstream/dev-1.0
  git push --set-upstream origin dev-1.0
  ```

* 新建分支

  ```sh
  git checkout -b xxx origin/dev
  ```

确保分支 `xxx` 是基于官方 dev 分支的最新代码


* 在新建的分支上本地修改代码以后，提交到自己仓库：

  ```sh
  git commit -m 'commit content'
  git push origin xxx --set-upstream
  ```

* 将修改提交到远端仓库

    * 在 github 的 Pull Request 页面，点击 "New pull request"

    * 选择修改完的本地分支和要合并的目的分支，点击 "Create pull request"

* 接着社区 Committer 们会做 CodeReview，然后他会与您讨论一些细节（包括设计，实现，性能等）。当团队中所有人员对本次修改满意后，会将提交合并到 dev 分支

* 最后，恭喜您已经成为了 Dinky 的官方贡献者！