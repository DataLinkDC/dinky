---
sidebar_position: 2
id: python_udf
title: Python UDF
---
## 环境准备
1. 需要把 `${FLINK_HOME}/opt/flink-python_${scala-version}-${flink-version}.jar` 放入到
   `${FLINK_HOME}/lib/` 和 `${Dinky_HOME}/plugins/` 下面。如下：
![flink_python_intro.png](http://www.aiwenmo.com/dinky/docs/zh-CN/udf_develop/how_to/flink_python_intro.png)

2. 在 Dinky 安装目录 `conf/application.yml` 下，配置 python 执行环境。例如 ：/usr/bin/python3.6
![python_udf_config.png](http://www.aiwenmo.com/dinky/docs/zh-CN/udf_develop/how_to/python_udf_config.png)
----
## 作业创建
1. 选择 Python 作业，并输入对应参数
```text
python_udf_test
python截取函数
SubFunction.f
```
![create_python_udf_work.png](http://www.aiwenmo.com/dinky/docs/zh-CN/udf_develop/how_to/create_python_udf_work.png)
> 注意名称，底层实现逻辑即通过名称创建 py 文件，如： python_udf_test.py

![create_python_udf_work2.png](http://www.aiwenmo.com/dinky/docs/zh-CN/udf_develop/how_to/create_python_udf_work2.png)
> 观察最右侧的类型，前缀就是 刚刚填的`名称`



2. 接下来创建一个 `FlinkSql` 作业
创建函数与Java不太一样， 最后得指定语言
   language PYTHON
```sql
create temporary function sb_p as 'python_udf_test.SubFunction' language PYTHON;


CREATE TABLE sourceTable (
                            id int,
                            python_c string
) WITH (
   'connector' = 'datagen'
);

CREATE  TABLE sinkTable
WITH (
   'connector' = 'print'
)
   LIKE sourceTable (EXCLUDING ALL);


insert into sinkTable select id,sb_p(python_c) from sourceTable;

```

![python_udf_sql.png](http://www.aiwenmo.com/dinky/docs/zh-CN/udf_develop/how_to/python_udf_sql.png)

flink-conf.yml
```yaml
python.client.executable: /root/miniconda3/bin/python3.8
python.executable: /root/miniconda3/bin/python3.8
```
> 这里需要注意的是，`flink-conf.yml` 得配置 `python` 的执行路径，`python` 环境必须包含 `apache-flink` ,支持 `python3.6 - python3.8`
> 
> `python.executable` 是 NodeManager 环境的 python 执行路径
> 
> `python.client.executable` 是 Dinky 当前机器所在的python环境
> 
> 特别注意，`yarn-application` 环境，2个参数均是hadoop集群 python3 的执行路径

3. 执行，结果查看
![python_udf_tm.png](http://www.aiwenmo.com/dinky/docs/zh-CN/udf_develop/how_to/python_udf_tm.png)

## 动图演示
![java_udf_show.gif](http://www.aiwenmo.com/dinky/docs/zh-CN/udf_develop/how_to/python_udf_show.gif)