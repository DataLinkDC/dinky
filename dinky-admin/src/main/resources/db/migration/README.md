
# Chinese

## 前置要求

- 数据库版本: MySQL 5.7+
- 必须有 mysql/postgresql 连接驱动


## 数据库脚本规则
- V 开头的代表发布版本; 规则: `V{版本号}__{描述}.sql`
- R 开头的代表回滚版本; 规则: `R{版本号}__{描述}.sql`

## 命名规则

- V{版本号}__{描述}.sql eg: V1.0.2__release.sql
- R{版本号}__{描述}.sql eg: R1.0.2__release.sql

**注意:** 
- V{版本号}__{描述}.sql 中间是**两个下划线**,固定规则,不符合规则将无法执行
- 每个版本只能有一个 V{版本号}__{描述}.sql 文件,否则将无法执行, 不管是 DDL 还是 DML 统一放在一个文件中

# English

## Pre requirements

- Database version: MySQL 5.7+
- Must have MySQL/postgreSQL connection driver

## Database Script Rules
- V represents the released version; Rule: `V{Version Number}__{Description}.SQL`
- R represents the rolled back version; Rule: `R{Version Number}__{Description}.SQL`

## Naming rules
- V{version number}__{description}.sql eg: V1.0.2__release.sql
- R{version number}__{description}.sql eg: R1.0.2__release.sql

**Attention:**
- V{version number}__{description}.SQL has two underscores in the middle, which are fixed rules. If they do not comply with the rules, they cannot be executed
- Each version can only have one V{version number}__{description}.sql file, otherwise it will not be executed, whether it is DDL or DML, it will be placed in one file