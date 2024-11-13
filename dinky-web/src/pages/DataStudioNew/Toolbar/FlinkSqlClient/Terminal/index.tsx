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

import { API_CONSTANTS } from '@/services/endpoints';
import React, { useState } from 'react';
import {
  Button,
  Col,
  Divider,
  Form,
  Input,
  InputNumber,
  Radio,
  Row,
  Select,
  Space,
  Switch,
  Tag,
  Typography
} from 'antd';
import { EditTwoTone } from '@ant-design/icons';
import useHookRequest from '@/hooks/useHookRequest';
import { getData } from '@/services/api';
import { Cluster } from '@/types/RegCenter/data';
import { CLUSTER_TYPE_OPTIONS, ClusterType } from '@/pages/RegCenter/Cluster/constants';
import { l } from '@/utils/intl';
import TerminalContent from '@/pages/DataStudioNew/Toolbar/FlinkSqlClient/Terminal/TerminalContent';
import {
  getTermConfig,
  setTermConfig,
  TermProps
} from '@/pages/DataStudioNew/Toolbar/FlinkSqlClient/Terminal/TerminalConfig';

const TerminalTab = () => {
  // const [form] = Form.useForm();

  const EMBBED_FILTER = [
    ClusterType.STANDALONE,
    ClusterType.YARN_SESSION,
    ClusterType.KUBERNETES_SESSION
  ];
  const GATEWAY_FILTER = [ClusterType.SQL_GATEWAY];

  const [disableUrlEditable, setUrlEditable] = useState(true);
  const [openTerm, setOpenTerm] = useState(false);
  const [connectCfg, setConnectCfg] = useState(getTermConfig());

  const [currentMode, setCurrentMode] = useState(connectCfg.mode);

  const dealClusterData = (data: Cluster.Instance[]) => {
    const renderClusterItem = (item: Cluster.Instance) => {
      return (
        <Space split={<Divider type='vertical' />}>
          <Tag color='cyan'>
            {CLUSTER_TYPE_OPTIONS().find((record) => item.type === record.value)?.label}
          </Tag>
          <Typography.Text>{item.name}</Typography.Text>
          <Typography.Text type={'secondary'} style={{ fontSize: 'small' }}>
            {item.alias}
          </Typography.Text>
          <Typography.Link>{item.hosts}</Typography.Link>
        </Space>
      );
    };

    return data
      .filter((item: Cluster.Instance) => {
        if (currentMode === 'MODE_EMBEDDED') {
          return EMBBED_FILTER.includes(item.type as ClusterType);
        } else {
          return GATEWAY_FILTER.includes(item.type as ClusterType);
        }
      })
      .map((item: Cluster.Instance) => {
        return {
          label: renderClusterItem(item),
          value: item.hosts
        };
      });
  };

  const clusterData = useHookRequest<any, any>(getData, {
    refreshDeps: [currentMode],
    defaultParams: [API_CONSTANTS.CLUSTER_INSTANCE_LIST, { isAutoCreate: false }],
    onSuccess: (data: Cluster.Instance[]) => dealClusterData(data)
  });

  const envData = useHookRequest<any, any>(getData, {
    defaultParams: [API_CONSTANTS.LIST_FLINK_SQL_ENV],
    onSuccess: (data: any[]) =>
      data.map((item: any) => ({ label: item.name, value: item.statement }))
  });

  const onFinish = (values: TermProps) => {
    setTermConfig(values);
    setConnectCfg(values);
    setOpenTerm(true);
  };

  const formItemLayout = {
    labelCol: {
      xs: { span: 24 },
      sm: { span: 6 }
    },
    wrapperCol: {
      xs: { span: 24 },
      sm: { span: 14 }
    }
  };

  return (
    <>
      {openTerm ? (
        <TerminalContent {...connectCfg} />
      ) : (
        <div style={{ padding: '10px', height: '100%' }}>
          <Form
            {...formItemLayout}
            onFinish={onFinish}
            initialValues={connectCfg}
            requiredMark={'optional'}
          >
            <Form.Item label={l('datastudio.middle.terminal.mode')} name='mode' required>
              <Radio.Group onChange={(e) => setCurrentMode(e.target.value)}>
                <Radio.Button value='MODE_EMBEDDED'>Embedded</Radio.Button>
                <Radio.Button value='MODE_GATEWAY' disabled>
                  SQL Gateway
                </Radio.Button>
              </Radio.Group>
            </Form.Item>

            <Form.Item label={l('datastudio.middle.terminal.websocket')} required>
              <Row gutter={24}>
                <Col span={21}>
                  <Form.Item
                    name='wsUrl'
                    noStyle
                    rules={[{ required: true, message: 'WS Addr is required!' }]}
                  >
                    <Input disabled={disableUrlEditable} />
                  </Form.Item>
                  <Typography.Text type='secondary' italic style={{ fontSize: 'small' }}>
                    {l('datastudio.middle.terminal.websocket.tip')}
                  </Typography.Text>
                </Col>
                <Col span={1}>
                  <EditTwoTone onClick={() => setUrlEditable(!disableUrlEditable)} />
                </Col>
              </Row>
            </Form.Item>

            <Form.Item label={l('datastudio.middle.terminal.cluster')} required>
              <Form.Item
                name='connectAddress'
                noStyle
                rules={[{ required: true, message: 'Cluster Addr is required!' }]}
              >
                <Select
                  placeholder=''
                  // onChange={onGenderChange}
                  allowClear
                  options={clusterData.data}
                ></Select>
              </Form.Item>
              <Typography.Text type='secondary' italic style={{ fontSize: 'small' }}>
                {l('datastudio.middle.terminal.cluster.tip')}
              </Typography.Text>
            </Form.Item>

            <Form.Item name='sessionId' label='sessionId'>
              <Input />
            </Form.Item>

            <Form.Item name='initSql' label='Init Sql'>
              <Select placeholder='Init SQL' options={envData.data} allowClear></Select>
            </Form.Item>

            <Form.Item
              rules={[{ required: true, message: 'Font Size is required!' }]}
              name='fontSize'
              label={l('datastudio.middle.terminal.fontSize')}
            >
              <InputNumber min={1} max={100} />
            </Form.Item>

            <Form.Item required label={l('datastudio.middle.terminal.backspaceAsCtrlH')}>
              <Form.Item name='backspaceAsCtrlH' noStyle required>
                <Switch />
              </Form.Item>
              <Typography.Text type='secondary' italic style={{ fontSize: 'small' }}>
                {l('datastudio.middle.terminal.backspaceAsCtrlH.tip')}
              </Typography.Text>
            </Form.Item>

            <Form.Item wrapperCol={{ offset: 6, span: 16 }}>
              <Button type='primary' htmlType='submit'>
                {l('datastudio.middle.terminal.connect')}
              </Button>
            </Form.Item>
          </Form>
        </div>
      )}
    </>
  );
};

export default TerminalTab;
