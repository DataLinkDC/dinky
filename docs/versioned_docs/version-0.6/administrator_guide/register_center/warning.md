---
position: 5
id: warning
title: 报警管理
---
如何创建报警实例及报警组，在0.6版本以后，用户可以创建报警实例及报警组，监控 FlinkSQL 作业。一个报警组可以使用多个报警实例，用户就可以进一步收到报警通知。收到的报警通知如下：

- unknown
- stop
- cancel
- finished

首先要进入**注册中心** > **报警管理**，然后选择左侧的报警实例管理，创建一个告警实例。然后选择对应的报警插件。然后选择报警组管理，创建一个报警组。

目前Dinky支持的报警插件有：

- **[钉钉](#钉钉)告警 :** WebHook
- **[企业微信](#企业微信)告警 :** 包含 **应用** **群聊**
- **[飞书](#飞书)告警 :** WebHook
- **[邮箱](#邮箱)告警 :**  通过邮件发送报警通知

## 报警实例管理

### 创建报警实例

**注册中心** > **报警管理**，选择**新建**

![create_alert_instance](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator_guide/register_center/warning/create_alert_instance.png)

点击以上告警实例类型,创建报警实例配置。报警实例配置完成，就可以创建报警组。

### 查询报警实例信息

![alert_instance_list](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator_guide/register_center/warning/alert_instance_list.png)

报警实例信息相关字段含义如下：


|     字段     |                    说明                    |
| :------------: | :-------------------------------------------: |
|     名称     |                  名称唯一                  |
|     类型     | WeChat<br/> DingTalk<br/> FeiShu<br/> Email |
|   是否启用   |             已启用<br/> 已禁用             |
| 最近更新时间 |               报警的修改时间               |
|     操作     |            对报警实例修改、删除            |

## 报警组管理

### 创建报警组

新建一个报警组，选择新建

![alert_group_list](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator_guide/register_center/warning/alert_group_list.png)

![create_alert_group](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator_guide/register_center/warning/create_alert_group.jpg)

### 查询报警组信息

报警组信息相关字段含义如下：


|     字段     |         说明         |
| :------------: | :--------------------: |
|     名称     |       名称唯一       |
|   是否启用   |  已启用<br/> 已禁用  |
| 最近更新时间 |    报警的修改时间    |
|     操作     | 对报警实例修改、删除 |

## 报警类型

### 钉钉

如果用户使用钉钉进行报警，请进入**注册中心** > **报警管理** > **报警实例管理**，点击**新建** 选择**钉钉**报警实例。

![create_dingdingtalk_alert](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator_guide/register_center/warning/create_dingdingtalk_alert.png)

**参数配置：**

- **名称：** 自定义;
- **地址:** Webhook,格式如下：https://oapi.dingtalk.com/robot/send?access_token=???????
- **关键字：** 安全设置的自定义关键词，钉钉关键字和 Dinky 中的关键字保持一致即可；
- **密令:** 安全设置的加签;
- **开启代理:** 默认否（一般默认）
- **@所有人:** 默认禁用；
- **@手机号:** 当@所有人禁用时 可以使用手机号 使用钉钉注册的手机号 多个用逗号隔开
- **是否启用:** 默认禁用，需要开启；
- **展示方式类型:** 支持 MarkDown 和文本；

**说明:** 自定义机器人发送消息时，在“被@人列表”里面的人员收到该消息时，会有@消息提醒。

[钉钉-开发者文档](https://open.dingtalk.com/document/robots/custom-robot-access)

### 企业微信

如果用户使用企业微信进行报警，请进入**注册中心** > **报警管理** > **报警实例管理**，点击**新建** 选择**企业微信**报警实例。

#### 微信企业应用配置

![create_wechat_alert](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator_guide/register_center/warning/create_wechat_app_alert.png)

**参数配置：**

- **名称：** 自定义;
- **企业id:** 询问企业微信管理员即可；
- **密令:** 安全设置的加签;
- **用户:** 企业微信联系人中可添加；
- **代理ID:**询问企业微信管理员即可；
- **发送方式:** 应用；
- **展示方式类型:** 支持 MarkDown 和文本；
- **是否启用:** 默认禁用，需要开启；

#### 微信企业群聊配置

![create_wechat_chat_alert](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator_guide/register_center/warning/create_wechat_chat_alert.png)

**参数配置：**

- **名称：** 自定义;
- **发送方式:** 群聊；
- **WebHook地址:** Webhook地址，如https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=xxxxxx
- **关键字:** 作为发送信息的title 标记；
- **用户:** 企业微信联系人中可添加；
- **@所有人:** 默认禁用，如果@所有人需要开启；
- **被@用户:** 企业微信用户名全拼；
- **展示方式类型:** 支持 MarkDown 和文本；
- **是否启用:** 默认禁用，需要开启；

**说明:** @所有人与被@用户可相互切换

其中发送方式分别对应企微文档：

[应用-开发者文档](https://work.weixin.qq.com/api/doc/90000/90135/90236)  
[群聊-开发者文档](https://work.weixin.qq.com/api/doc/90000/90135/90248)


### 飞书

如果用户使用飞书进行报警，请进入**注册中心** > **报警管理** > **报警实例管理**，点击**新建** 选择**飞书**报警实例。

![create_feishu_alert](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator_guide/register_center/warning/create_feishu_alert.png)

**参数配置：**

- **名称：** 自定义;
- **发送方式:** 群聊；
- **WebHook地址:** Webhook 地址，如https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=xxxxxx
- **关键字:** 作为发送信息的title 标记；
- **@所有人:** 默认禁用，如果@所有人需要开启；
- **被@用户:**飞书的用户ID；
- **是否启用:** 默认禁用，需要开启；
- **展示方式类型:** 支持富文本和纯文本；

**说明:** @所有人与被@用户可相互切换

[飞书-自定义机器人接入开发文档](https://open.feishu.cn/document/ukTMukTMukTM/ucTM5YjL3ETO24yNxkjN)

### 邮箱

如果用户使用邮箱进行报警，请进入**注册中心** > **报警管理** > **报警实例管理**，点击**新建** 选择**邮箱**报警实例。

![create_email_alert](http://www.aiwenmo.com/dinky/docs/zh-CN/administrator_guide/register_center/warning/create_email_alert.png)

**参数配置：**

- **名称：** 自定义;
- **发送方式:** 邮件发送；
- **收件人:** 收件人邮箱,多个使用逗号分开；
- **抄送人:** 抄送人邮箱,多个使用逗号分开；
- **邮件服务器Host:** 邮件服务器主机；
- **邮件服务器Port:** 邮件服务器端口；
- **发送者sender昵称:** 发送者到目标邮箱的显示昵称；
- **是否开启邮箱验证:**
  - 是
    - **邮箱用户名:** 邮箱；
    - **邮箱密码:** 邮箱密码；
  - 否
    - 表单无显示
- **是否开启tls证书验证:** 是/否；
- **是否开启SSL验证:**
  - 是
    - **受信任域:** 自行设置 可设置为*；
  - 否
    - 表单无显示
- **是否启用:** 默认禁用，需要开启；
- **展示方式类型:**
  - 支持文本 表格 附件 附件+表格；
  - PS: 需要注意的是 当选择  附件 || 附件+表格 时:
    - **XLS存放目录:** 非必填 默认路径为: /tmp/xls

:::warning 注意事项

报警管理只适用于 FlinkSQL

报警管理只支持异步提交和发布

:::
