export type AlertConfig = {
  type: string,
}

export const ALERT_TYPE = {
  DINGTALK:'DingTalk',
  WECHAT:'WeChat',
  FEISHU:'FeiShu',
};

export const ALERT_CONFIG_LIST: AlertConfig[] = [{
  type: ALERT_TYPE.DINGTALK,
},{
  type: ALERT_TYPE.WECHAT,
},{
  type: ALERT_TYPE.FEISHU,
}
];
