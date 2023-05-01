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

import React, {useState} from 'react';
import {Form, Modal} from 'antd';
import {l} from "@/utils/intl";
import {GlobalVar} from "@/types/RegCenter/data";
import {ProForm, ProFormItem, ProFormSwitch, ProFormText, ProFormTextArea} from "@ant-design/pro-components";
import {FORM_LAYOUT_PUBLIC, NORMAL_MODAL_OPTIONS} from "@/services/constants";
import CodeEdit from "@/components/CustomEditor/CodeEdit";

/**
 * params
 */
export type GlobalVarFormProps = {
  onCancel: (flag?: boolean) => void;
  onSubmit: (values: GlobalVar) => void;
  modalVisible: boolean;
  values: Partial<GlobalVar>;
};

/**
 * global variable code edit props
 */
const CodeEditProps ={
  height : "25vh",
  lineNumbers: "on",
}

const GlobalVarForm: React.FC<GlobalVarFormProps> = (props: any) => {

  const [globalVarValue, setGlobalVarValue] = useState<string>(props.values.fragmentValue || '');


  const [form] = Form.useForm();
  const [formVals, setFormVals] = useState<GlobalVar>({
    id: props.values.id,
    name: props.values.name,
    fragmentValue: props.values.fragmentValue,
    note: props.values.note,
    enabled: props.values.enabled,
    createTime: props.values.createTime,
    updateTime: props.values.updateTime,
  });

  const {
    onSubmit: handleSubmit,
    onCancel: handleModalVisible,
    modalVisible,
  } = props;


  const submitForm = async () => {
    const fieldsValue = await form.validateFields();
    fieldsValue.id = formVals.id;
    setFormVals({...formVals, ...fieldsValue});
    handleSubmit({...formVals, ...fieldsValue});
  };

  const handleGlobalVarChange =(value: string) => {
    setGlobalVarValue(value);
  }

  const GlobalVarRender = () => {
    return (
      <>
        <ProFormText
          name="name"
          label={l('rc.gv.name')}
          placeholder={l('rc.gv.namePlaceholder')}
          rules={[{required: true, message: l('rc.gv.namePlaceholder')}]}
        />

        <ProFormTextArea
          name="note"
          label={l('global.table.note')}
          placeholder={l('global.table.notePlaceholder')}
          allowClear
        />

        <ProFormSwitch
          name="enabled"
          label={l('global.table.isEnable')}
          checkedChildren={l('button.enable')} unCheckedChildren={l('button.disable')}
        />

        <ProFormItem
          name="fragmentValue"
          label={l('rc.gv.value')}
          rules={[{required: true, message: l('rc.gv.valuePlaceholder')}]}
        >
          <CodeEdit
            onChange={(value =>{handleGlobalVarChange(value)})}
            code={globalVarValue}
            language={"sql"}
            {...CodeEditProps}
          />
        </ProFormItem>
      </>
    );
  };


  return (
    <Modal
      {...NORMAL_MODAL_OPTIONS}
      title={formVals.id ? l('rc.gv.modify') : l('rc.gv.create')}
      open={modalVisible}
      onOk={() => submitForm()}
      onCancel={() => handleModalVisible()}
    >
      <ProForm
        {...FORM_LAYOUT_PUBLIC}
        layout={"horizontal"}
        submitter={false}
        form={form}
        initialValues={formVals}
      >
        {GlobalVarRender()}
      </ProForm>
    </Modal>
  );
};

export default GlobalVarForm;
