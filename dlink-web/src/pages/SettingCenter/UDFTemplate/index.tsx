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

import React, {useRef, useState} from "react";
import ProTable, {ActionType, ProColumns} from "@ant-design/pro-table";
import {PageContainer} from '@ant-design/pro-layout';
import {UDFTemplateItem} from "@/pages/SettingCenter/UDFTemplate/data";
import {addTemplate, deleteTemplate, getTemplate} from "@/pages/SettingCenter/UDFTemplate/service";
import {Button, Col, Drawer, Form, Input, Modal, Row, Select, Space} from "antd";
import {DeleteOutlined, FormOutlined, PlusOutlined} from "@ant-design/icons";
import 'antd/dist/antd.css';
import './index.css';
import CodeEdit from "@/components/Common/CodeEdit";
import {useIntl} from "umi";

const {Option} = Select;

const UDFTemplate: React.FC<{}> = (props) => {

  const [open, setOpen] = useState(false);

  const intl = useIntl();
  const l = (id: string, defaultMessage?: string, value?: {}) => intl.formatMessage({id, defaultMessage}, value);

  const initData: UDFTemplateItem = {
    id: null,
    name: "",
    codeType: "java",
    functionType: "UDF",
    templateCode: ""
  }

  const [tModel, setTModel] = useState<UDFTemplateItem>(initData);
  const actionRef = useRef<ActionType>();

  const addModel = () => {
    setTModel(initData);
  }

  const changeModel = (record: UDFTemplateItem) => {
    setTModel(record);
    setOpen(true);
  }

  const showDrawer = () => {
    addModel();
    setOpen(true);
  };

  const onClose = () => {
    actionRef.current?.reload();
    setOpen(false);
  };

  const Box = () => {
    const [form] = Form.useForm();
    const [code, setCode] = useState<string>(tModel.templateCode);

    const add = async () => {
      try {
        const values = await form.validateFields();
        values["id"] = tModel.id;
        values["templateCode"] = code;
        await addTemplate(values);
        onClose();
      } catch (errorInfo) {
        console.log('Failed:', errorInfo);
      }
    };

    return <Drawer
      visible={open}
      title={(tModel.id ? '修改' : '添加') + "UDF模板"}
      width={720}
      onClose={onClose}
      open={open}
      extra={
        <Space>
          <Button onClick={onClose}>{l('button.cancel')}</Button>
          <Button onClick={add} type="primary">
            {l('button.submit')}
          </Button>
        </Space>
      }
    >
      <Form layout="vertical" form={form} initialValues={tModel}>
        <Row gutter={16}>
          <Col span={12}>
            <Form.Item
              name="name"
              label="模板名"
              rules={[{required: true, message: '模板名称'}]}
            >
              <Input placeholder="请输入模板名"/>
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={16}>
          <Col span={12}>
            <Form.Item
              name="codeType"
              label="代码类型"
              rules={[{required: true, message: '请选择代码类型'}]}
            >
              <Select placeholder="请选择代码类型">
                <Option value="java">java</Option>
                <Option value="python">python</Option>
                <Option value="scala">scala</Option>
              </Select>
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item
              name="functionType"
              label="函数类型"
              rules={[{required: true, message: '请选择函数类型'}]}
            >
              <Select placeholder="Please choose the type">
                <Option value="UDF">UDF</Option>
                <Option value="UDAF">UDAF</Option>
                <Option value="UDTF">UDTF</Option>
              </Select>
            </Form.Item>
          </Col>
        </Row>
        <Row>
          <Col span={24}>
            <Form.Item
              name="templateCode"
              label="模板代码"
              rules={[
                {
                  required: true,
                  message: '请编辑模板代码',
                },
              ]}
            >
              {/*<Input.TextArea rows={20} placeholder="请编辑模板代码"/>*/}
              <CodeEdit code={code} language='java'
                        height='400px' onChange={async (val) => {
                // setTModel({...tModel,templateCode:val});
                setCode(val);
              }}/>
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Drawer>
  }

  const deleteUDFTemplate = (id: number) => {
    Modal.confirm({
      title: '删除集群',
      content: '确定删除该集群吗？',
      okText: l('button.confirm'),
      cancelText: l('button.cancel'),
      onOk: async () => {
        await deleteTemplate(id)
        actionRef.current?.reloadAndRest?.();
      }
    });
  }

  const columns: ProColumns<UDFTemplateItem>[] = [
    {
      title: '模板名',
      sorter: true,
      dataIndex: 'name',
    },
    {
      title: '代码类型',
      sorter: true,
      dataIndex: 'codeType',
      filters: [
        {
          text: 'java',
          value: 'java',
        }, {
          text: 'scala',
          value: 'scala',
        }, {
          text: 'python',
          value: 'python',
        },
      ],
      valueEnum: {
        'java': {text: 'java'},
        'scala': {text: 'scala'},
        'python': {text: 'python'},
      },
      onFilter: true
    }, {
      title: '函数类型',
      sorter: true,
      dataIndex: 'functionType',
      filters: [
        {
          text: 'UDF',
          value: 'UDF',
        }, {
          text: 'UDTF',
          value: 'UDTF',
        }, {
          text: 'UDAF',
          value: 'UDAF',
        },
      ],
      valueEnum: {
        'UDF': {text: 'UDF'},
        'UDTF': {text: 'UDTF'},
        'UDAF': {text: 'UDAF'},
      },
      onFilter: true
    }, {
      title: l('global.table.operate'),
      key: 'action',
      render: (text, record, _, action) => (
        <Space size="middle">
          <Button type="primary" icon={<FormOutlined/>} onClick={() => changeModel(record)}></Button>
          <Button type="primary" icon={<DeleteOutlined/>} onClick={() => {
            deleteUDFTemplate(record.id)
          }}></Button>
        </Space>
      ),
    }
  ];

  return (
    <PageContainer title={false}>
      {<Box/>}
      <ProTable
        request={(params, sorter, filter) => getTemplate()}
        columns={columns}
        search={false}
        toolBarRender={() => [
          <Button type="primary" onClick={showDrawer} icon={<PlusOutlined/>}>
            {l('button.create')}
          </Button>
        ]}
        actionRef={actionRef}
      />

    </PageContainer>
  );
};

export default UDFTemplate;
