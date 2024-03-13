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

import { Authorized } from '@/hooks/useAccess';
import {
  RuleType,
  TriggerType
} from '@/pages/SettingCenter/AlertRule/AlertRuleList/RuleEditForm/constants';
import {
  AlertRulesOption,
  buildValueItem,
  getOperatorOptions
} from '@/pages/SettingCenter/AlertRule/AlertRuleList/RuleEditForm/function';
import { getData } from '@/services/api';
import { SWITCH_OPTIONS } from '@/services/constants';
import { API_CONSTANTS } from '@/services/endpoints';
import { Alert } from '@/types/RegCenter/data';
import { AlertRule } from '@/types/SettingCenter/data';
import { l } from '@/utils/intl';
import {
  DrawerForm,
  ProCard,
  ProFormGroup,
  ProFormItem,
  ProFormList,
  ProFormRadio,
  ProFormSelect,
  ProFormSwitch,
  ProFormText,
  ProFormTextArea
} from '@ant-design/pro-components';
import { ProFormDependency } from '@ant-design/pro-form';
import { Button, Divider, Form, Space, Typography } from 'antd';

const { Link } = Typography;

type AlertRuleFormProps = {
  onCancel: (flag?: boolean) => void;
  onSubmit: (values: AlertRule) => void;
  modalVisible: boolean;
  values: Partial<AlertRule>;
};

const RuleEditForm = (props: AlertRuleFormProps) => {
  const { onSubmit: handleSubmit, onCancel: handleModalVisible, modalVisible, values } = props;

  // if is system rule disable edit
  const isSystem = values.ruleType == RuleType.SYSTEM;

  const [form] = Form.useForm<AlertRule>();

  const getAlertTemplate = async () => {
    const template: Alert.AlertTemplate[] = (await getData(API_CONSTANTS.ALERT_TEMPLATE)).data;
    return template.map((t) => ({ label: t.name, value: t.id }));
  };

  const submit = async () => {
    const fieldsValue = await form.validateFields();
    return handleSubmit({ ...fieldsValue, rule: JSON.stringify(fieldsValue.rule) });
  };

  const renderTemplateDropDown = (item: any) => {
    return (
      <>
        {item}
        <Authorized key='create' path='/registration/alert/template/add'>
          <>
            <Divider style={{ margin: '8px 0' }} />
            <Link href={'#/registration/alert/template'}>+ {l('rc.alert.template.new')}</Link>
          </>
        </Authorized>
      </>
    );
  };

  const renderFooter = () => {
    return [
      <Button key={'RuleCancel'} onClick={() => handleModalVisible(false)}>
        {l('button.cancel')}
      </Button>,
      <Button
        key={'RuleFinish'}
        type='primary'
        htmlType={'submit'}
        autoFocus
        onClick={() => submit()}
      >
        {l('button.finish')}
      </Button>
    ];
  };

  return (
    <DrawerForm
      layout={'vertical'}
      form={form}
      open={modalVisible}
      submitter={{ render: () => [...renderFooter()] }}
      drawerProps={{
        onClose: () => handleModalVisible(false),
        destroyOnClose: true
      }}
      initialValues={values}
    >
      <ProFormGroup>
        <ProFormText name='id' hidden={true} />
        <ProFormText name='ruleType' hidden={true} />
        <ProFormText
          disabled={isSystem}
          rules={[{ required: true }]}
          name='name'
          width='md'
          label={l('sys.alert.rule.name')}
          placeholder={l('sys.alert.rule.name')}
        />

        <ProFormSelect
          label={l('sys.alert.rule.template')}
          width='md'
          name='templateId'
          request={async () => getAlertTemplate()}
          placeholder={l('sys.alert.rule.template')}
          rules={[{ required: true, message: l('sys.alert.rule.template') }]}
          fieldProps={{ dropdownRender: (item) => renderTemplateDropDown(item) }}
        />
      </ProFormGroup>

      <ProFormGroup>
        <ProFormTextArea width='md' name='description' label={l('global.table.note')} />
        <ProFormSwitch name='enabled' {...SWITCH_OPTIONS()} label={l('global.table.isEnable')} />
      </ProFormGroup>

      <Divider orientation={'left'}>{l('sys.alert.rule.trigger')}</Divider>

      <ProFormRadio.Group
        disabled={isSystem}
        name='triggerConditions'
        label={l('sys.alert.rule.triggerConditions')}
        rules={[{ required: true }]}
        options={TriggerType}
      />

      <ProFormList
        name='rule'
        label={l('sys.alert.rule.triggerRule')}
        creatorButtonProps={
          isSystem
            ? false
            : {
                creatorButtonText: l('sys.alert.rule.addRule')
              }
        }
        copyIconProps={false}
        min={1}
        itemRender={({ listDom, action }, { index }) => (
          <ProCard
            bordered
            style={{ marginBlockEnd: 8 }}
            title={`${l('sys.alert.rule.rule')}${index + 1}`}
            extra={action}
            bodyStyle={{ paddingBlockEnd: 0 }}
          >
            {listDom}
          </ProCard>
        )}
      >
        <ProFormGroup>
          <Space key={'config'} align='baseline'>
            <ProFormItem>IF:</ProFormItem>
            <ProFormSelect
              disabled={isSystem}
              name='ruleKey'
              width={'sm'}
              mode={'single'}
              options={AlertRulesOption()}
            />
            <ProFormDependency name={['ruleKey']}>
              {(ruleKey) => (
                <ProFormSelect
                  disabled={isSystem}
                  name='ruleOperator'
                  mode={'single'}
                  width={'sm'}
                  options={getOperatorOptions(ruleKey.ruleKey)}
                />
              )}
            </ProFormDependency>
            <ProFormDependency name={['ruleKey']}>
              {(ruleKey) => buildValueItem(ruleKey.ruleKey, isSystem)}
            </ProFormDependency>
          </Space>
        </ProFormGroup>
      </ProFormList>
    </DrawerForm>
  );
};

export default RuleEditForm;
