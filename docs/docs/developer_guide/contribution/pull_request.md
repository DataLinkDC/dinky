---
sidebar_position: 4
id: pull_request
title: Pull Request 须知
---
Pull Request 本质上是一种软件的合作方式，是将涉及不同功能的代码，纳入主干的一种流程。这个过程中，可以进行讨论、审核和修改代码。

在 Pull Request 中尽量不讨论代码的实现方案，代码及其逻辑的大体实现方案应该尽量在 Issue 或者邮件列表中被讨论确定，在 Pull Request 中我们尽量只关注代码的格式以及代码规范等信息，从而避免实现方式的意见不同而导致 waste time。

### Pull Request 标题

标题格式：[`Pull Request 类型`-`Issue 号`][`模块名`] `Pull Request 描述`

例如：`[Fix-3333][admin] Fix global variables are not valid`

其中`Pull Request 类型`和`Issue 类型`的对应关系如下：


| Issue 类型  | Pull Request 类型                  | 样例（假设 Issue 号为 3333）                                |
| ------------- | ----------------------- |-----------------------------------------------------|
| Feature     | Feature               | [Feature-3333][admin] Implement xxx                 |
| Bug         | Fix                   | [Fix-3333][web] Fix xxx                             |
| Optimization | Optimization           | [Optimization-3333][alert] Optimize xxx             |
| Improve | Improve           | [Improve-3333][core] Improve xxx                    |
| Test        | Test                  | [Test-3333][metadata-mysql] Add the e2e test of xxx |
| Document        | Document                  | [Document-3333][cdc] Add the document of xxx        |

其中 `Issue 号`是指当前 Pull Request 对应要解决的 Issue 号，`模块名`同 Issue 的模块名。

### Pull Request 分支名

分支名格式：`Pull Request 类型`-`Issue 号`，举例：Feature-1。

### Pull Request 内容

请参阅到 commit message 篇。

### Pull Request Code Style

当你向 Dinky 提交 pull request 的时候 code-style 是你不得不考虑的问题。我们在 CI 中使用 Spotless 来保持代码风格的统一，它是一种帮助开发者编写遵循编码规范的 Java 代码格式化工具。如果你的 pull request 没有通过 Spotless 的检测，那它将不会被合并到主库中。你可以在提交 pull request 前使用 Spotless 来检测或者格式化你的代码。

1. 在提交 pull request 前格式化你的代码：执行 `mvn spotless:apply -P flink-all` 自动格式化。格式化代码的最佳时间是将你的修改提交到本地 git 版本库之前

2. 在提交 pull request 前编译整个项目：在 IDEA 中的 Profiles 中选择 aliyun,flink-all,nexus,prod,scala-2.12,web，重载项目后执行 `mvn clean install -P aliyun,prod,scala-2.12,web,flink-all`，编译成功后即可提交代码

### 相关问题

- 怎样处理一个 Pull Request 对应多个 Issue 的场景。

  首先 Pull Request 和 Issue 一对多的场景是比较少的。Pull Request 和 Issue 一对多的根本原因就是出现了多个
  Issue 需要做大体相同的一件事情的场景，通常针对这种场景就是把多个功能相同的 Issue 合并到同一个 Issue 上，然后把其他的
  Issue 进行关闭。

  尽量把一个 Pull Request 作为最小粒度。如果一个 Pull Request 只做一件事，Contributor 容易完成，Pull Request 影响的范围也会更加清晰，对 reviewer 的压力也会小。
