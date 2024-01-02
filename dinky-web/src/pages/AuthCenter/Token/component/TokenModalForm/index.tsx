/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

import { FormContextValue } from '@/components/Context/FormContext';
import { mapDispatchToProps, TokenStateType } from '@/pages/AuthCenter/Token/component/model';
import TokenForm from '@/pages/AuthCenter/Token/component/TokenModalForm/TokenForm';
import { MODAL_FORM_STYLE } from '@/services/constants';
import { SysToken } from '@/types/AuthCenter/data.d';
import { formatDateToYYYYMMDDHHMMSS, parseDateStringToDate } from '@/utils/function';
import { l } from '@/utils/intl';
import { ModalForm } from '@ant-design/pro-components';
import { ProFormInstance } from '@ant-design/pro-form/lib';
import { connect } from '@umijs/max';
import { Button, Form } from 'antd';
import React, { useEffect, useRef } from 'react';

const DATE_FORMAT = 'YYYY-MM-DD HH:mm:ss';

type TokenModalFormProps = {
  visible: boolean;
  onCancel: () => void;
  onSubmit: (value: SysToken) => void;
  value: Partial<SysToken>;
  loading: boolean;
  buildToken: () => void;
  tokenValue: string;
};
const TokenModalForm: React.FC<TokenModalFormProps & connect> = (props) => {
  /**
   * init props
   */
  const {
    onSubmit: handleSubmit,
    onCancel: handleModalVisible,
    visible,
    value,
    loading,
    buildToken,
    tokenValue
  } = props;

  const [expireType, setExpireType] = React.useState<number>(value.expireType ?? 1);
  const [selectUserId, setSelectUserId] = React.useState<number>(value.userId ?? 0);
  const formRef = useRef<ProFormInstance>();

  /**
   * init form
   */
  const [form] = Form.useForm();
  /**
   * init form context
   */
  const formContext = React.useMemo<FormContextValue>(
    () => ({
      resetForm: () => form.resetFields() // 定义 resetForm 方法
    }),
    [form]
  );

  /**
   * when modalVisible or values changed, set form values
   */
  useEffect(() => {
    if (!visible) return;
    props.queryUsers();
    selectUserId && props.queryRoles(selectUserId);
    selectUserId && props.queryTenants(selectUserId);
    form.setFieldsValue({
      ...value,
      expireTime:
        value.expireType === 2
          ? parseDateStringToDate(value.expireEndTime)
          : value.expireType === 3
          ? [
              parseDateStringToDate(value.expireStartTime),
              parseDateStringToDate(value.expireEndTime)
            ]
          : undefined
    });
  }, [visible, value, form, selectUserId]);

  /**
   * handle cancel
   */
  const handleCancel = () => {
    handleModalVisible();
    formContext.resetForm();
  };

  /**
   * submit form
   */
  const submitForm = async () => {
    const fieldsValue = await form.validateFields();
    let result = { ...value, ...fieldsValue };
    // 转化时间
    if (fieldsValue.expireType === 2) {
      // 只有一个时间 设置为结束时间
      result = {
        ...result,
        expireEndTime: formatDateToYYYYMMDDHHMMSS(fieldsValue.expireTime)
      };
    } else if (fieldsValue.expireType === 3) {
      // 两个时间都有 设置开始时间和结束时间
      result = {
        ...result,
        expireStartTime: formatDateToYYYYMMDDHHMMSS(fieldsValue.expireTime[0]),
        expireEndTime: formatDateToYYYYMMDDHHMMSS(fieldsValue.expireTime[1])
      };
    }

    await handleSubmit({ ...result });
    await handleCancel();
  };

  const renderFooter = () => {
    return [
      <Button key={'cancel'} onClick={() => handleCancel()}>
        {l('button.cancel')}
      </Button>,
      <Button
        key={'finish'}
        loading={loading}
        type='primary'
        htmlType={'submit'}
        autoFocus
        onClick={() => submitForm()}
      >
        {l('button.finish')}
      </Button>
    ];
  };

  const handleValuesChange = (changedValues: any, allValues: any) => {
    if (allValues.expireType) setExpireType(allValues.expireType);
    if (allValues.userId) setSelectUserId(allValues.userId);
  };

  const handleBuildToken = () => {
    buildToken();
    form.setFieldValue('tokenValue', tokenValue);
  };

  return (
    <>
      <ModalForm<SysToken>
        {...MODAL_FORM_STYLE}
        width={'25%'}
        title={value.id ? l('token.update') : l('token.create')}
        open={visible}
        form={form}
        formRef={formRef}
        onValuesChange={handleValuesChange}
        submitter={{ render: () => [...renderFooter()] }}
        initialValues={{ ...value }}
        modalProps={{
          destroyOnClose: true,
          onCancel: () => handleCancel()
        }}
      >
        <TokenForm expireType={expireType} buildToken={handleBuildToken} />
      </ModalForm>
    </>
  );
};
export default connect(
  ({ Token }: { Token: TokenStateType }) => ({
    tokenValue: Token.token
  }),
  mapDispatchToProps
)(TokenModalForm);
