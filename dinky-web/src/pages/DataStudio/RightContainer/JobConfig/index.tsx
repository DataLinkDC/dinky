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

import FlinkOptionsSelect from '@/components/Flink/OptionsSelect';
import { SAVE_POINT_TYPE } from '@/pages/DataStudio/constants';
import {
  assert,
  getCurrentData,
  getCurrentTab,
  isDataStudioTabsItemType,
  lockTask
} from '@/pages/DataStudio/function';
import { StateType, STUDIO_MODEL, STUDIO_MODEL_ASYNC } from '@/pages/DataStudio/model';
import {
  buildAlertGroupOptions,
  buildClusterConfigOptions,
  buildClusterOptions,
  buildEnvOptions,
  buildRunModelOptions,
  calculatorWidth,
  isCanRenderClusterConfiguration,
  isCanRenderClusterInstance
} from '@/pages/DataStudio/RightContainer/JobConfig/function';
import { JOB_LIFE_CYCLE } from '@/pages/DevOps/constants';
import { AlertStateType, ALERT_MODEL_ASYNC } from '@/pages/RegCenter/Alert/AlertInstance/model';
import { SysConfigStateType } from '@/pages/SettingCenter/GlobalSetting/model';
import { DIALECT, RUN_MODE, SWITCH_OPTIONS } from '@/services/constants';
import { l } from '@/utils/intl';
import { InfoCircleOutlined } from '@ant-design/icons';
import {
  ProForm,
  ProFormDigit,
  ProFormGroup,
  ProFormList,
  ProFormSelect,
  ProFormSwitch,
  ProFormText
} from '@ant-design/pro-components';
import { useModel } from '@umijs/max';
import { Alert, Input, Space } from 'antd';
import { useForm } from 'antd/es/form/Form';
import { debounce } from 'lodash';
import React, { useEffect, useState } from 'react';
import { connect } from 'umi';
import FlinkUdfOptionsSelect from '@/components/Flink/UdfSelect';
import { TaskUdfRefer } from '@/types/Studio/data';
import { ErrorMessageAsync } from '@/utils/messages';

const JobConfig = (props: any) => {
  const {
    sessionCluster,
    clusterConfiguration,
    dispatch,
    tabs: { panes, activeKey },
    env,
    group,
    rightContainer,
    flinkConfigOptions,
    flinkUdfOptions,
    taskOwnerLockingStrategy
  } = props;

  const current = getCurrentData(panes, activeKey);

  const [form] = useForm();

  const [selectRunMode, setSelectRunMode] = useState<string>(current?.type ?? RUN_MODE.LOCAL);

  const [currentSelectUdfIndexMap, setCurrentSelectUdfIndexMap] = useState<
    Map<number, TaskUdfRefer>
  >(
    new Map(
      current?.configJson?.udfRefer?.map((item: TaskUdfRefer, index: number) => [index, item]) ?? []
    )
  );

  const { initialState, setInitialState } = useModel('@@initialState');

  const isLockTask = lockTask(
    current?.firstLevelOwner!,
    current?.secondLevelOwners,
    initialState?.currentUser?.user,
    taskOwnerLockingStrategy
  );

  useEffect(() => {
    dispatch({
      type: STUDIO_MODEL_ASYNC.queryFlinkConfigOptions
    });
    dispatch({
      type: ALERT_MODEL_ASYNC.queryAlertGroup
    });
    dispatch({
      type: STUDIO_MODEL_ASYNC.queryFlinkUdfOptions
    });
    setSelectRunMode(current?.type ?? RUN_MODE.LOCAL);
    form.setFieldsValue({ ...current, type: current?.type });
  }, [current]);

  const onValuesChange = (change: { [key in string]: any }, all: any) => {
    const pane = getCurrentTab(panes, activeKey);
    if (!isDataStudioTabsItemType(pane)) {
      return;
    }

    Object.keys(change).forEach((key) => {
      if (key === 'configJson') {
        if (!pane.params.taskData.configJson) {
          // @ts-ignore
          pane.params.taskData.configJson = {};
        }

        Object.keys(change[key]).forEach((k) => {
          // @ts-ignore
          pane.params.taskData[key][k] = all[key][k];
        });
      } else {
        pane.params.taskData[key] = all[key];
      }
    });
    pane.isModified = true;
    dispatch({
      type: STUDIO_MODEL.saveTabs,
      payload: { ...props.tabs }
    });
  };

  /**
   * 处理 selectUdfIndexMap 的状态 | process the state of selectUdfIndexMap
   * @param index
   * @param className
   * @param name
   */
  function processSelectUdfMapState(index: number, className: string = '', name: string = '') {
    setCurrentSelectUdfIndexMap((prevState) => {
      const newState = new Map(prevState);
      newState.set(index, {
        className: className,
        name: name
      });
      return newState;
    });
  }

  const handleClassChange = async (value: string, index: number) => {
    // 检测 这个值是否已经存在 currentSelectUdfIndexMap 的 map 中 || check if the value already exists in the map of currentSelectUdfIndexMap
    const values = currentSelectUdfIndexMap.values();
    for (const taskUdfRefer of values) {
      if (taskUdfRefer?.className === value) {
        await ErrorMessageAsync(
          l('pages.datastudio.label.udf.duplicate.tip', '', { className: value }),
          3
        );
        // clear the value of the form
        form.setFieldsValue({
          configJson: {
            udfRefer: {
              [index]: {
                className: '',
                name: ''
              }
            }
          }
        });
        return;
      }
    }
    const simpleClassName = value?.split('.')?.pop() ?? '';
    const lowerName = simpleClassName.charAt(0).toLowerCase() + simpleClassName.slice(1);
    processSelectUdfMapState(index, value, lowerName);
    form.setFieldsValue({
      configJson: {
        udfRefer: {
          [index]: {
            className: value,
            name: lowerName
          }
        }
      }
    });
  };

  function handleNameChange(name: string, index: number) {
    // 拿到  currentSelectUdfIndexMap[index].get(index) 的值 || get the value of currentSelectUdfIndexMap[index].get(index)
    const currentSelectUdfIndexMapValue = currentSelectUdfIndexMap.get(index);

    // 如果 name 和 currentSelectUdfIndexMapValue?.name 相等 则不做任何操作 || if name and currentSelectUdfIndexMapValue?.name are equal, do nothing
    if (currentSelectUdfIndexMapValue?.name && name !== currentSelectUdfIndexMapValue?.name) {
      // 更新 currentSelectUdfIndexMap 的值
      processSelectUdfMapState(index, currentSelectUdfIndexMapValue?.className, name);
    }
    form.setFieldsValue({
      configJson: {
        udfRefer: {
          [index]: {
            className: currentSelectUdfIndexMapValue?.className ?? '',
            name: name
          }
        }
      }
    });
  }

  return (
    <div style={{ maxHeight: rightContainer.height, marginTop: 10 }}>
      {(current?.step === JOB_LIFE_CYCLE.PUBLISH || isLockTask) && (
        <>
          <Alert
            message={
              isLockTask
                ? l('pages.datastudio.label.jobConfig.lock')
                : l('pages.datastudio.label.jobConfig.watermark')
            }
            type='info'
            showIcon
          />
        </>
      )}
      <ProForm
        size={'middle'}
        initialValues={{
          type: selectRunMode,
          envId: -1,
          parallelism: 1,
          savePointStrategy: 0,
          alertGroupId: -1
        }}
        className={'data-studio-form'}
        style={{ paddingInline: '15px', overflow: 'scroll', marginTop: 5 }}
        form={form}
        submitter={false}
        layout='vertical'
        disabled={current?.step === JOB_LIFE_CYCLE.PUBLISH || isLockTask} // 当该任务处于发布状态时 表单禁用 不允许修改 | when this job is publishing, the form is disabled , and it is not allowed to modify
        onValuesChange={debounce(onValuesChange, 500)}
        syncToInitialValues
      >
        <ProFormSelect
          name='type'
          label={l('global.table.execmode')}
          tooltip={l('pages.datastudio.label.jobConfig.execmode.tip')}
          rules={[{ required: true, message: l('pages.datastudio.label.jobConfig.execmode.tip') }]}
          options={buildRunModelOptions()}
          fieldProps={{
            onChange: (value: string) => {
              setSelectRunMode(value);
              form.resetFields(['clusterId', 'clusterConfigurationId']);
            }
          }}
          allowClear={false}
        />
        {selectRunMode !== RUN_MODE.LOCAL && (
          <>
            {/*集群实例渲染逻辑*/}
            {isCanRenderClusterInstance(selectRunMode) && (
              <>
                <ProFormSelect
                  style={{ width: '100%' }}
                  placeholder={l('pages.datastudio.label.jobConfig.clusterConfig.tip1', '', {
                    type: current?.type
                  })}
                  label={l('pages.datastudio.label.jobConfig.cluster')}
                  tooltip={l('pages.datastudio.label.jobConfig.clusterConfig.tip2', '', {
                    type: current?.type
                  })}
                  rules={[
                    {
                      required: true,
                      message: l('pages.datastudio.label.jobConfig.clusterConfig.tip1', '', {
                        type: current?.type
                      })
                    }
                  ]}
                  name='clusterId'
                  options={buildClusterOptions(selectRunMode, sessionCluster)}
                />
              </>
            )}

            {/*集群配置渲染逻辑*/}
            {isCanRenderClusterConfiguration(selectRunMode) && (
              <ProFormSelect
                name='clusterConfigurationId'
                placeholder={l('pages.datastudio.label.jobConfig.clusterConfig.tip1', '', {
                  type: selectRunMode
                })}
                label={l('pages.datastudio.label.jobConfig.clusterConfig')}
                tooltip={l('pages.datastudio.label.jobConfig.clusterConfig.tip2', '', {
                  type: selectRunMode
                })}
                rules={[
                  {
                    required: true,
                    message: l('pages.datastudio.label.jobConfig.clusterConfig.tip1', '', {
                      type: selectRunMode
                    })
                  }
                ]}
                options={buildClusterConfigOptions(selectRunMode, clusterConfiguration)}
                allowClear={false}
              />
            )}
          </>
        )}

        {assert(current?.dialect, [DIALECT.FLINK_SQL], true, 'includes') && (
          <ProFormSelect
            name='envId'
            label={l('pages.datastudio.label.jobConfig.flinksql.env')}
            tooltip={l('pages.datastudio.label.jobConfig.flinksql.env.tip1')}
            options={buildEnvOptions(env)}
            rules={[
              { required: true, message: l('pages.datastudio.label.jobConfig.flinksql.env.tip1') }
            ]}
            showSearch
            allowClear={false}
          />
        )}

        <ProFormGroup>
          <ProFormDigit
            width={'xs'}
            label={l('pages.datastudio.label.jobConfig.parallelism')}
            name='parallelism'
            tooltip={l('pages.datastudio.label.jobConfig.parallelism.tip')}
            max={9999}
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
        </ProFormGroup>

        <ProFormSelect
          label={l('pages.datastudio.label.jobConfig.savePointStrategy')}
          name='savePointStrategy'
          tooltip={l('pages.datastudio.label.jobConfig.savePointStrategy.tip')}
          options={SAVE_POINT_TYPE}
          allowClear={false}
        />

        {current?.savePointStrategy === 3 && (
          <ProFormText
            label={l('pages.datastudio.label.jobConfig.savePointpath')}
            name='savePointPath'
            tooltip={l('pages.datastudio.label.jobConfig.savePointpath.tip1')}
            placeholder={l('pages.datastudio.label.jobConfig.savePointpath.tip2')}
          />
        )}

        <ProFormSelect
          label={l('pages.datastudio.label.jobConfig.alertGroup')}
          name='alertGroupId'
          placeholder={l('pages.datastudio.label.jobConfig.alertGroup.tip')}
          options={buildAlertGroupOptions(group)}
          allowClear={false}
        />

        <ProFormList
          label={l('pages.datastudio.label.jobConfig.other')}
          tooltip={l('pages.datastudio.label.jobConfig.other.tip')}
          name={['configJson', 'customConfig']}
          copyIconProps={false}
          creatorButtonProps={{
            style: { width: '100%' },
            creatorButtonText: l('pages.datastudio.label.jobConfig.addConfig')
          }}
        >
          <ProFormGroup>
            <Space key={'config'} align='baseline'>
              <FlinkOptionsSelect
                name='key'
                width={calculatorWidth(rightContainer.width) + 50}
                mode={'single'}
                allowClear
                showSearch
                placeholder={l('pages.datastudio.label.jobConfig.addConfig.params')}
                options={flinkConfigOptions}
              />
              <ProFormText
                name={'value'}
                width={calculatorWidth(rightContainer.width) - 60}
                placeholder={l('pages.datastudio.label.jobConfig.addConfig.value')}
              />
            </Space>
          </ProFormGroup>
        </ProFormList>
        <ProFormList
          label={l('pages.datastudio.label.udf')}
          tooltip={l('pages.datastudio.label.udf.tip')}
          name={['configJson', 'udfRefer']}
          copyIconProps={false}
          onAfterRemove={(_, index) => {
            // 删除一项之后拿到 index 从 currentSelectUdfIndexMap 中删除对应的值 || get the value from currentSelectUdfIndexMap and delete it
            setCurrentSelectUdfIndexMap((prevState) => {
              const newState = new Map(prevState);
              newState.delete(index);
              return newState;
            });
          }}
          creatorButtonProps={{
            style: { width: '100%' },
            creatorButtonText: l('pages.datastudio.label.udf.injectUdf')
          }}
        >
          {(_, index) => {
            return (
              <ProFormGroup>
                <Space key={'udf' + index} align='baseline'>
                  <FlinkUdfOptionsSelect
                    name={'className'}
                    width={calculatorWidth(rightContainer.width) + 80}
                    mode={'single'}
                    key={index + 'udf-config'}
                    allowClear
                    showSearch
                    placeholder={l('pages.datastudio.label.udf.className')}
                    options={flinkUdfOptions}
                    onChange={(value: string) => handleClassChange(value, index)}
                  />
                  <ProForm.Item name={'name'}>
                    <Input
                      onChange={(e) => handleNameChange(e.target.value, index)}
                      placeholder={l('pages.datastudio.label.udf.name')}
                      style={{ width: calculatorWidth(rightContainer.width) - 90 }}
                    />
                  </ProForm.Item>
                </Space>
              </ProFormGroup>
            );
          }}
        </ProFormList>
      </ProForm>
    </div>
  );
};

export default connect(
  ({
    Studio,
    Alert,
    SysConfig
  }: {
    Studio: StateType;
    Alert: AlertStateType;
    SysConfig: SysConfigStateType;
  }) => ({
    sessionCluster: Studio.sessionCluster,
    clusterConfiguration: Studio.clusterConfiguration,
    rightContainer: Studio.rightContainer,
    tabs: Studio.tabs,
    env: Studio.env,
    group: Alert.group,
    flinkConfigOptions: Studio.flinkConfigOptions,
    flinkUdfOptions: Studio.flinkUdfOptions,
    taskOwnerLockingStrategy: SysConfig.taskOwnerLockingStrategy
  })
)(JobConfig);
