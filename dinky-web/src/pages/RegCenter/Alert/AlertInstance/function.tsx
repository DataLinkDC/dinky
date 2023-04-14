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
import {DefaultSvg, DingTalkSvg, EmailSvg, FeiShuSvg, WeChatSvg} from "@/components/Icons/AlertIcon";

export const getJSONData = (values: Partial<Alert.AlertInstance>) => {
  if (!values.params || values.params === "") {
    return values;
  }
  let data = JSON.parse(values.params);
  return {...data, ...values};
};

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
  let data: string = JSON.stringify(params);
  return {...newValue, params: data};
};


export const getAlertIcon = (type: string, size?: number) => {
  switch (type) {
    case ALERT_TYPE.DINGTALK:
      return <DingTalkSvg size={size}/>;
    case ALERT_TYPE.WECHAT:
      return <WeChatSvg size={size}/>;
    case ALERT_TYPE.FEISHU:
      return <FeiShuSvg size={size}/>;
    case ALERT_TYPE.EMAIL:
      return <EmailSvg size={size}/>;
    default:
      return <DefaultSvg size={size}/>;
  }
};
