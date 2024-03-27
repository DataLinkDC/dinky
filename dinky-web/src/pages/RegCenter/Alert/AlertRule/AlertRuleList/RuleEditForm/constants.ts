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

import { JOB_STATUS } from '@/pages/DevOps/constants';
import { ClusterType } from '@/pages/RegCenter/Cluster/constants';
import { l } from '@/utils/intl';
import { CheckboxOptionType } from 'antd/es/checkbox/Group';
import { DefaultOptionType } from 'antd/es/select';

export enum RuleType {
  SYSTEM = 'SYSTEM',
  CUSTOM = 'CUSTOM'
}

export enum OperatorType {
  OPTIONS_SEL = 'OPTIONS_SEL',
  STR_VALUE = 'STR_VALUE',
  NUMBER_VALUE = 'NUMBER_VALUE'
}

export const TriggerType: CheckboxOptionType[] = [
  { label: l('sys.alert.rule.anyRule'), value: ' or ' },
  { label: l('sys.alert.rule.allRule'), value: ' and ' }
];

export const AllRuleOperator: DefaultOptionType[] = [
  { label: '>', value: 'GT' },
  { label: '<', value: 'LT' },
  { label: '=', value: 'EQ' },
  { label: '!=', value: 'NE' },
  { label: '>=', value: 'GE' },
  { label: '<=', value: 'LE' }
];

export const EqRuleOperator: DefaultOptionType[] = [{ label: '=', value: 'EQ' }];

export const BOOLEAN_VALUE_ENUM: DefaultOptionType[] = [
  { label: 'True', value: 'true' },
  { label: 'False', value: 'false' }
];

export const AlertRules: any = {
  taskId: {
    label: l('sys.alert.rule.label.taskId'),
    valueType: OperatorType.NUMBER_VALUE
  },
  duration: {
    label: l('sys.alert.rule.label.duration'),
    valueType: OperatorType.NUMBER_VALUE
  },
  jobStatus: {
    label: l('sys.alert.rule.label.jobStatus'),
    valueType: OperatorType.OPTIONS_SEL,
    valueEnum: Object.values(JOB_STATUS).map((t) => `'${t}'`)
  },
  batchModel: {
    label: l('sys.alert.rule.label.batchModel'),
    valueType: OperatorType.OPTIONS_SEL,
    valueEnum: BOOLEAN_VALUE_ENUM
  },
  clusterType: {
    label: l('sys.alert.rule.label.jobType'),
    valueType: OperatorType.OPTIONS_SEL,
    valueEnum: Object.values(ClusterType).map((t) => `'${t}'`)
  },
  checkpointCostTime: {
    label: l('sys.alert.rule.label.checkpointTime'),
    valueType: OperatorType.NUMBER_VALUE
  },
  isCheckpointFailed: {
    label: l('sys.alert.rule.label.checkpointFailed'),
    valueType: OperatorType.OPTIONS_SEL,
    valueEnum: BOOLEAN_VALUE_ENUM
  },
  isException: {
    label: l('sys.alert.rule.label.jobException'),
    valueType: OperatorType.OPTIONS_SEL,
    valueEnum: BOOLEAN_VALUE_ENUM
  }
};
