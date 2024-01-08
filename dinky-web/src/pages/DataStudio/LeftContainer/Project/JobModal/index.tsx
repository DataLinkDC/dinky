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
import { JOB_TYPE } from '@/pages/DataStudio/LeftContainer/Project/constants';
import { isFlinkJob, isUDF } from '@/pages/DataStudio/LeftContainer/Project/function';
import TemplateSelect from '@/pages/DataStudio/LeftContainer/Project/JobModal/components/TemplateSelect';
import { queryDataByParams } from '@/services/BusinessCrud';
import { RUN_MODE } from '@/services/constants';
import { API_CONSTANTS } from '@/services/endpoints';
import { Catalogue } from '@/types/Studio/data';
import { l } from '@/utils/intl';
import {
  ModalForm,
  ProFormGroup,
  ProFormSelect,
  ProFormText,
  ProFormTextArea
} from '@ant-design/pro-components';
import { ProFormDependency } from '@ant-design/pro-form';
import { ProFormCascader } from '@ant-design/pro-form/lib';
import { Form } from 'antd';
import { DefaultOptionType } from 'antd/es/select';
import React, { useEffect } from 'react';

type JobModalProps = {
  onCancel: () => void;
  onSubmit: (values: Catalogue) => void;
  modalVisible: boolean;
  title: React.ReactNode;
  values: Partial<Catalogue>;
};
const JobModal: React.FC<JobModalProps> = (props) => {
  const { onCancel, onSubmit, modalVisible, title, values } = props;
  const [jobType, setJobType] = React.useState<string>(values.type || 'FlinkSql');
  const [udfTemplate, setUdfTemplate] = React.useState<DefaultOptionType[]>([]);
  const [sqlTemplate, setSqlTemplate] = React.useState<string>('');
  const [form] = Form.useForm<Catalogue>();

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
    const newValues = { ...values, configJson: values.task?.configJson };
    if (modalVisible) form.resetFields();
    form.setFieldsValue(newValues);
  }, [open, values, form]);

  const queryUdfTemplate = () => {
    queryDataByParams<DefaultOptionType[]>(API_CONSTANTS.UDF_TEMPLATE_TREE).then((res) => {
      const newRes: DefaultOptionType[] = [];
      res?.forEach((item: any) => {
        if (item.value === jobType) {
          item.children.forEach((item: any) => {
            newRes.push(item);
          });
        }
      });
      setUdfTemplate(newRes);
    });
  };

  useEffect(() => {
    if (isUDF(jobType)) {
      queryUdfTemplate();
    }
  }, [jobType, form]);

  /**
   * handle cancel
   */
  const handleCancel = () => {
    formContext.resetForm();
    setJobType('');
    onCancel();
  };

  /**
   * form values change
   * @param changedValues
   */
  const onValuesChange = (changedValues: any) => {
    if (changedValues.type) {
      setJobType(changedValues.type);
      form.resetFields(['configJson']); // 如果是UDF，重置configJson, 否则 模版id 会有渲染问题
    }
  };

  /**
   * submit form
   */
  const submitForm = async (formData: Catalogue) => {
    await form.validateFields();
    if (isUDF(formData.type ?? '') && formData.configJson) {
      const { selectKeys } = formData.configJson.udfConfig;
      formData.configJson.udfConfig.templateId = selectKeys[selectKeys.length - 1];
    }
    // if this type is flink job, init task value and submit
    if (isFlinkJob(formData.type ?? '')) {
      const initTaskValue = {
        savePointStrategy: 0, // 0 is disabled
        parallelism: 1, // default parallelism
        envId: -1, // -1 is disabled
        step: 1, // default step is develop
        alertGroupId: -1, // -1 is disabled
        type: RUN_MODE.LOCAL, // default run mode is local
        dialect: formData.type,
        statement: sqlTemplate
      };
      onSubmit({ ...values, ...formData, task: initTaskValue } as Catalogue);
    } else {
      onSubmit({ ...values, ...formData, task: { statement: sqlTemplate } } as Catalogue);
    }
  };

  /**
   * validate name field value, contains '_'
   * because k8s job name not contains '_'
   * @param rule
   * @param value
   */
  const validateName = async (rule: any, value: string) => {
    if (/_/g.test(value)) {
      return Promise.reject(l('catalog.name.validate.error'));
    } else if (!value) {
      return Promise.reject(l('catalog.name.placeholder'));
    } else {
      return Promise.resolve();
    }
  };

  const renderForm = () => {
    return (
      <>
        <ProFormGroup>
          <ProFormText
            name='name'
            label={l('catalog.name')}
            tooltip={l('catalog.name.tip')}
            placeholder={l('catalog.name.placeholder')}
            validateTrigger={['onBlur', 'onChange', 'onSubmit']}
            rules={[{ required: true, validator: validateName }]}
            width={'xl'}
          />
          <ProFormSelect
            name={'type'}
            label={l('catalog.type')}
            tooltip={l('catalog.type.tip')}
            options={JOB_TYPE}
            initialValue={JOB_TYPE[0]['options'][0]['value']}
            disabled={!!values.id}
            placeholder={l('catalog.type.placeholder')}
            rules={[{ required: true, message: l('catalog.type.placeholder') }]}
            allowClear={false}
            width={'lg'}
          />
        </ProFormGroup>
        {isUDF(jobType) && (
          <ProFormGroup>
            <ProFormText
              name={['configJson', 'udfConfig', 'className']}
              label={l('catalog.udf.className')}
              placeholder={l('catalog.udf.className.placeholder')}
              rules={[
                {
                  required: true,
                  message: l('catalog.udf.className.placeholder')
                }
              ]}
              width={'md'}
            />
            <ProFormCascader
              name={['configJson', 'udfConfig', 'selectKeys']}
              label={l('catalog.udf.templateId')}
              shouldUpdate={(prevValues, curValues) => prevValues.type !== curValues.type}
              placeholder={l('catalog.udf.templateId.placeholder')}
              fieldProps={{
                changeOnSelect: true,
                options: udfTemplate
              }}
              rules={[
                {
                  required: true,
                  message: l('catalog.udf.templateId.placeholder')
                }
              ]}
              width={'sm'}
            />
          </ProFormGroup>
        )}
        <ProFormTextArea
          name='note'
          label={l('catalog.note')}
          placeholder={l('catalog.note.placeholder')}
        />

        {/*不支持UDF模板*/}
        {!isUDF(jobType) && (
          <ProFormDependency name={['type']}>
            {({ type }) => <TemplateSelect type={type} onChange={(v) => setSqlTemplate(v)} />}
          </ProFormDependency>
        )}
      </>
    );
  };

  return (
    <ModalForm<Catalogue>
      title={title}
      form={form}
      width={'60%'}
      initialValues={{ ...values }}
      open={modalVisible}
      autoFocusFirstInput
      onValuesChange={onValuesChange}
      modalProps={{
        destroyOnClose: true,
        maskClosable: false,
        okButtonProps: {
          htmlType: 'submit',
          autoFocus: true
        },
        onCancel: handleCancel
      }}
      onFinish={async (values) => submitForm(values)}
    >
      {renderForm()}
    </ModalForm>
  );
};

export default JobModal;
