import React, {useEffect, useState} from 'react';
import { Form, Button, Input, Modal,Select } from 'antd';

import Switch from "antd/es/switch";
import TextArea from "antd/es/input/TextArea";
import {DataBaseItem} from "@/pages/DataBase/data";

export type MysqlFormProps = {
  onCancel: (flag?: boolean, formVals?: Partial<DataBaseItem>) => void;
  onSubmit: (values: Partial<DataBaseItem>) => void;
  modalVisible: boolean;
  values: Partial<DataBaseItem>;
};
const FormItem = Form.Item;

const formLayout = {
  labelCol: { span: 7 },
  wrapperCol: { span: 13 },
};

const MysqlForm: React.FC<MysqlFormProps> = (props) => {
  const [formVals, setFormVals] = useState<Partial<DataBaseItem>>({
    id: props.values.id,
    name: props.values.name,
    alias: props.values.alias,
    type: props.values.type,
    note: props.values.note,
    enabled: props.values.enabled,
  });

  const [form] = Form.useForm();

  const {
    onSubmit: handleUpdate,
    onCancel: handleUpdateModalVisible,
    modalVisible,
    values,
  } = props;

  const submitForm = async () => {
    const fieldsValue = await form.validateFields();
    setFormVals({ ...formVals, ...fieldsValue });
    handleUpdate({ ...formVals, ...fieldsValue });
  };

  const renderContent = (formVals) => {
    return (
      <>
        <FormItem
          name="name"
          label="名称"
          rules={[{ required: true, message: '请输入名称！' }]}                     >
          <Input placeholder="请输入" />
        </FormItem>
        <FormItem
          name="alias"
          label="别名"
        >
          <Input placeholder="请输入" />
        </FormItem>
        <FormItem
          name="type"
          label="类型"
        >
          <Select defaultValue="Yarn" allowClear>
            <Option value="Standalone">Standalone</Option>
            <Option value="Yarn">Yarn</Option>
            <Option value="Others">Others</Option>
          </Select>
        </FormItem>
        <FormItem
          name="hosts"
          label="Hosts"
        >
          <TextArea placeholder="添加 Flink Hosts...例如：127.0.0.1:8081,127.0.0.1:8091" allowClear autoSize={{ minRows: 3, maxRows: 10 }}/>
        </FormItem>
        <FormItem
          name="note"
          label="注释"
        >
          <Input placeholder="请输入" />
        </FormItem>
        <FormItem
          name="enabled"
          label="是否启用"
          rules={[{ required: true, message: '请输入是否启用！' }]}                     >
          <Switch checkedChildren="启用" unCheckedChildren="禁用"
                  defaultChecked={formVals.enabled}/>
        </FormItem>
      </>
    );
  };

  const renderFooter = () => {
    return (
      <>
        <Button onClick={() => handleUpdateModalVisible(false, values)}>取消</Button>
        <Button type="primary" onClick={() => submitForm()}>
          完成
        </Button>
      </>
    );
  };

  return (
    <Modal
      width={640}
      bodyStyle={{ padding: '32px 40px 48px' }}
      destroyOnClose
      title={formVals.id?'编辑':'创建 MySql 数据源'}
      visible={modalVisible}
      footer={renderFooter()}
      onCancel={() => handleUpdateModalVisible()}
    >
      <Form
        {...formLayout}
        form={form}
        initialValues={{
          id: formVals.id,
          name: formVals.name,
          alias: formVals.alias,
          type: formVals.type,
          note: formVals.note,
          enabled: formVals.enabled,
        }}
      >
        {renderContent(formVals)}
      </Form>
    </Modal>
  );
};

export default MysqlForm;
