---
sidebar_position: 3
id: issue
title: Issue 须知
---
Issues 功能被用来追踪各种特性，Bug，功能等。项目维护者可以通过 Issues 来组织需要完成的任务。

Issue 是引出一个 Feature 或 Bug 等的重要步骤，在单个 Issue 中可以讨论的内容包括但不限于 Feature 的包含的功能，存在的 Bug 产生原因，前期方案的调研，以及其对应的实现设计和代码思路。

并且只有当 Issue 被 approve 之后才需要有对应的 Pull Request 去实现。

如果是一个 Issue 对应的是一个大 Feature，建议先将其按照功能模块等维度分成多个小的 Issue。

### Issue 标题

标题格式：[`Issue 类型`][`模块名`] `Issue 描述`

其中`Issue 类型`如下：


| Issue 类型  | 描述                                                                                     | 样例                                               |
| ------------- | ------------------------------------------------------------------------------------------ | ---------------------------------------------------- |
| Feature     | 包含所期望的`新功能`和`新特性   `                                                        | [Feature][admin] Add xxx admin in xxx controller   |
| Bug         | 程序中存在的`Bug`                                                                        | [Bug][admin] Throw exception when xxx              |
| Improvement | 针对目前程序的一些`改进`，不限于`代码格式`，`程序性能`等                                 | [Improvement][executor] Improve xxx on executor    |
| Test        | 专门针对`测试用例`部分                                                                   | [Test][metadata-mysql] Add xxx metadata-mysql test |
| Sub-Task    | 一般都是属于`Feature` 类的子任务，针对大 Feature，可以将其分成很多个小的子任务来一一完成 | [Sub-Task][executor] Implement xxx in xxx          |

其中`模块名`如下：


| 模块名     | 描述                 |
| ------------ | ---------------------- |
| admin      | 管理模块             |
| alert      | 报警模块             |
| app        | Flink APP 模块       |
| assembly   | 打包模块             |
| client     | Flink Client 模块    |
| common     | 基础模块             |
| connectors | Flink Connector 模块 |
| core       | 核心模块             |
| doc        | 资源模块             |
| docs       | 官网文档             |
| executor   | 执行器模块           |
| extends    | 扩展模块             |
| function   | Flink UDF 模块       |
| gateway    | 提交网关模块         |
| metadata   | 元数据模块           |
| web        | Web 模块             |

### Issue 内容模板

[Issue 模板](https://github.com/DataLinkDC/dlink/tree/dev/.github/ISSUE_TEMPLATE)

### Bug 类 Issue

当您发现一个 Bug 时，请提交一个 Issue 类的 Bug，提交前：

* 请先在 issue 列表里查找一下是否该 Bug 已经提交，如果已经有此 Bug，请在此 Bug 下接着回复。
* 如果该 Bug 是可以复现的。请尽量提供完整的重现步骤。

请在 Issues 页面中提交 Bug。

一个高质量的 Bug 通常有以下特征：

* 使用一个清晰并有描述性的标题来定义 Bug。
* 详细的描述复现 Bug 的步骤。包括您的配置情况，预计产生的结果，实际产生的结果。并附加详细的 TRACE 日志。
* 如果程序抛出异常，请附加完整的堆栈日志。
* 如有可能，请附上屏幕截图或动态的 GIF 图，这些图片能帮助演示整个问题的产生过程。
* 哪个版本。
* 需要修复的优先级(危急、重大、次要、细微)。

下面是 **Bug 的 Markdown 内容模板**，请按照该模板填写 issue。

```shell
**标题** 
标题格式: [BUG][Priority] bug标题
Priority分为四级: Critical、Major、Minor、Trivial

**问题描述**
[清晰准确描述遇到的问题]

**问题复现步骤:**
1. [第一步]
2. [第二步]
3. [...]

**期望的表现:**
[在这里描述期望的表现]

**观察到的表现:**
[在这里描述观察到的表现]

**屏幕截图和动态GIF图**
![复现步骤的屏幕截图和动态GIF图](图片的url)

**Dinky 版本:(以0.6.0为例)** 
 -[0.6.0]
 
**补充的内容:**
[请描述补充的内容，比如]

**需求或者建议**
[请描述你的需求或者建议]
```

### Feature 类 Issue

提交前：

* 请确定这不是一个重复的功能增强建议。 查看 Issue Page 列表，搜索您要提交的功能增强建议是否已经被提交过。

请在 issues 页面中提交 Feature。

一个高质量的 Feature 通常有以下特征：

* 一个清晰的标题来定义 Feature
* 详细描述 Feature 的行为模式
* 说明为什么该 Feature 对大多数用户是有用的。新功能应该具有广泛的适用性。
* 尽量列出其他平台已经具备的类似功能。商用与开源软件均可。

以下是 **Feature 的 Markdown 内容模板**，请按照该模板填写 issue 内容。

```shell
**标题** 
标题格式: [Feature][Priority] feature标题
Priority分为四级: Critical、Major、Minor、Trivial

**Feature的描述**
[描述新Feature应实现的功能]

**为什么这个新功能是对大多数用户有用的**
[解释这个功能为什么对大多数用户是有用的]

**补充的内容**
[列出其他的调度是否包含该功能，是如何实现的]

```

### Contributor

除一些特殊情况之外，在开始完成 Issue 之前，建议先在 Issue 下或者邮件列表中和大家讨论确定设计方案或者提供设计方案，以及代码实现思路。

如果存在多种不同的方案，建议通过邮件列表或者在 Issue 下进行投票决定，最终方案和代码实现思路被 approve 之后，再去实现，这样做的主要目的是避免在
Pull Request review 阶段针对实现思路的意见不同或需要重构而导致 waste time。

### 相关问题

- 当出现提出 Issue 的用户不清楚该 Issue 对应的模块时的处理方式。

  确实存在大多数提出 Issue 用户不清楚这个 Issue 是属于哪个模块的，其实这在很多开源社区都是很常见的。在这种情况下，其实 committer/contributor 是知道这个 Issue 影响的模块的，如果之后这个 Issue 被 committer 和 contributor approve 确实有价值，那么 committer 就可以按照 Issue 涉及到的具体的模块去修改 Issue 标题，或者留言给提出 Issue 的用户去修改成对应的标题。
