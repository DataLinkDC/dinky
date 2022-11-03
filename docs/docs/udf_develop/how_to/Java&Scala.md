---
sidebar_position: 1
id: java&scala_udf
title: Java&Scala UDF
---
## 作业创建
1. 选择java作业，并输入对应参数
```text
sub
截取函数
com.test.SubFunction
```
![create_java_udf_work.png](http://www.aiwenmo.com/dinky/docs/zh-CN/udf_develop/how_to/create_java_udf_work.png)
![create_java_udf_work2.png](http://www.aiwenmo.com/dinky/docs/zh-CN/udf_develop/how_to/create_java_udf_work2.png)
> 此时从模板构建了代码，剩下函数逻辑填补即可。

![java_udf_code.png](http://www.aiwenmo.com/dinky/docs/zh-CN/udf_develop/how_to/java_udf_code.png)

> 这里为了方便测试，返回一段字符串

2. 接下来创建一个 `FlinkSql` 作业
![java_udf_flink_sql.png](http://www.aiwenmo.com/dinky/docs/zh-CN/udf_develop/how_to/java_udf_flink_sql.png)

创建函数时，复制类名，以下为测试代码
```sql
create temporary function sb_j as 'com.test.SubFunction';


CREATE TABLE sourceTable (
    id int,
    java_c string
) WITH (
  'connector' = 'datagen'
);

CREATE  TABLE sinkTable
WITH (
    'connector' = 'print'
)
LIKE sourceTable (EXCLUDING ALL);


insert into sinkTable select id,sb_j(java_c) from sourceTable;

```

![java_udf_exec.png](http://www.aiwenmo.com/dinky/docs/zh-CN/udf_develop/how_to/java_udf_exec.png)
> 选择执行模式，我这里采用 `pre-job` 进行演示 

3. 执行，结果查看

![java_udf_flink_sout.png](http://www.aiwenmo.com/dinky/docs/zh-CN/udf_develop/how_to/java_udf_flink_sout.png)
查看 `Taskmanager` 输出，正常输出，验证成功

## 动图演示
![java_udf_show.gif](http://www.aiwenmo.com/dinky/docs/zh-CN/udf_develop/how_to/java_udf_show.gif)