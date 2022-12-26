---
sidebar_position: 3
id: add_jar_statement
title: ADD JAR
---

ADD JAR 语句用于将用户 jar 添加到 classpath。可作用与 `session` 和 `application` 模式 。
```sql
ADD JAR '<path_to_filename>.jar'
```
使用语法和 `sql-client` 的案例一致

## 实战范围
当连接器和第三方依赖过多时，经常容易导致 jar依赖冲突；

add jar可以选择性的识别添加到服务器，做到环境隔离
