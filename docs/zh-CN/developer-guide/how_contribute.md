## 如何贡献

首先非常感谢大家选择和使用 Dinky & Apache Flink，真诚地欢迎大家加入 Dinky 社区与 Apache Flink 中文社区，融入开源世界，打造新一代实时计算框架与平台，共建共赢！

我们鼓励任何形式的参与社区，如：

- 将遇到的问题通过 github 上 [issue](https://github.com/DataLinkDC/dlink/issues) 的形式反馈出来
- 回答别人遇到的 [issue](https://github.com/DataLinkDC/dlink/issues) 问题
- 讨论新 Feature 的实现方案
- 帮助完善文档及官网
- 帮助项目增加测试用例
- 为代码添加注释
- 为代码添加国际化
- 提交修复 Bug 或者 Feature 的 PR
- 发表应用案例实践、扩展组件分享或者与 Flink SQL 相关的技术文章
- 帮助推广 Dinky，基于 Dinky 实践参与其他社区文章发表、技术大会或者 meetup 的分享等
- 提供或赞助云服务器资源搭建云 Dinky 学习环境

欢迎加入贡献的队伍，加入开源从提交第一个 PR 开始

- 比如添加代码注释或找到带有 ”easy to fix” 标记或一些非常简单的 issue（拼写错误等）等等，先通过第一个简单的 PR 熟悉提交流程

注：贡献不仅仅限于 PR，对促进项目发展的都是贡献

相信参与 Dinky，一定会让您从开源中受益匪浅！

## 参与文档贡献

首先要下载 [Dinky源码](https://github.com/DataLinkDC/dlink/tree/dev) ，下载完成后，需要安装nodejs和npm。详细安装步骤可以查阅本地调试。由于Dinky文档是有 [docsify](https://docsify.js.org/#/zh-cn/quickstart) 维护。因此需要在dinky根目录执行如下命令，在本地方可启动：

```shell
# 推荐全局安装 docsify-cli 工具
npm i docsify-cli -g
# 运行 docsify serve 启动一个本地服务器
docsify serve docs
```

可以方便地实时预览效果。默认访问地址 [http://localhost:3000](http://localhost:3000/)

## 参与代码贡献

调试请查阅 [本地调试](/zh-CN/developer-guide/local_debug.md) 或 [远程调试](/zh-CN/developer-guide/remote_debug.md)

贡献参考[参与贡献 Issue 需知]()，[参与贡献 Pull Request 需知]()，[参与贡献 CommitMessage 需知]()

## 如何领取 Issue，提交 Pull Request

如果你想实现某个 Feature 或者修复某个 Bug。请参考以下内容：

- 所有的 Bug 与新 Feature 建议使用 Issues Page 进行管理。
- 如果想要开发实现某个 Feature 功能，请先回复该功能所关联的 Issue，表明你当前正在这个 Issue 上工作。 并在回复的时候为自己设置一个最后期限，并添加到回复内容中。
- 你应该新建一个分支来开始你的工作，分支的名字参考[参与贡献 Pull Request 需知]()。比如，你想完成 feature 功能并提交了 Issue demo，那么你的 branch 名字应为 feature-demo。 功能名称可与导师讨论后确定。
- 完成后，发送一个 Pull Request 到 Dinky 的 dev 分支，提交过程具体请参考下面《[提交代码流程]()》。

如果是想提交 Pull Request 完成某一个 Feature 或者修复某个 Bug，这里都建议大家从小处做起，完成一个小功能就提交一次，每次别改动太多文件，改动文件太多也会给 Reviewer 造成很大的心理压力，建议通过多次 Pull Request 的方式完成。

注意：本文档参考了《 [DolphinScheduler Contribute](https://dolphinscheduler.apache.org/zh-cn/community/development/contribute.html) 》，非常感谢 DolphinScheduler 社区的支持。
