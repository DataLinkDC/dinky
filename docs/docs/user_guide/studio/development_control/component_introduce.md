---
sidebar_position: 2
id: component_introduce
title: 方言介绍
---



目前支持的方言有：
 - FlinkSql
 - FlinkJar
 - FlinkSqlEnv
 - Mysql
 - PostGreSql
 - Oracle
 - SqlServer
 - Phoenix
 - Hive
 - Doris(StarRocks)
 - ClickHouse
 - Java 

## FlinkSql

支持 Apache Flink sql-client 的绝大多数 FlinkSQL 语法，其中部分语法被优化，详见。

## FlinkJar

支持用户自定义的 Flink Jar 任务的配置，详见。

## FlinkSqlEnv

支持将 FlinkSQL 封装为执行环境，供 FlinkSQL 任务使用，详见。在执行 FlinkSQL 时，会先执行 FlinkSqlEnv 内的语句。

## DB sql

支持对应数据源的原生 sql 方言，详见。

## Java

支持书写 Java 的 UDF 等，并自动加载至 Local 模式。（当前存在 Bug，请不要使用）


 

 