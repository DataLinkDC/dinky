import React, {useEffect, useState} from 'react';
import {Form, Button, Input, Modal, Select,Divider,Switch} from 'antd';
import {JarTableListItem} from "@/pages/Jar/data";

export type JarFormProps = {
  onCancel: (flag?: boolean) => void;
  onSubmit: (values: Partial<JarTableListItem>) => void;
  modalVisible: boolean;
  values: Partial<JarTableListItem>;
};
const Option = Select.Option;

const formLayout = {
  labelCol: {span: 7},
  wrapperCol: {span: 13},
};

const JarForm: React.FC<JarFormProps> = (props) => {

  const [form] = Form.useForm();
  const [formVals, setFormVals] = useState<Partial<JarTableListItem>>({
    id: props.values.id,
    name: props.values.name,
    alias: props.values.alias,
    type: props.values.type?props.values.type:'UserApp',
    path: props.values.path,
    mainClass: props.values.mainClass,
    paras: props.values.paras,
    note: props.values.note,
    enabled: props.values.enabled?props.values.enabled:true,
  });

  const {
    onSubmit: handleSubmit,
    onCancel: handleModalVisible,
    modalVisible,
  } = props;

  const submitForm = async () => {
    const fieldsValue = await form.validateFields();
    setFormVals({...formVals, ...fieldsValue});
    handleSubmit({...formVals, ...fieldsValue});
  };

  const renderContent = (formVals) => {
    return (
      <>
        <Form.Item
          name="type"
          label="类型"
        >
          <Select defaultValue="UserApp" value="UserApp">
            <Option value="UserApp">User App</Option>
          </Select>
        </Form.Item>
        <Divider>Jar 配置</Divider>
        <Form.Item
          name="path"
          label="文件路径"
          help="指定 hdfs 上的文件路径"
          rules={[{required: true, message: '请输入文件路径！'}]}
        >
          <Input placeholder="hdfs:///flink/app/demo.jar"/>
        </Form.Item>
        <Form.Item
          name="mainClass"
          label="启动类"
          help="指定可执行 Jar 的启动类，（可选）"
        >
          <Input placeholder="com.dlink.app.MainApp"/>
        </Form.Item>
        <Form.Item
          name="paras"
          label="执行参数"
          help="指定可执行 Jar 的启动类入参，（可选）"
        >
          <Input placeholder="--id 1,2"/>
        </Form.Item>
        <Divider>基本配置</Divider>
        <Form.Item
          name="name"
          label="标识"
          rules={[{required: true, message: '请输入名称！'}]}>
          <Input placeholder="请输入唯一英文标识"/>
        </Form.Item>
        <Form.Item
          name="alias"
          label="名称"
        >
          <Input placeholder="请输入名称"/>
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
                  defaultChecked={formVals.enabled}/>
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
      width={1200}
      bodyStyle={{padding: '32px 40px 48px'}}
      destroyOnClose
      title={formVals.id?"维护Jar配置":"创建Jar配置"}
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

export default JarForm;
