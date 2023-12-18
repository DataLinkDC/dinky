/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

import {
  AliYunSmsSvg,
  DingTalkSvg,
  EmailSvg,
  FeiShuSvg,
  SmsSvg,
  TencentSmsSvg,
  WeChatSvg
} from '@/components/Icons/AlertIcon';
import { ALERT_TYPE } from '@/types/RegCenter/data.d';
import { l } from '@/utils/intl';
import { Space } from 'antd';
import { DefaultOptionType } from 'rc-select/es/Select';

export const ALERT_TYPE_LIST_OPTIONS: DefaultOptionType[] = [
  {
    label: (
      <Space align={'baseline'} size={5}>
        <DingTalkSvg size={16} /> {l('rc.ai.dingTalk')}
      </Space>
    ),
    value: ALERT_TYPE.DINGTALK
  },
  {
    label: (
      <Space align={'baseline'} size={5}>
        <WeChatSvg size={16} /> {l('rc.ai.wechat')}
      </Space>
    ),
    value: ALERT_TYPE.WECHAT
  },
  {
    label: (
      <Space align={'baseline'} size={5}>
        <FeiShuSvg size={16} /> {l('rc.ai.feishu')}
      </Space>
    ),
    value: ALERT_TYPE.FEISHU
  },
  {
    label: (
      <Space align={'baseline'} size={5}>
        <EmailSvg size={16} /> {l('rc.ai.email')}
      </Space>
    ),
    value: ALERT_TYPE.EMAIL
  },
  {
    label: (
      <Space align={'baseline'} size={5}>
        <SmsSvg size={16} /> {l('rc.ai.sms')}
      </Space>
    ),
    value: ALERT_TYPE.SMS
  }
];

export enum SMS_TYPE {
  ALIBABA = 'alibaba',
  CLOOPEN = 'cloopen',
  CTYUN = 'ctyun',
  EMAY = 'emay',
  HUAWEI = 'huawei',
  JDCLOUD = 'jdcloud',
  NETEASE = 'netease',
  TENCENT = 'tencent',
  UNISMS = 'unisms',
  YUNPIAN = 'yunpian',
  ZHUTONG = 'zhutong'
}

export const MANU_FRACTURES = [
  {
    label: (
      <Space align={'baseline'} size={5}>
        <AliYunSmsSvg size={16} /> {l('rc.ai.mf.alibaba')}
      </Space>
    ),
    value: SMS_TYPE.ALIBABA,
    key: l('rc.ai.mf.alibaba')
  },
  {
    label: (
      <Space align={'baseline'} size={5}>
        <TencentSmsSvg size={16} /> {l('rc.ai.mf.tencent')}
      </Space>
    ),
    value: SMS_TYPE.TENCENT,
    key: l('rc.ai.mf.tencent')
  }
  // todo: 以下短信暂不实现
  // {
  //   label: l('rc.ai.mf.huawei'),
  //   value: SMS_TYPE.HUAWEI,
  //   disabled: true
  // },
  // {
  //   label: l('rc.ai.mf.uni'),
  //   value: SMS_TYPE.UNISMS,
  //   disabled: true
  // },
  // {
  //   label: l('rc.ai.mf.yunpian'),
  //   value: SMS_TYPE.YUNPIAN,
  //   disabled: true
  // },
  // {
  //   label: l('rc.ai.mf.jdcloud'),
  //   value: SMS_TYPE.JDCLOUD,
  //   disabled: true
  // },
  // {
  //   label: l('rc.ai.mf.cloopen'),
  //   value: SMS_TYPE.CLOOPEN,
  //   disabled: true
  // },
  // {
  //   label: l('rc.ai.mf.emay'),
  //   value: SMS_TYPE.EMAY,
  //   disabled: true
  // },
  // {
  //   label: l('rc.ai.mf.ctyun'),
  //   value: SMS_TYPE.CTYUN,
  //   disabled: true
  // },
  // {
  //   label: l('rc.ai.mf.netease'),
  //   value: SMS_TYPE.NETEASE,
  //   disabled: true
  // },
  // {
  //   label: l('rc.ai.mf.zhutong'),
  //   value: SMS_TYPE.ZHUTONG,
  //   disabled: true
  // }
];
