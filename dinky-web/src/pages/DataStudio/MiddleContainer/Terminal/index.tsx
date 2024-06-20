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
import {
  getTermConfig,
  setTermConfig,
  TermProps
} from '@/pages/DataStudio/MiddleContainer/Terminal/TerminalConfig';
import TerminalContent from '@/pages/DataStudio/MiddleContainer/Terminal/TerminalContent';

const TerminalTab = () => {
  // const [form] = Form.useForm();

  const EMBBED_FILTER = [
    ClusterType.STANDALONE,
    ClusterType.YARN_SESSION,
    ClusterType.KUBERNETES_SESSION
  ];
  const GATEWAY_FILTER = [ClusterType.SQL_GATEWAY];

  const [disableUrlEditable, setUrlEditable] = useState(true);
  const [opentTerm, setOpentTerm] = useState(false);
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
    setOpentTerm(true);
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
      {opentTerm ? (
        <TerminalContent {...connectCfg} />
      ) : (
        <div style={{ padding: '40px' }}>
          <Form
            {...formItemLayout}
            onFinish={onFinish}
            initialValues={connectCfg}
            style={{ maxWidth: 800 }}
            requiredMark={'optional'}
          >
            <Form.Item label='连接方式' name='mode' required>
              <Radio.Group onChange={(e) => setCurrentMode(e.target.value)}>
                <Radio.Button value='MODE_EMBEDDED'>Embedded</Radio.Button>
                <Radio.Button value='MODE_GATEWAY'>SQL Gateway</Radio.Button>
              </Radio.Group>
            </Form.Item>

            <Form.Item label='dinky后端' required>
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
                    一般情况下无需改动，如果您有自定义nginx配置，请修改此地址
                  </Typography.Text>
                </Col>
                <Col span={1}>
                  <EditTwoTone onClick={() => setUrlEditable(!disableUrlEditable)} />
                </Col>
              </Row>
            </Form.Item>

            <Form.Item label='Flink 集群' required>
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
                {' '}
                需要提前在注册中心添加对应集群,仅展示手动注册集群
              </Typography.Text>
            </Form.Item>

            <Form.Item name='sessionId' label='sessionId'>
              <Input />
            </Form.Item>

            <Form.Item name='initSql' label='ENV环境'>
              <Select placeholder='前置SQL' options={envData.data} allowClear></Select>
            </Form.Item>

            <Form.Item
              rules={[{ required: true, message: 'Font Size is required!' }]}
              name='fontSize'
              label='字体大小'
            >
              <InputNumber min={1} max={100} />
            </Form.Item>

            <Form.Item required label='回退字符转换'>
              <Form.Item name='backspaceAsCtrlH' noStyle required>
                <Switch />
              </Form.Item>
              <Typography.Text type='secondary' italic style={{ fontSize: 'small' }}>
                {' '}
                如果回退删除显示异常，修改此选项
              </Typography.Text>
            </Form.Item>

            <Form.Item wrapperCol={{ offset: 6, span: 16 }}>
              <Button type='primary' htmlType='submit'>
                连接
              </Button>
            </Form.Item>
          </Form>
        </div>
      )}
    </>
  );
};

export default TerminalTab;
