---
sidebar_position: 7
position: 7
id: flinkcdc_pipeline
title: EXECUTE PIPELINE
---

:::info 背景

该功能是为了在 Dinky 中直接提交 FlinkCDC 3.0 的 Pipeline 任务，并支持所有运行模式及 Dinky 的相关特性。
可以理解为把 FlinkCDC Pipeline 任务转换为 Operations，并可延用 Dinky 的能力。

:::

:::warning 注意事项

1. 需要添加 Flink CDC 3.0 Pipeline 相关依赖才可使用。
2. 内容使用了 yaml 语句，强烈要求注意缩进。
3. 如果使用 `Checkpoint` 或 `Savepoint` ,请在右边作业，选择 `Savepoint策略`，其次检查点 跳过 请使用 execution.savepoint.ignore-unclaimed-state: true 参数控制

:::

## 语法结构

```sql
EXECUTE PIPELINE WITHYAML (
<flinkcdc_pipeline_yaml>
)
```

## Demo:

```sql
EXECUTE PIPELINE WITHYAML (
source:
  type: mysql
  hostname: localhost
  port: 3306
  username: root
  password: 123456
  tables: app_db.\.*
  server-id: 5400-5404
  server-time-zone: UTC

sink:
  type: doris
  fenodes: 127.0.0.1:8030
  username: root
  password: ""
  table.create.properties.light_schema_change: true
  table.create.properties.replication_num: 1

pipeline:
  name: Sync MySQL Database to Doris
  parallelism: 2
)
```
