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
  AlertRules,
  AllRuleOperator,
  EqRuleOperator,
  OperatorType
} from '@/pages/SettingCenter/AlertRule/AlertRuleList/RuleEditForm/constants';
import { l } from '@/utils/intl';
import { ProFormDigit, ProFormSelect, ProFormText } from '@ant-design/pro-components';
import { DefaultOptionType } from 'antd/es/select';

export const AlertRulesOption = () => {
  const res: DefaultOptionType[] = [];
  for (const [k, v] of Object.entries(AlertRules)) {
    res.push({
      // @ts-ignore
      label: v.label,
      value: k
    });
  }
  return res;
};

export const getOperatorOptions = (key: string) => {
  if (key && key in AlertRules) {
    if (AlertRules[key].valueType == OperatorType.NUMBER_VALUE) {
      return AllRuleOperator;
    } else {
      return EqRuleOperator;
    }
  } else {
    return [];
  }
};

export const buildValueItem = (key: string, isSystem: boolean) => {
  const plh = l('pages.datastudio.label.jobConfig.addConfig.value');
  if (key && key in AlertRules) {
    switch (AlertRules[key].valueType) {
      case OperatorType.NUMBER_VALUE:
        return (
          <ProFormDigit
            disabled={isSystem}
            name={'ruleValue'}
            fieldProps={{ precision: 0 }}
            placeholder={plh}
          />
        );
      case OperatorType.STR_VALUE:
        return <ProFormText disabled={isSystem} name={'ruleValue'} placeholder={plh} />;
      case OperatorType.OPTIONS_SEL:
        return (
          <ProFormSelect
            disabled={isSystem}
            width={'sm'}
            name='ruleValue'
            mode={'single'}
            placeholder={plh}
            options={AlertRules[key].valueEnum}
          />
        );
    }
  }
  return <span></span>;
};
