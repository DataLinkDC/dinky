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
import {l} from "@/utils/intl";
import {DIALECT} from "@/components/Studio/conf";

const {Option} = Select;

const UDFTemplate: React.FC<{}> = () => {

  const [open, setOpen] = useState(false);

  const initData: UDFTemplateItem = {
    id: null,
    name: "",
    codeType: "Java",
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
      title={(tModel.id ? l('pages.sys.udf.template.modify') : l('pages.sys.udf.template.create'))}
      width={720}
      onClose={onClose}
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
              label={l('pages.sys.udf.template.name')}
              rules={[{required: true, message: l('pages.sys.udf.template.namePlaceholder')}]}
            >
              <Input placeholder={l('pages.sys.udf.template.namePlaceholder')}/>
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={16}>
          <Col span={12}>
            <Form.Item
              name="codeType"
              label={l('pages.sys.udf.template.codeType')}
              rules={[{required: true, message: l('pages.sys.udf.template.codeTypePlaceholder')}]}
            >
              <Select placeholder={l('pages.sys.udf.template.codeTypePlaceholder')}>
                <Option value={DIALECT.JAVA}>{DIALECT.JAVA}</Option>
                <Option value={DIALECT.SCALA}>{DIALECT.SCALA}</Option>
                <Option value={DIALECT.PYTHON}>{DIALECT.PYTHON}</Option>
              </Select>
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item
              name="functionType"
              label={l('pages.sys.udf.template.functionType')}
              rules={[{required: true, message: l('pages.sys.udf.template.functionTypePlaceholder')}]}
            >
              <Select placeholder={l('pages.sys.udf.template.functionTypePlaceholder')}>
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
              label={l('pages.sys.udf.template.templateCode')}
              rules={[
                {
                  required: true,
                  message: l('pages.sys.udf.template.templateCodePlaceholder'),
                },
              ]}
            >
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
      title: l('pages.sys.udf.template.delete'),
      content: l('pages.sys.udf.template.deleteConfirm'),
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
      title: l('pages.sys.udf.template.name'),
      sorter: true,
      dataIndex: 'name',
    },
    {
      title: l('pages.sys.udf.template.codeType'),
      sorter: true,
      dataIndex: 'codeType',
      filters: [
        {
          text: DIALECT.JAVA,
          value: DIALECT.JAVA,
        }, {
          text: DIALECT.SCALA,
          value: DIALECT.SCALA,
        }, {
          text: DIALECT.PYTHON,
          value: DIALECT.PYTHON,
        },
      ],
      valueEnum: {
        'Java': {text: DIALECT.JAVA},
        'Scala': {text: DIALECT.SCALA},
        'Python': {text: DIALECT.PYTHON},
      },
      onFilter: true
    }, {
      title: l('pages.sys.udf.template.functionType'),
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
      render: (text, record) => (
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
        request={() => getTemplate()}
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
