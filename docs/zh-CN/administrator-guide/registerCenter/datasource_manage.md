当用户使用Dinky做为数据开发工具时,用户首先需要进入<span style=''>注册中心数据源管理</span>,新建数据库连接即可。

![image-20220310221602049](http://www.aiwenmo.com/dinky/dev/docs/image-20220310221602049.png)

**名称:** 输入英文唯一标识;

**别名：** 自定义;

**分组类型:** 包括来源,数仓,应用,备份,其他;

**url:** 数据库连接地址,如 jdbc: mysql://127.0.0.1:3306/dlink;

**用户名:** 连接数据库的用户名;

**密码:** 连接数据库的密码;

**Flink连接配置:** 避免私密信息泄露，同时复用连接配置，在FlinkSQL中可使用${名称}来加载连接配置，如${ods},说明：名称指的是英文唯一标识，即如图所示的名称。注意需要开启全局变量（原片段机制）;

**Flink连接模板:** Flink连接模板作用是为生成FlinkSQL DDL而扩展的功能。其中${schemaName}动态获取数据库,${tableName}动态获取表名称。更多参数请参考[Flink官网](https://nightlies.apache.org/flink/flink-docs-master/docs/connectors/table/overview/);

**注释:** 自定义;

**是否启用:** 默认禁用,需要开启;

当前数据库统一使用如上定义的参数名称配置数据源连接。当前支持的数据源可参考[扩展数据源](/zh-CN/extend/datasource.md)章节。
