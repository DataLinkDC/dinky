import React from "react";
import {ModalForm, ProFormText} from "@ant-design/pro-components";
import {l} from "@/utils/intl";
import {Form, Tag} from "antd";
import {LoginOutlined} from "@ant-design/icons";
import {handleOption} from "@/services/BusinessCrud";
import {API_CONSTANTS} from "@/services/constants";

export const TestLogin = () => {

  const [form] = Form.useForm();

  const testLogin = async (value: any) => {
    await handleOption(API_CONSTANTS.LDAP_TEST_LOGIN, l("sys.ldap.settings.testLogin"), value);
  }

  return <>
    <ModalForm
      width={400}
      onFinish={testLogin}
      form={form}
      modalProps={{onCancel:()=>form.resetFields()}}
      trigger={<Tag icon={<LoginOutlined/>} color="#108ee9">{l("sys.ldap.settings.testLogin")}</Tag>}
    >
      <ProFormText name="username" label={l("login.username.placeholder")}/>
      <ProFormText name="password" label={l("login.password.placeholder")}/>
    </ModalForm>
  </>;
};
