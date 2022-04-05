import React, {useState} from 'react';
import {Button, Divider, Form, Input, Modal, Radio, Switch} from 'antd';
import {AlertInstanceTableListItem} from "@/pages/AlertInstance/data";
import {buildJSONData, getJSONData} from "@/pages/AlertInstance/function";
import {ALERT_TYPE} from "@/pages/AlertInstance/conf";

export type AlertInstanceFormProps = {
  onCancel: (flag?: boolean) => void;
  onSubmit: (values: Partial<AlertInstanceTableListItem>) => void;
  onTest: (values: Partial<AlertInstanceTableListItem>) => void;
  modalVisible: boolean;
  values: Partial<AlertInstanceTableListItem>;
};

const formLayout = {
  labelCol: {span: 7},
  wrapperCol: {span: 13},
};

const DingTalkForm: React.FC<AlertInstanceFormProps> = (props) => {

  const [form] = Form.useForm();
  const [formVals, setFormVals] = useState<Partial<AlertInstanceTableListItem>>({
    id: props.values?.id,
    name: props.values?.name,
    type: ALERT_TYPE.DINGTALK,
    params: props.values?.params,
    enabled: props.values?.enabled,
  });

  const {
    onSubmit: handleSubmit,
    onCancel: handleModalVisible,
    onTest: handleTest,
    modalVisible,
  } = props;

  const onValuesChange = (change: any,all: any)=>{
    setFormVals({...formVals,...change});
  };

  const sendTestForm = async () => {
    const fieldsValue = await form.validateFields();
    setFormVals(buildJSONData(formVals,fieldsValue));
    handleTest({...formVals, ...fieldsValue});
  };

  const submitForm = async () => {
    const fieldsValue = await form.validateFields();
    setFormVals(buildJSONData(formVals,fieldsValue));
    handleSubmit(buildJSONData(formVals,fieldsValue));
  };

  const renderContent = (vals) => {
    return (
      <>
        <Divider>钉钉配置</Divider>
        <Form.Item
          name="name"
          label="名称"
          rules={[{required: true, message: '请输入名称！'}]}
        >
          <Input placeholder="请输入名称"/>
        </Form.Item>
        <Form.Item
          name="webhook"
          label="地址"
          rules={[{required: true, message: '请输入WebHook！'}]}
        >
          <Input placeholder="请输入WebHook"/>
        </Form.Item>
        <Form.Item
          name="keyword"
          label="关键字"
        >
          <Input placeholder="请输入keyword"/>
        </Form.Item>
        <Form.Item
          name="secret"
          label="密令"
        >
          <Input placeholder="请输入secret"/>
        </Form.Item>
        <Form.Item
          name="isEnableProxy"
          label="开启代理">
          <Switch checkedChildren="是" unCheckedChildren="否"
                  defaultChecked={vals.isEnableProxy}/>
        </Form.Item>
        {vals.isEnableProxy?<>
          <Form.Item
            name="proxy"
            label="代理"
          >
            <Input placeholder="请输入proxy"/>
          </Form.Item>
          <Form.Item
            name="port"
            label="端口号"
          >
            <Input placeholder="请输入port"/>
          </Form.Item>
          <Form.Item
            name="user"
            label="用户"
          >
            <Input placeholder="请输入user"/>
          </Form.Item>
          <Form.Item
            name="password"
            label="密码"
          >
            <Input.Password placeholder="请输入password"/>
          </Form.Item></>:undefined
        }
        <Form.Item
          name="isAtAll"
          label="@所有人">
          <Switch checkedChildren="启用" unCheckedChildren="禁用"
                  defaultChecked={vals.isAtAll}/>
        </Form.Item>
        <Form.Item
          name="enabled"
          label="是否启用">
          <Switch checkedChildren="启用" unCheckedChildren="禁用"
                  defaultChecked={vals.enabled}/>
        </Form.Item>
        <Form.Item
          name="msgtype"
          label="展示方式"
          rules={[{required: true, message: '请选择展示方式！'}]}
        >
          <Radio.Group >
            <Radio value='markdown'>MarkDown</Radio>
            <Radio value='text'>文本</Radio>
          </Radio.Group>
        </Form.Item>
      </>
    );
  };

  const renderFooter = () => {
    return (
      <>
        <Button onClick={() => handleModalVisible(false)}>取消</Button>
        <Button  type="primary" danger htmlType="button" onClick={sendTestForm}>测试</Button>
        <Button type="primary" onClick={() => submitForm()}>
          完成
        </Button>
      </>
    );
  };


  return (
    <Modal
      width={1200}
      bodyStyle={{padding: '32px 40px 48px'}}
      destroyOnClose
      title={formVals.id?"维护报警实例配置":"创建报警实例配置"}
      visible={modalVisible}
      footer={renderFooter()}
      onCancel={() => handleModalVisible()}
    >
      <Form
        {...formLayout}
        form={form}
        initialValues={getJSONData(formVals)}
        onValuesChange={onValuesChange}
      >
        {renderContent(getJSONData(formVals))}
      </Form>
    </Modal>
  );
};

export default DingTalkForm;
