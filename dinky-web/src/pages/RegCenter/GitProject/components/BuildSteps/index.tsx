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


import {Modal} from "antd";
import React, {useEffect, useState} from "react";
import {GitProject} from "@/types/RegCenter/data";
import {AutoSteps, BuildMsgData} from "@/pages/RegCenter/GitProject/components/BuildSteps/AutoSteps";
import {JavaSteps, PythonSteps} from "@/pages/RegCenter/GitProject/components/BuildSteps/constants";
import {API_CONSTANTS} from "@/services/constants";
import proxy from "../../../../../../config/proxy";
import {renderStatus} from '@/pages/RegCenter/GitProject/function';
import {BuildStepsState} from '@/pages/RegCenter/GitProject/data.d';


/**
 * props
 */
type BuildStepsProps = {
  onCancel: (flag?: boolean) => void
  title: string;
  values: GitProject;
};

export const BuildSteps: React.FC<BuildStepsProps> = (props) => {

  /**
   * extract props
   */
  const {
    onCancel: handleModalVisible,
    title,
    values,
  } = props;
  let [logList, setLogList] = useState<Record<number, string[]>>({});
  const [log, setLog] = useState<string>("");
  const [currentStep, setCurrentStep] = useState<number>(0);
  const [showList, setShowList] = useState<boolean>(false);
  const [percent, setPercent] = useState<number>(0);
  const [steps, handleSteps] = useState<BuildStepsState[]>([]);


  /**
   * render step title
   * @param step
   */
  const renderTitle = (step: number) => {
    return (values.codeType === 1 ? JavaSteps : PythonSteps)[step - 1].title;
  }

  useEffect(() => {
    if (values.buildStep === 0) {
      setPercent(0);
      setCurrentStep(0);
    }

    const {REACT_APP_ENV = 'dev'} = process.env;
    // @ts-ignore
    const url = proxy[REACT_APP_ENV]["/api/"].target || ""
    // 这里不要代理。sse使用代理会变成同步
    // const eventSource = new EventSource("http://127.0.0.1:8888" + API_CONSTANTS.GIT_PROJECT_BUILD_STEP_LOGS + "?id=" + values.id);
    const eventSource = new EventSource(url + API_CONSTANTS.GIT_PROJECT_BUILD_STEP_LOGS + "?id=" + values.id);

    let stepArray: BuildStepsState[] = []; // 步骤数组
    let globalCurrentStep: number = 0;
    let execNum: number = 0;
    let stepNum: number = 1;
    let showDataStep = -1;
    let showData = "";
    let finish = false;

    //sse listen event message
    eventSource.onmessage = e => {
      try {
        // type  // 1是总状态  2是log  3是部分状态
        // status // 0是失败 1是进行中 2 完成
        let result = JSON.parse(e.data);
        const {currentStep, type, data, status, history} = result;

        if (type === 0) {
          if (execNum === 1) {
            logList = {}
          }
          execNum++;
          let parseResultData = JSON.parse(data); // parse data
          stepNum = parseResultData.length; // get step num
          setPercent(parseInt(String(100 / stepNum * currentStep))); // set percent
          setCurrentStep(currentStep); // set current step

          stepArray = parseResultData.map((item: any) => {
            return {
              key: item.step,
              title: renderTitle(item.step),
              status: renderStatus(item.status),
              description: item.startTime,
              disabled: true,
              onClick: () => {
                if (finish) {
                  if (item.step === showDataStep) {
                    setShowList(true)
                    setLog(showData)
                  } else {
                    setShowList(false)
                    setLog(logList[item.step]?.join("\n"))
                  }
                  setCurrentStep(item.step)
                }
              }
            };
          })
          globalCurrentStep = currentStep;
          handleSteps(stepArray);
        }
        if (type !== 0) {
          if (type === 1) {
            if (logList[currentStep] === undefined) {
              logList[currentStep] = []
            }
            if (data !== undefined) {
              logList[currentStep].push(data)
              setLog(logList[currentStep]?.join("\n"))
            }
          } else if (type === 2) {
            showDataStep = currentStep;
            showData = JSON.parse(data);
          } else if (type === 3) {
            stepArray[currentStep].description = data;
          }

          if (currentStep >= globalCurrentStep) {
            setCurrentStep(currentStep);
            setPercent(parseInt(String(100 / stepNum * currentStep)));
            stepArray[currentStep - 1].status = renderStatus(status)
          }
          if ((status === 2 || status === 0) && currentStep === stepNum) {
            if (currentStep === stepNum) {
              stepArray.forEach(d => d.disabled = false)
            }
            finish = true;
            eventSource.close();
            return;
          }
        }
      } catch (e) {
        finish = true;
        eventSource.close();
      }
    };


    return () => {
      eventSource.close();
    }

  }, [])

  /**
   * cancel
   */
  const handleCancel = () => {
    handleModalVisible();
  }


  /**
   * render
   */
  return <>
    <Modal
      title={title}
      width={"85%"}
      open={true}
      maskClosable={false}
      onCancel={() => handleCancel()}
      okButtonProps={{style: {display: "none"}}}
    >
      <AutoSteps steps={steps} percent={percent} currentStep={currentStep}
                 values={values} log={log} showList={showList}/>
    </Modal>
  </>;
};
