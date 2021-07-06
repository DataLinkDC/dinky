import React, {useEffect, useState} from 'react';
import {Form, Button, Input, Modal} from 'antd';

import type {SessionItem} from '../data.d';

export type UpdateFormProps = {
  onCancel: (flag?: boolean, formVals?: Partial<SessionItem>) => void;
  onSubmit: (values: Partial<SessionItem>) => void;
  updateModalVisible: boolean;
  isCreate: boolean;
  values: Partial<SessionItem>;
};

const FormItem = Form.Item;

const formLayout = {
  labelCol: {span: 7},
  wrapperCol: {span: 13},
};

const SessionForm: React.FC<UpdateFormProps> = (props) => {
  const [formVals, setFormVals] = useState<Partial<SessionItem>>({
    session: props.values.session,
    type: props.values.sessionConfig?.type,
    useRemote: props.values.sessionConfig?.useRemote,
    address: props.values.sessionConfig?.address,
    createUser: props.values.createUser,
    createTime: props.values.createTime,
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
        <FormItem
          name="session"
          label="名称"
          rules={[{required: true, message: '请输入唯一名称！'}]}>
          <Input placeholder="请输入"/>
        </FormItem>
        <FormItem
          name="alias"
          label="别名"
          rules={[{required: true, message: '请输入别名！'}]}>
          <Input placeholder="请输入"/>
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
          parentId: formVals.parentId,
        }}
      >
        {renderContent()}
      </Form>
    </Modal>
  );
};

export default SessionForm;
