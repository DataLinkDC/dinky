/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

import { RuleType } from '@/pages/SettingCenter/AlertRule/constants';
import { getData } from '@/services/api';
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
import { Button, Divider, Form, Space } from 'antd';

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
    const template: Alert.AlertTemplate[] = (await getData(API_CONSTANTS.ALERT_TEMPLATE)).datas;
    return template.map((t) => ({ label: t.name, value: t.id }));
  };

  const submit = async () => {
    const fieldsValue = await form.validateFields();
    return handleSubmit({ ...fieldsValue, rule: JSON.stringify(fieldsValue.rule) });
  };

  const renderFooter = () => {
    return [
      <Button key={'RuleCancel'} onClick={() => handleModalVisible(false)}>
        {l('button.cancel')}
      </Button>,
      <Button key={'RuleFinish'} type='primary' onClick={() => submit()}>
        {l('button.finish')}
      </Button>
    ];
  };

  return (
    <DrawerForm
      layout={'horizontal'}
      form={form}
      open={modalVisible}
      submitter={{ render: () => [...renderFooter()] }}
      drawerProps={{
        onClose: () => handleModalVisible(false),
        destroyOnClose: true
      }}
      initialValues={values}
    >
      <ProFormText name='id' hidden={true} />
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
      />

      <ProFormTextArea width='md' name='description' label={l('global.table.note')} />

      <Divider orientation={'left'}>{l('sys.alert.rule.triger')}</Divider>

      <ProFormRadio.Group
        disabled={isSystem}
        name='triggerConditions'
        label={l('sys.alert.rule.trigerConditions')}
        options={[
          { label: l('sys.alert.rule.anyRule'), value: ' or ' },
          { label: l('sys.alert.rule.allRule'), value: ' and ' }
        ]}
      />

      <ProFormList
        name='rule'
        label={l('sys.alert.rule.trigerRule')}
        creatorButtonProps={{
          creatorButtonText: l('sys.alert.rule.addRule')
        }}
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
              options={[
                { label: l('sys.alert.rule.jobStatus'), value: 'jobInstance.status' },
                {
                  label: l('sys.alert.rule.checkpointTime'),
                  value: 'checkPoints.checkpointTime(#key,#checkPoints)'
                },
                {
                  label: l('sys.alert.rule.checkpointFailed'),
                  value: 'checkPoints.checkFailed(#key,#checkPoints)'
                },
                {
                  label: l('sys.alert.rule.jobException'),
                  value: 'exceptionRule.isException(#key,#exceptions)'
                }
              ]}
            />
            <ProFormSelect
              disabled={isSystem}
              name='ruleOperator'
              mode={'single'}
              options={[
                { label: '>', value: 'GT' },
                { label: '<', value: 'LT' },
                { label: '=', value: 'EQ' },
                { label: '>=', value: 'GE' },
                { label: '<=', value: 'LE' },
                { label: '!=', value: 'NE' }
              ]}
            />

            <ProFormText
              disabled={isSystem}
              name={'ruleValue'}
              placeholder={l('pages.datastudio.label.jobConfig.addConfig.value')}
            />
          </Space>
        </ProFormGroup>
      </ProFormList>

      <ProFormSwitch name='enabled' label={l('button.enable')} />
    </DrawerForm>
  );
};

export default RuleEditForm;
