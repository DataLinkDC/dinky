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
import DataSourceProForm from '@/pages/RegCenter/DataSource/components/DataSourceModal/DataSourceProForm';
import { DataSources } from '@/types/RegCenter/data';
import { l } from '@/utils/intl';
import { ModalForm } from '@ant-design/pro-components';
import { Button, Form } from 'antd';
import React, { useEffect, useState } from 'react';

type DataSourceModalProps = {
  visible: boolean;
  onCancel: () => void;
  onSubmit: (values: Partial<DataSources.DataSource>) => void;
  onTest: (values: Partial<DataSources.DataSource>) => void;
  values: Partial<DataSources.DataSource>;
};

const DataSourceModal: React.FC<DataSourceModalProps> = (props) => {
  const { visible, values, onCancel, onSubmit, onTest } = props;
  const [submitting, setSubmitting] = React.useState<boolean>(false);
  const [flinkConfigValue, setFlinkConfigValue] = React.useState<string>(values.flinkConfig || '');
  const [flinkTemplateValue, setFlinkTemplateValue] = React.useState<string>(
    values.flinkTemplate || ''
  );
  const [dbType, setDbType] = useState<string>(values.type ?? 'MySQL');
  const [excludeFormItem, setExcludeFormItem] = useState<boolean>(false);

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
    if (!visible) {
      form.resetFields();
    } else {
      form.setFieldsValue(values);
    }
  }, [visible, values]);

  /**
   * handle cancel
   */
  const handleCancel = () => {
    onCancel();
    setSubmitting(false);
    formContext.resetForm();
  };

  /**
   * handle flink config value change
   * @param value
   */
  const handleFlinkConfigValueChange = (value: string) => {
    setFlinkConfigValue(value);
    form.setFieldsValue({ flinkConfig: value });
  };

  /**
   * handle flink template value change
   * @param value
   */
  const handleFlinkTemplateValueChange = (value: string) => {
    setFlinkTemplateValue(value);
    form.setFieldsValue({ flinkTemplate: value });
  };

  /**
   * test connect
   */
  const handleTestConnect = async () => {
    const fieldsValue = await form.validateFields();
    onTest({ ...values, ...fieldsValue });
  };

  /**
   * submit form
   */
  const submitForm = async () => {
    const fieldsValue = await form.validateFields();
    setSubmitting(true);
    onSubmit({ ...values, ...fieldsValue });
    handleCancel();
  };

  /**
   * render footer
   */
  const renderFooter = () => {
    return [
      <Button key={'cancel'} onClick={() => handleCancel()}>
        {l('button.cancel')}
      </Button>,
      <Button key={'test'} loading={submitting} type='primary' onClick={handleTestConnect}>
        {l('button.test')}
      </Button>,
      <Button
        key={'finish'}
        loading={submitting}
        type='primary'
        htmlType={'submit'}
        autoFocus
        onClick={() => submitForm()}
      >
        {l('button.finish')}
      </Button>
    ];
  };

  const handleTypeChange = (value: any) => {
    if (value.type) setDbType(value.type);
    if (value.type === 'Hive' || value.type === 'Presto') {
      setExcludeFormItem(true);
    } else {
      setExcludeFormItem(false);
    }
  };

  /**
   * render
   */
  return (
    <>
      <ModalForm<DataSources.DataSource>
        width={'50%'}
        open={visible}
        modalProps={{ onCancel: handleCancel }}
        title={values.id ? l('rc.ds.modify') : l('rc.ds.create')}
        form={form}
        onValuesChange={handleTypeChange}
        submitter={{ render: () => [...renderFooter()] }}
        initialValues={{
          ...values,
          flinkConfig: flinkConfigValue,
          flinkTemplate: flinkTemplateValue
        }}
      >
        <DataSourceProForm
          values={values}
          excludeFormItem={excludeFormItem}
          dbType={dbType}
          form={form}
          flinkConfigChange={handleFlinkConfigValueChange}
          flinkTemplateChange={handleFlinkTemplateValueChange}
        />
      </ModalForm>
    </>
  );
};
export default DataSourceModal;
