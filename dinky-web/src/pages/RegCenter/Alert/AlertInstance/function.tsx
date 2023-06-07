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


import {Alert, ALERT_TYPE} from "@/types/RegCenter/data.d";
import {
  AlertGroupSvg,
  DefaultSvg,
  DingTalkSvg,
  EmailSvg,
  FeiShuSvg,
  SmsSvg,
  WeChatSvg
} from '@/components/Icons/AlertIcon';
import {MANU_FACTURERS} from '@/pages/RegCenter/Alert/AlertInstance/constans';

/**
 * get json data to alert instance
 * @param values
 */
export const getJSONData = (values: Partial<Alert.AlertInstance>) => {
  if (!values.params || values.params === "") {
    return values;
  }
  let data = JSON.parse(values.params);
  return {...data, ...values};
};

/**
 * build json data to alert instance
 * @param values
 * @param params
 */
export const buildJSONData = (values: Partial<Alert.AlertInstance>, params: any) => {
  let newValue = values;
  if (params.name) {
    newValue.name = params.name;
    delete params.name;
  }
  if (params.enabled) {
    newValue.enabled = params.enabled;
    delete params.enabled;
  }
  if (params.type) {
    newValue.type = params.type;
    delete params.type;
  }
  let data: string = JSON.stringify(params);
  return {...newValue, params: data};
};

/**
 * get alert icon
 * @param type alert type
 * @param size icon size
 */
export const getAlertIcon = (type: string, size?: number) => {
  switch (type) {
    case ALERT_TYPE.DINGTALK:
      return <DingTalkSvg size={size}/>;
    case ALERT_TYPE.WECHAT:
      return <WeChatSvg size={size}/>;
    case ALERT_TYPE.FEISHU:
      return <FeiShuSvg size={size}/>;
    case ALERT_TYPE.SMS:
      return <SmsSvg size={size}/>;
    case ALERT_TYPE.EMAIL:
      return <EmailSvg size={size}/>;
    case ALERT_TYPE.GROUP:
      return <AlertGroupSvg size={size}/>;
    default:
      return <DefaultSvg size={size}/>;
  }
};

export const getSmsType = (type: number) => {
  return MANU_FACTURERS.find((item) => item.value === type)?.label;
}
