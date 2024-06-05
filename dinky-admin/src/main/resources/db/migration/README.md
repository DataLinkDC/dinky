
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



**升级脚本注意事项:**
- 如果你需要对某一个表添加字段,请不要使用`alter table add column`语句,使用如下语句:
    - MySQL: `CALL add_column_if_not_exists('tableName', 'columnName', 'dataType', 'defaultValue', 'comment');`
        - eg: `CALL add_column_if_not_exists('user', 'age', 'int', '0', 'age');`
    - PostgresSQL: `SELECT add_column_if_not_exists('model_name', 'table_name', 'column_name', 'data_type', 'default_value', 'comment');`
        - eg: `SELECT add_column_if_not_exists('public', 'user', 'age', 'int', '0', 'age');`


**其他注意事项:**
- 在你贡献代码时,如若涉及到了变更表结构,请添加回滚脚本,虽然 FlyWay 会有事务回滚操作,回滚脚本不会被 FlyWay 自动自行,但是为了本地调试测试时能方便进行回滚,所以添加回滚脚本
- 由于数据库类型不同,可能存在差异,请根据实际需求进行迭代增加脚本内容
- H2 数据库脚本需要按照规范进行正常的版本迭代(方便版本管理),但是 H2 数据库脚本不需要添加回滚脚本,因为 H2 数据库是内存数据库(默认程序启动时配置为内存模式,未持久化),每次启动都会重新创建,所以不需要回滚脚本

--- 

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


**Upgrade script considerations:**
- If you need to add fields to a table, do not use the 'alter table add column' statement. Instead, use the following statement:
    - MySQL: `CALL add_column_if_not_exists('tableName', 'columnName', 'dataType', 'defaultValue', 'comment');`
        - eg: `CALL add_column_if_not_exists('user', 'age', 'int', '0', 'age');`
    - PostgresSQL: `SELECT add_column_if_not_exists('model_name', 'table_name', 'column_name', 'data_type', 'default_value', 'comment');`
        - eg: `SELECT add_column_if_not_exists('public', 'user', 'age', 'int', '0', 'age');`


**Other precautions:**
- When you contribute code, if it involves changing the table structure, please add a rollback script. Although FlyWay may have transaction rollback operations, the rollback script will not be automatically rolled back by FlyWay. However, in order to facilitate rollback during local debugging and testing, add a rollback script
- Due to different database types, there may be differences. Please iterate and add script content according to actual needs
- The H2 database script needs to perform normal version iteration according to the specifications (for easy version management), but the H2 database script does not need to add a rollback script because the H2 database is an in memory database (configured in memory mode by default when the program starts, not persistent), and will be recreated every time it starts, so there is no need to add a rollback script