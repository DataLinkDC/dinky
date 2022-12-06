---
sidebar_position: 4
id: set_statement
title: SET 语句
---

SET 语句可以调整作业的关键运行参数。目前大多数参数都可以在 SQL 作业中进行配置。

## 语法

SET 语句中字符串类型的配置项和参数值可以不用半角单引号括起来或者必须用半角单引号括起来。

```sql
SET key = value;
#或者
SET `key` = `value`;
```

各个版本的参数配置详见Flink开源社区:

- [Flink1.11](https://nightlies.apache.org/flink/flink-docs-release-1.11/dev/table/config.html#overview)
- [Flink1.12](https://nightlies.apache.org/flink/flink-docs-release-1.12/dev/table/config.html#overview)
- [Flink1.13](https://nightlies.apache.org/flink/flink-docs-release-1.13/docs/dev/table/config/#overview)

- [Flink1.14](https://nightlies.apache.org/flink/flink-docs-release-1.14/docs/dev/table/config/#overview)
- [Flink1.15](https://nightlies.apache.org/flink/flink-docs-release-1.15/docs/dev/table/config/#overview)
:::warning 注意事项

   SET 语句行尾需加上分号

   SET 命令不支持注释，请不要在其后增加 `--` 注释信息

   SET 优先级> **作业配置** > **集群管理配置** > **Flink 配置文件**

:::
