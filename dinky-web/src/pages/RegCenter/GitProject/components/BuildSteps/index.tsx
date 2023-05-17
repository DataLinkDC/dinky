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


    const [currentStep, setCurrentStep] = useState<number>(values.buildStep || 0);
    const [percent, setPercent] = useState<number>(0);
    const [currentDataMsg, setCurrentDataMsg] = useState<BuildMsgData>();
    const [eventSource, setEventSource] = useState<EventSource>();
    const [buildSteps, setBuildSteps] = useState<Partial<any[]>>([]);
    const [steps , handleSteps] = useState<Partial<BuildStepsState[]>>([]);

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
        return l("rc.gp.build.step." + step);
    }

  // const [data, error] = useSSE(() => {
  //   return fetch("https://myapi.example.com").then((res) => res.json());
  // }, []);

    useEffect(() => {
        if (values.buildStep === 0) {
            setPercent(0);
            setCurrentStep(0);
        }
        const eventSource = new EventSource(API_CONSTANTS.GIT_PROJECT_BUILD_STEP_LOGS + "?id=" + values.id);

        setEventSource(eventSource);

        buildStepList();

        //sse listen event message
        eventSource.onmessage = e => {
            // type  // 1是总状态  2是log  3是部分状态
            // status // 0是失败 1是进行中 2 完成
            let resultMsg = JSON.parse(e.data);

            if (resultMsg.type === 1) {
                // {
                //     key: 1,
                //     title: l("rc.gp.build.step.1"),
                //     status: "wait",
                // },
                console.log(resultMsg)

                const {currentStep,data} = resultMsg;

                setCurrentStep(currentStep);
                setPercent((currentStep * 20) - 20);

                let a1 = JSON.parse(data);

               const nnn = a1.map((item: any) => {
                    return {
                        key: item.step,
                        title: renderTitle(item.step),
                        status: renderStatus(item.status),
                        description: item.startTime,
                        disabled: item.status === 2,
                        onClick: () => {
                            console.log(item,111111111)
                        }
                    };
                })
                handleSteps(nnn);
            }
            if (resultMsg.type !== 1) {
                setCurrentDataMsg(resultMsg);
                console.log(resultMsg,'msg')
                if (resultMsg.status === 2 && resultMsg.currentStep === 6) {
                    eventSource.close();
                    console.log("build success --- close")
                }
            }
        };


        return () => {
            eventSource.close();
        }

    }, [values])

    const handleCancel = () => {
        handleModalVisible();
        eventSource?.close();
    }


    return <>
        <Modal
            title={l("rc.gp.build")}
            width={"85%"}
            open={modalVisible}
            maskClosable={false}
            onCancel={() => handleCancel()}
            okButtonProps={{style: {display: "none"}}}
        >
            <AutoSteps steps={steps} currentDataMsg={currentDataMsg} percent={percent} currentStep={currentStep}
                       values={values}/>
        </Modal>
    </>;
};
