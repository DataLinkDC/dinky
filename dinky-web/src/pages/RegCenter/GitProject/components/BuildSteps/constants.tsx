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

import { l } from '@/utils/intl';

/**
 * BuildSteps
 *  when type is 1 , use java build step
 *  when type is 2 , use python build step
 */
export const BuildStepOfCodeType = {
  1: {
    1: l('rc.gp.build.step.1'),
    2: l('rc.gp.build.step.2'),
    3: l('rc.gp.build.step.3'),
    4: l('rc.gp.build.step.4'),
    5: l('rc.gp.build.step.5'),
    6: l('rc.gp.build.step.6')
  },
  2: {
    1: l('rc.gp.build.step.1'),
    2: l('rc.gp.build.step.2'),
    3: l('rc.gp.build.step.3'),
    4: l('rc.gp.build.step.4'),
    5: l('rc.gp.build.step.5'),
    6: l('rc.gp.build.step.6')
  }
};

export type BuildMsgResult = {
  type: number;
  data: any;
  currentStep: number;
  status: string;
};

export const JavaSteps = [
  {
    key: 1,
    title: l('rc.gp.build.step.1'),
    status: 'wait'
  },
  {
    key: 2,
    title: l('rc.gp.build.step.2'),
    status: 'wait'
  },
  {
    key: 3,
    title: l('rc.gp.build.step.3'),
    status: 'wait'
  },
  {
    key: 4,
    title: l('rc.gp.build.step.4'),
    status: 'wait'
  },
  {
    key: 5,
    title: l('rc.gp.build.step.5'),
    status: 'wait'
  },
  {
    key: 6,
    title: l('rc.gp.build.step.6'),
    status: 'wait'
  }
];

export const PythonSteps = [
  {
    key: 1,
    title: l('rc.gp.build.step.1'),
    status: 'process'
  },
  {
    key: 2,
    title: l('rc.gp.build.step.2'),
    status: 'wait'
  },
  {
    key: 3,
    title: l('rc.gp.build.step.4'),
    status: 'wait'
  },
  {
    key: 4,
    title: l('rc.gp.build.step.5'),
    status: 'wait'
  },
  {
    key: 5,
    title: l('rc.gp.build.step.6'),
    status: 'wait'
  }
];
