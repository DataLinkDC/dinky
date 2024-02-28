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

import { AutoSteps } from '@/pages/RegCenter/GitProject/components/BuildSteps/AutoSteps';
import {
  JavaSteps,
  PythonSteps
} from '@/pages/RegCenter/GitProject/components/BuildSteps/constants';
import { BuildStepsState } from '@/pages/RegCenter/GitProject/data.d';
import { renderStatus } from '@/pages/RegCenter/GitProject/function';
import { getSseData } from '@/services/api';
import { API_CONSTANTS } from '@/services/endpoints';
import { GitProject } from '@/types/RegCenter/data.d';
import { InitGitBuildStepsState } from '@/types/RegCenter/init.d';
import { GitBuildStepsState } from '@/types/RegCenter/state.d';
import { l } from '@/utils/intl';
import { Button, Modal } from 'antd';
import React, { useEffect, useState } from 'react';

/**
 * props
 */
type BuildStepsProps = {
  onOk: (flag?: boolean) => void;
  onReTry?: () => void;
  onRebuild: () => void;
  title: string;
  showLog?: boolean;
  values: Partial<GitProject>;
};

export const BuildSteps: React.FC<BuildStepsProps> = (props) => {
  /**
   * extract props
   */
  const { onOk: handleModalVisible, onReTry, showLog = false, onRebuild, title, values } = props;
  // todo: refactor this
  const [buildStepState, setBuildStepState] = useState<GitBuildStepsState>(InitGitBuildStepsState);

  let [logList, setLogList] = useState<Record<number, string[]>>({});
  const [log, setLog] = useState<string>('');
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
  };

  useEffect(() => {
    if (values.buildStep === 0) {
      setPercent(0);
      setCurrentStep(0);
    }
    // 这里不要代理。sse使用代理会变成同步
    // const eventSource = new EventSource("http://127.0.0.1:8888" + API_CONSTANTS.GIT_PROJECT_BUILD_STEP_LOGS + "?id=" + values.id);
    const eventSource = getSseData(
      API_CONSTANTS.BASE_URL + API_CONSTANTS.GIT_PROJECT_BUILD_STEP_LOGS + '?id=' + values.id
    );

    let stepArray: BuildStepsState[] = []; // 步骤数组
    let globalCurrentStep: number = 0;
    let execNum: number = 0;
    let stepNum: number = 1;
    let showDataStep = -1;
    let showData = '';
    let finish = false;
    let lastStep = 0;

    //sse listen event message
    eventSource.onmessage = (e) => {
      try {
        // type  // 1是总状态  2是log  3是部分状态
        // status // 0是失败 1是进行中 2 完成
        let result = JSON.parse(e.data);
        const { currentStep, type, data, status } = result;
        lastStep = currentStep;

        if (type === 0) {
          if (execNum === 1) {
            logList = {};
          }
          execNum++;
          let parseResultData = JSON.parse(data); // parse data
          stepNum = parseResultData.length; // get step num
          setPercent(parseInt(String((100 / stepNum) * currentStep))); // set percent
          setCurrentStep(currentStep); // set current step

          stepArray = parseResultData.map((item: any) => {
            return {
              key: item.step,
              title: renderTitle(item.step),
              status: renderStatus(item.status),
              description: item.startTime,
              disabled: true,
              onClick: () => {
                if (finish && item.step <= lastStep) {
                  if (item.step === showDataStep) {
                    setShowList(true);
                    setLog(showData);
                  } else {
                    setShowList(false);
                    setLog(logList[item.step]?.join('\n'));
                  }
                  setCurrentStep(item.step);
                }
              }
            };
          });
          globalCurrentStep = currentStep;
          handleSteps(stepArray);
        }
        if (type !== 0) {
          if (type === 1) {
            if (logList[currentStep] === undefined) {
              logList[currentStep] = [];
            }
            if (data !== undefined) {
              logList[currentStep].push(data);
              setLog(logList[currentStep]?.join('\n'));
            }
          } else if (type === 2) {
            showDataStep = currentStep;
            showData = JSON.parse(data);
          } else if (type === 3) {
            stepArray[currentStep].description = data;
          }

          if (currentStep >= globalCurrentStep) {
            setCurrentStep(currentStep);
            setPercent(parseInt(String((100 / stepNum) * currentStep)));
            stepArray[currentStep - 1].status = renderStatus(status);
          }
          if ((status === 2 && currentStep === stepNum) || status === 0) {
            stepArray.filter((x) => x.status != 'wait').forEach((d) => (d.disabled = false));
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
    };
  }, []);

  /**
   * cancel
   */
  const handleCancel = () => {
    handleModalVisible();
  };

  // todo: 重试需要实现在不关闭弹窗的情况下，重新构建, 目前是关闭弹窗，重新打开，重新构建
  const handleReTry = () => {
    onReTry?.();
  };

  const footerButtons = [
    <Button
      key={'retry'}
      type={'primary'}
      danger
      hidden={!onReTry}
      disabled={steps[currentStep - 1]?.status !== 'error'}
      onClick={() => handleReTry()}
    >
      {l('button.retry')}
    </Button>,
    <Button
      key={'rebuild'}
      type={'dashed'}
      hidden={showLog}
      loading={currentStep !== steps.length && percent !== 99}
      disabled={onReTry && steps[currentStep - 1]?.status === 'error'}
      onClick={() => onRebuild()}
    >
      {l('button.rebuild')}
    </Button>,
    <Button
      key={'close'}
      type={'primary'}
      hidden={!showLog}
      danger={showLog}
      onClick={() => handleCancel()}
    >
      {l('button.close')}
    </Button>,
    <Button
      key={'finish'}
      type={'primary'}
      hidden={showLog}
      htmlType={'submit'}
      autoFocus
      disabled={steps[currentStep - 1]?.status === 'error' && percent !== 99}
      loading={currentStep !== steps.length && percent !== 99}
      onClick={() => handleCancel()}
    >
      {l('button.finish')}
    </Button>,
    <Button
      key={'daemon'}
      hidden={!(currentStep !== steps.length && percent !== 99) || showLog}
      onClick={() => handleCancel()}
    >
      {steps[currentStep - 1]?.status === 'error' && percent !== 99
        ? l('button.close')
        : l('button.daemon')}
    </Button>
  ];

  /**
   * render
   */
  return (
    <Modal title={title} width={'85%'} open={true} maskClosable={false} footer={footerButtons}>
      <AutoSteps
        steps={steps}
        percent={percent}
        currentStep={currentStep}
        values={values}
        log={log}
        showList={showList}
      />
    </Modal>
  );
};
