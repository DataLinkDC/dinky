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
  ModalForm,
  ProForm,
  ProFormGroup, ProFormList, ProFormRadio,
  ProFormSelect, ProFormSwitch,
  ProFormText, ProFormTextArea,
} from '@ant-design/pro-components';
import {Button, Divider, Form, message, Space, Typography} from 'antd';
import {PlusOutlined} from "@ant-design/icons";
import {getData} from "@/services/api";
import {API_CONSTANTS} from "@/services/endpoints";
import {Task} from "@/types/Studio/data";
const { Text } = Typography;

const RuleEditForm = (props: any) => {

  const [form] = Form.useForm<{ name: string; company: string }>();

  const getOnlineTask = async () => {
    const tasks:Task[] =  (await getData(API_CONSTANTS.GET_ONLINE_TASK)).datas;
    const value:{label:string,value:number}[] = tasks.map(task=>({ label: task.name, value: task.id }))
    value.splice(0,0,{label: "全部Flink任务", value: -1 })
    return value;
  }


  return (
    <ModalForm
      layout={"horizontal"}
      form={form}
      trigger={
        <Button type="link">
          <PlusOutlined />
          配置
        </Button>
      }
      submitTimeout={2000}
      onFinish={async (values) => {
        message.success('提交成功');
        // 不返回不会关闭弹框
        console.log(values)
        return true;
      }}
    >
      <ProFormText
        rules={[{required: true,},]}
        name="name"
        width="md"
        label="策略名称"
        placeholder="请输入策略名称"
      />

      <ProFormSelect
        label={"告警模板"}
        width="md"
        name="templateId"
        request={async (params) =>getOnlineTask()}
        placeholder="告警模板"
        rules={[{ required: true, message: '请选择告警模板' }]}
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
            { label: '所有Flink任务', value: 'all' },
            { label: '指定Flink实例', value: 'flink' },
          ]}
          rules={[{ required: true, message: '请选择告警对象' }]}
        />

        <ProFormSelect
          label={"选择实例"}
          width="md"
          name="ruleTagetId"
          dependencies={['ruleType']}
          request={async (params) =>getOnlineTask()}
          placeholder="选择指定对象"
          rules={[{ required: true, message: '请选择指定对象' }]}
        />

      </ProForm.Group>

      <Divider orientation={"left"}>触发配置</Divider>

      <ProFormRadio.Group
        name="triggerConditions"
        label="触发条件"
        options={[
          {label: '任意规则', value: 'any'},
          {label: '所有规则', value: 'all'},
          {label: '自定义规则', value: 'custom',disabled:true},
        ]}
      />

      <Text>触发规则:</Text>

      <ProFormList
        name={"rule"}
        copyIconProps={false}
        creatorButtonProps={{
          style: { width: '100%' },
          creatorButtonText: "添加规则"
        }}
      >
        <ProFormGroup>
          <Space key={'config'} align='baseline'>
            <Text>当</Text>

            <ProFormSelect
              name='ruleKey'
              width={"sm"}
              mode={'single'}
              options={[
                {label: '集群状态', value: 'clusterStatus',},
                {label: '任务状态', value: 'taskStatus',},
                {label: '运行时间', value: 'taskDuration',},
                {label: 'Checkpont状态', value: 'CheckpontStatus',},
                {label: 'Checkpont时间', value: 'CheckpontDuration',},
              ]}
            />
            <ProFormSelect
              name='ruleOperator'
              // width={calculatorWidth(rightContainer.width) + 30}
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
              width={"sm"}
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

      <ProFormSwitch name="enabled" label="启用" />




    </ModalForm>
  );
};

export default RuleEditForm;
