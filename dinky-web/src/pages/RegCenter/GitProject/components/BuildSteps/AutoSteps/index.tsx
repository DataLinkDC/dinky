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


import {Button, List, Progress, Steps, theme} from "antd";
import React, {useEffect, useState} from "react";
import CodeShow from "@/components/CustomEditor/CodeShow";
import {CheckCircleTwoTone} from "@ant-design/icons";
import {ErrorNotification, SuccessMessage, SuccessMessageAsync} from "@/utils/messages";
import {l} from "@/utils/intl";
import {useUnmount} from "ahooks";
import JarList from "@/pages/RegCenter/GitProject/components/BuildSteps/JarShow";
import JarShow from "@/pages/RegCenter/GitProject/components/BuildSteps/JarShow";




export type BuildMsgData = {
  currentStep: number; // 1:check env 2:git clone 3:maven build 4:get jars 5:analysis udf class
  status: number; // 2:finish 1:running 0:fail
  resultType: number; // 1:log 2: jar list or class list
  data: any; // if resultType is 1, data is log, else data is jar list or class list
}

const processColor = {
  "0%": "#8ac1ea",
  "20%": "#99e5d0",
  "40%": "#9ae77b",
  "60%": "#59b732",
  "80%": "#47d50a",
  "100%": "#01ad31"
};

export const AutoSteps: React.FC = () => {

  let es = new EventSource("http://127.0.0.1:8080/flux/flux");

  /**
   * state
   */
  const {token} = theme.useToken();
  const [current, setCurrent] = useState(0);
  const [percent, setPercent] = useState(0);
  const [currentDataMsg, setCurrentDataMsg] = useState<BuildMsgData>({currentStep: 3, status: 2, resultType: 2, data: "asas"},);


  /**
   * steps
   */
  const steps = [
    {
      title: l('rc.gp.build.step.0'),
      status: "finish",
      description: new Date().toLocaleString(),
      icon: <CheckCircleTwoTone twoToneColor="#52c41a"/>,
    },
    {
      title: l('rc.gp.build.step.1'),
      status: "finish",
      description: new Date().toLocaleString(),
    },
    {
      title: l('rc.gp.build.step.2'),
      status: "process",
      description: new Date().toLocaleString(),
    },
    {
      title: l('rc.gp.build.step.3'),
      status: "wait",
      description: new Date().toLocaleString(),
    },
    {
      title: l('rc.gp.build.step.4'),
      status: "wait",
      description: new Date().toLocaleString(),
    },
    {
      title: l('rc.gp.build.step.5'),
      status: "wait",
      description: new Date().toLocaleString(),
    },
  ];


  /**
   * 自动完成
   *  前提：sse发送的结果中得步骤值===当前步骤值，且sse返回数据的状态是 finish
   */
  const autoFinish = async () => {
    if (currentDataMsg.currentStep === current && currentDataMsg.status === 2) {
      await SuccessMessageAsync(l("rc.gp.buildSuccess"));
    }
  };

  /**
   *  next step
   */
  const next = () => {
    setCurrent(current + 1);
    setPercent(percent + 20);

  };

  /**
   * prev step
   */
  const prev = () => {
    setCurrent(current - 1);
    setPercent(percent - 20);

  };

  /**
   * 自动步进
   *  前提：sse发送的结果中得步骤值===当前步骤值，且sse返回数据的状态是 finish
   */
  const autoIncrementSteps = () => {
    if (currentDataMsg.currentStep === current && currentDataMsg.status === 2) {
      next();
    }
  };


  console.log("next", current);
  console.log("prev", current);


  useEffect(() => {
    //创建sse对象

    //sse listen event message
    es.onmessage = e => {
      setCurrentDataMsg(e.data);
      // call autoIncrement to auto step
      autoIncrementSteps();
    };
    //sse listen event error
    es.onerror = e => {
      ErrorNotification(e.type);
    };
   return () => {
      es.close();
    }
  }, []);


  const contentStyle: React.CSSProperties = {
    lineHeight: "300px",
    textAlign: "center",
    color: token.colorTextTertiary,
    backgroundColor: token.colorFillAlter,
    borderRadius: token.borderRadiusLG,
    border: `1px dashed ${token.colorBorder}`,
    marginTop: 16,
  };

  const items = steps.map((item) => ({
    key: item.title,
    description: item.description,
    title: item.title,
  }));


  return <>
    <Steps current={current} items={items}/>
    <Progress
      percent={percent}
      strokeColor={processColor}
    />
    <div style={contentStyle}>
      {
        // if resultType is 1, data is log, else data is jar list or class list
        currentDataMsg.resultType === 1 ?
          <CodeShow height={"50vh"} code={steps[current].description} language={"java"} showFloatButton/> :
          <JarShow  step={current}  />
      }
    </div>
    <div style={{marginTop: 24}}>
      {current < steps.length - 1 && (
        <Button type="primary" onClick={() => next()}>
          {l('button.next')}
        </Button>
      )}
      {current > 0 && (
        <Button style={{margin: "0 8px"}} onClick={() => prev()}>
          {l('button.prev')}
        </Button>
      )}
    </div>
  </>;
};
