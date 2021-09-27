import React, {useEffect, useState} from 'react';
import {Form, Button, Input, Modal, Select} from 'antd';

import Switch from "antd/es/switch";
import TextArea from "antd/es/input/TextArea";
import {ClusterTableListItem} from "@/pages/Cluster/data";

export type UpdateFormProps = {
  onCancel: (flag?: boolean, formVals?: Partial<ClusterTableListItem>) => void;
  onSubmit: (values: Partial<ClusterTableListItem>) => void;
  updateModalVisible: boolean;
  values: Partial<ClusterTableListItem>;
};
const FormItem = Form.Item;
const Option = Select.Option;

const formLayout = {
  labelCol: {span: 7},
  wrapperCol: {span: 13},
};

const UpdateForm: React.FC<UpdateFormProps> = (props) => {
  const [formVals, setFormVals] = useState<Partial<ClusterTableListItem>>({
    id: props.values.id,
    name: props.values.name,
    alias: props.values.alias,
    type: props.values.type,
    hosts: props.values.hosts,
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
          <Input placeholder="请输入 如果不填默认使用[名称]"/>
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
          label="JobManager HA 地址"
        >
          <TextArea
            placeholder="添加 Flink 集群的 JobManager 的 RestApi 地址。当 HA 模式时，地址间用英文逗号分隔，例如：192.168.123.101:8081,192.168.123.102:8081,192.168.123.103:8081"
            allowClear
            autoSize={{minRows: 3, maxRows: 10}}/>
        </FormItem>
        <FormItem
          name="note"
          label="注释"
        >
          <TextArea
            placeholder="请输入"
            allowClear
            autoSize={{minRows: 3, maxRows: 10}}/>
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
      title="编辑集群"
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
          hosts: formVals.hosts,
          note: formVals.note,
          enabled: formVals.enabled,
        }}
      >
        {renderContent(formVals)}
      </Form>
    </Modal>
  );
};

export default UpdateForm;
