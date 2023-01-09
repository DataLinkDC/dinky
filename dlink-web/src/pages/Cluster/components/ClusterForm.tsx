import React, {useEffect, useState} from 'react';
import {Form, Button, Input, Modal, Select, Switch} from 'antd';

import {ClusterTableListItem} from "@/pages/Cluster/data";
import {RUN_MODE} from "@/components/Studio/conf";

export type ClusterFormProps = {
  onCancel: (flag?: boolean) => void;
  onSubmit: (values: Partial<ClusterTableListItem>) => void;
  modalVisible: boolean;
  values: Partial<ClusterTableListItem>;
};
const Option = Select.Option;

const formLayout = {
  labelCol: {span: 7},
  wrapperCol: {span: 13},
};

const ClusterForm: React.FC<ClusterFormProps> = (props) => {

  const [form] = Form.useForm();
  const [formVals, setFormVals] = useState<Partial<ClusterTableListItem>>({
    id: props.values.id,
    name: props.values.name,
    alias: props.values.alias,
    type: props.values.type,
    hosts: props.values.hosts,
    note: props.values.note,
    enabled: props.values.enabled,
  });

  const {
    onSubmit: handleSubmit,
    onCancel: handleModalVisible,
    modalVisible,
  } = props;

  const submitForm = async () => {
    const fieldsValue = await form.validateFields();
    fieldsValue.id = formVals.id;
    setFormVals(fieldsValue);
    handleSubmit(fieldsValue);
  };

  const renderContent = (formValsPara: Partial<ClusterTableListItem>) => {
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
          <Input placeholder="请输入名称"/>
        </Form.Item>
        <Form.Item
          name="type"
          label="类型"
        >
          <Select defaultValue={RUN_MODE.YARN_SESSION} allowClear>
            <Option value={RUN_MODE.STANDALONE}>Standalone</Option>
            <Option value={RUN_MODE.YARN_SESSION}>Yarn Session</Option>
            <Option value={RUN_MODE.YARN_PER_JOB}>Yarn Per-Job</Option>
            <Option value={RUN_MODE.YARN_APPLICATION}>Yarn Application</Option>
            <Option value={RUN_MODE.KUBERNETES_SESSION}>Kubernetes Session</Option>
            <Option value={RUN_MODE.KUBERNETES_APPLICATION}>Kubernetes Application</Option>
          </Select>
        </Form.Item>
        <Form.Item
          name="hosts"
          label="JobManager HA 地址"
          validateTrigger={['onChange']}
          rules={[
            {
              required: true,
              validator(_, hostsValue) {
                let hostArray = [];
                if (hostsValue.trim().length === 0) {
                  return Promise.reject(new Error('请输入 JobManager HA 地址!'));
                } else {
                  hostArray = hostsValue.split(',')
                  for (let i = 0; i < hostArray.length; i++) {
                    if (hostArray[i].includes('/')) {
                      return Promise.reject(new Error('不符合规则! 不能包含/'));
                    }
                    if (parseInt(hostArray[i].split(':')[1]) >= 65535) {
                      return Promise.reject(new Error('不符合规则! 端口号区间[0-65535]'));
                    }
                  }
                  return Promise.resolve();
                }
              },
            },
          ]}
        >
          <Input.TextArea
            placeholder="添加 Flink 集群的 JobManager 的 RestApi 地址。当 HA 模式时，地址间用英文逗号分隔，例如：192.168.123.101:8081,192.168.123.102:8081,192.168.123.103:8081"
            allowClear
            autoSize={{minRows: 3, maxRows: 10}}/>
        </Form.Item>
        <Form.Item
          name="note"
          label="注释"
        >
          <Input.TextArea placeholder="请输入文本注释" allowClear
                          autoSize={{minRows: 3, maxRows: 10}}/>
        </Form.Item>
        <Form.Item
          name="enabled"
          label="是否启用">
          <Switch checkedChildren="启用" unCheckedChildren="禁用"
                  defaultChecked={formValsPara.enabled}/>
        </Form.Item>
      </>
    );
  };

  const renderFooter = () => {
    return (
      <>
        <Button onClick={() => handleModalVisible(false)}>取消</Button>
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
      title={formVals.id ? "修改集群" : "创建集群"}
      visible={modalVisible}
      footer={renderFooter()}
      onCancel={() => handleModalVisible()}
    >
      <Form
        {...formLayout}
        form={form}
        initialValues={formVals}
      >
        {renderContent(formVals)}
      </Form>
    </Modal>
  );
};

export default ClusterForm;
