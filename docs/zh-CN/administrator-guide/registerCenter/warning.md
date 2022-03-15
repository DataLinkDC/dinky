如何创建报警实例及报警组，在0.6版本以后，用户可以创建报警实例及报警组，监控FlinkSQL作业。一个报警组可以使用多个报警实例，用户就可以进一步收到报警通知。收到的报警通知如下：

- unknow
- stop
- cancel
- finished

首先要进入<span style="">注册中心</span>，选择<span>报警管理</span>，然后选择左侧的报警实例管理，创建一个告警实例。然后选择对应的报警插件(当前支持钉钉及企业微信)。然后选择报警组管理，创建一个报警组。



## 报警实例管理

新建一个报警实例，选择新建

![image-20220313231030171](http://www.aiwenmo.com/dinky/dev/docs/image-20220313231030171.png)

![image-20220313231641014](http://www.aiwenmo.com/dinky/dev/docs/image-20220313231641014.png)



点击<span>钉钉或者企业微信</span>,创建报警实例配置。报警实例配置完成，就可以创建报警组。

## 报警组管理

新建一个报警组，选择新建

![image-20220313232311137](http://www.aiwenmo.com/dinky/dev/docs/image-20220313232311137.png)

![image-20220313232557068](http://www.aiwenmo.com/dinky/dev/docs/image-20220313232557068.png)

## 钉钉

如果用户使用钉钉进行报警，请在报警实例中选择报警实例管理，选择新建<span>钉钉</span>报警实例。钉钉的配置如下：

![image-20220313233200594](http://www.aiwenmo.com/dinky/dev/docs/image-20220313233200594.png)

**参数配置：**

- **名称：** 自定义;
- **地址:** Webhook,格式如下：https://oapi.dingtalk.com/robot/send?access_token=???????
- **关键字：** 安全设置的自定义关键词，如{"touser":"{toUser}","agentid":{agentId}","msgtype":"{showType}","{showType}":{"content":"{msg}"}}；
- **密令:** 安全设置的加签;
- **开启代理:** 默认否（一般默认）
- **@所有人:** 默认禁用，需要开启；
- **是否启用:** 默认禁用，需要开启；
- **展示方式类型:** 支持MarkDown和文本；

**说明:** 自定义机器人发送消息时，在“被@人列表”里面的人员收到该消息时，会有@消息提醒。

[自定义机器人接入开发文档](https://open.dingtalk.com/document/robots/custom-robot-access)   

## 企业微信

如果用户使用企业微信进行报警，请在报警实例中选择报警实例管理，选择新建<span>企业微信</span>报警实例。企业微信的配置如下：

![image-20220313235619613](http://www.aiwenmo.com/dinky/dev/docs/image-20220313235619613.png)

**参数配置：**

- **名称：** 自定义;
- **企业id:** 询问企业微信管理员即可；
- **密令:** 安全设置的加签;
- **用户:** 企业微信联系人中可添加；
- **发送消息:** 接口中定义的msg,如{"touser":"{toUser}","agentid":{agentId}","msgtype":"{showType}","{showType}":{"content":"{msg}"}}
- **代理ID:**询问企业微信管理员即可；
- **发送方式:** 支持应用和群聊；
- **展示方式类型:** 支持MarkDown和文本；
- **是否启用:** 默认禁用，需要开启；

其中发送方式分别对应企微文档：

   [应用](https://work.weixin.qq.com/api/doc/90000/90135/90236)  [群聊](https://work.weixin.qq.com/api/doc/90000/90135/90248)

   发送消息对应文档中的 content，与此相对应的值的变量为 {msg}
