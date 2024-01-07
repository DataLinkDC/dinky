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
  getCurrentData,
  getCurrentTab,
  isDataStudioTabsItemType
} from '@/pages/DataStudio/function';
import { SessionType, StateType, STUDIO_MODEL, STUDIO_MODEL_ASYNC } from '@/pages/DataStudio/model';
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
import { AlertStateType, ALERT_MODEL_ASYNC } from '@/pages/RegCenter/Alert/AlertInstance/model';
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
import { Badge, Space, Typography } from 'antd';
import { useForm } from 'antd/es/form/Form';
import { debounce } from 'lodash';
import { useEffect, useState } from 'react';
import { connect } from 'umi';

const { Text } = Typography;

const JobConfig = (props: any) => {
  const {
    sessionCluster,
    clusterConfiguration,
    dispatch,
    tabs: { panes, activeKey },
    env,
    group,
    rightContainer,
    flinkConfigOptions
  } = props;

  const current = getCurrentData(panes, activeKey);

  const currentSession: SessionType = {
    connectors: [],
    sessionConfig: {
      clusterId: current?.clusterId,
      clusterName: current?.clusterName
    }
  };
  const [form] = useForm();

  const [selectRunMode, setSelectRunMode] = useState<string>(current?.type ?? RUN_MODE.LOCAL);

  useEffect(() => {
    dispatch({
      type: STUDIO_MODEL_ASYNC.queryFlinkConfigOptions
    });
    dispatch({
      type: ALERT_MODEL_ASYNC.queryAlertGroup
    });

    form.setFieldsValue({ ...current, type: current?.type ?? RUN_MODE.LOCAL });
  }, [current]);

  const onValuesChange = (change: { [key in string]: any }, all: any) => {
    const pane = getCurrentTab(panes, activeKey);
    if (!isDataStudioTabsItemType(pane)) {
      return;
    }

    Object.keys(change).forEach((key) => {
      if (key === 'configJson') {
        if (!pane.params.taskData.configJson) {
          pane.params.taskData.configJson = {};
        }

        Object.keys(change[key]).forEach((k) => {
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

  const onChangeClusterSession = () => {
    //todo 这里需要验证
    // showTables(currentSession.session, dispatch);
  };

  const statusElement = currentSession.sessionConfig?.clusterId ? (
    <Space>
      <Badge status='success' />
      <Text type='success'>{currentSession.sessionConfig.clusterName}</Text>
    </Space>
  ) : (
    <Space>
      <Badge status='error' />
      <Text type='danger'>{l('pages.devops.jobinfo.localenv')}</Text>
    </Space>
  );

  const execMode = currentSession.session ? (
    statusElement
  ) : (
    <ProFormSelect
      style={{ width: '100%' }}
      placeholder={l('pages.datastudio.label.jobConfig.clusterConfig.tip1', '', {
        type: current?.type
      })}
      label={l('pages.datastudio.label.jobConfig.cluster')}
      tooltip={l('pages.datastudio.label.jobConfig.clusterConfig.tip1', '', {
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
      fieldProps={{
        onChange: onChangeClusterSession
      }}
    />
  );

  return (
    <div style={{ maxHeight: rightContainer.height }}>
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
        style={{ paddingInline: '15px', overflow: 'scroll' }}
        form={form}
        submitter={false}
        layout='vertical'
        onValuesChange={debounce(onValuesChange, 500)}
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

        {isCanRenderClusterInstance(selectRunMode) && <>{execMode}</>}

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

        {current?.dialect?.toLowerCase() === DIALECT.FLINK_SQL && (
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
          {/*{current?.dialect?.toLowerCase() === DIALECT.FLINK_SQL && (
            <ProFormSwitch
              label={l('pages.datastudio.label.jobConfig.insert')}
              name='statementSet'
              valuePropName='checked'
              tooltip={{
                title: l('pages.datastudio.label.jobConfig.insert.tip'),
                icon: <InfoCircleOutlined />
              }}
              {...SWITCH_OPTIONS()}
            />
          )}*/}

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
                width={calculatorWidth(rightContainer.width) + 100}
                mode={'single'}
                allowClear
                showSearch
                placeholder={l('pages.datastudio.label.jobConfig.addConfig.params')}
                options={flinkConfigOptions}
              />
              <ProFormText
                name={'value'}
                width={calculatorWidth(rightContainer.width) - 100}
                placeholder={l('pages.datastudio.label.jobConfig.addConfig.value')}
              />
            </Space>
          </ProFormGroup>
        </ProFormList>
      </ProForm>
    </div>
  );
};

export default connect(({ Studio, Alert }: { Studio: StateType; Alert: AlertStateType }) => ({
  sessionCluster: Studio.sessionCluster,
  clusterConfiguration: Studio.clusterConfiguration,
  rightContainer: Studio.rightContainer,
  tabs: Studio.tabs,
  env: Studio.env,
  group: Alert.group,
  flinkConfigOptions: Studio.flinkConfigOptions
}))(JobConfig);
