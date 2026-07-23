---
sidebar_position: 1
id: dinky_integration_ds
title: Dinky 集成 DolphinScheduler 
---

:::info 说明
本文档介绍 DolphinScheduler 集成, 以及如何使用 DolphinScheduler 进行任务调度

注意: 本文档基于 Dinky 1.2.2 版本编写, 请确保 Dinky 版本 >= 1.2.X
:::

## 前置要求

- DolphinScheduler 3.2.0+
- Dinky 1.2.0+
- Docker 19.03+


## DolphinScheduler 环境准备

### 启动 DolphinScheduler
```bash
export DOLPHINSCHEDULER_VERSION=3.2.2

docker run --name dolphinscheduler-standalone-server -p 12345:12345 -p 25333:25333 -d apache/dolphinscheduler-standalone-server:"${DOLPHINSCHEDULER_VERSION}"
```

页面访问: http://ip:12345/dolphinscheduler/ui/login 默认用户名/密码: admin/dolphinscheduler123

### 创建租户并使用户加入该租户

1. 进入 `安全中心` -> `租户管理` -> `创建租户` 
2. 进入 `安全中心` -> `用户管理` -> `创建用户`/修改用户 -> `选择租户` -> 保存

### 创建 Token

进入 `安全中心` -> `令牌管理` -> `创建令牌`  请注意设置过期时间,并复制生成的 Token

## Dinky 配置

> 假设你已经正常安装好dinky

### 配置 DolphinScheduler

进入 `配置中心` -> `全局配置` -> 选择 `DolphinScheduler 配置` 

1. 配置 `DolphinScheduler 地址` 为: http://ip:12345/dolphinscheduler , 注意 ip 为 DolphinScheduler 服务所在机器的 ip, 根据实际情况修改
2. 配置 `DolphinScheduler Token` 为: 上一步创建的 Token
3. 配置 `DolphinScheduler 项目名` 为: `Dinky` (注意: 此为默认值, 可自行修改)
4. 最后修改 `是否启用 DolphinScheduler` 为 `是` (注意: 默认为不启用)

![Jrv2ib](https://files.junyao.tech/uPic/Jrv2ib.png)

> 正常启用好，会在 DolphinScheduler 中自动创建名为`Dinky`的项目

## Dinky 使用示例

### Dinky 侧步骤
1. 进入 `数据开发` -> `项目` -> 右键目录 ->`创建任务`
2. 任务类型可选择 `FlinkSQL` 或 `FlinkJar` ,填写名称,并点击确定
3. 打开该任务,编辑器内输入以下 `datagen-2` 作业

```sql
CREATE TABLE Orders (
    order_number BIGINT,
    price        DECIMAL(32,2),
    buyer        ROW<first_name STRING, last_name STRING>,
    order_time   TIMESTAMP(3)
) WITH (
  'connector' = 'datagen',
  'rows-per-second' = '1',
  'number-of-rows' = '50'
);
select order_number,price,first_name,last_name,order_time from Orders 
```
4. 点击 `发布` 按钮, 等待任务发布成功后，页面会自动刷新,从而出现 `推送按钮`。
5. 点击 `推送` 按钮, 配置推送参数, 在 Dinky 1.0.0 及以后, 支持了配置前置依赖, 请根据实际情况填写   
:::warning 注意
选择前置任务后，任务将会在前置任务执行成功后才会执行,请自行合理选择,避免任务循环依赖,本平台不做依赖检查
:::
![jmd4WB](https://files.junyao.tech/uPic/jmd4WB.png)
7. 配置完成之后, 点击 `完成` 按钮, 等待推送成功

### DolphinScheduler 侧步骤

1. 进入 `项目管理` -> 点击`Dinky`项目(此为在`配置中心`中配置的项目名) -> `工作流定义` ,点击工作流列表中备注为`系统添加` 的工作流
2. 即可看到刚才在 Dinky 侧创建的任务`datagen-2` 已经被推送至 DolphinScheduler,且可以看到其依赖关系已经被正确设置
如图: (忽略其他任务,只关注 `datagen-2` 任务即可)
![pXGOER](https://files.junyao.tech/uPic/pXGOER.png)
3. 返回到 `工作流定义` 页面, 将该工作流上线, 可点击`运行`按钮, 运行该工作流, 也可配置定时调度配置, 定时调度配置请参考 DolphinScheduler 官方文档
4. 剩余操作请参考 DolphinScheduler 官方文档, 本文档不再赘述

## 总结

通过以上步骤, 我们可以看到, Dinky 与 DolphinScheduler 集成的效果,全面基于页面化配置,页面化操作, 无需编写任何代码,即可完成任务的调度, 降低了用户的学习成本,提高了用户的开发效率, 也降低了用户的维护成本, 使得用户可以更加专注于业务开发, 而不是繁琐的调度维护工作.
