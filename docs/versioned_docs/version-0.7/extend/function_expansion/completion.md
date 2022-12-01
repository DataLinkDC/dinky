---
sidebar_position: 5
id: completion
title: FlinkSQL 编辑器自动补全函数
---




## FlinkSQL 编辑器自动补全函数

Dlink-0.3.2 版本上线了一个非常实用的功能——自动补全。

我们在使用 IDEA 等工具时，提示方法并补全、生成的功能大大提升了开发效率。而 Dlink 的目标便是让 FlinkSQL 更加丝滑，所以其提供了自定义的自动补全功能。对比传统的使用 `Java` 字符串来编写 FlinkSQL 的方式，Dlink 的优势是巨大。

在文档中心，我们可以根据自己的需要扩展相应的自动补全规则，如 `UDF`、`Connector With` 等 FlinkSQL 片段，甚至可以扩展任意可以想象到内容，如注释、模板、配置、算法等。

具体新增规则的示例请看下文描述。

## set 语法来设置执行环境参数

对于一个 FlinkSQL 的任务来说，除了 sql 口径，其任务配置也十分重要。所以 Dlink-0.3.2 版本中提供了 `sql-client` 的 `set` 语法，可以通过 `set` 关键字来指定任务的执行配置（如 “ `set table.exec.resource.default-parallelism=2;` ” ），其优先级要高于 Dlink 自身的任务配置（面板右侧）。

那么长的参数一般人谁记得住？等等，别忘了 Dlink 的新功能自动补全~

配置实现输入 `parallelism` 子字符串来自动补全 `table.exec.resource.default-parallelism=` 。

在文档中心中添加一个规则，名称为 `parallelism`，填充值为 `table.exec.resource.default-parallelism=`，其他内容随意。

保存之后，来到编辑器输入 `par` .

选中要补全的规则后，编辑器中自动补全了 `table.exec.resource.default-parallelism=` 。

至此，有些小伙伴发现，是不是可以直接定义 `pl2` 来自动生成 `set table.exec.resource.default-parallelism=2;` ？

当然可以的。

还有小伙伴问，可不可以定义 `pl` 生成 `set table.exec.resource.default-parallelism=;` 后，光标自动定位到 `=` 于 `;` 之间？

这个也可以的，只需要定义 `pl` 填充值为 `set table.exec.resource.default-parallelism=${1:};` ，即可实现。

所以说，只要能想象到的都可以定义，这样的 Dlink 你爱了吗？

嘘，还有点小 bug 后续修复呢。如果有什么建议及问题请及时指出哦。