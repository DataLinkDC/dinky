import React, {useEffect, useState} from 'react';
import {Form, Button, Input, Modal} from 'antd';

import type {TableListItem} from '../data.d';
import Switch from "antd/es/switch";

export type UpdateFormProps = {
  onCancel: (flag?: boolean, formVals?: Partial<TableListItem>) => void;
  onSubmit: (values: Partial<TableListItem>) => void;
  updateModalVisible: boolean;
  values: Partial<TableListItem>;
};
const FormItem = Form.Item;

const formLayout = {
  labelCol: {span: 7},
  wrapperCol: {span: 13},
};

const UpdateForm: React.FC<UpdateFormProps> = (props) => {
  const [formVals, setFormVals] = useState<Partial<TableListItem>>({
    id: props.values.id,
    name: props.values.name,
    alias: props.values.alias,
    type: props.values.type,
    checkPoint: props.values.checkPoint,
    savePointPath: props.values.savePointPath,
    parallelism: props.values.parallelism,
    fragemnt: props.values.fragment,
    clusterId: props.values.clusterId,
    note: props.values.note,
    enabled: props.values.enabled,
  });

  const [form] = Form.useForm();

  const {
    onSubmit: handleUpdate,
    onCancel: handleUpdateModalVisible,
    updateModalVisible,
    values,
  } = props;

  const submitForm = async () => {
    const fieldsValue = await form.validateFields();
    setFormVals({...formVals, ...fieldsValue});
    handleUpdate({...formVals, ...fieldsValue});
  };

  const renderContent = (formVals) => {
    return (
      <>
        <FormItem
          name="name"
          label="名称"
          rules={[{required: true, message: '请输入名称！'}]}>
          <Input placeholder="请输入"/>
        </FormItem>
        <FormItem
          name="alias"
          label="别名"
        >
          <Input placeholder="请输入"/>
        </FormItem>
        <FormItem
          name="type"
          label="类型"
        >
          <Input placeholder="请输入"/>
        </FormItem>
        <FormItem
          name="note"
          label="注释"
        >
          <Input placeholder="请输入"/>
        </FormItem>
        <FormItem
          name="checkPoint"
          label="CheckPoint"
        >
          <Input placeholder="请输入"/>
        </FormItem>
        <FormItem
          name="savePointPath"
          label="SavePointPath"
        >
          <Input placeholder="请输入"/>
        </FormItem>
        <FormItem
          name="parallelism"
          label="Parallelism"
        >
          <Input placeholder="请输入"/>
        </FormItem>
        <FormItem
          name="fragemnt"
          label="Fragment"
        >
          <Input placeholder="请输入"/>
        </FormItem>
        <FormItem
          name="note"
          label="注释"
        >
          <Input placeholder="请输入"/>
        </FormItem>
        <FormItem
          name="enabled"
          label="是否启用"
          rules={[{required: true, message: '请输入是否启用！'}]}>
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
      bodyStyle={{padding: '32px 40px 48px'}}
      destroyOnClose
      title="编辑任务"
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
          type: formVals.type,
          note: formVals.note,
          checkPoint: formVals.checkPoint,
          savePointPath: formVals.savePointPath,
          parallelism: formVals.parallelism,
          fragemnt: formVals.fragemnt,
          clusterId: formVals.clusterId,
          enabled: formVals.enabled,
        }}
      >
        {renderContent(formVals)}
      </Form>
    </Modal>
  );
};

export default UpdateForm;
