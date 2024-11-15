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

import {
  ProForm,
  ProFormDigit,
  ProFormInstance,
  ProFormSelect,
  ProFormSwitch,
  ProFormText
} from '@ant-design/pro-components';
import { l } from '@/utils/intl';
import { InfoCircleOutlined } from '@ant-design/icons';
import { SWITCH_OPTIONS } from '@/services/constants';
import { ProFormDependency } from '@ant-design/pro-form';
import { ProFormFlinkConfig } from '@/pages/DataStudioNew/CenterTabContent/SqlTask/TaskConfig/ProFormFlinkConfig';
import { ProFormFlinkUdfConfig } from '@/pages/DataStudioNew/CenterTabContent/SqlTask/TaskConfig/ProFormFlinkUdfConfig';
import React, { useEffect, useRef, useState } from 'react';
import { TaskState, TempData } from '@/pages/DataStudioNew/type';
import { JOB_LIFE_CYCLE } from '@/pages/DevOps/constants';
import { Alert } from 'antd';
import { SAVE_POINT_TYPE } from '@/pages/DataStudioNew/constants';
import { buildAlertGroupOptions } from '@/pages/DataStudioNew/CenterTabContent/SqlTask/TaskConfig/function';

export const BasicConfig = (props: {
  tempData: TempData;
  data: TaskState;
  onValuesChange?: (changedValues: any, values: TaskState) => void;
  setCurrentState?: (values: TaskState) => void;
  isLockTask: boolean;
}) => {
  const { alertGroup, flinkConfigOptions, flinkUdfOptions } = props.tempData;
  const formRef = useRef<ProFormInstance>();
  const [containerWidth, setContainerWidth] = useState<number>(0);

  const divRef = useRef<HTMLDivElement>(null);
  useEffect(() => {
    if (!divRef.current) {
      return () => {};
    }
    // 监控布局宽度高度变化，重新计算树的高度
    const element = divRef.current!!;
    const observer = new ResizeObserver((entries) => {
      if (entries?.length === 1) {
        // 这里节点理应为一个，减去的高度是为搜索栏的高度
        setContainerWidth(entries[0].contentRect.width);
      }
    });
    observer.observe(element);
    return () => {
      observer.unobserve(element);
    };
  }, []);

  return (
    <div ref={divRef} className={'datastudio-theme'}>
      {(props.data.step === JOB_LIFE_CYCLE.PUBLISH || props.isLockTask) && (
        <>
          <Alert
            message={
              props.isLockTask
                ? l('pages.datastudio.label.jobConfig.lock')
                : l('pages.datastudio.label.jobConfig.watermark')
            }
            type='info'
            showIcon
          />
        </>
      )}
      <ProForm
        initialValues={{ ...props.data }}
        submitter={false}
        disabled={props.data?.step === JOB_LIFE_CYCLE.PUBLISH || props.isLockTask}
        onValuesChange={props.onValuesChange}
        formRef={formRef}
        layout={'vertical'}
        rowProps={{
          gutter: [16, 0]
        }}
      >
        <ProForm.Group style={{ display: 'flex', justifyContent: 'center' }}>
          <ProFormDigit
            width={'xs'}
            label={l('pages.datastudio.label.jobConfig.parallelism')}
            name='parallelism'
            tooltip={l('pages.datastudio.label.jobConfig.parallelism.tip')}
            max={100}
            min={1}
          />
          <ProFormSwitch
            label={l('pages.datastudio.label.jobConfig.fragment')}
            name='fragment'
            valuePropName='checked'
            tooltip={{
              title: l('pages.datastudio.label.jobConfig.fragment.tip'),
              icon: <InfoCircleOutlined />
            }}
            {...SWITCH_OPTIONS()}
          />
          <ProFormSwitch
            label={l('pages.datastudio.label.jobConfig.batchmode')}
            name='batchModel'
            valuePropName='checked'
            tooltip={{
              title: l('pages.datastudio.label.jobConfig.batchmode.tip'),
              icon: <InfoCircleOutlined />
            }}
            {...SWITCH_OPTIONS()}
          />
        </ProForm.Group>
        <ProFormSelect
          label={l('pages.datastudio.label.jobConfig.savePointStrategy')}
          name='savePointStrategy'
          tooltip={l('pages.datastudio.label.jobConfig.savePointStrategy.tip')}
          options={SAVE_POINT_TYPE}
          allowClear={false}
        />
        <ProFormDependency name={['savePointStrategy']}>
          {({ savePointStrategy }) => {
            if (savePointStrategy === 3) {
              return (
                <ProFormText
                  label={l('pages.datastudio.label.jobConfig.savePointpath')}
                  name='savePointPath'
                  tooltip={l('pages.datastudio.label.jobConfig.savePointpath.tip1')}
                  placeholder={l('pages.datastudio.label.jobConfig.savePointpath.tip2')}
                />
              );
            } else {
              return null;
            }
          }}
        </ProFormDependency>

        <ProFormSelect
          label={l('pages.datastudio.label.jobConfig.alertGroup')}
          name='alertGroupId'
          placeholder={l('pages.datastudio.label.jobConfig.alertGroup.tip')}
          options={buildAlertGroupOptions(alertGroup)}
          allowClear={false}
        />
        <ProFormFlinkConfig
          containerWidth={containerWidth}
          flinkConfigOptions={flinkConfigOptions}
          getCode={() =>
            formRef.current
              ?.getFieldValue(['configJson', 'customConfig'])
              ?.map((item: { key: string; value: string }) => {
                if (item.key || item.value) {
                  return (item.key ?? '') + ' : ' + (item.value ?? '');
                }
                return '';
              })
              ?.join('\n')
          }
        />
        <ProFormFlinkUdfConfig
          containerWidth={containerWidth}
          data={props.data}
          flinkUdfOptions={flinkUdfOptions}
          proFormInstance={() => formRef.current!!}
          setCurrentState={props.setCurrentState}
          defaultValue={[]}
        />
      </ProForm>
    </div>
  );
};
