import React, {useEffect, useState} from 'react';
import {Form, Button, Input, Space, Select} from 'antd';

import Switch from "antd/es/switch";
import TextArea from "antd/es/input/TextArea";
import {DataBaseItem} from "@/pages/DataBase/data";


export type MysqlFormProps = {
  onCancel: (flag?: boolean, formVals?: Partial<DataBaseItem>) => void;
  onSubmit: (values: Partial<DataBaseItem>) => void;
  modalVisible: boolean;
  values: Partial<DataBaseItem>;
};
const Option = Select.Option;

const formLayout = {
  labelCol: {span: 7},
  wrapperCol: {span: 13},
};

const MysqlForm: React.FC<MysqlFormProps> = (props) => {
  const [formVals, setFormVals] = useState<Partial<DataBaseItem>>({
    id: props.values.id,
    name: props.values.name,
    alias: props.values.alias,
    type: "MySql",
    groupName: props.values.groupName,
    url: props.values.url,
    username: props.values.username,
    password: props.values.password,
    dbVersion: props.values.dbVersion,
    note: props.values.note,
    enabled: props.values.enabled,
  });

  const [form] = Form.useForm();
  const {
    onSubmit: handleUpdate,
    onCancel: handleModalVisible,
    modalVisible,
    values,
  } = props;

  const submitForm = async () => {
    const fieldsValue = await form.validateFields();
    setFormVals({...formVals, ...fieldsValue});
    handleUpdate({...formVals, ...fieldsValue});
  };

  const onReset = () => {
    form.resetFields();
  };

  const renderContent = (formVals) => {
    return (
      <>
        <Form.Item
          name="name"
          label="名称"
          rules={[{required: true, message: '请输入名称！'}]}>
          <Input placeholder="请输入唯一英文标识"/>
        </Form.Item>
        <Form.Item
          name="alias"
          label="别名"
        >
          <Input placeholder="请输入别名"/>
        </Form.Item>
        <Form.Item
          name="groupName"
          label="分组类型"
        >
          <Select >
            <Option value="来源">来源</Option>
            <Option value="数仓">数仓</Option>
            <Option value="应用">应用</Option>
            <Option value="备份">备份</Option>
            <Option value="其他">其他</Option>
          </Select>
        </Form.Item>
        <Form.Item
          name="url"
          label="url"
        >
          <TextArea placeholder="jdbc:mysql://{host}:{port}/{database}" allowClear
                    autoSize={{minRows: 3, maxRows: 10}}/>
        </Form.Item>
        <Form.Item
          name="username"
          label="用户名"
        >
          <Input/>
        </Form.Item>
        <Form.Item
          name="password"
          label="密码"
        >
          <Input.Password/>
        </Form.Item>
        <Form.Item
          name="note"
          label="注释"
        >
          <Input placeholder="请输入"/>
        </Form.Item>
        <Form.Item
          name="enabled"
          label="是否启用"
          >
          <Switch checkedChildren="启用" unCheckedChildren="禁用"
                  defaultChecked={formVals.enabled}/>
        </Form.Item>
      </>
    );
  };

  return (
    <>{
        modalVisible && (
          <>
            <Form
              {...formLayout}
              form={form}
              initialValues={{
                id: formVals.id,
                name: formVals.name,
                alias: formVals.alias,
                type: formVals.type,
                groupName: formVals.groupName,
                url: formVals.url,
                username: formVals.username,
                password: formVals.password,
                note: formVals.note,
                enabled: formVals.enabled,
              }}
            >
              {renderContent(formVals)}
              <Form.Item wrapperCol={{offset: 8, span: 16}}>
                <Space>
                  <Button htmlType="button" onClick={()=>{
                    handleModalVisible(false)
                  }}>
                    返回
                  </Button>
                  <Button htmlType="button" onClick={onReset}>
                    重置
                  </Button>
                  <Button htmlType="button">
                    测试
                  </Button>
                  <Button type="primary" htmlType="button" onClick={() => submitForm()}>
                    保存
                  </Button>
                </Space>
              </Form.Item>
            </Form>
          </>
        )
      }</>
  );
};

export default MysqlForm;
