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

import { DolphinTaskDefinition, PushDolphinParams } from '@/types/Studio/data.d';

export const transformPushDolphinParams = (
  dolphinTaskDefinition: DolphinTaskDefinition,
  pushDolphinParams: PushDolphinParams,
  toFormValues: boolean
) => {
  if (toFormValues && dolphinTaskDefinition) {
    const transformValue: PushDolphinParams = {
      ...pushDolphinParams,
      description: dolphinTaskDefinition.description,
      timeoutFlag: dolphinTaskDefinition.timeoutFlag === 'OPEN',
      flag: dolphinTaskDefinition.flag === 'YES',
      isCache: dolphinTaskDefinition.isCache === 'YES',
      upstreamCodes: dolphinTaskDefinition.upstreamTaskMap
        ? Object.keys(dolphinTaskDefinition.upstreamTaskMap)
        : [],
      timeoutNotifyStrategy:
        dolphinTaskDefinition.timeoutNotifyStrategy === 'WARNFAILED'
          ? ['WARN', 'FAILED']
          : [dolphinTaskDefinition.timeoutNotifyStrategy]
    };
    return transformValue;
  } else {
    const falseTransformValue: DolphinTaskDefinition = {
      ...dolphinTaskDefinition,
      ...pushDolphinParams,
      description: pushDolphinParams.description,
      timeoutFlag: pushDolphinParams.timeoutFlag ? 'OPEN' : 'CLOSE',
      flag: pushDolphinParams.flag ? 'YES' : 'NO',
      isCache: pushDolphinParams.isCache ? 'YES' : 'NO',
      timeoutNotifyStrategy: (pushDolphinParams.timeoutNotifyStrategy as string[]).join('')
    };
    return falseTransformValue;
  }
};
