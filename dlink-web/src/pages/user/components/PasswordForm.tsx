import React, {useState} from 'react';
import {Form, Button, Input, Modal} from 'antd';
import {PasswordItem} from "@/pages/user/data";

export type PasswordFormProps = {
  onCancel: (flag?: boolean) => void;
  onSubmit: (values: Partial<PasswordItem>) => void;
  modalVisible: boolean;
  values: Partial<PasswordItem>;
};

const formLayout = {
  labelCol: {span: 7},
  wrapperCol: {span: 13},
};

const PasswordForm: React.FC<PasswordFormProps> = (props) => {

  const [form] = Form.useForm();
  const [formVals, setFormVals] = useState<Partial<PasswordItem>>({
    username: props.values.username,
  });

  const {
    onSubmit: handleSubmit,
    onCancel: handleModalVisible,
    modalVisible,
  } = props;


  const submitForm = async () => {
    const fieldsValue = await form.validateFields();
    setFormVals({ ...formVals, ...fieldsValue });
    handleSubmit({ ...formVals, ...fieldsValue });
  };

  const renderContent = () => {
    return (
      <>
        <Form.Item
          name="password"
          label="旧密码"
          hasFeedback
          rules={[{required: true, message: '请输入旧密码！'}]}>
          <Input.Password  placeholder="请输入旧密码"/>
        </Form.Item>
        <Form.Item
          name="newPassword"
          label="新密码"
          hasFeedback
          rules={[{required: true, message: '请输入新密码！'}]}>
          <Input.Password  placeholder="请输入新密码"/>
        </Form.Item>
        <Form.Item
        name="newPasswordCheck"
        label="重复新密码"
        hasFeedback
        dependencies={['newPassword']}
        rules={[
          {
            required: true,
            message: '请重复输入一致的新密码',
          },
          ({ getFieldValue }) => ({
            validator(_, value) {
              if (!value || getFieldValue('newPassword') === value) {
                return Promise.resolve();
              }
              return Promise.reject(new Error('重复新密码不一致!'));
            },
          }),
        ]}>
        <Input.Password placeholder="请重复输入新密码"/>
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
      title="修改密码"
      visible={modalVisible}
      footer={renderFooter()}
      onCancel={() => handleModalVisible()}
    >
      <Form
        {...formLayout}
        form={form}
        initialValues={formVals}
      >
        {renderContent()}
      </Form>
    </Modal>
  );
};

export default PasswordForm;
