---
sidebar_position: 5
position: 5
id: application_model_submit
title: Application 模式提交
---

## 做好集群配置
具体配置教程参考集群配置:
[http://www.dinky.org.cn/docs/next/user_guide/register_center/cluster_manage#%E9%9B%86%E7%BE%A4%E9%85%8D%E7%BD%AE%E5%88%97%E8%A1%A8](http://www.dinky.org.cn/docs/next/user_guide/register_center/cluster_manage#%E9%9B%86%E7%BE%A4%E9%85%8D%E7%BD%AE%E5%88%97%E8%A1%A8)
## 创建FlinkSql作业
### 在数据开发中创建FlinkSql作业
![create_flinkSql_job](http://pic.dinky.org.cn/dinky/docs/zh-CN/practical_guide/run_mode_practice/application_model_submit/create_flinkSql_job.png)
## 编写FlinkSql
```sql
DROP table if exists datagen;
CREATE TABLE datagen (
  a INT,
  b varchar
) WITH (
  'connector' = 'datagen',
  'rows-per-second' = '1',
  'number-of-rows' = '50'
);
DROP table if exists print_table;
CREATE TABLE print_table (
  a INT,
  b varchar
) WITH (
  'connector'='print'
);


insert into print_table  select a,b from datagen ;
```
## 切换执行模式并运行FlinkSql作业
![toggle_execution_mode_and_run_job](http://pic.dinky.org.cn/dinky/docs/zh-CN/practical_guide/run_mode_practice/application_model_submit/toggle_execution_mode_and_run_job.png)
## 观察控制台输出
![observe_console_output_1](http://pic.dinky.org.cn/dinky/docs/zh-CN/practical_guide/run_mode_practice/application_model_submit/observe_console_output_1.png)
![observe_console_output_2](http://pic.dinky.org.cn/dinky/docs/zh-CN/practical_guide/run_mode_practice/application_model_submit/observe_console_output_2.png)
## 跳转运维中心查看任务运行状况
![view_task_runs_1](http://pic.dinky.org.cn/dinky/docs/zh-CN/practical_guide/run_mode_practice/application_model_submit/view_task_runs_1.png)
![view_task_runs_2](http://pic.dinky.org.cn/dinky/docs/zh-CN/practical_guide/run_mode_practice/application_model_submit/view_task_runs_2.png)










