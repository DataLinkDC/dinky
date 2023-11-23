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

import CodeShow from '@/components/CustomEditor/CodeShow';
import JarShow from '@/pages/RegCenter/GitProject/components/BuildSteps/JarShow';
import { processColor } from '@/pages/RegCenter/GitProject/constans';
import { GitProject } from '@/types/RegCenter/data';
import { Progress, Steps, theme } from 'antd';
import React from 'react';

export type BuildMsgData = {
  type: number; // //   1 是log  2是部分状态
  currentStep: number; // 1:check env 2:git clone 3:maven build 4:get jars 5:analysis udf class
  data: any; // if resultType is 1, data is log, else data is jar list or class list
  status: number; // 2:finish 1:running 0:fail
  resultType?: number; // 1:log 2: jar list or class list
  log: string;
  showList: boolean;
};

type BuildStepsProps = {
  values: Partial<GitProject>;
  steps: any[];
  percent: number;
  currentStep: number;
  log: string;
  showList: boolean;
};

const CodeShowProps = {
  height: '50vh',
  language: 'javalog',
  lineNumbers: 'on',
  showFloatButton: true
};

export const AutoSteps: React.FC<BuildStepsProps> = (props) => {
  const { values, steps, percent, currentStep, log, showList } = props;

  /**
   * state
   */
  const { token } = theme.useToken();

  const contentStyle: React.CSSProperties = {
    color: token.colorTextTertiary,
    backgroundColor: token.colorFillAlter,
    borderRadius: token.borderRadiusLG,
    border: `1px dashed ${token.colorBorder}`,
    marginTop: 16
  };

  return (
    <>
      <Steps
        initial={0}
        type={'navigation'}
        status={'wait'}
        size={'small'}
        onChange={(current) => current}
        current={currentStep - 1}
        items={steps}
        percent={percent}
      />
      <Progress percent={percent} strokeColor={processColor} />
      <div style={contentStyle}>
        {
          // if resultType is 1, data is log, else data is jar list or class list
          !showList ? (
            <CodeShow
              code={log || ''}
              options={{ scrollBeyondLastLine: true }}
              {...CodeShowProps}
            />
          ) : (
            <JarShow value={values} data={log} />
          )
        }
      </div>
    </>
  );
};
