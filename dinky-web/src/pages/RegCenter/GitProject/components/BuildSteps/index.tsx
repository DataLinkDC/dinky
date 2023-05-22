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
import {l} from "@/utils/intl";
import {AutoSteps, BuildMsgData} from "@/pages/RegCenter/GitProject/components/BuildSteps/AutoSteps";
import {JavaSteps, PythonSteps} from "@/pages/RegCenter/GitProject/components/BuildSteps/constants";
import {API_CONSTANTS} from "@/services/constants";
import Exception from "@@/plugin-layout/Exception";
import integer from "async-validator/dist-types/validator/integer";


/**
 * props
 */
type BuildStepsProps = {
  onCancel: (flag?: boolean) => void;
  modalVisible: boolean;
  values: Partial<GitProject>;
};


export type BuildStepsState = {
  key: number;
  title: string;
  status: string;
  description: string;
  disabled: boolean;
  onClick: () => void;
}

export const BuildSteps: React.FC<BuildStepsProps> = (props) => {

  /**
   * extract props
   */
  const {
    onCancel: handleModalVisible,
    modalVisible,
    values,
  } = props;
  let [logList, setLogList] = useState<Record<number, string[]>>({});
  const [log, setLog] = useState<string>("");

  const [currentStep, setCurrentStep] = useState<number>(0);
  const [showList, setShowList] = useState<boolean>(false);
  const [percent, setPercent] = useState<number>(0);
  const [currentDataMsg, setCurrentDataMsg] = useState<BuildMsgData>();
  const [buildSteps, setBuildSteps] = useState<Partial<any[]>>([]);
  const [steps, handleSteps] = useState<BuildStepsState[]>([]);

  const buildStepList = () => {
    if (values.codeType === 1) { // if code type is  java then build java steps
      setBuildSteps(JavaSteps);
    } else {
      setBuildSteps(PythonSteps);
    }
  };


  const renderStatus = (status: number) => {
    let statusTemp = "";
    if (status === 0) {
      statusTemp = "error";
    } else if (status === 1) {
      statusTemp = "process";
    } else if (status === 2) {
      statusTemp = "finish";
    } else {
      statusTemp = "wait";
    }
    return statusTemp;
  }

  const renderTitle = (step: number) => {
    return (values.codeType === 1 ? JavaSteps : PythonSteps)[step-1].title;
  }

  useEffect(() => {
    if (values.buildStep === 0) {
      setPercent(0);
      setCurrentStep(0);
    }

    const eventSource = new EventSource("http://127.0.0.1:8888" + API_CONSTANTS.GIT_PROJECT_BUILD_STEP_LOGS + "?id=" + values.id);

    // setEventSource(eventSource);

    buildStepList();

    let stepArray: BuildStepsState[] = [];
    let globalCurrentStep: number = 0;
    let execNum: number = 0;
    let stepNum: number = 1;
    let showDataStep = -1;
    let showData = "";

    //sse listen event message
    eventSource.onmessage = e => {
      try {
        // type  // 1是总状态  2是log  3是部分状态
        // status // 0是失败 1是进行中 2 完成
        let resultMsg = JSON.parse(e.data);
        const {currentStep, type, data, status, history} = resultMsg;

        if (type === 0) {
          if (execNum === 1) {
            logList = {};
          }
          execNum++;
          let a1 = JSON.parse(data);
          stepNum = a1.length;
          setPercent(parseInt(String(100 / stepNum * currentStep)));
          setCurrentStep(currentStep);

          stepArray = a1.map((item: any) => {
            return {
              key: item.step,
              title: renderTitle(item.step),
              status: renderStatus(item.status),
              description: item.startTime,
              disabled: item.status === -1 || status === 1,
              onClick: () => {
                if (item.status !== -1 && status !== 1) {
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
          }
          if (type === 2) {
            showDataStep = currentStep;
            showData = JSON.parse(data);
          }
          if (currentStep >= globalCurrentStep) {
            setCurrentStep(currentStep);
            setPercent(parseInt(String(100 / stepNum * currentStep)));
            stepArray[currentStep - 1].status = renderStatus(status)
          }


          setCurrentDataMsg(resultMsg);
          if ((status === 2 || status === 0) && history === false) {
            eventSource.close();
            console.log("build success --- close")
          }
        }
      } catch (e) {
        console.error(e)
        eventSource.close();
      }
    };


    return () => {
      eventSource.close();
    }

  }, [])

  const handleCancel = () => {
    handleModalVisible();
  }


  return <>
    <Modal
      title={l("rc.gp.build")}
      width={"85%"}
      open={true}
      maskClosable={false}
      onCancel={() => handleCancel()}
      okButtonProps={{style: {display: "none"}}}
    >
      <AutoSteps steps={steps} currentDataMsg={currentDataMsg} percent={percent} currentStep={currentStep}
                 values={values} log={log} showList={showList}/>
    </Modal>
  </>;
};
