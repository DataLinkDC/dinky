---
sidebar_position: 1
id: create_statements
title:  CREATE 语句
---

CREATE 语句用于将表/视图/函数注册到当前或指定的 Catalog 中。注册的表/视图/函数可用于 SQL 查询。

FlinkSQL 支持如下CREATE语句

- CREATE CATALOG
- CREATE DATABASE
- CREATE TABLE
- CREATE VIEW
- CREATE FUNCTION

## CREATE CATALOG

使用给定的属性创建 Catalog。如果具有相同名称的目录已经存在，则会引发异常。当前 Dinky 支持的 Catalog 详见数据开发中的 [Catalog 管理](../../administrator_guide/studio/catalog_manage)。

### 语法结构

```sql
CREATE CATALOG catalog_name
  WITH (key1=val1, key2=val2, ...)
```

### With 参数说明

WITH 参数通常用于指定 Catalog 所需参数，语法为`'key1'='value1', 'key2' = 'value2'`的键值对。

### 示例

Hive Catalog 示例

```sql
CREATE CATALOG ods_catalog WITH (
    'type' = 'hive',
    'default-database' = 'default',
    'hive-version' = '2.1.1',
    'hive-conf-dir' = '/etc/hive/conf',
    'hadoop-conf-dir' = '/etc/hadoop/conf'
);
```

## CREATE DATABASE

创建数据库。如果已存在具有相同名称的数据库，则会引发异常

### 语法结构

```sql
CREATE DATABASE [IF NOT EXISTS] [catalog_name.]db_name
  [COMMENT database_comment]
```

### 示例

```sql
CREATE DATABASE IF NOT EXISTS ods_catalog.test
```

## CREATE TABLE

CREATE TABLE 语句用来描述数据源（Source）或者数据目的（Sink）表，并将其定义为一张表，以供后续语句引用。

### 语法结构

```sql
CREATE TABLE [IF NOT EXISTS] [catalog_name.][db_name.]table_name
 (
   { <列定义> | <计算列定义> }[ , ...n]
   [ <Watermark 定义> ]
   [ <表约束定义, 例如 Primary Key 等> ][ , ...n]
 )
 [COMMENT 表的注释]
 [PARTITIONED BY (分区列名1, 分区列名2, ...)]
 WITH (键1=值1, 键2=值2, ...)
 [ LIKE 其他的某个表 [( <LIKE 子句选项> )] ]
```

### 字句说明

CREATE TABLE 语句创建的表，既可以作为数据源表，也可以作为目标表。但是如果没有对应的 Connector，则会在运行时报错。

```sql
<列定义>:
  column_name column_type [ <column_constraint> ] [COMMENT column_comment]
<列的约束定义>:
  [CONSTRAINT constraint_name] PRIMARY KEY NOT ENFORCED
<表的约束定义>:
  [CONSTRAINT constraint_name] PRIMARY KEY (column_name, ...) NOT ENFORCED
<元数据列列定义:虚拟列>:
  column_name column_type METADATA [ FROM metadata_key ] [ VIRTUAL ]
<计算列定义>:
  column_name AS computed_column_expression [COMMENT column_comment]
<Watermark 定义>:
  WATERMARK FOR rowtime_column_name AS watermark_strategy_expression
<表名称定义>:
  [catalog_name.][db_name.]table_name
<like选项>:
{
   { INCLUDING | EXCLUDING } { ALL | CONSTRAINTS | PARTITIONS }
 | { INCLUDING | EXCLUDING | OVERWRITING } { GENERATED | OPTIONS | WATERMARKS } 
}[, ...]
```

**计算列**

计算列是一种虚拟列，它是逻辑上的定义而非数据源中实际存在的列，通常由同一个表的其他列、常量、变量、函数等计算而来。例如，如果数据源中定义了 price和 quantity，那么就可以新定义一个 cost字段，即 `cost AS price * quantity`，即可在后续查询中直接使用 cost 字段

:::tip 注意事项

计算列只允许在 CREATE TABLE 语句中使用

:::

**Watermark 定义**

Watermark 决定着 Flink 作业的时间模式，定义方式：

```sql
WATERMARK FOR 某个Rowtime类型的列名 AS 某个Watermark策略表达式
```

**示例**

```sql
CREATE TABLE student (
    `user` BIGINT,
    product STRING,
    registerTime TIMESTAMP(3), 
    WATERMARK FOR registerTime AS registerTime - INTERVAL '5' SECOND
) WITH ( . . . );
```

**主键 PRIMARY KEY**

主键约束既可以与列定义(列约束)一起声明，也可以作为单行(表约束)声明。对于这两种情况，它都应该只声明为单例。如果同时定义多个主键约束，则会引发异常。

**PARTITIONED BY**

根据指定的列对创建的表进行分区。如果这个表用作文件系统接收器，则为每个分区创建一个目录。

**WITH 参数**

WITH 参数通常用于指定数据源和数据目的的 Connector 所需参数，语法为`'key1'='value1', 'key2' = 'value2'`的键值对。

对于常见的 FlinkSQL Connector 的具体的使用方法，详见 [FlinkSQL Connector](../../connector/flinksql_connectors)

## CREATE VIEW

用户可以使用 CREATE VIEW 语句创建视图。视图是一个虚拟表，基于某条 SELECT 语句。视图可以用在定义新的虚拟数据源（类型转换、列变换和虚拟列等）。

查询表达式创建视图。如果目录中已存在具有相同名称的视图，则会引发异常。

### 语法结构

```sql
CREATE [TEMPORARY] VIEW [IF NOT EXISTS] [catalog_name.][db_name.]view_name
AS SELECT 语句
```



## CREATE FUNCTION

对于 SQL 作业，用户可以上传**自定义程序包**，然后在作业开发中使用。

### 语法结构

目前FlinkSQL 支持 Java、 Scala 和 Python 三种语言编写的程序包。当用户上传了自定义程序包后，在界面上即可用下面的 CREATE FUNCTION 语句来声明：

```sql
CREATE TEMPORARY SYSTEM FUNCTION 函数名
  AS '函数类全名' [LANGUAGE JAVA|SCALA|Python]
```

### 示例

```sql
CREATE FUNCTION parserJsonArray  AS 'qhc.com.flink.UDTF.ParserJsonArray' language JAVA;
```

