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


import {l} from "@/utils/intl";
import {
  DrawerForm,
  ModalForm, ProCard,
  ProForm,
  ProFormGroup, ProFormItem, ProFormList, ProFormRadio,
  ProFormSelect, ProFormSwitch,
  ProFormText, ProFormTextArea,
} from '@ant-design/pro-components';
import {Button, Divider, Form, Space} from 'antd';
import {getData} from "@/services/api";
import {API_CONSTANTS} from "@/services/endpoints";
import {Task} from "@/types/Studio/data";
import {Alert} from "@/types/RegCenter/data";
import React, {useState} from "react";
import {AlertRule} from "@/types/SettingCenter/data";

type AlertRuleFormProps = {
  onCancel: (flag?: boolean) => void;
  onSubmit: (values: AlertRule) => void;
  modalVisible: boolean;
  values: Partial<AlertRule>;
};

const RuleEditForm = (props: AlertRuleFormProps) => {

  const {
    onSubmit: handleSubmit,
    onCancel: handleModalVisible,
    modalVisible,
    values
  } = props;


  const [isAllInstance, setIsAllInstance] = useState<boolean>(false);

  const [form] = Form.useForm<AlertRule>();
  const [formVals, setFormVals] = useState<Partial<AlertRule>>({ ...values });

  const getAlertTemplate = async () => {
    const template: Alert.AlertTemplate[] = (await getData(API_CONSTANTS.ALERT_TEMPLATE)).datas;
    return template.map(t => ({label: t.name, value: t.id}))
  }

  const getOnlineTask = async () => {
    const tasks: Task[] = (await getData(API_CONSTANTS.GET_ONLINE_TASK)).datas;
    return tasks.map(task => ({label: task.name, value: task.id}))
  }

  const submit = async () => {
    const fieldsValue = await form.validateFields();
    return handleSubmit({...fieldsValue,rule:JSON.stringify(fieldsValue.rule)});
  }

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
      layout={"horizontal"}
      form={form}
      open={modalVisible}
      submitter={{render: () => [...renderFooter()]}}
      initialValues={values}
    >
      <ProFormText name="id" hidden={true}/>
      <ProFormText
        rules={[{required: true}]}
        name="name"
        width="md"
        label="策略名称"
        placeholder="请输入策略名称"
      />

      <ProFormSelect
        label={"告警模板"}
        width="md"
        name="templateId"
        request={async () => getAlertTemplate()}
        placeholder="告警模板"
        rules={[{required: true, message: '请选择告警模板'}]}
      />

      <ProFormTextArea
        width="md"
        name="description"
        label="备注"
      />

      <Divider orientation={"left"}>策略配置</Divider>

      <ProForm.Group
      >
        <ProFormSelect
          name="ruleTargetType"
          width="sm"
          label="告警对象"
          options={[
            {label: '所有Flink任务', value: 'all'},
            {label: '指定Flink实例', value: 'flink'},
          ]}
          onChange={(v) => setIsAllInstance(v == "all")}
          rules={[{required: true, message: '请选择告警对象'}]}
        />

        <ProFormSelect
          label={"选择实例"}
          width="md"
          name="ruleTagetId"
          disabled={isAllInstance}
          dependencies={['ruleType']}
          request={async () => getOnlineTask()}
          placeholder="选择指定对象"
          rules={[{required: !isAllInstance, message: '请选择告警对象'}]}
        />

      </ProForm.Group>

      <Divider orientation={"left"}>触发配置</Divider>

      <ProFormRadio.Group
        name="triggerConditions"
        label="触发条件"
        options={[
          {label: '任意规则', value: 'any'},
          {label: '所有规则', value: 'all'},
          {label: '自定义规则', value: 'custom', disabled: true},
        ]}
      />

      <ProFormList
        name="rule"
        label="触发规则"
        creatorButtonProps={{
          creatorButtonText: '添加规则',
        }}
        copyIconProps={false}
        min={1}
        itemRender={({listDom, action}, {index}) => (
          <ProCard
            bordered
            style={{marginBlockEnd: 8}}
            title={`规则${index + 1}`}
            extra={action}
            bodyStyle={{paddingBlockEnd: 0}}
          >
            {listDom}
          </ProCard>
        )}
      >
        <ProFormGroup>
          <Space key={'config'} align='baseline'>
            <ProFormItem>IF:</ProFormItem>
            <ProFormSelect
              name='ruleKey'
              width={"sm"}
              mode={'single'}
              options={[
                {label: '作业状态', value: 'jobInstance.status',},
                {label: '集群状态', value: 'clusterStatus',},
                {label: '任务状态', value: 'taskStatus',},
                {label: '运行时间', value: 'taskDuration',},
                {label: 'Checkpont状态', value: 'CheckpontStatus',},
                {label: 'Checkpont时间', value: 'CheckpontDuration',},
              ]}
            />
            <ProFormSelect
              name='ruleOperator'
              mode={'single'}
              options={[
                {label: '>', value: 'GT',},
                {label: '<', value: 'LT',},
                {label: '=', value: 'EQ',},
                {label: '>=', value: 'GE',},
                {label: '<=', value: 'LE',},
                {label: '!=', value: 'NE',},
              ]}
            />

            <ProFormText
              name={'ruleValue'}
              placeholder={l('pages.datastudio.label.jobConfig.addConfig.value')}
            />

            <ProFormText
              name={'rulePriority'}
              placeholder={'权重'}
            />

          </Space>
        </ProFormGroup>
      </ProFormList>


      <ProFormSwitch name="enabled" label="启用"/>


    </DrawerForm>
  );
};

export default RuleEditForm;
