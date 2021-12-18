import React, {useEffect, useState} from 'react';
import {Form, Button, Input, Modal,Select} from 'antd';

import type {TaskTableListItem} from '../data.d';
import {DIALECT} from "@/components/Studio/conf";

const {Option} = Select;

export type UpdateFormProps = {
  onCancel: (flag?: boolean, formVals?: Partial<TaskTableListItem>) => void;
  onSubmit: (values: Partial<TaskTableListItem>) => void;
  updateModalVisible: boolean;
  isCreate: boolean;
  values: Partial<TaskTableListItem>;
};

const formLayout = {
  labelCol: {span: 7},
  wrapperCol: {span: 13},
};

const SimpleTaskForm: React.FC<UpdateFormProps> = (props) => {
  const [formVals, setFormVals] = useState<Partial<TaskTableListItem>>({
    id: props.values.id,
    name: props.values.name,
    alias: props.values.alias,
    parentId: props.values.parentId,
  });

  const [form] = Form.useForm();

  const {
    onSubmit: handleUpdate,
    onCancel: handleUpdateModalVisible,
    updateModalVisible,
    values,
    isCreate,
  } = props;

  const submitForm = async () => {
    const fieldsValue = await form.validateFields();
    setFormVals({...formVals, ...fieldsValue});
    handleUpdate({...formVals, ...fieldsValue});
  };

  const renderContent = () => {
    return (
      <>
        {isCreate?(<Form.Item
          label="作业类型" name="dialect"
          tooltip='指定作业类型，默认为 FlinkSql'
        >
          <Select defaultValue={DIALECT.FLINKSQL} value={DIALECT.FLINKSQL}>
            <Option value={DIALECT.FLINKSQL}>FlinkSql</Option>
            <Option value={DIALECT.SQL}>Sql</Option>
            <Option value={DIALECT.JAVA}>Java</Option>
          </Select>
        </Form.Item>):undefined}
        <Form.Item
          name="name"
          label="名称"
          rules={[{required: true, message: '请输入唯一名称！'}]}>
          <Input placeholder="请输入"/>
        </Form.Item>
        <Form.Item
          name="alias"
          label="别名"
          rules={[{required: true, message: '请输入别名！'}]}>
          <Input placeholder="请输入"/>
        </Form.Item>
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
      bodyStyle={{padding: '32px 40px 48px'}}
      destroyOnClose
      title={isCreate ? '创建新作业' : ('重命名作业-' + formVals.name)}
      visible={updateModalVisible}
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
          dialect: formVals.dialect,
          parentId: formVals.parentId,
        }}
      >
        {renderContent()}
      </Form>
    </Modal>
  );
};

export default SimpleTaskForm;
