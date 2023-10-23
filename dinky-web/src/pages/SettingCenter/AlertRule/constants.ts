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

import { l } from '@/utils/intl';
import { CheckboxOptionType } from 'antd/es/checkbox/Group';
import { DefaultOptionType } from 'antd/es/select';

export enum RuleType {
  SYSTEM = 'SYSTEM',
  CUSTOM = 'CUSTOM'
}

export const TriggerType: CheckboxOptionType[] = [
  { label: l('sys.alert.rule.anyRule'), value: ' or ' },
  { label: l('sys.alert.rule.allRule'), value: ' and ' }
];

export const AlertRules: DefaultOptionType[] = [
  { label: l('sys.alert.rule.jobStatus'), value: 'jobInstance.status' },
  {
    label: l('sys.alert.rule.checkpointTime'),
    value: 'checkPoints.checkpointTime(#key,#checkPoints)'
  },
  {
    label: l('sys.alert.rule.checkpointFailed'),
    value: 'checkPoints.checkFailed(#key,#checkPoints)'
  },
  { label: l('sys.alert.rule.jobException'), value: 'exceptionRule.isException(#key,#exceptions)' }
];

export const RuleOperator: DefaultOptionType[] = [
  { label: '>', value: 'GT' },
  { label: '<', value: 'LT' },
  { label: '=', value: 'EQ' },
  { label: '!=', value: 'NE' },
  { label: '>=', value: 'GE' },
  { label: '<=', value: 'LE' }
];
