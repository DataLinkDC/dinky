/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import {ALERT_TYPE} from '@/types/RegCenter/data.d';
import {l} from '@/utils/intl';
import {DefaultOptionType} from 'rc-select/es/Select';


export const ALERT_TYPE_LIST_OPTIONS: DefaultOptionType[] = [
  {
    label: l('rc.ai.dingTalk'),
    value: ALERT_TYPE.DINGTALK,
    disabled: false,
  },
  {
    label: l('rc.ai.wechat'),
    value: ALERT_TYPE.WECHAT,
    disabled: false,
  },
  {
    label: l('rc.ai.feishu'),
    value: ALERT_TYPE.FEISHU,
    disabled: false,
  },
  {
    label: l('rc.ai.email'),
    value: ALERT_TYPE.EMAIL,
    disabled: false,
  },
  {
    label: l('rc.ai.sms'),
    value: ALERT_TYPE.SMS,
    disabled: false,
  }
];


export const MANU_FACTURERS = [
  {
    label: l('rc.ai.mf.alibaba'),
    value: 1,
  },
  {
    label: l('rc.ai.mf.huawei'),
    value: 2,
  },
  {
    label: l('rc.ai.mf.yunpian'),
    value: 3,
  },
  {
    label: l('rc.ai.mf.tencent'),
    value: 4,
  },
  {
    label: l('rc.ai.mf.uni'),
    value: 5,
  },
  {
    label: l('rc.ai.mf.jdcloud'),
    value: 6,
  },
  {
    label: l('rc.ai.mf.cloopen'),
    value: 7,
  },
  {
    label: l('rc.ai.mf.emay'),
    value: 8,
  },
  {
    label: l('rc.ai.mf.ctyun'),
    value: 9,
  },
];

export enum SMS_TYPE {
  ALIBABA = 1,
  HUAWEI = 2,
  YUNPIAN = 3,
  TENCENT = 4,
  UNI = 5,
  JDCLOUD = 6,
  CLOOPEN = 7,
  EMAY = 8,
  CTYUN = 9,
}
