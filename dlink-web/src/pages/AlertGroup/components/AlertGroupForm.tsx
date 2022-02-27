import React, {useEffect, useState} from 'react';
import {Form, Button, Input, Modal, Select,Divider,Switch} from 'antd';
import {AlertGroupTableListItem} from "@/pages/AlertGroup/data";

export type AlertGroupFormProps = {
  onCancel: (flag?: boolean) => void;
  onSubmit: (values: Partial<AlertGroupTableListItem>) => void;
  modalVisible: boolean;
  values: Partial<AlertGroupTableListItem>;
};
const Option = Select.Option;

const formLayout = {
  labelCol: {span: 7},
  wrapperCol: {span: 13},
};

const AlertGroupForm: React.FC<AlertGroupFormProps> = (props) => {

  const [form] = Form.useForm();
  const [formVals, setFormVals] = useState<Partial<AlertGroupTableListItem>>({
    id: props.values.id,
    name: props.values.name,
    alertInstanceIds: props.values.alertInstanceIds,
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
          name="name"
          label="标识"
          rules={[{required: true, message: '请输入名称！'}]}>
          <Input placeholder="请输入唯一英文标识"/>
        </Form.Item>
        <Form.Item
          name="alertInstanceIds"
          label="报警实例"
          help="请选择报警组实例"
        >
          <Input placeholder="请选择报警实例"/>
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
      title={formVals.id?"维护报警组":"创建报警组"}
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

export default AlertGroupForm;
