/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */


import {Button, Progress, Steps, theme} from "antd";
import React, {useEffect, useState} from "react";
import CodeShow from "@/components/CustomEditor/CodeShow";
import {ErrorNotification, SuccessMessageAsync} from "@/utils/messages";
import {l} from "@/utils/intl";
import {API_CONSTANTS} from "@/services/constants";
import {GitProject} from "@/types/RegCenter/data";
import {processColor} from "@/pages/RegCenter/GitProject/constans";
import JarShow from "@/pages/RegCenter/GitProject/components/BuildSteps/JarShow";
import {BuildStepsState} from "@/pages/RegCenter/GitProject/components/BuildSteps";
import {JsonArray} from "type-fest";


export type BuildMsgData = {
  type: number; // //   1 是log  2是部分状态
  currentStep: number; // 1:check env 2:git clone 3:maven build 4:get jars 5:analysis udf class
  data: any; // if resultType is 1, data is log, else data is jar list or class list
  status: number; // 2:finish 1:running 0:fail
  resultType?: number; // 1:log 2: jar list or class list
  log: string;

}


type BuildStepsProps = {
  values: Partial<GitProject>;
  steps: any[];
  currentDataMsg: any;
  percent: number;
  currentStep: number;
  log: string;
}

export const AutoSteps: React.FC<BuildStepsProps> = (props) => {

  const {
    values, steps,
    currentDataMsg, percent, currentStep, log
  } = props;


  /**
   * state
   */
  const {token} = theme.useToken();

  useEffect(() => {
  }, []);


  const contentStyle: React.CSSProperties = {
    lineHeight: "300px",
    // textAlign: "center",
    color: token.colorTextTertiary,
    backgroundColor: token.colorFillAlter,
    borderRadius: token.borderRadiusLG,
    border: `1px dashed ${token.colorBorder}`,
    marginTop: 16,
  };

  return <>
    <Steps status={"wait"}
           size={"small"}
           onChange={(current) => current}
           type={"navigation"} responsive={true}
           current={currentStep-1} items={steps} percent={percent} initial={0}/>
    <Progress percent={percent} strokeColor={processColor}/>
    <div style={contentStyle}>
      {
        // if resultType is 1, data is log, else data is jar list or class list
        // currentDataMsg?.resultType === 1 ?
        <CodeShow height={"50vh"} code={log || ""} language={"java"} options={{scrollBeyondLastLine: true}}
                  showFloatButton={true}/>
        // :
        // <JarShow step={currentStepStatus} data={currentDataMsg?.data}/>
      }
    </div>
    {/*<div style={{marginTop: 24}}>*/}
    {/*    {currentStepStatus < steps.length - 1 && (*/}
    {/*        <Button type="primary" onClick={() => nextStep()}>*/}
    {/*            {l("button.next")}*/}
    {/*        </Button>*/}
    {/*    )}*/}
    {/*    {currentStepStatus > 0 && (*/}
    {/*        <Button style={{margin: "0 8px"}} onClick={() => prevStep()}>*/}
    {/*            {l("button.prev")}*/}
    {/*        </Button>*/}
    {/*    )}*/}
    {/*</div>*/}
  </>;
};
