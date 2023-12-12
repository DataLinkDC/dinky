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

import { FormContextValue } from "@/components/Context/FormContext";
import { NORMAL_MODAL_OPTIONS,SWITCH_OPTIONS } from "@/services/constants";
import { l } from "@/utils/intl";
import {
  ModalForm, ProFormCheckbox,
  ProFormDigit,
  ProFormGroup,
  ProFormRadio,
  ProFormSelect,
  ProFormSwitch,
  ProFormTextArea
} from "@ant-design/pro-components";

import { PriorityList,TimeoutNotifyStrategy } from "@/pages/DataStudio/HeaderContainer/PushDolphin/constants";
import { queryDataByParams } from "@/services/BusinessCrud";
import {DolphinTaskDefinition, DolphinTaskMinInfo} from "@/types/Studio/data.d";
import { Button,Form,Space } from "antd";
import { DefaultOptionType } from "antd/es/select";
import React,{ useEffect } from "react";

type PushDolphinProps = {
  onCancel: () => void;
  value: any;
  modalVisible: boolean;
  loading: boolean;
};

interface PushDolphinParams {
  upstreamCodes: string[];
  taskPriority: number;
  failRetryTimes: number;
  failRetryInterval: number;
  delayTime: number;
  timeout: number;
  timeoutFlag: boolean;
  flag: boolean;
  timeoutNotifyStrategy: string[];
  description: string;
  timeoutNotifyStrategyType: string;
}

export const PushDolphin: React.FC<PushDolphinProps> = (props) => {
  const { onCancel, value, modalVisible, loading } = props;

  const [dolphinTaskList, setDolphinTaskList] = React.useState<DolphinTaskMinInfo[]>([]);

  const [formValues, setFormValues] = React.useState<PushDolphinParams>({
    upstreamCodes: [],
    taskPriority: 0,
    failRetryTimes: 0,
    failRetryInterval: 0,
    delayTime: 0,
    timeout: 0,
    timeoutFlag: false,
    flag: false,
    timeoutNotifyStrategy: [],
    description: '',
    timeoutNotifyStrategyType: 'WARN',
  })


  /**
   * init form
   */
  const [form] = Form.useForm<PushDolphinParams>();

  /**
   * init form context
   */
  const formContext = React.useMemo<FormContextValue>(
    () => ({
      resetForm: () => form.resetFields() // 定义 resetForm 方法
    }),
    [form]
  );

  /**
   * cancel choose
   */
  const handleCancel = () => {
    onCancel();
    formContext.resetForm();
  };

  const renderFooter = () => {
    return [
      <Button key={'pushCancel'} onClick={handleCancel}>
        {l('button.cancel')}
      </Button>,
      <Button key={'push'} type='primary' htmlType={'submit'} loading={loading} onClick={() => {}}>
        {l('button.finish')}
      </Button>
    ];
  };

  useEffect(() => {
    queryDataByParams<DolphinTaskMinInfo[]>('/api/scheduler/upstream/tasks', {
      dinkyTaskId: value.id
    }).then((res) => setDolphinTaskList(res as DolphinTaskMinInfo[]));
    queryDataByParams<DolphinTaskDefinition>('/api/scheduler/task', {
      dinkyTaskId: value.id
    }).then((res) => {
      console.log(res)
    });

  }, [modalVisible]);

  const buildUpstreamTaskOptions = (
    data: DolphinTaskMinInfo[] | undefined
  ): DefaultOptionType[] => {
    if (data && data.length > 0) {
      return data.map((item) => {
        const label = (
          <Space>
            {item.taskName} <span>{item.taskType}</span>
            <span>
              {item.processDefinitionName} [{item.taskVersion}]
            </span>
          </Space>
        );
        return {
          label: label,
          value: item.id,
          key: item.id
        };
      });
    }
    return [];
  };

  const handleValueChange = (changedValues: any, allValues: any) => {
    setFormValues({...formValues,...allValues});
    console.log(changedValues, allValues);
  };


  return (
    <ModalForm<PushDolphinParams>
      {...NORMAL_MODAL_OPTIONS}
      title={`将任务 [ ${value.name} ]推送至 DolphinScheduler`}
      open={modalVisible}
      form={form}
      initialValues={formValues}
      modalProps={{ onCancel: handleCancel, ...NORMAL_MODAL_OPTIONS }}
      submitter={{ render: () => [...renderFooter()] }}
      syncToInitialValues
      onValuesChange={handleValueChange}
      loading={loading}
    >
      <ProFormSelect
        label={'前置任务'}
        name={'upstreamCodes'}
        showSearch
        mode={'multiple'}
        extra={'选择前置任务后，任务将会在前置任务执行成功后才会执行'}
        options={buildUpstreamTaskOptions(dolphinTaskList)}
      />

      <ProFormGroup>
        <ProFormSelect
          label={'优先级'}
          name={'taskPriority'} width={'sm'}
          options={PriorityList}
        />

        <ProFormDigit
          label={'重试次数'}
          name={'failRetryTimes'}
          width={'sm'}
          min={0}
          max={99}
          fieldProps={{
            precision: 0 ,
          }}
        />

        <ProFormDigit
          label={'重试间隔(分钟)'}
          name={'failRetryInterval'}
          width={'sm'}
          min={0}
          fieldProps={{
            precision: 0 ,
          }}
        />

        <ProFormDigit
          label={'延时执行(分钟)'}
          name={'delayTime'}
          width={'sm'}
          min={0}
          fieldProps={{
            precision: 0 ,
          }}
        />
        <ProFormDigit
          label={'超时时间(分钟)'}
          name={'timeout'}
          width={'sm'}
          min={0}
          max={30}
          fieldProps={{
            precision: 0 ,
          }}
        />

        <ProFormSwitch initialValue={false} label={'超时警告'} {...SWITCH_OPTIONS()} name={'timeoutFlag'} />

        <ProFormSwitch initialValue={false} label={'运行标志'} {...SWITCH_OPTIONS()} name={'flag'} />
      </ProFormGroup>

      {/*如果是失败告警，则需要设置告警策略*/}
      <ProFormCheckbox.Group
        label={'超时策略'}
        name={'timeoutNotifyStrategy'}
        width={'sm'}
        options={TimeoutNotifyStrategy}
      />

      <ProFormTextArea label={l('global.table.note')} name={'description'} />
    </ModalForm>
  );
};

export default PushDolphin;
