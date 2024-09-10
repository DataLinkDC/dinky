---
sidebar_position: 999
position: 999
id: cdcsource_faq
title: 整库同步 FAQ
---


## 常见问题

### 如何确认整库同步任务提交成功

查看 FlinkWeb 的 JobGraph 是否包含 Sink，如不包含则说明在构建 Sink 时出错，到 `配置中心-系统信息-Logs` 查看后台日志，寻找报错原因。

### 多并行度乱序如何解决

设置并行度为1；或者设置目标数据源的相关配置来实现最终一致性，如 Doris Sequence 列。

### 源库DDL变动怎么办

Savepoint Stop/Cancel 作业，然后从最近的 Savepoint/Checkpoint 恢复作业。如果变动过大导致任务无法从保存点正常恢复，在
CDCSOURCE 前添加 `set 'execution.savepoint.ignore-unclaimed-state' = 'true';`。

### 是否支持完整的模式演变

不支持，目前模式演变取决于 Sink 的数据源连接器能力，如 Doris 连接器支持字段级模式演变。

### No operators defined in streaming topology. Cannot execute.

jdbc 连接超时导致无法获取正确的元数据信息，可以重启 Dinky 或者升级到 0.7.2及以上版本。

### NoClassDefFoundError

排查依赖冲突或者缺少依赖，注意胖包的使用。

### 语法检查和血缘分析未正确显示

当前不支持，只支持作业提交。

### 源码位置

- 1.0.0 之前的版本在 dlink-client 模块下的 cdc 里；
- 1.0.0 之后的版本在 dlink-cdc 模块中

### 其他 cdc 和其他 sink 的支持

FlinkCDC 支持的能力都可以直接在 Dinky 上使用，可自行扩展支持；所有的 Flink SQL Connector 都可以在 CDCSOURCE
中直接使用，无需代码扩展，只需要在参数前追加 `sink.` 即可；其他特殊的 DataStream Connector 可自行扩展。

:::tip 说明

- 若有错误和疏漏请及时提出，如果有其他实践请补充，将同步更新至官网文档，多谢支持。
- 本文档仅供参考，具体以实际使用为准。如你在使用过程中整理了一些实践经验，欢迎在文档中补充。请参考 [如何参与文档贡献](../../developer_guide/contribution/document)。

:::